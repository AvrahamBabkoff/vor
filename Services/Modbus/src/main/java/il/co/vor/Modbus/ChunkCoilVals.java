package il.co.vor.Modbus;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import il.co.vor.common.Enums.OperandDataType;
import il.co.vor.common.Enums.RegisterType;
import net.wimpi.modbus.procimg.Register;
import net.wimpi.modbus.util.BitVector;
import net.wimpi.modbus.util.ModbusUtil;


public class ChunkCoilVals extends ModbusChunkVals {
	private static Logger _logger = Logger.getLogger(OperandManager.class.getName());
	
	private BitVector chunk_coil_vals = null;
	
	public Object GetValsData() {
		
		return chunk_coil_vals;
	}

	public void SetValsData(Object vals) {
		
		chunk_coil_vals = (BitVector) vals;

	}

	@Override
	public boolean GetVal(int offset, int size, OperandWrapper operand_wrapper) {
		double d_val = -1;
		String str_val = "";
		boolean ret = true;
		OperandDataType data_type = null;
		
		try {
			
			operand_wrapper.SetNotValid();
			data_type = operand_wrapper.GetOperandDataType();
			
			if ((chunk_coil_vals == null) || (chunk_coil_vals.size() <= 0)) {
				ret = false;
			} else {
				boolean b = chunk_coil_vals.getBit(offset);
				d_val = (b ? 1 : 0);
				
				if (data_type == OperandDataType.ASCII) {
					operand_wrapper.SetStringVal(str_val);
				} else {
					operand_wrapper.SetDoubleVal(d_val);
				}
				
			}
		
		} catch (Exception e) {
			ret = false;
			_logger.log(Level.SEVERE, String.format("Exception: %s", e.getMessage()));
			e.printStackTrace();
		}

		
		return ret;
	}

	@Override
	public boolean Init(String startAddress, int count, RegisterType register_type) {
		boolean ret = false;
		
		if (register_type == RegisterType.MODBUS_COIL || register_type == RegisterType.MODBUS_DISCRETE_INPUT)
		{
			ret = super.Init(startAddress,count,register_type);
		}
		
		return ret;
	}

	public boolean GetConvertedVal(String val) {
		return ("Y".equals(val.toUpperCase()) 
			      || "1".equals(val.toUpperCase())
			      || "TRUE".equals(val.toUpperCase())
			      || "ON".equals(val.toUpperCase()) 
			     );
	}

	@Override
	public boolean SetVal(int offset, OperandWrapper operand_wrapper, String val) {
		boolean ret = false;
		
		try {
			double d_val = -1;
			Boolean bval = GetConvertedVal(val);
			chunk_coil_vals.setBit(offset, bval);
			
			d_val = (bval ? 1 : 0);
			
			if (operand_wrapper.GetOperandDataType() == OperandDataType.ASCII) {
				operand_wrapper.SetStringVal(val);
			} else {
				operand_wrapper.SetDoubleVal(d_val);
			}
			ret = true;
		} catch (Exception e) {
			_logger.log(Level.SEVERE, String.format("Exception: %s", e.getMessage()));
			e.printStackTrace();
		}
		
		return ret;
	}


}
