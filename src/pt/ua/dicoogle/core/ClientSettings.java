/*  Copyright   2010 Samuel da Costa Campos
 * 
 *  This file is part of Dicoogle.
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

import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import pt.ua.dicoogle.rGUI.server.users.HashService;

/**
 *
 * @author Samuel da Costa Campos <samuelcampos@ua.pt>
 * @see XMLClientSupport
 *
 */
public class ClientSettings {

    private String tempFilesDir;
    private String externalViewer;
    private String defaultServerHost;
    private int defaultServerPort;
    private String defaultUserName;
    private String defaultPassword;
    private boolean autoConnect;
    

    private static ClientSettings instance = null;
    private static Semaphore sem = new Semaphore(1, true);

    public static synchronized ClientSettings getInstance()
    {
        try
        {
            sem.acquire();
            if (instance == null)
            {
                instance = new ClientSettings();
            }
            sem.release();
        }
        catch (InterruptedException ex)
        {
            Logger.getLogger(ServerSettings.class.getName()).log(Level.SEVERE, null, ex);
        }
        return instance;
    }

    private ClientSettings(){
        tempFilesDir = "";
        externalViewer = "";
        defaultServerHost = "";
        defaultServerPort = 0;
        defaultUserName = "";
        defaultPassword = "";
    }

    public void setDefaultSettings()
    {
        tempFilesDir = System.getProperty("java.io.tmpdir");
        externalViewer = "";
        defaultServerHost = "localhost";
        defaultServerPort = 9014;
        defaultUserName = "dicoogle";
        defaultPassword = HashService.getSHA1Hash("dicoogle");
    }

    public void setExtV(String EV)
    {
        externalViewer = EV;
    }

    public String getExtV()
    {
        return externalViewer;
    }

    /**
     * @return the defaultServerHost
     */
    public String getDefaultServerHost() {
        return defaultServerHost;
    }

    /**
     * @param defaultServerHost the defaultServerHost to set
     */
    public void setDefaultServerHost(String defaultServerHost) {
        this.defaultServerHost = defaultServerHost;
    }

    /**
     * @return the defaultServerPort
     */
    public int getDefaultServerPort() {
        return defaultServerPort;
    }

    /**
     * @param defaultServerPort the defaultServerPort to set
     */
    public void setDefaultServerPort(int defaultServerPort) {
        this.defaultServerPort = defaultServerPort;
    }

    /**
     * @return the tempFilesDir
     */
    public String getTempFilesDir() {
        return tempFilesDir;
    }

    /**
     * @param tempFilesDir the tempFilesDir to set
     */
    public void setTempFilesDir(String tempFilesPath) {
        this.tempFilesDir = tempFilesPath;
    }

    /**
     * @return the defaultUserName
     */
    public String getDefaultUserName() {
        return defaultUserName;
    }

    /**
     * @param defaultUserName the defaultUserName to set
     */
    public void setDefaultUserName(String defaultUserName) {
        this.defaultUserName = defaultUserName;
    }

    /**
     *
     * @return the default password
     */
    public String getDefaultPassword(){
       return defaultPassword;
    }

    /**
     *
     * @param passHash - new default password
     */
    public void setDefaultPassword(String passHash){
        defaultPassword = passHash;
    }

    public boolean getAutoConnect(){
        return autoConnect;
    }

    public void setAutoConnect(boolean value){
        autoConnect = value;
    }


}
