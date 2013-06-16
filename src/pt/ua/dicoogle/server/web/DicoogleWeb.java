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


import java.util.logging.Level;
import java.util.logging.Logger;
import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.routing.Router;
import pt.ua.dicoogle.core.ServerSettings;
import pt.ua.dicoogle.webservices.RestDimResource;

/**
 *
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 *
 */
public class DicoogleWeb extends Application {

    static Component component = null;

    public static void startWebservice(){
        try{

            // Adds a new HTTP server listening on customized port.
            // The implementation used is based on java native classes
            // It's not the fastest possible implementation
            // but it suffices for the time being
            // (we can always use other backends at the expense of a few more dependencies)
            component = new Component();
            component.getServers().add(Protocol.HTTP,
                    ServerSettings.getInstance().getWeb().getServerPort());

            // Attaches this application to the server.
            component.getDefaultHost().attach(new DicoogleWeb());

            // And starts the component.
            component.start();
        }
        catch(Exception e){
            //TODO:log this properly...
            System.err.println(e.getMessage());
        }
    }

    public static void stopWebservice(){
        if(component == null) return;

        for(Server server : component.getServers()){
            try {
                server.stop();
            } catch (Exception ex) {
                Logger.getLogger(DicoogleWeb.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        component=null;
    }

     /**
     * Creates a root Restlet that will receive all incoming calls.
     */
    @Override
    public synchronized Restlet createInboundRoot() {
        // Create a router Restlet that routes each call to a
        // new instance of our resources
        Router router = new Router(getContext());

        // Defines routing to resources
        router.attach("/", MainServlet.class);//search resource
        router.attach("/results", MainServlet.class);//results

        return router;
    }
}

