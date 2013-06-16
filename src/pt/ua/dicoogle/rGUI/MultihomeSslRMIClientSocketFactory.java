/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pt.ua.dicoogle.rGUI;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.rmi.RMISecurityManager;
import java.rmi.server.RMIClientSocketFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.rmi.ssl.SslRMIClientSocketFactory;

/**
 * 
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
public class MultihomeSslRMIClientSocketFactory extends SslRMIClientSocketFactory
        implements RMIClientSocketFactory, Serializable {
    private static final long serialVersionUID = 11L;

    //private final SslRMIClientSocketFactory factory;

    public MultihomeSslRMIClientSocketFactory() {
        super();
    }

    @Override
    public Socket createSocket(String hostString, int port) throws IOException {
        final String[] hosts = hostString.split("!");
        final int nhosts = hosts.length;
        
        if (nhosts < 2)
            return super.createSocket(hostString, port);

        List<IOException> exceptions = new ArrayList<IOException>();

        Selector selector = Selector.open();
        
        for (String host : hosts) {
            SocketChannel channel = SocketChannel.open();
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_CONNECT);
            SocketAddress addr = new InetSocketAddress(host, port);
            
            
            try{
                channel.connect(addr);
            }catch(SocketException e){
                //System.err.println("Sockect Exception: "+ e.getMessage() + ", Host: " + host + ", Port: "+ port);
            }
        }
        SocketChannel connectedChannel = null;

        connect:
        while (true) {
            if (selector.keys().isEmpty()) {
                throw new IOException("Connection failed for " + hostString +
                        ": " + exceptions);
            }
            selector.select();  // you can add a timeout parameter in millseconds
            Set<SelectionKey> keys = selector.selectedKeys();
            if (keys.isEmpty()) {
                throw new IOException("Selection keys unexpectedly empty for " +
                        hostString + "[exceptions: " + exceptions + "]");
            }
            for (SelectionKey key : keys) {
                SocketChannel channel = (SocketChannel) key.channel();
                key.cancel();
                try {
                    channel.configureBlocking(true);
                    channel.finishConnect();
                    connectedChannel = channel;
                    break connect;
                } catch (IOException e) {
                    exceptions.add(e);
                }
            }
        }

        assert connectedChannel != null;

        // Close the channels that didn't connect
        for (SelectionKey key : selector.keys()) {
            Channel channel = key.channel();
            if (channel != connectedChannel)
                channel.close();
        }

        final Socket socket = connectedChannel.socket();

        // We've determined that we can connect to this host but we didn't use
        // the right factory so we have to reconnect with the factory.
        String host = socket.getInetAddress().getHostAddress();
        socket.close();
        return super.createSocket(host, port);
    }

}
