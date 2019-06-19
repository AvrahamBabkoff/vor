package il.co.vor;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import il.co.vor.common.Constants;
import il.co.vor.utilities.PropertyFileReader;

public class LogCleanup implements Runnable 
{
	private SimpleDateFormat m_SDfh;
	private int m_iLogsDaysToKeep;
	public static final int    LOGS_DEFAULT_DAYS_TO_KEEP = 5;
	public static final int    LOGS_HOURS_NOT_TO_COMPRESS = 2;
	public static final int    LOGS_ARCHIVE_SLEEP_DURATION_IN_SECONDS = 3600;
	public static final String    LOGS_ARCHIVE_DEFAULT_COMMAND = "python %s %%s %%s %%s";
	public static final String    LOGS_ARCHIVE_DEFAULT_SCRIPT = "archive_logs.py";
	private Logger _logger = Logger.getLogger(LogCleanup.class.getName());
	
	private long m_lMiliToSleep;// = 10*1000;
	private long m_lLogsMilisToCompressUpTo;
	private long m_lLogMilisToKeep;
	private String m_sRootFolderName;
	private String m_sLogArchiveCommandTemplate;
	private String m_sDefaultCommand;

	public LogCleanup(String sRootFolderName, SimpleDateFormat sdf) 
	{
		super();
		String sDefaultCommand;
		String sDefaultScript;
		File f;
		m_sRootFolderName = sRootFolderName;
		//m_SDfh = new SimpleDateFormat ("yyyyMMddHH");
		m_SDfh = sdf;
		m_iLogsDaysToKeep = PropertyFileReader.getPropertyAsInt(Constants.PROP_NAME_LOG_DAYS_TO_KEEP, LOGS_DEFAULT_DAYS_TO_KEEP);
		if (m_iLogsDaysToKeep < 1)
		{
			m_iLogsDaysToKeep = 1;
		}
		m_lLogMilisToKeep = TimeUnit.DAYS.toMillis(m_iLogsDaysToKeep);
		m_lLogsMilisToCompressUpTo = TimeUnit.HOURS.toMillis(LOGS_HOURS_NOT_TO_COMPRESS);
		m_lMiliToSleep = TimeUnit.SECONDS.toMillis(LOGS_ARCHIVE_SLEEP_DURATION_IN_SECONDS);
		// get command for archiving
		// default script: C:\Users\avraham.IMSI\vos\bin\archive_logs.py	

		// check first if archive_logs.py exists in current dir:
		sDefaultScript = String.format("%s%s%s", 
				System.getProperty("user.dir"),
				Constants.FILE_SEPARATOR,
				LOGS_ARCHIVE_DEFAULT_SCRIPT);
		
		f = new File(sDefaultScript);
		if (!(f.exists() && !f.isDirectory())) 
		{ 
			sDefaultScript = String.format("%s%s%s%s%s", 
					System.getProperty ("user.home"),
					Constants.FILE_SEPARATOR,
					Constants.BIN_ROOT_FOLDER,
					Constants.FILE_SEPARATOR,
					LOGS_ARCHIVE_DEFAULT_SCRIPT);
		}		
		
		
		// default command: "python C:\Users\avraham.IMSI\vos\bin\archive_logs.py %s %s %s"
		sDefaultCommand = String.format(LOGS_ARCHIVE_DEFAULT_COMMAND, sDefaultScript);
		m_sDefaultCommand = sDefaultCommand;
		//_logger.log(Level.SEVERE, String.format("default command is %s", sDefaultCommand));
		m_sLogArchiveCommandTemplate = PropertyFileReader.getProperty(Constants.PROP_NAME_LOG_ARCHIVE_COMMAND, sDefaultCommand);		
	}

	@Override
	public void run() 
	{		
		Date d;
		String sDirToDelete;
		String sDirToCompress;
		//Process process;
		long lCurrentMillis;
		String sLogArchiveCommand = "";
		while (true)
		{
			try 
			{
				_logger.log(Level.SEVERE, "begin loop. Default command is " + m_sDefaultCommand);
				lCurrentMillis = System.currentTimeMillis();
				d = new Date (lCurrentMillis - m_lLogMilisToKeep);
				sDirToDelete = m_SDfh.format(d);
				d = new Date (lCurrentMillis - m_lLogsMilisToCompressUpTo);
				sDirToCompress = m_SDfh.format(d);

				// format command string:
				sLogArchiveCommand = String.format(m_sLogArchiveCommandTemplate, m_sRootFolderName, sDirToDelete, sDirToCompress);
				//process = Runtime.getRuntime().exec("python C:\\Users\\avraham.IMSI\\Documents\\log_compress_01.py C:\\Users\\avraham.IMSI\\logs\\DAL_CONFIG 2017051016");
				// command: "python C:\Users\avraham.IMSI\vos\bin\archive_logs.py C:\Users\avraham.IMSI\vos\logs\DAL_CONFIG 2017072717 2017080115"
				/*process =*/ Runtime.getRuntime().exec(sLogArchiveCommand);
				_logger.log(Level.INFO, String.format("successfully execute command: %s", sLogArchiveCommand));
			} 
			catch (IOException e) 
			{
				String str = e.getMessage();
				_logger.log(Level.SEVERE, String.format("failed to execute command: %s, exception: %s ", sLogArchiveCommand, str));
			}
			try
			{
				_logger.log(Level.SEVERE, String.format("going to sleep for %d miliseconds", m_lMiliToSleep));
				Thread.sleep(m_lMiliToSleep);
				_logger.log(Level.SEVERE, String.format("woke after %d miliseconds", m_lMiliToSleep));
			}
			catch (InterruptedException e) 
			{
				System.out.println("il.co.vor.LogCleanup.run: Exitting!!!");
				break;
			} 
		}
	}

}
