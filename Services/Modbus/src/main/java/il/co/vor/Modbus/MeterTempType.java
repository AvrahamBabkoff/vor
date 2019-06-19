package il.co.vor.Modbus;

public enum MeterTempType {
	UNKNOWN(999),COLD(0),HOT(1),SELECT_BY_PLC(2);
	
	private final int value;
	private MeterTempType(int value) {
	    this.value = value;
	}

	public int GetValue() {
	    return value;
	}
	
	public static MeterTempType fromInt(int i) {
        for (MeterTempType name : MeterTempType .values()) {
            if (name.GetValue() == i) { return name; }
        }
        return null;
    }
}
