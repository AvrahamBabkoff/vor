package il.co.vor.API.GenUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class GenericExceptionMapper implements ExceptionMapper<Exception>
{
	private static final Logger logger = Logger.getLogger(GenericExceptionMapper.class.getName());

	@Override
	public Response toResponse(Exception ex) 
	{
		// TODO Auto-generated method stub
//		return null;
		Response response;
		String sError;
		
		sError = ex.getMessage();
	    if (ex instanceof WebApplicationException) 
	    {
	        WebApplicationException webEx = (WebApplicationException)ex;
	        response = webEx.getResponse();
	    } 
	    else 
	    {
	        response = Response.status(Response.Status.INTERNAL_SERVER_ERROR)
	                .entity(ex.getMessage()).type("text/plain").build();
	    }
        logger.log(Level.SEVERE, String.format("GenericExceptionMapper response code: %d. %s\t%s", 		
        		response.getStatus(),
        		System.lineSeparator(), 
        		sError));
	    return response;		
	}

}
