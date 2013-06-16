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
package pt.ua.dicoogle.sdk.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.filechooser.FileSystemView;

/**
 * Detect which platform is running
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public class Platform
{

    public  enum MODE {PORTABLE, BUNDLE};

    public static MODE getMode()
    {
        MODE mode = MODE.PORTABLE;
        File currentDir = new File (".");
        File bundle = new File("BUNDLE.here");
        File windows = new File("Java Launcher.exe");
        File mac = new File("Dicoogle.app");
        if (currentDir.getAbsolutePath().contains("Dicoogle.app")|| bundle.exists()||mac.exists()||windows.exists())
        {
            mode = MODE.BUNDLE;
        }
        return mode;
    }

    public static String homePath()
    {

        String homePath = "";
        MODE mode = getMode();
        if (mode==MODE.BUNDLE)
        {
            homePath = getHomeDirectory().getAbsolutePath() + File.separator + ".dicoogle" + File.separator;
        }
        return homePath;
    }

    public static File getHomeDirectory()
    {

        File result = null;
        if (System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1)
        {
            try {
                Process p = Runtime.getRuntime().exec("cmd /c echo %HOMEDRIVE%%HOMEPATH%");
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                try {
                    p.waitFor();
                } catch (InterruptedException e) {
                    throw new IOException("Interrupted: " + e.getMessage());
                }
                result = new File(reader.readLine());
            } catch (IOException ex) {
                Logger.getLogger(Platform.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else
        {
            result = FileSystemView.getFileSystemView().getHomeDirectory();
        }
        return result ;
	
    }


}
