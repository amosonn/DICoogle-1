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

package pt.ua.dicoogle.common.index;

import pt.ua.dicoogle.core.index.IndexEngine;

/**
 *
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public class IndexTask extends Task
{
    
    boolean resume;
    public IndexTask(String path, boolean resume)
    {
        super(path);
        this.resume = resume;
    }

    @Override
    public void run()
    {
        IndexEngine.getInstance().index(getPath(), resume);
    }
}
