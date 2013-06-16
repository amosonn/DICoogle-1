
package pt.ua.dicoogle.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import pt.ua.dicoogle.core.index.IndexEngine;

/**
 *
 * @author bastiao
 */
public class RenameOperation extends Thread
{


    private String file1; 
    private String file2;
    
    
    public RenameOperation(String file1, String file2)
    {
        this.file1 = file1;
        this.file2 = file2;   
    }
    
    public void run()
    {
        rename(file1, file2);
        IndexEngine core = IndexEngine.getInstance() ;
        core.indexQueue(new File(file2).getAbsolutePath(), true);
    }
    
    
    public boolean rename(String file1, String file2)
    {
        boolean renamed = false;
        try
        {
            FileChannel ic = new FileInputStream(file1).getChannel();

            FileChannel oc = new FileOutputStream(file2).getChannel();
            ic.transferTo(0, ic.size(), oc);
            ic.close();
            oc.close();
            renamed = true;
        }
        catch (Exception e)
        {
            renamed = false;
        }
        
        
        return renamed;
        
    }
    
    
    
}
