
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
public class IndexOperation extends Thread
{

    private String file;
    
    
    public IndexOperation(String file)
    {
        this.file = file;   
    }
    
    public void run()
    {
        
        IndexEngine core = IndexEngine.getInstance() ;
        core.indexQueue(new File(file).getAbsolutePath(), true);
    }
    
}
