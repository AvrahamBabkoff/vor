package il.co.vor.VOSNetServer;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.ssl.SSLContextConfigurator;
import org.glassfish.grizzly.ssl.SSLEngineConfigurator;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import il.co.vor.VOSLogger;
import il.co.vor.common.Constants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.nio.file.*;

/**
 * Main class.
 *
 */
public class NetServer 
{
	public interface ShutdownService 
	{
	    void doShutdown();
	}
	
	// Service startup should be as follows:
	// Service name, and DB connect string should be (command line) parameters of the service
	// Once the service connects to the DB, base URI should be obtained from the DB
    // Base URI the Grizzly HTTP server will listen on. Read port from DB. It is hard coded now
	private static final Logger vosLogger = VOSLogger.getInstance();
	private static final Logger logger = Logger.getLogger(NetServer.class.getName());
    //public static final String BASE_URI = "https://0.0.0.0:8080/vos_config/";
    public static final String sAPIRootPackage = "il.co.vor.API";
    public static final String sGenUtilsApiPackage = "GenUtils";
	public static String BASE_URI = "";
    private static boolean bInitialized = false;
    private static HttpServer m_server = null;
    private static ShutdownService m_sds = null;

    
    @SuppressWarnings("deprecation")
	private static SSLEngineConfigurator createSslConfiguration() 
    {
        // Initialize SSLContext configuration
        SSLContextConfigurator sslContextConfig = new SSLContextConfigurator();

        ClassLoader cl = NetServer.class.getClassLoader();
        // Set key store
        
        InputStream in = cl.getResourceAsStream("NetServer.jks");
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
        	logger.log(Level.FINE, "done reading jks file. size is " + data.length);
            //System.out.println("done reading jks file. size is " + data.length);
        }
        else
        {
        	logger.log(Level.SEVERE, "failed to retrieve jks input stream...");
        	//System.out.println("failed to retrieve jks input stream...");
        }

        // Create SSLEngine configurator
        return new SSLEngineConfigurator(sslContextConfig.createSSLContext(),
                false, false, false);
    }
    
    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer(String...packages) 
    {
        // create a resource config that scans for JAX-RS resources and providers
        // in il.co.vor package
        final ResourceConfig rc = new ResourceConfig().packages(packages);

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        //return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc, true, createSslConfiguration(), true);
    }

    /**
     * Main method.
     * @param args
     * @throws IOException
     */
    public static synchronized void InitNetServer(String sBaseUri, int iPort, String sApiPackage, ShutdownService sds) throws IOException 
    {
		String sServiceApiFullPackageName;
		String sGenUtilsApiFullPackageName;
		String sShutdownUrlFileName;
		Path pathShutdownUrlFileName;
		String sShutdownUrl;
    	// connect to db. Note: TODO: should read connect string parameters from config
    	//DBConnectionPool.DBConnectionPoolInit();
    	if(false == bInitialized)
    	{
    		bInitialized = true;
    		m_sds = sds;
    		// class static constants should be taken from properties file
    		sServiceApiFullPackageName = sAPIRootPackage + "." + sApiPackage;
    		sGenUtilsApiFullPackageName = sAPIRootPackage + "." + sGenUtilsApiPackage;
    		
    		// get root of API packages from properties file. For now, assume "il.co.vor.API"
    		BASE_URI = "https://0.0.0.0:" + iPort + "/" + sBaseUri + "/";
    		m_server = startServer(sServiceApiFullPackageName, sGenUtilsApiFullPackageName);
    		
	        logger.log(Level.WARNING, String.format("NetServer started at %s", BASE_URI));
	        //System.out.println(String.format("Jersey app started with WADL available at "
	        //        + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
	        // check if we have a system property to dump url in
	        sShutdownUrlFileName = System.getProperty(Constants.SYSTEM_PROPERTIES_CURRENT_SHUTDOWN_URL_FILE_NAME_PROP_NAME);
	        if(sShutdownUrlFileName != null && !sShutdownUrlFileName.isEmpty())
	        {
	        	sShutdownUrl = BASE_URI.replace("0.0.0.0", "localhost") + Constants.NET_VOS_SHUTDOWN;
	        	pathShutdownUrlFileName = Paths.get(sShutdownUrlFileName);
	        	Files.write(pathShutdownUrlFileName, sShutdownUrl.getBytes());
	        }
    	}
    }


    public static synchronized void ShutDownNetServer() 
    {
    	m_server.shutdown();
    	if (null != m_sds)
    	{
    		m_sds.doShutdown();
    	}
    	VOSLogger.Terminate();
    }

}

