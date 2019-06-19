package il.co.vor.FTPTM;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import il.co.vor.VOSLogger;
import il.co.vor.DalConfigObjects.FtpPlaceHolder;
import il.co.vor.DalConfigObjects.FtpServer;
import il.co.vor.DalDataObjects.FtpFile;
import il.co.vor.FTP.defines.Constants;
import il.co.vor.FTP.defines.Enums.FtpServerSiteType;
import il.co.vor.FTP.threads.BulkState;
import il.co.vor.FTP.threads.DirState;
import il.co.vor.FTP.threads.DownloadFTPFile;
import il.co.vor.FTP.threads.GetDownloadFiles;
import il.co.vor.FTP.threads.GetUploadFiles;
import il.co.vor.FTP.threads.OrphandFTPFiles;
import il.co.vor.FTP.threads.UploadFTPFile;
import il.co.vor.common.Enums;
import il.co.vor.common.ParamNames;

import il.co.vor.utilities.ParametersReader;


public class StateMachineManager implements Observer {

	private static Logger _logger = Logger.getLogger(FTPClientManager.class.getName());

	// (num of sites)*dir + dwl + upload + bulk + archive + clean
	private final static int THREADS = ParametersReader.getParameterAsInt(ParamNames.FTPTM_FTP_STATE_MACHINE_THREADPOOL_SIZE);
	private static ScheduledExecutorService _actionScheduledExecutor;
	private static ThreadPoolExecutor _actionExecutor;
	private static int _dirStatePeriod;
	private static int _bulkStatePeriod;
	private static int _dwlUploadStatePeriod;
	private static int _orphandFTPFilesTaskWakeupPeriod;
	
	private static int _numberOfParallelDownloadsUploads;
	private static int _currentQueueCapacity;
	private static int _maxNumberOfDownloadUploadFiles;
	
	private static int _numberOfBulkInserts;
	private static int _orphandFTPFilesTimeout;
	private static ArrayList<FtpPlaceHolder> _myServicePlaceHolders;
	private static String _myLocalRootPath;
	private static FTPClientManager _ftpClientManager;
	//Runnable threads
	private static OrphandFTPFiles _orphandFTPFiles;
	private static DirState _dirState;
	private static BulkState _bulkState;
	private static GetDownloadFiles _getDownloadFiles; 
	private static GetUploadFiles _getUploadFiles; 
	private static LinkedBlockingQueue<Runnable> _actionQueue;
	private static StateMachineManager _actionWatcher;

	public static void InitStateMachine() {
		//Init Params
		_myServicePlaceHolders = ServiceParameters._sitesPlaceHolders;
		//bulk params
		_numberOfBulkInserts = ParametersReader.getParameterAsInt(ParamNames.FTPTM_NUMBER_OF_BULK_FILES); //10
		_bulkStatePeriod = ParametersReader.getParameterAsInt(ParamNames.FTPTM_BULK_PERIOD_TIME);//10
		
		//bulk params
		_dwlUploadStatePeriod = ParametersReader.getParameterAsInt(ParamNames.FTPTM_DWL_UPLOAD_PERIOD_TIME);//5
		
		//OrphandFTPFiles params
		_orphandFTPFilesTaskWakeupPeriod = ParametersReader.getParameterAsInt(ParamNames.FTPTM_ORPHAND_FTP_FILES_TASK_WAKEUP_PERIOD, il.co.vor.common.Constants.FTPTM_DEFAULT_ORPHAND_FTP_FILES_TASK_WAKEUP_PERIOD);
		_orphandFTPFilesTimeout = ParametersReader.getParameterAsInt(ParamNames.FTPTM_ORPHAND_FTP_FILES_TIMEOUT, il.co.vor.common.Constants.FTPTM_DEFAULT_ORPHAND_FTP_FILES_TIMEOUT);
		
		//download and upload params
		_dirStatePeriod = ParametersReader.getParameterAsInt(ParamNames.FTPTM_DIR_PERIOD_TIME);//10
		_maxNumberOfDownloadUploadFiles = ParametersReader.getParameterAsInt(ParamNames.FTPTM_MAX_NUMBER_OF_DWL_UPLOAD_FILES);//30
		_numberOfParallelDownloadsUploads = ParametersReader.getParameterAsInt(ParamNames.FTPTM_NUMBER_OF_PARALLEL_DWL_UPLOAD);//30
		
		_currentQueueCapacity = ParametersReader.getParameterAsInt(ParamNames.FTPTM_MAX_FILE_OPERATION_QUEUE_SIZE);//100
		
		_actionScheduledExecutor = Executors.newScheduledThreadPool(THREADS);
		_ftpClientManager = new FTPClientManager();
		_actionWatcher = new StateMachineManager();
		
		_myLocalRootPath = ParametersReader.getParameter(ParamNames.FTPTM_LOCAL_ROOT_PATH, true);
		
		//OrphandFTPFiles
		_orphandFTPFiles = new OrphandFTPFiles(_orphandFTPFilesTimeout);
		_actionScheduledExecutor.scheduleAtFixedRate(_orphandFTPFiles, 0, _orphandFTPFilesTaskWakeupPeriod, TimeUnit.SECONDS);
		
		//DIR State
		if (ServiceParameters._ftpServers != null && ServiceParameters._ftpServers.size() != 0){
			for(FtpServer value : ServiceParameters._ftpServers.values()){
				if(value.getFtpServerSiteType() == FtpServerSiteType.SITE.ordinal()){
					_myServicePlaceHolders = SelectServerPlaceHolders(_myServicePlaceHolders,value);
					_dirState = new  DirState(value,_myServicePlaceHolders);
					_actionScheduledExecutor.scheduleAtFixedRate(_dirState, Constants.SCHEDULE_OPERATION_DELAY, _dirStatePeriod, TimeUnit.SECONDS);	
				}
			}
		}
		//Bulk State
		if (ServiceParameters._ftpServers != null && ServiceParameters._ftpServers.size() != 0){

			_bulkState = new  BulkState(_numberOfBulkInserts);
			_actionScheduledExecutor.scheduleAtFixedRate(_bulkState, Constants.SCHEDULE_OPERATION_DELAY, _bulkStatePeriod, TimeUnit.SECONDS);	

		}
		
		_actionQueue = new LinkedBlockingQueue<Runnable>();		
		
		_actionExecutor = new ThreadPoolExecutor(_numberOfParallelDownloadsUploads, _numberOfParallelDownloadsUploads, 1000,TimeUnit.MILLISECONDS, _actionQueue);
		_actionExecutor.prestartAllCoreThreads();
		
		//Download
		_getDownloadFiles = new GetDownloadFiles();
		_getDownloadFiles.addObserver(_actionWatcher);
		_actionScheduledExecutor.scheduleAtFixedRate(_getDownloadFiles, _dwlUploadStatePeriod, _dirStatePeriod, TimeUnit.SECONDS);
		
		//Upload
		CreateDBMachineFolderTree();
		_getUploadFiles = new GetUploadFiles();
		_getUploadFiles.addObserver(_actionWatcher);
		_actionScheduledExecutor.scheduleAtFixedRate(_getUploadFiles, _dwlUploadStatePeriod, _dirStatePeriod, TimeUnit.SECONDS);
		
		}
		     

	private static ArrayList<FtpPlaceHolder> SelectServerPlaceHolders(ArrayList<FtpPlaceHolder> servicePlaceHolders,FtpServer server) {
		// TODO Auto-generated method stub
		ArrayList<FtpPlaceHolder> sitePlaceHolders = new ArrayList<FtpPlaceHolder>();
		
		if (servicePlaceHolders != null && servicePlaceHolders.size() > 0) {
			for (FtpPlaceHolder ph : servicePlaceHolders) {
				if (ph.getSiteId() == server.getSite().getSiteId())
					sitePlaceHolders.add(ph);
			}
		}
		return sitePlaceHolders;
	}

	private static void AddDownloadFilesToActionQueues(ArrayList<FtpFile> ftpFiles) {
		// TODO Auto-generated method stub
		for (FtpFile ff: ftpFiles) {
			DownloadFTPFile item = new DownloadFTPFile(ff , _myLocalRootPath);
			item.addObserver(_actionWatcher);
			_actionQueue.add(item);
		}
	}
	
	private static void AddUploadFilesToActionQueues(ArrayList<FtpFile> ftpFiles) {
		// TODO Auto-generated method stub
		for (FtpFile ff: ftpFiles) {
			UploadFTPFile item = new UploadFTPFile(ff);
			item.addObserver(_actionWatcher);
			_actionQueue.add(item);
		}
	}
	
	public static synchronized int allocate(){
		int numberOfFiles;
		int prevCurrentQueueCapacity = _currentQueueCapacity;
		numberOfFiles = Math.min(_maxNumberOfDownloadUploadFiles, _currentQueueCapacity / 2);
		_currentQueueCapacity -= numberOfFiles;
		_logger.log(Level.INFO, "allocate: Before := " + prevCurrentQueueCapacity + " allocate: After := " + _currentQueueCapacity);


		return numberOfFiles;
	}
	
	public static synchronized void release(int releaseBy){
		int prevCurrentQueueCapacity = _currentQueueCapacity;
		_currentQueueCapacity += releaseBy;
		_logger.log(Level.INFO, "release: Before := " + prevCurrentQueueCapacity + " release: After := " + _currentQueueCapacity);

	}
	
	public static void DoGracefulShutdown(){
		
		VOSLogger.Terminate();
		_actionScheduledExecutor.shutdown();
		_actionExecutor.shutdown();
		
		try {
			_actionScheduledExecutor.awaitTermination(30, TimeUnit.SECONDS);
			_actionExecutor.awaitTermination(30, TimeUnit.SECONDS);
		} catch (InterruptedException e) {		
			_logger.log(Level.SEVERE, "Failed to awaitTermination of Shutdown Service : " + e.getMessage());
		}
		_actionScheduledExecutor.shutdownNow();
		_actionExecutor.shutdownNow();
	}
	
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		@SuppressWarnings("unchecked")
		ArrayList<FtpFile> res = (ArrayList<FtpFile>) arg;
		
		switch (o.getClass().getSimpleName()) {
		case "GetDownloadFiles":
			if(res != null && res.size() >0)
			{
				AddDownloadFilesToActionQueues(res);
			}
			break;
		case "GetUploadFiles":
			if(res != null && res.size() >0)
			{
				AddUploadFilesToActionQueues(res);
			}
			break;

		case "DownloadFTPFile":
			release(1);
			break;

		case "UploadFTPFile":
			release(1);
			break;
		default:
			break;
		}
	}
	
	private static void CreateDBMachineFolderTree() {

		FtpServer dbServie = ServiceParameters._ftpServers.get(-100);
		String ftpServer 	= dbServie.getFtpServerAddressIp();
		int ftpPort 		= dbServie.getFtpServerAddressPort();
		String ftpUsername 	= dbServie.getFtpServerUserName();
		String ftpPassword 	= dbServie.getFtpServerUserPassword();

		for(FtpServer value : ServiceParameters._ftpServers.values()){

			if(value.getFtpServerSiteType() == 0){
				String siteFolderName = il.co.vor.common.Constants.FILE_SEPARATOR + value.getSite().getSiteName();
				String fileTypeFolderName;

				_ftpClientManager.DoConnect(ftpServer, ftpPort);
				_ftpClientManager.DoLogin(ftpUsername, ftpPassword);
				try{
					if (!_ftpClientManager.CheckDirectoryExists(siteFolderName)) {

						_ftpClientManager.DoMakeDirectory(siteFolderName,false,false);
					}

					fileTypeFolderName = Enums.FileType.CDR_DATA.name();
					if(!_ftpClientManager.CheckDirectoryExists(siteFolderName + il.co.vor.common.Constants.FILE_SEPARATOR + fileTypeFolderName))
						_ftpClientManager.DoMakeDirectory(siteFolderName + il.co.vor.common.Constants.FILE_SEPARATOR + fileTypeFolderName,false,false);

					fileTypeFolderName = Enums.FileType.OPERANDS_DATA.name();
					if(!_ftpClientManager.CheckDirectoryExists(siteFolderName + il.co.vor.common.Constants.FILE_SEPARATOR + fileTypeFolderName))
						_ftpClientManager.DoMakeDirectory(siteFolderName + il.co.vor.common.Constants.FILE_SEPARATOR + fileTypeFolderName,false,false);

					fileTypeFolderName = Enums.FileType.METERS_DATA.name();
					if(!_ftpClientManager.CheckDirectoryExists(siteFolderName + il.co.vor.common.Constants.FILE_SEPARATOR + fileTypeFolderName))
						_ftpClientManager.DoMakeDirectory(siteFolderName + il.co.vor.common.Constants.FILE_SEPARATOR + fileTypeFolderName,false,false);

					_logger.log(Level.INFO, "Root DIR created");

				}
				catch(SecurityException se){
					_logger.log(Level.SEVERE, "Failed to create Root folder - security violation");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					_logger.log(Level.SEVERE, "CreateDBMachineFolderTree IOException. " + e.getMessage());
				}        
			}
		}
	}

}
