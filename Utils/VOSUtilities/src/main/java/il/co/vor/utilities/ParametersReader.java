package il.co.vor.utilities;

import java.util.HashMap;

public class ParametersReader 
{
	private static HashMap<String, String> m_Props = null;

	public static String getParameter(String key, boolean bEmptyStringIfNull)
	{
		String str = null;
		try
		{
			if (null != m_Props)
			{
				str = m_Props.get(key);
				if ((null == str) && bEmptyStringIfNull)
				{
					str = "";
				}
			}
		}
		catch (Exception e)
		{
			str = null;
		}
		
		return str;
	}

	public static String getParameter(String key, String sDefault)
	{
		String str = null;
		try
		{
			if (null != m_Props)
			{
				str = m_Props.get(key);
			}
		}
		catch (Exception e)
		{
			str = null;
		}				
		if (null == str)
		{
			str = sDefault;
		}
		return str;
	}
	
	public static Integer getParameterAsInt(String property)
	{
		Integer iRes = null;
		try
		{
			iRes =  Integer.parseInt(getParameter(property, true));
		}
		catch (Exception me)
		{
			//throw me;
			iRes = null;
		}
		
		return iRes;		
	}

	public static int getParameterAsInt(String property, int iDefault)
	{
		int iRes = iDefault;
		try
		{
			iRes =  Integer.parseInt(getParameter(property, String.valueOf(iDefault)));
		}
		catch (Exception me)
		{
		}
		
		return iRes;		
	}
	
	public static void setProps(HashMap<String, String> props) 
	{
		ParametersReader.m_Props = props;
	}
}
