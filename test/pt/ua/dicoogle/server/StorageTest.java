package pt.ua.dicoogle.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.*;

/**
 *
 * @author bastiao
 */
public class StorageTest
{
    
    public StorageTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() 
    {
        
        
        SOPList list = SOPList.getInstance();

        list.setDefaultSettings();
        List l = list.getKeys();
        String[] keys = new String[l.size()];

        for (int i = 0; i < l.size(); i++) {
            keys[i] = (String) l.get(i);
        }

        RSIStorage rsi = new RSIStorage(keys, SOPList.getInstance());
        try {
            rsi.start();
        } catch (IOException ex) {
            Logger.getLogger(StorageTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    @After
    public void tearDown()
    {
    }

    @Test
    public void test1() 
    {
        exec(null);
    }
    
    public void exec(String cmd) 
    {
            try 
            {
                Runtime rt = Runtime.getRuntime();
                //Process pr = rt.exec("cmd /c dir");
                Process pr = rt.exec("sh test/pt/ua/dicoogle/scripts/storage.sh");
 
                BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
 
                String line=null;
 
                while((line=input.readLine()) != null) {
                    System.out.println(line);
                }
 
                int exitVal = pr.waitFor();
                System.out.println("Exited with error code "+exitVal);
 
            } catch(Exception e) {
                System.out.println(e.toString());
                e.printStackTrace();
            }
        }
}
