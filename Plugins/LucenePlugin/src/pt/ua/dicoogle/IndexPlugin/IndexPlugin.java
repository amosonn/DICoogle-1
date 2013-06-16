/*  Copyright 2011 Carlos Ferreira
 *
 *  This file is part of Dicoogle.
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
package pt.ua.dicoogle.IndexPlugin;


import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import pt.ua.dicoogle.core.index.lucene.IndexCore;
import pt.ua.dicoogle.core.index.lucene.SearchThread;
import pt.ua.dicoogle.core.index.lucene.Settings;
import pt.ua.dicoogle.sdk.Utils.SearchResult;
import pt.ua.dicoogle.sdk.Utils.TaskQueue;
import pt.ua.dicoogle.sdk.Utils.TaskRequest;
import pt.ua.dicoogle.sdk.Utils.TaskRequestsConstants;
import pt.ua.dicoogle.sdk.index.IDoc;
import pt.ua.dicoogle.sdk.index.IndexPluginInterface;
import pt.ua.dicoogle.sdk.observables.FileObservable;
import pt.ua.dicoogle.sdk.observables.ListObservableSearch;


/**
 *
 * @author Carlos Ferreira
 * @author Luís Bastião
 */
@PluginImplementation
public class IndexPlugin implements IndexPluginInterface
{

    private IndexCore index;
    ListObservableSearch<SearchResult> lo = null;
    private int id = 0;
    
    public IndexPlugin()
    {
        /* Verify settings of indexer */
        File f = new File("index/indexed/write.lock");
        if (f.exists())
        {
            f.delete();
        }
        index = IndexCore.getInstance();
    }

    public synchronized void attendTask(TaskRequest tr)
    {
        Logger.getLogger(IndexPlugin.class.getName()).log(Level.SEVERE,  "########## attendTASK ##########");
        
        int taskType = tr.getTask();
        Logger.getLogger(IndexPlugin.class.getName()).log(Level.SEVERE,  "########## Task: ##########" +taskType );
        switch (taskType)
        {
            case (TaskRequestsConstants.T_INDEX_FILE):
                
                if (tr.getParameters().get(TaskRequestsConstants.P_FILE_PATH) == null)
                {
                    return;
                }
                //this.index((String) tr.getParameters().get(TaskRequestsConstants.P_FILE_PATH));
                break;
            case (TaskRequestsConstants.T_QUERY_LOCALLY):
                if ((tr.getParameters().get(TaskRequestsConstants.P_QUERY) == null) || (tr.getParameters().get(TaskRequestsConstants.P_EXTRAFIELDS)==null))
                {
                    return;
                }

                List<SearchResult> resuts = index.search((String) tr.getParameters().get(TaskRequestsConstants.P_QUERY),  (List<String>) tr.getParameters().get(TaskRequestsConstants.P_EXTRAFIELDS), IndexCore.NO_BLOCKS, null);
                HashMap<Integer,Object> results = new HashMap<Integer, Object>();
                results.put(TaskRequestsConstants.R_SEARCH_RESULTS, resuts);
                tr.setResults(results);
                tr.completeTask();
                break;
            case (TaskRequestsConstants.T_RESET_LOCAL_INDEX):
                if (tr.getParameters().get(TaskRequestsConstants.P_FILE_PATH) == null)
                {
                    return;
                }
                index.resetIndex((String) tr.getParameters().get(TaskRequestsConstants.P_FILE_PATH));
                break;
                
            case (TaskRequestsConstants.T_QUERY_PRUNE):
                index.setPrune(true);
                System.out.println("Where Deadlock will happen");
                index.notifySearchingBlock();
                System.out.println("Where Deadlock will happen2");
                break;
            case (TaskRequestsConstants.T_BLOCK_SIGNAL):
                index.notifySearchingBlock();
                
                break;
        }
        
        
        //index.attendTask(tr);
    }

    public String getName()
    {
        return index.getName();
    }

    public ListObservableSearch<SearchResult> search(String string, List<String> al)
    {
        index.setPrune(false);
        lo = new ListObservableSearch<SearchResult>();
        synchronized(this)
        {
            String idStr = String.valueOf(id);
            lo.setQueryId(new String(idStr));
            id++;
        }
        SearchThread st = new SearchThread(string, lo, index, al);
        st.start();
        
        return lo;
    }

    public ListObservableSearch<SearchResult> searchOne(String string, List<String> al, String string1)
    {
        index.setPrune(false);
                
        lo = new ListObservableSearch<SearchResult>();

        SearchThread st = new SearchThread(string, lo, index, al);
        st.start();
        try {
            st.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(IndexPlugin.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return lo;
        
    }

    public FileObservable requestFile(String string, String string1, String string2)
    {
        return index.requestFile(string, string1);
    }

    public boolean isLocalPlugin()
    {
        return index.isLocalPlugin();
    }

    public void setDefaultSettings()
    {
        index.setDefaultSettings();
    }

    public void Stop()
    {
        return;
    }

    /**
     * this plugin does not need (for now) to request tasks to other plugins
     */
    /* public void Initialize(Collection<TaskRequest> clctn)
    {

    return;
    }*/

    public boolean isRunning()
    {
        /**
         * running or not running...makes no difference in this plugin
         */
        return true;
    }

    public void setSettings(ArrayList<Object> al)
    {
        Settings SInstance = Settings.getInstance();
        SInstance.setIndexerEffort((Integer) al.get(0));
        SInstance.setThumbnailsMatrix((String) al.get(1));
        SInstance.setSaveThumbnails((Boolean) al.get(2));
        SInstance.setIndexZIPFiles((Boolean) al.get(3));
    }

    public ArrayList<Object> getPanelInitilizationParams()
    {
        ArrayList<Object> params = new ArrayList<Object>();
        Settings SInstance = Settings.getInstance();
        params.add(SInstance.getIndexerEffort());
        params.add(SInstance.getThumbnailsMatrix());
        params.add(SInstance.isSaveThumbnails());
        params.add(SInstance.isIndexZIPFiles());
        return params;
    }

    /**
     * this plugin does not need (for now) to request tasks to other plugins
     */
    public void initialize(TaskQueue tq)
    {
        return;
    }

    public void index(IDoc doc)
    {
        index.index(doc);
    }

    public void index(List<IDoc> list) {
        index.index(list);
    }

    public void optimize() {
        index.optimize();
    }

    public void update(Observable o, Object o1) {
        
    }
    
    public List<SearchResult> searchSync(String string, List<String> list) {
        return index.searchingSync(string, list);
    }


}
