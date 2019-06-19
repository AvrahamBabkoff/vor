package il.co.vor.FTP.threads;

//import java.io.PrintWriter;
//import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.net.ftp.FTPFile;

import il.co.vor.DalConfigObjects.FtpPlaceHolder;
import il.co.vor.DalConfigObjects.FtpServer;
import il.co.vor.DalDataObjects.FtpFile;
import il.co.vor.FTP.defines.Constants;
import il.co.vor.FTPTM.FTPClientManager;
import il.co.vor.FTPTM.ServiceParameters;
import il.co.vor.common.Enums.FileType;
import il.co.vor.common.VosDataResources;

public class DirState implements Runnable {

	private Logger _logger = Logger.getLogger(DirState.class.getName());
	private FtpServer _myServer;
	private FTPClientManager _ftpClientManager;
	private ArrayList<FtpPlaceHolder> _myServerPlaceHolders;
	private Dictionary<Integer,Object> _nextPlaceHolders;
	private ArrayList<FtpFile> _myDiredFiles;
	private ArrayList<FtpFile> _myInsertResultFiles;
	//private Boolean _isPlaceHolderLastAttempt = false;
	private String _serverRootPath;
	private int _siteID;
	
	public DirState(FtpServer server, ArrayList<FtpPlaceHolder> _ServerPlaceHolders) {
		// TODO Auto-generated constructor stub
		_myServer = server;
		_myServerPlaceHolders = _ServerPlaceHolders;
		_serverRootPath = _myServer.getftpServerRootPath();
		_siteID = _myServer.getSite().getSiteId();
		_nextPlaceHolders = new Hashtable<>();
		_ftpClientManager = new FTPClientManager();
	}

	public void run() {
		// TODO Auto-generated method stub	
		long startTime,m1,m2 = 0,m3 = 0;
		String remotePath = "";
		String siteName = _myServer.getSite().getSiteName();
		try {
			if (_myServer.getFtpServerAddressIp() != null && !_myServer.getFtpServerAddressIp().isEmpty()) {
				
				_myInsertResultFiles = new ArrayList<FtpFile>();
				_logger.log(Level.INFO, "Start DIR site name := " + siteName);
				startTime = System.currentTimeMillis();
				_ftpClientManager.DoConnect(_myServer.getFtpServerAddressIp(), _myServer.getFtpServerAddressPort());
				_ftpClientManager.DoLogin(_myServer.getFtpServerUserName(), _myServer.getFtpServerUserPassword());
				m1 = System.currentTimeMillis();
				_myServerPlaceHolders = _ftpClientManager.DoDirPlaceHolder(_myServerPlaceHolders,_siteID);

				if (_myServerPlaceHolders != null) {
					for (FtpPlaceHolder ph : _myServerPlaceHolders) {
						_myDiredFiles = new ArrayList<FtpFile>();
						FileType fileType = FileType.values()[ph.getFtpFileType()];
						//DIR
						if(_nextPlaceHolders.get(ph.getFtpFileType()) == null){
							CreatNextPlaceHolder(ph,fileType.name());
						}		
						remotePath = fileType.name() + il.co.vor.common.Constants.FILE_SEPARATOR + ph.getFtpPlaceHolderFolder();
						FTPFile [] diredFiles = _ftpClientManager.DoDir(remotePath,Constants.FTP_DIR_TYPE_COMMAND_LISTFILES,_serverRootPath,ph.getFtpFileType(),_siteID,null);
						_myDiredFiles.addAll(_ftpClientManager.ConvertToResultArray(diredFiles,_serverRootPath,remotePath,fileType.ordinal(),_siteID));
						m2 = System.currentTimeMillis();
						_logger.log(Level.INFO,siteName + " dired " +  _myDiredFiles.size() + " files");
						//Insert
						///if (!_myDiredFiles.isEmpty()) {
							_myInsertResultFiles = ServiceParameters.getDalDataClient().getFtpFiles().insertFtpFiles(_myDiredFiles).getApiData().get(VosDataResources.FTP_FILES_NAME);
							_logger.log(Level.INFO,siteName + " insert " +  _myInsertResultFiles.size() + " files");
							m3 = System.currentTimeMillis();
							Object isPlaceHolderLastChecked = _nextPlaceHolders.get(GetKey(ph.getFtpFileType()));
							
							if(isPlaceHolderLastChecked != null){
								ServiceParameters.getDalConfigClient().getFtpPlaceHolders().updateFtpPlaceHolder(ph);
							}
							if(_myInsertResultFiles.isEmpty() && isPlaceHolderLastChecked != null && (Boolean)isPlaceHolderLastChecked){
								DoPlaceHolderChanges(ph, fileType);
								//_isPlaceHolderLastAttempt = false;
							}else if (_myInsertResultFiles.isEmpty() && isPlaceHolderLastChecked != null){
								_nextPlaceHolders.put(GetKey(ph.getFtpFileType()), new Boolean(true));
								//_isPlaceHolderLastAttempt = true;
							}					
						/*}else{
							DoPlaceHolderChanges(ph, fileType);
						}*/
					}				
					_logger.log(Level.INFO, "End of remote path DIR := " + remotePath + " DIR counts " + _myDiredFiles.size() + " files" + il.co.vor.common.Constants.LINE_SEPARATOR
							+ "Connect + Login time := " + (m1-startTime) + " mil" + " DIR time := " + (m2-m1) + " mil" + il.co.vor.common.Constants.LINE_SEPARATOR + 
							"Insert " + _myInsertResultFiles.size() + " files after DIR, time := " + (m3-m2) + " mil" + " Tottal time := " + (m3-startTime) + " mil");

				} else {
					_logger.log(Level.SEVERE, "No FTP Server address !");
				}
			}
			//_logger.log(Level.SEVERE, "DirState completed. SiteId: " + _siteID + ", _myServerPlaceHolders: " + _myServerPlaceHolders.toString());

		} catch (Exception e) {
			_logger.log(Level.SEVERE, "DirState failed. SiteId: " + _siteID + ", exception: " + e.getMessage());
			/*
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			// TODO Auto-generated catch block
			_logger.log(Level.SEVERE, "DirState failed. SiteId: " + _siteID + ", exception: " + e.getMessage());
			_logger.log(Level.SEVERE, "DirState failed. SiteId: " + _siteID + ", trace: " + sw.toString());
			_logger.log(Level.SEVERE, "DirState failed. _myServerPlaceHolders: " + _myServerPlaceHolders.toString());
			*/
		}
		//setChanged();
		//notifyObservers(_myDiredFiles);
	}

	public void DoPlaceHolderChanges(FtpPlaceHolder ph, FileType fileType) {
		ChangePlaceHolderToNextDay(ph.getFtpFileType());
		ServiceParameters.getDalConfigClient().getFtpPlaceHolders().updateFtpPlaceHolder(ph);
		CreatNextPlaceHolder(ph,fileType.name());
		_nextPlaceHolders.put(GetKey(ph.getFtpFileType()), new Boolean(false));
	}

	private void ChangePlaceHolderToNextDay(int fileType) {
		// TODO Auto-generated method stub
		for (FtpPlaceHolder ph : _myServerPlaceHolders) {
			if(ph.getFtpFileType() == fileType){
				ph.setFtpPlaceHolderFolder(_nextPlaceHolders.get(fileType).toString());
			}
		}
	}

	private void CreatNextPlaceHolder(FtpPlaceHolder ph, String fileTypeName) {
		// TODO Auto-generated method stub
		Date date;
		boolean isChangeDirectorySuccess;
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

		try {

			date = format.parse(ph.getFtpPlaceHolderFolder());

			date = addDays(date, 1);

			isChangeDirectorySuccess = _ftpClientManager.ChangeWorkingDirectory(
					fileTypeName + il.co.vor.common.Constants.FILE_SEPARATOR + format.format(date));

			if (isChangeDirectorySuccess) {
				_nextPlaceHolders.put(ph.getFtpFileType(), format.format(date)); 
			} else {
				_nextPlaceHolders.put(ph.getFtpFileType(),
						_ftpClientManager.FindNextPlaceHolder(fileTypeName + il.co.vor.common.Constants.FILE_SEPARATOR,
								ph.getFtpPlaceHolderFolder(), _siteID)); 
			}
			_nextPlaceHolders.put(GetKey(ph.getFtpFileType()), new Boolean(false));

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			_logger.log(Level.SEVERE, "Failed to parse Date format");
		}
	}
	
	private Integer GetKey(int ftpFileType) {
		// TODO Auto-generated method stub
		int res = -1;
		switch (ftpFileType){
		case 1: 
			res = 11;
			break;
		case 2: 
			res = 22;
			break;
		case 3: 
			res = 33;
			break;
		default:
			break;
		}
		return res;
	}

	private Date addDays(Date date, int days) {
		// TODO Auto-generated method stub
		
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.add(Calendar.DATE, days);
				
		return cal.getTime();
	}
}
