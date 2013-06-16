/*  Copyright   2010 - IEETA
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


package pt.ua.dicoogle.rGUI.server.controllers;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import pt.ua.dicoogle.common.index.Task;
import pt.ua.dicoogle.core.index.IndexEngine;
import pt.ua.dicoogle.rGUI.interfaces.controllers.ITaskList;
import pt.ua.dicoogle.rGUI.interfaces.signals.ITaskListSignal;

/**
 *
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public class TaskList implements ITaskList
{

    private ITaskListSignal signalBack;
    private static TaskList instance = null;

    public static synchronized TaskList getInstance()
    {

        if (instance == null)
        {
            instance = new TaskList();
        }
        return instance;
    }

    public void resetSignalBack()
    {
        signalBack = null;
    }

    @Override
    public void RegisterSignalBack(ITaskListSignal signalBack) throws RemoteException
    {
        this.signalBack = signalBack;
    }

    @Override
    public ArrayList<String> getTaskList()  throws RemoteException
    {

        IndexEngine core = IndexEngine.getInstance();
        //System.out.println("TaskList: " + core.getTaskList());
        //System.out.println("TaskList.lenght: " + core.getTaskList().size());
        LinkedBlockingQueue<Task> tasks = core.getTaskList();
        ArrayList<String> listPaths = new ArrayList<String>();
        for (Task t : tasks)
        {
            listPaths.add(t.getPath());
        }
        return listPaths;
    }


    @Override
    public void updatedTasks() throws RemoteException
    {
        if (signalBack!=null)
            this.signalBack.sendTaskSignal(0);
    }

    @Override
    public boolean isIndexing() throws RemoteException
    {
        IndexEngine core = IndexEngine.getInstance();
        return core.isIndexing();
    }

    @Override
    public int getPercentCompleted() throws RemoteException
    {
        return IndexEngine.getInstance().percentCompleted();
    }

}
