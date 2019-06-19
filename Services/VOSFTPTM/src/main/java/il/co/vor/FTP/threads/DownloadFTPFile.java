package il.co.vor.FTP.threads;

import java.io.File;
import java.util.ArrayList;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

import il.co.vor.DalConfigObjects.FtpServer;
import il.co.vor.DalDataObjects.FtpFile;
import il.co.vor.FTPTM.FTPClientManager;
import il.co.vor.FTPTM.ServiceParameters;
import il.co.vor.common.Constants;
import il.co.vor.common.Enums.FilePhaseStatus;
import il.co.vor.common.Enums.FileType;
import il.co.vor.common.ParamNames;
import il.co.vor.utilities.ParametersReader;

public class DownloadFTPFile extends Observable implements Runnable {

	private Logger _logger = Logger.getLogger(DownloadFTPFile.class.getName());
	private FtpFile _myFtpFile;
	private String _myLocalRootPath;
	private ArrayList<FtpFile> _ftpFileList;
	private String _ftpServer;
	private int _ftpPort;
	private String _ftpUsername;
	private String _ftpPassword;
	private String _ftpServerRootPath;
	private FTPClientManager _ftpClientManager;
	private String _remotePath;
	private String _localPath;
	private int _maxReattemptCounter;
	
	public DownloadFTPFile(FtpFile myFtpFile, String myLocalRootPath) {
		// TODO Auto-generated constructor stub
		_myFtpFile = myFtpFile;
		_myLocalRootPath = myLocalRootPath;
		_ftpClientManager = new FTPClientManager();
		_maxReattemptCounter = ParametersReader.getParameterAsInt(ParamNames.FTPTM_MAX_FTP_ACTION_RETRIES);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		boolean res;
		int reattemptCounter = 0;
		FilePhaseStatus updateStatus = FilePhaseStatus.FAILED;
		String fileName = _myFtpFile.getFtpFileName();
		long startTime,m1,m2,m3;
		
		try {
			
			do {
				reattemptCounter++;
				GetFTPServerConnectionDetailes();
				_logger.log(Level.INFO, "Start reattempt " + reattemptCounter + " of downloading file := " + fileName);
				startTime = System.currentTimeMillis();
				_ftpClientManager.DoConnect(_ftpServer, _ftpPort);
				_ftpClientManager.DoLogin(_ftpUsername, _ftpPassword);
				m1 = System.currentTimeMillis();
				res = _ftpClientManager.DoRetrieveFile(_remotePath, _localPath);
				m2 = System.currentTimeMillis();
				if (res) {
					updateStatus = FilePhaseStatus.DONE;
				}
				_logger.log(Level.INFO, "End reattempt " + reattemptCounter + " of downloading file := " + fileName + Constants.LINE_SEPARATOR
						+ " Connect + Login time := " + (m1-startTime) + " mil" + " Upload time := " + (m2-m1) + " mil");
			} while (reattemptCounter <= _maxReattemptCounter && (res==false));
			
			_ftpFileList = new ArrayList<FtpFile>();
			_ftpFileList.add(_myFtpFile);
			_myFtpFile.setFtpFileDwlPhaseStatus(updateStatus.ordinal());
			_myFtpFile.setFtpFileLocalPath(_localPath.replace(_myFtpFile.getFtpFileName(), ""));
			ServiceParameters.getDalDataClient().getFtpFiles().updateFtpFilesDownloadCompleted(_ftpFileList);
			m3 = System.currentTimeMillis();
			_logger.log(Level.INFO, "Update status of file := " + fileName + " finished." + Constants.LINE_SEPARATOR
					+ " Time := " + (m3-m2) + " mil" + " Tottal time := " + (m3-startTime) + " mil");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			_logger.log(Level.SEVERE, "DownloadFTPFile run failed " + e.getMessage());
		}
		setChanged();
		notifyObservers();
	}

	private void GetFTPServerConnectionDetailes() {
		// TODO Auto-generated method stub
		int siteID 		= _myFtpFile.getSiteId();
		FileType fileType = FileType.values()[_myFtpFile.getFtpFileType()];
		String siteName;
		String serviceName;
		String folderName;
		FtpServer server = ServiceParameters._ftpServers.get(siteID);
		
		_ftpServer 			= server.getFtpServerAddressIp();
		_ftpPort 			= server.getFtpServerAddressPort();
		_ftpUsername 		= server.getFtpServerUserName();
		_ftpPassword 		= server.getFtpServerUserPassword();
		_ftpServerRootPath 	= server.getftpServerRootPath();
		 
		_remotePath = _myFtpFile.getFtpFileServerPath().replace(_ftpServerRootPath, "") + Constants.FILE_SEPARATOR + _myFtpFile.getFtpFileName();
		
		siteName = server.getSite().getSiteName();
		serviceName = ParametersReader.getParameter(Constants.PROP_NAME_SERVICE_NAME, false);
		folderName = _myFtpFile.getFtpFileArchiveFolder();
		
		_localPath = _myLocalRootPath + Constants.FILE_SEPARATOR + serviceName + Constants.FILE_SEPARATOR + siteName + Constants.FILE_SEPARATOR + fileType.name() + folderName;
		
		File folderDir = new File(_localPath);

		// if the directory does not exist, create it
		if (!folderDir.exists()) {

		    try{
		    	folderDir.mkdirs();
		    	_logger.log(Level.INFO, "Date folder created");
		    } 
		    catch(SecurityException se){
		    	_logger.log(Level.SEVERE, "Failed to create Date folder - security violation");
		    }        

		}
		_localPath = _localPath + Constants.FILE_SEPARATOR + _myFtpFile.getFtpFileName();
	}

}
