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
 */


package pt.ua.dicoogle.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;
import pt.ua.dicoogle.sdk.Utils.Platform;
import pt.ua.dicoogle.sdk.Utils.TagValue;
import pt.ua.dicoogle.sdk.Utils.TagsStruct;

/**
 *
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public class TagsXML extends DefaultHandler
{

    private TagsStruct tags = TagsStruct.getInstance() ;


    private boolean isTags = false ;
    private boolean isDIM = false ;
    private boolean isOthers = false ;
    private boolean isModalities = false ;
    private boolean isTag = false ;


    private String tagTemp  = null ;
    private String vr  = null ;
    private String tagAliasTemp = null ;

    private String modalities = null ;
    private String modalityID = null ;
    private ArrayList<String> modalityList = new ArrayList<String>();


    public TagsXML()
    {

    }


    @Override
    public void startElement( String uri, String localName, String qName,
            Attributes attribs )
    {



        if (localName.equals("Tags"))
        {
            isTags = true ;
        }
        else if( localName.equals( "DIM" ) )
        {
            isDIM = true;
        }
        else if ( localName.equals("tag") )
        {
            isTag = true ;
            this.tagTemp = this.resolveAttrib("id", attribs, localName);
            this.tagAliasTemp = this.resolveAttrib("alias", attribs, localName);
            this.vr = this.resolveAttrib("vr", attribs, localName);

        }
        else if ( localName.equals("others"))
        {
            isOthers = true ;
        }
         else if ( localName.equals("modalities"))
        {
            isModalities = true ;
            this.modalities = this.resolveAttrib("enable", attribs, localName);
            String indexAll = this.resolveAttrib("all", attribs, localName);
            if (indexAll.equals("true"))
            {
                tags.setIndexAllModalities(true);
            }
            else if (indexAll.equals("false"))
            {
                tags.setIndexAllModalities(false);
            }
            else
            {
                tags.setIndexAllModalities(false);
            }



        }
         else if (localName.equals("modality"))
         {

            this.modalityID = this.resolveAttrib("id", attribs, localName);
         }
    }


    @Override
    public void endElement( String uri, String localName, String qName )
    {

        if (localName.equals("Tags"))
        {
            isTags = false ;
        }
        else if( localName.equals( "DIM" ) )
        {
            isDIM = false;
        }
        else if ( localName.equals("tag") )
        {

            /** Verify if it is a DIM Fields */
            if (isDIM)
            {
                if (this.tagTemp!= null)
                {

                    tags.addDIM(new TagValue(Integer.parseInt(this.tagTemp, 16),
                        this.tagAliasTemp));

                }
            }
            else /** Otherwise it will be inserted on OtherFields */
            {
              /** Verification if it a duplicate fields will be made in TagsStruct
               And it is the reason it isn't verify outside the Module
               */

                TagValue v =new TagValue(Integer.parseInt(this.tagTemp, 16),
                        this.tagAliasTemp);
                v.setVR(vr);
                tags.addOthers(v);
            }
            isTag = false;
        }
        else if ( localName.equals("others"))
        {
            isOthers = false ;
        }
         else if ( localName.equals("modalities"))
        {
            isModalities = false ;


            if (modalities.equals("true"))
            {
                tags.setModalities(this.modalityList);
            }
            else
            {
                tags.setModalities(new ArrayList<String>());
            }

        }
         else if (localName.equals("modality") )
         {
            this.modalityList.add(this.modalityID);
         }
    }


   @Override
    public void characters( char[] data, int start, int length )
    {
         String tag = new String(data, start, length);


         if ( isTags && isDIM && isTag)
         {

             //System.out.println(tag);
             return;
         }


    }


     private String resolveAttrib( String attr, Attributes attribs, String defaultValue) {
         String tmp = attribs.getValue(attr);
         return (tmp!=null)?(tmp):(defaultValue);
     }


    public TagsStruct getXML() throws FileNotFoundException, SAXException, IOException
    {
        try
        {
            File file = new File(Platform.homePath() + "tags.xml");
            if (!file.exists())
            {
                tags.setDefaults();
                printXML();
                return tags;
            }


            // Never throws the exception cause file not exists so need try catch
            InputSource src = new InputSource( new FileInputStream(file) );
            XMLReader r = XMLReaderFactory.createXMLReader();
            r.setContentHandler(this);
            r.parse(src);
            return tags;
        }
        catch (IOException ex)
        {

        }
        catch (SAXException ex)
        {

        }
        return null;
    }

    public void printXML()
    {

        FileOutputStream out = null;
        try
        {
            out = new FileOutputStream(Platform.homePath() + "tags.xml");
        } catch (FileNotFoundException ex)
        {
            Logger.getLogger(TagsXML.class.getName()).log(Level.SEVERE, null, ex);
        }
        PrintWriter pw = new PrintWriter(out);
        StreamResult streamResult = new StreamResult(pw);
        SAXTransformerFactory tf = (SAXTransformerFactory) TransformerFactory.newInstance();
        //      SAX2.0 ContentHandler.
        TransformerHandler hd = null;
        try
        {
            hd = tf.newTransformerHandler();
        } catch (TransformerConfigurationException ex)
        {
            Logger.getLogger(TagsXML.class.getName()).log(Level.SEVERE, null, ex);
        }
        Transformer serializer = hd.getTransformer();
        serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        serializer.setOutputProperty(OutputKeys.METHOD, "xml");
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
        serializer.setOutputProperty(OutputKeys.STANDALONE, "yes");
        hd.setResult(streamResult);
        try
        {
            hd.startDocument();
        } catch (SAXException ex)
        {
            Logger.getLogger(TagsXML.class.getName()).log(Level.SEVERE, null, ex);
        }

        TagsStruct t = TagsStruct.getInstance();
        AttributesImpl atts = new AttributesImpl();
        try
        {
            //String curTitle = String.valueOf(s.getPort());
            //root element
            hd.startElement("", "", "Tags", atts);

            // DIM
            hd.startElement("", "", "DIM", atts);

            // tags
            Iterator it = t.getDimFields().keySet().iterator();
            while (it.hasNext())
            {
                int next = (Integer) it.next();
                atts.addAttribute("", "", "id", "", Integer.toHexString(next));
                atts.addAttribute("", "", "alias", "", t.getDimFields().get(next).getAlias()) ;
                hd.startElement("", "", "tag", atts);
                atts.clear();
                hd.endElement("", "", "tag");
            }
            hd.endElement("", "", "DIM");



            // Others
            hd.startElement("", "", "others", atts);

            // tags
            it = t.getManualFields().keySet().iterator();
            while (it.hasNext())
            {
                int next = (Integer) it.next();
                atts.addAttribute("", "", "id", "", Integer.toHexString(next));
                atts.addAttribute("", "", "alias", "", t.getManualFields().get(next).getAlias()) ;
                atts.addAttribute("", "", "vr", "", t.getManualFields().get(next).getVR()) ;
                hd.startElement("", "", "tag", atts);
                atts.clear();
                hd.endElement("", "", "tag");
            }
            hd.endElement("", "", "others");

            // Modalities
            String enable = "false" ;
            if (t.getModalities() != null)
                enable = "true";
            atts.clear();
            atts.addAttribute("", "", "enable", "", enable);
            String indexAllModalities = "false" ;
            if (t.isAllModalietiesEnable())
                indexAllModalities = "true";
            atts.addAttribute("", "", "all", "",indexAllModalities );
            hd.startElement("", "", "modalities", atts);

            // tags
           if (t.getModalities() != null){
               it = t.getModalities().iterator();
                while (it.hasNext())
                {
                    String next =  (String) it.next();
                    atts.addAttribute("", "", "id", "", next);
                    hd.startElement("", "", "modality", atts);
                    atts.clear();
                    hd.endElement("", "", "modality");
                }
           }


            hd.endElement("", "", "modalities");
            
            
            hd.startElement("", "", "dictionaries", atts);


            
            
            
            

            hd.endElement("", "", "dictionaries");
            
            
            hd.endElement("", "", "Tags");


            hd.endDocument();
        } catch (SAXException ex)
        {
            Logger.getLogger(TagsXML.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            try {
                out.close();
            } catch (IOException ex) {

            }
        }


    }
}
