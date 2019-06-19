package il.co.vor.common;

import java.nio.charset.StandardCharsets;


public class Enums 
{
	
	public enum ServiceType 
	{
	    UNKNOWN,
	    NPL,
	    LOGIC,
	    DAL_CONFIG,
	    FTPTM,
	    DAL_DATA,
	    DAL_REPORT
	}
	
	public enum FileType 
	{
	    UNKNOWN,
	    OPERANDS_DATA,
	    METERS_DATA,
	    CDR_DATA
	}
	
	public enum FilePhase 
	{
		DIR,
		DWL,
		UPLOAD,
		BULK,
		ARCHIVE,
		CLEAN
	}
	
	public enum FilePhaseStatus 
	{
	    NOTSTARTED,
	    STARTED,
	    FAILED,
	    DONE
	}

	public enum FtpFileAction
	{
		UNKNOWN,
		INSERT,
		UPDATE_DOWNLOAD_STATUS,
		UPDATE_UPLOAD_STATUS
	}
	
	public enum RegisterType {
		UNKNOWN,
		MODBUS_DISCRETE_INPUT,
		MODBUS_COIL,
		MODBUS_INPUT_REGISTER,
		MODBUS_HOLDING_REGISTER,
		S7_DATABLOCK
	}
	
	public enum OperandDataType {
		UNKNOWN,
		DECIMAL,
		INTEGER,
		HEX,
		BINARY,
		BIT_FLOAT_32,
		BIT_FLOAT_64,
		ASCII,
		SHORT,
		UNSIGNED_SHORT
	}
	
	public enum PlcProtocolType {
		UNKNOWN,
		S7,
		MODBUS
	}
	
	public enum MeterType {
        None,
        EnergyForBilling,
        EnergyForMonitor,
        ElectricityForCredit,
        ElectricityForMonitor,
        ElectricityForBilling
	}

    public enum MeterTempType
    {
    	None(Constants.METERS_TEMP_TYPE_NONE, ""), 
    	Cold(Constants.METERS_TEMP_TYPE_COLD, "\u05E7\u05D9\u05E8\u05D5\u05E8"), 
    	Hot(Constants.METERS_TEMP_TYPE_HOT, "\u05D7\u05D9\u05DE\u05D5\u05DD"), 
    	SelectByPlc(Constants.METERS_TEMP_TYPE_PLC, "");
    	 
        private int code;
        private String  desc;
     
        private MeterTempType(int code, String desc) {
            this.code = code;
            //this.desc = new String(desc.getBytes(),StandardCharsets.UTF_8);
            this.desc = desc;
        }
     
        public int getCode() {
            return code;
        }
        public String getDesc() {
            return desc;
        }
        
        public static MeterTempType getMeterTempTypeFromCode(int code) {
        	MeterTempType result = None;
        	switch(code) {
	        	case Constants.METERS_TEMP_TYPE_COLD:
	        		result = Cold;
	        		break;
	        	case Constants.METERS_TEMP_TYPE_HOT:
	        		result = Hot;
	        		break;
	        	case Constants.METERS_TEMP_TYPE_PLC:
	        		result = SelectByPlc;
	        		break;
	        	default:
	        		result = None;
        	}
			return result;
        	
        }
    }
}
