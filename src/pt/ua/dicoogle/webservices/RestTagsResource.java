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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import pt.ua.dicoogle.sdk.Utils.DictionaryAccess;
import pt.ua.dicoogle.webservices.elements.JaxbStrList;

/**
 *
 * @author samuelcampos
 */
public class RestTagsResource extends ServerResource {

    @Get
    public Representation representXML() {
        StringRepresentation sr;
        
        Hashtable<String, Integer> tags = DictionaryAccess.getInstance().getTagList();

        ArrayList<String> list = new ArrayList<String>();

        for (String tagName : tags.keySet()) {
            list.add(tagName);
        }

        Collections.sort(list);

        JaxbStrList jaxbList = new JaxbStrList(list);

        StringWriter sw = new StringWriter();

        try {
            JAXBContext context = JAXBContext.newInstance(JaxbStrList.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            m.marshal(jaxbList, sw);

            sr = new StringRepresentation(sw.toString(), MediaType.APPLICATION_XML);

        } catch (Exception ex) {
            Logger.getLogger(RestTagsResource.class.getName()).log(Level.SEVERE, null, ex);
            
            sr = new StringRepresentation("", MediaType.APPLICATION_XML);
        }

        return sr;
    }
}
