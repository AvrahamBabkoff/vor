package il.co.vor.API.DalConfigService;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;

//import java.util.HashMap;
//import java.util.Map;

//import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
//import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import il.co.vor.DalConfigObjects.Service;
//import il.co.vor.DalConfigObjects.Service;
import il.co.vor.VOSDBConnection.SPExecuterAndJSONSerializer;
import il.co.vor.VOSDBConnection.SQLStatements;
import il.co.vor.common.VosConfigResources;

@Path(VosConfigResources.SERVICES_NAME)
public class Services 
{
	private static final String m_strSpGetService = SQLStatements.getSqlStatement(SQLStatementsParamNames.SP_GET_SERVICE_PARAM_NAME);
	private static final String m_strSpGetServices = SQLStatements.getSqlStatement(SQLStatementsParamNames.SP_GET_SERVICES_PARAM_NAME);

	@Path(VosConfigResources.API_SERVICES_GET_BY_NAME_URI_TEMPLATE)
	@GET
	@Produces ("application/json")
	public String getService(@PathParam(VosConfigResources.SERVICES_PROP_SERVICE_NAME_NAME) String serviceName) 
	{
    	String strJASONResponse = "";
    	strJASONResponse = getServiceImpl (serviceName);
        return strJASONResponse;
    }

	public static String getServiceImpl (String serviceName)
	{
    	String strJASONResponse = "";
		
    	SPExecuterAndJSONSerializer spExec = new SPExecuterAndJSONSerializer();
    	//strJASONResponse = spExec.setSP("SELECT * FROM get_service (?)").setParameters(serviceName).setResultSetNames(VosConfigResources.SERVICES_NAME).ExecuteAndSerializeAsJSONString();
    	strJASONResponse = spExec.setSP(m_strSpGetService).setParameters(serviceName).setResultSetNames(VosConfigResources.SERVICES_NAME).ExecuteAndSerializeAsJSONString(null);
        return strJASONResponse;
	}

	@GET
	@Produces ("application/json")
	public String getServices(@DefaultValue("-1") @QueryParam(VosConfigResources.SERVICES_PROP_SERVICE_TYPE_NAME) int serviceType) 
	{
    	String strJASONResponse = "";
    	SPExecuterAndJSONSerializer spExec = new SPExecuterAndJSONSerializer();
    	//strJASONResponse = spExec.setSP("SELECT * FROM get_parameters (?)").
    	strJASONResponse = spExec.setSP(m_strSpGetServices).
    					   setParameters(serviceType).
    					   setResultSetNames(VosConfigResources.SERVICES_NAME).
    					   ExecuteAndSerializeAsJSONString(null);
        return strJASONResponse;
	}
	
	
	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public Service updateService(Service service) 
	{
		return service;
	}
	
/*
	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public Map<String, Service> updateService(Service service) 
	{
		Map<String, Service> services = new HashMap<>();
		services.put("the grand service", service);
		return services;
	}
	
	

	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public Service updateService(Service service) 
	{
		VOSLogger.setLogLevel(service.getServiceDescription());
		return service;
	}
	
	
	
	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public List<Service> updateService(Service service) 
	{
		List<Service> services = new ArrayList<>();
		services.add(service);
		return services;
	}


	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public Map<String, Service> updateService(Service service) 
	{
		Map<String, Service> services = new HashMap<>();
		services.put("the grand service", service);
		return services;
	}

	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public List<Service> updateService(Service service) 
	{
		List<Service> services = new ArrayList<>();
		services.add(service);
		return services;
	}

	
	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public Service updateService(Service service) 
	{
		return service;
	}
	
	
	
	
	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public String updateService(Service service) 
	{
		return "{}";
	}

	
	
	@POST
	@Consumes("text/plain")
	public void postClichedMessage(String message) 
	{
	    // Store the message
		int i = 0;
		i++;
	}
	*/
}
