package il.co.vor.Modbus;
import java.io.Serializable;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.StringJoiner;

import il.co.vor.common.Constants;

public class MeterObj implements IFileDataObj,Serializable {
	
	private int meter_data_id = -1;
	private int meter_id = -1;
	private ZonedDateTime meter_data_time = null;
	private String str_data_time = "";
	private String meter_data_val = "";
	private int taoz_type = -1;
	private int meter_temp_type = -1;
	private boolean is_auto = false;
	
	public MeterObj(int meterDataID, int meterID, ZonedDateTime meterDataTime, String strDataTime, String meterDataVal, int taozType, int meterTempType, boolean isAuto) {
		meter_data_id = meterDataID;
		meter_id = meterID;
		meter_data_time = meterDataTime;
		meter_data_val = meterDataVal;
		taoz_type = taozType;
		meter_temp_type = meterTempType;
		is_auto = isAuto;
		str_data_time = strDataTime;
	}
	public int GetMeterDataID() {
		return meter_data_id;
	}
	public void SetMeterDataID(int meter_data_id) {
		this.meter_data_id = meter_data_id;
	}
	public int GetMeterID() {
		return meter_id;
	}
	public void SetMeterID(int meter_id) {
		this.meter_id = meter_id;
	}
	public ZonedDateTime GetMeterDataTime() {
		return meter_data_time;
	}
	public void SetMeterDataTime(ZonedDateTime meter_data_time) {
		this.meter_data_time = meter_data_time;
	}
	public String GetMeterDataVal() {
		return meter_data_val;
	}
	public void SetMeterDataVal(String meter_data_val) {
		this.meter_data_val = meter_data_val;
	}
	public int GetTaozType() {
		return taoz_type;
	}
	public void SetTaozType(int taoz_type) {
		this.taoz_type = taoz_type;
	}
	public int GetMeterTempType() {
		return meter_temp_type;
	}
	public void SetMeterTempType(int meter_temp_type) {
		this.meter_temp_type = meter_temp_type;
	}
	public boolean isIsAuto() {
		return is_auto;
	}
	public void SetIsAuto(boolean is_auto) {
		this.is_auto = is_auto;
	}
	
	public String toString(){
		
		//String curr_val = "";
		String ret = "";
		
		StringBuilder sb = new StringBuilder();
		//curr_val = DateTimeFormatter.ofPattern(Constants.CSV_DATE_FORMAT).format(meter_data_time.withZoneSameInstant(ZoneOffset.UTC));
		
		sb.append(String.valueOf(meter_id)).append(Constants.CSV_COMMA_DELIMITER).append(str_data_time).append(Constants.CSV_COMMA_DELIMITER).append(meter_data_val).append(Constants.CSV_COMMA_DELIMITER).append(String.valueOf(taoz_type)).append(Constants.CSV_COMMA_DELIMITER).append(String.valueOf(meter_temp_type)).append(Constants.CSV_COMMA_DELIMITER).append(String.valueOf(NPLHelper.GetBooleanAsInt(is_auto)));
		
		ret = sb.toString();
        return  ret;
    }
	
	public StringBuilder toString(StringBuilder _sb){
		
		//String curr_val = "";
		//String ret = "";
		
		//StringBuilder sb = new StringBuilder();
		//curr_val = DateTimeFormatter.ofPattern(Constants.CSV_DATE_FORMAT).format(meter_data_time.withZoneSameInstant(ZoneOffset.UTC));
		
		if (meter_data_val != null)
		{
			_sb.append(String.valueOf(meter_id)).append(Constants.CSV_COMMA_DELIMITER).append(str_data_time).append(Constants.CSV_COMMA_DELIMITER).append(meter_data_val).append(Constants.CSV_COMMA_DELIMITER).append(String.valueOf(taoz_type)).append(Constants.CSV_COMMA_DELIMITER).append(String.valueOf(meter_temp_type)).append(Constants.CSV_COMMA_DELIMITER).append(String.valueOf(NPLHelper.GetBooleanAsInt(is_auto)));
		}
		//ret = _sb.toString();
		
        return  _sb;
    }
	
	@Override
	public ZonedDateTime GetLogTime() {
		// TODO Auto-generated method stub
		return GetMeterDataTime();
	}
}
