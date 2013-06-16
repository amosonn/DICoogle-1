package pt.ua.dicoogle.sdk.p2p.Messages;

/**
 *
 * @author Carlos Ferreira
 */
public class FileMessage extends ObjMessage<byte[]>
{
    private String filename;

    public FileMessage(byte[] obj, String Type, String Filename)
    {
        super(obj, Type);
        this.filename = Filename;
    }

    public String getFilename()
    {
        return filename;
    }    
}
