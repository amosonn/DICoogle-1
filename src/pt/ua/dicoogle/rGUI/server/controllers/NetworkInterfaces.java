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
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;
import java.util.logging.Level;
import pt.ua.dicoogle.core.ServerSettings;
import pt.ua.dicoogle.rGUI.interfaces.controllers.INetworkInterfaces;

/**
 *
 * @author Carlos Ferreira
 */
public class NetworkInterfaces implements INetworkInterfaces
{
    private ServerSettings settings;
    private static Semaphore sem = new Semaphore(1, true);
    private static NetworkInterfaces instance = null;

    public static synchronized NetworkInterfaces getInstance()
    {
        try
        {
            sem.acquire();
            if (instance == null)
            {
                instance = new NetworkInterfaces();
            }
            sem.release();
        } catch (InterruptedException ex)
        {
            Logger.getLogger(QRServers.class.getName()).log(Level.SEVERE, null, ex);
        }
        return instance;
    }

    private NetworkInterfaces()
    {
        settings = ServerSettings.getInstance();
    }

    @Override
    public ArrayList<String> getNetworkInterfaces() throws RemoteException
    {
        return this.settings.getNetworkInterfacesNames();
    }

    @Override
    public void setNetworkInterface(String name) throws RemoteException
    {
        if(name != null)
        {
            //System.out.println("interface chosen: " + name);
            this.settings.setNetworkInterfaceName(name);
        }
    }

    @Override
    public String getNetworkInterface() throws RemoteException
    {
        return this.settings.getNetworkInterfaceName();
    }


}
