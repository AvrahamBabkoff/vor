package il.co.vor.DalConfigObjects;

import javax.xml.bind.annotation.XmlElement;

import il.co.vor.common.VosConfigResources;

public class Parameter 
{
	private int m_serviceId;
	private int m_siteId;
	private String m_parameterValue;
	private String m_parameterName;
	private String m_parameterDescription;
	private int m_parameterId;
	
	@XmlElement(name=VosConfigResources.SERVICES_PROP_SERVICE_ID_NAME)
	public int getServiceId() 
	{
		return m_serviceId;
	}
	
	public void setServiceId(int serviceId) 
	{
		this.m_serviceId = serviceId;
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
	
	@XmlElement(name=VosConfigResources.PARAMETERS_PROP_PARAMETER_VALUE_NAME)
	public String getParameterValue() 
	{
		return m_parameterValue;
	}
	
	public void setParameterValue(String parameterValue) 
	{
		this.m_parameterValue = parameterValue;
	}
	
	@XmlElement(name=VosConfigResources.PARAMETERS_PROP_PARAMETER_NAME_NAME)
	public String getParameterName() 
	{
		return m_parameterName;
	}
	
	public void setParameterName(String parameterName) 
	{
		this.m_parameterName = parameterName;
	}
	
	@XmlElement(name=VosConfigResources.PARAMETERS_PROP_PARAMETER_DESCRIPTION_NAME)
	public String getParameterDescription() 
	{
		return m_parameterDescription;
	}
	
	public void setParameterDescription(String parameterDescription) 
	{
		this.m_parameterDescription = parameterDescription;
	}
	
	@XmlElement(name=VosConfigResources.PARAMETERS_PROP_PARAMETER_ID_NAME)
	public int getParameterId() 
	{
		return m_parameterId;
	}
	
	public void setParameterId(int parameterId) 
	{
		this.m_parameterId = parameterId;
	}		
}
