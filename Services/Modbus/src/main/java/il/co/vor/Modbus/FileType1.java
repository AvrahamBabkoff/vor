package il.co.vor.Modbus;

public enum FileType1 {
	UNKNOWN(0),LOG_DATA(1),METER(2),CDR(3);

	private final int value;
	private FileType1(int value) {
	    this.value = value;
	}

	public int GetValue() {
	    return value;
	}
	
	public static FileType1 fromInt(int i) {
        for (FileType1 name : FileType1.values()) {
            if (name.GetValue() == i) { return name; }
        }
        return null;
    }
	
	
}
