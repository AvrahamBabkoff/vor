package il.co.vor.API.DalConfigService;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import il.co.vor.VOSDBConnection.SPExecuterAndJSONSerializer;
import il.co.vor.VOSDBConnection.SQLStatements;
import il.co.vor.common.VosConfigResources;

@Path(VosConfigResources.SITES_NAME)
public class Sites 
{
	private static final String m_strSpGetSites = SQLStatements.getSqlStatement(SQLStatementsParamNames.SP_GET_SITES_PARAM_NAME);

	@GET
	@Produces ("application/json")
	public String getSites() 
	{
    	String strJASONResponse = "";
    	SPExecuterAndJSONSerializer spExec = new SPExecuterAndJSONSerializer();
    	//strJASONResponse = spExec.setSP("SELECT * FROM get_sites ()").
    	strJASONResponse = spExec.setSP(m_strSpGetSites).
    					   setResultSetNames(VosConfigResources.SITES_NAME).
    					   ExecuteAndSerializeAsJSONString(null);
        return strJASONResponse;
	}
}
