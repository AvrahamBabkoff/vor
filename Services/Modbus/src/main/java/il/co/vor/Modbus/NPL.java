package il.co.vor.Modbus;

import java.util.logging.Level;
import java.util.logging.Logger;

import il.co.vor.VOSLogger;
import il.co.vor.ApiObjectsCommon.ApiMultiResultWrapper;
import il.co.vor.DalConfigClient.DalConfigClient;
import il.co.vor.DalConfigObjects.Plc;
import il.co.vor.DalConfigObjects.Service;
import il.co.vor.common.Constants;
import il.co.vor.common.VosConfigResources;
import il.co.vor.utilities.PropertyFileReader;

public class NPL {

	@SuppressWarnings("unused")
	private static final Logger logger = VOSLogger.getInstance();
	
	public static void main(String[] args) {

		Logger _logger = Logger.getLogger(NPL.class.getName());

		//String sIPAddress = "";
		String sServiceName = "";

		Service service = null;

		ApiMultiResultWrapper<Plc> amrwrplc = null;
		ApiMultiResultWrapper<Service> amrwrservice = null;
		DalConfigClient dcc = DalConfigClient.getInstance();
		Plc plc = null;

		//int iPort = -1;

		OperandManager operand_manager = null;
		boolean bOK = false;

		// If there is a fatal error, will try again from the start, forever
		while (!bOK)
		{
			try {
				
				try{
					// init
					sServiceName = PropertyFileReader.getProperty(Constants.PROP_NAME_SERVICE_NAME);//args[0];
					//sIPAddress = PropertyFileReader.getProperty(Constants.PROP_NAME_DAL_IP_ADDRESS);//args[1];
					
					//iPort = Integer.parseInt(PropertyFileReader.getProperty(Constants.PROP_NAME_DAL_PORT));//args[2]
					//VOSDalConfigMain.initVOSDalConfig();
					bOK = true;
					_logger.log(Level.INFO, String.format("NPL Started. service_name: %s", sServiceName));
				} catch (Exception e) {
					bOK = false;
					_logger.log(Level.SEVERE, String.format("Cannot connect to VOSDALConfig, Check PropertyFileReader. Exception: %s", e.getMessage()));
					e.printStackTrace();
				}

				if (bOK) {
					amrwrservice = dcc.getServices().getServiceObject(sServiceName);
					
					if (amrwrservice != null) { // read service data
						service =  amrwrservice.getApiData().get(VosConfigResources.API_SERVICES_GET_RESOURCE_NAME).get(0);
		
						if (service != null) {
							
							amrwrplc = dcc.getPlcs().getServicePlcObject(service.getServiceId());
							
							if (amrwrplc != null) { // read plc data
								plc = amrwrplc.getApiData().get(VosConfigResources.PLCS_NAME).get(0);
								
								if (plc != null) {
									// activate operand manager
									operand_manager = OperandManager.init(service.getServiceId(), plc, service.getServiceAddressPort());
									if (operand_manager != null) {
										
									} else {
										bOK = false;
										_logger.log(Level.SEVERE, String.format("Failed to create data structures for Service %s", sServiceName));
									}
								}
								else {
									bOK = false;
									_logger.log(Level.SEVERE, String.format("Could not find PLC for Service %s", sServiceName));
								}
		
							} else {
								bOK = false;
								_logger.log(Level.SEVERE, String.format("Could not find PLCs data for Service %s", sServiceName));
							}
						}
						else {
							bOK = false;
							_logger.log(Level.SEVERE, String.format("Could not find Service %s", sServiceName));
						}
		
					} else {
						bOK = false;
						_logger.log(Level.SEVERE, String.format("Could not find Services data %s", sServiceName));
					}
				}
	
			} catch (Exception e) {
				bOK = false;
				_logger.log(Level.SEVERE, String.format("Exception: %s", e.getMessage()));
				e.printStackTrace();
			}
		}
	}

	private static void printUsage() {
		System.out.format("Usage:%s %s %s %s", "Service Name", "IP", "Port", "Example: NPL2 127.0.0.1 8080");

	}
}
