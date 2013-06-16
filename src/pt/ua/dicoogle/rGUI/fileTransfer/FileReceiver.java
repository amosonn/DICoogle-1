/*  Copyright   2010 Samuel Campos
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


package pt.ua.dicoogle.rGUI.fileTransfer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import pt.ua.dicoogle.DebugManager;
import pt.ua.dicoogle.core.ClientSettings;
import pt.ua.dicoogle.rGUI.RFileBrowser.RemoteFile;

/**
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
public class FileReceiver extends Thread {
    private RemoteFile file;
    private InetAddress serverAddr;
    private int serverPort;
    private TransferStatus ts;

    private Socket socket;

    private String filePath;

    public FileReceiver(RemoteFile file, InetAddress serverAddr, int serverPort, TransferStatus ts) throws IOException{
        SocketFactory socketFactory = SSLSocketFactory.getDefault();
        socket = socketFactory.createSocket(serverAddr, serverPort);

        this.file = file;
        this.serverAddr = serverAddr;
        this.serverPort = serverPort;
        this.ts = ts;

        String dirPath = ClientSettings.getInstance().getTempFilesDir();

        //if the temporary folder is not defined
        if (dirPath == null || dirPath.equals(""))
            dirPath = ".";

        filePath = dirPath + "/" + file.getName();
        ts.setFilePath(filePath);
    }

    @Override
    public void run(){
        DebugManager.getInstance().debug("Starting transfer Thread...");

        long sizeTransfered = receiver();

        if(sizeTransfered != -1)
            DebugManager.getInstance().debug("Transfer complete! File: " + file.getName());

        return;
    }

    public String getFilePath(){
        return filePath;
    }

    private long receiver(){
        long transferedBytes = 0;

        try{
            // Buffer size = 1 KB
            byte data[] = new byte[1024];

            InputStream in = socket.getInputStream();
            FileOutputStream out = new FileOutputStream(filePath);

            int size;
            // write in file
            while ((size = in.read(data)) != -1){
                transferedBytes += size;

                out.write(data, 0, size);
		out.flush();

                // refresh the progress bar
                if(ts != null)
                    ts.setTransferedBytes(transferedBytes);
            }

            // Freeing resources
            out.close();
            in.close();
            socket.close();

        } catch (IOException ex) {
            ts.errorInTransfer("There was an error downloading the file!");

            try {
                if (!socket.isClosed())
                    socket.close();
            } catch (IOException e) {
                Logger.getLogger(FileReceiver.class.getName()).log(Level.SEVERE, null, e);
            }

            //Logger.getLogger(FileSender.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }

        return transferedBytes;
    }
}
