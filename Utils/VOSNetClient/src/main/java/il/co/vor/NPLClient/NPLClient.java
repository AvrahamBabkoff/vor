package il.co.vor.NPLClient;

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
import il.co.vor.DalDataClient.DalDataClient;
import il.co.vor.DalDataObjects.CalcOperandProperty;
import il.co.vor.NPLObjects.OperandSnapshot;
import il.co.vor.VOSNetClient.GenClient;
import il.co.vor.VOSNetClient.NetClient;
import il.co.vor.common.Constants;
import il.co.vor.common.VosConfigResources;
import il.co.vor.common.VosDataResources;
import il.co.vor.common.VosNPLResources;

public class NPLClient extends GenClient
{
	private static Logger _logger = Logger.getLogger(NPLClient.class.getName());	
	private NPLClient.Operands m_Operands;
	
	public NPLClient(String sIP, int iPort)
	{
		super(sIP, iPort, Constants.NET_VOS_NPL_BASE_URI);
		m_Operands = new NPLClient.Operands(getBaseURL());
		_logger.log(Level.WARNING, String.format("NPLClient initialized: IP = %s, port = %d", sIP, iPort));
	}
	
	public NPLClient.Operands getOperands() 
	{
		return m_Operands;
	}
	
	public static class Operands 
	{
		private static final Logger logger = Logger.getLogger(Operands.class.getName());
		private static ObjectMapper m_mapper = null;
		private static TypeReference<ApiMultiResultWrapper<OperandSnapshot>> m_ref = null;
		private String m_baseURL;
		@SuppressWarnings("unused")
		private static final boolean bInitialize = _init();

		private Operands(String _baseURL) 
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
		    m_ref = new TypeReference<ApiMultiResultWrapper<OperandSnapshot>>() { };
			return true;
		}
		
		
		
		public ApiMultiResultWrapper<OperandSnapshot> GetOperandsSnapshot(){
			
			String sError = "";
			String sRes = "";
			String sUri = "";
			
			ApiMultiResultWrapper<OperandSnapshot> amrwr = null;
			try 
			{
				sUri = String.format("%s/%s", VosConfigResources.OPERANDS_NAME, VosNPLResources.API_SANPSHOT_OPERANDS_GET_RESOURCE_NAME);
				sRes = NetClient.callGetRaw(m_baseURL, sUri);
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
		
		public ApiMultiResultWrapper<CalcOperandProperty> ReloadCalcOperandsProperties()
		{
			String sError;
			String sRes;

			ApiMultiResultWrapper<CalcOperandProperty> amrwr = null;
			try 
			{
				sRes = NetClient.callGetRaw(m_baseURL, String.format("%s/%s",VosConfigResources.OPERANDS_NAME ,VosNPLResources.API_RELOAD_CALC_OPERANDS_PROPERTIES));
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
