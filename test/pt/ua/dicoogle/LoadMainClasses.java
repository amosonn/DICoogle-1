/*  Copyright   2012 - IEETA
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

import pt.ua.dicoogle.plugins.PluginController;

/**
 *
 * @author bastiao
 */
public class LoadMainClasses 
{

    
    public void loadClasses()
    {
        Main.LaunchDicoogle();
    
    }
    
    
    public void loadPlugins()
    {
        PluginController PController = PluginController.getInstance();
        pt.ua.dicoogle.rGUI.server.controllers.ControlServices.getInstance();
        
    }
    
    
    public void shutdown()
    {
    
    }
    
}
