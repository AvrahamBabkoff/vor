package il.co.vor.VOSDBConnection;

import java.io.InputStream;
import java.util.Properties;

import il.co.vor.common.Constants;
import il.co.vor.utilities.PropertyFileReader;

public class SQLStatements 
{
	//private final static Properties  m_Props = loadSqlStatements ();
	private static Properties  m_Props = null;
	
	public static void init ()
	{
		m_Props = loadSqlStatements();
	}
	
	private static Properties loadSqlStatements()
	{
		Properties props = null;
		String sFileName = null;
		try
		{
			props = new Properties();
			//PropertyFileReader.getProperty(Constants.PROP_NAME_DB_SQL_Startements_Properties_File_Name)
			sFileName =PropertyFileReader.getProperty(Constants.PROP_NAME_DB_SQL_Startements_Properties_File_Name);
			if (null != sFileName)
			{
				InputStream is = PropertyFileReader.class.getResourceAsStream(sFileName);
				props.load(is);
			}			
			return props;
		}
		catch (Exception e)
		{
			props = null;
		}
		
		return props;
	}
	
	public static String getSqlStatement(String key)
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
}
