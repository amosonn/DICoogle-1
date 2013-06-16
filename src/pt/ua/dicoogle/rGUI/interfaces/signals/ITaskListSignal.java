package pt.ua.dicoogle.rGUI.interfaces.signals;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * TaskList
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */

public interface ITaskListSignal extends Remote
{
    public void sendTaskSignal(int flag) throws RemoteException;
}