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
package pt.ua.dicoogle.p2p.jgroups.FileHandlers;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jgroups.Address;
import org.jgroups.ChannelClosedException;
import org.jgroups.ChannelNotConnectedException;
import pt.ua.dicoogle.p2p.jgroups.Utils.FileFragmentMessage;
import pt.ua.dicoogle.p2p.jgroups.sockets.MulticastSocketHandler;
import pt.ua.dicoogle.sdk.p2p.Messages.MessageI;
import pt.ua.dicoogle.sdk.p2p.Messages.MessageType;

/**
 *
 * @author Carlos Ferreira
 */
public class FileSender extends Thread
{

    private Address destAddress;
    private File toBeSent;
    private static final int MAX_MESSAGE_SIZE = 10000000; // 10MB per message

    public FileSender(String fileAddress, Address dest)
    {
        //do not allow the file broadcast
        if (dest == null)
        {
            return;
        }
        this.destAddress = dest;
        if (fileAddress == null)
        {
            return;
        }
        this.toBeSent = new File(fileAddress);
        if (toBeSent.exists())
        {
            this.start();
        }
    }

    @Override
    public void run()
    {
        System.out.println("Run do FileSender..........");
        try
        {
            RandomAccessFile raf = new RandomAccessFile(this.toBeSent, "r");
            byte[] bytes = new byte[FileSender.MAX_MESSAGE_SIZE];
            for (int i = 0; i < raf.length(); i += FileSender.MAX_MESSAGE_SIZE)
            {
//                raf.seek(i);
                System.out.println("PASSOU AQUI...........");
                int read = raf.read(bytes);//, i, FileSender.MAX_MESSAGE_SIZE);
                MessageI message = null;
                if (read < FileSender.MAX_MESSAGE_SIZE)
                {
                    System.out.println("PASSOU AQUI........... 2 ");
                    byte[] bytesRead = Arrays.copyOf(bytes, read);
                    message = new FileFragmentMessage(bytesRead, MessageType.FILE_RESP, this.toBeSent.getName(), i, true);
                } else
                {System.out.println("PASSOU AQUI........... 3 ");
                    
                    message = new FileFragmentMessage(bytes, MessageType.FILE_RESP, this.toBeSent.getName(),i, false);
                }
                System.out.println("PASSOU AQUI........... 4");
                MulticastSocketHandler.getInstance().send(message, this.destAddress);
                System.out.println("Fragment send, offset: "+ i + " to: "+ this.toBeSent.getName()+ " read: "+ read+ " size: "+ raf.length());
            }
        } catch (IOException ex)
        {
            Logger.getLogger(FileSender.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ChannelNotConnectedException ex)
        {
            Logger.getLogger(FileSender.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ChannelClosedException ex)
        {
            Logger.getLogger(FileSender.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
