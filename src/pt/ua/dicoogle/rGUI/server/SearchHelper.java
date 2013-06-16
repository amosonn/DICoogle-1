/*  Copyright   2010 Samuel da Costa Campos
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
package pt.ua.dicoogle.rGUI.server;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import pt.ua.dicoogle.core.index.IndexEngine;
import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.rGUI.client.signals.SearchSignal;
import pt.ua.dicoogle.rGUI.server.controllers.PluginController4user;
import pt.ua.dicoogle.rGUI.server.controllers.Search;
import pt.ua.dicoogle.sdk.Utils.SearchResult;
import pt.ua.dicoogle.sdk.Utils.TaskRequest;
import pt.ua.dicoogle.sdk.Utils.TaskRequestsConstants;
import pt.ua.dicoogle.sdk.observables.ListObservableSearch;

/**
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
public class SearchHelper implements Observer
{

    private ListObservableSearch<SearchResult> SearchResList = null;
    private IndexEngine indexer;
    //private PeerEngine peer;
    private boolean lastSearchWasLocal = true;
    private boolean export = false; // indicates whether the search is to export or do not
    private long time;
    private Search searchControler;

    public SearchHelper(Search search)
    {
        this.searchControler = search;

        indexer = IndexEngine.getInstance();

    }

    public void setList(ListObservableSearch<SearchResult> SearchResList)
    {
        this.SearchResList = SearchResList;
    }
    
    
    public ListObservableSearch<SearchResult> search(String query, ArrayList<String> extrafields,
            HashMap<String, Boolean> plugins, boolean export, Observer obs)
    {
    
        if (SearchResList != null)
        {
            this.SearchResList.resetArray();
        }

        this.export = export;
        this.time = System.nanoTime();


        Set<String> keys = plugins.keySet();

        if (this.SearchResList != null)
        {
            this.SearchResList.deleteObserver(obs);
        }
        
        ArrayList<String> pluginsLocals = new ArrayList<String>();
        ArrayList<String> pluginsOthers = new ArrayList<String>();
        
        for (String pi : plugins.keySet())
        {
            Boolean value = plugins.get(pi);
            if (value)
            {
                try {
                    if (PluginController4user.getInstance().isLocalPlugin(pi))
                    {
                        pluginsLocals.add(pi);
                    }
                    else
                    {
                        pluginsOthers.add(pi);
                    }
                } catch (RemoteException ex) {
                    Logger.getLogger(SearchHelper.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        }
        
        
        Thread seachThread = new SearchLocal(query, extrafields, pluginsLocals, obs);
        seachThread.start();
        //SearchResList = PluginController.getInstance().search(pluginsLocals, query, extrafields, obs);
        
        
        // Fucking ugly, but it works! 
        // I hate this Observer shit!
        
        
        while (SearchResList==null)
        {
            
        
            
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(SearchHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return SearchResList;
        
        
    }
    
    
    public void search(String query, ArrayList<String> extrafields,
            HashMap<String, Boolean> plugins, boolean export)
    {
        search(query, extrafields, plugins, export, this);
    
    }

    public SearchResult searchThumbnail(String FileName, String FileHash)
    {
        ArrayList<SearchResult> queryResultListLocal;

        ArrayList<String> extrafields = new ArrayList<String>();
        extrafields.add("Thumbnail");

        String query = "FileName:" + FileName + " AND FileHash:" + FileHash;
        // TODO: Implement it.
        return null;
    }

    public void searchP2PThumbnail(String FileName, String FileHash, String addr)
    {

        ArrayList<String> extrafields = new ArrayList<String>();
        extrafields.add("Thumbnail");

        String query = "FileName:" + FileName + " AND FileHash:" + FileHash;
        // TODO: Implement it.
        this.lastSearchWasLocal = false;
    }

    @Override
    public void update(Observable o, Object arg)
    {
        ArrayList tmp = ((ListObservableSearch) o).getArray();
        Boolean finished = ((ListObservableSearch) o).isFinish();
        
        ArrayList<SearchResult> resultsList;
        
        if (tmp.isEmpty())
        {
            return;
                  
        } else
        {
            
            ((ListObservableSearch) o).resetArray();
            
            if (SearchResult.class.isInstance(tmp.get(0)))
            {
                
                if (finished)
                {
                    searchControler.queryFinished();
                }
                
                    resultsList = tmp;

                    if (resultsList.size() == 1 && resultsList.get(0).getExtrafields().size() == 0)
                    {
                       return;
                    }

                    //if the result is just the requested Thumbnail
                    if (resultsList.size() == 1 && resultsList.get(0).getExtrafields().size() == 1
                            && resultsList.get(0).getExtrafields().get("Thumbnail") != null)
                    {
                        searchControler.setP2PThumbnails(resultsList);
                        
                    } else
                    {
                        ((ListObservableSearch) o).resetArray();
                        long timeEnd = System.nanoTime();

                        if (!export)
                        {
                            searchControler.setSearchTime(((int) ((timeEnd - time) / 1000000L)));
                            searchControler.setP2PSearchResult(resultsList);
                        } else
                        {
                            searchControler.setExportSearchResult(resultsList);
                        }
                        
                        PluginController.getInstance().addTask(new TaskRequest(TaskRequestsConstants.T_BLOCK_SIGNAL, null, null));
                    }
           
            }
            
            
            // Memory monitoring 
             long freeMemory = Runtime.getRuntime().freeMemory() / 1024 / 1024;
             long totalMemory = Runtime.getRuntime().totalMemory() / 1024 / 1024;
             long maxMemory = Runtime.getRuntime().maxMemory() / 1024 / 1024;
             
             double percentage = freeMemory / totalMemory;
             
             if (percentage>0.8)
             {
                 // Prune query!!! 
                 PluginController.getInstance().addTask(new TaskRequest(TaskRequestsConstants.T_QUERY_PRUNE, null, null));
             }
        }
        
    }

    /**
     * Private class that implements one Thread to search in IndexEngine
     */
    private class SearchLocal extends Thread
    {

        private String query;
        private ArrayList<String> extrafields;
        private ArrayList<String> pluginsLocals;
        private Observer searchHelper;

        public SearchLocal(String query, ArrayList<String> extrafields, ArrayList<String> pluginsLocals , Observer searchHelper)
        {
            this.query = query;
            this.extrafields = extrafields;
            this.pluginsLocals = pluginsLocals;
            this.searchHelper = searchHelper;
        }

        @Override
        public void run()
        {
            SearchResList = PluginController.getInstance().search(pluginsLocals, query, extrafields, searchHelper);
        }
    }
}
