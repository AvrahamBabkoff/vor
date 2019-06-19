package il.co.vor.Modbus;

public enum PropertyType {
	UNKNOWN(0),SECONDS(1),MINUTES(2),INTEGER(3);

	private final int value;
	private PropertyType(int value) {
	    this.value = value;
	}

	public int GetValue() {
	    return value;
	}
	
	public static PropertyType fromInt(int i) {
        for (PropertyType name : PropertyType .values()) {
            if (name.GetValue() == i) { return name; }
        }
        return null;
    }
}
