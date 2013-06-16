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


package pt.ua.dicoogle.rGUI.utils;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import pt.ua.dicoogle.DebugManager;
import pt.ua.dicoogle.Main;
import pt.ua.dicoogle.sdk.Utils.Platform;

/**
 *
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public class KeysManager
{
    public static String getServerKeyPath()
    {
        File test = null;
        String keyStorePath = Main.class.getResource("/pt/ua/dicoogle/config/Server_Keystore").getPath();

        test = new File(keyStorePath);
        if (!test.exists()) {
            keyStorePath = "./Server_Keystore";
        }

        test = new File(keyStorePath);
        if (!test.exists() || !test.canRead())
        {
            //Last shot
            InputStream stream = Main.class.getResourceAsStream("/pt/ua/dicoogle/config/Server_Keystore");
            if (stream!=null)
            {
                keyStorePath=Platform.homePath()+"Server_Keystore";
                try
                {
                    DataOutputStream out = new DataOutputStream(
                            new BufferedOutputStream(
                            new FileOutputStream(keyStorePath)));
                    int c;
                    while((c = stream.read()) != -1)
                    {
                            out.writeByte(c);
                    }
                    stream.close();
                    out.close();

                }
                catch(IOException e)
                {
                        System.err.println("Error Writing/Reading Streams.");
                }
            }
            else
            {
                DebugManager.getInstance().debug("Missing the KeyStore file that contains the SSL keys of server");
                System.exit(-2);
            }
        }
        return keyStorePath;
}


    public static String getClientKeyPath()
    {
        File test = null;
        String TrustStorePath = Main.class.getResource("/pt/ua/dicoogle/config/Client_Truststore").getPath();
        
        test = new File(TrustStorePath);
        if (!test.exists())
        {
            TrustStorePath = "./Client_Truststore";
        }

        test = new File(TrustStorePath);
        if (!test.exists() || !test.canRead()) 
        {

            //Last shot
            InputStream stream = Main.class.getResourceAsStream("/pt/ua/dicoogle/config/Client_Truststore");
            if (stream!=null)
            {
                TrustStorePath=Platform.homePath()+"Client_Truststore";
                try
                {
                    DataOutputStream out = new DataOutputStream(
                            new BufferedOutputStream(
                            new FileOutputStream(TrustStorePath)));
                    int c;
                    while((c = stream.read()) != -1)
                    {
                            out.writeByte(c);
                    }
                    stream.close();
                    out.close();

                }
                catch(IOException e)
                {
                        System.err.println("Error Writing/Reading Streams.");
                }
            }
            else
            {
                DebugManager.getInstance().debug("Missing the TrustStore file that confirms the server certificate");
                System.exit(-3);
            }
        }

        return TrustStorePath;
    }
}
