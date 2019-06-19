package il.co.vor.FTPTM;


import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import il.co.vor.VOSLogger;
import il.co.vor.DalConfigObjects.FtpServer;
import il.co.vor.FTP.defines.Enums.FtpServerSiteType;
import il.co.vor.VOSNetServer.NetServer;
import il.co.vor.common.Constants;
import il.co.vor.common.Enums;
import il.co.vor.common.ParamNames;
import il.co.vor.utilities.ParametersReader;


public class FTPTM {

	@SuppressWarnings("unused")
	private static final Logger logger = VOSLogger.getInstance();
	
	//region Members
	private static Logger _logger = Logger.getLogger(FTPTM.class.getName());
	private static String OS = System.getProperty("os.name").toLowerCase();
	private static String _localRootPath;
	//endregion

	public static void main(String[] args) {
		int iPort = -1;
		ServiceParameters.LoadDalParameters();
		ServiceParameters.LoadParameters();
		// initialize net server
		try 
		{
			iPort = ParametersReader.getParameterAsInt(ParamNames.SERVICES_SERVICE_ADDRESS_PORT, -1);
			NetServer.InitNetServer(Constants.NET_VOS_FTPTM_BASE_URI, iPort, Constants.NET_VOS_FTPTM_API_ROOT_PACKAGE_NAME, new MyShutdown());
		} 
		catch (IOException e) 
		{			
			_logger.log(Level.SEVERE, String.format("Failed to init net server: port = %d, exception: %s", iPort, e.getMessage()));
		}
		CreateSitesFolderTree();
		if(ServiceParameters._isParamsLoadedSuccessfully){
			//System.out.print("FTP State Machine Init Starting...");
			StateMachineManager.InitStateMachine();
		}else{
			_logger.log(Level.SEVERE, "State Machine will not run , failed load Sevice Parameters");
			//System.out.print("State Machine will not run !");
		}
		
		if (OS.indexOf("win") < 0) // linux
		{
			Runtime.getRuntime().addShutdownHook(new Thread()
		     {
		         @Override
		         public void run()
		         {
		        	
		        	 StateMachineManager.DoGracefulShutdown();
		     
		         }
		     });
		}
		/*try {
			Thread.sleep(300000);
			StateMachineManager.DoGracefulShutdown();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

	private static void CreateSitesFolderTree() {
		_localRootPath = ParametersReader.getParameter(ParamNames.FTPTM_LOCAL_ROOT_PATH, true);
		File rootDir = new File(_localRootPath + Constants.FILE_SEPARATOR + ServiceParameters._myService.getServiceName());

		// if the directory does not exist, create it
		if (!rootDir.exists()) {

		    try{
		    	rootDir.mkdirs();
		    	_logger.log(Level.INFO, "Root DIR created");
		    } 
		    catch(SecurityException se){
		    	_logger.log(Level.SEVERE, "Failed to create Root folder - security violation");
		    }        

		}
		
		for(FtpServer value : ServiceParameters._ftpServers.values()){
			
			File serverDir = new File (rootDir + Constants.FILE_SEPARATOR + value.getSite().getSiteName());
			File serverSubDir;
			
			if (!serverDir.exists() && value.getFtpServerSiteType() != FtpServerSiteType.DB.ordinal()) {

			    try{
			    	serverSubDir = new File (serverDir , Enums.FileType.CDR_DATA.toString());
			    	serverSubDir.mkdirs();
			    	serverSubDir = new File (serverDir , Enums.FileType.OPERANDS_DATA.toString());
			    	serverSubDir.mkdirs();
			    	serverSubDir = new File (serverDir , Enums.FileType.METERS_DATA.toString());
			    	serverSubDir.mkdirs();
			    	_logger.log(Level.INFO, "Root DIR created");
			    } 
			    catch(SecurityException se){
			    	_logger.log(Level.SEVERE, "Failed to create Root folder - security violation");
			    }        

			}else if(value.getFtpServerSiteType() != FtpServerSiteType.DB.ordinal()) {
				
			}
			
		}		
	}
	
	public static class MyShutdown implements NetServer.ShutdownService
	{

		@Override
		public void doShutdown() 
		{
			StateMachineManager.DoGracefulShutdown();
		}		
	}

}
