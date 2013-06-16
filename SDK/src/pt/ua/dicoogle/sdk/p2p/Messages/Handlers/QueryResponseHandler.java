package pt.ua.dicoogle.sdk.p2p.Messages.Handlers;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import pt.ua.dicoogle.sdk.NetworkPluginAdapter;
import pt.ua.dicoogle.sdk.Utils.SearchResult;
import pt.ua.dicoogle.sdk.observables.ListObservable;
import pt.ua.dicoogle.sdk.p2p.Messages.MessageFields;
import pt.ua.dicoogle.sdk.p2p.Messages.MessageI;
import pt.ua.dicoogle.sdk.p2p.Messages.MessageXML;
import pt.ua.dicoogle.sdk.p2p.Messages.SearchResultFields;
import pt.ua.dicoogle.sdk.Utils.QueryNumber;

/**
 *
 * @author Carlos Ferreira
 * @author Pedro Bento
 */
public class QueryResponseHandler implements MessageHandler
{

    private NetworkPluginAdapter NPA;
    private ListObservable<SearchResult> results;

    public QueryResponseHandler(NetworkPluginAdapter NPA, ListObservable<SearchResult> results)
    {
        this.NPA = NPA;
        this.results = results;
    }

    public void handleMessage(MessageI message, String sender)
    {
        if (!MessageXML.class.isInstance(message))
        {
            return;
        }

        MessageXML msg = (MessageXML) message;

        /**
         * leitura do pacote.
         */
        byte[] xml = msg.getMessage();
        SAXReader saxReader = new SAXReader();

        Document document = null;
        try
        {
            document = saxReader.read(new ByteArrayInputStream(xml));
        } catch (DocumentException ex)
        {
            ex.printStackTrace(System.out);
        }

        Element root = document.getRootElement();

        /**
         * Verification of the validity of the message
         */
        if (!root.getName().equals(MessageFields.MESSAGE))
        {
            return;
        }

        Element tmp = root.element(MessageFields.QUERY_NUMBER);
        if (tmp == null)
        {
            return;
        }

        Integer queryNumber = null;
        try
        {
            queryNumber = Integer.parseInt(tmp.getText());
        } catch (Exception ex)
        {
            return;
        }

        /**
         * if the queryNumber is not the most recent query's number, then ignore the message
         * It is the response for a search deprecated.
         * This will be checked again, nevertheless here it is checked to save time 
         * with avoidable XML processing in case of the deprecation of the message received
         */
        if (queryNumber != QueryNumber.getInstance().getQueryNumber())
        {
            return;
        }

        tmp = root.element(MessageFields.SEARCH_RESULTS);
        List<Element> Results = tmp.elements(MessageFields.SEARCH_RESULT);
        List<SearchResult> res = new ArrayList<SearchResult>();
        Element tmp2;
        for (Element result : Results)
        {
            String Filename = null, FileHash = null, FileSize = null;
            tmp2 = result.element(SearchResultFields.FILE_NAME);
            if (tmp2 == null)
            {
                return;
            }
            Filename = tmp2.getText();

            tmp2 = result.element(SearchResultFields.FILE_HASH);
            if (tmp2 == null)
            {
                return;
            }
            FileHash = tmp2.getText();

            tmp2 = result.element(SearchResultFields.FILE_SIZE);
            if (tmp2 == null)
            {
                return;
            }
            FileSize = tmp2.getText();

            tmp2 = result.element(SearchResultFields.EXTRAFIELDS);
            if (tmp2 == null)
            {
                return;
            }
            List<Element> fields = tmp2.elements();
            Hashtable<String, String> Extrafields = new Hashtable();
            for (Element field : fields)
            {
                Extrafields.put(field.getName(), field.getText());
            }
            res.add(new SearchResult(FileHash, Filename, sender, FileSize, Extrafields, this.NPA.getName()));
        }
        synchronized (this.results) // monitor to queryNumber
        {
            if (queryNumber != QueryNumber.getInstance().getQueryNumber())
            {
                //System.out.println("different QueryNumber");
                return;
            }
            this.results.resetArray();
            this.results.addAll(res);
            //System.out.println(this.results.getArray().size() + "results added to the observable");
        }

    }
}
