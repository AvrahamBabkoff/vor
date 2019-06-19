package il.co.vor;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import il.co.vor.common.Constants;

public class VOSFormatter extends Formatter 
{
	private String m_sHostName;
//  private static final DateFormat m_df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
    private static final DateFormat m_df = new SimpleDateFormat("MMM dd HH:mm:ss.SSS", Locale.US);
    private String m_sServiceMame;
    private VOSLogRecord m_VOSLogRecord;
    private boolean m_bLogToConsole;
    StringBuilder m_builder;// = new StringBuilder (2048);
    private VOSLogToSocket m_logToSocket;

    
	public boolean isLogToConsole() 
	{
		return m_bLogToConsole;
	}

	public void setLogToConsole(boolean bLogToConsole) 
	{
		this.m_bLogToConsole = bLogToConsole;
	}

	public VOSFormatter(String serviceName, boolean bLogToConsole, VOSLogToSocket logToSocket) 
	{
		m_builder = new StringBuilder (2048);
		m_bLogToConsole = bLogToConsole;
		m_sServiceMame = serviceName;
		m_logToSocket = logToSocket;
		// set host name
		try 
		{
			m_sHostName = InetAddress.getLocalHost().getHostName();
		} 
		catch (UnknownHostException e) 
		{
			m_sHostName = "localhost";
			e.printStackTrace();
		}
	}
	
	public void setVOSLogRecord (VOSLogRecord record)
	{
		m_VOSLogRecord = record;
	}

	@Override
	public String format (LogRecord record) 
	{
		Object [] objParameters = null;
		String strRecord;
        StringBuilder builder = m_builder;//new StringBuilder (2048);
        builder.setLength(0);
        builder.append(m_df.format(new Date(record.getMillis()))).append(" ");
        builder.append(m_sHostName).append(" ");
        builder.append(m_sServiceMame).append(": ");
        builder.append(record.getLevel().getName().substring(1, 2)).append(" ");
        builder.append(m_VOSLogRecord.getSourceClassName()).append(".");
        builder.append(m_VOSLogRecord.getSourceMethodName()).append("\t");
        builder.append("[").append(m_VOSLogRecord.getLoggingThreadId()+"").append("] ");
        builder.append("[").append(record.getSequenceNumber ()+"").append("] ");
        objParameters = record.getParameters();
        if (null != objParameters)
        {
        	builder.append(MessageFormat.format(record.getMessage(), record.getParameters()));
        }
        else
        {
        	builder.append(record.getMessage());
        }
        builder.append(Constants.LINE_SEPARATOR);
        strRecord = builder.toString();
        if(true == m_bLogToConsole)
        {
        	System.out.print(strRecord);
        }
        if (null != m_logToSocket)
        {
        	m_logToSocket.logToSocket(strRecord, record.getLevel());
        }
        return strRecord;
	}

}
