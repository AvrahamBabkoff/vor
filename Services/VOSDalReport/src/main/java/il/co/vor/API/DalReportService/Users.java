package il.co.vor.API.DalReportService;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import il.co.vor.VOSDBConnection.SPExecuterAndJSONSerializer;
import il.co.vor.VOSDBConnection.SQLStatements;
import il.co.vor.common.VosConfigResources;
import il.co.vor.common.VosReportResources;

@Path(VosReportResources.USERS_NAME)
public class Users {
	private static final String m_strSpGetUser = SQLStatements.getSqlStatement(SQLStatementsParamNames.SP_GET_USER_NAME);
	private static final String m_strSpGetUserFactories = SQLStatements.getSqlStatement(SQLStatementsParamNames.SP_GET_USER_FACTORIES_NAME);

	@GET
	@Produces ("application/json")
	public String getUser(@DefaultValue("") @QueryParam(VosReportResources.USERS_PROP_USER_NAME_NAME) String userName, 
			@DefaultValue("") @QueryParam(VosReportResources.USERS_PROP_USER_PASSWORD_NAME) String password) 
	{
    	String strJASONResponse = "";
    	SPExecuterAndJSONSerializer spExec = new SPExecuterAndJSONSerializer();
    	//strJASONResponse = spExec.setSP("SELECT * FROM get_parameters (?)").
    	strJASONResponse = spExec.setSP(m_strSpGetUser).
    					   setParameters(userName, password).
    					   setResultSetNames(VosReportResources.USERS_NAME).
    					   ExecuteAndSerializeAsJSONString(null);
        return strJASONResponse;
	}

	@Path(VosReportResources.API_USERS_GET_FACTORIES_URI_TEMPLATE)
	@GET
	@Produces ("application/json")
	public String getUserFactories(@PathParam(VosReportResources.USERS_PROP_USER_ID_NAME) int userId) 
	{
    	String strJASONResponse = "";
    	SPExecuterAndJSONSerializer spExec = new SPExecuterAndJSONSerializer();
    	//strJASONResponse = spExec.setSP("SELECT * FROM get_parameters (?)").
    	strJASONResponse = spExec.setSP(m_strSpGetUserFactories).
    					   setParameters(userId).
    					   setResultSetNames(VosReportResources.FACTORIES_NAME).
    					   ExecuteAndSerializeAsJSONString(null);
        return strJASONResponse;
	}

}
