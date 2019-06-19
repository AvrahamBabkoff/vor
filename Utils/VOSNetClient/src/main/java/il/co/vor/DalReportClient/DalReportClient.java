package il.co.vor.DalReportClient;

import java.util.logging.Level;
import java.util.logging.Logger;

import il.co.vor.VOSNetClient.GenClient;
import il.co.vor.common.Constants;

public class DalReportClient extends GenClient
{
private static Logger _logger = Logger.getLogger(DalReportClient.class.getName());

	
	public DalReportClient(String sIP, int iPort)
	{
		super(sIP, iPort, Constants.NET_VOS_DAL_REPORT_ROOT_URI);

		_logger.log(Level.WARNING, String.format("DalReportClient initialized: IP = %s, port = %d", sIP, iPort));
	}
}
