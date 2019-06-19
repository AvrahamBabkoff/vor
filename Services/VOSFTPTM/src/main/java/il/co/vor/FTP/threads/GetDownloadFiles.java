package il.co.vor.FTP.threads;

import java.util.ArrayList;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

import il.co.vor.DalDataObjects.FtpFile;
import il.co.vor.FTPTM.ServiceParameters;
import il.co.vor.FTPTM.StateMachineManager;
import il.co.vor.common.VosDataResources;

public class GetDownloadFiles extends Observable implements Runnable {

	private int _numberOfFTPFiles;
	private Logger _logger = Logger.getLogger(GetDownloadFiles.class.getName());
	private int _serviceID = ServiceParameters._myService.getServiceId();
	private ArrayList<FtpFile> _ftpFiles;

	public void run() {
		try {
			_numberOfFTPFiles = StateMachineManager.allocate();
			_logger.log(Level.INFO, "New " + _numberOfFTPFiles + " Download files");
			_ftpFiles = ServiceParameters.getDalDataClient().getFtpFiles().getFtpFilesForDownload(_serviceID, _numberOfFTPFiles).getApiData().get(VosDataResources.FTP_FILES_NAME);
			
			if(_numberOfFTPFiles != _ftpFiles.size()){
				StateMachineManager.release(_numberOfFTPFiles - _ftpFiles.size());
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			_logger.log(Level.SEVERE, "GetDownloadFiles run failed " + e.getMessage());
			StateMachineManager.release(_numberOfFTPFiles);
		}
		setChanged();
		notifyObservers(_ftpFiles);
	}
	
	public int getNumberOfFTPFiles() {
		return _numberOfFTPFiles;
	}

}
