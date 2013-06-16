/*  Copyright   2010 - IEETA
 *
 *  This file is part of Dicoogle.
 *
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
package pt.ua.dicoogle.webservices;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.restlet.data.Disposition;
import org.restlet.data.MediaType;
import org.restlet.representation.FileRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import pt.ua.dicoogle.core.index.IndexEngine;
import pt.ua.dicoogle.sdk.Utils.SearchResult;

/**
 *
 * @author psytek
 *
 * WARNING: This will return *any* file on the host! The best way to correct
 * this will be to change the generated XML in order to produce SOPInstanceUID,
 * and let them act as a tag for the file. This will imply a new query every
 * time we wanto to download a new file, but that's probably fine...
 *
 */
public class RestFileResource extends ServerResource {

    @Get
    public FileRepresentation represent() {
        String SOPInstanceUID = getRequest().getResourceRef().getQueryAsForm().getValues("uid");
        if (SOPInstanceUID == null) {
            return null;
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
        FileRepresentation FRepre =  new FileRepresentation(file, MediaType.register("application/dicom", "dicom medical data file"));
        FRepre.getDisposition().setType(Disposition.TYPE_ATTACHMENT);
        FRepre.getDisposition().setFilename(file.getName());
        
        return FRepre;
    }
}
