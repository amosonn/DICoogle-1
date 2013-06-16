
package pt.ua.dicoogle.server;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import pt.ua.dicoogle.core.ServerSettings;
import pt.ua.dicoogle.core.XMLSupport;

/**
 *
 * @author bastiao
 */
public class StorageMainTest 
{

    public static void main (String [] args)
    {
        /* Load all Server Settings from XML */
        ServerSettings settings = new XMLSupport().getXML();

        ServerSettings.getInstance().setStoragePort(6666);

        SOPList list = SOPList.getInstance();

        list.setDefaultSettings();
        List l = list.getKeys();
        String[] keys = new String[l.size()];

        for (int i = 0; i < l.size(); i++) {
            keys[i] = (String) l.get(i);
        }

        RSIStorage rsi = new RSIStorage(keys, SOPList.getInstance());
        try
        {
            rsi.start();
        } 
        catch (IOException ex)
        {
            Logger.getLogger(StorageTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    
    }
    
}
