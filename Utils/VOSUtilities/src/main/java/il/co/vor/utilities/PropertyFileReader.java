package il.co.vor.utilities;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import il.co.vor.common.Constants;

//import com.sbc.cti.exceptions.CTIBridgeException;

public class PropertyFileReader
{
	
	private final static Properties  m_Props = loadProperties ();



	private static Properties loadProperties()
	{
		Properties props = null;
		String sCmdLineServiceName;
		String sUserHomeDirectory;
		String sPropertiesFileFullPath;
		InputStream is = null;
		try
		{
			// get service name from -D command line argument
			sCmdLineServiceName = System.getProperty(Constants.SYSTEM_PROPERTIES_SERVICE_NAME_PROP_NAME);
			
			// get home directory of current logged in user
			sUserHomeDirectory = System.getProperty("user.home");
			sPropertiesFileFullPath = sUserHomeDirectory + Constants.FILE_SEPARATOR + Constants.PROPERTIES_ROOT_FOLDER + Constants.FILE_SEPARATOR + sCmdLineServiceName + Constants.FILE_SEPARATOR + Constants.PROPERTIES_FILE_NAME; 
			props = new Properties();
			is = new FileInputStream(sPropertiesFileFullPath);
			//InputStream is = PropertyFileReader.class.getResourceAsStream(Constants.PROPERTIES_FILE_NAME);
			props.load(is);
			
			// set service name property in props
			props.setProperty(Constants.PROP_NAME_SERVICE_NAME, sCmdLineServiceName);
			
			return props;
		}
		catch (Exception e)
		{
			props = null;
		}
		
		return props;
	}



	public static String getProperty(String key)
	{
		String str = null;
		try
		{
			str = m_Props.getProperty(key);
		}
		catch (Exception e)
		{
			str = null;;
		}
		
		return str;
	}

	public static String getProperty(String key, String sDefault)
	{
		String str = null;
		try
		{
			str = m_Props.getProperty(key);
		}
		catch (Exception e)
		{
			str = null;
			// str = sDefault;
		}
		
		if (null == str)
		{
			str = sDefault;
		}
		return str;
	}

	public static Boolean getPropertyAsBoolean(String key)
	{
		Boolean bRes = null;
		String sBool = getProperty(key);
		if (sBool != null)
		{
			bRes = new Boolean(sBool.equalsIgnoreCase("true"));
		}

		return bRes;
	}
	

	public static boolean getPropertyAsBoolean(String key, boolean bDefault)
	{
		boolean bRes = bDefault;
		String sBool = getProperty(key, String.valueOf(bDefault));
		if (sBool != null)
		{
			bRes = new Boolean(sBool.equalsIgnoreCase("true")).booleanValue();
		}

		return bRes;
	}
	
	
	
	public static Integer getPropertyAsInt(String property)
	{
		Integer iRes = null;
		try
		{
			iRes =  Integer.parseInt(getProperty(property));
		}
		catch (Exception me)
		{
			//throw me;
			iRes = null;
		}
		
		return iRes;		
	}
	
	public static Long getPropertyAsLong(String property)
	{
		Long lRes = null;
		try
		{
			lRes =  Long.parseLong(getProperty(property));
		}
		catch (Exception e)
		{
			lRes = null;
		}
		
		return lRes;
	}

	public static int getPropertyAsInt(String property, int iDefault)
	{
		int iRes = iDefault;
		try
		{
			iRes =  Integer.parseInt(getProperty(property));
		}
		catch (Exception me)
		{
		}
		
		return iRes;		
	}

	
	public static boolean setProperty(String key, String value)
	{
		boolean bRes = true;
		try
		{
			m_Props.setProperty(key, value);
		}
		catch (Exception e)
		{
			bRes = false;
		}
		
		return bRes;
	}
	
	public static void setPropertyFromParam(String sParamName)
	{
		setProperty(sParamName, ParametersReader.getParameter(sParamName, true));
	}


}