package pt.ua.dicoogle.sdk.index.handlers;

/**
 * File Handler Exception
 * @author Marco
 */
public class FileHandlerException extends Exception 
{

    /**
     * File Handler Exception
     * @param string Message to display
     * @param e base exception caught
     */
    public FileHandlerException(String string, Exception e) 
    {
        super(string, e);
    }

}
