/*  Copyright   2009 - IEETA
 *
 *  This file is part of Dicoogle.
 *  
 *  Author: Luís A. Bastião Silva <bastiao@ua.pt>
 *
 *  Dicoogle is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Dicoogle is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Dicoogle.  If not, see <http://www.gnu.org/licenses/>.
 */


package pt.ua.dicoogle.queryretrieve;


import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;


//import org.dcm4che2.tool.dcmecho.DcmEcho ;

import pt.ua.dicoogle.server.queryretrieve.QueryRetrieve ;

/**
 *
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public class DicomQueryRetrieveTest
{
    public static QueryRetrieve qr = null ;
    public DicomQueryRetrieveTest()
    {
    }

    @BeforeClass
    public static void setUpClass() throws Exception 
    {
        
        /*
         * Create instance of QueryRetrieve 
         * Then it need to be started. If it was not start properly 
         * the @Test case will fail for sure
         * 
         * So it is the right way to management this matter because make a @Test
         * to test startListening doesn't not make any sense IMHO
         * 
         * $author: Luís A. Bastião Silva
         */

        qr = new QueryRetrieve();
        qr.startListening();
    }

    @AfterClass
    public static void tearDownClass() throws Exception
    {

        /**
         * The same reason to no write test to stopListening
         */

        qr.stopListening();
        qr = null ;
    }

    @Before
    public void setUp()
    {

        /**
         * Index something to control do retrieve right results
         *
         */

        

    }

    @After
    public void tearDown()
    {
    }

    /* Test Cases now */

    @Test
    public void doEcho()
    {
        // Do a DICOM Echo Request and verify if it is run properly

        String[] _args = {"STORESCP@localhost:11112"} ;
        //DcmEcho.main(_args);
        //DcmEcho dcmecho = new DcmEcho();


        /**
         * Set the local domain
         */
        //dcmecho.setRemoteHost("127.0.0.1");
        //dcmecho.setRemotePort(1044);

        /**
         * Set the destination 
         */

        /**
        dcmecho.setCalling("Dicoogle");
        dcmecho.setCalledAET("Dicoogle", true);
        try {
            dcmecho.open();
        } catch (Exception ex) {
            fail("it's impossible open the connection, someone is wrong" +
                    "and it should be on the server. QueryRetrieve code is " +
                    "not a mess?");
        }
        */
    }


    @Test
    public void doQuery()
    {



        /**
         * Now I need a Query/Retrieve SCU
         */

        /** First test will be a simple query, and it will not be critical
         * 
         */

    

    }

}