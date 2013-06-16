/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pt.ua.dicoogle.common.index;

import pt.ua.dicoogle.core.index.IndexEngine;

/**
 *
 * @author samuelcampos
 */
public class ReIndexTask extends Task {

    public ReIndexTask(String path)
    {
        super(path);
    }

    @Override
    public void run() {
        IndexEngine.getInstance().deleteFile(getPath(), false );
        IndexEngine.getInstance().index(getPath(), false);
    }

}
