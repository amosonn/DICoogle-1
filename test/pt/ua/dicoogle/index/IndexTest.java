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

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.junit.*;
//import pt.ua.dicoogle.Dataset;
import pt.ua.dicoogle.DebugManager;
import pt.ua.dicoogle.LoadMainClasses;
import pt.ua.dicoogle.core.ServerSettings;
import pt.ua.dicoogle.core.index.FileIndexer;
import pt.ua.dicoogle.core.index.IndexEngine;
import pt.ua.dicoogle.sdk.Utils.SearchResult;
import pt.ua.dicoogle.sdk.index.handlers.FileHandlerException;

/**
 *
 * @author bastiao
 */
public class IndexTest 
{
    
    private LoadMainClasses load ;
    public IndexTest() 
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
        
        load = new LoadMainClasses();
        ServerSettings.getInstance().setIndexerEffort(100);
        
        load.loadPlugins();
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void indexOneFile() 
    {
    
    }
/*    
    @Test
    public void indexFolder() throws IOException, FileHandlerException 
    {                
        FileIndexer fileIndexer = new FileIndexer();
        DebugManager.getInstance().setDebug(true);
        fileIndexer.index(new File(Dataset.getInstance().getDataSetPath()), true);
        
        // Do a search to check the results 
        List<SearchResult> results = IndexEngine.getInstance().searchSync("*:*", null);
        System.out.println(results.size());
        
    }*/
    
}
