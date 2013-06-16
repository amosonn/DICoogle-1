package pt.ua.dicoogle.sdk.p2p.Messages.Handlers;

import pt.ua.dicoogle.sdk.p2p.Messages.MessageI;

/**
 * Interface that must be implemented by all message handlers
 *
 * @author Carlos Ferreira
 * @author Pedro Bento
 */
public interface MessageHandler
{
    /**
     * Method that handles with the message received.
     * @param message received
     */
    public void handleMessage(MessageI message, String address);
}
