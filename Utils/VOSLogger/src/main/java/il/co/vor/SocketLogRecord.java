package il.co.vor;

import java.io.Serializable;
import java.util.logging.Level;

public class SocketLogRecord implements Serializable 
{
	private static final long serialVersionUID = 1L;

	private String record;
	private Level level;
	private Object console;

	
	public SocketLogRecord(String _record, Level _level)
	{
		super();
		record = _record;
		level = _level;
		console = null;
	}

	public String getRecord() 
	{
		return record;
	}
	
	public Level getLevel() 
	{
		return level;
	}

	public Object getConsole() 
	{
		return console;
	}

	public void setConsole(Object console) 
	{
		this.console = console;
	}

	
}
