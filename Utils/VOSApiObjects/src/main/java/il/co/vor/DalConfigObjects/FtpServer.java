package il.co.vor.DalConfigObjects;

import javax.xml.bind.annotation.XmlElement;

import il.co.vor.common.VosConfigResources;

public class FtpServer 
{
	private int m_ftpServerId;
	private String m_ftpServerName;
	private String m_ftpServerDescription;
	private int m_ftpServerType;
	private String m_ftpServerRootPath;
	private String m_ftpServerAddressIp;
	private int m_ftpServerAddressPort;
	private String m_ftpServerUserName;
	private String m_ftpServerUserPassword;
	private String m_ftpServerKeyStorePath;
	private int m_ftpServerMaxParallelActions;
	private int m_ftpServerSiteType;
	private int m_serviceId;
	private Site m_site;
	
	@XmlElement(name=VosConfigResources.FTP_SERVERS_PROP_FTP_SERVER_ID_NAME)
	public int getFtpServerId() 
	{
		return m_ftpServerId;
	}
	
	public void setFtpServerId(int ftpServerId) 
	{
		this.m_ftpServerId = ftpServerId;
	}
	
	@XmlElement(name=VosConfigResources.FTP_SERVERS_PROP_FTP_SERVER_NAME_NAME)
	public String getFtpServerName() 
	{
		return m_ftpServerName;
	}
	
	public void setFtpServerName(String ftpServerName) 
	{
		this.m_ftpServerName = ftpServerName;
	}
	
	@XmlElement(name=VosConfigResources.FTP_SERVERS_PROP_FTP_SERVER_DESCRIPTION_NAME)
	public String getFtpServerDescription() 
	{
		return m_ftpServerDescription;
	}
	
	public void setFtpServerDescription(String ftpServerDescription) 
	{
		this.m_ftpServerDescription = ftpServerDescription;
	}
	
	@XmlElement(name=VosConfigResources.FTP_SERVERS_PROP_FTP_SERVER_TYPE_NAME)
	public int getFtpServerType() 
	{
		return m_ftpServerType;
	}
	
	public void setFtpServerType(int ftpServerType) 
	{
		this.m_ftpServerType = ftpServerType;
	}
	
	@XmlElement(name=VosConfigResources.FTP_SERVERS_PROP_FTP_SERVER_ROOT_PATH_NAME)
	public String getftpServerRootPath() 
	{
		return m_ftpServerRootPath;
	}
	
	public void setftpServerRootPath(String ftpServerRootPath) 
	{
		this.m_ftpServerRootPath = ftpServerRootPath;
	}
	
	@XmlElement(name=VosConfigResources.FTP_SERVERS_PROP_FTP_SERVER_ADDRESS_IP_NAME)
	public String getFtpServerAddressIp() 
	{
		return m_ftpServerAddressIp;
	}
	
	public void setFtpServerAddressIp(String ftpServerAddressIp) 
	{
		this.m_ftpServerAddressIp = ftpServerAddressIp;
	}
	
	@XmlElement(name=VosConfigResources.FTP_SERVERS_PROP_FTP_SERVER_ADDRESS_PORT_NAME)
	public int getFtpServerAddressPort() 
	{
		return m_ftpServerAddressPort;
	}
	
	public void setFtpServerAddressPort(int ftpServerAddressPort) 
	{
		this.m_ftpServerAddressPort = ftpServerAddressPort;
	}
	
	@XmlElement(name=VosConfigResources.FTP_SERVERS_PROP_FTP_SERVER_USER_NAME_NAME)
	public String getFtpServerUserName() 
	{
		return m_ftpServerUserName;
	}
	
	public void setFtpServerUserName(String ftpServerUserName) 
	{
		this.m_ftpServerUserName = ftpServerUserName;
	}
	
	@XmlElement(name=VosConfigResources.FTP_SERVERS_PROP_FTP_SERVER_USER_PASSWORD_NAME)
	public String getFtpServerUserPassword() 
	{
		return m_ftpServerUserPassword;
	}
	
	public void setFtpServerUserPassword(String ftpServerUserPassword) 
	{
		this.m_ftpServerUserPassword = ftpServerUserPassword;
	}
	
	@XmlElement(name=VosConfigResources.FTP_SERVERS_PROP_FTP_SERVER_KEY_STORE_PATH_NAME)
	public String getFtpServerKeyStorePath() 
	{
		return m_ftpServerKeyStorePath;
	}
	
	public void setFtpServerKeyStorePath(String ftpServerKeyStorePath) 
	{
		this.m_ftpServerKeyStorePath = ftpServerKeyStorePath;
	}
	
	@XmlElement(name=VosConfigResources.FTP_SERVERS_PROP_FTP_SERVER_MAX_PARALLEL_ACTIONS_NAME)
	public int getFtpServerMaxParallelActions() 
	{
		return m_ftpServerMaxParallelActions;
	}
	
	public void setFtpServerMaxParallelActions(int ftpServerMaxParallelActions) 
	{
		this.m_ftpServerMaxParallelActions = ftpServerMaxParallelActions;
	}
	
	@XmlElement(name=VosConfigResources.FTP_SERVERS_PROP_FTP_SERVER_SITE_TYPE_NAME)
	public int getFtpServerSiteType() 
	{
		return m_ftpServerSiteType;
	}
	
	public void setFtpServerSiteType(int ftpServerSiteType) 
	{
		this.m_ftpServerSiteType = ftpServerSiteType;
	}
	
	@XmlElement(name=VosConfigResources.SERVICES_PROP_SERVICE_ID_NAME)
	public int getServiceId() 
	{
		return m_serviceId;
	}
	
	public void setServiceId(int serviceId) 
	{
		this.m_serviceId = serviceId;
	}
	
	@XmlElement(name=VosConfigResources.SITE_OBJECT_NAME)
	public Site getSite() 
	{
		return m_site;
	}
	
	public void setSite(Site site) 
	{
		this.m_site = site;
	}

}
