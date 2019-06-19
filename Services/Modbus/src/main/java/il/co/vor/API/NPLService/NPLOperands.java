package il.co.vor.API.NPLService;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import com.fasterxml.jackson.databind.ObjectMapper;

import il.co.vor.ApiObjectsCommon.ApiResult;
import il.co.vor.Modbus.OperandManager;
import il.co.vor.common.VosNPLResources;
import il.co.vor.common.VosConfigResources;


@Path(VosConfigResources.OPERANDS_NAME)
public class NPLOperands {
	
	private static final Logger logger = Logger.getLogger(NPLOperands.class.getName());
	private ApiResult m_ar = new ApiResult();
	private ObjectMapper m_mapper = null;
	
	@Path(VosNPLResources.API_SANPSHOT_OPERANDS_GET_RESOURCE_NAME)
	@GET
	@Produces ("application/json")
	public String GetOperandsSnapshot() {
		
		String strJASONResponse = "";

		try {
			
			strJASONResponse = OperandManager.GetOperandsSnapshotResult();
			
		} catch (Exception e) {
			logger.log(Level.SEVERE, String.format("Exception. %s", e.getMessage()));
		}
		
		return strJASONResponse;
	}
	
	@Path(VosNPLResources.API_SET_OPERAND_VALUE)
	@GET
	@Produces ("application/json")
	public String SetOperandValue(@DefaultValue("-1") @QueryParam(VosConfigResources.OPERANDS_PROP_OPERAND_ID_NAME) int operandId,
			@DefaultValue("") @QueryParam(VosNPLResources.API_OPERAND_VALUE) String operandVal) {
		
		String strJASONResponse = "";
		m_ar.setError(1);
		m_ar.setMessage("Failed to update value.");
		
		try {
			long lStart = 0;
			long lEnd = 0;

			lStart = System.currentTimeMillis();
			logger.log(Level.INFO, "API SetOperandValue Start");
			if (OperandManager.SetOperandValue(operandId, operandVal))
			{
				m_ar.setError(0);
				m_ar.setMessage("");
				strJASONResponse = m_mapper.writeValueAsString(m_ar);
			}
			logger.log(Level.INFO, "API SetOperandValue End");
			lEnd = System.currentTimeMillis();
			logger.log(Level.INFO, String.format("API SetOperandValue Duration: %d milliseconds", (lEnd - lStart)));
			
		} catch (Exception e) {
			logger.log(Level.SEVERE, String.format("Exception. %s", e.getMessage()));
		}
		
		return strJASONResponse;
	}
	
	@Path(VosNPLResources.API_RELOAD_CALC_OPERANDS_PROPERTIES)
	@GET
	@Produces ("application/json")
	public String ReloadCalcOperandsProperties() {
		
		String strJASONResponse = "";
		m_ar.setError(1);
		m_ar.setMessage("Failed to update.");
		
		try {
			long lStart = 0;
			long lEnd = 0;

			lStart = System.currentTimeMillis();
			logger.log(Level.INFO, "API ReloadCalcOperandsProperties Start");
			if (OperandManager.ReloadCalcOperandsProperties())
			{
				m_ar.setError(0);
				m_ar.setMessage("");
				strJASONResponse = m_mapper.writeValueAsString(m_ar);
			}
			logger.log(Level.INFO, "API ReloadCalcOperandsProperties End");
			lEnd = System.currentTimeMillis();
			logger.log(Level.INFO, String.format("API ReloadCalcOperandsProperties Duration: %d milliseconds", (lEnd - lStart)));
			
		} catch (Exception e) {
			logger.log(Level.SEVERE, String.format("Exception. %s", e.getMessage()));
		}
		
		return strJASONResponse;
	}
	

}
