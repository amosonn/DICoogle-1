package pt.ua.dicoogle.index;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.*;
import pt.ua.dicoogle.core.ExportDataSupport;
import pt.ua.dicoogle.rGUI.interfaces.controllers.ISearch;
import static org.junit.Assert.*;
import pt.ua.dicoogle.LoadMainClasses;
import pt.ua.dicoogle.core.index.IndexEngine;
import pt.ua.dicoogle.rGUI.client.windows.ExportData;
import pt.ua.dicoogle.sdk.Utils.SearchResult;
/**
 *
 * @author bastiao
 */
public class ExportTest {
 
    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
        
        

    }
    
    @After
    public void tearDown() {
    }
    
    
    
    @Test
    public void indexOneFile() 
    {
    
    }
    
    @Test
    public void indexFolder() 
    {
    
    }
    
    public class ExportDataTest implements Observer 
    {
        private ISearch search;
        
        public ExportDataTest()
        {
            
        }
 
        
        @Override
        public void update(Observable o, Object o1) 
        {
            System.out.println("Test: grouping results");
            if (o==null)
            {
                Logger.getLogger(ExportDataTest.class.getName()).log(Level.SEVERE, "Update, but it is null");
                return;
            }
            Logger.getLogger(ExportData.class.getName()).log(Level.SEVERE, "Update");
            Boolean finish = (Boolean) o1;
            System.out.println("Test: grouping results, boolean" + finish);
        }
            
    }
    
    @Test
    public void export()
    {
        LoadMainClasses load = new LoadMainClasses();
        load.loadClasses();
        IndexEngine.getInstance();
        List<SearchResult> result = IndexEngine.getInstance().searchSync("PatientName:2011*", null);
        System.out.println("Test results: " + result.size());
        //String query = "PatientName:2011*";
        String query = "*:*";
        
        HashMap<String, Boolean> plugins = new HashMap<String, Boolean>();
        
        plugins.put("Local", true);
        ArrayList<String> tags = new ArrayList<String>();
        tags.add("PatientName");
        tags.add("Modality");
        tags.add("SOPInstanceUID");
        tags.add("StudyDate");
        tags.add("PatientSex");
        ExportDataSupport e = null;
        try {
            e = new ExportDataSupport(query, plugins, true, tags, "out.txt");
        } catch (Exception ex) 
        {
            Logger.getLogger(ExportTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if (e==null)
        {
            fail("Cannot create Export Data Support");
        }
        
        ExportDataTest testObs = new ExportDataTest();
        try 
        {
            e.InitiateExport(testObs);
        }
        catch (Exception ex) 
        {
            Logger.getLogger(ExportTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) 
        {
            Logger.getLogger(ExportTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        StringBuilder text = new StringBuilder();
        String NL = System.getProperty("line.separator");
        Scanner scanner = null;
        try {
            scanner = new Scanner(new FileInputStream("out.txt.csv"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ExportTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        int i = 0 ;
        try {
            while (scanner.hasNextLine()) {
                i++;
                text.append(scanner.nextLine() + NL);
            }
        } finally 
        {
            scanner.close();
        }
        
        File f = new File("out.txt.csv");
        System.out.println("I: " + i);
        assertEquals(i>4, true);
        //f.delete();   
    }
}
