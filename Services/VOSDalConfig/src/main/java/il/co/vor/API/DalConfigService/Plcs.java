package il.co.vor.API.DalConfigService;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import il.co.vor.VOSDBConnection.SPExecuterAndJSONSerializer;
import il.co.vor.VOSDBConnection.SQLStatements;
import il.co.vor.common.VosConfigResources;

@Path(VosConfigResources.PLCS_NAME)
public class Plcs 
{
	private static final String m_strSpGetServicePlc = SQLStatements.getSqlStatement(SQLStatementsParamNames.SP_GET_SERVICE_PLC_PARAM_NAME);
	private static final String m_strSpGetModbusChunks = SQLStatements.getSqlStatement(SQLStatementsParamNames.SP_GET_MODBUS_CHUNKS_PARAM_NAME);
	
	@GET
	@Produces ("application/json")
	public String getServicePlc(@DefaultValue("-1") @QueryParam(VosConfigResources.SERVICES_PROP_SERVICE_ID_NAME) int serviceId) 
	{
		String strJASONResponse = "";
    	SPExecuterAndJSONSerializer spExec = new SPExecuterAndJSONSerializer();
    	strJASONResponse = spExec.
    					   //setSP("SELECT * FROM get_plc (?)").
    					   setSP(m_strSpGetServicePlc).
    					   setParameters(serviceId).
    					   setResultSetNames("plcs").
    					   ExecuteAndSerializeAsJSONString(null);
        return strJASONResponse;
	}

	@Path(VosConfigResources.API_MODBUS_CHUNKS_GET_URI_TEMPLATE)
	@GET
	@Produces ("application/json")
	public String getModbusChunks(@PathParam(VosConfigResources.PLCS_PROP_PLC_ID_NAME) int plcId)
	{
		String strJASONResponse = "";
    	SPExecuterAndJSONSerializer spExec = new SPExecuterAndJSONSerializer();
    	strJASONResponse = spExec.
    					   //setSP("SELECT * FROM get_plc_modbus_chunks (?)").
    					   setSP(m_strSpGetModbusChunks).
    					   setParameters(plcId).
    					   setResultSetNames(VosConfigResources.MODBUS_CHUNKS_NAME).
    					   ExecuteAndSerializeAsJSONString(null);
        return strJASONResponse;
	}
}
