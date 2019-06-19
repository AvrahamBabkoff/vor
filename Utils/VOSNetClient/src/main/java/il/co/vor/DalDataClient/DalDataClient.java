package il.co.vor.DalDataClient;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

import il.co.vor.ApiObjectsCommon.ApiMultiResultWrapper;
import il.co.vor.ApiObjectsCommon.NumberOfAffectedRows;
import il.co.vor.DalConfigClient.DalConfigClient;
import il.co.vor.DalConfigObjects.Service;
import il.co.vor.DalDataObjects.CalcOperandProperty;
import il.co.vor.DalDataObjects.FtpFile;
import il.co.vor.VOSNetClient.GenClient;
import il.co.vor.VOSNetClient.NetClient;
import il.co.vor.common.Constants;
import il.co.vor.common.Enums;
import il.co.vor.common.VosConfigResources;
import il.co.vor.common.VosDataResources;

public class DalDataClient  extends GenClient 
{

	private static DalDataClient.CalcOperandsProperties m_CalcOperandsProperties;
	private DalDataClient.FtpFiles m_FtpFiles;
	private static Logger _logger = Logger.getLogger(DalDataClient.class.getName());
	
	
	private static final DalDataClient m_instance = createDefaultInstance ();
	
	public DalDataClient(String sIP, int iPort) 
	{
		super(sIP, iPort, Constants.NET_VOS_DAL_DATA_ROOT_URI);
		m_FtpFiles = new DalDataClient.FtpFiles(getBaseURL());
		m_CalcOperandsProperties = new DalDataClient.CalcOperandsProperties(getBaseURL());
	}

	
	public static DalDataClient getInstance() 
	{
		return m_instance;
	}

	private static DalDataClient createDefaultInstance() 
	{
		return initDefaultDalDataClient ();
	}
	
	
	private static DalDataClient initDefaultDalDataClient() 
	{
		boolean bRes = true;
		String sIP = null;
		int iPort = 0;
		ApiMultiResultWrapper<Service> amrwrservice = null;
		ArrayList<Service> _aService = null;
		Service _service = null;
		DalConfigClient dcc = null;
		DalDataClient ddc = null;

		dcc = DalConfigClient.getInstance();
		if (null != dcc)
		{
			_logger.log(Level.WARNING, "Initializing VOSDalDataMain");
			bRes = ((amrwrservice = dcc.getServices().getServicesObject(Enums.ServiceType.DAL_DATA.ordinal())) != null);
			if (false != bRes)
			{
				if (amrwrservice.getApiResult().getError() != 0 ||
				   (amrwrservice.getApiData() == null) ||
				   (_aService = amrwrservice.getApiData().get(VosConfigResources.API_SERVICES_GET_RESOURCE_NAME)) == null ||
				   (_aService.size() != 1))
				{
					bRes = false;
				}
			}
			if (false != bRes)
			{
				_service = _aService.get(0);
				sIP = _service.getServiceAddressIp();
				iPort = _service.getServiceAddressPort();
				ddc = new DalDataClient(sIP, iPort);
				_logger.log(Level.WARNING, String.format("Default DalDataClient initialized: sIP = %s, iPort = %d", sIP, iPort));
			}
			else
			{
				_logger.log(Level.SEVERE, "DalDataClient failed to initialize");
			}		
		}
		else
		{
			_logger.log(Level.WARNING, "DalConfigClient is null. not creating default instance");
		}
		return ddc;
	}
			



	public DalDataClient.FtpFiles getFtpFiles() 
	{
		return m_FtpFiles;
	}
	
	public static class FtpFiles 
	{
		private static final Logger logger = Logger.getLogger(FtpFiles.class.getName());
		private static ObjectMapper m_mapper = null;
		private static TypeReference<ApiMultiResultWrapper<FtpFile>> m_ref = null;
		private static TypeReference<ApiMultiResultWrapper<NumberOfAffectedRows>> m_num_ref = null;
		private String m_baseURL;
		@SuppressWarnings("unused")
		private static final boolean bInitialize = _init();

		private FtpFiles(String _baseURL) 
		{
			m_baseURL = _baseURL;
		}

		private static boolean _init()
		{
			//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
			m_mapper = new ObjectMapper();
			 AnnotationIntrospector aiJaxb = new JaxbAnnotationIntrospector(TypeFactory.defaultInstance());
		     AnnotationIntrospector aiJackson = new JacksonAnnotationIntrospector();
		        // first Jaxb, second Jackson annotations
		    m_mapper.setAnnotationIntrospector(AnnotationIntrospector.pair(aiJaxb, aiJackson));
		    //m_mapper.setDateFormat(sdf);
		    m_ref = new TypeReference<ApiMultiResultWrapper<FtpFile>>() { };
		    m_num_ref = new TypeReference<ApiMultiResultWrapper<NumberOfAffectedRows>>() { };
			return true;
		}

		public ApiMultiResultWrapper<NumberOfAffectedRows> updateOrphandFtpFiles(int serviceId, int orphandFTPFilesTimeout)
		{
			String sError;
			String sRes;
			String sUri;

			ApiMultiResultWrapper<NumberOfAffectedRows> amrwr = null;
			try 
			{
				sUri = String.format("%s/%s?%s=%d&%s=%d", VosDataResources.FTP_FILES_NAME, VosDataResources.FTP_ORPHAND_FILES_NAME, VosConfigResources.SERVICES_PROP_SERVICE_ID_NAME, serviceId, Constants.NET_VOS_TIMEOUT_PARAM_NAME, orphandFTPFilesTimeout);
				
				sRes = NetClient.callPostRaw(m_baseURL, sUri, "");
				if(sRes != null && !sRes.isEmpty())
				{
					amrwr = m_mapper.readValue(sRes, m_num_ref);
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
		
		public ApiMultiResultWrapper<FtpFile> getFtpFilesObject()
		{
			String sError;
			String sRes;

			ApiMultiResultWrapper<FtpFile> amrwr = null;
			try 
			{
				sRes = NetClient.callGetRaw(m_baseURL, "ftp_files");
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

		public ApiMultiResultWrapper<FtpFile> insertFtpFiles(List<FtpFile> ftpFiles)
		{
			String sError = null;
			String sRes = null;
			String sBody = null;

			ApiMultiResultWrapper<FtpFile> amrwr = null;
			try 
			{
				sBody = m_mapper.writeValueAsString(ftpFiles);
				
				
				sRes = NetClient.callPostRaw(m_baseURL, "ftp_files?action=" + String.valueOf(Enums.FtpFileAction.INSERT.ordinal()), sBody);
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


		
		
		
		public ApiMultiResultWrapper<FtpFile> updateFtpFilesDownloadCompleted(List<FtpFile> ftpFiles)
		{
			String sError = null;
			String sRes = null;
			String sBody = null;

			ApiMultiResultWrapper<FtpFile> amrwr = null;
			try 
			{
				sBody = m_mapper.writeValueAsString(ftpFiles);
				
				
				sRes = NetClient.callPostRaw(m_baseURL, "ftp_files?action=" + String.valueOf(Enums.FtpFileAction.UPDATE_DOWNLOAD_STATUS.ordinal()), sBody);
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
		
		public ApiMultiResultWrapper<FtpFile> updateFtpFilesUploadCompleted(List<FtpFile> ftpFiles)
		{
			String sError = null;
			String sRes = null;
			String sBody = null;

			ApiMultiResultWrapper<FtpFile> amrwr = null;
			try 
			{
				sBody = m_mapper.writeValueAsString(ftpFiles);
				
				
				sRes = NetClient.callPostRaw(m_baseURL, "ftp_files?action=" + String.valueOf(Enums.FtpFileAction.UPDATE_UPLOAD_STATUS.ordinal()), sBody);
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
		
		
		public ApiMultiResultWrapper<FtpFile> getFtpFilesForDownload(int serviceId, int count)
		{		
			return getFtpFilesForFtpAction (Enums.FilePhase.DWL.ordinal(), serviceId, count);
		}
		

		public ApiMultiResultWrapper<FtpFile> getFtpFilesForUpload(int serviceId, int count)
		{		
			return getFtpFilesForFtpAction (Enums.FilePhase.UPLOAD.ordinal(), serviceId, count);
		}
		
		
		private ApiMultiResultWrapper<FtpFile> getFtpFilesForFtpAction(int action, int serviceId, int count)
		{
			String sError;
			String sRes;
			String sUri;

			ApiMultiResultWrapper<FtpFile> amrwr = null;
			try 
			{
				sUri = String.format("%s/%d?%s=%d&%s=%d", VosDataResources.FTP_FILES_NAME, action, VosConfigResources.SERVICES_PROP_SERVICE_ID_NAME, serviceId, Constants.NET_VOS_COUNT_PARAM_NAME, count);
				
				sRes = NetClient.callPostRaw(m_baseURL, sUri, "");
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
		
		public ApiMultiResultWrapper<FtpFile> bulkInsert(int serviceId, int count)
		{		
			String sError;
			String sRes;
			String sUri;

			ApiMultiResultWrapper<FtpFile> amrwr = null;
			try 
			{
				sUri = String.format("%s/bulk_insert?%s=%d&%s=%d", VosDataResources.FTP_FILES_NAME, VosConfigResources.SERVICES_PROP_SERVICE_ID_NAME, serviceId, Constants.NET_VOS_COUNT_PARAM_NAME, count);
				
				sRes = NetClient.callPostRaw(m_baseURL, sUri, "");
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
	
	public DalDataClient.CalcOperandsProperties getCalcOperandsProperties() 
	{
		return m_CalcOperandsProperties;
	}
	
	/** class CalcOperandsProperties - exposes (db entity) calc operands properties APIs 
	 ** 
	 ** 1. ApiMultiResultWrapper<CalcOperandsProperty> getFtpServersObject(int serviceId)
	 **
	 **/

	public static class CalcOperandsProperties 
	{
		private static final Logger logger = Logger.getLogger(CalcOperandsProperties.class.getName());
		private static ObjectMapper m_mapper = null;
		private static TypeReference<ApiMultiResultWrapper<CalcOperandProperty>> m_ref = null;
		private String m_baseURL;
		@SuppressWarnings("unused")
		private static final boolean bInitialize = _init();

		private CalcOperandsProperties(String _baseURL) 
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
		    m_ref = new TypeReference<ApiMultiResultWrapper<CalcOperandProperty>>() { };
			return true;
		}

		
		public ApiMultiResultWrapper<CalcOperandProperty> getCalcOperandsPropertiesObject(int serviceId)
		{
			String sError;
			String sRes;

			ApiMultiResultWrapper<CalcOperandProperty> amrwr = null;
			try 
			{
				sRes = NetClient.callGetRaw(m_baseURL, VosDataResources.CALC_OPERANDS_PROPERTIES_NAME+"?"+VosConfigResources.SERVICES_PROP_SERVICE_ID_NAME+"=" + Integer.toString(serviceId));
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
		
		public ApiMultiResultWrapper<CalcOperandProperty> updateCalcOperandProperties(int serviceId, List<CalcOperandProperty> properties)
		{
			String sError = null;
			String sRes = null;
			String sBody = null;

			ApiMultiResultWrapper<CalcOperandProperty> amrwr = null;
			try 
			{
				sBody = m_mapper.writeValueAsString(properties);
				
				
				sRes = NetClient.callPostRaw(m_baseURL, String.format("%s/%s?%s=%s",VosDataResources.CALC_OPERANDS_PROPERTIES_NAME ,VosDataResources.API_CALC_OPERANDS_PROPERTIES_UPDATE ,VosConfigResources.SERVICES_PROP_SERVICE_ID_NAME, Integer.toString(serviceId)),sBody);
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
}
