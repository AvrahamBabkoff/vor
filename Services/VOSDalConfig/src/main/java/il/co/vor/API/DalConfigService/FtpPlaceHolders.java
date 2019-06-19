package il.co.vor.API.DalConfigService;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import il.co.vor.DalConfigObjects.FtpPlaceHolder;
import il.co.vor.VOSDBConnection.SPExecuterAndJSONSerializer;
import il.co.vor.VOSDBConnection.SQLStatements;
import il.co.vor.common.VosConfigResources;


@Path(VosConfigResources.FTP_PLACE_HOLDERS_NAME)
public class FtpPlaceHolders 
{
	private static final String m_strSpGetFtpPlaceHolders = SQLStatements.getSqlStatement(SQLStatementsParamNames.SP_GET_FTP_PLACE_HOLDERS_PARAM_NAME);
	private static final String m_strSpUpsertFtpPlaceHolders = SQLStatements.getSqlStatement(SQLStatementsParamNames.SP_UPSERT_FTP_PLACE_HOLDERS_PARAM_NAME);

	@GET
	@Produces ("application/json")
	public String getFtpServers(@DefaultValue("-1") @QueryParam(VosConfigResources.SERVICES_PROP_SERVICE_ID_NAME) int serviceId) 
	{
    	String strJASONResponse = "";
    	SPExecuterAndJSONSerializer spExec = new SPExecuterAndJSONSerializer();
    	
    	strJASONResponse = spExec.setSP(m_strSpGetFtpPlaceHolders).
    					   setParameters(serviceId).
    					   setResultSetNames(VosConfigResources.FTP_PLACE_HOLDERS_NAME).
    					   ExecuteAndSerializeAsJSONString(null);
        return strJASONResponse;
	}
	
	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public String updateFtpPlaceHolders(FtpPlaceHolder ftpPlaceHolder) 
	{
    	String strJASONResponse = "";
    	SPExecuterAndJSONSerializer spExec = null;
    	if (null != ftpPlaceHolder)
    	{
    		spExec = new SPExecuterAndJSONSerializer();
    	
    		strJASONResponse = spExec.setSP(m_strSpUpsertFtpPlaceHolders).
    					   setParameters(ftpPlaceHolder.getFtpFileType(),
    							   		 ftpPlaceHolder.getSiteId(),
    							   		ftpPlaceHolder.getFtpPlaceHolderFolder()).
    					   setResultSetNames(VosConfigResources.FTP_PLACE_HOLDERS_NAME).
    					   ExecuteAndSerializeAsJSONString(null);
    	}
        return strJASONResponse;
	}	
	
	
}
