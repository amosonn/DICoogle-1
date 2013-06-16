/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.ua.dicoogle.webservices;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.restlet.data.Disposition;
import org.restlet.data.MediaType;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import pt.ua.dicoogle.core.index.IndexEngine;
import pt.ua.dicoogle.rGUI.client.UIHelper.Dicom2JPEG;
import pt.ua.dicoogle.sdk.Utils.SearchResult;

/**
 *
 * @author samuelcampos
 */
public class RestImageResource extends ServerResource {

    @Get
    public Representation represent() {
        String SOPInstanceUID = getRequest().getResourceRef().getQueryAsForm().getValues("uid");
        if (SOPInstanceUID == null) {
            return null;
        }

        String heightString = getRequest().getResourceRef().getQueryAsForm().getValues("height");
        int height = 0;

        if (heightString != null) {
            try {
                height = Integer.valueOf(heightString);
            } catch (NumberFormatException ex) {
            };
        }

        IndexEngine core = IndexEngine.getInstance();
        ArrayList<String> extra = new ArrayList<String>();
        extra.add("SOPInstanceUID");
        String query = "SOPInstanceUID:" + SOPInstanceUID;
        List<SearchResult> queryResultList = core.search(query, extra);
        if (queryResultList.size() < 1) {
            return null;//TODO:Throw exception
        }
        for (SearchResult r : queryResultList) {
            Logger.getLogger(RestDimResource.class.getName()).severe(r.getOrigin());
        }
        File file = new File(queryResultList.get(0).getOrigin());

        return new DynamicFileRepresentation(MediaType.IMAGE_JPEG, file, height);
    }

    public class DynamicFileRepresentation extends OutputRepresentation {

        private File dicomImage;
        private int height;

        public DynamicFileRepresentation(MediaType mediaType, File dicomImage, int height) {
            super(mediaType);
            
            this.dicomImage = dicomImage;
            this.height = height;
        }

        @Override
        public void write(OutputStream outputStream) throws IOException {
            Dicom2JPEG.convertDicom2Jpeg(dicomImage, outputStream, height);
        }
    }
}
