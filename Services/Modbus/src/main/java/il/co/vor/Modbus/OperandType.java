package il.co.vor.Modbus;

public enum OperandType {
	UNKNOWN(0),REGISTER(1),CALC_OPERAND(2);
	
	private final int value;
	private OperandType(int value) {
	    this.value = value;
	}

	public int GetValue() {
	    return value;
	}
	
	public static OperandType fromInt(int i) {
        for (OperandType name : OperandType .values()) {
            if (name.GetValue() == i) { return name; }
        }
        return null;
    }
}
