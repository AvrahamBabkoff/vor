package il.co.vor.Modbus;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import il.co.vor.DalConfigObjects.Plc;
import net.wimpi.modbus.ModbusException;
import net.wimpi.modbus.ModbusIOException;
import net.wimpi.modbus.ModbusSlaveException;
import net.wimpi.modbus.io.ModbusTCPTransaction;
import net.wimpi.modbus.msg.ModbusRequest;
import net.wimpi.modbus.msg.ModbusResponse;
import net.wimpi.modbus.msg.ReadCoilsRequest;
import net.wimpi.modbus.msg.ReadCoilsResponse;
import net.wimpi.modbus.msg.ReadInputDiscretesResponse;
import net.wimpi.modbus.msg.ReadInputRegistersRequest;
import net.wimpi.modbus.msg.ReadInputRegistersResponse;
import net.wimpi.modbus.msg.ReadMultipleRegistersRequest;
import net.wimpi.modbus.msg.ReadMultipleRegistersResponse;
import net.wimpi.modbus.msg.WriteCoilRequest;
import net.wimpi.modbus.msg.WriteMultipleRegistersRequest;
import net.wimpi.modbus.net.TCPMasterConnection;
import net.wimpi.modbus.procimg.Register;
import net.wimpi.modbus.procimg.SimpleRegister;
import net.wimpi.modbus.util.BitVector;
import net.wimpi.modbus.util.ModbusUtil;
import il.co.vor.common.Enums;
import il.co.vor.common.Enums.RegisterType;

import il.co.vor.common.Enums.OperandDataType;

public class PLCModbusIO implements IPLCIO {
	
	private Plc m_plc = null;
	private TCPMasterConnection m_con = null; // the connection
	private Logger _logger = Logger.getLogger(OperandManager.class.getName());
	
	private  ModbusRequest GetModbusReadRequest(RegisterType register_type, int address, int size) {
		ModbusRequest req = null;

		try {
			switch (register_type) {
			case MODBUS_DISCRETE_INPUT:
				req = new ReadMultipleRegistersRequest(address, size);
				break;
			case MODBUS_COIL:
				req = new ReadCoilsRequest(address, size);
				break;
			case MODBUS_INPUT_REGISTER:
				req = new ReadInputRegistersRequest(address, size);
				break;
			case MODBUS_HOLDING_REGISTER:
				req = new ReadMultipleRegistersRequest(address, size);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			_logger.log(Level.SEVERE,
					String.format("Cannot create modbus request. Exception: %s", e.getMessage()));
			e.printStackTrace();
		}

		return req;
	}
	
	private  ModbusRequest GetModbusWriteRequest(ModbusChunkVals mcvals, String val, int address, OperandDataType data_type) {
		ModbusRequest req = null;
		RegisterType rt = mcvals.GetRegisterType();
		
		try {
			switch (rt) {
				case MODBUS_DISCRETE_INPUT:
					req = new WriteMultipleRegistersRequest(address, ((ChunkRegisterVals)mcvals).GetConvertedVal(val, data_type));
					break;
				case MODBUS_COIL:
					req = new WriteCoilRequest(address, ((ChunkCoilVals)mcvals).GetConvertedVal(val));
					break;
				case MODBUS_INPUT_REGISTER:
					req = new WriteCoilRequest(address, ((ChunkCoilVals)mcvals).GetConvertedVal(val));
					break;
				case MODBUS_HOLDING_REGISTER:
					req = new WriteMultipleRegistersRequest(address, ((ChunkRegisterVals)mcvals).GetConvertedVal(val, data_type));
					break;
				default:
					break;
			}
		} catch (Exception e) {
			_logger.log(Level.SEVERE,
					String.format("Cannot create modbus write request. Exception: %s", e.getMessage()));
			e.printStackTrace();
		}

		return req;
	}


	@Override
	public boolean WriteOperandVal(IChunkVals cvals, String operand_val, String address, OperandDataType data_type) {
		boolean ret = false;
		ModbusChunkVals mcvals = null;
		RegisterType rt = RegisterType.UNKNOWN;
		
		ModbusRequest req = null;
		ModbusTCPTransaction trans = null;
		ModbusResponse response = null; 
		
		long lStart = 0;
		long lEnd = 0;

		lStart = System.currentTimeMillis();
		_logger.log(Level.INFO, "PLCModbus7IO WriteOperandVal Start");

		try {
			trans = new ModbusTCPTransaction(m_con);
			req = GetModbusWriteRequest((ModbusChunkVals) cvals, operand_val, Integer.parseInt(address), data_type);
			//mcvals = (ModbusChunkVals) cvals.GetValsData();
			trans.setRequest(req);
			trans.execute();
			
			response = trans.getResponse();
			ret = true;
			_logger.log(Level.INFO, String.format("WriteOperandVal ref: %s value: %s data_type: %s", address,operand_val,String.valueOf(data_type)));
			
		} catch (Exception e) {
			_logger.log(Level.SEVERE,
					String.format("Cannot WriteOperandVal. ref: %s value: %s data_type: %s Exception: %s", address,operand_val,String.valueOf(data_type), e.getMessage()));
			e.printStackTrace();
		}
		
		_logger.log(Level.INFO, "PLCSModbusIO WriteOperandVal End");
		lEnd = System.currentTimeMillis();
		_logger.log(Level.INFO, String.format("PLCModbusIO WriteOperandVal Duration: %d milliseconds", (lEnd - lStart)));

		return ret;
	}
	
	
	
	
	@Override
	public void Init(Plc _plc) {
		m_plc = _plc;
	
	}
	
	@Override
	public boolean CheckConnection(){
		
		boolean ret = false;
		
		InetAddress addr = null;
		
		try {
			if (m_con == null) {
				addr = InetAddress.getByName(m_plc.getPlcAddressIp());

				m_con = new TCPMasterConnection(addr);
				m_con.setPort(m_plc.getPlcAddressPort());
			}

			if ((m_con != null) && (!m_con.isConnected())) {
				m_con.connect();
			}
			
			ret = ((m_con != null) && (m_con.isConnected()));
			
		} catch (Exception e) {
			_logger.log(Level.SEVERE, String.format("Exception: %s", e.getMessage()));
			e.printStackTrace();

		}

		return ret;
	}


	@Override
	public boolean ReadChunk(IChunkVals cvals) {
		boolean ret = false;
		ModbusRequest req = null;
		ModbusResponse response = null;
		ChunkWrapper chunk = null;

		ModbusTCPTransaction trans = null;
		Object vals = null; // BitVector or Register[]

		RegisterType register_type = ((ModbusChunkVals)cvals).GetRegisterType();
		
		try {
			
			trans = new ModbusTCPTransaction(m_con);
			
			
			
			req = GetModbusReadRequest(register_type, ((ModbusChunkVals)cvals).GetStartAddress(), ((ModbusChunkVals)cvals).GetCount());
	
			trans.setRequest(req);
			trans.execute();
			
			response = trans.getResponse();
	
			if (response != null) {
				try {
					switch (register_type) {
					case MODBUS_DISCRETE_INPUT:
						vals = ((ReadInputDiscretesResponse) response).getDiscretes();
						break;
					case MODBUS_COIL:
						vals = ((ReadCoilsResponse) response).getCoils();
						break;
					case MODBUS_INPUT_REGISTER:
						vals = ((ReadInputRegistersResponse) response).getRegisters();
						break;
					case MODBUS_HOLDING_REGISTER:
						vals = ((ReadMultipleRegistersResponse) response).getRegisters();
					default:
						break;
					}
					
					
				} catch (Exception e) {
					_logger.log(Level.SEVERE, String.format("Exception: %s", e.getMessage()));
					e.printStackTrace();
				}
			}
		} catch (ModbusIOException e1) {
			ret = false;
			_logger.log(Level.SEVERE, String.format("Failed to read chunk data. address: %s type: %s size: %s. ModbusIOException Exception: %s",
				String.valueOf(((ModbusChunkVals)cvals).GetStartAddress()),
				String.valueOf(register_type), String.valueOf(((ModbusChunkVals)cvals).GetCount()), e1.getMessage()));
			e1.printStackTrace();
		} catch (ModbusSlaveException e1) {
			_logger.log(Level.SEVERE, String.format("Failed to read chunk data. address: %s type: %s size: %s. ModbusSlaveException Exception: %s",
					String.valueOf(((ModbusChunkVals)cvals).GetStartAddress()),
					String.valueOf(register_type), String.valueOf(((ModbusChunkVals)cvals).GetCount()), e1.getMessage()));
			e1.printStackTrace();
		} catch (ModbusException e1) {
			_logger.log(Level.SEVERE, String.format("Failed to read chunk data. address: %s type: %s size: %s. ModbusException Exception: %s",
					String.valueOf(((ModbusChunkVals)cvals).GetStartAddress()),
					String.valueOf(register_type), String.valueOf(((ModbusChunkVals)cvals).GetCount()), e1.getMessage()));
			e1.printStackTrace();
		}
		
		if (vals != null)
		{
			ret = true;
			cvals.SetValsData(vals);
		}
		
		return ret;
	}


	@Override
	public IChunkVals CreateChunkVals(String startAddress, int count, RegisterType register_type) {

		IChunkVals chunk_vals = null;

		try {
			switch (register_type) {
			case MODBUS_DISCRETE_INPUT:
			case MODBUS_COIL:
				chunk_vals = new ChunkCoilVals();
				break;
			case MODBUS_INPUT_REGISTER:
			case MODBUS_HOLDING_REGISTER:
				chunk_vals = new ChunkRegisterVals();
				break;

			default:
				chunk_vals = null;
			}

			if (chunk_vals != null) {
				chunk_vals.Init(startAddress, count, register_type);
			}
		} catch (Exception e) {
			chunk_vals = null;
			_logger.log(Level.SEVERE, String.format("Failed to CreateChunkVals. startAddress=%s count=%s register_type=%s",
					startAddress,String.valueOf(count),String.valueOf(register_type)));
			_logger.log(Level.SEVERE, String.format("Exception: %s", e.getMessage()));

		}
		return chunk_vals;
	}







	@Override
	public void Shutdown() {
		_logger.log(Level.WARNING,"Close PLC Connection.");
		try {
			if ((m_con != null) && (m_con.isConnected()))
			{
				m_con.close();
			}
		} catch (Exception e) {
			_logger.log(Level.SEVERE, String.format("Exception: %s", e.getMessage()));

		}
		m_con = null;
		
	}


}
