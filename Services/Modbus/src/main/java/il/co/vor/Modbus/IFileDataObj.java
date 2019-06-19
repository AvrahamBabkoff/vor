package il.co.vor.Modbus;

import java.time.ZonedDateTime;

public interface IFileDataObj {
	
	public ZonedDateTime GetLogTime();

	public StringBuilder toString(StringBuilder sb);

}
