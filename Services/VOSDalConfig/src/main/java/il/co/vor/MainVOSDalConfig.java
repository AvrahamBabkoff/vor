package il.co.vor;


//import il.co.vor.common.*;
//import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
//import java.io.InputStream;
//import java.net.URI;
import java.util.logging.Logger;

import org.json.JSONObject;

import il.co.vor.API.DalConfigService.Services;
import il.co.vor.VOSDBConnection.DBConnectionPool;
import il.co.vor.VOSNetServer.NetServer;
import il.co.vor.common.Constants;
import il.co.vor.utilities.PropertyFileReader;

/**
 * Main class.
 *
 */
public class MainVOSDalConfig 
{
	// Service startup should be as follows:
	// Service name, and DB connect string should be (command line) parameters of the service
	// Once the service connects to the DB, base URI should be obtained from the DB
    // Base URI the Grizzly HTTP server will listen on. Read port from DB. It is hard coded now
	@SuppressWarnings("unused")
	private static final Logger logger = VOSLogger.getInstance();
    //public static final String BASE_URI = "https://0.0.0.0:8080/vos_config/";

/*    
    @SuppressWarnings("deprecation")
	private static SSLEngineConfigurator createSslConfiguration() 
    {
        // Initialize SSLContext configuration
        SSLContextConfigurator sslContextConfig = new SSLContextConfigurator();

        ClassLoader cl = Main.class.getClassLoader();
        // Set key store
        
        InputStream in = cl.getResourceAsStream("keystore.jks");
        byte[] data = null;
        if (null != in)
        {
        	
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            for (;;) 
            {
                int nread = 0;
    			try 
    			{
    				nread = in.read(buffer);
    	            if (nread <= 0) 
    	            {
    	                break;
    	            }
    			} 
    			catch (IOException e) 
    			{
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
                baos.write(buffer, 0, nread);
            }
            data = baos.toByteArray();
        	sslContextConfig.setKeyStoreBytes(data);
        	sslContextConfig.setKeyStorePass("changeit");
            System.out.println("done reading jks file. size is " + data.length);
        }
        else
        {
        	System.out.println("failed to retrieve jks input stream...");
        }

        // Create SSLEngine configurator
        return new SSLEngineConfigurator(sslContextConfig.createSSLContext(),
                false, false, false);
    }
    */
    
    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
/*
    public static HttpServer startServer() 
    {
        // create a resource config that scans for JAX-RS resources and providers
        // in il.co.vor package
        final ResourceConfig rc = new ResourceConfig().packages("il.co.vor");

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        //return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc, true, createSslConfiguration(), true);
    }
*/
    /**
     * Main method.
     * @param args
     * @throws IOException
     */
	
	private static int getPort()
	{
		int iPort = -1;
		String sJSON = null;
		int iResult = -1;
		JSONObject jsonRes = null;
		String sServiceName = PropertyFileReader.getProperty(Constants.PROP_NAME_SERVICE_NAME);
		
		if (null != sServiceName)
		{
			sJSON = Services.getServiceImpl(sServiceName);
			if (null != sJSON)
			jsonRes = new JSONObject(sJSON);
			if (null != jsonRes)
			{
				iResult = ((JSONObject)jsonRes.get("Meta")).getInt("error");
				if (iResult == 0)
				{
					iPort = ((JSONObject)jsonRes.get("Data")).getJSONArray("services").getJSONObject(0).getInt("service_address_port");
				}
			}
		}
		return iPort;
	}
    public static void main(String[] args) throws IOException 
    {
    	// connect to db. Note: TODO: should read connect string parameters from config
    	Logger _logger = Logger.getLogger(MainVOSDalConfig.class.getName());
    	int iPort = 0;
    	DBConnectionPool.DBConnectionPoolInit(false);
    	
    	iPort = getPort ();
    	
    	if (iPort <= 0)
    	{
    		_logger.log(Level.SEVERE, String.format("Failed to obtain port from DB for %s", Constants.NET_VOS_DAL_CONFIG_ROOT_URI));
    		VOSLogger.Terminate();
    	}
    	else
    	{
    		_logger.log(Level.WARNING, String.format("Starting net server for %s on port %d", 
    				Constants.NET_VOS_DAL_CONFIG_ROOT_URI, iPort));
    		NetServer.InitNetServer(Constants.NET_VOS_DAL_CONFIG_ROOT_URI, iPort, Constants.NET_VOS_DAL_CONFIG_API_ROOT_PACKAGE_NAME, null);
    	}
        //final HttpServer server = startServer();
        //System.out.println(String.format("Jersey app started with WADL available at "
        //        + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
        //System.in.read();
        //server.stop();
    }
}

