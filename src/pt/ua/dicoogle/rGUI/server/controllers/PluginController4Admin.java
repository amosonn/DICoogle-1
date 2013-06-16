/*  Copyright   2011 Carlos Ferreira
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
import java.util.HashMap;
import java.util.List;
import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IPluginControllerAdmin;
import pt.ua.dicoogle.sdk.Utils.PluginPanel;

/**
 *
 * @author Carlos Ferreira
 */
public class PluginController4Admin implements IPluginControllerAdmin
{
    private static PluginController4Admin instance = null;

    private PluginController4Admin()
    {
    }

    public static PluginController4Admin getInstance()
    {
        if(PluginController4Admin.instance == null)
        {
            PluginController4Admin.instance = new PluginController4Admin();
        }
        return PluginController4Admin.instance;
    }

    @Override
    public synchronized List<String> getPluginNames() throws RemoteException
    {
        return PluginController.getInstance().getPluginsNames();
    }

    @Override
    public synchronized void setSettings(HashMap<String, ArrayList> settings) throws RemoteException
    {
        PluginController.getInstance().setSettings(settings);
    }

    @Override
    public void InitiatePlugin(String PluginName) throws RemoteException
    {
        PluginController.getInstance().initializePlugin(PluginName);
    }

    @Override
    public void StopPlugin(String PluginName) throws RemoteException
    {
        PluginController.getInstance().stopPlugin(PluginName);
    }

    @Override
    public boolean isRunning(String PluginName) throws RemoteException
    {
        return PluginController.getInstance().isPluginRunning(PluginName);
    }

    @Override
    public boolean isLocalPlugin(String PluginName) throws RemoteException
    {
        return PluginController.getInstance().isLocalPlugin(PluginName);
    }

    @Override
    public HashMap<String, ArrayList> getInitializeParams() throws RemoteException
    {
        return PluginController.getInstance().getPanelInitialParams();
    }

    @Override
    public byte[] getJarFile(String PluginName) throws RemoteException
    {
        return PluginController.getInstance().getJarFile(PluginName);
    }

    @Override
    public void saveSettings() throws RemoteException
    {
        PluginController.getInstance().saveSettings();
    }

}
