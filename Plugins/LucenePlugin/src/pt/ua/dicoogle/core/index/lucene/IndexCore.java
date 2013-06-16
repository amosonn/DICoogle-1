package pt.ua.dicoogle.core.index.lucene;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.index.CorruptIndexException;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.lucene.document.Field;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.ParseException;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.commons.codec.binary.Base64;
import org.apache.lucene.document.AbstractField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.Version;
import pt.ua.dicoogle.IndexPlugin.PluginConstants;
import pt.ua.dicoogle.sdk.Utils.Platform;
import pt.ua.dicoogle.sdk.Utils.SearchResult;
import pt.ua.dicoogle.sdk.Utils.TaskRequest;
import pt.ua.dicoogle.sdk.index.DicomByteArrField;
import pt.ua.dicoogle.sdk.index.DicomNumericField;
import pt.ua.dicoogle.sdk.index.DicomTextField;
import pt.ua.dicoogle.sdk.index.IDicomField;
import pt.ua.dicoogle.sdk.index.IDoc;
import pt.ua.dicoogle.sdk.index.LongField;
import pt.ua.dicoogle.sdk.observables.FileObservable;
import pt.ua.dicoogle.sdk.observables.ListObservable;
import pt.ua.dicoogle.sdk.observables.ListObservableSearch;


/**
 * Core Lucene related functions, this class serves as a facade of the index engine
 * This is the link between a higher layer and the index service.
 * @author Marco Pereira
 * @author by: Luís A. Bastião Silva <bastiao@ua.pt>
 * Modified by: Carlos Ferreira
 * Modified by: Pedro Bento
 */
public class IndexCore//implements IndexCoreAPI
{

    private Object monitorBlocks = new Object();
    
    private int counterNotifications = 0 ;
    private int counterBlocksSent = 0 ; 
            
            
    private boolean prune = false;
    
    private Directory index;
    private Analyzer analyzer;
    private Properties props;
    
    ListObservable<TaskRequest> tasks;
    long commits = 0;
    
    /**
     * The path to the peers index.+
     */
    private String basePath;
    /**
     * The file that defines the different handler types to be used with lucene
     */
    private String handler = "/pt/ua/dicoogle/core/index/lucene/LuceneSupport/handler.properties";
    private static IndexCore instance = null;
    public static final int BLOCKS_SEARCH = 20000;
    public static final int NO_BLOCKS = -1;
    /**
     * Gets an instance of the IndexCore.
     * <br> If an instance does not exist it is created.
     * @param newHandler the path to the handler.properties file
     * @return IndexCore
     */
    public static synchronized IndexCore getInstance(String newHandler)
    {
        if (instance == null)
        {
            instance = new IndexCore(newHandler);
        }
        return instance;
    }

    /**
     * Gets an instance of the IndexCore.
     * <br> If an instance does not exist it is created. with the default
     * handler.properties file.
     * @return IndexCore
     */
    public static synchronized IndexCore getInstance()
    {
        if (instance == null)
        {
            instance = new IndexCore(null);
        }
        return instance;
    }

    private IndexCore(String hnd)
    {
        this.tasks = new ListObservable();
        try
        {
            basePath = Platform.homePath() + "index" + File.separator;
            index = FSDirectory.open(new File(basePath + "indexed"));
            File f = new File(basePath + File.separator + "compressed");
            f.mkdirs();
            analyzer = new StandardAnalyzer(Version.LUCENE_30);


            /**
             * If index file doesn't exist creates a new one.
             */
            
            if (!IndexReader.indexExists(index))
            {
                IndexWriter writer;
                writer = new IndexWriter(index, analyzer, true, IndexWriter.MaxFieldLength.UNLIMITED);
                writer.close();
            }

            
        } catch (IOException ex)
        {
            ex.printStackTrace(System.out);
        }
    }
    
    

    /**
     * Checks if an index exists based on a given path
     * @param path
     * @return true if the index exists, false otherwise
     */
    private boolean indexExists(String path)
    {
        try
        {
            Directory local_main = FSDirectory.open(new File(path));
            return IndexReader.indexExists(local_main);
        } catch (IOException ex)
        {
            ex.printStackTrace(System.out);
        }
        return false;
    }

    
    
    private Document getDoc(IDoc doc)
    {
                
            Document luceneDoc = new Document();
            
            // Do you think that instanceof is a performance problem?
            // Then read about JIT. 
            // By: Luis Bastiao
            for (IDicomField f : doc.getDicomFields())
            {
                if (f instanceof DicomTextField)
                {
                    DicomTextField _f = (DicomTextField) f;
                    Field f2 = new Field(_f.getName(), _f.getValue(), Field.Store.YES, Field.Index.ANALYZED );
                    luceneDoc.add(f2);
                }
                else if (f instanceof DicomByteArrField)
                {
                    DicomByteArrField _f = (DicomByteArrField) f;
                    Field f2 = new Field(_f.getName(), _f.getValue(), Field.Store.YES );
                    luceneDoc.add(f2);
                }
                
                else if (f instanceof DicomNumericField)
                {
                    
                    DicomNumericField _f = (DicomNumericField) f;
                    NumericField f2 = new NumericField(_f.getName(), Field.Store.YES, true);
                    f2.setFloatValue(_f.getValue());
                    luceneDoc.add(f2);
                
                }
                else if (f instanceof LongField)
                {
                    
                    LongField _f = (LongField) f;
                    NumericField f2 = new NumericField(_f.getName());
                    f2.setLongValue(_f.getValue());
                    luceneDoc.add(f2);
                
                }
            }
            return luceneDoc;
    }
    
    public void notifySearchingBlock()
    {
        
        synchronized(monitorBlocks)
        {
            monitorBlocks.notifyAll();
            counterNotifications++;
        }
    }
    
    
    /**
     * Index a file.
     * This method tries to add a file to the index.
     * @param path the path to the file we want to index
     */
    //@Override
    public synchronized void index(List<IDoc> docs)
    {
        IndexWriter writer = null;
        try
        {
            if (!IndexReader.indexExists(index))
            {
                writer = new IndexWriter(index, analyzer, true, IndexWriter.MaxFieldLength.UNLIMITED);
            } else
            {
                writer = new IndexWriter(index, analyzer, false, IndexWriter.MaxFieldLength.UNLIMITED);
            }

            
            
            for (IDoc doc : docs)
            {
                Document luceneDoc = getDoc(doc);
                writer.addDocument(luceneDoc);
            }
                                                            
            
            //File f = new File(path);
            
            writer.setMaxFieldLength(Integer.MAX_VALUE);
            writer.commit();
            commits++;
            if (commits == 50000)
            {
                writer.optimize();
                commits = 0;
            }
            
            

        } catch (IOException ex)
        {
            ex.printStackTrace(System.out);
        } finally
        {
            try
            {
                writer.close();
            } catch (CorruptIndexException ex)
           {
                Logger.getLogger(IndexCore.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex)
            {
                Logger.getLogger(IndexCore.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public synchronized void optimize()
    {
        IndexWriter writer = null;
        try
        {
            if (!IndexReader.indexExists(index))
            {
                writer = new IndexWriter(index, analyzer, true, IndexWriter.MaxFieldLength.UNLIMITED);
            } else
            {
                writer = new IndexWriter(index, analyzer, false, IndexWriter.MaxFieldLength.UNLIMITED);
            }

            writer.setMaxFieldLength(Integer.MAX_VALUE);
            
            writer.optimize();
            
            

        } catch (IOException ex)
        {
            ex.printStackTrace(System.out);
        } finally
        {
            try
            {
                writer.close();
            } catch (CorruptIndexException ex)
           {
                Logger.getLogger(IndexCore.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex)
            {
                Logger.getLogger(IndexCore.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
    
    
    /**
     * Index a file.
     * This method tries to add a file to the index.
     * @param path the path to the file we want to index
     */
    //@Override
    public synchronized void index(IDoc doc)
    {
        IndexWriter writer = null;
        try
        {
            if (!IndexReader.indexExists(index))
            {
                writer = new IndexWriter(index, analyzer, true, IndexWriter.MaxFieldLength.UNLIMITED);
            } else
            {
                writer = new IndexWriter(index, analyzer, false, IndexWriter.MaxFieldLength.UNLIMITED);
            }

            
            long start = System.nanoTime(); 
                        

            
            Document luceneDoc = getDoc(doc);
            
                                                            
            //long tm = System.nanoTime()-start;
                        
            //System.out.println("Converting IDoc -> Document Lucene: " + tm/1000000L);
            
            //File f = new File(path);
            writer.addDocument(luceneDoc);
            writer.setMaxFieldLength(Integer.MAX_VALUE);
            //System.out.println("Converting IDoc -> Document Lucene: " + tm/1000000L);
            //start = System.nanoTime(); 
            writer.commit();
            //tm = System.nanoTime()-start;
                        
            //System.out.println("Commit Lucene: " + tm/1000000L);
            start = System.nanoTime(); 
            //writer.optimize();
            //tm = System.nanoTime()-start;
            //System.out.println("Opmtimize index Lucene: " + tm/1000000L);
            

        } catch (IOException ex)
        {
            ex.printStackTrace(System.out);
        } finally
        {
            try
            {
                writer.close();
            } catch (CorruptIndexException ex)
           {
                Logger.getLogger(IndexCore.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex)
            {
                Logger.getLogger(IndexCore.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void resetIndex(String path)
    {
        try
        {
            IndexWriter writer;
            writer = new IndexWriter(index, analyzer, true, IndexWriter.MaxFieldLength.UNLIMITED);
            writer.close();
            //index(path);
        } catch (IOException ex)
        {
            ex.printStackTrace(System.out);
        }
    }

//    @Override
    private synchronized int deleteFile(String path, boolean removeFile)
    {

        int deleted = 0;
        try
        {

            IndexReader reader = IndexReader.open(index, false);
            IndexSearcher searcher = null;
            if (IndexReader.indexExists(index))
            {
                searcher = new IndexSearcher(index, true);
            }
            //System.out.println(path);
            path = path.replace("\\", "\\\\");
            QueryParser parser = new QueryParser(Version.LUCENE_30, "FileName", analyzer);
            Query query = parser.parse("FilePath:\"" + path + "\"");
            TopDocs hits = searcher.search(query, 1);
            if (hits.scoreDocs.length > 0)
            {
                int docID = hits.scoreDocs[0].doc;
                reader.deleteDocument(docID);
                if (removeFile)
                {
                    File f = new File(path);
                    f.delete();
                }
                reader.flush();
                reader.commit(null);
                reader.close();
            }
            searcher.close();

        } catch (ParseException ex)
        {
            Logger.getLogger(IndexCore.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex)
        {

            Logger.getLogger(IndexCore.class.getName()).log(Level.SEVERE, null, ex);

            long start = System.currentTimeMillis();


        }


        return deleted;
    }

    

    public ListObservable<TaskRequest> getTaskRequestsList()
    {
        return this.tasks;
    }

    public String getName()
    {
        return PluginConstants.PluginName;
    }

    public void Initialize()
    {
    }

    
    /**
     * 
     * @param queryString
     * @param extrafields
     * @param blocks -1 means no BLOCKs, greater than zero means the size of blocks 
     * @return 
     */
    public synchronized List searching(String queryString, List<String> extrafields, int blocks, ListObservableSearch<SearchResult> lo)
    {
        List list = new ArrayList();
        boolean added = false;
        try
        {
            
            int numberOfHits = 0;
            //standard search... nearly textbook example
            GenericQueryParser parser;
            IndexSearcher searcher = null;
            if (IndexReader.indexExists(index))
            {
                searcher = new IndexSearcher(index, true);
            } else
            {
                return list;
            }

            Pattern pattern = Pattern.compile("([a-zA-Z_0-9]*:(Float|Numeric):)+");
            Matcher matcher = pattern.matcher(queryString);
            List<String> fieldsNumeric = new ArrayList<String>();
            while (matcher.find())
            {
                String field = matcher.group().split(":")[0];
                fieldsNumeric.add(field);
            }
            
            queryString = queryString.replace("Float:", "");
            queryString = queryString.replace("Numeric:", "");
            parser = new GenericQueryParser(Version.LUCENE_30, "FileName", analyzer, fieldsNumeric);

            Query query = parser.parse(queryString);

            AllDocCollector collector = new AllDocCollector();
            searcher.search(query, collector);

            int hitcount = collector.getHits().size();
            List<ScoreDoc> hitsList = collector.getHits();
            String FileName;
            String FileHash;
            String FilePath;
            String FileSize;
            
            //System.out.println("BLOCKS: " + blocks);
            //System.out.println("BLOCKS: " + BLOCKS_SEARCH);
            //System.out.println("BLOCKS2: " + ((Runtime.getRuntime().maxMemory()/1024)/1024));
            //System.out.println("HITCOUNT: " + hitcount);
            //(((Runtime.getRuntime().totalMemory()/1024)/1024)*20000)/128;
            
            //System.out.println("OperatorsName exists2: " + extrafields.contains("OperatorsName"));
            
            Hashtable<String, String> EF;
            /**
             * Check for each result, if there is a field that matches with the extrafields
             * parameter.
             */
            
            for (int i = 0; i < hitcount; i++)
            {
                //System.out.println("hitcount result : " + i);
                added = false;
                Document doc = searcher.doc(hitsList.get(i).doc);

                FileName = doc.get("FileName");
                FileHash = doc.get("FileHash");
                FilePath = doc.get("FilePath");
                FileSize = doc.get("FileSize");
                EF = new Hashtable<String, String>();

                if (extrafields != null)
                {
                    List l = doc.getFields();
                    for (int j = 0; j < extrafields.size(); j++)
                    {
                        String field = (String) extrafields.get(j);
                        
                        for (int k = 0; k < l.size(); k++)
                        {
                            Field f = (Field) l.get(k);
                        
                            if (field.equals(f.name()))
                            {
                                f = doc.getField(field);
                                if (f.isBinary())
                                {
                                    AbstractField binaryF = (AbstractField) f;
                                    byte[] temp = binaryF.getBinaryValue();
                                    byte[] tempb64 = Base64.encodeBase64(temp);
                                    String x = new String(tempb64);
                                    temp = null;
                                    tempb64 = null;
                                    EF.put(field, x);
                                    x = null;
                                } else
                                {
                                    
                                    String x = f.stringValue();

                                    EF.put(f.name(), x);
                                }
                                break;
                            }
                        }
                    }   
                }
                list.add(new SearchResult(FileHash, FileName, FilePath, FileSize, EF, PluginConstants.PluginName));
                
                numberOfHits++;
                if (numberOfHits%blocks==0)
                {
                    synchronized(monitorBlocks)
                    {
                        //System.out.println("Starting Sync monitorBlocks");
                        //System.out.println("List size: " +  list.size());
                        //System.out.println("Adding list, numberOfHits%blocks==0 => true");
                        lo.addAll(list);
                        counterBlocksSent++;
                        
                        //System.out.println("counterNotifications: "+ counterNotifications);
                        //System.out.println("counterBlocksSent: "+ counterBlocksSent);
                        
                        if (counterNotifications<counterBlocksSent)
                        {
                            try
                            {
                                //System.out.println("Is going to sleep");
                                monitorBlocks.wait();
                                //System.out.println("Wake now");
                            }
                            catch (InterruptedException ex) {
                            Logger.getLogger(IndexCore.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        }    
                            
                        
                        
                        //System.out.println("End Sync monitorBlocks");
                    }
                    
                    lo.resetArray();
                    list = new ArrayList();
                    added = true;
                
                }
                
                //System.out.println("Prunning #### !!!  " + prune);
                if (prune)
                {
                    lo.setFinish(true);
                    break;
                }
                
            }
            searcher.close();
            
            lo.setFinish(true);
            
            if (!added)
            {
                
                lo.addAll(list);
                //lo.resetArray();
                list = new ArrayList();
            }
            else
            {
                // Notifications!!
                lo.resetArray();
            }
            numberOfHits = 0 ;
            

        } catch (ParseException ex)
        {
            ex.printStackTrace(System.out);
        } catch (IOException ex)
        {
            ex.printStackTrace(System.out);
        } finally
        {
        }
        
        

        return list;
    }

    
    
        /**
     * 
     * @param queryString
     * @param extrafields
     * @param blocks -1 means no BLOCKs, greater than zero means the size of blocks 
     * @return 
     */
    public synchronized List searchingSync(String queryString, List<String> extrafields)
    {
        List list = new ArrayList();
        boolean added = false;
        try
        {
            
            int numberOfHits = 0;
            //standard search... nearly textbook example
            GenericQueryParser parser;
            IndexSearcher searcher = null;
            if (IndexReader.indexExists(index))
            {
                searcher = new IndexSearcher(index, true);
            } else
            {
                return list;
            }

            Pattern pattern = Pattern.compile("([a-zA-Z_0-9]*:(Float|Numeric):)+");
            Matcher matcher = pattern.matcher(queryString);
            List<String> fieldsNumeric = new ArrayList<String>();
            while (matcher.find())
            {
                String field = matcher.group().split(":")[0];
                fieldsNumeric.add(field);
            }
            
            queryString = queryString.replace("Float:", "");
            queryString = queryString.replace("Numeric:", "");
            parser = new GenericQueryParser(Version.LUCENE_30, "FileName", analyzer, fieldsNumeric);

            Query query = parser.parse(queryString);
            AllDocCollector collector = new AllDocCollector();
            searcher.search(query, collector);

            int hitcount = collector.getHits().size();
            List<ScoreDoc> hitsList = collector.getHits();
            String FileName;
            String FileHash;
            String FilePath;
            String FileSize;
            

            //(((Runtime.getRuntime().totalMemory()/1024)/1024)*20000)/128;
            Hashtable<String, String> EF;
            /**
             * Check for each result, if there is a field that matches with the extrafields
             * parameter.
             */
            for (int i = 0; i < hitcount; i++)
            {
                added = false;
                Document doc = searcher.doc(hitsList.get(i).doc);

                FileName = doc.get("FileName");
                FileHash = doc.get("FileHash");
                FilePath = doc.get("FilePath");
                FileSize = doc.get("FileSize");
                EF = new Hashtable<String, String>();

                if (extrafields != null)
                {
                    List l = doc.getFields();
                    
                    for (int j = 0; j < extrafields.size(); j++)
                    {
                        String field = (String) extrafields.get(j);

                        for (int k = 0; k < l.size(); k++)
                        {
                            Field f = (Field) l.get(k);
                            
                            if (field.equals(f.name()))
                            {
                                f = doc.getField(field);
                                if (f.isBinary())
                                {
                                    
                                    AbstractField binaryF = (AbstractField) f;
                                    byte[] temp = binaryF.getBinaryValue();
                                    byte[] tempb64 = Base64.encodeBase64(temp);
                                    String x = new String(tempb64);
                                    temp = null;
                                    tempb64 = null;
                                    EF.put(field, x);
                                    x = null;
                                } else
                                {
                                    
                                    String x = f.stringValue();

                                    EF.put(field, x);
                                }
                                break;
                            }
                        }
                    }
                }
                
                
                list.add(new SearchResult(FileHash, FileName, FilePath, FileSize, EF, PluginConstants.PluginName));
                numberOfHits++;
                
            }
            searcher.close();
            
            

        } catch (ParseException ex)
        {
            ex.printStackTrace(System.out);
        } catch (IOException ex)
        {
            ex.printStackTrace(System.out);
        } finally
        {
        }

        return list;
    }

    
    
    
    
    public synchronized List<SearchResult> search(String queryString, List<String> extrafields, int blocks, ListObservableSearch<SearchResult> lo)
    {

        //long t0Begin = System.nanoTime();
        return searching(queryString, extrafields, BLOCKS_SEARCH , lo);
        //return list;
    }

    public List<SearchResult> searchOne(String string, List<String> al, String string1, ListObservableSearch<SearchResult> lo)
    {
        return this.search(string, al, NO_BLOCKS, lo);
    }

    public FileObservable requestFile(String address, String name)
    {
        FileObservable fo = new FileObservable(address, name);
        fo.setFilePath(address);
        return fo;
    }

    public boolean isLocalPlugin()
    {
        return true;
    }


    public void setDefaultSettings()
    {
        Settings.getInstance().setDefaultSettings();
    }

    /**
     * @return the prune
     */
    public boolean isPrune() {
        return prune;
    }

    /**
     * @param prune the prune to set
     */
    public void setPrune(boolean prune) {
        this.prune = prune;
    }

}
