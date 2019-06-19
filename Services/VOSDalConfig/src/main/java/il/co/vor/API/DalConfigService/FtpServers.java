package il.co.vor.API.DalConfigService;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import il.co.vor.VOSDBConnection.SPExecuterAndJSONSerializer;
import il.co.vor.VOSDBConnection.SQLStatements;
import il.co.vor.common.VosConfigResources;

@Path(VosConfigResources.FTP_SERVERS_NAME)
public class FtpServers 
{
	private static final String m_strSpGetFtpServers = SQLStatements.getSqlStatement(SQLStatementsParamNames.SP_GET_FTP_SERVERS_PARAM_NAME);

	@GET
	@Produces ("application/json")
	public String getFtpServers(@DefaultValue("-1") @QueryParam(VosConfigResources.SERVICES_PROP_SERVICE_ID_NAME) int serviceId) 
	{
    	String strJASONResponse = "";
    	SPExecuterAndJSONSerializer spExec = new SPExecuterAndJSONSerializer();
    	//strJASONResponse = spExec.setSP("SELECT * FROM get_parameters (?)").
    	spExec.setSP(m_strSpGetFtpServers);
    	spExec.setParameters(serviceId);
    	spExec.setResultSetNames(VosConfigResources.FTP_SERVERS_NAME);
    	spExec.setRootObjectProperties(
    							   VosConfigResources.FTP_SERVERS_PROP_FTP_SERVER_ID_NAME,
    							   VosConfigResources.FTP_SERVERS_PROP_FTP_SERVER_NAME_NAME,
    							   VosConfigResources.FTP_SERVERS_PROP_FTP_SERVER_DESCRIPTION_NAME,
    							   VosConfigResources.FTP_SERVERS_PROP_FTP_SERVER_SITE_TYPE_NAME,
    							   VosConfigResources.FTP_SERVERS_PROP_FTP_SERVER_ADDRESS_IP_NAME,
    							   VosConfigResources.FTP_SERVERS_PROP_FTP_SERVER_ADDRESS_PORT_NAME,
    							   VosConfigResources.FTP_SERVERS_PROP_FTP_SERVER_USER_NAME_NAME,
    							   VosConfigResources.FTP_SERVERS_PROP_FTP_SERVER_USER_PASSWORD_NAME,
    							   VosConfigResources.FTP_SERVERS_PROP_FTP_SERVER_KEY_STORE_PATH_NAME,
    							   VosConfigResources.FTP_SERVERS_PROP_FTP_SERVER_MAX_PARALLEL_ACTIONS_NAME,
    							   VosConfigResources.FTP_SERVERS_PROP_FTP_SERVER_SITE_TYPE_NAME,
    							   VosConfigResources.FTP_SERVERS_PROP_FTP_SERVER_ROOT_PATH_NAME,
    							   VosConfigResources.SERVICES_PROP_SERVICE_ID_NAME);
    	spExec.addObject(
    							   VosConfigResources.SITE_OBJECT_NAME, 
    							   VosConfigResources.SITES_PROP_SITE_ID_NAME,
    							   VosConfigResources.SITES_PROP_SITE_DESCRIPTION_NAME,
    							   VosConfigResources.SITES_PROP_SITE_NAME_NAME);
    					   
    	strJASONResponse = spExec.ExecuteAndSerializeNestedAsJSONString();
        return strJASONResponse;
	}

}
