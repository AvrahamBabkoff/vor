package il.co.vor.Modbus;

public enum OperandFlag {
	UNKNOWN(0),
	SEQ_REF_TYPE(1),
	SEQ_ACTIVATION_TYPE(2),
	TAOZ_TYPE(4),
	MODES_TYPE(8),
	BAR_GAUGE_TYPE(16),
	ALARM_TYPE(32),
	REAL_TIME_GRAPH_TYPE(64),
	MANUAL_TABLE_TYPE(128),
	SITE_VIEW_TYPE(256),
	LOG_BY_PERCENTAGE(512),
	LOG_BY_INTERVAL(1024),
	OPERAND_OPTIMIZATION(2048);

	private final int value;
	private OperandFlag(int value) {
	    this.value = value;
	}

	public int GetValue() {
	    return value;
	}
	
	public static OperandFlag fromInt(int i) {
        for (OperandFlag name : OperandFlag .values()) {
            if (name.GetValue() == i) { return name; }
        }
        return null;
    }
	
	
}
