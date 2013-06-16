package pt.ua.dicoogle.sdk.Utils;

/**
 * Class to provides a unique ID to a query.
 * This class solves the problem, caused when the user search for two different things
 * without receive every responses of the other peers. There is only one query active at a time.
 * @author Carlos Ferreira
 * @author Pedro Bento
 */
public class QueryNumber
{
    private static Integer queryNumber;
    private static QueryNumber instance = null;

    public static QueryNumber getInstance()
    {
        if (instance == null)
        {
            instance = new QueryNumber();
        }
        return instance;
    }

    private QueryNumber()
    {
        queryNumber = new Integer(0);
    }

    public Integer getNewQueryNumber()
    {
        return ++queryNumber;
    }

    public Integer getQueryNumber()
    {
        return queryNumber;
    }
}
