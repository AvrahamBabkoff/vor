package il.co.vor;

import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Logger;

import il.co.vor.common.Constants;
import il.co.vor.utilities.PropertyFileReader;

import java.util.logging.Level;

/**
 * The VORLogger JAR wraps the Java Logger for two purposes:
 * 1. Implement a custom file persistence scheme (other than a single file, or a rotating scheme).
 * In particular, the scheme implemented is one minute files, placed in hourly directories.
 * The file name format is <mm>.log (mm=00..59), and the directory name format is YYYYMMDD24HH.
 * 
 * 2. Logging should be done asynchronously. For this, a dedicated thread is created. When a message is logged,
 * the LogRecord is placed in a BlockingQueue instead of writing the LogRecord to file. The dedicated thread 
 * performs a blocking wait on the queue. Upon retrieving a LogRecord object, it writes the LogRecord to the 
 * proper file 
 *   
 * 
 */

public class VOSLogger 
{
	private static MyFileHandler m_MyFileHandler;
	private static MyFileHandler new_MyFileHandler;
	private static Thread m_thread;
	private static Thread new_thread;
	private static Level m_level;
	private static final Logger m_instance = createInstance();
	private static boolean m_bTerminated = false;
	 
	protected VOSLogger() 
	{
	}
 
	public static Logger getInstance() {
		return m_instance;
	}

	private static Logger createInstance() 
	{
		Logger LOGGER = null;
		try 
		{
			// set default format
			System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tc %4$s: %5$s%n");
			
			// get top level logger
			LOGGER = Logger.getLogger("");
			// remove all handlers
			Handler[] handlers = LOGGER.getHandlers();
			for(Handler handler : handlers) 
			{
				handler.close();
				LOGGER.removeHandler(handler);
			}
			
			// set level- to do: save in file for future
			m_level = getInitialLevel();
			LOGGER.setLevel(m_level);
		//System.out.println("getting log level from properties file :"+PropertyFileReader.getLevel());
			// create our file handler
			m_MyFileHandler  = new MyFileHandler ();
			m_MyFileHandler.setLevel(m_level);			
			LOGGER.addHandler(m_MyFileHandler);

			// create the logging thread
			m_thread = new Thread(m_MyFileHandler);
			m_thread.start();
			
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}		
		return LOGGER;
	}
	
	private static Level getLevel(String sLogLevel, Level defLevel)
	{
		//System.out.println("System log level"+System.getProperty("ACC.runAtLevel"));
		Level levelRes = defLevel;//Level.ALL;
		//String sLogLevel = PropertyFileReader.getProperty(Constants.PROP_NAME_LOG_LEVEL);
		
		if (null != sLogLevel)
		{
			if (sLogLevel.equals(Level.INFO.getName()))
			{
				levelRes =  Level.INFO;
			}
			else if (sLogLevel.equals(Level.WARNING.getName()))
			{
				levelRes =  Level.WARNING;
			}
			else if (sLogLevel.equals(Level.ALL.getName()))
			{
				levelRes =  Level.ALL;
			}
			else if (sLogLevel.equals(Level.FINE.getName()))
			{				
				levelRes =  Level.FINE;
			}
			else if (sLogLevel.equals(Level.SEVERE.getName()))
			{				
				levelRes =  Level.SEVERE;
			}
			else if (sLogLevel.equals(Level.OFF.getName()))
			{				
				levelRes = Level.OFF;
			}
		}
			//System.out.println("returning Default");
		return levelRes;
		
	}
	
	private static Level getInitialLevel()
	{
		return getLevel(PropertyFileReader.getProperty(Constants.PROP_NAME_LOG_LEVEL), Level.ALL);
		/*
		//System.out.println("System log level"+System.getProperty("ACC.runAtLevel"));
		Level levelRes = Level.ALL;
		String sLogLevel = PropertyFileReader.getProperty(Constants.PROP_NAME_LOG_LEVEL);
		
		if (null != sLogLevel)
		{
			if (sLogLevel.equals(Level.INFO.getName()))
			{
				levelRes =  Level.INFO;
			}
			else if (sLogLevel.equals(Level.WARNING.getName()))
			{
				return Level.WARNING;
			}
			else if (sLogLevel.equals(Level.ALL.getName()))
			{
				return Level.ALL;
			}
			else if (sLogLevel.equals(Level.FINE.getName()))
			{				
				return Level.FINE;
			}
		}
			//System.out.println("returning Default");
		return levelRes;
		*/
	}

	public static synchronized void setLogLevel(String sNewLevel)
	{
		Level newLevel = getLevel(sNewLevel, m_level);
		if (newLevel != m_level)
		{
			m_level = newLevel;
			m_instance.setLevel(m_level);
			m_MyFileHandler.setLevel(m_level);
		}
	}
	
	public static synchronized void setLogToConsole(boolean bLogToConsole)
	{
		m_MyFileHandler.setLogToConsole(bLogToConsole);
	}

	public static synchronized String getLogLevel()
	{
		String sRes = m_level.getName();
		
		return sRes;
	}

	public static  int getLogToSocketPort()
	{
		int iPort = m_MyFileHandler.getLogToSocketPort();
		
		return iPort;
	}

	public static synchronized void Terminate () 
	{
		try 
		{
			if (false == m_bTerminated)
			{
			// allow for final logs to be write
				m_bTerminated = true;
				Thread.sleep(1000);
				System.out.println("In VOSLogger.Terminate");
				m_instance.removeHandler(m_MyFileHandler);
				m_thread.interrupt();
			}
			//m_MyFileHandler.close();
		} 
		catch (InterruptedException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public static void changeLogger (Level Level ) 
	{
		
			
			try {
				new_MyFileHandler  = new MyFileHandler ();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			new_MyFileHandler.setLevel(Level);			
			m_instance.addHandler(new_MyFileHandler);

			// create the logging thread
			new_thread = new Thread(new_MyFileHandler);
			new_thread.start();
			
			try {
				Thread.sleep(1000);
				m_instance.removeHandler(m_MyFileHandler);
				m_thread.interrupt();
				m_MyFileHandler.close();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	}
}
