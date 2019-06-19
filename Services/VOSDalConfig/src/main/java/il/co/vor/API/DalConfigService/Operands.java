package il.co.vor.API.DalConfigService;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import il.co.vor.VOSDBConnection.SPExecuterAndJSONSerializer;
import il.co.vor.VOSDBConnection.SQLStatements;
import il.co.vor.common.VosConfigResources;


@Path(VosConfigResources.OPERANDS_NAME)
public class Operands 
{
	private static final String m_strSpGetAllOperands = SQLStatements.getSqlStatement(SQLStatementsParamNames.SP_GET_ALL_OPERANDS_PARAM_NAME);
	
	@GET
	@Produces ("application/json")
	public String getAllOperands(@DefaultValue("-1") @QueryParam(VosConfigResources.SERVICES_PROP_SERVICE_ID_NAME) int serviceId) 
	{
		String strJASONResponse = "";
    	SPExecuterAndJSONSerializer spExec = new SPExecuterAndJSONSerializer();
    	//spExec.setSP("SELECT * FROM get_all_operands (?)");
    	spExec.setSP(m_strSpGetAllOperands);
    	spExec.setResultSetNames(VosConfigResources.OPERANDS_NAME);
    	spExec.setParameters(serviceId);
    	spExec.setRootObjectProperties(	
    			VosConfigResources.OPERANDS_PROP_OPERAND_ID_NAME, 
    			VosConfigResources.EQUIPMENTS_PROP_EQUIPMENT_ID, 
    			VosConfigResources.OPERANDS_PROP_OPERAND_TYPE_NAME, 
    			VosConfigResources.OPERANDS_PROP_OPERAND_NAME_NAME, 
    			VosConfigResources.OPERANDS_PROP_OPERAND_DESCRIPTION_NAME, 
    			VosConfigResources.OPERANDS_PROP_OPERAND_FORMAT_NAME, 
    			VosConfigResources.OPERANDS_PROP_OPERAND_LOG_BY_PERCENTAGE_VALUE_NAME, 
    			VosConfigResources.OPERANDS_PROP_OPERAND_LOG_BY_INTERVAL_VALUE_NAME, 
    			VosConfigResources.PHYSICAL_UNITS_PROP_PHYSICAL_UNIT_ID_NAME, 
    			VosConfigResources.OPERANDS_PROP_OPERAND_FLAGS_NAME, 
    			VosConfigResources.OPERANDS_PROP_OPERAND_DATA_TYPE_NAME);
    	
    	spExec.addObject(
    			VosConfigResources.REGISTER_OBJECT_NAME, 		
    			VosConfigResources.REGISTERS_PROP_REGISTER_ID_NAME,
    			VosConfigResources.REGISTERS_PROP_REGISTER_NAME_NAME , 
    			VosConfigResources.REGISTERS_PROP_REGISTER_DESCRIPTION_NAME, 
    			VosConfigResources.REGISTERS_PROP_REGISTER_REFERENCE_NAME, 
    			VosConfigResources.REGISTERS_PROP_REGISTER_SIZE_NAME, 
    			VosConfigResources.REGISTERS_PROP_REGISTER_TYPE_NAME
    			);
    	spExec.addObject(
    			VosConfigResources.CALC_OPERAND_OBJECT_NAME, 		
    			VosConfigResources.CALC_OPERANDS_PROP_CALC_OPERAND_ID_NAME, 
    			VosConfigResources.CALC_OPERANDS_PROP_CALC_OPERAND_NAME_NAME, 
    			VosConfigResources.CALC_OPERANDS_PROP_CALC_OPERAND_DESCRIPTION_NAME, 
    			VosConfigResources.CALC_OPERANDS_PROP_CALC_OPERAND_FORMULA_PREFIX_NAME, 
    			VosConfigResources.CALC_OPERANDS_PROP_CALC_OPERAND_FORMULA_NAME,
    			VosConfigResources.CALC_OPERANDS_PROP_CALC_OPERAND_ORDINAL_NAME
    			);
    	spExec.addObject(
    			VosConfigResources.METER_OBJECT_NAME, 		
    			VosConfigResources.METERS_PROP_METER_ID_NAME, 
    			VosConfigResources.METERS_PROP_METER_NAME_NAME, 
    			VosConfigResources.METERS_PROP_METER_DESCRIPTION_NAME, 
    			VosConfigResources.METERS_PROP_METER_TYPE_NAME, 
    			VosConfigResources.METERS_PROP_METER_TEMP_TYPE_NAME, 
    			VosConfigResources.METERS_PROP_MAX_CONSUMPTION_NAME);
    	strJASONResponse = spExec.ExecuteAndSerializeNestedAsJSONString();
        return strJASONResponse;
	}

}
