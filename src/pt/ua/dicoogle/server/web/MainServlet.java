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
package pt.ua.dicoogle.server.web;


import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;



import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Post;

import pt.ua.dicoogle.core.index.IndexEngine;
import pt.ua.dicoogle.core.QueryExpressionBuilder;
import pt.ua.dicoogle.sdk.Utils.SearchResult;

/**
 *
 * @author Lu√≠s A. Basti√£o Silva <bastiao@ua.pt>
 */
public class MainServlet extends ServerResource {

    @Get
    public StringRepresentation represent() {
        IndexEngine core = IndexEngine.getInstance();

        String out = "";
        File file = null;
        FileInputStream fisx = null;
        BufferedInputStream bis = null;
        DataInputStream dis = null;
        
        try {
            
            

            // Here BufferedInputStream is added for fast reading.
            bis = new BufferedInputStream(MainServlet.class.getResourceAsStream("/pt/ua/dicoogle/server/web/Main.html"));
            dis = new DataInputStream(bis);

            // dis.available() returns 0 if the file does not have more lines.
            while (dis.available() != 0) {

                // this statement reads the line from the file and print it to
                // the console.


                String tmpWrite = dis.readLine();
                if (tmpWrite.indexOf("IMAGE-LOGO") != -1) {
                    tmpWrite = "<img width='100px' height='100px' src='" 
                           //+ MainServlet.class.getResource("/pt/ua/dicoogle/server/web/logo.jpg").getPath()
                            + "logo.jpg"
                            + "'></img>";
                    tmpWrite = ""; // fixme
                }
                out = out + tmpWrite;
            }

            // dispose all the resources after using them.
            
            bis.close();
            dis.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        
        return new StringRepresentation(out, MediaType.register("text/html", "html"));
    }

    @Post
    public StringRepresentation postS(Representation entity) {

        String out = "";
        ArrayList<String> extrafields = null;
        extrafields = new ArrayList<String>();
        IndexEngine indexer = IndexEngine.getInstance();

        extrafields.add("PatientName");
        extrafields.add("PatientID");
        extrafields.add("Modality");
        extrafields.add("StudyDate");
        extrafields.add("Thumbnail");
        Form form = new Form(entity);
        List<SearchResult> list = null;
        String submit = form.getFirstValue("textfield");
        String query = null;
        if (submit==null || submit.equals("")) {
            query = "*:*";
        } else {
            QueryExpressionBuilder exp = new QueryExpressionBuilder(submit);
            query = exp.getQueryString();
        }
        list = indexer.search(query, extrafields);
        File file = null;
        FileInputStream fisx = null;
        BufferedInputStream bis = null;
        DataInputStream dis = null;
        
        try {

            // Here BufferedInputStream is added for fast reading.
            bis = new BufferedInputStream(MainServlet.class.getResourceAsStream("/pt/ua/dicoogle/server/web/Results.html"));
            dis = new DataInputStream(bis);

            // dis.available() returns 0 if the file does not have more lines.
            while (dis.available() != 0) {

                // this statement reads the line from the file and print it to
                // the console.


                String tmpWrite = dis.readLine();
                if (tmpWrite.indexOf("IMAGE-LOGO") != -1) {
                    tmpWrite = "<img width='100px' height='100px' src='"
                            + //MainServlet.class.getResource("/pt/ua/dicoogle/server/web/logo.jpg").getPath()
                            "logo.jpg"
                            + "'></img>";
                    tmpWrite = "";// fix me
                } else if (tmpWrite.indexOf("RESULTS") != -1) {

                    tmpWrite = "";
                    out = out + "Results from <b>:";

                    out = out + submit;
                    if (list != null) {
                        out = out + "</b><br />Found: " + list.size() + " results";
                        if (list.size() == 0) {
                            break;
                        }
                    } else {
                        out = out + "<br /> No results found.";
                        break;
                    }

                    /**
                     *
                     * <table width="200" border="1">
                    <tr>
                    <td>Patient Name </td>
                    <td>Modality</td>
                    </tr>
                    <tr>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                    </tr>
                    </table>
                     */
                    out = out + " <br ><br> <table class='sample' width='300' border='1'><tr><td>Patient Name</td>"
                            + "<td>Modality</td><td> Study Date</td>"
                            + "</tr>";
                    for (SearchResult s : list) {
                        out = out + "<tr><td>";
                        Hashtable extra = s.getExtrafields();
                        String pp = (String) extra.get("PatientName");
                        if (pp.contains("FELIX")) {
                            pp = "FELIX";
                        }
                        out = out + pp + "</td><td>" + extra.get("Modality") + "</td><td>" + extra.get("StudyDate") + "</td> </tr>";
                    }
                    out = out + "</table>";

                    break;


                }

                out = out + tmpWrite;
            }

            // dispose all the resources after using them.
            
            bis.close();
            dis.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new StringRepresentation(out, MediaType.register("text/html", "html"));




    }
}
