/*  Copyright   2011 IEETA
 *
 *  This file is part of Dicoogle.
 *
 *  Author: Carlos Ferreira
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
package pt.ua.dicoogle.plugins;

import pt.ua.dicoogle.sdk.Utils.TaskQueue;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.xeoh.plugins.base.PluginManager;
import net.xeoh.plugins.base.impl.PluginManagerFactory;
import net.xeoh.plugins.base.util.PluginManagerUtil;
//import pt.ua.dicooglePluginSDK.PluginInterfaces.PluginInterface;
import pt.ua.dicoogle.sdk.GenericPluginInterface;
import pt.ua.dicoogle.sdk.GraphicPluginAdapter;
import pt.ua.dicoogle.sdk.NetworkPluginAdapter;
import pt.ua.dicoogle.sdk.observables.FileObservable;
import pt.ua.dicoogle.sdk.Utils.TaskRequest;
import pt.ua.dicoogle.sdk.Utils.SearchResult;
import pt.ua.dicoogle.sdk.Utils.TaskRequestsConstants;
import pt.ua.dicoogle.sdk.index.IDoc;
import pt.ua.dicoogle.sdk.index.IndexPluginInterface;
import pt.ua.dicoogle.sdk.observables.ListObservableSearch;

/**
 *
 * @author Carlos Ferreira
 */
public class PluginController implements Observer
{

    private Collection<GenericPluginInterface> plugins;
    private Collection<IndexPluginInterface> localPlugins;
    private static PluginController instance = null;
    private TaskQueue tasks = null;
    private ListObservableSearch<SearchResult> results = null;
    private List<ListObservableSearch<SearchResult>> observables = null;

    private PluginController()
    {
        localPlugins = new ArrayList();
        PluginManager pm = PluginManagerFactory.createPluginManager();
        pm.addPluginsFrom(new File("Plugins/").toURI());
        PluginManagerUtil pmu = new PluginManagerUtil(pm);        
        this.plugins = pmu.getPlugins(GenericPluginInterface.class);


        for (GenericPluginInterface pi : this.plugins)
        {
            
            File f = new File("plugins/settings/" + pi.getName() + ".settings");
            if (f.exists())
            {
                try
                {
                    FileInputStream bis = new FileInputStream(f);
                    ObjectInput in = new ObjectInputStream(bis);
                    ArrayList settings = (ArrayList) in.readObject();
                    pi.setSettings(settings);
                } catch (ClassNotFoundException ex)
                {
                    Logger.getLogger(PluginController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex)
                {
                    Logger.getLogger(PluginController.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
            if (pi.isLocalPlugin())
            {
                localPlugins.add((IndexPluginInterface) pi);
            }
        }
        tasks = new TaskQueue();
        tasks.addObserver(this);

        this.results = new ListObservableSearch<SearchResult>();
        this.observables = new ArrayList<ListObservableSearch<SearchResult>>();

    }

    public static synchronized PluginController getInstance()
    {

        if (instance == null)
        {
            instance = new PluginController();
        }
        return instance;

    }

    public void initializePlugin(String pluginName)
    {
        for (GenericPluginInterface pi : this.plugins)
        {
            if (pi.getName().compareTo(pluginName) == 0)
            {
                pi.initialize(tasks);
                return;
            }
        }
    }

    public void stopPlugin(String pluginName)
    {
        for (GenericPluginInterface pi : this.plugins)
        {
            if (pi.getName().compareTo(pluginName) == 0)
            {
                pi.Stop();
                return;
            }
        }
    }

    public void stopAll()
    {
        for (GenericPluginInterface pi : this.plugins)
        {
            pi.Stop();
        }
    }

    
    public void initGUI()
    {
        for (GenericPluginInterface pi : this.plugins)
        {
            if (pi instanceof GraphicPluginAdapter)
            {
                GraphicPluginAdapter piTmp = (GraphicPluginAdapter) pi;
                piTmp.initGUI();
            }
            
        }
        
    }
    
    
    public HashMap<String, ArrayList> getPanelInitialParams()
    {
        HashMap<String, ArrayList> panels = new HashMap<String, ArrayList>();
        for (GenericPluginInterface pi : this.plugins)
        {
            panels.put(pi.getName(), pi.getPanelInitilizationParams());
        }
        return panels;
    }

    public void setSettings(HashMap<String, ArrayList> panels)
    {
        for (GenericPluginInterface pi : this.plugins)
        {
            if (panels.containsKey(pi.getName()))
            {
                pi.setSettings(panels.get(pi.getName()));
            }
        }
    }

    public void setSettings(String pluginName, ArrayList settings)
    {
        for (GenericPluginInterface pi : this.plugins)
        {
            if (pi.getName().compareTo(pluginName) == 0)
            {
                pi.setSettings(settings);
                return;
            }
        }
    }

    public synchronized void index(IDoc doc)
    {

        this.results.resetArray();
        for (ListObservableSearch list : this.observables)
        {
            list.deleteObserver(this);
        }
        this.observables.clear();
        Collection<IndexPluginInterface> list = (Collection<IndexPluginInterface>) this.localPlugins;
        for (IndexPluginInterface pi : list)
        {
            pi.index(doc);
        }

    }
    public synchronized void optimize() {
    

        Collection<IndexPluginInterface> list = (Collection<IndexPluginInterface>) this.localPlugins;
        for (IndexPluginInterface pi : list)
        {
            pi.optimize();
        }
        
    }
    public void index(List<IDoc> docs) {
        
        this.results.resetArray();
        for (ListObservableSearch list : this.observables)
        {
            list.deleteObserver(this);
        }
        this.observables.clear();
        Collection<IndexPluginInterface> list = (Collection<IndexPluginInterface>) this.localPlugins;
        for (IndexPluginInterface pi : list)
        {
            pi.index(docs);
        }
    }
    
    public synchronized List<SearchResult> localSearchSync(String query, List<String> Extrafields)
    {
        List<SearchResult> resultLocal = new ArrayList<SearchResult>(); 
        for (IndexPluginInterface pi : this.localPlugins)
        {
            resultLocal.addAll(pi.searchSync(query, Extrafields));
        }
        return resultLocal;
    }
    
    public synchronized List<SearchResult> localSearch(String query, List<String> Extrafields)
    {

        
        this.results.resetArray();
        for (ListObservableSearch list : this.observables)
        {
            list.deleteObserver(this);
        }
        this.observables.clear();

        for (IndexPluginInterface pi : this.localPlugins)
        {
            this.results.addAll(pi.searchSync(query, Extrafields));
        }
        return results.getArray();
    }

    public ListObservableSearch<SearchResult> search(ArrayList<String> pluginName, String query, List<String> Extrafields, Observer obs)
    {
        this.results.deleteObservers();
        //System.out.println("Search now");
        this.results.resetArray();
        this.results.addObserver(obs);
        for (ListObservableSearch list : this.observables)
        {
            list.deleteObserver(this);
        }
        this.observables.clear();

        for (GenericPluginInterface pi : this.plugins)
        {
            if (pluginName.contains(pi.getName()))
            {
                if (pi.isLocalPlugin())
                {
                    //this.results.addAll(pi.search(query, Extrafields).getArray());
                    ListObservableSearch lo = pi.search(query, Extrafields);
                    lo.addObserver(this);
                    this.observables.add(lo);
                } else
                {
                    ListObservableSearch lo = pi.search(query, Extrafields);
                    lo.addObserver(this);
                    this.observables.add(lo);
                }
            }
        }
        return this.results;
    }

    public synchronized ListObservableSearch<SearchResult> searchOne(String pluginName, String query, List<String> Extrafields, String address, Observer obs)
    {
        this.results.deleteObservers();
        this.results.resetArray();
        this.results.addObserver(obs);
        for (ListObservableSearch list : this.observables)
        {
            list.deleteObserver(this);
        }

        this.observables.clear();
        for (GenericPluginInterface pi : this.plugins)
        {
            if (pi.getName().compareTo(pluginName) == 0)
            {
                if (pi.isLocalPlugin())
                {
                    this.results.addAll(pi.searchOne(query, Extrafields, address).getArray());
                } else
                {
                    //TODO: some synchronization mechanims to guarantee that all results are captured
                    ListObservableSearch lo = pi.search(query, Extrafields);
                    lo.addObserver(this);
                    this.observables.add(lo);
                }
            }
        }
        return this.results;
    }

    public FileObservable requestFile(String pluginName, String address, String fileName, String FileHash)
    {
        for (GenericPluginInterface pi : this.plugins)
        {
            if (pi.getName().compareTo(pluginName) == 0)
            {
                return pi.requestFile(address, fileName, FileHash);
            }
        }
        return null;
    }

    public void addTask(TaskRequest task)
    {
        System.out.println("Add Task");
        this.tasks.addTask(task);
        System.out.println("Add Task2");
    }

    public boolean isLocalPlugin(String pluginName)
    {
        for (GenericPluginInterface pi : this.plugins)
        {
            if (pi.getName().compareTo(pluginName) == 0)
            {
                return pi.isLocalPlugin();
            }
        }
        return false;
    }

    public List<NetworkMember> getMembers()
    {
        ArrayList<NetworkMember> members = new ArrayList<NetworkMember>();

        for (GenericPluginInterface pi : this.plugins)
        {
            if (NetworkPluginAdapter.class.isInstance(pi))
            {
                NetworkPluginAdapter npa = (NetworkPluginAdapter) pi;
                if (npa.getMembers()==null)
                {
                    continue;
                }
                ArrayList<String> mmbrs = npa.getMembers().getArray();
                for (String str : mmbrs)
                {
                    members.add(new NetworkMember(str, npa.getName()));
                }
            }
        }
        return members;
    }

    public List<String> getPluginsNames()
    {
        List<String> pluginNames = new ArrayList<String>();
        for (GenericPluginInterface pi : this.plugins)
        {
            if (pi.isLocalPlugin())
            {
                pluginNames.add(0, pi.getName());
            } else
            {
                pluginNames.add(pi.getName());
            }
        }
        return pluginNames;
    }

    public boolean isPluginRunning(String pluginName)
    {
        for (GenericPluginInterface pi : this.plugins)
        {
            if (pi.getName().compareTo(pluginName) == 0)
            {
                return pi.isRunning();
            }
        }
        return false;
    }

    public byte[] getJarFile(String PluginName)
    {
        for (GenericPluginInterface pi : this.plugins)
        {
            if (pi.getName().compareTo(PluginName) == 0)
            {
                
                {
                    FileInputStream fis = null;

                    try
                    {
                        File jarFile = new File(pi.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
                        byte[] b = new byte[(int) jarFile.length()];
                        fis = new FileInputStream(jarFile);
                        try
                        {
                            fis.read(b);
                        } catch (IOException ex)
                        {
                            Logger.getLogger(PluginController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        return b;
                    } catch (FileNotFoundException ex)
                    {
                        Logger.getLogger(PluginController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (URISyntaxException ex)
                    {
                        Logger.getLogger(PluginController.class.getName()).log(Level.SEVERE, null, ex);
                    } finally
                    {
                        try
                        {
                            fis.close();
                        } catch (IOException ex)
                        {
                            Logger.getLogger(PluginController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }
        return null;
    }

    
    
    
    
    public void saveSettings()
    {
        File folder = new File("plugins/settings");
        if (!folder.canRead())
        {
            folder.mkdir();
        }
        for (GenericPluginInterface p : this.plugins)
        {
            File f = new File("plugins/settings/" + p.getName() + ".settings");
            if (!f.exists())
            {
                try
                {
                    f.createNewFile();
                } catch (IOException ex)
                {
                    Logger.getLogger(PluginController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            ArrayList settings = p.getPanelInitilizationParams();
            try
            {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutput out = new ObjectOutputStream(bos);
                FileOutputStream fos = new FileOutputStream(f);
                out.writeObject(settings);
                byte[] settingsBytes = bos.toByteArray();

                fos.write(settingsBytes);

                out.close();
                bos.close();
                fos.close();
            } catch (IOException ex)
            {
                Logger.getLogger(PluginController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    
    class RunTask extends Thread 
    {
    
        private TaskRequest nextTask = null;
        private GenericPluginInterface pi = null;
        
        public RunTask(GenericPluginInterface pi, TaskRequest nextTask)
        {
            this.nextTask = nextTask;
            this.pi = pi;
        }
        
        public void run()
        {
            
            pi.attendTask(nextTask);
        
        }
    
    }
    
    @Override
    public void update(Observable o, Object arg)
    {
        System.out.println("@Update");
        
        if (TaskQueue.class.isInstance(o))
        {
            TaskQueue obs = (TaskQueue) o;

            for (TaskRequest nextTask = obs.getNextTask(); nextTask != null; nextTask = obs.getNextTask())
            {
                int taskType = nextTask.getTask();
                switch (taskType)
                {
                    case (TaskRequestsConstants.T_INDEX_FILE):
                    case (TaskRequestsConstants.T_LOCAL_DELETE_FILE):
                    case (TaskRequestsConstants.T_QUERY_LOCALLY):
                    case (TaskRequestsConstants.T_RESET_LOCAL_INDEX):
                        for (GenericPluginInterface pi : this.localPlugins)
                        {
                            RunTask run = new RunTask(pi, nextTask);
                            run.start();
                        }
                        break;
                    case (TaskRequestsConstants.T_BLOCK_SIGNAL):
                    case (TaskRequestsConstants.T_QUERY_PRUNE):  
                        for (GenericPluginInterface pi : this.localPlugins)
                        {
                            RunTask run = new RunTask(pi, nextTask);
                            run.start();
                        }
                        for (GenericPluginInterface pi : this.plugins)
                        {
                            RunTask run = new RunTask(pi, nextTask);
                            run.start();
                        }
                        break;
                    case (TaskRequestsConstants.T_LOGGER_MESSAGE_ALREADY_INDEXED):
                        break;
                }

            }

        }
        if (ListObservableSearch.class.isInstance(o))
        { 
            ListObservableSearch<SearchResult> lo = (ListObservableSearch) o;
            this.results.resetArray();
            
            //System.out.println("PluginController@update " + lo.isFinish());
            this.results.setFinish(lo.isFinish());
            this.results.addAll(lo.getArray());
        }
    }


}
