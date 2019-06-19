package il.co.vor.API.GenUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import il.co.vor.VOSNetServer.NetServer;
import il.co.vor.common.Constants;

@Path(Constants.NET_VOS_SHUTDOWN)
public class StopService 
{
	private static final Logger logger = Logger.getLogger(PostRequest.class.getName());
	@POST
	public String getLogLevel() 
	{
		logger.log(Level.SEVERE, "requested to shutdown...");
		NetServer.ShutDownNetServer();
		return "TERMINATING...";
    }

}
