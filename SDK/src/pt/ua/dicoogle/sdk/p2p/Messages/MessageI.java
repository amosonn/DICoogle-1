package pt.ua.dicoogle.sdk.p2p.Messages;

import java.io.Serializable;

/**
 * All messages shall implement this interface
 *
 * @author Carlos Ferreira
 * @author Pedro Bento
 */
public interface MessageI<I> extends Serializable
{
    public I getMessage();
    public String getType();
}
