package pt.ua.dicoogle.core.dicom;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import pt.ua.dicoogle.sdk.Utils.TagValue;
import pt.ua.dicoogle.sdk.Utils.TagsStruct;

/**
 *
 * @author bastiao
 */
public class PrivateDictionary 
{

    public void parse(String file)
    {
        String NL = System.getProperty("line.separator");
        Scanner scanner = null;
        try {
            scanner = new Scanner(new FileInputStream(file));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PrivateDictionary.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            while (scanner.hasNextLine()) {
                String text = scanner.nextLine();
                String tag = ""; 
                    
                String type = "";
                String name = ""; 
                
                
                if (text.startsWith("(") )
                {
                    String txt[] = text.split(" |\t");

                    for (int i = 0; i< txt.length; i++)
                    {
                        if (txt[i].startsWith("("))
                        {
                            tag = txt[i] ;
                        }
                        else if (txt[i].length()==2)
                        {
                            type = txt[i] ;
                            name = txt[i+1] ;
                        }
                    }
                }
                if (!tag.equals("")&&!type.equals("")&&!name.equals(""))
                {
                    //System.out.println("Tag: "+tag);
                    //System.out.println("Type: "+type);
                    //System.out.println("Name: "+name);
                    
                    TagsStruct tg = TagsStruct.getInstance();
                    tag = tag.replaceAll("\\(", "");
                    tag = tag.replaceAll("\\)", "");
                    tag = tag.replaceAll(" ", "");
                    tag = tag.replaceAll(",", "");
                    //System.out.println("Tag: "+tag);
                    //System.out.println("Type: "+type);
                    //System.out.println("Name: "+name);
                    
                    TagValue v = new TagValue(Integer.parseInt(tag, 16), name);
                    
                    v.setVR(type);
                    tg.addOthers(v);
                    
                }
            }
        } finally {
            scanner.close();
        }
    }
    
    public static void main(String [] args)
    {
        PrivateDictionary pd = new PrivateDictionary();
        pd.parse("/Users/bastiao/MAP-I/Code/dicomlamedictionaryanddicom/similarity.dic");
        
    
    }
    
}
