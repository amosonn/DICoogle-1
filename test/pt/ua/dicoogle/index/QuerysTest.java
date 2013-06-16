/*  Copyright   2012 - IEETA
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

package pt.ua.dicoogle.index;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.*;
import pt.ua.dicoogle.LoadMainClasses;
import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.sdk.observables.ListObservableSearch;
import static  org.junit.Assert.assertTrue;
import pt.ua.dicoogle.core.index.IndexEngine;
import pt.ua.dicoogle.sdk.Utils.SearchResult;
/**
 *
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public class QuerysTest
{

    private LoadMainClasses load = null;
    private Object monitor = null;
    public QuerysTest()
    {
        
    }

    @BeforeClass
    public static void setUpClass() throws Exception
    {
    }

    @AfterClass
    public static void tearDownClass() throws Exception
    {
    }

    @Before
    public void setUp()
    {
        //load = new LoadMainClasses();
        //load.loadClasses();
        monitor = new Object();
        
    }

    @After
    public void tearDown()
    {
        //load.shutdown();
    }

    @Test
    public void queryOne()
    {
        //PluginController.getInstance().search(null, null, null, null);     
    }
    
    @Test
    public void queryNumeric()
    {
        String str = "Sensitivity:Numeric:[20.0 TO 300.0]";
        //PluginController.getInstance().search(null, null, null, null);
    }
    

    @Test
    public void queryPatientName()
    {

        String str = "Sensitivity:Numeric:[20.0 TO 300.0]";
        
        //PluginController.getInstance().search(null, null, null, null);
     
    }
    
    class Results implements Observer
    {

        @Override
        public synchronized void update(Observable o, Object o1) 
        {
            
            ListObservableSearch osearch = ((ListObservableSearch) o);
            List results = osearch.getArray();
            if (results == null)
            {
                System.out.println("Results is null");
            }
            else
            {
                System.out.println("Results size" + results.size());
            }
            System.out.println("Received results. " + osearch.isFinish());
            //if (osearch!=null)
            //{
            //    osearch.resetArray();
            //}
            synchronized(monitor)
            {
                monitor.notifyAll();
            }
        }

    }
    
    
    
    @Test
    public void bigQuery()
    {
        
        // Load big index 
        
        String str = "*:*";
        str = "PatientName:2011*";
        List<String> extrafields = new ArrayList<String>();

        extrafields.add("PatientName");
        extrafields.add("PatientID");
        extrafields.add("Modality");
        extrafields.add("StudyDate");
        extrafields.add("SOPInstanceUID");
        Results obs = new Results();
        ArrayList<String> plugins = new ArrayList<String>();
        plugins.add("Local");
        System.out.println("Searching.. before ");
        ListObservableSearch r = PluginController.getInstance().search(plugins, str, extrafields, obs);
        System.out.println("Searching.. ");
        synchronized(monitor)
        {
            while (!r.isFinish())
            {
                    try
                    {
                        System.out.println("Waiting.. ");
                        // Wait for the results 
                        monitor.wait();
                    } 
                    catch (InterruptedException ex) {
                        Logger.getLogger(QuerysTest.class.getName()).log(Level.SEVERE, null, ex);
                    }
             }
        }
        System.out.println("Searching Done?.. "+r.isFinish());
        assertTrue("The results was received with sucess", r.isFinish());
    }        
    
    
    @Test
    public void testSyncQuery()
    {
    
        LoadMainClasses load = new LoadMainClasses();
        load.loadClasses();
        IndexEngine.getInstance();
        List<SearchResult> result = IndexEngine.getInstance().searchSync("PatientName:2011*", null);
        System.out.println("Test results: " + result.size());
        String query = "PatientName:2011*";
        
    }
   
}