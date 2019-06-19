package il.co.vor.DalReport;


//import il.co.vor.common.*;
//import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
//import java.io.InputStream;
//import java.net.URI;
import java.util.logging.Logger;


import il.co.vor.VOSLogger;
import il.co.vor.DalConfigClient.DalConfigClient;
import il.co.vor.VOSDBConnection.DBConnectionPool;
import il.co.vor.VOSNetServer.NetServer;
import il.co.vor.common.Constants;
import il.co.vor.common.ParamNames;
import il.co.vor.utilities.ParametersReader;

/**
 * Main class.
 *
 */
public class DalReport 
{
	private static String OS = System.getProperty("os.name").toLowerCase();
	public static class MyShutdown implements NetServer.ShutdownService
	{

		@Override
		public void doShutdown() 
		{
			DBConnectionPool.DBConnectionPoolClose();
			VOSLogger.Terminate();
		}
		
	}
	// Service startup should be as follows:
	// Service name, and DB connect string should be (command line) parameters of the service
	// Once the service connects to the DB, base URI should be obtained from the DB
    // Base URI the Grizzly HTTP server will listen on. Read port from DB. It is hard coded now
	@SuppressWarnings("unused")
	private static final Logger logger = VOSLogger.getInstance();

    
    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    /**
     * Main method.
     * @param args
     * @throws IOException
     */
	
	private static int getPort()
	{
		int iPort = -1;
		
		iPort = ParametersReader.getParameterAsInt(ParamNames.SERVICES_SERVICE_ADDRESS_PORT);
		return iPort;
	}
	
	
	
    public static void main(String[] args) throws IOException 
    {    	
    	@SuppressWarnings("unused")
    	// we need to invoke the init of DalConfigClient in order to load all parameters from config schema
		DalConfigClient dcc = DalConfigClient.getInstance();
    	// connect to db. Note: TODO: should read connect string parameters from config
    	Logger _logger = Logger.getLogger(DalReport.class.getName());
    	int iPort = 0;
    	DBConnectionPool.DBConnectionPoolInit(true);
    	
    	iPort = getPort ();
    	
    	if (iPort <= 0)
    	{
    		_logger.log(Level.SEVERE, String.format("Failed to obtain port from DB for %s", Constants.NET_VOS_DAL_REPORT_ROOT_URI));
    		VOSLogger.Terminate();
    	}
    	else
    	{
    		_logger.log(Level.WARNING, String.format("Starting net server for %s on port %d", 
    				Constants.NET_VOS_DAL_REPORT_ROOT_URI, iPort));
    		NetServer.InitNetServer(Constants.NET_VOS_DAL_REPORT_ROOT_URI, iPort, Constants.NET_VOS_DAL_REPORT_API_ROOT_PACKAGE_NAME, null);
    		if (OS.indexOf("win") < 0) // linux
			{
				Runtime.getRuntime().addShutdownHook(new Thread()
			     {
			         @Override
			         public void run()
			         {
			        	
			        	 DBConnectionPool.DBConnectionPoolClose();
			 			VOSLogger.Terminate();
			     
			         }
			     });
			}
    	}
    }
}

