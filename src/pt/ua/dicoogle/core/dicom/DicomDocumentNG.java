/*  Copyright   2010 - IEETA
 *
 *  This file is part of Dicoogle.
 *
 *  Author: Luís A. Bastião Silva <bastiao@ua.pt>
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
 * 
 */
package pt.ua.dicoogle.core.dicom;


import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.dcm4che2.data.DicomElement;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.VR;
import org.dcm4che2.io.DicomInputStream;
import org.dcm4che2.io.StopTagInputHandler;
import pt.ieeta.anonymouspatientdata.core.impl.MatchTable;
import pt.ua.dicoogle.DebugManager;
import pt.ua.dicoogle.core.ServerSettings;
import pt.ua.dicoogle.core.index.IndexEngine;

import pt.ua.dicoogle.rGUI.client.UIHelper.Dicom2JPEG;
import pt.ua.dicoogle.sdk.Utils.DictionaryAccess;
import pt.ua.dicoogle.sdk.Utils.SearchResult;
import pt.ua.dicoogle.sdk.Utils.TagValue;
import pt.ua.dicoogle.sdk.Utils.TagsStruct;
import pt.ua.dicoogle.sdk.index.DicomDocument;
import pt.ua.dicoogle.sdk.index.IDoc;
import pt.ua.dicoogle.sdk.index.handlers.DocumentHandlerException;
import pt.ua.dicoogle.sdk.index.handlers.FileAlreadyExistsException;





/**
 * Specific implementation for DICOM files of the DocumentHandler
 *
 * It was refactored in May 2009:
 * Now it load dynamic fields from XML struct
 *
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 * @author Marco
 */
public class DicomDocumentNG
{

    private IDoc doc;       // Input Doc
    
    private TagsStruct tagStruct ;

    private ServerSettings settings = ServerSettings.getInstance();

    /**
     * Convert the byte array to an int.
     *
     * @param b The byte array
     * @return The integer
     */
    public static long byteArrayToInt(byte[] b) {
        return byteArrayToInt(b, 0);
    }

    /**
     * Convert the byte array to an int starting from the given offset.
     *
     * @param b The byte array
     * @param offset The array offset
     * @return The integer
     */
    public static long byteArrayToInt(byte[] b, int offset) {
        long value = 0;
        for (int i = 0; i < b.length; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (b[i + offset] & 0x000000FF) << shift;
        }
        return value;
    }
/**
 * Converts a 4 byte array of unsigned bytes to an long
 * @param b an array of 4 unsigned bytes
 * @return a long representing the unsigned int
 */
public static final long unsignedIntToLong(byte[] b) 
{
    long l = 0;
    l |= b[0] & 0xFF;
    l <<= 8;
    l |= b[1] & 0xFF;
    l <<= 8;
    l |= b[2] & 0xFF;
    l <<= 8;
    l |= b[3] & 0xFF;
    return l;
}
    

public static Object[] reverse(Object[] arr)
{
List<Object> list = Arrays.asList(arr);
Collections.reverse(list);
return list.toArray();
}

/**
 * Converts a two byte array to an integer
 * @param b a byte array of length 2
 * @return an int representing the unsigned short
 */
public static final int unsignedShortToInt(byte[] b) 
{
    int i = 0;
    i |= b[0] & 0xFF;
    i <<= 8;
    i |= b[1] & 0xFF;
    return i;
}

    /**
     * Verify if a VR is binary
     *
     * @param vr VR is the DICOM Value Representation
     * @return boolean returns true if it is a binary fields (see DICOM VR fields)
     */
    public static boolean isBinaryFields(VR vr)
    {
        return vr == VR.SS || vr == VR.US ||vr == VR.SL || vr == VR.UL || vr == VR.FD;
    }

    public static void main(String [] args)
    {
    
        byte [] arr = new byte[4];
        arr[0] = 0;
        arr[1] = 0;
        arr[2] = 1;
        arr[3] = -71;
        long tmpValue = unsignedIntToLong(arr);

        
    }
    
    public static String getValue(DicomElement element)
    {
        String value = null;
        
        if (!DicomDocumentNG.isBinaryFields(element.vr()))
        {
            
            value =  new String(element.getBytes());
            
        }
        else
        {
            
            
            if (element.vr()==VR.FD && element.getBytes().length == 8)
            {
                
                double tmpValue = element.getDouble(true);
                value = String.valueOf(tmpValue);

                
            }    
            else if (element.vr()==VR.FL&& element.getBytes().length == 4)
            {
                
                float tmpValue = element.getFloat(true);
                value = String.valueOf(tmpValue);                
            }    
        
                    
            else if (element.vr()==VR.UL && element.getBytes().length == 4) 
            {
                
                //long tmpValue = unsignedIntToLong(element.getBytes());
                long tmpValue =  element.getInt(true);
                value = String.valueOf(tmpValue);

                
            }     
            else if (element.vr()==VR.US && element.getBytes().length == 2) 
            {
                
                
                short [] tmpValue =  element.getShorts(true);
                value = String.valueOf(tmpValue[0]);

                
            }        
            else if (element.vr()!=VR.US) 
            {
                
                long tmpValue = byteArrayToInt(element.getBytes());
                //int tmpValue = element.getInt(true);
                value = String.valueOf(tmpValue);
            }
            else
            {
                
        
                
                
                //byte [] arrAux = element.getBytes();
                /*
                if (element.bigEndian())
                {
                    byte aux = arrAux[1];
                    arrAux[1] = arrAux[0];
                    arrAux[0] = aux;
                }*/
                int tmpValue = element.getInt(true);
                value = String.valueOf(tmpValue);
            }

        }
        
        return value;
        

    }



    

     
public static long toLong(byte[] data) {
    if (data == null || data.length != 8) return 0x0;
    // ----------
    return (long)(
            // (Below) convert to longs before shift because digits
            //         are lost with ints beyond the 32-bit limit
            (long)(0xff & data[0]) << 56  |
            (long)(0xff & data[1]) << 48  |
            (long)(0xff & data[2]) << 40  |
            (long)(0xff & data[3]) << 32  |
            (long)(0xff & data[4]) << 24  |
            (long)(0xff & data[5]) << 16  |
            (long)(0xff & data[6]) << 8   |
            (long)(0xff & data[7]) << 0
            );
}
  public static double toDouble(byte[] data) {
    if (data == null || data.length != 8) return 0x0;
    // ---------- simple:
    
    return Double.longBitsToDouble(toLong(data));
}
    public static double[] toDoubleA(byte[] data) {
    if (data == null) return null;
    // ----------
    if (data.length % 8 != 0) return null;
    double[] dbls = new double[data.length / 8];
    for (int i = 0; i < dbls.length; i++) {
        dbls[i] = toDouble( new byte[] {
            data[(i*8)],
            data[(i*8)+1],
            data[(i*8)+2],
            data[(i*8)+3],
            data[(i*8)+4],
            data[(i*8)+5],
            data[(i*8)+6],
            data[(i*8)+7],
        } );
    }
    return dbls;
}
    
public static final int byteArrayToInt2(byte [] b) {
        return (b[0] << 24)
                + ((b[1] & 0xFF) << 16)
                + ((b[2] & 0xFF) << 8)
                + (b[3] & 0xFF);
}


public String convertStringToHex(String str){
 
	  char[] chars = str.toCharArray();
 
	  StringBuffer hex = new StringBuffer();
	  for(int i = 0; i < chars.length; i++){
	    hex.append(Integer.toHexString((int)chars[i]));
	  }
 
	  return hex.toString();
  }
 
    public String anonymousFilter(String tag, String value)
    {
        String result = value;
        if (value==null)
            return value;
        if (tag.equals("PatientName"))
        {
            result = MatchTable.getInstance().getName(value);
        }
        else if (tag.equals("PatientID"))
        {
            result = MatchTable.getInstance().getPatientID(value);
        }
        else if(tag.equals("AccessionNumber"))
        {
            result = MatchTable.getInstance().getAccessionNumber(value);
        }
        return result;
    }
    


    /**
     * Retrieve information of DICOM file based on XML
     * and loaded tags
     * @param f
     * @return
     * @throws pt.ua.dicoogle.core.index.lucene.LuceneSupport.DocumentHandlerException
     */
   
    public IDoc getDocument(File f) throws DocumentHandlerException, FileAlreadyExistsException
    {
        long start = System.currentTimeMillis();

        List list = IndexEngine.getInstance().searchSync("FilePath:\""+f.getAbsolutePath()+"\"", null);
        
        if (list.size()>0)
        {
            //System.out.println("File: " + f.getAbsoluteFile() + "already exists");
            throw new FileAlreadyExistsException();
        }


        IDoc _doc = null ;
        
        tagStruct = TagsStruct.getInstance();
        
        FileInputStream fis = null;
        DicomInputStream dis = null;
        
        BufferedInputStream bis = null;
        
        try
        {
            if (f.getAbsolutePath().endsWith(".dll")|| f.getAbsolutePath().endsWith(".exe") || f.getName().startsWith("."))
            {
                return null;
            }
            
            
            fis = new FileInputStream(f);
            bis = new BufferedInputStream(fis);
            dis = new DicomInputStream(bis);
            //dis = new DicomInputStream(bis);
            
            dis.setFileSize(f.length());

            
            dis.setHandler(new StopTagInputHandler(Tag.PixelData));
            
            //DebugManager.getInstance().debug(f.getAbsolutePath());
            DicomObject d = dis.readDicomObject();
            _doc = new DicomDocument();




            /** Verify if SOPInstanceUID exists
             * If it exists it will verify columns + rows.
             * If it is a big image it will be re-index
             * otherwise it will be skipped.
             */
            
            String SOPInstanceUID = d.getString(Tag.SOPInstanceUID);

            DicomElement e1 = d.get(Tag.Columns);
            DicomElement e2 = d.get(Tag.Rows);

            if (e1!=null&&e2!=null)
            {
                String columns = getValue(e1);
                String rows = getValue(e2);
                if (rows!= null && !rows.equals("")&&columns!= null && !columns.equals(""))
                {
                    String query = "SOPInstanceUID:" + SOPInstanceUID + " AND (Columns:Float:[" + columns + " TO 65536] AND Rows:Float:[" + rows + " TO 65536])";
                    List<SearchResult> listSOP = IndexEngine.getInstance().search(query, null);
                    if (listSOP.size() > 0) 
                    {
                        
                        //System.out.println("SOPInstanceUID already exists: " + SOPInstanceUID);
                        throw new FileAlreadyExistsException();
                        //return null;
                    }
                }
            }


            /**
             * How it it works?
             * Actually Lucene 2.4.X  doesn't support added hidden fields, so
             * the stretegie it is following:
             *   - DIM fields specified on XML are already indexed
             *   - Other fields was getted dinamic way  (It depends of Settings)
             *   - Thumbnails are indexed, if enabled on Settings
             */

            /***
             *
             * DIM Fields is mandatory -- No need to check on file XML file
             * if it is really enabled
             *
             *
             */
            Hashtable<Integer, TagValue> dim = tagStruct.getDimFields() ; // Get the HashTable
            Set<Integer> keys = dim.keySet() ;
            String data;


            for ( int key : keys )
            {
                
                DicomElement e = d.get(key);
                if (settings.isIndexAnonymous())
                {
                    data = this.anonymousFilter(dim.get(key).getAlias(), d.getString(key));
                }
                else
                {
                    data = d.getString(key);
                }
                
                
                /** If it is null should be passed has a empty string,
                 *  it's bad idea pass null to Lucene.
                 */
                if(data == null)
                {
                    data = "";
                }
                addField(_doc, d.vrOf(key), dim.get(key).getAlias(), data );
            }

            //DebugManager.getInstance().debug(">>>> DIM fields was indexed");

            String otherToIndex = "" ;


            /**
             *  Other fields is not mandatory
             *  It is needed to check if
             *
             */

            Hashtable<Integer, TagValue> others = tagStruct.getManualFields() ; // Get the HashTable
            keys = others.keySet() ;
            for ( int key : keys )
            {
                data = d.getString(key);
                /** If it is null should be passed has a empty string,
                 *  it's bad idea pass null to Lucene.
                 */
                if(data == null)
                {
                    data = "";
                }

                
                VR v = VR.valueOf(Integer.parseInt(convertStringToHex(others.get(key).getVR()), 16));
                
                addField(_doc, v, others.get(key).getAlias(), data );
                otherToIndex = otherToIndex + " " + data ;
            }

            //DebugManager.getInstance().debug(">>>> DIM fields was indexed");

            /**
             * Other or Modalities need a deep search on DICOM Document
             * And it produces a delay indexing
             */
            if (tagStruct.isModalities())
            {

                /** Now verify if the modality of this document is enable in
                 * setting file
                 */

                if (tagStruct.isModalityEnable(d.getString(Tag.Modality))||tagStruct.isAllModalietiesEnable())
                {
                    List<String> _list = getRecursiveDicomElement(_doc, d, "", dim, others, -1);
                    otherToIndex = _list.get(0);
                 }

            }
            //DebugManager.getInstance().debug("Indexing others .. Modalities");
            _doc.add("others", otherToIndex);

            if (settings != null && indexStoreThumbnails())
            {
                int matrix = Integer.parseInt(settings.getThumbnailsMatrix());
                ByteArrayOutputStream jpgMem = Dicom2JPEG.Dicom2MemJPEG(f, matrix);
                //DebugManager.getInstance().log("SIZE: " + jpgMem.toByteArray().length);
                if (jpgMem != null)
                        _doc.add("Thumbnail", jpgMem.toByteArray());
            }
        }
        catch (FileAlreadyExistsException ex) {
            throw new FileAlreadyExistsException();
        }
                
        catch (Exception ex) {
            
            DebugManager.getInstance().log("Index error: " + f.getAbsolutePath() + " : " + 
                     "\n");
           if (DebugManager.getInstance().isDebug())
                ex.printStackTrace();
            DebugManager.getInstance().log(f.getAbsolutePath() + " : " + 
                    ex.toString() + "\n");
            
            _doc = null;

        } finally {

            try {
                dis.close();
                fis.close();
                bis.close();
            }
            catch (Exception ex) {
                
               _doc = null;
            }
        }
        
        long end = System.currentTimeMillis();

        System.out.println("Execution time was "+(end-start)+" ms.");
        
        return _doc ;

    }

    private List<String> getRecursiveDicomElement(IDoc _doc, DicomObject d, String prefix,
            Hashtable<Integer, TagValue> dim, Hashtable<Integer, TagValue> others, int nItems )
    {
        
        String otherToIndex = "";
        String tagList = "";
        List<String> tmp = new ArrayList<String>();
        // Hard heuristic just to be sure that application will be not running forever
        if (prefix.length()> 512)
        {
            
        
            tmp.add(otherToIndex);
            tmp.add(tagList);
            
        }
        Map<String, String> sequences = new HashMap<String, String>();

        DictionaryAccess dictionaryDicom =  DictionaryAccess.getInstance();
        Iterator<DicomElement> it = d.iterator();
        
        int i = 0;
        while (it.hasNext()) {
            DicomElement dcm = it.next();
            int tmpTag = dcm.tag();
            //System.out.println(tmpTag);
            

            if (!dim.containsKey(tmpTag) && !others.containsKey(tmpTag)) {
                String tagName = d.nameOf(dcm.tag());
                
                String newTag = dictionaryDicom.tagName(dcm.tag());
                
                //DebugManager.getInstance().debug("tagname: " + tagName+ "or: " + dcm.toString());
                
                if (dcm.hasItems()) {
                    //System.out.println("Prefix: " + prefix);
                    newTag = dictionaryDicom.tagName(dcm.tag());
                    //System.out.println("Tag: " + newTag );
                    //System.out.println("Items: " + dcm.countItems() );
                    //prefix = prefix + newTag + "_";
                    String prefixAux = prefix + newTag + "_";
                    //if (newTag.equals("null"))
                    //    break;
                    
                    //otherToIndex = otherToIndex + " " + getFields(dcm, _doc);
                    //System.out.print("prefixAux:" + prefixAux);
                    if (dcm.countItems()>0)
                    {
                        List<String> list = getRecursiveDicomElement(_doc, dcm.getDicomObject(0), prefixAux, dim, others, dcm.countItems());

                        otherToIndex = otherToIndex + " " + list.get(0);
                        tagList = tagList + " " + list.get(1);

                        sequences.put(newTag, list.get(1)); 
                    }
                    //System.out.println("Seq->" + newTag+ ", val-->" + list.get(1));
                    
                    //addField(_doc, VR.TM, newTag, list.get(1));
                    
                } /**
                 *
                 * Drop the non-search-valid fields (Pixel data etc)
                 *
                 */
                else if (dcm.vr() != VR.OB && dcm.vr() != VR.OW && !tagName.equals("?")&& dictionaryDicom.tagName(dcm.tag())!=null) {
                    String value;
                    
                    //System.out.println("Tag: " + newTag);
                    //System.out.println("Prefix: " + prefix);
                    value = getValue(dcm);
                    
                    if (value != null) {
                         newTag = dictionaryDicom.tagName(dcm.tag());
                        tagList = tagList + " " + prefix+newTag;
                        if (newTag != null) {
                            addField(_doc, dcm.vr(), prefix+newTag, value);
                            //addField(_doc, dcm.vr(), newTag, value);
                        }
                        otherToIndex = otherToIndex + " " + value;
                    }
                }
                
            }
            i++;
            //if (i!=0 && i>=nItems)
            //    break;
        }
        //System.out.println("FIM DA RECURSIVIDADE");
        // There is a sequence
        
        for (String s : sequences.keySet())
        {
            //System.out.println("SQ: " + s +", val: " + sequences.get(s));
            addField(_doc, VR.ST, s, sequences.get(s)); 
        }

        /*String [] tmpString = tagList.split(" ");
        for (int k = 0 ; k< tmpString.length; k++)
        {
            if (tmpString[k].contains("Sequence"))
            {
                String [] tmpString2 = tmpString[k].split("_");
                for (int j = 0 ; j< tmpString2.length; j++)
                {

                }
            }
        }*/

        
        
        tmp = new ArrayList<String>();
                
        tmp.add(otherToIndex);
        tmp.add(tagList);
            
        return tmp;
    }
    
    

    /**
     *
     * @return
     */
    private boolean indexStoreThumbnails()
    {
        boolean result = false;
        if (settings!=null)
            result = settings.getSaveThumbnails();
        return result;
    }



    private void addField(IDoc _doc, VR vr, String tag, String value)
    {
        if (_doc==null)
            return;
        if (tag==null)
            return;
        if (value==null)
            value = "";
        if (vr == VR.IS|| vr==VR.DS||vr==VR.US ||  vr == VR.FD || vr == VR.FD )
        {
            

            try
            {

                Float _v = Float.valueOf(value);
                _doc.add(tag, _v);
                //System.out.println("Tag: " + tag + " : " + value);
            }
            catch(NumberFormatException ex)
            {
                _doc.add ( tag,value );
            }
        }
        else
        {
            _doc.add ( tag,value);
        }
    }

}
