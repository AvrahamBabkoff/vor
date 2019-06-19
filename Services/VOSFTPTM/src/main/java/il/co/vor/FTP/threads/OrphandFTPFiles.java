package il.co.vor.FTP.threads;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import il.co.vor.ApiObjectsCommon.NumberOfAffectedRows;
import il.co.vor.FTPTM.ServiceParameters;
import il.co.vor.common.VosDataResources;

public class OrphandFTPFiles implements Runnable {

	private Logger _logger = Logger.getLogger(OrphandFTPFiles.class.getName());

	ArrayList<NumberOfAffectedRows> amrwrnum = null;
	int number_of_affected_rows = 0;
	
	private int _serviceID = -1;
	private int _orphandFTPFilesTimeout;
	
	public OrphandFTPFiles(Integer orphand_ftp_files_timeout) {
		_serviceID = ServiceParameters._myService.getServiceId();
		
		_orphandFTPFilesTimeout = orphand_ftp_files_timeout;
	}
	
	@Override
	public void run() {
		try {
		
			amrwrnum = ServiceParameters.getDalDataClient().getFtpFiles().updateOrphandFtpFiles(_serviceID, _orphandFTPFilesTimeout).getApiData()
					.get(VosDataResources.FTP_ORPHAND_FILES_NAME);
			if ((amrwrnum != null) && (!amrwrnum.isEmpty())) 
			{
				number_of_affected_rows = amrwrnum.get(0).getNumberOfAffectedRows();
				
				if (number_of_affected_rows != 0)
				{
					_logger.log(Level.SEVERE, String.format("OrphandFTPFiles. service id = %s. %s FTP Files were Updated",
						String.valueOf(_serviceID), String.valueOf(number_of_affected_rows)));
				}
			}
		} catch (Exception e) {
			_logger.log(Level.SEVERE, String.format("Exception: %s", e.getMessage()));
			//e.printStackTrace();
			_logger.log(Level.SEVERE,
					String.format("OrphandFTPFiles failed. service id = %s", String.valueOf(_serviceID)));
		}
	}
	
}
