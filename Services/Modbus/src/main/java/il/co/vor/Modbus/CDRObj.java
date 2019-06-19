package il.co.vor.Modbus;
import java.io.Serializable;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.StringJoiner;

import il.co.vor.common.Constants;

public class CDRObj implements IFileDataObj,Serializable {
	
	private int CDR_id = -1;
	private int operand_id = -1;
	private ZonedDateTime CDR_time = null;
	private String str_data_time = "";
	private String CDR_prev_val = "";
	private String CDR_new_val = "";
	private int CDR_source = -1;
	private String CDR_comment = "";
	
	public CDRObj(int CDRID, int operandID, ZonedDateTime CDRTime, String strDataTime, String CDRPrevVal, String CDRNewVal, int CDRSource, String CDRComment) {
		CDR_id = CDRID;
		operand_id = operandID;
		CDR_time = CDRTime;
		CDR_prev_val = CDRPrevVal;
		CDR_new_val = CDRNewVal;
		CDR_source = CDRSource;
		CDR_comment = CDRComment;
		str_data_time = strDataTime;
	}
	
	public int GetCDRID() {
		return CDR_id;
	}
	public void SetCDRID(int cDR_id) {
		CDR_id = cDR_id;
	}
	public int GetOperandID() {
		return operand_id;
	}
	public void SetOperandID(int operand_id) {
		this.operand_id = operand_id;
	}
	public ZonedDateTime GetCDRTime() {
		return CDR_time;
	}
	public void SetCDRTime(ZonedDateTime cDR_time) {
		CDR_time = cDR_time;
	}
	public String GetCDRPrevVal() {
		return CDR_prev_val;
	}
	public void SetCDRPrevVal(String cDR_prev_val) {
		CDR_prev_val = cDR_prev_val;
	}
	public String GetCDRNewVal() {
		return CDR_new_val;
	}
	public void SetCDRNewVal(String cDR_new_val) {
		CDR_new_val = cDR_new_val;
	}
	public int GetCDRSource() {
		return CDR_source;
	}
	public void SetCDRSource(int cDR_source) {
		CDR_source = cDR_source;
	}
	public String GetCDRComment() {
		return CDR_comment;
	}
	public void SetCDRComment(String cDR_comment) {
		CDR_comment = cDR_comment;
	}
	
	public String toString(){
		
		//String curr_val = "";
		String ret = "";
		
		StringBuilder sb = new StringBuilder();
		//curr_val = DateTimeFormatter.ofPattern(Constants.CSV_DATE_FORMAT).format(CDR_time.withZoneSameInstant(ZoneOffset.UTC));
		
		sb.append(String.valueOf(operand_id)).append(Constants.CSV_COMMA_DELIMITER).append(str_data_time).append(Constants.CSV_COMMA_DELIMITER).append(CDR_prev_val).append(Constants.CSV_COMMA_DELIMITER).append(CDR_new_val).append(Constants.CSV_COMMA_DELIMITER).append(String.valueOf(CDR_source)).append(Constants.CSV_COMMA_DELIMITER).append(NPLHelper.GetCSVNormalizedString(CDR_comment));
		
		ret = sb.toString();
        return  ret;
    }

	public StringBuilder toString(StringBuilder _sb){
		
		//String curr_val = "";
		//String ret = "";
		
		//StringBuilder sb = new StringBuilder();
		//curr_val = DateTimeFormatter.ofPattern(Constants.CSV_DATE_FORMAT).format(CDR_time.withZoneSameInstant(ZoneOffset.UTC));
		
		if (CDR_new_val != null)
		{
			_sb.append(String.valueOf(operand_id)).append(Constants.CSV_COMMA_DELIMITER).append(str_data_time).append(Constants.CSV_COMMA_DELIMITER).append(CDR_prev_val).append(Constants.CSV_COMMA_DELIMITER).append(CDR_new_val).append(Constants.CSV_COMMA_DELIMITER).append(String.valueOf(CDR_source)).append(Constants.CSV_COMMA_DELIMITER).append(NPLHelper.GetCSVNormalizedString(CDR_comment));
		}
		//ret = _sb.toString();
		
        return  _sb;
    }

	@Override
	public ZonedDateTime GetLogTime() {
		return GetCDRTime();
	}
	
}
