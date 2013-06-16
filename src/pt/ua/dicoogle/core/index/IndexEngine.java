/*  Copyright  2010 - IEETA
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
package pt.ua.dicoogle.core.index;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import pt.ua.dicoogle.DebugManager;
import pt.ua.dicoogle.common.index.DeleteFileTask;
import pt.ua.dicoogle.common.index.IndexTask;
import pt.ua.dicoogle.common.index.ReIndexTask;
import pt.ua.dicoogle.common.index.ResetIndexTask;
import pt.ua.dicoogle.common.index.Task;
import pt.ua.dicoogle.core.ServerSettings;

import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.rGUI.server.controllers.Logs;
import pt.ua.dicoogle.rGUI.server.controllers.TaskList;
import pt.ua.dicoogle.sdk.Utils.SearchResult;
import pt.ua.dicoogle.sdk.Utils.TaskRequest;
import pt.ua.dicoogle.sdk.Utils.TaskRequestsConstants;
import pt.ua.dicoogle.sdk.index.IDoc;
import pt.ua.dicoogle.sdk.index.handlers.FileHandlerException;


/**
 * It has the responsability of choose the index backend, and
 * create an abstract layer to the implemented methods of Indexer.
 *
 *
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public class IndexEngine /*extends Observable*/ implements IndexAPI, IndexCoreAPI
{

    /*********************
     * Private Attributes
     *********************/
    private PluginController PController = null ;

    private LinkedBlockingQueue<Task> taskList = new LinkedBlockingQueue<Task>();

    private boolean indexing = false;
    private String currentPath;
    private int currentFiles=0;
    
    private HashSet<String> extensionsAllowed = new HashSet();
    private int totalFilesInQueue = 0 ;
    private int filesIndexed = 0;
    private final Object monitorFilesIndexed = new Object();
    private boolean stillCounting = false;
    private static ServerSettings s = ServerSettings.getInstance();

    
    private Thread countFiles = null;
    
    private static IndexEngine instance = null ;

    public synchronized static IndexEngine getInstance()
    {
        IndexEngine inst = null ;
        if (instance==null)
        {
            instance = new IndexEngine(s.getExtensionsAllowed());
            inst = instance ;
        }
        else
        {
            inst = instance  ;
        }
        return inst ;
    }


    /*********************
     * Constructors
     *********************/
     private IndexEngine()
     {
         this.PController = PluginController.getInstance();
     }

     private IndexEngine(HashSet<String> extensionsAllowed)
     {
        this();
        this.extensionsAllowed = extensionsAllowed;
     }




     /*********************
     * Public Methods
     *********************/

   /* @Override
    public void update(Observable o, Object arg)
    {
        int files = (Integer)arg;
        synchronized(monitorFilesIndexed)
        {
            filesIndexed += files;
        }
        try
        {
            TaskList.getInstance().updatedTasks();
        } catch (RemoteException ex)
        {
            Logger.getLogger(IndexEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }*/

    /**
     * @return the filesIndexed
     */
    public int getFilesIndexed() {
        return filesIndexed;
    }

    /**
     * @return the totalFilesInQueue
     */
    public int getTotalFilesInQueue() {
        return totalFilesInQueue;
    }

    /**
     * @param totalFilesInQueue the totalFilesInQueue to set
     */
    public void setTotalFilesInQueue(int totalFilesInQueue) {
        this.totalFilesInQueue = totalFilesInQueue;
    }

    @Override
    public int percentCompleted()
    {
        
        float percent = 0 ;
        synchronized(monitorFilesIndexed)
        {
             percent = ((float)filesIndexed/(float)currentFiles)*100;
             if (indexing && percent==100)
             {
                 percent = 99;
             }
        }
        
        return (int)percent;
    }

        /**
     * Count number of files to be indexed
     * @param path Directory that will be indexed
     * @return number of DICOM files
     */
    private long  preparingIndexing2(String path)
    {
        

        /**
         * Go deep and check how many DICOM files it have
         */
        //int count = 0 ;
        File root = new File (path);
        
        long nFiles = 0;
        if (root == null)
        {
            
            return 0;
        }
        File [] list = root.listFiles();
        if (list == null)
        {
            
            return 0;
        }
        for (File i : list)
        {
            
            if (i.isDirectory())
            {
                //Thread.yield();
                 nFiles = nFiles +preparingIndexing2(i.getAbsolutePath());
                
            }
            else
            {
                //synchronized(monitorFilesIndexed)
                {
                    //System.out.println("devel_mode: current files" + currentFiles );
                    nFiles++;

                }
            }
        }
        return nFiles;
  
    }
    
    final int BIG_DIR = 1000; // What is a big directory?
    final int STATC = 20; // Number of samples
    final int BIG_COUNT = 5000; // What is a small count?
    private int estimateNumberOfFiles(String path)
    {

        File root = new File (path);
        int estimatedSize = 0;
        
        File [] list = root.listFiles();
        
        if (list.length> BIG_DIR)
        {
            Random r = new Random();          // Default seed comes from system time.
            
            // Let's go deep for each 100 and see what's going on
            
            
            int [] values = new int[STATC];
            for (int i = 0 ; i<STATC; i++)
            {
                
                int rand =  r.nextInt(list.length) + 1;
                
                if (list[rand].isDirectory())
                {
                    // Call the estimation for the subdirectories
                    values[i] = estimateNumberOfFiles(list[rand].getAbsolutePath());
                }
                else
                {
                    values[i] = 1;
                }
            }
            
            // Compute the average number of files
            int sum = 0;
            for(int i=0; i < STATC; i++){
                sum += values[i] ;
            }
            sum = sum / STATC;
            currentFiles = currentFiles + sum*list.length;
            estimatedSize = estimatedSize  + sum*list.length;
            
        }
        else
        {
            
            for (File i : list)
            {
                if (i.isDirectory())
                {
                    estimatedSize = estimatedSize +estimateNumberOfFiles(i.getAbsolutePath());
                    currentFiles = currentFiles + estimateNumberOfFiles(i.getAbsolutePath());
                }
                else{
                    estimatedSize++;
                    currentFiles++;
                }
                
            }
        }
        
        
        return estimatedSize;
        
        
    
    }
    
    
    @Override
    public void deleteFile(String path, boolean removeFile) {
        HashMap<Integer, Object> parameters = new HashMap<Integer, Object>();
        parameters.put(TaskRequestsConstants.P_FILE_PATH, path);
        parameters.put(TaskRequestsConstants.P_TO_REMOVE_FILE, removeFile);
        PluginController.getInstance().addTask(new TaskRequest(TaskRequestsConstants.T_LOCAL_DELETE_FILE, null, parameters));
        //this.index.deleteFile(path, removeFile);
    }

    
    
    @Override
    public void index()
    {

        Task task = null;
        synchronized(this)
        {
            DebugManager.getInstance().log("Peeking task");
            task = getTaskList().peek();
        }
        if (task==null)
        {

            DebugManager.getInstance().log("No tasks found!");
            synchronized(monitorFilesIndexed)
            {
                filesIndexed = 0;
                setTotalFilesInQueue(0) ;
            }
            return;
        }
        DebugManager.getInstance().log("Creating thread for task");
        IndexThread thread = new IndexThread();
        if (task instanceof IndexTask || task instanceof ResetIndexTask)
        {
            File fTmp = new File(task.getPath());

            int nFiles = 0;
            if (fTmp.isDirectory())
            {
                class PrepareIndex extends Thread
                {
                    
                    private String path = "";
                    
                    public void setPath(String path)
                    {
                        this.path = path;
                    }
                    
                    public void run()
                    {
                        preparingIndexingStatus(true);
                        long start = System.nanoTime();    
                        //preparingIndexing(path);
                        int _currentFiles = (int) estimateNumberOfFiles(path);
                        if (currentFiles < BIG_COUNT)
                        {
                            currentFiles = 0;
                            preparingIndexing(path);
                        }
                        
                        long tm = System.nanoTime()-start;
                        //System.out.println("Estimated: " +currentFiles );
                        //System.out.println("Counting files time: " + tm/1000000L);
                        preparingIndexingStatus(false);
                    }
                    
                    
                }
                PrepareIndex p = new PrepareIndex();
                p.setPath(task.getPath());
                //p.setPriority((Thread.NORM_PRIORITY - Thread.MIN_PRIORITY) / 2);
                this.countFiles = p;
                p.start();
                
                try 
                {
                    TaskList.getInstance().updatedTasks();
                }
                catch (RemoteException ex) 
                {
                    Logger.getLogger(IndexEngine.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else
                nFiles = 1;
            //synchronized(monitorFilesIndexed)
            //{
                //currentFiles = nFiles;
                //setTotalFilesInQueue(getTotalFilesInQueue() + nFiles);
                //System.out.println("Current files " + currentFiles);
            //}
        }
        else if(task instanceof ReIndexTask){
            synchronized(monitorFilesIndexed)
            {
                currentFiles = 1;
                setTotalFilesInQueue(getTotalFilesInQueue() + 1);
            }
        }
        DebugManager.getInstance().log("Starting thread");
        //thread.setPriority((Thread.MAX_PRIORITY - Thread.NORM_PRIORITY) / 2);
        thread.start();
        
    }


    @Override
    public void indexQueue(String path, boolean resume)
    {
        //System.out.println("Putting in QUEUE" + path);
        IndexTask indexTask = new IndexTask(path, resume);
        synchronized(IndexEngine.getInstance())
        {
            getTaskList().add(indexTask);
        }
        //System.out.println("Queue: " + getTaskList());
        try
        {
            TaskList.getInstance().updatedTasks();
        }
        catch (RemoteException ex)
        {
            Logger.getLogger(IndexEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        index();
    }

    @Override
    public void reIndexQueue(String path)
    {
        ReIndexTask reIndexTask = new ReIndexTask(path);
        synchronized(IndexEngine.getInstance())
        {
            getTaskList().add(reIndexTask);
        }
        try
        {
            TaskList.getInstance().updatedTasks();
        }
        catch (RemoteException ex)
        {
            Logger.getLogger(IndexEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        index();
    }



    @Override
    public void deleteFileQueue(String path, boolean removeFile)
    {
        //System.out.println("Putting in QUEUE Delete Files" + path);
        DeleteFileTask indexTask = new DeleteFileTask(path, removeFile);
        synchronized(IndexEngine.getInstance())
        {
            getTaskList().add(indexTask);
        }
        //System.out.println("Queue: " + getTaskList());
        try
        {
            TaskList.getInstance().updatedTasks();
        }
        catch (RemoteException ex)
        {
            Logger.getLogger(IndexEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        index();
    }

    @Override
    public void resetIndexQueue(String path)
    {


        ResetIndexTask indexTask = new ResetIndexTask(path);
        synchronized(IndexEngine.getInstance())
        {
            getTaskList().add(indexTask);
        }
        
        try
        {
            TaskList.getInstance().updatedTasks();
        }
        catch (RemoteException ex)
        {
            Logger.getLogger(IndexEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        index();
    }

    @Override
    public List search(String queryString, List<String> extrafields)
    {
        return this.PController.localSearch(queryString, extrafields);
    }
    
    

    public List<SearchResult> searchSync(String queryString, List<String> extrafields)
    {
        return this.PController.localSearchSync(queryString, extrafields);
    }
    
    

     /**
     * Verify if an extension will be indexed
     *
     * @param ext              Extension to verify if is in the list
     * @return indexExtension  Returns true if the extension will be indexed,
     * otherwise returns false
     */
    protected boolean isValidExtension(String ext)
    {
        return this.extensionsAllowed.contains(ext);
    }

    /**
     * @return the indexing
     */
    public boolean isIndexing() {
        return indexing;
    }

    /**
     * @param indexing the indexing to set
     */
    public void setIndexing(boolean indexing) {
        this.indexing = indexing;
    }

    @Override
    public void index(String path, boolean resume) {
        //HashMap<Integer, Object> parameters = new HashMap<Integer,Object>();
        //parameters.put(TaskRequestsConstants.P_FILE_PATH, path);
        //this.PController.addTask(new TaskRequest(TaskRequestsConstants.T_INDEX_FILE, null, parameters));
        DebugManager.getInstance().log("Starting looking in directory to index");
        long start = System.nanoTime();
        FileIndexer indexer = null;
        try {
            indexer = new FileIndexer(countFiles);
        } catch (IOException ex) {
            Logger.getLogger(IndexEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            indexer.index(new File(path), resume);
            indexer.pushMissingFiles();
            this.PController.optimize();
            
        } catch (FileHandlerException ex) {
            Logger.getLogger(IndexEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        long tm = System.nanoTime()-start;
        //System.out.println("FILE INDEX: " + tm/1000000L);
        
    }
    
    public void indexFile(IDoc doc)
    {
        
    }
    
    public void indexFiles(List<IDoc> docs)
    {
        this.PController.index(docs);
    }

    /**
     * @return the currentPath
     */
    public String getCurrentPath() {
        return currentPath;
    }

    /**
     * @param currentPath the currentPath to set
     */
    public void setCurrentPath(String currentPath) {
        this.currentPath = currentPath;
    }

    /**
     * @return the taskList
     */
    public LinkedBlockingQueue<Task> getTaskList() {
        return taskList;
    }

    /**
     * @param taskList the taskList to set
     */
    public void setTaskList(LinkedBlockingQueue<Task> taskList) {
        this.taskList = taskList;
    }





    /*********************
     * Private Methods
     *********************/

    private void processTask()
    {
        // Get task from Queue
        Task task = this.getTaskList().poll();
    }
    
    private void preparingIndexingStatus(boolean status)
    {
        this.stillCounting = status;
    }

        /**
     * Count number of files to be indexed
     * @param path Directory that will be indexed
     * @return number of DICOM files
     */
    private void  preparingIndexing(String path)
    {
        

        /**
         * Go deep and check how many DICOM files it have
         */
        //int count = 0 ;
        File root = new File (path);

        // This filter only returns directories
        FileFilter fileFilter = new FileFilter()
        {
                public boolean accept(File file)
                {
                    return file.isDirectory();
                }
        };
        
        if (root == null)
        {
            DebugManager.getInstance().log("Error preparingIndexing: root == null, path" + path);
            return;
        }
        File [] list = root.listFiles();
        if (list == null)
        {
            DebugManager.getInstance().log("Error preparingIndexing: root.listFiles() == null, path" + path);
            return;
        }
        for (File i : list)
        {
            
            if (i.isDirectory())
            {
                //Thread.yield();
                preparingIndexing(i.getAbsolutePath());
                
            }
            else
            {
                //synchronized(monitorFilesIndexed)
                {
                    //System.out.println("devel_mode: current files" + currentFiles );
                    currentFiles++;

                }
            }
        }
  
        
    }
    
    public void incrementFilesIndex()
    {
        filesIndexed++;
    }

    
    
    @Override
    public void resetIndex(String path)
    {
        HashMap<Integer, Object> parameters = new HashMap<Integer, Object>();
        parameters.put(TaskRequestsConstants.P_FILE_PATH, path);
        PluginController.getInstance().addTask(new TaskRequest(TaskRequestsConstants.T_RESET_LOCAL_INDEX, null, parameters));
    }

    /*********************
     * Private Classes
     *********************/
    class IndexThread extends Thread
    {
        private String path = null ;

        public IndexThread()
        {
        }

        @Override
        public void run()
        {
            long time = System.nanoTime();
            DebugManager.getInstance().log("Starting Indexing\n");


            Task task = null;
            synchronized(IndexEngine.getInstance())
            {
                DebugManager.getInstance().log("Entering in sync\nIndexing:" + indexing);
                while(indexing)
                {
                    try
                    {
                        DebugManager.getInstance().log("Blocked in indexing, waiting for notify");
                        IndexEngine.getInstance().wait();
                    }
                    catch (InterruptedException ex)
                    {
                        Logger.getLogger(IndexEngine.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                DebugManager.getInstance().debug("Polling task");
                task = getTaskList().poll();
                path = task.getPath();
                currentPath = path;
                indexing = true;
            }
            try
            {
                DebugManager.getInstance().debug("Update tasks, so... ");
                TaskList.getInstance().updatedTasks();
            }
            catch (RemoteException ex)
            {
                Logger.getLogger(IndexEngine.class.getName()).log(Level.SEVERE, null, ex);
            }
            task.run();

            DebugManager.getInstance().log("\n\t***Directory Index Time (miliseg)***"
             + "\n\tStart Time: " + time + "\n\tEnd Time:" + System.nanoTime()
             + "\n\tDelta Time: " + ((System.nanoTime() - time)/1000000L)+"\n");

            synchronized(IndexEngine.getInstance())
            {
                task = getTaskList().peek();
            }
            if (task==null)
            {
                synchronized(monitorFilesIndexed)
                {
                    filesIndexed = 0;
                    setTotalFilesInQueue(0) ;

                    Logs.getInstance().addServerLog("Index tasks completed");
                }
            }
            synchronized(IndexEngine.getInstance())
            {
                indexing = false;
                IndexEngine.getInstance().notifyAll();

            }
            try {
                if (task instanceof IndexTask)
                {

                }
                TaskList.getInstance().updatedTasks();

            } catch (RemoteException ex) {
                Logger.getLogger(IndexEngine.class.getName()).log(Level.SEVERE, null, ex);
            }



        }
    }


    class OnlyDICOM implements FilenameFilter
    {

        ArrayList<String> extensions = new ArrayList<String>() ;
        public OnlyDICOM()
        {
            this.extensions.add("." + "dcm");
            this.extensions.add("." + "mcd");

        }
        
        @Override
        public boolean accept(File dir, String name)
        {
            boolean accept = false ;
            for (String ext : extensions)
            {
                if (name.endsWith(ext))
                {
                    accept = true ;
                    break ;
                }
            }
            return accept ;
        }
    }

}
