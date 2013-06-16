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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import pt.ua.dicoogle.DebugManager;
import pt.ua.dicoogle.core.dim.DIMGeneric;
import pt.ua.dicoogle.core.index.IndexEngine;
import pt.ua.dicoogle.core.QueryExpressionBuilder;
import pt.ua.dicoogle.sdk.Utils.DictionaryAccess;

/**
 *
 * @author fmvalente
 */


//TODO:add type to file search
public class RestDimResource extends ServerResource{

    
    
    @Get
    public String represent(){
        IndexEngine core = IndexEngine.getInstance();
        String search = getRequest().getResourceRef().getQueryAsForm().getValues("q");
        String advSearch = getRequest().getResourceRef().getQueryAsForm().getValues("advq");
        String type = getRequest().getResourceRef().getQueryAsForm().getValues("type");

        if(advSearch == null){
            //prepares query
            if(search == null) search="";
            else if(search.equals("null")) search = "";

            if (search.equals(""))
                search = "*:*";
            else{
                QueryExpressionBuilder q = new QueryExpressionBuilder(search);
                search = q.getQueryString();
            }
        }
        else{
            search = advSearch;
        }
        System.err.println("ADVSEARCH: "+advSearch);
        System.err.println("FINAL SEARCH QUERY: "+search);

        
        ArrayList extrafields = new ArrayList();
        //attaches the required extrafields
        
        extrafields.add("PatientName");
        extrafields.add("PatientID");
        extrafields.add("Modality");
        extrafields.add("StudyDate");
        extrafields.add("SeriesInstanceUID");
        extrafields.add("StudyID");
        extrafields.add("StudyInstanceUID");
        extrafields.add("Thumbnail");
        extrafields.add("SOPInstanceUID");

        //performs the search
        List queryResultList = core.searchSync(search, extrafields);
        DebugManager.getInstance().debug("#Results: "+ queryResultList.size());
        DebugManager.getInstance().log("[WebService] - #Query: "+ search);

        //constructs our xml data struct
        DIMGeneric dim = null;
        try{
            dim = new DIMGeneric(queryResultList);
        }
        catch (Exception ex) {
            Logger.getLogger(RestDimResource.class.getName()).log(Level.SEVERE, null, ex);
        }

        //and returns an xml version of our dim search
        return dim.getXML();
    }
}
