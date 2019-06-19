package il.co.vor.API.GenUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import il.co.vor.VOSNetServer.NetServer;

@Provider
public class PreRequest  implements ContainerRequestFilter 
{
	private static final Logger logger = Logger.getLogger(NetServer.class.getName());
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException 
	{
		InputStreamReader in = null;
        UriInfo uriInfo = requestContext.getUriInfo();
        URI uri = uriInfo.getRequestUri();
        String sp = uri.getRawPath();
        String sq = uri.getRawQuery();
        String sInput;
        logger.log(Level.WARNING, "In Filter!!!!!!!!!!!!!!!!!!!!");
        InputStream ip = requestContext.getEntityStream();
        ByteArrayInputStream bais = null;
		int bufferSize = 1024;
		char[] buffer = new char[bufferSize];
		StringBuilder sb = null;
		in = new InputStreamReader(ip, "UTF-8");
		
		sb = new StringBuilder();

		for (; ; ) 
		{
		    int rsz = in.read(buffer, 0, buffer.length);
		    if (rsz < 0)
		        break;
		    sb.append(buffer, 0, rsz);
		}
		sInput = sb.toString();
		bais = new ByteArrayInputStream(sInput.getBytes(Charset.forName("UTF-8"))); 
		requestContext.setEntityStream(bais);
		//in.close();
        
        logger.log(Level.WARNING, String.format("API called. %s\tURI path:  %s%s\tQuery: %s%s\tBody: %s", 
        		System.lineSeparator(), 
        		sp, System.lineSeparator(), 
        		sq, System.lineSeparator(), 
        		sb.toString()));
		
	}

}
 