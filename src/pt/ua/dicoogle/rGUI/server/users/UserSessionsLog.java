/*  Copyright   2010 Samuel Campos
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
package pt.ua.dicoogle.rGUI.server.users;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import pt.ua.dicoogle.DebugManager;
import pt.ua.dicoogle.rGUI.server.controllers.Logs;
import pt.ua.dicoogle.sdk.Utils.Platform;

/**
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
public class UserSessionsLog {

    private String sessionsLogFile;
    private static UserSessionsLog instance = null;
    private static Semaphore sem = new Semaphore(1, true);
    private static Semaphore semFile = new Semaphore(1, true);

    public static synchronized UserSessionsLog getInstance() {
        try {
            sem.acquire();
            if (instance == null) {
                instance = new UserSessionsLog();
            }
            sem.release();
        } catch (InterruptedException ex) {
            Logger.getLogger(UserSessions.class.getName()).log(Level.SEVERE, null, ex);
        }
        return instance;
    }

    private UserSessionsLog() {
        sessionsLogFile = Platform.homePath() + "sessions.log";
    }

    public void login(String username, String host, boolean admin) {
        String line;

        if (admin)
            line = host + " - " + username + ": administrator logged";
        else
            line = host + " - " + username + ": user logged";
  
        writeLine(line, true);
    }

    public void loginFailed(String username, String host, boolean admin) {
        String line;

        if (admin) {
            line = host + " - " + username + ": administrator login failed";
        } else {
            line = host + " - " + username + ": user login failed";
        }

        writeLine(line, true);
    }

    public void logout(String username, String host, boolean admin) {
        String line;

        if (admin) {
            line = host + " - " + username + ": administrator logged out";
            writeLine(line, false);
        } else {
            line = host + " - " + username + ": user logged out";
            writeLine(line, true);
        }

        
    }

    /**
     *
     * @param line
     * @param send - indicates whether or not to send to the controller
     */
    private void writeLine(String line, boolean send) {
        BufferedWriter out = null;

        DebugManager.getInstance().debug(line);

        Date now = new Date();
        String tmp = now.toString() + ": " + line + "\n";

        if(send)
            Logs.getInstance().addSessionsLog(tmp);
        
        try {
            semFile.acquire();

            out = new BufferedWriter(new FileWriter(sessionsLogFile, true));
            out.write(tmp);
            out.close();


        } catch (InterruptedException ex) {
            Logger.getLogger(UserSessionsLog.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UserSessionsLog.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(UserSessionsLog.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        semFile.release();
    }

    public void cleanLog() {
        BufferedWriter out = null;
        try {
            semFile.acquire();

            out = new BufferedWriter(new FileWriter(sessionsLogFile));
            out.write("");
            out.close();

        } catch (InterruptedException ex) {
            Logger.getLogger(UserSessionsLog.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UserSessionsLog.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(UserSessionsLog.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        semFile.release();
    }

    public String readLog() {
        String ret = "";

        BufferedReader in = null;
        try {
            String tmp;


            semFile.acquire();
            in = new BufferedReader(new FileReader(sessionsLogFile));

            while ((tmp = in.readLine()) != null) {
                ret = ret + "\n" + tmp;
            }

            in.close();

        } catch (InterruptedException ex) {
            Logger.getLogger(UserSessionsLog.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UserSessionsLog.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                in.close();

            } catch (IOException ex) {
                Logger.getLogger(UserSessionsLog.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        ret = ret + "\n";
        semFile.release();

        return ret;
    }
}
