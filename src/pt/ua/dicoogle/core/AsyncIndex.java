/*  Copyright   2011 - IEETA
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
package pt.ua.dicoogle.core;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.contentobjects.jnotify.JNotify;
import net.contentobjects.jnotify.JNotifyException;
import net.contentobjects.jnotify.JNotifyListener;
import pt.ua.dicoogle.core.index.IndexEngine;

/**
 *
 * Luís A. Bastião Silva <bastiao@ua.pt>
 */
public class AsyncIndex {

    public AsyncIndex() {

        // path to watch
        String path = ServerSettings.getInstance().getDicoogleDir();

        // watch mask, specify events you care about,
        // or JNotify.FILE_ANY for all events.
        int mask = JNotify.FILE_CREATED
                | JNotify.FILE_DELETED
                | JNotify.FILE_MODIFIED
                | JNotify.FILE_RENAMED;

        // watch subtree?
        boolean watchSubtree = true;

        // add actual watch
        if (ServerSettings.getInstance().isMonitorWatcher())
        {
            int watchID = 0;
            try {
                watchID = JNotify.addWatch(path, mask, watchSubtree, new Listener());
            } catch (JNotifyException ex) {
                Logger.getLogger(AsyncIndex.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        


        // to remove watch the watch
//        boolean res = false;
//        try {
//            res = JNotify.removeWatch(watchID);
//        } catch (JNotifyException ex) {
//            Logger.getLogger(AsyncIndex.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        if (!res) {
//            // invalid watch ID specified.
//        }



    }
}

class Listener implements JNotifyListener {

    public void fileRenamed(int wd, String rootPath, String oldName,
            String newName) {
        print("renamed " + rootPath + " : " + oldName + " -> " + newName);
    }

    public void fileModified(int wd, String rootPath, String name) {
        print("modified " + rootPath + " : " + name);
    }

    public void fileDeleted(int wd, String rootPath, String name) {
        print("deleted " + rootPath + " : " + name);
        IndexEngine.getInstance().deleteFile(rootPath + File.pathSeparator+ name, false);
    }

    public void fileCreated(int wd, String rootPath, String name) {
        print("created " + rootPath + " : " + name);
        IndexEngine.getInstance().indexQueue(rootPath + File.pathSeparator+ name, true);
    }

    void print(String msg) {
        System.err.println(msg);
    }
}
