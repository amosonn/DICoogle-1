package pt.ua.dicoogle.sdk.p2p.Messages;

/**
 * Object to be sent
 * @author Carlos Ferreira
 * @author Pedro Bento
 */
public class ObjMessage<type> implements MessageI<type>
{
    private type message;
    private String Type;
    
    public ObjMessage(type message, String Type)
    {
        this.message = message;
        this.Type = Type;
    }

    public type getMessage()
    {
        return this.message;
    }

    public String getType()
    {
        return this.Type;
    }
}
