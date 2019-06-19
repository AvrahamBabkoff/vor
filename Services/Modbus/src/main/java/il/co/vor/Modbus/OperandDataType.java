package il.co.vor.Modbus;

public enum OperandDataType {
	UNKNOWN(0),DECIMAL(1),INTEGER(2),HEX(3),BINARY(4),BIT_FLOAT_32(5),BIT_FLOAT_64(6),ASCII(7),SHORT(8),UNSIGNED_SHORT(9);

	private final int value;
	private OperandDataType(int value) {
	    this.value = value;
	}

	public int GetValue() {
	    return value;
	}
	
	public static OperandDataType fromInt(int i) {
        for (OperandDataType name : OperandDataType .values()) {
            if (name.GetValue() == i) { return name; }
        }
        return null;
    }
}
