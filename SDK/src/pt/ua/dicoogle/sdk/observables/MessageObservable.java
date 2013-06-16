package pt.ua.dicoogle.sdk.observables;

import java.util.Observable;

/**
 * Observable responsible for the notification of user interfaces about a message received.
 * @author Pedro Bento
 * @author Carlos Ferreira
 */
public class MessageObservable<I> extends Observable
{
    //The last message received.

    private I Message;
    private String address = null;

    /**
     * Constructor that just initialize the message.
     */
    public MessageObservable()
    {
        this.Message = null;
        this.address = null;
    }

    /**
     * Getter of the message.
     * @return the last message received.
     */
    public I getMessage()
    {
        return this.Message;
    }

    public String getAddress()
    {
        return address;
    }


    public I getObject()
    {
        return this.Message;
    }

    /**
     * Setter of the message and consequent notification of all observers.
     * @param Message New message of the observer.
     */
    public void setMessage(I Message, String address)
    {
        this.Message = Message;
        this.address = address;
        this.setChanged();
        this.notifyObservers();
    }
}
