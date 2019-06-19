package il.co.vor.SystemManager;

import il.co.vor.DalConfigObjects.Service;
import il.co.vor.VOSNetClient.GenClient;

/**
 * ConvertBatchPerformanceResult encapsulates the result of processing
 * an input file
 * 
 * Objects of this class are consumed by the list on the main window 
 * populated by processed input files
 * 
 * Properties:
 * 
 * filePath - full path of input file
 * listResults - linked list of conversion latency times in milliseconds
 * uiResultObject - UI (frame) object currently displaying this objects results in a graph - if not null 
 * 
 **/
public class ServiceUIWraper 
{
	private volatile Object uiObject;
	private Service service;
	private GenClient genClient;
	private boolean isActive;
	private String logLevel;
	private String siteName;
	
	public String getSiteName() {
		return siteName;
	}


	public void setSiteName(String _siteName) {
		this.siteName = _siteName;
	}


	public boolean isActive() {
		return isActive;
	}


	public void setActive(boolean _isActive) {
		isActive = _isActive;
	}


	public String getLogLevel() {
		return logLevel;
	}


	public void setLogLevel(String _logLevel) {
		logLevel = _logLevel;
	}
	
	
	public Service getService() 
	{
		return service;
	}


	public GenClient getGenClient() {
		return genClient;
	}


	public Object getUiObject() 
	{
		return uiObject;
	}


	public void setUiObject(Object _uiObject) 
	{
		this.uiObject = _uiObject;
	}


	public ServiceUIWraper (Service _service, GenClient _genClient) 
	{
		super();
		service = _service;
		genClient = _genClient;
		uiObject = null;
	}



	@Override
	public String toString () 
	{
		return service.getServiceName();
	}
}
 