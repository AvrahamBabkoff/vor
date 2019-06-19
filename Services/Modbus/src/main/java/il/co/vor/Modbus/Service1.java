package il.co.vor.Modbus;
import org.json.JSONObject;

import il.co.vor.common.VosConfigResources;

public class Service1 {
	
	private int service_type;
	private String service_description;
	private String service_name;
	private int service_id;
	private String service_address_ip;
	private int site_id;
	private int service_address_port;
	
	public Service1(String serviceName, int serviceID, String serviceDescription, String serviceAddressIP, int siteID, int serviceAddressPort,int serviceType){

		service_description = serviceDescription;
		service_name = serviceName;
		service_id = serviceID;
		service_address_ip = serviceAddressIP;
		site_id = siteID;
		service_address_port = serviceAddressPort;
		service_type = serviceType;
	}
	
	public Service1(JSONObject jsono)
	{
	     this(jsono.getString(VosConfigResources.SERVICES_PROP_SERVICE_NAME_NAME),jsono.getInt(VosConfigResources.SERVICES_PROP_SERVICE_ID_NAME),
	    		 jsono.getString(VosConfigResources.SERVICES_PROP_SERVICE_DESCRIPTION_NAME),jsono.getString(VosConfigResources.SERVICES_PROP_SERVICE_ADDRESS_IP_NAME),
	    		 jsono.getInt(VosConfigResources.SITES_PROP_SITE_ID_NAME),jsono.getInt(VosConfigResources.SERVICES_PROP_SERVICE_ADDRESS_PORT_NAME),jsono.getInt(VosConfigResources.SERVICES_PROP_SERVICE_TYPE_NAME));
	}
	
	public int GetServiceType() {
		return service_type;
	}
	public void SetServiceType(int service_type) {
		this.service_type = service_type;
	}
	public String GetServiceDescription() {
		return service_description;
	}
	public void SetServiceDescription(String service_description) {
		this.service_description = service_description;
	}
	public String GetServiceName() {
		return service_name;
	}
	public void SetServiceName(String service_name) {
		this.service_name = service_name;
	}
	public int GetServiceID() {
		return service_id;
	}
	public void SetServiceID(int service_id) {
		this.service_id = service_id;
	}
	public String GetServiceAddressIP() {
		return service_address_ip;
	}
	public void SetServiceAddressIP(String service_address_ip) {
		this.service_address_ip = service_address_ip;
	}
	public int GetSiteID() {
		return site_id;
	}
	public void SetSiteID(int site_id) {
		this.site_id = site_id;
	}
	public int GetServiceAddressPort() {
		return service_address_port;
	}
	public void SetServiceAddressPort(int service_address_port) {
		this.service_address_port = service_address_port;
	}
	
}
