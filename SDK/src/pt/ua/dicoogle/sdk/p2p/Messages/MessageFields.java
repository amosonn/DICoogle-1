/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.ua.dicoogle.sdk.p2p.Messages;

/**
 *
 * @author Carlos Ferreira
 * @author Pedro Bento
 */
public class MessageFields
{
    /**
     * Root element of every xml messages
     */
    public static final String MESSAGE = "Message";

    /**
     * Definition of the message type
     */
    public static final String MESSAGE_TYPE = "MessageType";
    /**
     * The number of the query
     */
    public static final String QUERY_NUMBER = "QueryNumber";
    /**
     * The field where the query value is declared
     */
    public static final String QUERY = "Query";
    /**
     * The list of search results
     */
    public static final String SEARCH_RESULTS = "SearchResults";
    public static final String EXTRAFIELD = "Extrafield";

    /**
     * One result of the search.
     */
    public static final String SEARCH_RESULT = "SearchResult";

    public static final String FILE_REQUESTED = "FileRequested";
}
