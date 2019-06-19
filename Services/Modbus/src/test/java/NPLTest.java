import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;



import il.co.vor.VOSLogger;
import il.co.vor.ApiObjectsCommon.ApiMultiResultWrapper;
import il.co.vor.DalConfigClient.DalConfigClient;
import il.co.vor.DalConfigObjects.Plc;
import il.co.vor.DalConfigObjects.Service;
import il.co.vor.Modbus.ChunkData;
import il.co.vor.Modbus.IPLCIO;
import il.co.vor.Modbus.NPL;
import il.co.vor.Modbus.NPLHelper;
import il.co.vor.Modbus.OperandManager;
import il.co.vor.Modbus.OperandVal;
import il.co.vor.Modbus.PLC;
import il.co.vor.Modbus.PLCModbusIO;
import il.co.vor.common.Enums.RegisterType;
import il.co.vor.common.Constants;
import il.co.vor.common.VosConfigResources;
import il.co.vor.utilities.PropertyFileReader;
import net.wimpi.modbus.ModbusException;
import net.wimpi.modbus.ModbusIOException;
import net.wimpi.modbus.ModbusSlaveException;
import net.wimpi.modbus.io.ModbusTCPTransaction;
import net.wimpi.modbus.msg.ModbusRequest;
import net.wimpi.modbus.msg.ModbusResponse;
import net.wimpi.modbus.msg.WriteMultipleRegistersRequest;
import net.wimpi.modbus.msg.WriteSingleRegisterRequest;
import net.wimpi.modbus.msg.WriteCoilRequest;
import net.wimpi.modbus.net.TCPMasterConnection;
import net.wimpi.modbus.procimg.Register;
import net.wimpi.modbus.procimg.SimpleRegister;
import net.wimpi.modbus.util.ModbusUtil;

public class NPLTest extends NPL {

	private static TCPMasterConnection con = null; // the connection
	private static final Logger logger = VOSLogger.getInstance();
	
	public static void main(String[] args) {
		
		Logger _logger = Logger.getLogger(NPL.class.getName());
		
		// TODO Auto-generated method stub
		JSONObject res = null;
		JSONArray arr = null;
		String IPAddress = "";
		String service_name = "";

		Service service = null;
		Plc plc = null;
		Map<String, String> params = null;

		int port = -1;

		OperandManager operand_manager = null;
		ApiMultiResultWrapper<Plc> amrwrplc = null;
		ApiMultiResultWrapper<Service> amrwrservice = null;
		DalConfigClient dcc = DalConfigClient.getInstance();
		_logger.log(Level.SEVERE, "Start Updating PLC");
		try {

			service_name = PropertyFileReader.getProperty(Constants.PROP_NAME_SERVICE_NAME);//args[0];
			//IPAddress = PropertyFileReader.getProperty(Constants.PROP_NAME_DAL_IP_ADDRESS);//args[1];
			//port = Integer.parseInt(PropertyFileReader.getProperty(Constants.PROP_NAME_DAL_PORT));//args[2]


			amrwrservice = dcc.getServices().getServiceObject(service_name);
			
			//res = Services.getService(service_name);
			if (amrwrservice != null) {
				service =  amrwrservice.getApiData().get(VosConfigResources.API_SERVICES_GET_RESOURCE_NAME).get(0);
				//arr = ((JSONObject) res.get(Constants.JSON_ROOT_DATA_PROP_NAME)).getJSONArray(VosConfigResources.API_SERVICES_GET_RESOURCE_NAME);
				//service = new Service(arr.getJSONObject(0));

				if (service != null) {
					

					//res = Plcs.getServicePlc(service.GetServiceID());
					amrwrplc = dcc.getPlcs().getServicePlcObject(service.getServiceId());
					plc = amrwrplc.getApiData().get(VosConfigResources.PLCS_NAME).get(0);
					
					if (plc != null) {
						//arr = ((JSONObject) res.get(Constants.JSON_ROOT_DATA_PROP_NAME)).getJSONArray(VosConfigResources.PLCS_NAME);
						//plc = new PLC(arr.getJSONObject(0));

						if (plc != null) {
							_logger.log(Level.INFO, String.format("Connect PLC. Service Name: %s PLC: %s",
									service_name, plc.getPlcName()));
							con = GetPLCConnection(plc);
							//operand_manager = OperandManager.init(service.GetServiceID(), plc);
							//if (operand_manager != null) {
								// test
							/*WriteDoubleVal(0,750,plc);
							WriteDoubleVal(4,754.700,plc);
							WriteDoubleVal(8,758.700,plc);
							//WriteDoubleVal(0,750,plc);
							WriteDoubleVal(9,1003.256,plc);
							WriteDoubleVal(50,-14.999,plc);
							WriteCoilVal(8,true,plc);
							WriteCoilVal(10,false,plc);*/
							
							for (int i1=0; i1 < 103; i1++)
							{
								try {
									WriteCoilVal(i1,(i1%2==1?true:false),plc);
								} catch (Exception e) {
									
								}
							}
							
							for (int i1=0; i1<1610; i1=i1+4)
							{
								try {
									WriteDoubleVal(i1,i1,plc);
									/*WriteDoubleVal(22314,22314,plc);
									WriteDoubleVal(22216,22216,plc);
									WriteDoubleVal(22414,22414,plc);
									WriteDoubleVal(22264,22264,plc);
									WriteDoubleVal(20644,20644,plc);*/
								} catch (Exception e) {
									
								}
							}
							
							/*
							try {
								WriteStrVal(2000,"very very long text",plc);
								//WriteStrVal(2000,"טקסט ארוך מאוד !",plc);
							} catch (Exception e) {
								
							}*/
							int i = 5;
							i = i+5;
							//double val = ReadDoubleVal(0,plc,20);
							//val = ReadDoubleVal(8,plc,30);
							//val = ReadDoubleVal(9,plc,40);
							/*for (int i1=60; i1<900; i1=i1+8)
							{
								try {
								WriteDoubleVal(i1,i1,plc);
								} catch (Exception e) {
									
								}
							}*/
							
							/*for (int i1=3261; i1<3312; i1=i1+1)
							{
								try {
									WriteCoilVal(i1,true,plc);
								} catch (Exception e) {
									
								}
							}
							*/
							/*
							for (int i1=860; i1<3253; i1=i1+8)
							{
								try {
								WriteDoubleVal(i1,i1,plc);
								} catch (Exception e) {
									
								}
							}
								*/
							/*} else {
								throw new Exception(
										String.format("Failed to create data structures for Service %s", service_name));
							}*/
						}
						

					} else {
						throw new Exception(String.format("Could not find PLC for Service %s", service_name));
					}
				}

			} else {
				throw new Exception(String.format("Could not find Service %s", service_name));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void printUsage() {
		System.out.format("Usage:%s %s %s %s", "Service Name", "IP", "Port", "Example: NPL2 127.0.0.1 8080");

	}
	
/*	private static double ReadDoubleVal(int ref, Plc plc, int size) {
		ModbusTCPTransaction trans = null;
		ModbusRequest req = null; // the request
		ModbusResponse response = null; // the response

		con = GetPLCConnection(plc);
		trans = new ModbusTCPTransaction(con); // the transaction

		req = NPLHelper.GetModbusRequest(RegisterType.values()[4], ref, size);

		trans.setRequest(req);
		try {
			trans.execute();
		} catch (ModbusIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ModbusSlaveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ModbusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		response = trans.getResponse();

		OperandVal operval = NPLHelper.GetVal(RegisterType.fromInt(4), response, OperandDataType1.fromInt(5));
		double val = operval.GetDoubleVal();
		return val;
	}*/

	private static void WriteDoubleVal(int ref, double value, Plc plc){
		double val = -1;
		byte[] bvals = null;
		
		Register[] rvals = null;
		
		ModbusTCPTransaction trans = null;
		ModbusRequest req = null; // the request
		ModbusResponse response = null; // the response
		//int ref = 0;
		//int value = 0;
		int repeat = 1;
		int unit = 0;
		
		
		//TCPMasterConnection con = null;
		
		bvals = ModbusUtil.doubleToRegisters(value);
		PLCModbusIO register_io = new PLCModbusIO();
	
		rvals = null; ///PLCModbusIO.byteArrayToRegister(bvals);
		con = GetPLCConnection(plc);
		//con = GetPLCConnection(plc);
		req = new WriteMultipleRegistersRequest(ref, rvals);
		
		trans = new ModbusTCPTransaction(con); // the transaction
		trans.setRequest(req);
		
		try {
			
			trans.execute();
		
			response = trans.getResponse();
			//con.close();		
			logger.log(Level.WARNING, String.format("WriteDoubleVal ref: %s value: %s plc: %s", ref,value,plc));
			
		} catch (ModbusIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ModbusSlaveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ModbusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void WriteCoilVal(int ref, boolean value, Plc plc){
	
		ModbusTCPTransaction trans = null;
		ModbusRequest req = null; // the request
		ModbusResponse response = null; // the response
		//int ref = 0;
		//int value = 0;
		int repeat = 1;
		int unit = 0;
		
		
		//TCPMasterConnection con = null;
		if (null == con)
		{
			con = GetPLCConnection(plc);
		}
		//con = GetPLCConnection(plc);
		req = new WriteCoilRequest(ref, value);
		
		trans = new ModbusTCPTransaction(con); // the transaction
		trans.setRequest(req);
		
		try {
			
			trans.execute();
		
			response = trans.getResponse();
			//con.close();		
			logger.log(Level.WARNING, String.format("WriteCoilVal ref: %s value: %s plc: %s", ref,value,plc));
			
		} catch (ModbusIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ModbusSlaveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ModbusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void WriteStrVal(int ref, String value, Plc plc){
		double val = -1;
		byte[] bvals = null;
		
		Register[] rvals = null;
		
		ModbusTCPTransaction trans = null;
		ModbusRequest req = null; // the request
		ModbusResponse response = null; // the response
		//int ref = 0;
		//int value = 0;
		int repeat = 1;
		int unit = 0;
		
		
		//TCPMasterConnection con = null;
		con = GetPLCConnection(plc);
		
		if (value.length() % 2 != 0)
		{
			value = value + " ";
		}
		try {
			bvals = value.getBytes("utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		PLCModbusIO register_io = new PLCModbusIO();
		
		rvals = null; //PLCModbusIO.byteArrayToRegister(bvals);
		
		//con = GetPLCConnection(plc);
		req = new WriteMultipleRegistersRequest(ref, rvals);
		
		trans = new ModbusTCPTransaction(con); // the transaction
		trans.setRequest(req);
		
		try {
			
			trans.execute();
		
			response = trans.getResponse();
			//con.close();		
			logger.log(Level.WARNING, String.format("WriteStrVal ref: %s value: %s plc: %s", ref,value,plc));
			
		} catch (ModbusIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ModbusSlaveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ModbusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void WriteVal(int ref, int value, Plc plc){
		double val = -1;
		byte[] bvals = null;
		ModbusTCPTransaction trans = null;
		ModbusRequest req = null; // the request
		ModbusResponse response = null; // the response
		//int ref = 0;
		//int value = 0;
		int repeat = 1;
		int unit = 0;
		
		con = GetPLCConnection(plc);
		
		req = new WriteSingleRegisterRequest(ref,
				new SimpleRegister(value));
		trans = new ModbusTCPTransaction(con); // the transaction
		trans.setRequest(req);
		try {
			trans.execute();
		
			response = trans.getResponse();
			//con.close();		
			logger.log(Level.WARNING, String.format("WriteVal ref: %s value: %s plc: %s", ref,value,plc));
		} catch (ModbusIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ModbusSlaveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ModbusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static TCPMasterConnection GetPLCConnection(Plc plc) {
		

		if (con == null) {
		
			try {
				InetAddress addr = InetAddress.getByName(plc.getPlcAddressIp());

				con = new TCPMasterConnection(addr);
				con.setPort(plc.getPlcAddressPort());

				con.connect();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return con;
	}
}
