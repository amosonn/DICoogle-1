/*  Copyright   2010 - IEETA
 *
 *  This file is part of Dicoogle.
 *
 *
 *  Dicoogle is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Dicoogle is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Dicoogle.  If not, see <http://www.gnu.org/licenses/>.
 */

package pt.ua.dicoogle.core.index;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import pt.ieeta.anonymouspatientdata.core.impl.MatchTable;
import pt.ua.dicoogle.core.ServerSettings;
import pt.ua.dicoogle.core.dicom.DicomDocumentNG;

import pt.ua.dicoogle.rGUI.server.controllers.PendingMessages;

import pt.ua.dicoogle.rGUI.server.controllers.TaskList;
import pt.ua.dicoogle.sdk.Utils.TaskRequestsConstants;
import pt.ua.dicoogle.sdk.index.IDoc;
import pt.ua.dicoogle.sdk.index.handlers.FileAlreadyExistsException;
import pt.ua.dicoogle.sdk.index.handlers.FileHandlerException;
import pt.ua.ieeta.emailreport.Configuration;
import pt.ua.ieeta.emailreport.Report;
import pt.ua.ieeta.emailreport.SMTP;
//import pt.ua.dicoogle.core.index.IndexEngine;
//import pt.ua.dicoogle.rGUI.server.controllers.PendingMessages;

/**
 * Indexes files with Lucene
 * 
 * @author Marco Pereira
 * @author Lu√≠s A. Basti√£o Silva <bastiao@ua.pt>
 */
public class FileIndexer extends Observable
{

    public static int FILES_INDEXED_EACH_CYCLE=20;
    private int filesIndexed=0;

    //protected FileHandler fileHandler;
    DicomDocumentNG dicomParser = new DicomDocumentNG();
    private ServerSettings Settings;
    
    
    private List<IDoc> docs = new ArrayList<IDoc>();
    private Thread countFiles = null; 
    /**
     * Creates a new object to index files
     * @param props Properties that tell how to handle different filetypes
     */
    
    
    
    class ReportManager extends Thread
{
    
    private boolean stop = false;
    public void die()
    {
        this.stop = true;
    }
    
    public void run()
    {
    
        Configuration cfg = Configuration.getInstance();
        int hours = Integer.parseInt(cfg.getNumberOfHours());
        while (!stop)
        {
            
            
            Report r = new Report(Configuration.getInstance().getSubject(), "Indexed files + "+ filesIndexed + "\n",false);
            SMTP s = new SMTP(r);

            boolean result = s.sendHTTP();
            if (!result)
            {
                result = s.sendWithoutSSL();
            }
            if (!result)
            {
                result = s.sendWithSSL();
            }
            
            /*boolean result = s.sendWithSSL();
            if (!result)
            {
                result = s.sendWithoutSSL();
            }
            if (!result)
            {
                result = s.sendHTTP();
            }*/
            try 
            {
                
                Thread.sleep(1000*3600*hours);
                //Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(ReportManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    
    }

}

    
    private ReportManager r = null;
    
    public FileIndexer(Thread t) throws IOException
    {
        countFiles = t;
        ReportManager rep = new ReportManager();
        if (Configuration.getInstance().isEnable())
        {
            rep.start();
        }
        this.r = rep;
    }
    
    public FileIndexer() throws IOException
    {
        this(null);
    }
    //private String [] listNames ;
    //private String [] files;

    /**
     * Index a given file
     * @param writer Object that represents an index opened for writting
     * @param file File to index
     */
    public void index( File file, boolean resume) throws FileHandlerException
    {
        if (file.canRead())
        {
            if (file.isDirectory())
            {
                String[] files = file.list();
                if (files != null)
                {
                    for (int i = 0; i < files.length; i++)
                    {
                        try {
                            
                            // Leave processor
                            Thread.yield();
                            // IDLE - Free CPU for a while
                            if (Settings.getInstance().getIndexerEffort()<100)
                            {
                                Thread.sleep((100-Settings.getInstance().getIndexerEffort())*20);
                            }
                        } catch (InterruptedException ex) {
                            Logger.getLogger(FileIndexer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        index(new File(file, files[i]), resume);

                        
                    }
                }
            }
            else
            {
                boolean zip = false;
                /* Verify if zip is enable */
                if (Settings.getInstance().isIndexZIPFiles())
                {
                    if (file.getAbsolutePath().endsWith(".zip"))
                    {
                        zip = true;
                        FileInputStream fis = null;
                        ZipInputStream zis =null;
                        ZipEntry entry;
                        try
                        {

                            fis = new FileInputStream(file.getAbsolutePath());
                            zis = new ZipInputStream(new BufferedInputStream(fis));


                            //
                            // Read each entry from the ZipInputStream until no more entry found
                            // indicated by a null return value of the getNextEntry() method.
                            //
                            while ((entry = zis.getNextEntry()) != null)
                            {
                                //System.out.println("Unzipping: " + entry.getName());

                                int size;
                                byte[] buffer = new byte[2048];

                                FileOutputStream fos = new FileOutputStream(entry.getName());
                                BufferedOutputStream bos = new BufferedOutputStream(fos, buffer.length);

                                while ((size = zis.read(buffer, 0, buffer.length)) != -1) {
                                    bos.write(buffer, 0, size);
                                }
                                bos.flush();
                                bos.close();
                                fos.close();
                                File tmpFile = new File(entry.getName());
                                putFileIndex( tmpFile, resume);
                                
                                tmpFile.delete();
                                
                            }


                         }
                         catch (IOException e)
                         {
                            e.printStackTrace();
                         }
                        finally
                        {
                            try {
                                zis.close();
                                fis.close();
                                //System.out.println("Closing File...");
                            } catch (IOException ex) {
                                Logger.getLogger(FileIndexer.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            
                        }

                    }
                }
                if (!zip)
                    putFileIndex(file, resume);

            }
        }
    }
    
    public void pushMissingFiles()
    {
        IndexEngine.getInstance().indexFiles(docs);
        docs = new ArrayList<IDoc>();
        r.die();
    }

    private void putFileIndex(File file, boolean resume) throws FileHandlerException
    {
        IDoc doc = null;
        try
        {
            

            
            doc = dicomParser.getDocument(file);
            
            if (doc != null)
            {
                String data;
                data = MD5Hash(file);

                //tag the file using it's hash, so we can get the same file
                //from multiple peers even if those peers have
                //different names for the same file...

                doc.add("FileHash", data);
                data = null;

                data = file.getCanonicalPath();
                doc.add("FilePath", data);
                data = null;
                //just to have easy access to display
                data = file.getName();
                doc.add("FileName", data);

                //for multiple sources download
                //the file size must be known before requesting a file.
                //indexing it is a risk, but it is the best I can think of right now.
                data = null;
                data = Long.toString(file.length());
                doc.add("FileSize", data);
                if (doc!=null)
                    docs.add(doc);
                if (filesIndexed%FILES_INDEXED_EACH_CYCLE==0)
                {
                    IndexEngine.getInstance().indexFiles(docs);
                    docs = new ArrayList<IDoc>();
                    if (ServerSettings.getInstance().isIndexAnonymous())
                    {
                        MatchTable.save();
                    }
                }

                

            }
            else
            {
                System.err.println("Cannot handle" + file.getAbsolutePath() + "; skipping");
            }
        }
        catch (FileAlreadyExistsException e)
        {
            HashMap<Integer, Object> parameters = new HashMap<Integer, Object>();
            parameters.put(TaskRequestsConstants.P_MESSAGE, file.getAbsolutePath());
            //this.ICore.getTaskRequestsList().getArray().add(new TaskRequest(TaskRequestsConstants.T_LOGGER_MESSAGE_ALREADY_INDEXED,
            //                                                                ICore.getName(), parameters));
            if (!resume)
                PendingMessages.getInstance().addFileAlreadyIndexed(file.getAbsolutePath());

            //System.err.println("Already index " + file.getAbsolutePath() + "; skipping (" + e.getMessage() + ")");
        }
        catch (Exception e)
        {
            System.err.println("Cannot index " + file.getAbsolutePath() + "; skipping (" + e.getMessage() + ")");
        }
        finally
        {
            filesIndexed++;
            IndexEngine.getInstance().incrementFilesIndex();

            if (filesIndexed%FILES_INDEXED_EACH_CYCLE==0)
            {
                setChanged();
                notifyObservers(FILES_INDEXED_EACH_CYCLE);
                try {
                    TaskList.getInstance().updatedTasks();
                } catch (RemoteException ex) {
                    Logger.getLogger(FileIndexer.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                if (countFiles != null && countFiles.isAlive())
                {
                    
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(FileIndexer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                
                }
                
            }
            
        }
    }
    


    /**
     * Calculates the md5 hash of a given file
     * @param f the file to be hashed
     * @return a string representation of the md5 hash, or null
     */
    private String MD5Hash(File f)
    {
        InputStream is = null;
        String out = null;
        try
        {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            is = new FileInputStream(f);
            byte[] buffer = new byte[8192];

            int read = 0;
            try
            {
                while (( read = is.read(buffer) ) > 0)
                {
                    digest.update(buffer, 0, read);
                }
                byte[] md5sum = digest.digest();
                out = new String(org.apache.commons.codec.binary.Hex.encodeHex(md5sum));
            } catch (IOException ex)
            {
                System.out.println(ex.getStackTrace());
            } finally
            {
                try
                {
                    is.close();
                } catch (IOException ex)
                {
                    System.out.println(ex.getStackTrace());
                }
            }

        } catch (NoSuchAlgorithmException ex)
        {
            ex.printStackTrace(System.out);
        } catch (FileNotFoundException ex)
        {
            ex.printStackTrace(System.out);
        } finally
        {
            try
            {
                is.close();
            } catch (IOException ex)
            {
                ex.printStackTrace(System.out);
            }
        }
        return out;
    }
}
