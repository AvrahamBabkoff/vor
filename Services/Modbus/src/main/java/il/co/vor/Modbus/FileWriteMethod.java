package il.co.vor.Modbus;

public enum FileWriteMethod {
	UNKNOWN(999),INTERVAL_CLOSE(0),INTERVAL_FLUSH(1),FINAL_CLOSE(2);
	
	private final int value;
	private FileWriteMethod(int value) {
	    this.value = value;
	}

	public int GetValue() {
	    return value;
	}
	
	public static FileWriteMethod fromInt(int i) {
        for (FileWriteMethod name : FileWriteMethod.values()) {
            if (name.GetValue() == i) { return name; }
        }
        return null;
    }
}
