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
package pt.ua.dicoogle.p2p.jgroups.Utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Carlos Ferreira
 */
public class NetworkInterfacesManager
{

    private List<NetworkInterface> networkInterfaces;
    private List<String> networkNames;

    public NetworkInterfacesManager()
    {
        this.networkNames = new ArrayList<String>();

        this.networkInterfaces = new ArrayList<NetworkInterface>();

        Enumeration<NetworkInterface> nets = null;
        try
        {
            nets = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException ex)
        {
            ex.printStackTrace();
        }
        if (nets == null)
        {
            return;
        }

        for (NetworkInterface netint : Collections.list(nets))
        {
            try
            {
                if ((!netint.isLoopback()) && (netint.supportsMulticast()))
                {
                    Enumeration<InetAddress> addresses = netint.getInetAddresses();
                    while (addresses.hasMoreElements())
                    {
                        if (Inet4Address.class.isInstance(addresses.nextElement()))
                        {
                            this.networkNames.add(netint.getDisplayName());
                            this.networkInterfaces.add(netint);
                            break;
                        }
                    }
                }
            } catch (SocketException ex)
            {
                Logger.getLogger(NetworkInterfacesManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public List<String> getNetworkInterfaces()
    {
        return this.networkNames;
    }

    public String getFirstAddress()
    {
        if (!this.networkInterfaces.isEmpty())
        {
            NetworkInterface netint = this.networkInterfaces.get(0);
            try
            {
                if ((!netint.isLoopback()) && (netint.supportsMulticast()))
                {
                    Enumeration<InetAddress> addresses = netint.getInetAddresses();
                    while (addresses.hasMoreElements())
                    {
                        InetAddress address = addresses.nextElement();
                        if (Inet4Address.class.isInstance(address))
                        {
                            return address.toString();
                        }
                    }
                }
            } catch (SocketException ex)
            {
                Logger.getLogger(NetworkInterfacesManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public String getInterfaceAddress(String interfaceName)
    {
        for (NetworkInterface netint : this.networkInterfaces)
        {
            if (netint.getDisplayName().compareTo(interfaceName) == 0)
            {
                Enumeration<InetAddress> addresses = netint.getInetAddresses();
                while (addresses.hasMoreElements())
                {
                    if (Inet4Address.class.isInstance(addresses.nextElement()))
                    {
                        try
                        {
                            return Inet4Address.getLocalHost().toString();
                        } catch (UnknownHostException ex)
                        {
                            Logger.getLogger(NetworkInterfacesManager.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                break;
            }
        }
        return null;
    }

    public String getInterfaceNameByAddress(String address)
    {
        for (NetworkInterface ni : this.networkInterfaces)
        {

            for (Enumeration<InetAddress> addrs = ni.getInetAddresses(); addrs.hasMoreElements();)
            {
                InetAddress addr = addrs.nextElement();
                if (address.compareTo(addr.toString()) == 0)
                {
                    return ni.getDisplayName();
                }
            }
        }
        return null;
    }

    /* public static void main(String[] args)
    {
    NetworkInterfacesManager nim = new NetworkInterfacesManager();
    Enumeration<NetworkInterface> nets = null;
    try
    {
    nets = NetworkInterface.getNetworkInterfaces();
    } catch (SocketException ex)
    {
    ex.printStackTrace();
    }
    for (NetworkInterface netint : Collections.list(nets))
    {
    try
    {
    if ((!netint.isLoopback()) && (netint.supportsMulticast()))
    {
    Enumeration<InetAddress> addresses = netint.getInetAddresses();
    while (addresses.hasMoreElements())
    {
    
    InetAddress ia = addresses.nextElement();
    if (Inet4Address.class.isInstance(ia))
    {
    System.out.println(ia.toString());
    }
    }
    }
    } catch (SocketException ex)
    {
    Logger.getLogger(NetworkInterfacesManager.class.getName()).log(Level.SEVERE, null, ex);
    }
    }
    }*/
}
