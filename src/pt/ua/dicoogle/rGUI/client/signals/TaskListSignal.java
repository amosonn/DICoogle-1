package pt.ua.dicoogle.rGUI.client.signals;

import java.rmi.RemoteException;
import pt.ua.dicoogle.rGUI.client.windows.TaskList;
import pt.ua.dicoogle.rGUI.interfaces.signals.ITaskListSignal;

/**
 *
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public class TaskListSignal implements ITaskListSignal
{
    TaskList taskListWindow = null;

    public TaskListSignal(TaskList tasks)
    {
        taskListWindow = tasks;
    }

    @Override
    public synchronized void sendTaskSignal(int flag) throws RemoteException
    {
        switch(flag){
            case 0:
                taskListWindow.getTaskList();
                break;
            
        }
    }
}
