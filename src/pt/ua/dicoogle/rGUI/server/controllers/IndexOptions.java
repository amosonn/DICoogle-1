/*  Copyright   2010 Samuel Campos
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

package pt.ua.dicoogle.rGUI.server.controllers;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import pt.ua.dicoogle.DebugManager;
import pt.ua.dicoogle.core.TagsXML;
import pt.ua.dicoogle.core.dicom.PrivateDictionary;
import pt.ua.dicoogle.core.index.IndexEngine;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IIndexOptions;
import pt.ua.dicoogle.rGUI.server.DicoogleScan;
import pt.ua.dicoogle.sdk.Utils.TagValue;
import pt.ua.dicoogle.sdk.Utils.TagsStruct;

/**
 * Controller of Index Options Settings
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */

public class IndexOptions implements IIndexOptions {

    private Hashtable<Integer, TagValue> dimFields;
    private ArrayList<String> modalities;
    private Hashtable<Integer, TagValue> manualFields;
    private boolean isIndexAllModalities;

    private static Semaphore sem = new Semaphore(1, true);
    private static IndexOptions instance = null;

    private TagsStruct tags = TagsStruct.getInstance();

    public static synchronized IndexOptions getInstance()
    {
        try {
            sem.acquire();
            if (instance == null) {
                instance = new IndexOptions();
            }
            sem.release();
        } catch (InterruptedException ex) {
            Logger.getLogger(QRServers.class.getName()).log(Level.SEVERE, null, ex);
        }
        return instance;
    }
    
    private IndexOptions(){
        loadSettings(); 
    }

    /**
     * Load settings from TagsStruct
     */
    public void loadSettings(){
        dimFields = tags.getDimFields();

        modalities = tags.getModalities();

        manualFields = tags.getManualFields();

        isIndexAllModalities = tags.isIndexAllModalities();
    }

    /**
     * Save the Settings related to IndexOptions (TagsStruct)
     *
     * Print TagsXML
     */
    public void saveSettings(){
        tags.setDimFields(dimFields);
        tags.setModalities(modalities);
        tags.setManualFields(manualFields);
        tags.setIndexAllModalities(isIndexAllModalities);

        // Flush to XML file
        TagsXML ts = new TagsXML();
        ts.printXML();
    }


    /**
     *
     * @return  true - if there are unsaved settings ( != TagsStruct)
     *          false - not
     */
    public boolean unsavedSettings(){
        if(!dimFields.equals(tags.getDimFields()) || !modalities.equals(tags.getModalities()) 
                || !manualFields.equals(tags.getManualFields())
                || isIndexAllModalities != tags.isIndexAllModalities())
            return true;

        return false;
    }

    @Override
    public Hashtable<String, ArrayList<TagValue>> getDIMFields() throws RemoteException {
        DebugManager.getInstance().debug("Getting DIM Fields");
        
        Set<Integer> setKeys = dimFields.keySet();

        /** Get groups **/
        Hashtable<String, ArrayList<TagValue>> groupTable = new Hashtable<String, ArrayList<TagValue>>();
        for (int key : setKeys) {

            if (groupTable.containsKey(TagValue.getGroup(key))) {
                groupTable.get(TagValue.getGroup(key)).add(dimFields.get(key));

            } else {
                ArrayList<TagValue> temp = new ArrayList<TagValue>();
                temp.add(dimFields.get(key));
                groupTable.put(TagValue.getGroup(key), temp);
            }
        }

        return groupTable;
    }

    @Override
    public ArrayList<String> getModalities() throws RemoteException {
        return modalities;
    }

    @Override
    public boolean addModality(String modality) throws RemoteException {
        if(!modalities.contains(modality))
            return modalities.add(modality);
        else
            return false;
    }

    @Override
    public boolean removeModality(String modality) throws RemoteException {
        return modalities.remove(modality);
    }

    @Override
    public Hashtable<String, ArrayList<TagValue>> getManualFields() throws RemoteException {
        Set<Integer> setKeys = manualFields.keySet();

        /** Get groups **/
        Hashtable<String, ArrayList<TagValue>> groupTable = new Hashtable<String, ArrayList<TagValue>>();
        for (int key : setKeys) {
            DebugManager.getInstance().debug(">> Grouping the Others Tags in MainWindow Notebook");

            if (groupTable.containsKey(TagValue.getGroup(key))) {
                groupTable.get(String.valueOf(TagValue.getGroup(key))).add(manualFields.get(key));

            } else {
                ArrayList<TagValue> temp = new ArrayList<TagValue>();
                temp.add(manualFields.get(key));
                groupTable.put(TagValue.getGroup(key), temp);
            }
        }

        return groupTable;
    }

    @Override
    public boolean addManualField(int group, int subGroup, String name) throws RemoteException {
        String tag = String.valueOf(group) + String.valueOf(subGroup);
        TagValue t = new TagValue(Integer.parseInt(tag, 16), name);
        TagsStruct.getInstance().addOthers(t);

        if (!manualFields.containsKey(t.getTagNumber())){
            manualFields.put(t.getTagNumber(), t);
            
            return true;
        }
        return false;
    }

    @Override
    public boolean removeManualField(int group, int subGroup) throws RemoteException {
        String tag = String.valueOf(group) + String.valueOf(subGroup);

        return manualFields.remove(Integer.parseInt(tag, 16)) != null;
    }

    @Override
    public boolean isIndexAllModalities() throws RemoteException {
        return isIndexAllModalities;
    }

    @Override
    public void setIndexAllModalities(boolean value) throws RemoteException {
        isIndexAllModalities = value;
    }




    /**
     * Index new file or folder
     *
     * @param Path - file or folter path
     * @throws RemoteException
     */
    @Override
    public void index(String Path, boolean resume) throws RemoteException {
        Logs.getInstance().addServerLog("Start indexing: " + Path);
        
        DicoogleScan scandir = new DicoogleScan(Path);
        scandir.scan(resume);
    }

    /**
     * Indexes even if the file is already indexed
     *
     * @param path
     * @throws RemoteException
     */
    @Override
    public void reIndex(ArrayList<String> list) throws RemoteException {
        Logs.getInstance().addServerLog("Start reIndexing " + list.size() + " file(s)");
        
        class REIndex extends Thread{
            private ArrayList<String> list;

            public REIndex(ArrayList<String> list){
                this.list = list;
            }

            @Override
            public void run(){
                for(String Path: list)
                    IndexEngine.getInstance().reIndexQueue(Path);
            }
        }

        //reIndex all files
        (new REIndex(list)).start();
    }

    @Override
    public void removeFilesFromIndexer(ArrayList<String> files, boolean deleteFiles) throws RemoteException {
        Logs.getInstance().addServerLog("Start removing " + files.size() + " file(s)");

        class DeleteFiles extends Thread{
            private ArrayList<String> list;
            private boolean delete;

            public DeleteFiles(ArrayList<String> list, boolean delete){
                this.list = list;
                this.delete = delete;
            }

            @Override
            public void run(){
                for(String Path: list)
                    IndexEngine.getInstance().deleteFileQueue(Path, delete);
            }
        }

        //reIndex all files
        (new DeleteFiles(files, deleteFiles)).start();

    }

    @Override
    public boolean removeDictionary(String dic) throws RemoteException {
        
        TagsStruct.getInstance().removeDicionary(dic);
        
        return true;
        
    }

    @Override
    public boolean addDictionary(String dic) throws RemoteException {
        
        TagsStruct.getInstance().addDicionary(dic);
        PrivateDictionary pd = new PrivateDictionary();
        pd.parse(dic);
        return true;
        
    }

}
