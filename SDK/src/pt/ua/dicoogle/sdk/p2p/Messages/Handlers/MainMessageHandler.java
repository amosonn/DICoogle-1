package pt.ua.dicoogle.sdk.p2p.Messages.Handlers;

//import pt.ua.dicoogle.p2p.jgroups.sockets.MulticastSocketHandler;
import pt.ua.dicoogle.sdk.NetworkPluginAdapter;
import pt.ua.dicoogle.sdk.Utils.SearchResult;
import pt.ua.dicoogle.sdk.observables.ListObservable;
import pt.ua.dicoogle.sdk.p2p.Messages.MessageI;
import pt.ua.dicoogle.sdk.p2p.Messages.MessageType;

/**
 * Class that chooses which message handler will process the message received.
 * @author Carlos Ferreira
 * @author Pedro Bento
 */
public class MainMessageHandler implements MessageHandler
{
    private NetworkPluginAdapter NPA;
    private ListObservable<SearchResult> results = null;

    public MainMessageHandler(NetworkPluginAdapter NPA)
    {
        this.NPA = NPA;
        results = NPA.getSearchResults();
    }

    public void handleMessage(MessageI message, String address)
    {
        MessageHandler handler = null;
        if (message.getType().equals(MessageType.QUERY))
        {
            if (address.equals(this.NPA.getLocalAddress()))
            {
                return;
            }
            handler = new QueryHandler(300, this.NPA);
        }
        if (message.getType().equals(MessageType.QUERY_RESP))
        {
            handler = new QueryResponseHandler(this.NPA, this.results);
        }

        if (message.getType().equals(MessageType.FILE_REQ))
        {
            handler = new FileRequestHandler(this.NPA);
        }
        if (message.getType().equals(MessageType.FILE_RESP))
        {
            handler = new FileResponseHandler("received", this.NPA);
        }
        handler.handleMessage(message, address);
    }
}
