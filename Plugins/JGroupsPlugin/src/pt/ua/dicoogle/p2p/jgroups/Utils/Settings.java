/*  Copyright   2011 Carlos Ferreira
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
package pt.ua.dicoogle.p2p.jgroups.Utils;

/**
 *
 * @author Carlos Ferreira
 */
public class Settings
{
    private static Settings instance = null;
    
    private String bindAddress = null;
    private String clusterName = "dicoogle";

    private Settings()
    {
    }

    public synchronized static Settings getInstance()
    {
        if (instance == null)
        {
            instance = new Settings();
        }
        return instance;
    }

    public String getBindAddress()
    {
        return bindAddress;
    }

    public void setBindAddress(String bindAddress)
    {
        this.bindAddress = bindAddress;
    }

    public String getClusterName()
    {
        return clusterName;
    }

    public void setClusterName(String clusterName)
    {
        this.clusterName = clusterName;
    }
}
