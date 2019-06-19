package il.co.vor.API.GenUtils;

//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import il.co.vor.VOSLogger;
import il.co.vor.ApiObjectsCommon.*;
import il.co.vor.common.Constants;
import il.co.vor.common.VosGenResources;

@Path(Constants.NET_VOS_LOG_ROOT_URI)
public class LogLevel 
{
	private static final Logger logger = Logger.getLogger(PostRequest.class.getName());
	
	@Path(Constants.NET_VOS_LOG_LEVEL_URI_TEMPLATE)
	@POST
	@Produces ("application/json")
	public ApiMultiResultWrapper<LoggerInfo> setLogLevel(@PathParam(Constants.NET_VOS_LOG_LEVEL) String logLevel) 
	{
		logger.log(Level.SEVERE, "In setLogLevel");
		VOSLogger.setLogLevel (logLevel);
		return getLogInfo();
    }

/*
	@Path(Constants.NET_VOS_LOG_LEVEL_URI_TEMPLATE)
	@POST
	public Response setLogLevel(@PathParam(Constants.NET_VOS_LOG_LEVEL) String logLevel) 
	{
		logger.log(Level.SEVERE, "In setLogLevel");
		VOSLogger.setLogLevel (logLevel);
		return Response.status(200).build();
    }


	@Path(Constants.NET_VOS_LOG_LEVEL)
	@GET
	public String getLogLevel() 
	{
		String sRes;
		logger.log(Level.SEVERE, "In getLogLevel");
		
		String sLogLevel = VOSLogger.getLogLevel ();
		int iPort = VOSLogger.getLogToSocketPort ();
		
		sRes = String.format("Level: %s, Port: %d", sLogLevel, iPort);
		return sRes;
    }
*/    

	
	//@Path(Constants.NET_VOS_LOG_LEVEL)
	@GET
	@Produces ("application/json")
	public ApiMultiResultWrapper<LoggerInfo> getLogInfo() 
	{
		
		String sLogLevel;
		int iPort;
		LoggerInfo li;// = new LoggerInfo();
		ApiMultiResultWrapper<LoggerInfo> oRes;
		li = new LoggerInfo();
		sLogLevel = VOSLogger.getLogLevel ();
		iPort = VOSLogger.getLogToSocketPort ();
		
		li.setLevel(sLogLevel);
		li.setPort(iPort);
		
		oRes = ApiMultiResultWrapper.createApiMultiResultWrapper(0, "", li, VosGenResources.LOG_INFO_NAME);
		return oRes;
    }

	@Path(Constants.NET_VOS_LOG_CONSOLE_URI_TEMPLATE)
	@POST
	public Response setLogToConsole(@PathParam(Constants.NET_VOS_LOG_CONSOLE) String sLogToConsole) 
	{
		boolean bLogToConsole = false;
		logger.log(Level.SEVERE, "In setLogToConsole");
		bLogToConsole = new Boolean(sLogToConsole.equalsIgnoreCase("on")).booleanValue();
		VOSLogger.setLogToConsole (bLogToConsole);
		return Response.status(200).build();
    }

}
/*		
String sLogLevel;
int iPort;
LoggerInfo li;// = new LoggerInfo();
ArrayList<LoggerInfo> ll;// = new ArrayList<>();
ApiMultiResultWrapper<LoggerInfo> oRes;
ApiResult ar;// = new ApiResult();
//ApiData<LoggerInfo> ad;
Map<String, ArrayList<LoggerInfo>> mMap;// = new HashMap<String, ArrayList<LoggerInfo>>();
//oRes.getApiData().
logger.log(Level.SEVERE, "In getLogLevel");

sLogLevel = VOSLogger.getLogLevel ();
iPort = VOSLogger.getLogToSocketPort ();
li = new LoggerInfo();
li.setLevel(sLogLevel);
li.setPort(iPort);
ll = new ArrayList<>();
ll.add(li);
ar = new ApiResult();
ar.setError(0);
ar.setMessage("");
//ad = new ApiData<LoggerInfo>();
//ad.setResultSet(ll);
mMap = new HashMap<String, ArrayList<LoggerInfo>>();
mMap.put(VosGenResources.LOG_INFO_NAME, ll);
oRes = new ApiMultiResultWrapper<LoggerInfo>();
oRes.setApiResult(ar);
oRes.setApiData(mMap);
//sRes = String.format("Level: %s, Port: %d", sLogLevel, iPort);

*/		
