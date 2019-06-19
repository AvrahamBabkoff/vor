package il.co.vor.FTPTM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import il.co.vor.DalConfigObjects.Service;
import il.co.vor.DalDataClient.DalDataClient;
import il.co.vor.FTP.defines.Enums.FtpServerSiteType;
import il.co.vor.common.Constants;
import il.co.vor.common.VosConfigResources;
import il.co.vor.utilities.PropertyFileReader;
import il.co.vor.DalConfigClient.DalConfigClient;
import il.co.vor.DalConfigObjects.FtpPlaceHolder;
import il.co.vor.DalConfigObjects.FtpServer;

public class ServiceParameters {
	
	private static Logger _logger = Logger.getLogger(ServiceParameters.class.getName());
	public static String _serviceName;
	public static Service _myService;
	public static Map<Integer,FtpServer> _ftpServers;
	public static ArrayList<FtpPlaceHolder> _sitesPlaceHolders;
	public static boolean _isParamsLoadedSuccessfully = true;
	private static final DalConfigClient _dcc = DalConfigClient.getInstance();
	private static final DalDataClient _ddc = DalDataClient.getInstance();
	
	public static DalConfigClient getDalConfigClient() 
	{
		return _dcc;
	}

	public static DalDataClient getDalDataClient() 
	{
		return _ddc;
	}

	public static void  LoadParameters()
	{
		try {
			InitializeVOSDalConfig();
			if(_isParamsLoadedSuccessfully){
				InitializeVOSDalData();
				_myService = getDalConfigClient().getServices().getServiceObject(_serviceName).getApiData().get(VosConfigResources.API_SERVICES_GET_RESOURCE_NAME).get(0);
				//_myParams = Parameters.getParametersObject(_myService.getServiceId()).getApiData().get(VosConfigResources.PARAMETERS_NAME);
				CreateFTPServersMap();
				_sitesPlaceHolders = getDalConfigClient().getFtpPlaceHolders().getFtpPlaceHoldersObject(_myService.getServiceId()).getApiData().get(VosConfigResources.FTP_PLACE_HOLDERS_NAME);
			}else{
				_logger.log(Level.SEVERE, String.format("InitializeVOSDalConfig failed"));
			}
		} catch (Exception e) {
			_logger.log(Level.SEVERE, String.format("LoadParameters Exception: %s %s", e.getMessage(), e.getStackTrace()));
		}

	}
	
	private static void CreateFTPServersMap() {
		// TODO Auto-generated method stub
		try {
			ArrayList<FtpServer> ftpServers = getDalConfigClient().getFtpServers().getFtpServersObject(_myService.getServiceId()).getApiData().get(VosConfigResources.FTP_SERVERS_NAME);
			_ftpServers = new HashMap<Integer,FtpServer>();
			
			for(FtpServer fs: ftpServers){
				int siteId = fs.getSite().getSiteId();
				if(fs.getFtpServerSiteType() == FtpServerSiteType.DB.ordinal())
					siteId = -100;
				_ftpServers.put(siteId, fs);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			_logger.log(Level.SEVERE, String.format("CreateFTPServersMap Exception: %s %s", e.getMessage(), e.getStackTrace()));
		}
	}

	public static void  LoadDalParameters()
	{
		try {

			_serviceName = PropertyFileReader.getProperty(Constants.PROP_NAME_SERVICE_NAME);	
			if(_serviceName == null || _serviceName == "")
				_isParamsLoadedSuccessfully = false;
					
		} catch (Exception e) {
			_isParamsLoadedSuccessfully = false;
			_logger.log(Level.SEVERE, String.format("LoadDalParameters: ServiceName Exception: %s %s", e.getMessage(), e.getStackTrace()));
		}
		
	}
	
	private static void InitializeVOSDalConfig() {
		// TODO Auto-generated method stub
		boolean res = true;
		
		//res = VOSDalConfigMain.initVOSDalConfig();
		if(!res){
			_isParamsLoadedSuccessfully = false;
			_logger.log(Level.SEVERE,"InitializeVOSDalConfig: failed to load Dal Config");
		}
	}
	
	private static void InitializeVOSDalData() {
		// TODO Auto-generated method stub
		boolean res = true;
		
		//res = VOSDalDataMain.initVOSDalData();
		if(!res){
			_isParamsLoadedSuccessfully = false;
			_logger.log(Level.SEVERE,"InitializeVOSDalData: failed to load Dal Data");
		}
		
	}
	
}
