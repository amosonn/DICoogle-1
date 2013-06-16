package pt.ua.dicoogle.sdk.p2p.Messages.Handlers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import pt.ua.dicoogle.sdk.NetworkPluginAdapter;
import pt.ua.dicoogle.sdk.Utils.SearchResult;
import pt.ua.dicoogle.sdk.Utils.TaskRequest;
import pt.ua.dicoogle.sdk.Utils.TaskRequestsConstants;
import pt.ua.dicoogle.sdk.observables.ListObservable;
import pt.ua.dicoogle.sdk.p2p.Messages.Builders.MessageBuilder;
import pt.ua.dicoogle.sdk.p2p.Messages.MessageFields;
import pt.ua.dicoogle.sdk.p2p.Messages.MessageI;
import pt.ua.dicoogle.sdk.p2p.Messages.MessageXML;

/**
 *
 * @author Carlos Ferreira
 * @author Pedro Bento
 */
public class QueryHandler implements MessageHandler, Observer
{

    private int maxResultsPerMessage;
    //private ListObservable<TaskRequest> tasks;
    private NetworkPluginAdapter plugin;

    public QueryHandler(int maxResultsPerMessage, NetworkPluginAdapter plugin)
    {
        this.plugin = plugin;
        this.maxResultsPerMessage = maxResultsPerMessage;
    }

    public void handleMessage(MessageI message, String sender)
    {
        if (!MessageXML.class.isInstance(message))
        {
            return;
        }

        byte[] msg = (byte[]) message.getMessage();

        SAXReader saxReader = new SAXReader();

        Document document = null;
        try
        {
            document = saxReader.read(new ByteArrayInputStream(msg));
        } catch (DocumentException ex)
        {
            ex.printStackTrace(System.out);
        }

        /**
         * Reading the message XML, getting the necessary fields.
         */
        Element root = document.getRootElement();
        String query;
        Element queryNumber = root.element(MessageFields.QUERY_NUMBER);
        List<String> extra = new ArrayList();
        Element queryE = root.element(MessageFields.QUERY);
        /**
         * If the message has not the query element, then it is an invalid message
         * ignore it.
         */
        if (queryE == null)
        {
            return;
        }
        query = queryE.getText();
        List<Element> extrafields = root.elements(MessageFields.EXTRAFIELD);
        if (extrafields == null)
        {
            extrafields = new ArrayList();
        }
        for (Element extrafield : extrafields)
        {
            extra.add(extrafield.getText());
        }

        /**
         * Local search.
         */
        Map<Integer, Object> parameters = new HashMap<Integer, Object>();
        parameters.put(TaskRequestsConstants.P_QUERY, query);
        parameters.put(TaskRequestsConstants.P_EXTRAFIELDS, extra);
        parameters.put(TaskRequestsConstants.P_REQUESTER_ADDRESS, sender);
        parameters.put(TaskRequestsConstants.P_QUERY_NUMBER, queryNumber.getText());

        TaskRequest task = new TaskRequest(TaskRequestsConstants.T_QUERY_LOCALLY,
                this.plugin.getName(), parameters);
        task.addObserver(this);

        this.plugin.getTaskRequestsList().addTask(task);

        //System.out.println("Received Query From outsite: " + query);
        // ArrayList<SearchResult> resultsAll = IndexEngine.getInstance().search(query, extra);
        //



    }

    public void update(Observable o, Object arg)
    {
        if (!TaskRequest.class.isInstance(o))
        {
            return;
        }

        TaskRequest task = (TaskRequest) o;


        Map<Integer, Object> res = task.getResults();
        Object SR = res.get(TaskRequestsConstants.R_SEARCH_RESULTS);
        if ((SR == null) || (!ArrayList.class.isInstance(SR)))
        {
            return;
        }

        ArrayList<SearchResult> resultsAll = (ArrayList<SearchResult>) SR;
        List<SearchResult> results;
        /**
         * Build of the response for the query
         */
        MessageBuilder builder = new MessageBuilder();
        MessageI newMessage = null;
        for (int i = 0; i < resultsAll.size(); i += maxResultsPerMessage)
        {
            int size = maxResultsPerMessage;
            if (i + maxResultsPerMessage > resultsAll.size())
            {
                size = resultsAll.size() - i;
            }

            results = resultsAll.subList(i, i + size);
            try
            {
                newMessage = builder.buildQueryResponse(results, (String) task.getParameters().get(TaskRequestsConstants.P_QUERY_NUMBER), this.plugin.getName());
            } catch (IOException ex)
            {
                Logger.getLogger(QueryHandler.class.getName()).log(Level.SEVERE, null, ex);
            }

            /**
             * Sending the response
             */
            if (newMessage != null)
            {
                this.plugin.send(newMessage, (String) task.getParameters().get(TaskRequestsConstants.P_REQUESTER_ADDRESS));
            }
        }
    }
}
