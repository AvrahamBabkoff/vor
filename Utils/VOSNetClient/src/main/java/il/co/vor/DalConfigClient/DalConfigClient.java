package il.co.vor.DalConfigClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

import il.co.vor.ApiObjectsCommon.ApiMultiResultWrapper;
import il.co.vor.DalConfigObjects.*;
import il.co.vor.VOSNetClient.GenClient;
import il.co.vor.VOSNetClient.NetClient;
import il.co.vor.common.Constants;
import il.co.vor.common.ParamNames;
import il.co.vor.common.VosConfigResources;
import il.co.vor.utilities.ParametersReader;
import il.co.vor.utilities.PropertyFileReader;

public class DalConfigClient extends GenClient
{
	private DalConfigClient.FtpPlaceHolders m_FtpPlaceHolders;
	private DalConfigClient.Services m_Services;
	private DalConfigClient.FtpServers m_FtpServers;
	private DalConfigClient.Operands m_Operands;
	private DalConfigClient.Parameters m_Parameters;
	private DalConfigClient.Sites m_Sites;
	private DalConfigClient.Plcs m_Plcs;
	private static Logger _logger = Logger.getLogger(DalConfigClient.class.getName());

	private static final DalConfigClient m_instance = createDefaultInstance ();
	
	public DalConfigClient(String sIP, int iPort)
	{
		super(sIP, iPort, Constants.NET_VOS_DAL_CONFIG_ROOT_URI);

		m_FtpPlaceHolders = new DalConfigClient.FtpPlaceHolders(getBaseURL());
		m_Services = new DalConfigClient.Services(getBaseURL());
		m_FtpServers = new DalConfigClient.FtpServers(getBaseURL());
		m_Operands = new DalConfigClient.Operands(getBaseURL());
		m_Parameters = new DalConfigClient.Parameters(getBaseURL());
		m_Plcs = new DalConfigClient.Plcs(getBaseURL());
		m_Sites = new DalConfigClient.Sites(getBaseURL());
	}
	
	
	
	
	
	

	/**
	 * Create default instance of DalConfigClient. Default instance is initialized with IP and Port in config.properties file for
	 * this service
	 * 
	 * This method also initializes the parameters object of this service
	 * @return DalConfigClient (singleton) instance
	 */
	private  static DalConfigClient initDefaultDalConfigClient ()
	{
		String sIP = "";
		int iPort = 0;
		int i = 0;
		boolean bRes = true;
		ApiMultiResultWrapper<Service> amrwrservice = null;
		String sServiceName = null;
		Service _service = null;
		ArrayList<Service> _aService = null;

		ApiMultiResultWrapper<Parameter> amrwrparams = null;
		Parameter _param = null;
		ArrayList<Parameter> _aParams = null;
		DalConfigClient dgc = null;
		HashMap<String, String> _hmParams = new HashMap<String, String>();

		sServiceName = PropertyFileReader.getProperty(Constants.PROP_NAME_SERVICE_NAME);
		
		sIP = PropertyFileReader.getProperty(Constants.PROP_NAME_DAL_IP_ADDRESS);
		iPort = PropertyFileReader.getPropertyAsInt(Constants.PROP_NAME_DAL_PORT, -1);
		
		if ((null != sServiceName) && (null != sIP) && (-1 != iPort))
		{
			_hmParams.put(Constants.PROP_NAME_SERVICE_NAME, sServiceName);
			// create DalConfigClient instance with default IP and port
			dgc = new DalConfigClient(sIP, iPort);
			
			_logger.log(Level.WARNING, String.format("VOSDalConfigMain initialized: IP = %s, port = %d", sIP, iPort));
	
			
			bRes = ((amrwrservice = dgc.getServices().getServiceObject(sServiceName)) != null);
			if (false != bRes)
			{
				if (amrwrservice.getApiResult().getError() != 0 ||
				   (amrwrservice.getApiData() == null) ||
				   (_aService = amrwrservice.getApiData().get(VosConfigResources.API_SERVICES_GET_RESOURCE_NAME)) == null ||
				   (_aService.size() != 1))
				{
					_logger.log(Level.SEVERE, "getServiceObject failed!!!");
					bRes = false;
				}
			}
			if (false != bRes)
			{
				_service = _aService.get(0);
				_hmParams.put(ParamNames.SERVICES_SERVICE_ID, String.valueOf(_service.getServiceId()));
				_hmParams.put(ParamNames.SERVICES_SERVICE_ADDRESS_IP, _service.getServiceAddressIp());
				_hmParams.put(ParamNames.SERVICES_SERVICE_ADDRESS_PORT, String.valueOf(_service.getServiceAddressPort()));
	
				bRes = ((amrwrparams = dgc.getParameters().getParametersObject(_service.getServiceId())) != null);
			}
			if (false != bRes)
			{
				if ((amrwrparams.getApiResult().getError() != 0) ||
				    (amrwrparams.getApiData() == null) ||
				    ((_aParams = amrwrparams.getApiData().get(VosConfigResources.PARAMETERS_NAME)) == null))
				{
					_logger.log(Level.SEVERE, "getParametersObject failed!!!");
					bRes = false;
				}
			}
			if (false != bRes)
			{
				for (i = 0; i < _aParams.size(); i++) 
				{
					_param = _aParams.get(i);
					_hmParams.put(_param.getParameterName(),_param.getParameterValue());
					_logger.log(Level.WARNING, String.format("Parameters. name: %s value: %s",_param.getParameterName(),_param.getParameterValue()));
				}
				ParametersReader.setProps(_hmParams);
			}
			
			_logger.log(((bRes==true)?Level.WARNING:Level.SEVERE), String.format("VOSDalConfigMain initialize %s: IP = %s, port = %d", ((bRes==true)?"succeeded":"failed!!!"), sIP, iPort));
		}
		else
		{
			_logger.log(Level.WARNING, String.format("not creating default instance. Service Name =  %s, IP = %s, port = %d", sServiceName, sIP, iPort));
		}
		return dgc;
	}
	
	
	
	
	
	
	public static DalConfigClient getInstance() 
	{
		return m_instance;
	}

	
	
	
	
	
	
	
	private static DalConfigClient createDefaultInstance() 
	{
		return initDefaultDalConfigClient ();
	}

	public DalConfigClient.FtpPlaceHolders getFtpPlaceHolders() 
	{
		return m_FtpPlaceHolders;
	}

	public DalConfigClient.Services getServices() 
	{
		return m_Services;
	}
		
	public DalConfigClient.FtpServers getFtpServers() 
	{
		return m_FtpServers;
	}

	public DalConfigClient.Operands getOperands() 
	{
		return m_Operands;
	}

	public DalConfigClient.Parameters getParameters() 
	{
		return m_Parameters;
	}
	
	public DalConfigClient.Sites getSites() 
	{
		return m_Sites;
	}
	
	public DalConfigClient.Plcs getPlcs() 
	{
		return m_Plcs;
	}
	
	/** class FtpPlaceHolders - exposes (db entity) ftp_place_holders APIs 
	 ** 
	 ** 1. ApiMultiResultWrapper<FtpPlaceHolder> getFtpPlaceHoldersObject(int serviceId)
	 **
	 **	2. ApiMultiResultWrapper<FtpPlaceHolder> updateFtpPlaceHolder(FtpPlaceHolder ftpPlaceHolder)
	 **/
	public static class FtpPlaceHolders 
	{
		private static final Logger logger = Logger.getLogger(FtpPlaceHolders.class.getName());
		private static ObjectMapper m_mapper = null;
		private static TypeReference<ApiMultiResultWrapper<FtpPlaceHolder>> m_ref = null;
		private String m_baseURL;
		@SuppressWarnings("unused")
		private static final boolean bInitialize = _init();

		private FtpPlaceHolders(String _baseURL) 
		{
			m_baseURL = _baseURL;
			// TODO Auto-generated constructor stub
		}


		private static boolean _init()
		{
			m_mapper = new ObjectMapper();
			 AnnotationIntrospector aiJaxb = new JaxbAnnotationIntrospector(TypeFactory.defaultInstance());
		     AnnotationIntrospector aiJackson = new JacksonAnnotationIntrospector();
		        // first Jaxb, second Jackson annotations
		    m_mapper.setAnnotationIntrospector(AnnotationIntrospector.pair(aiJaxb, aiJackson));
		    m_ref = new TypeReference<ApiMultiResultWrapper<FtpPlaceHolder>>() { };
			return true;
		}


		public ApiMultiResultWrapper<FtpPlaceHolder> getFtpPlaceHoldersObject(int serviceId)
		{
			String sError;
			String sRes;

			ApiMultiResultWrapper<FtpPlaceHolder> amrwr = null;
			try 
			{
				sRes = NetClient.callGetRaw(m_baseURL, "ftp_place_holders?service_id=" + Integer.toString(serviceId));
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

		
		public ApiMultiResultWrapper<FtpPlaceHolder> updateFtpPlaceHolder(FtpPlaceHolder ftpPlaceHolder)
		{
			String sError = null;
			String sRes = null;
			String sBody = null;

			ApiMultiResultWrapper<FtpPlaceHolder> amrwr = null;
			try 
			{
				sBody = m_mapper.writeValueAsString(ftpPlaceHolder);
				
				
				sRes = NetClient.callPostRaw(m_baseURL, "ftp_place_holders", sBody);
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

	/** class Services - exposes (db entity) services APIs 
	 ** 
	 ** 1. ApiMultiResultWrapper<Service> getServiceObject(String serviceName)
	 **
	 **	2. ApiMultiResultWrapper<Service> getServicesObject(int serviceType)
	 **/
	public static class Services 
	{
		private static final Logger logger = Logger.getLogger(Services.class.getName());
		private static ObjectMapper m_mapper = null;
		private static TypeReference<ApiMultiResultWrapper<Service>> m_ref = null;
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
		    m_ref = new TypeReference<ApiMultiResultWrapper<Service>>() { };
			return true;
		}
		
		private Services(String _baseURL) 
		{
			m_baseURL = _baseURL;
			// TODO Auto-generated constructor stub
		}

		public ApiMultiResultWrapper<Service> getServiceObject(String serviceName)
		{
			String sError;
			String sRes;

			ApiMultiResultWrapper<Service> amrwr = null;
			try 
			{
				sRes = NetClient.callGetRaw(m_baseURL, "services/" + serviceName);
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

		public ApiMultiResultWrapper<Service> getServicesObject(int serviceType)
		{
			String sError;
			String sRes;

			ApiMultiResultWrapper<Service> amrwr = null;
			try 
			{
				sRes = NetClient.callGetRaw(m_baseURL, "services?service_type=" + Integer.toString(serviceType));
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

	/** class Services - exposes (db entity) ftp_servers APIs 
	 ** 
	 ** 1. ApiMultiResultWrapper<FtpServer> getFtpServersObject(int serviceId)
	 **
	 **/
	public static class FtpServers 
	{
		private static final Logger logger = Logger.getLogger(FtpServers.class.getName());
		private static ObjectMapper m_mapper = null;
		private static TypeReference<ApiMultiResultWrapper<FtpServer>> m_ref = null;
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
		    m_ref = new TypeReference<ApiMultiResultWrapper<FtpServer>>() { };
			return true;
		}

		private FtpServers(String _baseURL) 
		{
			m_baseURL = _baseURL;
			// TODO Auto-generated constructor stub
		}

		public ApiMultiResultWrapper<FtpServer> getFtpServersObject(int serviceId)
		{
			String sError;
			String sRes;

			ApiMultiResultWrapper<FtpServer> amrwr = null;
			try 
			{
				sRes = NetClient.callGetRaw(m_baseURL, "ftp_servers?service_id=" + Integer.toString(serviceId));
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

	/** class Operands - exposes (db entity) operands APIs 
	 ** 
	 ** 1. ApiMultiResultWrapper<FtpServer> getFtpServersObject(int serviceId)
	 **
	 **/

	public static class Operands 
	{
		private static final Logger logger = Logger.getLogger(Services.class.getName());
		private static ObjectMapper m_mapper = null;
		private static TypeReference<ApiMultiResultWrapper<Operand>> m_ref = null;
		private String m_baseURL;
		@SuppressWarnings("unused")
		private static final boolean bInitialize = _init();

		private Operands(String _baseURL) 
		{
			m_baseURL = _baseURL;
		}


		private static boolean _init()
		{
			m_mapper = new ObjectMapper();
			 AnnotationIntrospector aiJaxb = new JaxbAnnotationIntrospector(TypeFactory.defaultInstance());
		     AnnotationIntrospector aiJackson = new JacksonAnnotationIntrospector();
		        // first Jaxb, second Jackson annotations
		    m_mapper.setAnnotationIntrospector(AnnotationIntrospector.pair(aiJaxb, aiJackson));
		    m_ref = new TypeReference<ApiMultiResultWrapper<Operand>>() { };
			return true;
		}

		
		public ApiMultiResultWrapper<Operand> getAllOperandsObject(int serviceId)
		{
			String sError;
			String sRes;

			ApiMultiResultWrapper<Operand> amrwr = null;
			try 
			{
				sRes = NetClient.callGetRaw(m_baseURL, "operands?service_id=" + Integer.toString(serviceId));
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
	
	
	/** class Parameters - exposes (db entity) parameters APIs 
	 ** 
	 ** 1. ApiMultiResultWrapper<FtpServer> getParametersObject(int serviceId)
	 **
	 **/
	public static class Parameters 
	{
		private static final Logger logger = Logger.getLogger(Parameters.class.getName());
		private static ObjectMapper m_mapper = null;
		private static TypeReference<ApiMultiResultWrapper<Parameter>> m_ref = null;
		private String m_baseURL;
		@SuppressWarnings("unused")
		private static final boolean bInitialize = _init();

		private Parameters(String _baseURL) 
		{
			m_baseURL = _baseURL;
		}


		private static boolean _init()
		{
			m_mapper = new ObjectMapper();
			 AnnotationIntrospector aiJaxb = new JaxbAnnotationIntrospector(TypeFactory.defaultInstance());
		     AnnotationIntrospector aiJackson = new JacksonAnnotationIntrospector();
		        // first Jaxb, second Jackson annotations
		    m_mapper.setAnnotationIntrospector(AnnotationIntrospector.pair(aiJaxb, aiJackson));
		    m_ref = new TypeReference<ApiMultiResultWrapper<Parameter>>() { };
			return true;
		}


		public ApiMultiResultWrapper<Parameter> getParametersObject(int serviceId)
		{
			String sError;
			String sRes;

			ApiMultiResultWrapper<Parameter> amrwr = null;
			try 
			{
				sRes = NetClient.callGetRaw(m_baseURL, "parameters?service_id=" + Integer.toString(serviceId));
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
	
	/** class Sites - exposes (db entity) sites APIs 
	 ** 
	 ** 1. ApiMultiResultWrapper<Site> getSitesObject()
	 **
	 **/
	public static class Sites 
	{
		private static final Logger logger = Logger.getLogger(Sites.class.getName());
		private static ObjectMapper m_mapper = null;
		private static TypeReference<ApiMultiResultWrapper<Site>> m_ref = null;
		private String m_baseURL;
		@SuppressWarnings("unused")
		private static final boolean bInitialize = _init();

		private Sites(String _baseURL) 
		{
			m_baseURL = _baseURL;
		}


		private static boolean _init()
		{
			m_mapper = new ObjectMapper();
			 AnnotationIntrospector aiJaxb = new JaxbAnnotationIntrospector(TypeFactory.defaultInstance());
		     AnnotationIntrospector aiJackson = new JacksonAnnotationIntrospector();
		        // first Jaxb, second Jackson annotations
		    m_mapper.setAnnotationIntrospector(AnnotationIntrospector.pair(aiJaxb, aiJackson));
		    m_ref = new TypeReference<ApiMultiResultWrapper<Site>>() { };
			return true;
		}


		public ApiMultiResultWrapper<Site> getSitesObject()
		{
			String sError;
			String sRes;

			ApiMultiResultWrapper<Site> amrwr = null;
			try 
			{
				sRes = NetClient.callGetRaw(m_baseURL, VosConfigResources.API_SITES_GET_RESOURCE_NAME);
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
	
	/** class Plcs - exposes (db entity) plcs and modbus_chunks APIs 
	 ** 
	 ** 1. ApiMultiResultWrapper<FtpServer> getParametersObject(int serviceId)
	 **
	 **/
	public static class Plcs 
	{
		private static final Logger logger = Logger.getLogger(Plcs.class.getName());
		private static ObjectMapper m_mapper = null;
		private static TypeReference<ApiMultiResultWrapper<Plc>> m_refPlc = null;
		private static TypeReference<ApiMultiResultWrapper<ModbusChunk>> m_refModbusChunk = null;
		private String m_baseURL;
		@SuppressWarnings("unused")
		private static final boolean bInitialize = _init();

		private Plcs(String _baseURL) 
		{
			m_baseURL = _baseURL;
		}


		private static boolean _init()
		{
			m_mapper = new ObjectMapper();
			 AnnotationIntrospector aiJaxb = new JaxbAnnotationIntrospector(TypeFactory.defaultInstance());
		     AnnotationIntrospector aiJackson = new JacksonAnnotationIntrospector();
		        // first Jaxb, second Jackson annotations
		    m_mapper.setAnnotationIntrospector(AnnotationIntrospector.pair(aiJaxb, aiJackson));
		    m_refPlc = new TypeReference<ApiMultiResultWrapper<Plc>>() { };
		    m_refModbusChunk = new TypeReference<ApiMultiResultWrapper<ModbusChunk>>() { };
			return true;
		}


		public ApiMultiResultWrapper<Plc> getServicePlcObject(int serviceId)
		{
			String sError;
			String sRes;

			ApiMultiResultWrapper<Plc> amrwr = null;
			try 
			{
				sRes = NetClient.callGetRaw(m_baseURL, "plcs?service_id=" + Integer.toString(serviceId));
				if(sRes != null && !sRes.isEmpty())
				{
					amrwr = m_mapper.readValue(sRes, m_refPlc);
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
		
		
		
		public ApiMultiResultWrapper<ModbusChunk> getModbusChunksObject(int plcId)
		{
			String sError;
			String sRes;

			ApiMultiResultWrapper<ModbusChunk> amrwr = null;
			try 
			{
				sRes = NetClient.callGetRaw(m_baseURL, "plcs/" + Integer.toString(plcId) +"/modbus_chunks");
				if(sRes != null && !sRes.isEmpty())
				{
					amrwr = m_mapper.readValue(sRes, m_refModbusChunk);
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
}
