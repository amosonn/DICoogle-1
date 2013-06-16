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


package pt.ua.dicoogle.rGUI.interfaces.controllers;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import pt.ua.dicoogle.rGUI.interfaces.signals.ITaskListSignal;

/**
 *
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public interface ITaskList extends Remote
{
    public void RegisterSignalBack(ITaskListSignal signalBack) throws RemoteException;
    public ArrayList<String> getTaskList() throws RemoteException;
    public void updatedTasks() throws RemoteException;
    public boolean isIndexing() throws RemoteException;
    public int getPercentCompleted() throws RemoteException;
}
