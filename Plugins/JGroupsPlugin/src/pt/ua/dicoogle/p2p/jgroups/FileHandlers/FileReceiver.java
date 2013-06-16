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
package pt.ua.dicoogle.p2p.jgroups.FileHandlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import pt.ua.dicoogle.p2p.jgroups.Utils.FileFragmentMessage;

/**
 *
 * @author Carlos Ferreira
 */
public class FileReceiver
{

    public FileReceiver()
    {
    }

    public void handleFileFragment(FileFragmentMessage message, String filePath)
    {
        RandomAccessFile RAF;
        File file = new File(filePath);
        try
        {
            RAF = new RandomAccessFile(file, "rw");
            RAF.seek(message.getOffset());
            RAF.write(message.getMessage());
                RAF.close();

        } catch (IOException ex)
        {
            Logger.getLogger(FileReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
