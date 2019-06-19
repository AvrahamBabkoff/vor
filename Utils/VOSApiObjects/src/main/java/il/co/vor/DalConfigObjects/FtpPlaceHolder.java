package il.co.vor.DalConfigObjects;

import javax.xml.bind.annotation.XmlElement;

import il.co.vor.common.VosConfigResources;
import il.co.vor.common.VosDataResources;

public class FtpPlaceHolder 
{
	private int m_ftpPlaceHolderId;
	private int m_ftpFileType;
	private int m_siteId;
	private String m_ftpPlaceHolderFolder;
	
	@XmlElement(name=VosConfigResources.FTP_PLACE_HOLDERS_PROP_FTP_PLACE_HOLDER_ID_NAME)
	public int getFtpPlaceHolderId() 
	{
		return m_ftpPlaceHolderId;
	}
	
	public void setFtpPlaceHolderId(int ftpPlaceHolderId) 
	{
		this.m_ftpPlaceHolderId = ftpPlaceHolderId;
	}
	
	@XmlElement(name=VosDataResources.FTP_FILES_PROP_FTP_FILE_TYPE_NAME)
	public int getFtpFileType() 
	{
		return m_ftpFileType;
	}
	
	public void setFtpFileType(int ftpFileType) 
	{
		this.m_ftpFileType = ftpFileType;
	}
	
	@XmlElement(name=VosConfigResources.SITES_PROP_SITE_ID_NAME)
	public int getSiteId() 
	{
		return m_siteId;
	}
	
	public void setSiteId(int siteId) 
	{
		this.m_siteId = siteId;
	}
	
	@XmlElement(name=VosConfigResources.FTP_PLACE_HOLDERS_PROP_FTP_PLACE_HOLDER_FOLDER_NAME)
	public String getFtpPlaceHolderFolder() 
	{
		return m_ftpPlaceHolderFolder;
	}
	
	public void setFtpPlaceHolderFolder(String m_ftpPlaceHolderFolder) 
	{
		this.m_ftpPlaceHolderFolder = m_ftpPlaceHolderFolder;
	}

}
