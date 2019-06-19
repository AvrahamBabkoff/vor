package il.co.vor.DalConfigObjects;

import javax.xml.bind.annotation.XmlElement;

import il.co.vor.common.VosConfigResources;

public class Plc 
{
	private String m_plcName;
	private int m_plcAddressPort;
	private String m_plcAddressIp;
	private int m_nplPlcId;
	private int m_serviceId;
	private String m_plcDescription;
	private int m_siteId;
	private int m_plcProtocolType;
	private int m_plcId;
	private int m_plcAddressSlaveId;
	
	@XmlElement(name=VosConfigResources.PLCS_PROP_PLC_NAME_NAME)
	public String getPlcName() 
	{
		return m_plcName;
	}
	
	public void setPlcName(String plcName) 
	{
		this.m_plcName = plcName;
	}
	
	@XmlElement(name=VosConfigResources.PLCS_PROP_PLC_ADDRESS_PORT_NAME)
	public int getPlcAddressPort() 
	{
		return m_plcAddressPort;
	}
	
	public void setPlcAddressPort(int plcAddressPort) 
	{
		this.m_plcAddressPort = plcAddressPort;
	}
	
	@XmlElement(name=VosConfigResources.PLCS_PROP_PLC_ADDRESS_IP_NAME)
	public String getPlcAddressIp() 
	{
		return m_plcAddressIp;
	}
	
	public void setPlcAddressIp(String plcAddressIp) 
	{
		this.m_plcAddressIp = plcAddressIp;
	}
	
	@XmlElement(name=VosConfigResources.NPL_PLC_PROP_NPL_PLC_ID_NAME)
	public int getNplPlcId() 
	{
		return m_nplPlcId;
	}
	
	public void setNplPlcId(int nplPlcId) 
	{
		this.m_nplPlcId = nplPlcId;
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
	
	@XmlElement(name=VosConfigResources.PLCS_PROP_PLC_DESCRIPTION_NAME)
	public String getPlcDescription() 
	{
		return m_plcDescription;
	}
	
	public void setPlcDescription(String plcDescription) 
	{
		this.m_plcDescription = plcDescription;
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
	
	@XmlElement(name=VosConfigResources.PLCS_PROP_PLC_PROTOCOL_TYPE_NAME)
	public int getPlcProtocolType() 
	{
		return m_plcProtocolType;
	}
	
	public void setPlcProtocolType(int plcProtocolType) 
	{
		this.m_plcProtocolType = plcProtocolType;
	}
	
	
	@XmlElement(name=VosConfigResources.PLCS_PROP_PLC_ID_NAME)
	public int getPlcId() 
	{
		return m_plcId;
	}
	
	public void setPlcId(int plcId) 
	{
		this.m_plcId = plcId;
	}
	
	@XmlElement(name=VosConfigResources.PLCS_PROP_PLC_ADDRESS_SLAVE_ID_NAME)
	public int getPlcAddressSlaveId() 
	{
		return m_plcAddressSlaveId;
	}
	
	public void setPlcAddressSlaveId(int plcAddressSlaveId) 
	{
		this.m_plcAddressSlaveId = plcAddressSlaveId;
	}		
}
