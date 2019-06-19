package il.co.vor.API.DalConfigService;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import il.co.vor.VOSDBConnection.SPExecuterAndJSONSerializer;
import il.co.vor.VOSDBConnection.SQLStatements;
import il.co.vor.common.VosConfigResources;

@Path(VosConfigResources.PARAMETERS_NAME)
public class Parameters 
{
	private static final String m_strSpGetParameters = SQLStatements.getSqlStatement(SQLStatementsParamNames.SP_GET_PARAMETERS_PARAM_NAME);

	@GET
	@Produces ("application/json")
	public String getParameters(@DefaultValue("-1") @QueryParam(VosConfigResources.SERVICES_PROP_SERVICE_ID_NAME) int serviceId) 
	{
    	String strJASONResponse = "";
    	SPExecuterAndJSONSerializer spExec = new SPExecuterAndJSONSerializer();
    	//strJASONResponse = spExec.setSP("SELECT * FROM get_parameters (?)").
    	strJASONResponse = spExec.setSP(m_strSpGetParameters).
    					   setParameters(serviceId).
    					   setResultSetNames(VosConfigResources.PARAMETERS_NAME).
    					   ExecuteAndSerializeAsJSONString(null);
        return strJASONResponse;
	}
}
