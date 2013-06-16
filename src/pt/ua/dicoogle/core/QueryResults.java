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
package pt.ua.dicoogle.core;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import pt.ua.dicoogle.core.index.IndexEngine;
import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.sdk.Utils.SearchResult;


/**
 *
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public class QueryResults
{
    List queryResultList;
    List<String> allText = new ArrayList<String>();
    
    public QueryResults(String string)
    {



      /**
       * It is need to autocomplete, so it just get the important fields
       * to search in "free text"
       */
      ArrayList<String> extrafields = new ArrayList<String>();

      extrafields.add("PatientName");
      extrafields.add("PatientID");
      extrafields.add("Modality");



        queryResultList = PluginController.getInstance().localSearch(string, extrafields);
                //IndexEngine.getInstance().search(string,extrafields);

        SearchResult r = null;
        Hashtable extra;
        for(int i = 0; i<queryResultList.size(); i++)
        {
                r=(SearchResult) queryResultList.get(i);
                extra = r.getExtrafields();
                allText.add((String)extra.get("PatientName"));
                allText.add((String)extra.get("Modality"));
                
        }
        
    }

    public List<String> getFields()
    {
        return this.allText ; 
    }

}
