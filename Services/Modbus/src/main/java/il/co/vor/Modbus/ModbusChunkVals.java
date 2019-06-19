package il.co.vor.Modbus;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import il.co.vor.common.Enums.OperandDataType;
import il.co.vor.common.Enums.RegisterType;
import net.wimpi.modbus.procimg.Register;
import net.wimpi.modbus.util.BitVector;
import net.wimpi.modbus.util.ModbusUtil;

public abstract class ModbusChunkVals implements IChunkVals {

	private static Logger _logger = Logger.getLogger(OperandManager.class.getName());
	
	private RegisterType m_register_type = RegisterType.UNKNOWN;
	private int m_start_address = -1;
	private int m_count = -1;
	
	public RegisterType GetRegisterType()
	{
		return m_register_type;
	}
	
	public int GetStartAddress()
	{
		return m_start_address;
	}
	
	public int GetCount()
	{
		return m_count;
	}

	public boolean Init(String startAddress, int count, RegisterType register_type){
		boolean ret = false;
		
		try {
			if (!startAddress.isEmpty() && (count >= 0))
			{
				m_start_address = Integer.parseInt(startAddress);
				m_count = count;
				m_register_type = register_type;
				ret = true;
			}
		} catch (Exception e) {
			_logger.log(Level.SEVERE, String.format("Failed to init chunk. startAddress=%s count=%s register_type=%s",
					startAddress,String.valueOf(count),String.valueOf(register_type)));
			_logger.log(Level.SEVERE, String.format("Exception: %s", e.getMessage()));
		}
		return ret;
	}

	public int IsInChunk(String address, int iSize, RegisterType register_type){
		int ret = -1;

		try {
			int register_address = Integer.parseInt(address);
			
			if (register_type == m_register_type) {
				if (register_address >= m_start_address) {
					if ((register_address + iSize) <= (m_start_address + m_count)) {
						// calculate start and end indexes of
						// register inside chunk
						ret = register_address - m_start_address;
					}
				}
			}
		} catch (Exception e) {
			_logger.log(Level.SEVERE, String.format("Failed to check IsInChunk. address=%s iSize=%s register_type=%s",
					address,String.valueOf(iSize),String.valueOf(register_type)));
			_logger.log(Level.SEVERE, String.format("Exception: %s", e.getMessage()));
		}
		return ret;
	}
	
	
	
}
