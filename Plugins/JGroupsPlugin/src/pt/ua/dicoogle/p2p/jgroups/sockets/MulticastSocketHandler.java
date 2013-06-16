/*  Copyright   2009 Carlos Ferreira
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
package pt.ua.dicoogle.p2p.jgroups.sockets;


import java.io.IOException;
import java.io.Serializable;
import org.jgroups.ChannelClosedException;
import org.jgroups.ChannelException;
import org.jgroups.ChannelNotConnectedException;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.Address;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import pt.ua.dicoogle.p2p.jgroups.FileHandlers.FileReceiver;
import pt.ua.dicoogle.p2p.jgroups.Utils.FileFragmentMessage;
import pt.ua.dicoogle.sdk.observables.ListObservable;
import pt.ua.dicoogle.sdk.observables.ListObservableSearch;
import pt.ua.dicoogle.sdk.observables.MessageObservable;
//import pt.ua.dicoogle.sdk.observables.ObjectObservable;
import pt.ua.dicoogle.sdk.p2p.Messages.FileMessage;
import pt.ua.dicoogle.sdk.p2p.Messages.MessageI;
import pt.ua.dicoogle.sdk.p2p.Messages.MessageType;

/**
 * Class responsible for the communication between peers.
 * @author Carlos Ferreira
 * @author Pedro Bento
 */
public class MulticastSocketHandler extends ReceiverAdapter
{
    //Channel of communication between peers.

    private static JChannel channel;
    //Observable responsible for the membersOb of the view.
    private static ListObservableSearch<Address> membersOb;
    //Observable responsible for the last message received.
    private static MessageObservable lastmessageOb;
    //Observable responsible for the exceptions of the receiver method.
   // private static ObjectObservable<Exception> exceptionsOb;
    //the only instance of multicastSocketHandler
    private static MulticastSocketHandler instance = null;

    /**
     * Getter of the instance of this singleton
     * @return instance of MulticastSocketHandler
     */
    public static MulticastSocketHandler getInstance()
    {
        if (MulticastSocketHandler.instance == null)
        {
            instance = new MulticastSocketHandler();
        }
        return instance;
    }

    /**
     * private constructor of the instance.
     */
    private MulticastSocketHandler()
    {
        //Creating the observables.
        membersOb = new ListObservableSearch();
        lastmessageOb = new MessageObservable();
//        exceptionsOb = new ObjectObservable();
    }

    public void initialize(String BindAddress, String clusterName) throws ChannelException
    {
        //This line solves the problem of OS with both ipv4 and ipv6 stacks.
        System.setProperty("java.net.preferIPv4Stack", "true");
        if (BindAddress != null)
        {
            System.out.println("Multicast Initialization on address:" + BindAddress);
            System.setProperty("jgroups.bind_addr", BindAddress);
        }
        //initialization of the channel and connects it to the cluster.
        channel = new JChannel();
        channel.setReceiver(getInstance());
        channel.connect(clusterName);
    }

    /**
     * Everytime the view of the cluster is changed this method is called.
     * The view is the set of membersOb of the cluster.
     * @param new_view the new view of the cluster.
     */
    @Override
    public void viewAccepted(View new_view)
    {
        System.out.println(new_view);
        membersOb.setArray(new_view.getMembers());
    }

    /**
     * Everytime there is a message received this method is automatically called.
     * The exceptions caught in this method are communicated to the outside throw
     * a exception observable, that can be got using the getExceptionOb() method.
     * @see getExceptionsOb()
     * @param msg the message received.
     */
    @Override
    public void receive(Message msg)
    {
        MessageI bodyMessage = null;
        try
        {
            bodyMessage = (MessageI) msg.getObject();
        } catch (Exception ex)
        {
            ex.printStackTrace(System.out);
//            MulticastSocketHandler.exceptionsOb.setObject(ex);
        }
        
        if(bodyMessage.getType().compareTo(MessageType.FILE_RESP)==0)
        {
            FileFragmentMessage body = (FileFragmentMessage) bodyMessage;
            FileReceiver receiver = new FileReceiver();
            receiver.handleFileFragment(body, "received/"+body.getFilename());
            if(!body.isLastFragment())
                return;
            else
            {
                String filePath = "received/"+body.getFilename();
                lastmessageOb.setMessage(new FileMessage(filePath.getBytes(), MessageType.FILE_RESP, body.getFilename()), msg.getSrc().toString());
                return;
            }
        }
        try
        {
            lastmessageOb.setMessage(bodyMessage, msg.getSrc().toString());
            //System.out.println("Received from:" + msg.getSrc());
            //System.out.println(bodyMessage);
        } catch (Exception ex)
        {
            ex.printStackTrace(System.out);
        }
    }

    /**
     * Sender of an object for all members of the network.
     * @param message the message to be sent, it is an object that must be serializable
     */
    public void send(Object message) throws ChannelNotConnectedException, ChannelClosedException, IOException
    {
        if (!Serializable.class.isInstance(message))
        {
            throw new java.io.IOException("Class not serializable");
        }
        //creatting the message to be sent.
        Message msg = new Message(null, null, (Serializable) message);

        //Sending the message.
        channel.send(msg);
        //System.out.println("Sending to all: ");
        //System.out.println(message);
    }

    /**
     * Sender of an object for a specific member of the network.
     * @param toSend object that must be serializable and will be sent to the network.
     * @param address of the destination of the message.
     */
    public void send(Object toSend, Address address) throws ChannelNotConnectedException, ChannelClosedException, IOException
    {
        if (!Serializable.class.isInstance(toSend))
        {
            throw new java.io.IOException("Class not serializable");
        }
        //creatting the message to be sent.
        Message msg = new Message(address, null, (Serializable) toSend);

        //Sending the message.
        channel.send(msg);
        //System.out.println("Sending to: " + msg.getDest());
        //System.out.println(toSend);
    }

    /**
     * Getter of the observable with the last message received
     * @return
     */
    public MessageObservable getLastmessage()
    {
        return lastmessageOb;
    }

    /**
     * Getter of the observable with the list of members of the network
     * @return
     */
    public ListObservableSearch<Address> getMembers()
    {
        return membersOb;
    }

    /**
     * getter of the observable with the last exception thrown
     * @return observable of exceptions
     */
  /*  public static ObjectObservable<Exception> getExceptionsOb()
    {
        return exceptionsOb;
    }
*/
    public Address getLocalAddress()
    {
        return channel.getLocalAddress();
    }

    public static void connect(String BindAddress) throws ChannelException
    {
        System.out.println("Multicast Initialization on address:" + BindAddress);
        if (BindAddress != null)
        {
            System.setProperty("jgroups.bind_addr", BindAddress);
        }
        channel = new JChannel();
        channel.setReceiver(getInstance());
        channel.connect("dicoogle");
    }

    public static void disconnect()
    {
        channel.disconnect();
    }

    public static boolean isConnected()
    {
        if (channel != null)
        {
            return channel.isConnected();
        }

        return false;
    }
}
