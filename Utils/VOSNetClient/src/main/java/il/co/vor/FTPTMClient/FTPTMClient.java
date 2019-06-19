package il.co.vor.FTPTMClient;

import java.util.logging.Level;
import java.util.logging.Logger;

import il.co.vor.VOSNetClient.GenClient;
import il.co.vor.common.Constants;

public class FTPTMClient extends GenClient
{
	private static Logger _logger = Logger.getLogger(FTPTMClient.class.getName());

	
	public FTPTMClient(String sIP, int iPort)
	{
		super(sIP, iPort, Constants.NET_VOS_FTPTM_BASE_URI);

		_logger.log(Level.WARNING, String.format("FTPTMClient initialized: IP = %s, port = %d", sIP, iPort));
	}
	
}
