/*  Copyright   2008 João Pereira, Miguel Fonseca
 *              2007 Marco Pereira, Filipe Freitas
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

package pt.ua.dicoogle.server;

import pt.ua.dicoogle.core.ServerSettings;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.nio.channels.FileChannel;
import org.dcm4che2.data.BasicDicomObject;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.UID;
import org.dcm4che2.io.DicomOutputStream;
import org.dcm4che2.net.Association;
import org.dcm4che2.net.CommandUtils;
import org.dcm4che2.net.Device;
import org.dcm4che2.net.DicomServiceException;
///import org.dcm4che2.net.Executor;
/** dcm4che doesn't support Executor anymore, so now import from java.util */ 
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.dcm4che2.net.NetworkApplicationEntity;
import org.dcm4che2.net.NetworkConnection;
import org.dcm4che2.net.NewThreadExecutor;
import org.dcm4che2.net.PDVInputStream;
import org.dcm4che2.net.Status;
import org.dcm4che2.net.TransferCapability;
import org.dcm4che2.net.service.StorageService;
import org.dcm4che2.net.service.VerificationService;
import org.dcm4che2.io.DicomInputStream;
import pt.ua.dicoogle.DebugManager;
import pt.ua.dicoogle.core.index.IndexEngine;


/**
 * DICOM Storage Service is provided by this class
 * @author Marco Pereira
 */

public class RSIStorage extends StorageService
{
    
    private SOPList list;
    private ServerSettings settings;
    
    private Executor executor = new NewThreadExecutor("RSIStorage");
    private Device device = new Device("RSIStorage");
    private NetworkApplicationEntity nae = new NetworkApplicationEntity();
    private NetworkConnection nc = new NetworkConnection();
    
    private String path;
    private DicomDirCreator dirc;
    
    private int fileBufferSize = 256; 
   
    
    /**
     * 
     * @param Services List of supported SOP Classes
     * @param l list of Supported SOPClasses with supported Transfer Syntax
     * @param s Server Settings for this execution of the storage service
     */
    
    public RSIStorage(String [] Services, SOPList l)
    {
        //just because the call to super must be the first instruction
        super(Services); 
        
            //our configuration format
            list = l;
            settings = ServerSettings.getInstance();

            path = settings.getPath();
            if (path == null) {
                path = "/dev/null";
            }

            device.setNetworkApplicationEntity(nae);
            device.setNetworkConnection(nc);
            nae.setNetworkConnection(nc);

            //we accept assoociations, this is a server
            nae.setAssociationAcceptor(true);
            //we support the VerificationServiceSOP
            nae.register(new VerificationService());
            //and the StorageServiceSOP
            nae.register(this);

            nae.setAETitle(settings.getAE());

            nc.setPort(settings.getStoragePort());

            String[] array = settings.getCAET();
            if (array != null) {
                //nae.setPreferredCallingAETitle(settings.getCAET());
            }

            initTS(Services);       
    }
    /**
     *  Sets the tranfer capability for this execution of the storage service
     *  @param Services Services to be supported
     */
    private void initTS(String [] Services)
    {
        int count = list.getAccepted();
        //System.out.println(count);
        TransferCapability[] tc = new TransferCapability[count + 1];
        String [] Verification = {UID.ImplicitVRLittleEndian, UID.ExplicitVRLittleEndian, UID.ExplicitVRBigEndian};
        String [] TS;
        TransfersStorage local;        

        tc[0] = new TransferCapability(UID.VerificationSOPClass, Verification, TransferCapability.SCP);
        int j = 0;
        for (int i = 0; i < Services.length; i++)
        {
            count = 0;
            local = list.getTS(Services[i]);  
            if (local.getAccepted())
            {
                TS = local.getVerboseTS();
                if(TS != null)
                {                

                    tc[j+1] = new TransferCapability(Services[i], TS, TransferCapability.SCP);
                    j++;
                }                        
            }
        }
        
        nae.setTransferCapability(tc);
    }
      
    @Override
    /**
     * Called when a C-Store Request has been accepted
     * Parameters defined by dcm4che2
     */
    public void cstore(final Association as, final int pcid, DicomObject rq, PDVInputStream dataStream, String tsuid) throws DicomServiceException, IOException
    {
        DebugManager.getInstance().debug(":: Verify Permited AETs @ C-Store Request ");

        boolean permited = false;

        if(ServerSettings.getInstance().getPermitAllAETitles()){
            permited = true;
        }
        else {
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
            
            return;
        } else {
            DebugManager.getInstance().debug("Client association permited: " + as.getCallingAET() + "!");
        }

        final DicomObject rsp = CommandUtils.mkRSP(rq, CommandUtils.SUCCESS);
        onCStoreRQ(as, pcid, rq, dataStream, tsuid, rsp);
        as.writeDimseRSP(pcid, rsp);       
        //onCStoreRSP(as, pcid, rq, dataStream, tsuid, rsp);
    }
    
    @Override
    /**
     * Actually do the job of saving received file on disk
     * on this server with extras such as Lucene indexing
     * and DICOMDIR update
     */
    protected void onCStoreRQ(Association as, int pcid, DicomObject rq, PDVInputStream dataStream, String tsuid, DicomObject rsp) throws IOException, DicomServiceException 
    {  
        try
        {
            
            String cuid = rq.getString(Tag.AffectedSOPClassUID);
            String iuid = rq.getString(Tag.AffectedSOPInstanceUID);
            
            //first we write the file to a temporary location
            BasicDicomObject fmi = new BasicDicomObject();
            fmi.initFileMetaInformation(cuid, iuid, tsuid);            
            File file = File.createTempFile("___________", iuid);            
            FileOutputStream fos = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fos,fileBufferSize);
            DicomOutputStream dos = new DicomOutputStream(bos);
            dos.writeFileMetaInformation(fmi);                        
            dataStream.copyTo(dos);
            dos.close();
            System.out.println(file.getAbsolutePath());
            
            //now we move it to it's final home
            //file = new File(iuid);
            
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis, fileBufferSize);
            DicomInputStream dis = new DicomInputStream(bis);
            DicomObject d = dis.readDicomObject();
            String name = d.getString(Tag.PatientName);
            name = name.replace(' ', '_');
            //System.out.println(name);
            //String extraPath = name+File.separator+d.getString(Tag.StudyID)+File.separator+d.getString(Tag.SeriesNumber)+File.separator;            
            //new File(path+File.separator+extraPath).mkdirs();           
            
            String extraPath= getDirectory(d);
            new File(extraPath).mkdirs(); 
            long time = System.currentTimeMillis();
            String fileStr = getFullPathCache(extraPath, d);

            RenameOperation op = new RenameOperation(file.getAbsolutePath(), fileStr);
            op.start();
            
            //IndexOperation index = new IndexOperation(fileStr);
            //index.start();
            
            
        } catch (IOException e) {
           //System.out.println(e.toString());
           throw new DicomServiceException(rq, Status.ProcessingFailure, e.getMessage());          
         }
    }
    
    
    
    private String getFullPath(DicomObject d)
    {
    
        return getDirectory(d) + File.separator + getBaseName(d);
    
    }
    
    
    private String getFullPathCache(String dir, DicomObject d)
    {
    
        return dir + File.separator + getBaseName(d);
    
    }
    
    
    
    private String getBaseName(DicomObject d)
    {
        String result = "UNKNOWN.dcm";
        String sopInstanceUID = d.getString(Tag.SOPInstanceUID);
        return sopInstanceUID+".dcm";
    }
    
    
    private String getDirectory(DicomObject d)
    {
    
        String result = "UN";
        
        String institutionName = d.getString(Tag.InstitutionName);
        String modality = d.getString(Tag.Modality);
        String studyDate = d.getString(Tag.StudyDate);
        String accessionNumber = d.getString(Tag.AccessionNumber);
        
        institutionName = institutionName.trim();
        institutionName = institutionName.replace(" ", "");
        institutionName = institutionName.replace(".", "");
        institutionName = institutionName.replace("&", "");
        
        if (institutionName==null || institutionName.equals(""))
        {
            institutionName = "UN_IN";
        }
        
        if (modality == null || modality.equals(""))
        {
            modality = "UN_MODALITY";
        }
        
        if (studyDate == null || studyDate.equals(""))
        {
            studyDate = "UN_DATE";
        }
        else
        {
            try
            {
                String year = studyDate.substring(0, 4);
                String month =  studyDate.substring(4, 6);
                String day =  studyDate.substring(6, 8);
                
                studyDate = year + File.separator + month + File.separator + day;
                
            }
            catch(Exception e)
            {
                e.printStackTrace();
                studyDate = "UN_DATE";
            }
        }
        
        if (accessionNumber == null || accessionNumber.equals(""))
        {
            accessionNumber = "UN_ACC";
        }
        
        result = path+File.separator+institutionName+File.separator+modality+File.separator+studyDate+File.separator+accessionNumber;
        
        return result;
        
    }
    
    /*
     * Start the Storage Service 
     * @throws java.io.IOException
     */
    public void start() throws IOException
    {       
        //dirc = new DicomDirCreator(path, "Dicoogle");
        device.startListening(executor);                
    } 
    
    /**
     * Stop the storage service 
     */
    public void stop()
    {
        device.stopListening();
        //dirc.dicomdir_close();
    }   
}
