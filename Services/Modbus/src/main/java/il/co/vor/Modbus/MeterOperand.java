package il.co.vor.Modbus;
import org.json.JSONObject;

import il.co.vor.common.VosConfigResources;

public class MeterOperand {
	
	private int meter_id = -1;
	private int operand_id = -1;
	private int meter_temp_type = -1;
	
	public MeterOperand(int operandID, int meterID, int meterTempType){
		operand_id = operandID;
		meter_id = meterID;
		meter_temp_type = meterTempType;
	}
	
	public MeterOperand(int operandID, JSONObject jsono)
	{
	     this(operandID, jsono.getInt(VosConfigResources.METERS_PROP_METER_ID_NAME),jsono.getInt(VosConfigResources.METERS_PROP_METER_TEMP_TYPE_NAME));
	}
	
	public int GetMeterID() {
		return meter_id;
	}
	public void SetMeterID(int meter_id) {
		this.meter_id = meter_id;
	}
	public int GetOperandID() {
		return operand_id;
	}
	public void SetOperandID(int operand_id) {
		this.operand_id = operand_id;
	}
	public int GetMeterTempType() {
		return meter_temp_type;
	}
	public void SetMeterTempType(int meter_temp_type) {
		this.meter_temp_type = meter_temp_type;
	}
	
	
}
