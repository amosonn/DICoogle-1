package pt.ua.dicoogle.sdk.p2p.Messages.Builders;

import pt.ua.dicoogle.sdk.p2p.Messages.MessageFields;
import pt.ua.dicoogle.sdk.p2p.Messages.MessageI;
import pt.ua.dicoogle.sdk.p2p.Messages.MessageType;
import pt.ua.dicoogle.sdk.p2p.Messages.MessageXML;
import pt.ua.dicoogle.sdk.p2p.Messages.SearchResultFields;
import pt.ua.dicoogle.sdk.Utils.QueryNumber;
import pt.ua.dicoogle.sdk.Utils.SearchResult;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 *
 * @author Carlos Ferreira
 * @author Pedro Bento
 */
public class MessageBuilder
{
    public MessageI buildQueryMessage(String query, List<String> extrafields, String pluginName, Integer queryNumber) throws IOException
    {
        DocumentFactory dfactory = new DocumentFactory();
        Document document = dfactory.createDocument("UTF-8");

        /**
         * Creation of the root element of the document
         */
        Element root = document.addElement(MessageFields.MESSAGE);

        Element temp;
        temp = root.addElement(MessageFields.MESSAGE_TYPE);
        temp.setText(MessageType.QUERY);

        temp = root.addElement(MessageFields.QUERY_NUMBER);

        temp.setText(queryNumber.toString());

        temp = root.addElement(MessageFields.QUERY);
        temp.setText(query);

        for (String extrafield : extrafields)
        {
            temp = root.addElement(MessageFields.EXTRAFIELD);
            temp.setText(extrafield);
        }

        OutputFormat format = new OutputFormat("  ", true, "UTF-8");
        ByteArrayOutputStream a = new ByteArrayOutputStream();

        XMLWriter output = null;
        try
        {
            output = new XMLWriter(a, format);
        } catch (UnsupportedEncodingException ex)
        {
            ex.printStackTrace(System.out);
        }

        output.write(document);

        output.close();

        //just for tests
        //System.out.println(a.toString());

        return new MessageXML(a.toByteArray());
    }

    public MessageI buildQueryResponse(List<SearchResult> results, String queryNumber, String pluginName) throws IOException
    {
        DocumentFactory dfactory = new DocumentFactory();
        Document document = dfactory.createDocument("UTF-8");
        Element root = document.addElement(MessageFields.MESSAGE);
        Element temp, temp2, temp3;
        temp = root.addElement(MessageFields.MESSAGE_TYPE);
        temp.setText(MessageType.QUERY_RESP);

        temp = root.addElement(MessageFields.QUERY_NUMBER);
        temp.setText(queryNumber);
        temp = root.addElement(MessageFields.SEARCH_RESULTS);
        for (SearchResult result : results)
        {
            temp2 = temp.addElement(MessageFields.SEARCH_RESULT);

            temp3 = temp2.addElement(SearchResultFields.FILE_NAME);
            temp3.setText(result.getFileName());

            temp3 = temp2.addElement(SearchResultFields.FILE_HASH);
            temp3.setText(result.getFileHash());
            //temp3 = temp2.addElement(SearchResultFields.FILE_PATH);
            //temp3.setText(result.getPath());
            temp3 = temp2.addElement(SearchResultFields.FILE_SIZE);
            temp3.setText(result.getSize());

            temp3 = temp2.addElement(SearchResultFields.EXTRAFIELDS);
            Hashtable<String, String> extrafields = result.getExtrafields();
            Set<String> keys = extrafields.keySet();
            for (String key : keys)
            {
                temp2 = temp3.addElement(key);
                /** FIXME: Probably need to replace other fields like \14 ? */
                temp2.setText(extrafields.get(key).replaceAll("\0", " "));
            }
        }
        OutputFormat format = new OutputFormat("  ", true, "UTF-8");
        ByteArrayOutputStream a = new ByteArrayOutputStream();

        XMLWriter output = null;
        try
        {
            output = new XMLWriter(a, format);
        } catch (UnsupportedEncodingException ex)
        {
            ex.printStackTrace(System.out);
        }

        output.write(document);

        output.close();
        return new MessageXML(a.toByteArray());
    }

    public MessageI buildFileRequest(String filename, String fileHash, String pluginName) throws IOException
    {
        DocumentFactory dfactory = new DocumentFactory();
        Document document = dfactory.createDocument("UTF-8");
        Element root = document.addElement(MessageFields.MESSAGE);
        Element temp;
        temp = root.addElement(MessageFields.MESSAGE_TYPE);
        temp.setText(MessageType.FILE_REQ);
        temp = root.addElement(MessageFields.FILE_REQUESTED);
        Element temp2 = temp.addElement(SearchResultFields.FILE_NAME);
        temp2.setText(filename);
        temp2 = temp.addElement(SearchResultFields.FILE_HASH);
        temp2.setText(fileHash);
        OutputFormat format = new OutputFormat("  ", true, "UTF-8");
        ByteArrayOutputStream a = new ByteArrayOutputStream();

        XMLWriter output = null;
        try
        {
            output = new XMLWriter(a, format);
        } catch (UnsupportedEncodingException ex)
        {
            ex.printStackTrace(System.out);
        }

        output.write(document);

        output.close();
        return new MessageXML(a.toByteArray());
    }
}
