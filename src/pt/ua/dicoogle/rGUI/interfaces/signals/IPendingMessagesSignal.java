/*  Copyright   2010 Samuel Campos
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

package pt.ua.dicoogle.rGUI.interfaces.signals;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Pending messages signal interface
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
public interface IPendingMessagesSignal extends Remote {

    /**
     *
     * @param flag
     *              0 - File Already Indexed
     * @throws RemoteException
     */
    public void sendPendingMessagesSignal(int flag) throws RemoteException;

}
