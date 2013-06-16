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
package pt.ua.dicoogle.p2p.jgroups;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import org.jgroups.Address;
import org.jgroups.ChannelClosedException;
import org.jgroups.ChannelException;
import org.jgroups.ChannelNotConnectedException;
import pt.ua.dicoogle.p2p.jgroups.FileHandlers.FileSender;
import pt.ua.dicoogle.p2p.jgroups.sockets.MulticastSocketHandler;
import pt.ua.dicoogle.p2p.jgroups.Utils.NetworkInterfacesManager;
import pt.ua.dicoogle.p2p.jgroups.Utils.Settings;
import pt.ua.dicoogle.sdk.NetworkPluginAdapter;
import pt.ua.dicoogle.sdk.Utils.SearchResult;
import pt.ua.dicoogle.sdk.observables.ListObservable;
import pt.ua.dicoogle.sdk.observables.ListObservableSearch;
import pt.ua.dicoogle.sdk.observables.MessageObservable;
import pt.ua.dicoogle.sdk.p2p.Messages.MessageI;


/**
 *
 * @author Carlos Ferreira
 */
@PluginImplementation
public class MulticastPlugin extends NetworkPluginAdapter implements Observer
{

    public static final String PluginName = "LAN JGroups";
    private static MulticastPlugin instance = null;
    private static boolean isRunning = false;
    private ListObservableSearch<String> members;

    public MulticastPlugin()
    {
        instance = this;
    }

    @Override
    public String getName()
    {
        return PluginName;
    }

    @Override
    public synchronized NetworkPluginAdapter getInstance()
    {
        if (instance == null)
        {
            instance = new MulticastPlugin();
        }
        return instance;
    }

    @Override
    public MessageObservable initialize()
    {
        try
        {
            if (Settings.getInstance().getBindAddress() == null)
            {
                this.setDefaultSettings();
            }
            MulticastSocketHandler.getInstance().initialize(Settings.getInstance().getBindAddress().substring(Settings.getInstance().getBindAddress().lastIndexOf('/') + 1),
                    Settings.getInstance().getClusterName());

        } catch (ChannelException ex)
        {
            Logger.getLogger(MulticastPlugin.class.getName()).log(Level.SEVERE, null, ex);
        }
        MulticastPlugin.isRunning = true;
        synchronized (this)
        {
            this.members = new ListObservableSearch<String>();
            ListObservableSearch<Address> mmbers = MulticastSocketHandler.getInstance().getMembers();
            mmbers.addObserver(this);
            ArrayList<Address> mArray = mmbers.getArray();
            ArrayList<String> StArray = new ArrayList<String>();
            for (Address address : mArray)
            {
                StArray.add(address.toString());
            }
            this.members.resetArray();
            this.members.addAll(StArray);
        }

        return MulticastSocketHandler.getInstance().getLastmessage();
    }

    @Override
    public void connect()
    {
        try
        {
            MulticastSocketHandler.connect(null);
        } catch (ChannelException ex)
        {
            Logger.getLogger(MulticastPlugin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void disconnect()
    {
        if (MulticastPlugin.isRunning)
        {
            MulticastSocketHandler.disconnect();
        }
    }

    @Override
    public boolean isConnected()
    {
        return MulticastPlugin.isRunning;
    }

    @Override
    public void send(Object o)
    {
        if (MulticastPlugin.isRunning)
        {
            try
            {
                MulticastSocketHandler.getInstance().send(o);
            } catch (ChannelNotConnectedException ex)
            {
                Logger.getLogger(MulticastPlugin.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ChannelClosedException ex)
            {
                Logger.getLogger(MulticastPlugin.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex)
            {
                Logger.getLogger(MulticastPlugin.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void send(Object o, String string)
    {
        ArrayList<Address> membrs = MulticastSocketHandler.getInstance().getMembers().getArray();

        Address member = null;

        for (Address addr : membrs)
        {
            if (string.compareTo(addr.toString()) == 0)
            {
                member = addr;
                break;
            }
        }
        if (member == null)
        {
            return;
        }
        if (MulticastPlugin.isRunning)
        {
            try
            {
                MulticastSocketHandler.getInstance().send(o, member);
            } catch (ChannelNotConnectedException ex)
            {
                Logger.getLogger(MulticastPlugin.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ChannelClosedException ex)
            {
                Logger.getLogger(MulticastPlugin.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex)
            {
                Logger.getLogger(MulticastPlugin.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void sendFile(String path, String destAddress)
    {
        ArrayList<Address> membrs = MulticastSocketHandler.getInstance().getMembers().getArray();
        Address member = null;
        for (Address addr : membrs)
        {
            System.out.println(addr + " vs "+ destAddress);
            if (destAddress.compareTo(addr.toString()) == 0)
            {
                member = addr;
                break;
            }
        }
        System.out.println("Sending file to: "+ member);
        FileSender sender = new FileSender(path, member);
    }

    @Override
    public ListObservableSearch<String> getMembers()
    {
        return this.members;
    }

    @Override
    public String getLocalAddress()
    {
        return MulticastSocketHandler.getInstance().getLocalAddress().toString();
    }

    @Override
    protected MessageObservable getLastmessage()
    {
        return MulticastSocketHandler.getInstance().getLastmessage();
    }

    public void setDefaultSettings()
    {
        Settings.getInstance().setClusterName("dicoogle");
        Settings.getInstance().setBindAddress(new NetworkInterfacesManager().getFirstAddress());
    }

    public void setSettings(ArrayList<Object> al)
    {
        if (al == null)
        {
            return;
        }
        Settings SInstance = Settings.getInstance();
        SInstance.setBindAddress((String) al.get(0));
        SInstance.setClusterName((String) al.get(1));
    }

    public ArrayList<Object> getPanelInitilizationParams()
    {
        ArrayList<Object> params = new ArrayList<Object>();
        params.add(Settings.getInstance().getBindAddress());
        params.add(Settings.getInstance().getClusterName());
        return params;
    }

    @Override
    public synchronized void update(Observable a, Object arg)
    {
        if (ListObservable.class.isInstance(a))
        {
            ListObservable addresses = (ListObservable) a;
            this.members.resetArray();
            if (addresses.getArray().isEmpty())
            {
                return;
            } else
            {
                if (!Address.class.isInstance(addresses.getArray().get(0)))
                {
                    return;
                }
                ArrayList<Address> adds = addresses.getArray();
                ArrayList<String> strings = new ArrayList<String>();
                for (Address add : adds)
                {
                    strings.add(add.toString());
                }
                this.members.addAll(strings);
            }
        }
        if (MessageObservable.class.isInstance(a))
        {
            MessageObservable message = (MessageObservable) a;
            System.out.println("NPA....received the message:\n"+ message);
            this.MMH.handleMessage((MessageI) message.getMessage(), message.getAddress());
            
        }
    }

}
