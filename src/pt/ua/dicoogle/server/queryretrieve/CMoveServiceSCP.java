/*  Copyright   2009 - IEETA
 *
 *  This file is part of Dicoogle.
 *
 *  Author: Luís A. Bastião Silva <bastiao@ua.pt>
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
package pt.ua.dicoogle.server.queryretrieve;

import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.TransformerConfigurationException;
import org.dcm4che2.data.DicomElement;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.net.Association;
import org.dcm4che2.net.DicomServiceException;
import org.dcm4che2.net.DimseRSP;
import org.dcm4che2.net.Status;
import pt.ua.dicoogle.DebugManager;
import pt.ua.dicoogle.core.exceptions.CFindNotSupportedException;

import pt.ua.dicoogle.core.LogDICOM;
import pt.ua.dicoogle.core.LogLine;
import pt.ua.dicoogle.core.LogXML;
import pt.ua.dicoogle.core.MoveDestination;
import pt.ua.dicoogle.server.DicomNetwork;
import pt.ua.dicoogle.server.SearchDicomResult;
import pt.ua.dicoogle.core.ServerSettings;
import pt.ua.dicoogle.rGUI.server.controllers.Logs;

/**
 *
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public class CMoveServiceSCP extends CMoveService {

    private DicomNetwork service = null;

    public CMoveServiceSCP(String[] sopClasses, Executor executor) {
        super(sopClasses, executor);
    }

    public CMoveServiceSCP(String sopClass, Executor executor) {
        super(sopClass, executor);
    }

    @Override
    protected DimseRSP doCMove(Association as, int pcid, DicomObject cmd,
            DicomObject data, DicomObject rsp) throws DicomServiceException {
        DebugManager.getInstance().debug("doCMove");
        DebugManager.getInstance().debug("DoCmove");

        DimseRSP replay = null;

        /**
         * Verify Permited AETs
         */
        DebugManager.getInstance().debug(":: Verify Permited AETs @ C-MOVE Action ");

        boolean permited = false;

        if (ServerSettings.getInstance().getPermitAllAETitles()) {
            permited = true;
        } else 
        {
            String permitedAETs[] = ServerSettings.getInstance().getCAET();

            for (int i = 0; i < permitedAETs.length; i++) {
                if (permitedAETs[i].equals(as.getCallingAET())) {
                    permited = true;
                    break;
                }
            }
        }

        if (!permited) {
            DebugManager.getInstance().debug("Client association NOT permited: " + as.getCallingAET() + "!");
            as.abort();

            return new MoveRSP(data, rsp, null);
        } else {
            DebugManager.getInstance().debug("Client association permited: " + as.getCallingAET() + "!");
        }

        /** FIXME: Write wait by rspreplay */
        try {
            Thread.sleep(ServerSettings.getInstance().getRspDelay());
        } catch (Exception e) {
            e.printStackTrace();
        }



        /**
         *
         * Now it is the code to move
         * In this sense it have a fork, besides we can open the store request
         * in the source direction, or it can make a store to a third party
         *
         */
        /** Get the real IP to move */
        InetAddress ip = as.getSocket().getInetAddress();
        /** Get the port to move */
        int portAddr = as.getSocket().getPort();

        String destination = cmd.getString(org.dcm4che2.data.Tag.MoveDestination);
        DebugManager.getInstance().debug("A Move was required to <ip:port> : <"
                + ip.getHostAddress() + ":" + portAddr + ">" + " to --> " + destination);



        /** Verify if it have the field destination */
        if (destination == null) {
            throw new DicomServiceException(cmd, Status.UnrecognizedOperation,
                    "Missing Move Destination");
        }

        DebugManager.getInstance().debug("-- Objects containing the data requested by C-MOVE");
        DebugManager.getInstance().debug(data.toString());
        DebugManager.getInstance().debug(cmd.toString());
        DebugManager.getInstance().debug(rsp.toString());
        String SOPUID = new String(data.get(Integer.parseInt("0020000D", 16)).getBytes());
        String CMoveID = cmd.getString(org.dcm4che2.data.Tag.MessageID);
        System.out.println("C-MOVE ID REQUEST: " + CMoveID);
        
        /**
         * Get object to search
         */
        ArrayList<String> extrafields = null;
        extrafields = new ArrayList<String>();

        extrafields.add("PatientName");
        extrafields.add("PatientID");
        extrafields.add("Modality");
        extrafields.add("StudyDate");
        extrafields.add("Thumbnail");
        extrafields.add("StudyInstanceUID");
        
        SearchDicomResult.QUERYLEVEL level = null;
        if (CFindBuilder.isPatientRoot(rsp)) {
            level = SearchDicomResult.QUERYLEVEL.PATIENT;
        } else if (CFindBuilder.isStudyRoot(rsp)) {
            level = SearchDicomResult.QUERYLEVEL.STUDY;
        }

        CFindBuilder cfind = null;
        try {
            cfind = new CFindBuilder(data, rsp);
        } catch (CFindNotSupportedException ex) {
            ex.printStackTrace();
        }
        String query = cfind.getQueryString();
        
        SearchDicomResult search = new SearchDicomResult(query,
                true, extrafields, SearchDicomResult.QUERYLEVEL.IMAGE);
        ArrayList<File> files = new ArrayList<File>();




        if (search == null) {
            DebugManager.getInstance().debug(">> Search is null, so"
                    + " somethig is wrong ");
        } else {

            while (search.hasNext()) {
                DicomObject obj = search.next();
                DicomElement e = obj.get(Integer.parseInt("0020000D", 16));
                String tmp = null;
                if (e != null) {
                    tmp = new String(e.getBytes());
                }
                if (SOPUID != null && tmp != null) {
                    files.add(new File(search.getCurrentFile()));
                }

            }
        }





        if (files.size() != 0) {

            /**
             * What is the destination?
             *
             */
            String hostDest = ip.getHostAddress();
            ServerSettings ob = ServerSettings.getInstance();
            for (MoveDestination m : ob.getMoves()) {
                if (m.getAETitle().equals(destination)) {
                    hostDest = m.getIpAddrs();
                    portAddr = m.getPort();
                }
            }


            LogLine ll = new LogLine("cmove", LogLine.getDateTime(), destination,
                    "Files: " + files.size() + " -- (" + hostDest + ":" + portAddr + ")");
            LogDICOM.getInstance().addLine(ll);
            LogXML l = new LogXML();

            try
            {
                l.printXML();
            } catch (TransformerConfigurationException ex) {
                Logger.getLogger(CMoveServiceSCP.class.getName()).log(Level.SEVERE, null, ex);
            }

            Logs.getInstance().addLog(ll);
            if (CMoveID==null||CMoveID.equals(""))
            {
                DebugManager.getInstance().debug("No originator message ID");
                return null;
            }
            try
            {
                new CallDCMSend(files, portAddr, hostDest, destination, CMoveID);
            } catch (Exception ex)
            {
                DebugManager.getInstance().debug("Error Sending files to Storage Server!");
            }
        }

        /** UnNecessary now 

        // put a BufferedReader on the ls output

        InputStream inputstream =
        proc.getInputStream();
        InputStreamReader inputstreamreader =
        new InputStreamReader(inputstream);
        BufferedReader bufferedreader =
        new BufferedReader(inputstreamreader);

        // read the ls output

        String line;
        try
        {
        while ((line = bufferedreader.readLine()) != null)
        {
        System.out.println(">>>"+line);
        }
        //replay = new MoveRSP(keys, rsp, this.core); // Third Party Move
        } catch (IOException ex)
        {
        Logger.getLogger(CMoveServiceSCP.class.getName()).log(Level.SEVERE, null, ex);
        }
        try
        {
        Thread.sleep(100);
        //replay = new MoveRSP(keys, rsp, this.core); // Third Party Move
        } catch (InterruptedException ex)
        {
        Logger.getLogger(CMoveServiceSCP.class.getName()).log(Level.SEVERE, null, ex);
        }
         */
        replay = new MoveRSP(data, rsp, null); // Third Party Move
        return replay;

    }

    /**
     * @return the service
     */
    public DicomNetwork getService() {
        return service;
    }

    /**
     * @param service the service to set
     */
    public void setService(DicomNetwork service) {
        this.service = service;
    }
}
