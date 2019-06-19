package il.co.vor;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
//import java.util.logging.Level;
import java.util.logging.LogRecord;

import il.co.vor.common.Constants;
import il.co.vor.utilities.PropertyFileReader;


public class MyFileHandler extends FileHandler implements Runnable 
{
	private static final int m_iQueSize = 100000;
	private static final int m_iPollDelayInSeconds = 90;
	private static final String DIRECTORY_FORMAT = "yyyyMMddHH";
	private static final String FILE_FORMAT = "mm";
	private final BlockingQueue<VOSLogRecord> m_queue;
	private BufferedOutputStream m_BufferedOutputStream;
	private String m_RootFolderName;
	private String m_CurrentDirectoryName;
	private String m_CurrentFileName;
	private SimpleDateFormat m_SDfh;
	private SimpleDateFormat m_SDfm;
	private VOSFormatter m_Formatter;
	LogCleanup m_lc;
	VOSLogToSocket m_sl;
	private Thread m_LogCleanupThread;
	private Thread m_SocketListnerThread;
	
	public MyFileHandler () throws IOException, SecurityException 
	{
		super ();		
						
		String sName;
		boolean bLogToConsole = false;
		// get root folder from which to create the log directory hierarchy
		m_RootFolderName = System.getProperty ("user.home") + Constants.FILE_SEPARATOR + Constants.LOG_ROOT_FOLDER;
		
		// Service can optionally set its name. All hourly directories will
		// be created below the provided name. If no name is given, "Default" 
		// is used
		//sName = System.getProperty ("VOR.ServiceName");
		sName = PropertyFileReader.getProperty(Constants.PROP_NAME_SERVICE_NAME);
		if (null == sName)
		{
			sName = "Default";
		}
		// check setting for logging to console
		bLogToConsole = PropertyFileReader.getPropertyAsBoolean(Constants.PROP_NAME_LOG_CONSOLE, false);
		m_sl = new VOSLogToSocket ();
		m_Formatter = new VOSFormatter(sName, bLogToConsole, m_sl);
 		
		super.setFormatter (m_Formatter);
		
		m_RootFolderName += Constants.FILE_SEPARATOR + sName;
		
		// make sure the root folder is created
		new File (m_RootFolderName).mkdirs ();
		
		// initialize format objects
		m_SDfh = new SimpleDateFormat (DIRECTORY_FORMAT);
		m_SDfm = new SimpleDateFormat (FILE_FORMAT);
		m_CurrentDirectoryName = "";
		m_CurrentFileName = "";
		m_BufferedOutputStream = null;
		// initialize the Log queue
		m_queue = new ArrayBlockingQueue<VOSLogRecord>(m_iQueSize);
		
		//
		m_lc = new LogCleanup(m_RootFolderName, new SimpleDateFormat (DIRECTORY_FORMAT));
		m_LogCleanupThread = new Thread(m_lc);
		m_LogCleanupThread.start();

		m_SocketListnerThread = new Thread(m_sl);
		m_SocketListnerThread.start();
	}
	
	@Override
	public synchronized void close()
	{
/*		try 
		{
			Thread.sleep(1000);
			m_LogCleanupThread.interrupt();
		}
		catch(Exception e)
		{
			
		}
*/		super.close();
	}

	// post the record the the queue. Actual formating and writing to the log file is done in a dedicated thread.
	// note that placing in the queue is done without blocking while waiting for available space. If the Q is full 
	// the log is discarded
	@Override
	public void	publish (LogRecord record)
	{
		VOSLogRecord vorLR = new VOSLogRecord (record);		
		m_queue.offer (vorLR);		
	}
	
	// check if a new file needs to be created
	private void SetCurrentFile (boolean bCreateNew)
	{	
		
		boolean bChangeFile = false;
		Date d = new Date (System.currentTimeMillis());
		String newDirectory = m_RootFolderName + Constants.FILE_SEPARATOR +  m_SDfh.format(d);
		String newFile = m_SDfm.format(d);


		if (!newDirectory.equals(m_CurrentDirectoryName) && (true == bCreateNew))
		{
			// need to create a new directory and a new file
			m_CurrentDirectoryName = newDirectory;
			new File(m_CurrentDirectoryName).mkdirs();
			bChangeFile = true;
		}
		if (!newFile.equals(m_CurrentFileName))
		{
			// need to create a new file
			bChangeFile = true;			
		}

		if (bChangeFile)
		{
				try 
				{
					if (true == bCreateNew)
					{
						m_CurrentFileName = newFile;
						m_BufferedOutputStream = new BufferedOutputStream 
														(new FileOutputStream (m_CurrentDirectoryName + Constants.FILE_SEPARATOR + "log."+ m_CurrentFileName, true),2048);
						this.setOutputStream(m_BufferedOutputStream);
					}
					else
					{
						if (null != m_BufferedOutputStream)
						{
							m_BufferedOutputStream = null;
							this.close();
						}
					}
					
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}												
		}
	}

  	public void run() 
	{
  		VOSLogRecord vlr = null;
  		try 
  		{
  			while (true) 
  			{
  				//_publish(m_queue.take());
  				vlr = m_queue.poll(m_iPollDelayInSeconds, TimeUnit.SECONDS);
  				if (Thread.interrupted())
  				{
  					System.out.println("in MyFileHandler.run, thread was interupted. breaking!!!");
  					break;
  				}
  				else
  				{
  					_publish(vlr);
  				}
  				vlr = null;
  			}
  		} 
  		catch (InterruptedException ex) 
  		{
  		}
  		this.close();
		try 
		{
			Thread.sleep(1000);
			m_LogCleanupThread.interrupt();
			m_sl.Terminate();
			m_SocketListnerThread.interrupt();
		}
		catch(Exception e)
		{
			
		}
  		
	}
	
	
	// method _published is called from the dedicated thread
	public void	_publish(VOSLogRecord record)
	{
		// check if a new file needs to be created
		SetCurrentFile (null != record);
		if (null != record)
		{
			m_Formatter.setVOSLogRecord(record);
			super.publish(record.getLogRecord());
		}
	}
	
	public void setLogToConsole(boolean bLogToConsole) 
	{
		m_Formatter.setLogToConsole(bLogToConsole);
	}


	public int getLogToSocketPort() 
	{
		return m_sl.getPort();
	}
}
