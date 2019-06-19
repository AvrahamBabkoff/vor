package il.co.vor.VOSNetClient;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

import il.co.vor.ApiObjectsCommon.ApiMultiResultWrapper;
import il.co.vor.ApiObjectsCommon.LoggerInfo;
import il.co.vor.common.Constants;

public class GenClient 
{
	private String m_sIP;
	private int m_iPort;
	private String m_baseURL;
	GenClient.LoggerClient m_LoggerClient;
	GenClient.StopServiceClient m_StopServiceClient;
	
	public GenClient(String sIP, int iPort, String _baseURI)
	{
		m_sIP = sIP;
		m_iPort = iPort;
		m_baseURL = "https://"+ m_sIP + ":" + m_iPort + "/" + _baseURI + "/";
		m_LoggerClient = new GenClient.LoggerClient(getBaseURL());
		m_StopServiceClient = new GenClient.StopServiceClient(getBaseURL());
	}
	
	public String getBaseURL() 
	{
		return m_baseURL;
	}

	public GenClient.LoggerClient getLoggerClient ()
	{
		return m_LoggerClient;
	}
	
	public GenClient.StopServiceClient getStopServiceClient ()
	{
		return m_StopServiceClient;
	}

	public static class LoggerClient 
	{
		private static final Logger logger = Logger.getLogger(LoggerClient.class.getName());
		private static ObjectMapper m_mapper = null;
		private static TypeReference<ApiMultiResultWrapper<LoggerInfo>> m_ref = null;
		private String m_baseURL;
		@SuppressWarnings("unused")
		private static final boolean bInitialize = _init();

		private static boolean _init()
		{
			m_mapper = new ObjectMapper();
			 AnnotationIntrospector aiJaxb = new JaxbAnnotationIntrospector(TypeFactory.defaultInstance());
		     AnnotationIntrospector aiJackson = new JacksonAnnotationIntrospector();
		        // first Jaxb, second Jackson annotations
		    m_mapper.setAnnotationIntrospector(AnnotationIntrospector.pair(aiJaxb, aiJackson));
		    m_ref = new TypeReference<ApiMultiResultWrapper<LoggerInfo>>() { };
			return true;
		}

		private LoggerClient(String _baseURL) 
		{
			m_baseURL = _baseURL;
			// TODO Auto-generated constructor stub
		}

		public ApiMultiResultWrapper<LoggerInfo> getLoggerInfoObject()
		{
			String sError;
			String sRes;

			ApiMultiResultWrapper<LoggerInfo> amrwr = null;
			try 
			{
				sRes = NetClient.callGetRaw(m_baseURL, Constants.NET_VOS_LOG_ROOT_URI);
				if(sRes != null && !sRes.isEmpty())
				{
					amrwr = m_mapper.readValue(sRes, m_ref);
				}
				else
				{
					throw new Exception("API returned with empty string");
				}
			} 
			catch (Exception e) 
			{
				amrwr = null;
				sError = e.getMessage();
		        logger.log(Level.SEVERE, String.format("Exception. %s", sError));
			}
			return amrwr;
		}

		public ApiMultiResultWrapper<LoggerInfo> setLogLevel(String sLevel)
		{
			String sError;
			String sRes;

			ApiMultiResultWrapper<LoggerInfo> amrwr = null;
			try 
			{
				sRes = NetClient.callPostRaw(m_baseURL, "logs/level/" + sLevel, "");
				if(sRes != null && !sRes.isEmpty())
				{
					amrwr = m_mapper.readValue(sRes, m_ref);
				}
				else
				{
					throw new Exception("API returned with empty string");
				}
			} 
			catch (Exception e) 
			{
				amrwr = null;
				sError = e.getMessage();
		        logger.log(Level.SEVERE, String.format("Exception. %s", sError));
			}
			return amrwr;
		}
	}

	
	public static class StopServiceClient 
	{
		private static final Logger logger = Logger.getLogger(LoggerClient.class.getName());
		private String m_baseURL;
		@SuppressWarnings("unused")
		private static final boolean bInitialize = _init();

		private static boolean _init()
		{
			return true;
		}

		private StopServiceClient(String _baseURL) 
		{
			m_baseURL = _baseURL;
		}

		public void doShutdown()
		{
			String sError;
			String sRes;

			try 
			{
				sRes = NetClient.callPostRaw(m_baseURL, Constants.NET_VOS_SHUTDOWN, "");
				if(sRes == null || sRes.isEmpty())
				{
					throw new Exception("API returned with empty string");
				}
			} 
			catch (Exception e) 
			{
				sError = e.getMessage();
		        logger.log(Level.SEVERE, String.format("Exception. %s", sError));
			}
		}

	}
	
}
