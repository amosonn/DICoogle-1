package pt.ua.dicoogle.sdk.observables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Observable;

/**
 * Observable responsible for the notification of user interfaces about any change in the
 * list of members of the view.
 *
 * @author Carlos Ferreira
 * @author Pedro Bento
 */
public class ListObservable<type> extends Observable
{
    //array of the members of the view.
    private Collection<type> array;

    /**
     * Constructor of the class. It does nothing.
     */
    public ListObservable()
    {
        this.array = Collections.synchronizedCollection(new ArrayList<type>());
    }

    /**
     * Setter of the memberlist, it receives a vector and puts all members
     * in the array.
     * After that it notifies all observers.
     * @param members
     */
    public synchronized void setArray(Collection<type> vec)
    {
        //initialization of the memberlist
        this.array.clear();
        this.array.addAll(vec);
        
        //notification of the observers.
        this.setChanged();
        this.notifyObservers();
    }

    public synchronized void addAll(Collection list)
    {
        this.array.addAll(list);
        this.setChanged();
        this.notifyObservers();
    }

    public synchronized void add(type object)
    {
        this.array.add(object);
        this.setChanged();
        this.notifyObservers();
    }

    /**
     * Getter of copy of the array list of the members
     * @return the list of the members of the view
     */
    public ArrayList getArray()
    {
        ArrayList newArray = new ArrayList();
        newArray.addAll(this.array);
        return newArray;
    }
    public void resetArray()
    {
        this.array.clear();
        //notification of the observers.
        this.setChanged();
        this.notifyObservers();

    }

    @Override
    public String toString()
    {
        String string = "[ ";
        for(type element: array)
        {
            string += element.toString() + "  ";
        }
        string += "]";
        return string;
    }
}
