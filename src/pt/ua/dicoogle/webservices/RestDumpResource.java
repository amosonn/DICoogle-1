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
import java.util.Map;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import pt.ua.dicoogle.DebugManager;
import pt.ua.dicoogle.core.index.IndexEngine;
import pt.ua.dicoogle.sdk.Utils.DictionaryAccess;
import pt.ua.dicoogle.sdk.Utils.SearchResult;
import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml3;



/**
 *
 * @author fmvalente
 */


//TODO:add type to file search
public class RestDumpResource extends ServerResource{

    
    
    @Get
    public String represent(){
        IndexEngine core = IndexEngine.getInstance();
        String SOPInstanceUID = getRequest().getResourceRef().getQueryAsForm().getValues("uid");
        if(SOPInstanceUID == null) return null;

        
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
        
        DictionaryAccess da = DictionaryAccess.getInstance() ;
        
        
        
        extrafields = new ArrayList(da.getTagList().keySet());
        
        String query = "SOPInstanceUID:"+SOPInstanceUID;


        //performs the search
        List<SearchResult> queryResultList = core.searchSync(query, extrafields);
        DebugManager.getInstance().debug("#Results: "+ queryResultList.size());
        DebugManager.getInstance().log("[WebService] - #Query: "+ query);
        
        
        if(queryResultList.size()<1) return null;//TODO:Throw exception
        
        SearchResult r = queryResultList.get(0);
        
        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<tags>\n");
        
        Map<String, String> extra = r.getExtrafields();
        for (String k : r.getExtrafields().keySet())
        {
            sb.append("\t<tag name=\"").append(k).append("\">").append(escapeHtml3(extra.get(k).trim())).append("</tag>\n");
        }
        
        sb.append("</tags>");
        

        //and returns an xml version of our dim search
        return sb.toString();
    }
}
