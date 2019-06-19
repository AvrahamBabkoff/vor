package il.co.vor;
import java.util.logging.LogRecord;

/**
 * The VORLogRecord class wraps a LogRecord object
 * 
 * Since actual formatting and logging take place on the logger's worker thread, 3 properties need special handling:
 * 1. Thread ID of caller
 * 2. Class name of caller
 * 3. Method name of caller
 *  
 *  Thread ID is obvious.
 *  Regarding class name and method name: according to the documentation, getSourceMethodName and to getSourceClassName  
 *  need to be called explicitly on the callers thread prior to posting the LogRecord object to another thread:
 *  
 *  From the documentation of Class LogRecord:
 *  "Note that if the client application has not specified an explicit source method name and source class name, then the LogRecord 
 *  class will infer them automatically when they are first accessed (due to a call on getSourceMethodName or getSourceClassName) by 
 *  analyzing the call stack. Therefore, if a logging Handler wants to pass off a LogRecord to another thread, or to transmit it over RMI, 
 *  and if it wishes to subsequently obtain method name or class name information it should call one of getSourceClassName or 
 *  getSourceMethodName to force the values to be filled in."
 *  
 *  VORLogRecord's constructor takes care of this 
 */

public class VOSLogRecord 
{
	private LogRecord m_LogRecord;
	private long m_lThreadId;
	private String m_sClassName;
	private String m_sMethodName;
	

	VOSLogRecord (LogRecord record)
	{
		m_lThreadId = Thread.currentThread().getId();
		m_LogRecord = record;
		m_sClassName = record.getSourceClassName();
		m_sMethodName = record.getSourceMethodName();
		
		if (m_sClassName == null)
		{
			m_sClassName = "Unknown";
		}
		
		if (m_sMethodName == null)
		{
			m_sMethodName = "Unknown";
		}
	}
	
	public LogRecord getLogRecord ()
	{
		return m_LogRecord;
	}
	
	public long getLoggingThreadId ()
	{
		return m_lThreadId;
	}

	public String getSourceClassName ()
	{
		return m_sClassName;
	}

	public String getSourceMethodName ()
	{
		return m_sMethodName;
	}
}
