package il.co.vor.DalConfigObjects;

import javax.xml.bind.annotation.XmlElement;

import il.co.vor.common.VosConfigResources;

public class Site {
	
	private int m_siteId;
	private String m_siteName;
	private String m_siteDescription;	
	
	
	@XmlElement(name=VosConfigResources.SITES_PROP_SITE_NAME_NAME)
	public String getSiteName() 
	{
		return m_siteName;
	}
	
	public void setSiteName(String siteName) 
	{
		this.m_siteName = siteName;
	}	
	
	@XmlElement(name=VosConfigResources.SITES_PROP_SITE_DESCRIPTION_NAME)
	public String getSiteDescription() 
	{
		return m_siteDescription;
	}
	
	public void setSiteDescription(String siteDescription) 
	{
		this.m_siteDescription = siteDescription;
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

}
