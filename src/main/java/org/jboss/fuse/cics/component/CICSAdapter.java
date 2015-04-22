package org.jboss.fuse.cics.component;

import com.ibm.ctg.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

/**
 * This is an adapter class to access IBM CICS Transaction gateway. 
 * This class uses com.ibm.ctg.client IBM CTG connector proprietary APIs.
 * Author: sgutierr@redhat.com
 */
public class CICSAdapter {
    /** Variables */
    private static JavaGateway gateway = null;
    private String gatewayURL = "";
    private String sslKeyring;
    private String sslPassword;
    private String userId;
    private String password;
    private String server;
    private String program;
    private int commAreaSize;
    private ECIRequest eciRequestObject;
    private static int iPort;

    /** Constants */
    private static final int MINIMUM_SIZE = 50;
    private static final int MAX_COMMAREA_SIZE = 32500;
    private static final String DEFAULT_URL = "local://dummy:2006";
    private static final String EC02 = "EC02";

    /** logger */
	private static final Logger logger = LoggerFactory.getLogger(CICSAdapter.class);
    /**
     * Public constructor.
     *
     *
     * The constructor may be started with three optional
     * initialisation parameters
     *  If no URL is provided a defeault URL of 'local:' is used.
     * @param server           The Gateway URL protocol://address:port/
     * @param sslKeyring           SSL Classname
     * @param sslPassword          SSL Password
     * @param port                 iPort
     * @throws java.io.IOException
     */
    public CICSAdapter(String server, String sslKeyring, String sslPassword, int port) throws IOException {

        /** Declare and initialize method variables and ECIRequest object */
        eciRequestObject=null;
        this.iPort=port;

        /** The correct gateway URL format is: Sets the protocol,
         *  address and port of this JavaGateway by means of a single URL string.
         *  The URL takes the expected form of :  protocol://address:port/
         **/
        try{

        if (server == null)
        {
            gatewayURL = DEFAULT_URL;
        }
        else{
            gatewayURL="tcp://"+server+":"+String.valueOf(port);
        }
        this.sslKeyring  = sslKeyring;
        this.sslPassword = sslPassword;

         // Set the keyring and keyring password and then initialize
         // the JavaGateway object to flow data to the Gateway

        if (sslKeyring != null && sslPassword != null)
        {
            Properties sslProps = new Properties();
            sslProps.setProperty(JavaGateway.SSL_PROP_KEYRING_CLASS, sslKeyring.trim());
            sslProps.setProperty(JavaGateway.SSL_PROP_KEYRING_PW, sslPassword.trim());

           // When you create a JavaGateway you determine the protocol to use, and if required,
           // the connection details of the remote CICS Transaction Gateway server (network address and port number).
           // With the JavaGateway class you can either specify this information using the setAddress(), setProtocol() and setPort() methods,
           // or you can provide all the information in URL form of Protocol://Address:Port. You can use the setURL() method or pass the URL into one of the JavaGateway constructors.
            gateway= new JavaGateway(gatewayURL, iPort, sslProps);
        }
        else {
            //The JavaGateway represents a logical connection between your program and the CICS Transaction Gateway
            // when you specify a remote protocol.
            // If you specify a local connection, you connect directly to the CICS server, bypassing any CICS Transaction Gateway servers.
            gateway= new JavaGateway(gatewayURL, iPort);
        }
        }
        catch (IOException e){
            logger.error(e.getMessage());
        }
    }

    public void openGateway() throws IOException {
        try {
            if (!gateway.isOpen()) {
                gateway.setURL(getGatewayURL());
                gateway.open();
            }
        }
        catch (IOException e)
        {
            logger.error(e.getMessage());
        }
    }

    public byte[] runTransaction(CICSEndpoint endpoint)
            throws IOException, Exception
    {
        byte[] commArea=null;
        userId = endpoint.getUserId();
        password = endpoint.getPassword();
        server = endpoint.getServer();
        program = endpoint.getProgram();
        commAreaSize = endpoint.getCommAreaSize();
    try{
        // Set comm Area Size and create buffer to store result
        if (program.equals(EC02))
        {
            commAreaSize = 50;
        }
        else
        {
            if (commAreaSize>MAX_COMMAREA_SIZE)
            {
                commAreaSize = MAX_COMMAREA_SIZE;
            }

            if (commAreaSize<MINIMUM_SIZE)
            {
                commAreaSize = MINIMUM_SIZE;
            }
        }
        commArea = new byte[commAreaSize];

         /*
         * Use the extended constructor to set the parameters on the
         * ECIRequest object
         */
         eciRequestObject =
                new ECIRequest(ECIRequest.ECI_SYNC,      //ECI call type
                        server.trim(),          //CICS server
                        endpoint.getUserId(),                     //CICS userid
                        endpoint.getPassword(),                     //CICS password
                        program.trim(),               //CICS program to be run
                        null,                     //CICS transid to be run
                        commArea,             //Byte array containing the
                        // COMMAREA
                        commAreaSize,            //COMMAREA length
                        ECIRequest.ECI_NO_EXTEND, //ECI extend mode
                        ECIRequest.ECI_LUW_NEW);           //ECI LUW token

           /*
         * Call the flowRequest method and display returned data in hex and
         * ASCII format. If the method returns true a security error has
         * occurred and the user is prompted for a CICS user ID and password.
         */
        while (flowRequest(eciRequestObject) == true) {
            System.out.println("\nEnter your CICS user ID:");
            System.out.println("\nEnter your CICS password or password phrase:");
        }

        System.out.println("\nProgram " + program
                + " returned with data:- \n");
        System.out.print("\tHex: ");

        /*
        * Print result in HEX Mode
        */
        for (int i = 0; i < commArea.length; i++) {
            System.out.print(Integer.toHexString(commArea[i]));
        }
        /*
        * Print result in ASCII text
        */
        try {
            System.out.println("\n\tASCII text: "
                    + new String(commArea, "ASCII"));
        } catch (UnsupportedEncodingException e) {
            System.out.println
                    ("\tThe ASCII encoding scheme is not supported.");
        }

        // Close the JavaGateway object before exiting
        if (gateway.isOpen() == true) {
            gateway.close();
        }
    }
        catch (Exception e) {
        e.printStackTrace();
    }
        return commArea;
    }

    public String toString() {
        return ( "gatewayURL "+gatewayURL+"sslKeyring "+sslKeyring+"sslPassword "+sslPassword+"userId "+userId+"password "+password+"server"+server+"program"+program);
    }

    /*
   * The flowRequest method flows data contained in the ECIRequest object to
   * the Gateway and determines whether it has been successful by checking the
   * return code. If an error has occurred, the return code string and abend
   * codes are printed to describe the error before the program exits.
   * Note: Security may be required for client connection to the server and
   *       not just for the ECI request. Refer to the security chapter in the
   *       product documentation for further details.
   */
    private static boolean flowRequest(ECIRequest requestObject) {
        try {
            int iRc = gateway.flow(requestObject);

            // Checks for gateway errors and returns false if there are no errors
            switch (requestObject.getCicsRc()) {
                case ECIRequest.ECI_NO_ERROR:
                    if (iRc == 0) {
                        return false;
                    } else {
                        System.out.println("\nError from Gateway ("
                                + requestObject.getRcString()
                                + "), correct and rerun this sample.");
                        if (gateway.isOpen() == true) {
                            gateway.close();
                        }
                        System.exit(0);
                    }

         /*
         * Checks for security errors and returns true if validation has
         * failed on four or less occasions
         */
                case ECIRequest.ECI_ERR_SECURITY_ERROR:
                    System.out.print("\n\nValidation failed. ");
                    System.out.println("Try entering your details again.");
                    break;

         /*
         * Checks for transaction abend errors where the user is authorised
         * to access the server but not run the EC01 program.
         * The sample should be rerun and a user ID and password with the
         * required authorisation entered.
         */
                case ECIRequest.ECI_ERR_TRANSACTION_ABEND:
                    System.out.println("\nAn error was returned from the server."
                            + "\nRefer to the abend code for further details.");
            }
            System.out.println("\nECI returned: "
                    + requestObject.getCicsRcString());
            System.out.println("Abend code was "
                    + requestObject.Abend_Code + ".\n");
            if (gateway.isOpen() == true) {
                gateway.close();
            }
            System.exit(0);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        return true;
    }
    public void destroy()
    {
        try
        {
            if (gateway!=null)
            {
                if (gateway.isOpen())
                {
                    gateway.close();
                }
            }
        }
        catch (IOException e)
        {
            StringWriter exceptionOut = new StringWriter();
            e.printStackTrace(new PrintWriter(exceptionOut ));
            String trace = exceptionOut.toString();
            logger.error(trace);
        }
    }

    public static JavaGateway getGateway() {
        return gateway;
    }public static void setGateway(JavaGateway gateway) {
        CICSAdapter.gateway = gateway;
    }public String getGatewayURL() {
        return gatewayURL;
    }public void setGatewayURL(String gatewayURL) {
        this.gatewayURL = gatewayURL;
    }public String getSslKeyring() {
        return sslKeyring;
    }public void setSslKeyring(String sslKeyring) {
        this.sslKeyring = sslKeyring;
    }public String getSslPassword() {
        return sslPassword;
    }public void setSslPassword(String sslPassword) {
        this.sslPassword = sslPassword;
    }public String getUserId() {
        return userId;
    }public void setUserId(String userId) {
        this.userId = userId;
    }public String getPassword() {
        return password;
    }public void setPassword(String password) {
        this.password = password;
    }public String getServer() {
        return server;
    }public void setServer(String server) {
        this.server = server;
    }public String getProgram() {
        return program;
    }public void setProgram(String program) {
        this.program = program;
    }public int getCommAreaSize() {
        return commAreaSize;
    }public void setCommAreaSize(int commAreaSize) {
        this.commAreaSize = commAreaSize;
    }



}
