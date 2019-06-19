package il.co.vor.FTP.threads;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

import il.co.vor.DalConfigObjects.FtpServer;
import il.co.vor.DalDataObjects.FtpFile;
import il.co.vor.FTPTM.FTPClientManager;
import il.co.vor.FTPTM.ServiceParameters;
import il.co.vor.common.Constants;
import il.co.vor.common.ParamNames;
import il.co.vor.common.Enums.FilePhaseStatus;
import il.co.vor.common.Enums.FileType;
import il.co.vor.utilities.ParametersReader;


public class UploadFTPFile extends Observable implements Runnable {

	private Logger _logger = Logger.getLogger(UploadFTPFile.class.getName());
	private FtpFile _myFtpFile;
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
	
	public UploadFTPFile(FtpFile myFtpFile) {
		// TODO Auto-generated constructor stub
		_myFtpFile = myFtpFile;
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
				_logger.log(Level.INFO, "Start reattempt " + reattemptCounter + " of uploading file := " + fileName);
				startTime = System.currentTimeMillis();
				GetFTPServerConnectionDetailes();
				m1 = System.currentTimeMillis();
				res = _ftpClientManager.DoStoreFile(_remotePath, _localPath);
				m2 = System.currentTimeMillis();
				if (res) {
					updateStatus = FilePhaseStatus.DONE;
				} 
				_logger.log(Level.INFO, "End reattempt " + reattemptCounter + " of uploading file := " + fileName + Constants.LINE_SEPARATOR
							+ " Connect + Login time := " + (m1-startTime) + " mil" + " Upload time := " + (m2-m1) + " mil");
			} while (reattemptCounter <= _maxReattemptCounter && (res==false));
			
			_ftpFileList = new ArrayList<FtpFile>();
			_ftpFileList.add(_myFtpFile);
			_myFtpFile.setFtpFileUploadPhaseStatus(updateStatus.ordinal());
			_myFtpFile.setFtpFileDestinationPath(_ftpServerRootPath + _remotePath.replace(_myFtpFile.getFtpFileName(), ""));
			ServiceParameters.getDalDataClient().getFtpFiles().updateFtpFilesUploadCompleted(_ftpFileList);
			m3 = System.currentTimeMillis();
			_logger.log(Level.INFO, "Update status of file := " + fileName + " finished." + Constants.LINE_SEPARATOR
					+ " Time := " + (m3-m2) + " mil" + " Tottal time := " + (m3-startTime) + " mil");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			_logger.log(Level.SEVERE, "UploadFTPFile run failed " + e.getMessage());
		}
		setChanged();
		notifyObservers();
	}

	private void GetFTPServerConnectionDetailes() {
		// TODO Auto-generated method stub
		FileType fileType = FileType.values()[_myFtpFile.getFtpFileType()];
		int siteID 		= _myFtpFile.getSiteId();
		FtpServer server = ServiceParameters._ftpServers.get(siteID);
		String folderName = _myFtpFile.getFtpFileArchiveFolder();
		FtpServer DBserver = ServiceParameters._ftpServers.get(-100);
		
		_ftpServer 			= DBserver.getFtpServerAddressIp();
		_ftpPort 			= DBserver.getFtpServerAddressPort();
		_ftpUsername 		= DBserver.getFtpServerUserName();
		_ftpPassword 		= DBserver.getFtpServerUserPassword();
		_ftpServerRootPath 	= DBserver.getftpServerRootPath();
		 
		_remotePath = folderName;

		_ftpClientManager.DoConnect(_ftpServer, _ftpPort);
		_ftpClientManager.DoLogin(_ftpUsername, _ftpPassword);
		
		
		// if the directory does not exist, create it
		try {
			boolean res = _ftpClientManager.CheckDirectoryExists(Constants.FILE_SEPARATOR + server.getFtpServerName() + Constants.FILE_SEPARATOR + fileType.name());
			if(res)
			
			if (!_ftpClientManager.CheckDirectoryExists(_remotePath)) {

				try{
					_remotePath = Constants.FILE_SEPARATOR + server.getFtpServerName() + Constants.FILE_SEPARATOR + fileType.name() + _remotePath;
					if (!_ftpClientManager.CheckDirectoryExists(_remotePath)) {
						_ftpClientManager.DoMakeDirectory(_remotePath,false,false);
						_logger.log(Level.INFO, "Date folder created");
					}
				} 
				catch(SecurityException se){
					_logger.log(Level.SEVERE, "Failed to create Date folder - security violation");
				}        

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			_logger.log(Level.SEVERE, "IOException." + e.getMessage());
		}
		_remotePath = _remotePath + Constants.FILE_SEPARATOR + _myFtpFile.getFtpFileName();		
		_localPath = _myFtpFile.getFtpFileLocalPath() + _myFtpFile.getFtpFileName();
	}


}
