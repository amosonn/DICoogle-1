/*  Copyright   2010 - IEETA
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
package pt.ua.dicoogle;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import pt.ua.dicoogle.rGUI.server.controllers.Logs;
import pt.ua.dicoogle.sdk.Utils.Platform;

/**
 *
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public class DebugManager 
{
    
   private static DebugManager instance = new DebugManager();
   private boolean debug = true ;
   private String logFile = Platform.homePath() + "log.txt";
   
   private DebugManager()
   {
        debug = true ;

        File log = new File(logFile);
        if (!log.exists())
        {
            FileWriter fstream = null;
            try {
                fstream = new FileWriter(logFile);
                BufferedWriter out = new BufferedWriter(fstream);
                out.close();
            } catch (IOException ex)
            {

            } finally {
                try {
                    fstream.close();
                } catch (IOException ex) {
            
                }
            }
        }
            
                

        
   }
   
   public static DebugManager getInstance()
   {
       return instance;
   }

   public void log(String msg)
   {
        log(msg, 0);
   }
   public void log(String msg, int level)
   {
        FileWriter fstream = null;

        try {
            fstream = new FileWriter(logFile, true);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(getDateTime() + ":: " + msg+"\n");
            out.close();
        } catch (IOException ex)
        {
        } finally {
            try {
                fstream.close();
            } catch (IOException ex) {

            }
        }

   }
   
   public void debug(String message)
   {
        if (isDebug())
            System.out.println(message);
   }

    /**
     * @return the debug
     */
    public boolean isDebug()
    {
        return debug;
    }

    /**
     * @param debug the debug to set
     */
    public void setDebug(boolean debug)
    {
        this.debug = debug;
    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

}
