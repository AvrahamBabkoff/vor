package il.co.vor.Modbus;
import org.json.JSONObject;

import il.co.vor.common.VosConfigResources;

public class PLC {
	
	private int plc_id = -1;
	private String plc_name = "";
	private String plc_description = "";
	private int plc_protocol_type = -1;
	private String plc_address_ip = "";
	private int plc_address_port = -1;
	private int plc_address_slave_id = -1;
	private int site_id = -1;
	
	public PLC(int plcID, String plcName, String plcDescription, int plcProtocolType, String plcAddressIP, int plcAddressPort, int plcAddressSlaveID, int siteID){
		plc_id = plcID;
		plc_name = plcName;
		plc_description = plcDescription;
		plc_protocol_type = plcProtocolType;
		plc_address_ip = plcAddressIP;
		plc_address_port = plcAddressPort;
		plc_address_slave_id = plcAddressSlaveID;
		site_id = siteID;
	}
	
	public PLC(JSONObject jsono)
	{
	     this(jsono.getInt(VosConfigResources.PLCS_PROP_PLC_ID_NAME),
	    		 jsono.getString(VosConfigResources.PLCS_PROP_PLC_NAME_NAME),jsono.getString(VosConfigResources.PLCS_PROP_PLC_DESCRIPTION_NAME),
	    		 jsono.getInt(VosConfigResources.PLCS_PROP_PLC_PROTOCOL_TYPE_NAME),jsono.getString(VosConfigResources.PLCS_PROP_PLC_ADDRESS_IP_NAME),
	    		 jsono.getInt(VosConfigResources.PLCS_PROP_PLC_ADDRESS_PORT_NAME),jsono.getInt(VosConfigResources.PLCS_PROP_PLC_ADDRESS_SLAVE_ID_NAME),jsono.getInt(VosConfigResources.SITES_PROP_SITE_ID_NAME));
	}
	
	public int GetPlcID() {
		return plc_id;
	}
	public void SetPlcID(int plc_id) {
		this.plc_id = plc_id;
	}
	public String GetPlcName() {
		return plc_name;
	}
	public void SetPlcName(String plc_name) {
		this.plc_name = plc_name;
	}
	public String GetPlcDescription() {
		return plc_description;
	}
	public void SetPlcDescription(String plc_description) {
		this.plc_description = plc_description;
	}
	public int GetPlcProtocolType() {
		return plc_protocol_type;
	}
	public void SetPlcProtocolType(int plc_protocol_type) {
		this.plc_protocol_type = plc_protocol_type;
	}
	public String GetPlcAddressIP() {
		return plc_address_ip;
	}
	public void SetPlcAddressIP(String plc_address_ip) {
		this.plc_address_ip = plc_address_ip;
	}
	public int GetPlcAddressPort() {
		return plc_address_port;
	}
	public void SetPlcAddressPort(int plc_address_port) {
		this.plc_address_port = plc_address_port;
	}
	public int GetPlcAddressSlaveID() {
		return plc_address_slave_id;
	}
	public void SetPlcAddressSlaveID(int plc_address_slave_id) {
		this.plc_address_slave_id = plc_address_slave_id;
	}
	public int GetSiteID() {
		return site_id;
	}
	public void SetSiteID(int site_id) {
		this.site_id = site_id;
	}
	
}
