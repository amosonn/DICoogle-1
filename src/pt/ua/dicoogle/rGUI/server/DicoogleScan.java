/*  Copyright   
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

package pt.ua.dicoogle.rGUI.server;

import java.io.File;
import java.io.IOException;
import org.dcm4che2.io.DicomInputStream;
import pt.ua.dicoogle.core.index.IndexEngine;

/**
 *
 * @author carloscosta
 */
public class DicoogleScan {
    private File path = null;

    public DicoogleScan(File path){
        this.path = path;
    }

    public DicoogleScan(String path){
        this(new File(path));
    }
    public void scan(boolean resume){
        scan(path, resume);
    }

    private void scan(File path, boolean resume){
        if (path == null)
                return;

            IndexEngine core = IndexEngine.getInstance();

            //long time = System.nanoTime();

            // O Método FileIndexer.index já a indexação recursiva de files DICOM


            core.indexQueue(path.getAbsolutePath(), resume);

            //System.out.println("\n***Directory Index Time (miliseg)***"
            //                 + "\nStart Time: " + time + "\nEnd Time:" + System.nanoTime()
            //                 + "\nDelta Time: " + ((System.nanoTime() - time)/1000000L));

    }

    public static boolean isDicom(File file) throws IOException
    {
        boolean result = false;
        DicomInputStream dis = new DicomInputStream(file);
        if (dis.getTransferSyntax() != null)
            result = true;
        return result;
    }

}