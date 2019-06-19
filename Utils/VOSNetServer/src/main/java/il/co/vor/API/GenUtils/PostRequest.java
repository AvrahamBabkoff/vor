package il.co.vor.API.GenUtils;

import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;

@Provider
public class PostRequest implements ContainerResponseFilter
{
	private static final Logger logger = Logger.getLogger(PostRequest.class.getName());

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException 
	{
		ObjectMapper mapper = null;//new ObjectMapper();
		Object o = null;
		String sClassName = null;
		String sPayload = "";
		o = responseContext.getEntity();
		
		if (null != o)
		{			
			sClassName = o.getClass().getName();
			
			if (sClassName.equals("java.lang.String"))
			{
				sPayload = o.toString();
			}
			else			
			{
				mapper = new ObjectMapper();
				sPayload = mapper.writeValueAsString(o);
			}
		}
		sPayload = sPayload.substring(0, Math.min(2048, sPayload.length()));
        logger.log(Level.WARNING, String.format("API response code: %d. %s\t%s", 		
        		responseContext.getStatus(),
        		System.lineSeparator(), 
        		sPayload));
	}

}
