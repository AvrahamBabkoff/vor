package il.co.vor.ApiObjectsCommon;

import javax.xml.bind.annotation.XmlElement;

import il.co.vor.common.VosGenResources;

public class LoggerInfo 
{
	private int m_port;
	private String m_level;
	
	@XmlElement(name=VosGenResources.LOG_INFO_PORT_NAME)
	public int getPort () 
	{
		return m_port;
	}

	public void setPort (int _port) 
	{
		m_port = _port;
	}

	@XmlElement(name=VosGenResources.LOG_INFO_LEVEL_NAME)
	public String getLevel () 
	{
		return m_level;
	}
	
	public void setLevel (String _level) 
	{
		m_level = _level;
	}


}
