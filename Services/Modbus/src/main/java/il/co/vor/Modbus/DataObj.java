package il.co.vor.Modbus;
import java.io.File;
import java.io.Serializable;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.StringJoiner;
import java.util.logging.Level;

import il.co.vor.common.Constants;

public class DataObj implements  IFileDataObj,Serializable {
	private int log_data_id = -1;
	private int operand_id = -1;
	private ZonedDateTime log_data_time = null;
	private String str_data_time = "";
	private String log_data_val = "";
	private boolean manual_mode = false;
	
	public DataObj(int logDataID, int operandID, ZonedDateTime logDataTime, String strDataTime, String logDataVal, boolean manualMode) {
		log_data_id = logDataID;
		operand_id = operandID;
		log_data_time = logDataTime;
		log_data_val = logDataVal;
		manual_mode = manualMode;
		str_data_time = strDataTime;
	}
	
	public int GetLogDataID() {
		return log_data_id;
	}
	public void SetLogDataID(int log_data_id) {
		this.log_data_id = log_data_id;
	}
	public int GetOperandID() {
		return operand_id;
	}
	public void SetOperandID(int operand_id) {
		this.operand_id = operand_id;
	}
	public ZonedDateTime GetLogDataTime() {
		return log_data_time;
	}
	public void SetLogDataTime(ZonedDateTime log_data_time) {
		this.log_data_time = log_data_time;
	}
	public String GetLogDataVal() {
		return log_data_val;
	}
	public void SetLogDataVal(String log_data_val) {
		this.log_data_val = log_data_val;
	}
	public boolean isManualMode() {
		return manual_mode;
	}
	public void SetManualMode(boolean manual_mode) {
		this.manual_mode = manual_mode;
	}
	
	public String toString(){
		
		//String curr_val = "";
		String ret = "";
		
		StringBuilder sb = new StringBuilder();
		//curr_val = DateTimeFormatter.ofPattern(Constants.CSV_DATE_FORMAT).format(log_data_time.withZoneSameInstant(ZoneOffset.UTC));
		
		sb.append(String.valueOf(operand_id)).append(Constants.CSV_COMMA_DELIMITER).append(str_data_time).append(Constants.CSV_COMMA_DELIMITER).append(log_data_val).append(Constants.CSV_COMMA_DELIMITER).append(String.valueOf(NPLHelper.GetBooleanAsInt(manual_mode)));
		
		ret = sb.toString();
		
        return  ret;
    }
	
	public StringBuilder toString(StringBuilder _sb){
		
		//String curr_val = "";
		
		
		//String ret = "";
		
		//StringBuilder sb = new StringBuilder();
		//curr_val = DateTimeFormatter.ofPattern(Constants.CSV_DATE_FORMAT).format(log_data_time.withZoneSameInstant(ZoneOffset.UTC));
		
		if (log_data_val != null)
		{
			_sb.append(String.valueOf(operand_id)).append(Constants.CSV_COMMA_DELIMITER).append(str_data_time).append(Constants.CSV_COMMA_DELIMITER).append(log_data_val).append(Constants.CSV_COMMA_DELIMITER).append(String.valueOf(NPLHelper.GetBooleanAsInt(manual_mode)));
		}
		//ret = _sb.toString();
		
        return  _sb;
    }

	@Override
	public ZonedDateTime GetLogTime() {
		
		return GetLogDataTime();
	}
	
	
}
