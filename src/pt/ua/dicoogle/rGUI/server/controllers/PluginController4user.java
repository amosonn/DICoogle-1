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
import pt.ua.dicoogle.rGUI.interfaces.controllers.IPluginControllerUser;
import pt.ua.dicoogle.sdk.Utils.PluginPanel;

/**
 *
 * @author Carlos Ferreira
 */
public class PluginController4user implements IPluginControllerUser
{
    private static PluginController4user instance = null;
    
    private PluginController4user()
    {
    }

    public static PluginController4user getInstance()
    {
        if(PluginController4user.instance == null)
        {
            PluginController4user.instance = new PluginController4user();
        }
        return PluginController4user.instance;
    }
    
    @Override
    public synchronized List<String> getPluginNames() throws RemoteException
    {
        return PluginController.getInstance().getPluginsNames();
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


}
