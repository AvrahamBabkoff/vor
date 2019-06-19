package il.co.vor.DalConfigObjects;

//import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import il.co.vor.common.VosConfigResources;

@XmlRootElement
public class Service //implements Serializable
{
    private int m_serviceType;
    private String m_serviceDescription;
    private String m_serviceName;
    private int m_serviceId;
    private String m_serviceAddressIp;
    private int m_siteId;
    private int m_serviceAddressPort;

    
	@XmlElement(name=VosConfigResources.SERVICES_PROP_SERVICE_TYPE_NAME)
    public int getServiceType ()
    {
		return m_serviceType;
    }

	public void setServiceType (int serviceType) 
	{
		m_serviceType = serviceType;
	}
	
	@XmlElement(name=VosConfigResources.SERVICES_PROP_SERVICE_DESCRIPTION_NAME)
    public String getServiceDescription ()
    {
		return m_serviceDescription;
    }
	
	public void setServiceDescription (String serviceDescription)
	{
		m_serviceDescription = serviceDescription;
	}

	@XmlElement(name=VosConfigResources.SERVICES_PROP_SERVICE_NAME_NAME)
    public String getServiceName ()
    {
		return m_serviceName;
    }
	
	public void setServiceName (String serviceName)
	{
		m_serviceName = serviceName;
	}

	@XmlElement(name=VosConfigResources.SERVICES_PROP_SERVICE_ID_NAME)
    public int getServiceId ()
    {
		return m_serviceId;
    }
	
	public void setServiceId (int serviceId)
	{
		m_serviceId = serviceId;
	}

	@XmlElement(name=VosConfigResources.SERVICES_PROP_SERVICE_ADDRESS_IP_NAME)
    public String getServiceAddressIp ()
    {
		return m_serviceAddressIp;
    }
	
	public void setServiceAddressIp (String serviceAddressIp)
	{
		m_serviceAddressIp = serviceAddressIp;
	}

	@XmlElement(name=VosConfigResources.SITES_PROP_SITE_ID_NAME)
    public int getSiteId ()
    {
		return m_siteId;
    }

	public void setSiteId (int siteId)
	{
		m_siteId = siteId;
	}
	
	@XmlElement(name=VosConfigResources.SERVICES_PROP_SERVICE_ADDRESS_PORT_NAME)
    public int getServiceAddressPort ()
    {
		return m_serviceAddressPort;
    }
	
	public void setServiceAddressPort (int serviceAddressPort)
	{
		m_serviceAddressPort = serviceAddressPort;
	}
    
 /*
	@XmlElement(name=VosConfigResources.SERVICES_PROP_SERVICE_TYPE_NAME)
    public int serviceType;

	@XmlElement(name=VosConfigResources.SERVICES_PROP_SERVICE_DESCRIPTION_NAME)
    public String serviceDescription;

	@XmlElement(name=VosConfigResources.SERVICES_PROP_SERVICE_NAME_NAME)
    public String serviceName;

	@XmlElement(name=VosConfigResources.SERVICES_PROP_SERVICE_ID_NAME)
    public int serviceId;

	@XmlElement(name=VosConfigResources.SERVICES_PROP_SERVICE_ADDRESS_IP_NAME)
    public String serviceAddressIp;

	@XmlElement(name=VosConfigResources.SITES_PROP_SITE_ID_NAME)
    public int siteId;

	@XmlElement(name=VosConfigResources.SERVICES_PROP_SERVICE_ADDRESS_PORT_NAME)
    public int serviceAddressPort;
*/    
}
