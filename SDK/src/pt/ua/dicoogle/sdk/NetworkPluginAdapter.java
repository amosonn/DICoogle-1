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
package pt.ua.dicoogle.sdk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import pt.ua.dicoogle.sdk.Utils.QueryNumber;
import pt.ua.dicoogle.sdk.Utils.SearchResult;
import pt.ua.dicoogle.sdk.Utils.TaskQueue;
import pt.ua.dicoogle.sdk.Utils.TaskRequest;
import pt.ua.dicoogle.sdk.observables.FileObservable;
import pt.ua.dicoogle.sdk.observables.ListObservable;
import pt.ua.dicoogle.sdk.observables.ListObservableSearch;
import pt.ua.dicoogle.sdk.observables.MessageObservable;
import pt.ua.dicoogle.sdk.p2p.Messages.Builders.MessageBuilder;
import pt.ua.dicoogle.sdk.p2p.Messages.Handlers.MainMessageHandler;
import pt.ua.dicoogle.sdk.p2p.Messages.MessageI;

/**
 *
 * @author Carlos Ferreira
 */
public abstract class NetworkPluginAdapter implements GenericPluginInterface, Observer
{

    private boolean isRunning = false;
    protected MainMessageHandler MMH;
    private ListObservableSearch<SearchResult> searchResults = new ListObservableSearch<SearchResult>();
    private TaskQueue TaskRequestsList;
    private MessageObservable mo;
    private List<FileObservable> requestedFiles = Collections.synchronizedList(new ArrayList<FileObservable>());

    @Override
    public abstract String getName();

    public abstract NetworkPluginAdapter getInstance();

    public abstract MessageObservable initialize();

    public abstract void connect();

    public abstract void disconnect();

    public abstract boolean isConnected();

    public abstract void send(Object message);

    public abstract void send(Object toSend, String address);

    public abstract void sendFile(String path, String destAddress);

    public abstract ListObservableSearch<String> getMembers();

    public abstract String getLocalAddress();

    protected abstract MessageObservable getLastmessage();

    public List<FileObservable> getRequestedFiles()
    {
        return this.requestedFiles;
    }

    public TaskQueue getTaskRequestsList()
    {
        return TaskRequestsList;
    }

    @Override
    public void attendTask(TaskRequest task)
    {
        return;
    }

    @Override
    public void initialize(TaskQueue tasks)
    {
        //System.out.println("NetworkPluginAdapter initialize");
        this.isRunning = true;
        this.TaskRequestsList = tasks;
        mo = getInstance().initialize();
        mo.addObserver(getInstance());
        this.MMH = new MainMessageHandler(this);
    }

    @Override
    public void Stop()
    {
        this.isRunning = false;
        this.disconnect();
    }

    @Override
    public ListObservableSearch<SearchResult> search(String query, List<String> extrafields)
    {
        MessageBuilder mb = new MessageBuilder();
        MessageI message = null;
        try
        {
            Integer qNumber;
            synchronized (this.searchResults)
            {
                qNumber = QueryNumber.getInstance().getNewQueryNumber();
                this.searchResults.resetArray();
            }
            message = mb.buildQueryMessage(query, extrafields, this.getName(), qNumber);
        } catch (IOException ex)
        {
            Logger.getLogger(NetworkPluginAdapter.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        send(message);
        return this.searchResults;
    }

    @Override
    public ListObservableSearch<SearchResult> searchOne(String query, List<String> Extrafields, String address)
    {
        MessageBuilder mb = new MessageBuilder();
        MessageI message = null;
        try
        {
            Integer qNumber;
            synchronized (this.searchResults)
            {
                qNumber = QueryNumber.getInstance().getNewQueryNumber();
                this.searchResults.resetArray();
            }
            message = mb.buildQueryMessage(query, Extrafields, this.getName(), qNumber);
        } catch (IOException ex)
        {
            Logger.getLogger(NetworkPluginAdapter.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        send(message, address);
        return this.searchResults;
    }

    @Override
    public FileObservable requestFile(String address, String name, String hash)
    {
        MessageBuilder mb = new MessageBuilder();
        MessageI message = null;
        try
        {
            message = mb.buildFileRequest(name, hash, this.getName());
        } catch (IOException ex)
        {
            Logger.getLogger(NetworkPluginAdapter.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        FileObservable newFileObservable = new FileObservable(address, name);
        this.requestedFiles.add(newFileObservable);
        send(message, address);
        return newFileObservable;
    }

    @Override
    public boolean isLocalPlugin()
    {
        return false;
    }

    @Override
    public boolean isRunning()
    {
        return this.isRunning;
    }

    @Override
    public void update(Observable o, Object arg)
    {
        //System.out.println("NetworkPluginPanel....some message received..."+ o.toString());
        if (MessageObservable.class.isInstance(o))
        {
            MessageObservable message = (MessageObservable) o;
            //System.out.println("NPA....received the message:\n"+ message);
            this.MMH.handleMessage((MessageI) message.getMessage(), message.getAddress());
            
        }
    }

    public ListObservable<SearchResult> getSearchResults()
    {
        return searchResults;
    }
    
    
}
