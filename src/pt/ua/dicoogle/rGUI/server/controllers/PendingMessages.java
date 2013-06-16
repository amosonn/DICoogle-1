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

package pt.ua.dicoogle.rGUI.server.controllers;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IPendingMessages;
import pt.ua.dicoogle.rGUI.interfaces.signals.IPendingMessagesSignal;

/**
 * Controller to manage the pending messages to the administrator
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
public class PendingMessages implements IPendingMessages {

    private IPendingMessagesSignal signal;

    // list of files that are already indexed but the administrator as to decide if they will be re-indexed or not
    private ArrayList<String> filesAlreadyIndexed;

    /**
     * I thought of using synchronized methods to take care of concurrency 
     * in filesAlreadyIndexed ArrayList but this causes deadlock.
     * I use Semaphore semList instead.
     */
    private static Semaphore semList = new Semaphore(1, true);
    
    private static PendingMessages instance = null;
    private static Semaphore sem = new Semaphore(1, true);

    public static synchronized PendingMessages getInstance()
    {
        try {
            sem.acquire();
            if (instance == null) {
                instance = new PendingMessages();
            }
            sem.release();
        } catch (InterruptedException ex) {
            Logger.getLogger(PendingMessages.class.getName()).log(Level.SEVERE, null, ex);
        }
        return instance;
    }

    private PendingMessages(){
        filesAlreadyIndexed = new ArrayList<String>();
    }


    /**
     * Add one file to the list of files already indexed
     * 
     * @param absolutePath
     */
    public void addFileAlreadyIndexed(String absolutePath){

        try {
            
            semList.acquire();
            filesAlreadyIndexed.add(absolutePath);
            semList.release();
            
            try {
                if (signal != null) {
                    signal.sendPendingMessagesSignal(0);
                }
            } catch (RemoteException ex) {
                Logger.getLogger(PendingMessages.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (InterruptedException ex) {
            Logger.getLogger(PendingMessages.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    /**
     *
     * @return the list of the files that are already indexed
     * @throws RemoteException
     */
    @Override
    public ArrayList<String> getFilesAlreadyIndexed() throws RemoteException {
        try {
            semList.acquire();

            ArrayList<String> temp = filesAlreadyIndexed;
            filesAlreadyIndexed = new ArrayList<String>();

            semList.release();

            return temp;
        } catch (InterruptedException ex) {
            Logger.getLogger(PendingMessages.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * Register the remote signal object to send pending messages
     *
     * @param signalBack
     * @throws RemoteException
     */
    @Override
    public void RegisterSignalBack(IPendingMessagesSignal signalBack) throws RemoteException {
        if (signalBack != null){
            signal = signalBack;

            if(filesAlreadyIndexed.size() > 0)
                signal.sendPendingMessagesSignal(0);
        }
    }

    public void resetSignalBack(){
        signal = null;
    }

}
