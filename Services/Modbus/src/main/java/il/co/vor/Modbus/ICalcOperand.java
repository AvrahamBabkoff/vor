package il.co.vor.Modbus;

import java.time.ZonedDateTime;

public interface ICalcOperand {
	public String Calculate(long ulReadCount, ZonedDateTime dtReadTime);
	public String Update(double dValue, ZonedDateTime dt, double dPrevValue);
	public int GetOperandID();
}
