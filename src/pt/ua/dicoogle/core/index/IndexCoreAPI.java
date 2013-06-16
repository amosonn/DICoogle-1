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

package pt.ua.dicoogle.core.index;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This interface contains all methods to be implemented for Core entities.
 * All Index extensions and IndexEngine should implement it
 *
 * 
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public interface IndexCoreAPI
{
    public  void index(String path, boolean resume);
    public void deleteFile(String path, boolean removeFile);
    public List search(String queryString, List<String> extrafields);
    public void resetIndex(String path);
    /*public void setIndexEffort(int value) ;
    public long remainingFiles() ;*/
}
