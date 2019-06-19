package il.co.vor.FTP.threads;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import il.co.vor.FTPTM.ServiceParameters;
import il.co.vor.common.VosDataResources;

public class BulkState implements Runnable {

	private Logger _logger = Logger.getLogger(BulkState.class.getName());
	private int _serviceID;
	private int _numberOfBulkInserts;
	
	public BulkState(int numberOfBulkInserts) {
		// TODO Auto-generated constructor stub
		_numberOfBulkInserts = numberOfBulkInserts;
		_serviceID = ServiceParameters._myService.getServiceId();
	}
	@Override
	public void run() 
	{
		try
		{
			// TODO Auto-generated method stub
			long startTime,m1;
			_logger.log(Level.INFO, "Start Bulk insert of " + _numberOfBulkInserts + " files ");
			startTime = System.currentTimeMillis();
			ServiceParameters.getDalDataClient().getFtpFiles().bulkInsert(_serviceID, _numberOfBulkInserts).getApiData().get(VosDataResources.FTP_FILES_NAME);
			m1 = System.currentTimeMillis();
			_logger.log(Level.INFO, "End of Bulk insert of " + _numberOfBulkInserts + " files, time := " + (m1-startTime) + " mil");
		}
		catch (Exception e) 
		{
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			// TODO Auto-generated catch block
			_logger.log(Level.SEVERE, "BulkState failed. exception: " + e.getMessage());
			_logger.log(Level.SEVERE, "BulkState failed.  trace: " + sw.toString());
		}		
	}

}
