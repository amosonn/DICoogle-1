package pt.ua.dicoogle.sdk.p2p.Messages;

import java.io.ByteArrayInputStream;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 *
 * @author Carlos Ferreira
 * @author Pedro Bento
 */
public class MessageXML implements MessageI<byte[]>
{
    private String Message;

    public MessageXML(byte[] message)
    {
        this.Message = new String(message);
    }

    public String getType()
    {
        SAXReader saxReader = new SAXReader();
        ByteArrayInputStream input = new ByteArrayInputStream(Message.getBytes());
        Document document = null;
        try
        {
            document = saxReader.read(input);
        } catch (DocumentException ex)
        {
            ex.printStackTrace(System.out);
        }
        Element root = document.getRootElement();
        Element tmp = root.element(MessageFields.MESSAGE_TYPE);
        return tmp.getText();
    }

    public byte[] getMessage()
    {
        return this.Message.getBytes();
    }

    @Override
    public String toString()
    {
        return this.Message;
    }
}
