package il.co.vor.VOSNetClient;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;

import il.co.vor.common.Constants;
import il.co.vor.utilities.PropertyFileReader;



public class NetClient 
{
	private static Logger _logger = Logger.getLogger(NetClient.class.getName());
	private static int m_iApiTimeout;
	@SuppressWarnings("unused")
	private static final boolean bInitialize = _init();

	private static boolean _init()
	{
		m_iApiTimeout = PropertyFileReader.getPropertyAsInt(Constants.PROP_NAME_API_TIMEOUT, Constants.GEN_DEFAULT_API_TIMEOUT);
		SSLDisable.disableCertificateValidation();
		_logger.log(Level.WARNING, String.format("NetClient initialized: m_iApiTimeout = %d", m_iApiTimeout));
		return true;
	}
	
	



	public static String callGetRaw(String _baseURL, String sResource)
	{
		URL obj = null;
		HttpsURLConnection con;
		InputStreamReader in = null;
		String sRes = null;
		StringBuilder sb = null;
		String url = _baseURL + sResource;
		int bufferSize = 1024;
		int responseCode;
		char[] buffer = new char[bufferSize];
		try
		{
			obj = new URL(url);
			con = (HttpsURLConnection) obj.openConnection();
			con.setConnectTimeout(m_iApiTimeout);
			con.setReadTimeout(m_iApiTimeout);	
	
			con.setRequestMethod("GET");
	
	
			//@SuppressWarnings("unused")
			responseCode = con.getResponseCode();
			if(HttpURLConnection.HTTP_OK == responseCode)
			{
				in = new InputStreamReader(con.getInputStream(), "UTF-8");
				
				sb = new StringBuilder();
		
				for (; ; ) 
				{
				    int rsz = in.read(buffer, 0, buffer.length);
				    if (rsz < 0)
				        break;
				    sb.append(buffer, 0, rsz);
				}			
				//in.close();
		
				sRes = sb.toString();
			}
		}
		catch (Exception e)
		{
			_logger.log(Level.SEVERE, String.format("Exception. Source = %s, Exception=%s", sResource, e.getMessage()));
		}
		finally
		{
				try 
				{
					if (null != in)
					{
						in.close();
					}
				} 
				catch (IOException e) 
				{
				}
		}
		return sRes;
	}

	public static String callPostRaw(String _baseURL, String sResource, String sBody)
	{
		URL obj = null;
		HttpsURLConnection con;
		InputStreamReader in = null;
		OutputStream out = null;
		String sRes = null;
		StringBuilder sb = null;
		String url = _baseURL + sResource;
		int bufferSize = 1024;
		int responseCode;
		char[] buffer = new char[bufferSize];
		try
		{
			byte [] theBody = sBody.getBytes("UTF-8");
			
			obj = new URL(url);
			con = (HttpsURLConnection) obj.openConnection();
			con.setConnectTimeout(m_iApiTimeout);
			con.setReadTimeout(m_iApiTimeout);	
	
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-type", "application/json");
	
			con.setDoOutput(true);
			
			out = con.getOutputStream();
			out.write(theBody);
			//con.getOutputStream().write(theBody);
			
			//@SuppressWarnings("unused")
			responseCode = con.getResponseCode();
	
			if(HttpURLConnection.HTTP_OK == responseCode)
			{
				in = new InputStreamReader(con.getInputStream(), "UTF-8");
				
				sb = new StringBuilder();
		
				for (; ; ) 
				{
				    int rsz = in.read(buffer, 0, buffer.length);
				    if (rsz < 0)
				        break;
				    sb.append(buffer, 0, rsz);
				}			
				//in.close();
		
				sRes = sb.toString();
			}
		}
		catch (Exception e)
		{
			_logger.log(Level.SEVERE, String.format("Exception. Source = %s. Exception=%s", sResource, e.getMessage()));
			_logger.log(Level.SEVERE, String.format("Exception. Body = %s", sBody));
		}
		finally
		{
				try 
				{
					if (null != out)
					{
						out.close();
					}
				} 
				catch (IOException e) 
				{
				}
				try 
				{
					if (null != in)
					{
						in.close();
					}
				} 
				catch (IOException e) 
				{
				}
		}
		return sRes;
	}
}
