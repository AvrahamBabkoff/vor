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

@Path(VosConfigResources.ALGORITHMS_NAME)
public class Algorithms {
	private static final String m_strSpGetCurrAlgorithm = SQLStatements.getSqlStatement(SQLStatementsParamNames.SP_GET_CURRENT_ALGORITHM_NAME);

	@Path(VosReportResources.ALGORITHMS_CURR_NAME)
	@GET
	@Produces ("application/json")
	public String getCurrAlgorithm(
			@DefaultValue("-1") @QueryParam(VosConfigResources.SITES_PROP_SITE_ID_NAME) int siteId)
	{
    	String strJASONResponse = "";
    	SPExecuterAndJSONSerializer spExec = new SPExecuterAndJSONSerializer();
    	//strJASONResponse = spExec.setSP("SELECT * FROM get_parameters (?)").
    	strJASONResponse = spExec.setSP(m_strSpGetCurrAlgorithm).
				   setParameters(siteId).
    					   setResultSetNames(VosConfigResources.ALGORITHMS_NAME).
    					   ExecuteAndSerializeAsJSONString(null);
        return strJASONResponse;
	}

}
