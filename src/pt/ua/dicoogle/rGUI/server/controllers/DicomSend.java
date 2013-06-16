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

import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import pt.ua.dicoogle.DebugManager;
import pt.ua.dicoogle.core.MoveDestination;
import pt.ua.dicoogle.core.ServerSettings;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IDicomSend;
import pt.ua.dicoogle.server.queryretrieve.CallDCMSend;

/**
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
public class DicomSend implements IDicomSend {

    private static Semaphore sem = new Semaphore(1, true);
    private static DicomSend instance = null;

    public static synchronized DicomSend getInstance() {
        try {
            sem.acquire();
            if (instance == null) {
                instance = new DicomSend();
            }
            sem.release();
        } catch (InterruptedException ex) {
            Logger.getLogger(DicomSend.class.getName()).log(Level.SEVERE, null, ex);
        }
        return instance;
    }

    private DicomSend() {
    }

    @Override
    public ArrayList<MoveDestination> getDestinations() throws RemoteException {
        return ServerSettings.getInstance().getMoves();
    }

    @Override
    public boolean sendFiles(MoveDestination destination, ArrayList<String> FilePaths) throws RemoteException {
        if(destination == null || FilePaths == null || FilePaths.isEmpty())
            return false;

        /**
         * Convert all images to an array with File list
         */
        ArrayList<File> fileList = new ArrayList<File>();

        Iterator<String> it  = FilePaths.iterator();
        while (it.hasNext()) 
            fileList.add(new File(it.next()));
        

         DebugManager.getInstance().debug("Sending files to Destination");
         
        /**
         * Call DICOM Storage SCU for each image
         */
        DebugManager.getInstance().debug("AETITLE to DICOM STORAGE SCU: "
                + destination.getAETitle());

        try {
            CallDCMSend s = new CallDCMSend(fileList, destination.getPort(), destination.getIpAddrs(), destination.getAETitle(), null);
        } catch (Exception ex) {
            return false;
        }

        return true;
    }
}
