package il.co.vor.common;

import java.io.File;

public class Constants 
{
	public static final String FILE_SEPARATOR = File.separator;
	public static final String LINE_SEPARATOR = System.lineSeparator();
	public static final String NET_VOS_DAL_CONFIG_ROOT_URI = "vos_config";
	public static final String NET_VOS_DAL_CONFIG_API_ROOT_PACKAGE_NAME = "DalConfigService";
	public static final String VOS_ROOT_FOLDER = "vos";
	public static final String PROPERTIES_ROOT_FOLDER = VOS_ROOT_FOLDER + FILE_SEPARATOR + "properties";
	public static final String LOG_ROOT_FOLDER = VOS_ROOT_FOLDER + FILE_SEPARATOR + "logs";
	public static final String BIN_ROOT_FOLDER = VOS_ROOT_FOLDER + FILE_SEPARATOR + "bin";
	public static final String ALGO_CONFIG_ROOT_FOLDER = VOS_ROOT_FOLDER + FILE_SEPARATOR + "config";
	public static final String PROPERTIES_FILE_NAME = "config.properties";
	public static final String SYSTEM_PROPERTIES_SERVICE_NAME_PROP_NAME = "il.co.vor.ServiceName";
	public static final String SYSTEM_PROPERTIES_CURRENT_SHUTDOWN_URL_FILE_NAME_PROP_NAME = "il.co.vor.ShutdownUrlFileName";
	public static final String NET_VOS_LOG_ROOT_URI = "logs";
	public static final String NET_VOS_LOG_LEVEL = "level";
	public static final String NET_VOS_LOG_LEVEL_ROOT_URI = NET_VOS_LOG_ROOT_URI + "/" + NET_VOS_LOG_LEVEL;
	public static final String NET_VOS_LOG_LEVEL_URI_TEMPLATE = NET_VOS_LOG_LEVEL + "/" + "{" + NET_VOS_LOG_LEVEL + "}";
	public static final String NET_VOS_LOG_CONSOLE = "console";
	public static final String NET_VOS_LOG_CONSOLE_ROOT_URI = NET_VOS_LOG_ROOT_URI + "/" + NET_VOS_LOG_CONSOLE;
	public static final String NET_VOS_LOG_CONSOLE_URI_TEMPLATE = NET_VOS_LOG_CONSOLE + "/" + "{" + NET_VOS_LOG_CONSOLE + "}";
	public static final String NET_VOS_SHUTDOWN = "shutdown";
	public static final String NET_VOS_DAL_DATA_ROOT_URI = "vos_data";
	public static final String NET_VOS_DAL_DATA_API_ROOT_PACKAGE_NAME = "DalDataService";
	public static final String NET_VOS_COUNT_PARAM_NAME = "count";
	public static final String NET_VOS_TIMEOUT_PARAM_NAME = "timeout";
	public static final int MAX_WAIT_WRITE_COUNTER = 4;
	public static final int WRITE_MONITOR_SLEEP_TIME = 1000;

	public static final String NET_VOS_DAL_REPORT_ROOT_URI = "vos_report";
	public static final String NET_VOS_DAL_REPORT_API_ROOT_PACKAGE_NAME = "DalReportService";
	

	public static final String PROP_NAME_DB_DriverClassName = "DB.DriverClassName";
	public static final String PROP_NAME_DB_User = "DB.User";
	public static final String PROP_NAME_DB_Password = "DB.Password";
	public static final String PROP_NAME_DB_URI = "DB.URI";
	public static final String PROP_NAME_DB_PoolInitialSize = "DB.PoolInitialSize";
	public static final String PROP_NAME_DB_PoolMaxTotal = "DB.PoolMaxTotal";
	public static final String PROP_NAME_DB_PoolWaitForConnectionTimeout = "DB.PoolWaitForConnectionTimeout";
	public static final String PROP_NAME_SERVICE_NAME = "Service.Name";
	public static final String PROP_NAME_LOG_LEVEL = "Log.Level";
	public static final String PROP_NAME_LOG_DAYS_TO_KEEP = "Log.DaysToKeep";
	public static final String PROP_NAME_LOG_ARCHIVE_COMMAND = "Log.ArchiveCommand";	
	public static final String PROP_NAME_LOG_CONSOLE = "Log.Console";	
	public static final String PROP_NAME_DB_SQL_Startements_Properties_File_Name = "DB.SqlStatementsPropertiesFileName";
	public static final String PROP_NAME_DB_SQL_ResultSetAsObject = "DB.ResultSetAsObject";
	public static final String PROP_NAME_DAL_IP_ADDRESS = "DAL.IPAddress";
	public static final String PROP_NAME_DAL_PORT = "DAL.Port";
	public static final String PROP_NAME_API_TIMEOUT = "API.Timeout";
	public static final String PROP_NAME_ALGORITHMS_CONFIG_FILE = "ALGO.Config";
	public static final String PROP_NAME_ALGORITHMS_CONFIG_SOURCES = "ALGO.ConfigSources";
	public static final String PROP_NAME_ALGORITHMS_WIN_MOUNT_FILE = "ALGO.WinMountFile";
	public static final String PROP_NAME_ALGORITHMS_LINUX_MOUNT_FILE = "ALGO.LinuxMountFile";
    
	public static final String JSON_ROOT_META_PROP_NAME = "Meta";
	public static final String JSON_META_ERROR_PROP_NAME = "error";
	public static final String JSON_META_MESSAGE_PROP_NAME = "message";
	//message
	public static final String JSON_ROOT_DATA_PROP_NAME = "Data";
	public static final String CONNECTION_PRE_STATEMENT_PARAM_NAME = "Connection.PreStetement";
	
	public static final String OPERANDS_DATA_EXPORT_FILE_EXTENSION = ".operands_data";
	public static final String CDR_DATA_EXPORT_FILE_EXTENSION = ".cdr_data";
	public static final String METERS_DATA_EXPORT_FILE_EXTENSION = ".meters_data";
	
	public static final int    NPL_DECIMAL_PRECISION_SCALE = 4;
	public static final int    NPL_DEFAULT_FILE_MAX_INTERVAL = 60;
	public static final int    NPL_DEFAULT_REFRESH_DATA_INTERVAL = 10;
	public static final int    NPL_DEFAULT_EXPORT_FILE_MAX_LINES_NUM = 2400;
	public static final int    NPL_THREADPOOL_THREADS_NUMBER = 2;
	
	public static final String NPL_DEFAULT_EXPORT_TEMP_PATH = "\\Export\\Temp\\";
	public static final String NPL_DEFAULT_EXPORT_FINAL_PATH = "\\Export\\";
	public static final String NPL_DEFAULT_ROOT_DIRECTORY = "C:\\VOS";
	//Write to file method INTERVAL_CLOSE(0),INTERVAL_FLUSH(1),FINAL_CLOSE(2)
	public static final int    NPL_DEFAULT_WRITE_FILE_METHOD = 2;
	public static final String NET_VOS_NPL_BASE_URI = "vos_npl";
	public static final String NET_VOS_NPL_API_ROOT_PACKAGE_NAME = "NPLService";
	public static final String NPL_FAILED_REFRESH_MESSAGE = "Failed Refresh";
	public static final String NPL_COMPILED_FOLDER = "CompiledClasses1";
	public static final String NPL_COMPILED_CLASS = "CalculatedOperands";
	
	public static final String CSV_COMMA_DELIMITER = "\t";
	public static final String CSV_NAME_PARTS_DELIMITER = "_";
	public static final String CSV_COMMA_DELIMITER_REPLACEMENT = "    ";
	public static final String CSV_NEW_LINE_SEPARATOR = "\n";
	public static final String CSV_DOUBLE_QUOTES = "\"";
	public static final String CSV_EXTENSION = ".csv";
	public static final String CSV_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	
	public static final String ZIP_EXTENSION = ".zip";
	
	public static final int THREAD_TERMINATION_TIME = 30;
	
	public static final int GEN_DEFAULT_API_TIMEOUT = 11000;
	
	public static final int FTPTM_DEFAULT_ORPHAND_FTP_FILES_TASK_WAKEUP_PERIOD = 86400;
	public static final int FTPTM_DEFAULT_ORPHAND_FTP_FILES_TIMEOUT = 10800;
	public static final String NET_VOS_FTPTM_BASE_URI = "vos_ftp";
	public static final String NET_VOS_FTPTM_API_ROOT_PACKAGE_NAME = "FTPTMService";
	
	public static final int METERS_TEMP_TYPE_COLD = 0;
	public static final int METERS_TEMP_TYPE_HOT = 1;
	public static final int METERS_TEMP_TYPE_PLC = 99;
	public static final int METERS_TEMP_TYPE_NONE = 999;
	
	public static final String SAMPLE_TIME = "sample_time";
	public static final String NOW_STR = "now()::timestamp";
	
	private Constants() 
	{
    }
}
