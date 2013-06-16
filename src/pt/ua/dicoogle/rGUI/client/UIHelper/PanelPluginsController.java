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

package pt.ua.dicoogle.rGUI.client.UIHelper;

import java.io.File;
import java.util.Collection;
import net.xeoh.plugins.base.PluginManager;
import net.xeoh.plugins.base.impl.PluginManagerFactory;
import net.xeoh.plugins.base.util.PluginManagerUtil;
import pt.ua.dicoogle.sdk.Utils.PluginPanel;

/**
 *
 * @author Carlos Ferreira
 */
public class PanelPluginsController
{
    private Collection<PluginPanel> panels;
    private static PanelPluginsController instance = null;

    private PanelPluginsController()
    {
        PluginManager pm = PluginManagerFactory.createPluginManager();
        pm.addPluginsFrom(new File("pluginClasses/").toURI());
        PluginManagerUtil pmu = new PluginManagerUtil(pm);
        this.panels = pmu.getPlugins(PluginPanel.class);
    }

    public static synchronized PanelPluginsController getInstance()
    {
        if(instance == null)
        {
            instance = new PanelPluginsController();
        }
        return instance;
    }

    public Collection<PluginPanel> getPanels()
    {
        return this.panels;
    }


}
