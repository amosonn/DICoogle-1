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
package pt.ua.dicoogle.core.index.lucene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Carlos Ferreira
 */
public class Settings
{

    private static final String INDEXER_EFFORT = "Indexer Effort";
    private int IndexerEffort = 0;
    private static final String INDEX_ZIP_FILES = "Index Zip Files";
    private boolean indexZIPFiles = true;
    private static final String THUMBNAILS_MATRIX = "Thumbnails matrix";
    private String thumbnailsMatrix = "64";
    private static final String SAVE_THUMBNAILS = "Save Thumbnails";
    private boolean SaveThumbnails;
    private static Settings instance = null;
    private static Semaphore sem = new Semaphore(1, true);

    private Settings()
    {
        IndexerEffort = 0;
    }

    public static synchronized Settings getInstance()
    {
        try
        {
            sem.acquire();
            if (instance == null)
            {
                instance = new Settings();
            }
            sem.release();
        } catch (InterruptedException ex)
        {
            Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
        }
        return instance;
    }

    // Nasty bug fix; no thumbnails references here = null pointers
    public void setDefaultSettings()
    {
        this.IndexerEffort = 0;
        this.indexZIPFiles = true;
        this.thumbnailsMatrix = "64";
        this.SaveThumbnails = false;
    }

    public HashMap<String, Object> getParametersName()
    {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(Settings.INDEXER_EFFORT, this.IndexerEffort);
        parameters.put(Settings.INDEX_ZIP_FILES, this.indexZIPFiles);
        parameters.put(Settings.THUMBNAILS_MATRIX, this.thumbnailsMatrix);
        parameters.put(Settings.SAVE_THUMBNAILS, this.SaveThumbnails);
        return parameters;
    }

    public int getIndexerEffort()
    {
        return IndexerEffort;
    }

    public boolean isIndexZIPFiles()
    {
        return indexZIPFiles;
    }

    public String getThumbnailsMatrix()
    {
        return thumbnailsMatrix;
    }

    public boolean isSaveThumbnails()
    {
        return SaveThumbnails;
    }

    public void setIndexerEffort(int IndexerEffort)
    {
        this.IndexerEffort = IndexerEffort;
    }

    public void setSaveThumbnails(boolean SaveThumbnails)
    {
        this.SaveThumbnails = SaveThumbnails;
    }

    public void setIndexZIPFiles(boolean indexZIPFiles)
    {
        this.indexZIPFiles = indexZIPFiles;
    }

    public void setThumbnailsMatrix(String thumbnailsMatrix)
    {
        this.thumbnailsMatrix = thumbnailsMatrix;
    }


}
