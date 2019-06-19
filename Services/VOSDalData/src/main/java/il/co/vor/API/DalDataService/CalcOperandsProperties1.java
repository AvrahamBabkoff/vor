package il.co.vor.API.DalDataService;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import il.co.vor.VOSDBConnection.SPExecuterAndJSONSerializer;
import il.co.vor.VOSDBConnection.SQLStatements;
import il.co.vor.common.VosConfigResources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

import il.co.vor.ApiObjectsCommon.ApiMultiResultWrapper;
import il.co.vor.DalDataObjects.CalcOperandProperty;
import il.co.vor.DalDataObjects.FtpFile;
import il.co.vor.VOSDBConnection.*;
import il.co.vor.common.Constants;
import il.co.vor.common.Enums;
//import il.co.vor.VOSDBConnection.BatchExecuter;
//import il.co.vor.VOSDBConnection.SPExecuterAndJSONSerializer;
//import il.co.vor.VOSDBConnection.SQLStatements;
import il.co.vor.common.VosConfigResources;
import il.co.vor.common.VosDataResources;

@Path(VosDataResources.CALC_OPERANDS_PROPERTIES_NAME)
public class CalcOperandsProperties1 
{
	private static final Logger logger = Logger.getLogger(CalcOperandsProperties1.class.getName());
	private static ObjectMapper m_mapper = null;
	private static TypeReference<ApiMultiResultWrapper<CalcOperandProperty>> m_ref = null;

	private static final String m_strSpGetCalcOperandsProperties = SQLStatements.getSqlStatement(SQLStatementsParamNames.SP_GET_CALC_OPERANDS_PROPERTIES_NAME);
	private static final String m_strSpArchiveProperties = SQLStatements.getSqlStatement(SQLStatementsParamNames.SP_ARCHIVE_PROPERTIES_PARAM_NAME);

	private static final String m_strSpInsertProperties = String.format(SQLStatements.getSqlStatement(SQLStatementsParamNames.SQL_INSERT_PROPERTIES_PARAM_NAME),
			VosDataResources.CALC_OPERANDS_PROPERTIES_NAME,
			VosDataResources.CALC_OPERANDS_PROPERTIES_PROPERTY_NAME_NAME,
			VosDataResources.CALC_OPERANDS_PROPERTIES_PROPERTY_TYPE_NAME,
			VosDataResources.CALC_OPERANDS_PROPERTIES_PROPERTY_DESCRIPTION_NAME,
			VosConfigResources.SERVICES_PROP_SERVICE_ID_NAME,
			VosDataResources.CALC_OPERANDS_PROPERTIES_PROPERTY_VAL_NAME
			);
	
//	@SuppressWarnings("unused")
//	private static final boolean bInitialize = _init();
//
//	private static boolean _init()
//	{
//		//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
//		m_mapper = new ObjectMapper();
//		 AnnotationIntrospector aiJaxb = new JaxbAnnotationIntrospector(TypeFactory.defaultInstance());
//	     AnnotationIntrospector aiJackson = new JacksonAnnotationIntrospector();
//	        // first Jaxb, second Jackson annotations
//	    m_mapper.setAnnotationIntrospector(AnnotationIntrospector.pair(aiJaxb, aiJackson));
//	
//	    m_ref = new TypeReference<ApiMultiResultWrapper<CalcOperandProperty>>() { };
//	    
//		return true;
//	}
	
	@Path(VosDataResources.CALC_OPERANDS_PROPERTIES_NAME)
	@GET
	@Produces ("application/json")
	public String getCalcOperandsProperties(@DefaultValue("-1") @QueryParam(VosConfigResources.SERVICES_PROP_SERVICE_ID_NAME) int serviceId)
	{
    	String strJASONResponse = "";
    	SPExecuterAndJSONSerializer spExec = new SPExecuterAndJSONSerializer();
    	//strJASONResponse = spExec.setSP("SELECT * FROM get_parameters (?)").
    	strJASONResponse = spExec.setSP(m_strSpGetCalcOperandsProperties).
    					   setParameters(serviceId).
    					   setResultSetNames(VosDataResources.CALC_OPERANDS_PROPERTIES_NAME).
    					   ExecuteAndSerializeAsJSONString(null);
        return strJASONResponse;
	}
	
//	@Path(VosDataResources.API_CALC_OPERANDS_PROPERTIES_UPDATE)
//	@POST
//	@Consumes("application/json")
//	@Produces("application/json")
//	public /*List<CalcOperandProperty>*/ String updateProperties
//	(
//			@DefaultValue("-1") @QueryParam(VosConfigResources.SERVICES_PROP_SERVICE_ID_NAME) int serviceId,
//			List<CalcOperandProperty> calcOperandProperties
//	) 
//	{
//    	String strJASONResponse = doUpdate(serviceId, calcOperandProperties);
//
//		return strJASONResponse;
//	}	
//
//	
//	
//	private String doUpdate(int serviceId, List<CalcOperandProperty> calcOperandProperties)
//	{
//		int i = 0;
//		BatchExecuter be = null;
//		CalcOperandProperty property = null;
//    	String strJASONResponse = "";
//    	SPExecuterAndJSONSerializer spExec = null;
//    	String sErrorCode = "";
//    	int iError = 0;
//
//    	
//		be = new BatchExecuter();
//		spExec = new SPExecuterAndJSONSerializer();
//		try 
//		{
//			// call procedure to insert current properties to archive table
//			be.setStatement(m_strSpArchiveProperties);
//			be.addBatch(serviceId);
//			be.executeBatch(false);
//			
//			// call procedure to insert new records
//			be.setStatement(m_strSpInsertProperties);
//			
//			for (i = 0; i < calcOperandProperties.size(); i++)
//			{
//				property = calcOperandProperties.get(i);
//				be.addBatch(property.getPropertyName(),
//							property.getPropertyType(),
//							property.getPropertyDescription(),
//							serviceId,
//							property.getPropertyVal());
//			}
//			be.executeBatch(true);
//			strJASONResponse = spExec.setSP(m_strSpGetCalcOperandsProperties).
//					   setParameters(serviceId).
//					   setResultSetNames(VosDataResources.CALC_OPERANDS_PROPERTIES_NAME).
//					   ExecuteAndSerializeAsJSONString(null);
//			
//		} 
//		catch (Exception e) 
//		{
//			iError = -1;
//			sErrorCode = e.getMessage();
//			logger.log(Level.SEVERE, String.format("Exception. %s"), sErrorCode);
//			strJASONResponse = String.format("{\"Meta\":{\"error\":%d,\"message\":\"%s\"}}", iError, sErrorCode);
//		}
//
//		finally
//    	{
//    		if (null != be)
//    		{
//				be.close();
//    		}
//    	}
//		
//		//return ftpFiles.get(3);
//		return strJASONResponse;
//		
//	}
	
	
}

