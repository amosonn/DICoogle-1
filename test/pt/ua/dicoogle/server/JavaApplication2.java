package pt.ua.dicoogle.server;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import pt.ua.dicoogle.core.ServerSettings;
import pt.ua.dicoogle.core.XMLSupport;

/**
 *
 * @author bastiao
 */
public class JavaApplication2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException{
                
        ServerSettings settings = new XMLSupport().getXML();

        ServerSettings.getInstance().setStoragePort(6666);

        SOPList list = SOPList.getInstance();
        list.setDefaultSettings();

        list.setDefaultSettings();
        List l = list.getKeys();
        String[] keys = new String[l.size()];

        for (int i = 0; i < l.size(); i++)
        {
            keys[i] = (String) l.get(i);
        }

        RSIStorage rsi = new RSIStorage(keys, SOPList.getInstance());
        rsi.start();
    }
        
}
