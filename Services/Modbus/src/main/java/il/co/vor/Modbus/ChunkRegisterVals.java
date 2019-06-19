package il.co.vor.Modbus;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import il.co.vor.common.Enums.OperandDataType;
import il.co.vor.common.Enums.RegisterType;
import net.wimpi.modbus.procimg.Register;
import net.wimpi.modbus.procimg.SimpleRegister;
import net.wimpi.modbus.util.BitVector;
import net.wimpi.modbus.util.ModbusUtil;

public class ChunkRegisterVals extends ModbusChunkVals {

	private static Logger _logger = Logger.getLogger(OperandManager.class.getName());
	
	private Register[] chunk_register_vals = null;
	
	public Object GetValsData() {
		
		return chunk_register_vals;
	}

	public void SetValsData(Object vals) {
		
		chunk_register_vals = (Register[]) vals;

	}
	
	@Override
	public boolean Init(String startAddress, int count, RegisterType register_type) {
		boolean ret = false;
		
		if (register_type == RegisterType.MODBUS_HOLDING_REGISTER || register_type == RegisterType.MODBUS_INPUT_REGISTER)
		{
			ret = super.Init(startAddress,count,register_type);
		}
		
		return ret;
	}
	
	public static Register[] byteArrayToRegister(byte[] byteArray) throws RuntimeException {

		// TODO byteArray might has a odd number of bytes...
		SimpleRegister[] register = null;

		try {
			if (byteArray.length % 2 == 0) {
				register = new SimpleRegister[byteArray.length / 2];

				// for (int i = 0; i < byteArray.length; i++) {
				for (int i = 0; i < byteArray.length / 2; i++) {
					register[i] = new SimpleRegister(byteArray[i * 2], byteArray[i * 2 + 1]);

				}
			} else {
				_logger.log(Level.SEVERE,
						String.format("Conversion from byteArray to Register is not working for odd number of bytes"));
				throw new RuntimeException(
						"Conversion from byteArray to Register is not working for odd number of bytes");
			}
		} catch (Exception e) {
			register = null;
			_logger.log(Level.SEVERE, String.format("Exception: %s", e.getMessage()));
			e.printStackTrace();
		}
		return register;
	}
	
	public static byte[] RegisterArrayToByteArray(Register[] registerArray) throws RuntimeException {

		// TODO byteArray might has a odd number of bytes...
		byte[] byteArray = null;

		try {
			if (registerArray.length > 0) {
				byteArray = new byte[registerArray.length * 2];
				int j = 0;
				// for (int i = 0; i < byteArray.length; i++) {
				for (int i = 0; i < registerArray.length; i++) {
					byteArray[i * 2] = registerArray[i].toBytes()[0];
					byteArray[i * 2 + 1] = registerArray[i].toBytes()[1];
				}
			} else {
				_logger.log(Level.SEVERE, String.format("Register array is empty"));
				throw new RuntimeException("Register array is empty");
			}
		} catch (Exception e) {
			byteArray = null;
			_logger.log(Level.SEVERE, String.format("Exception: %s", e.getMessage()));
			e.printStackTrace();
		}

		return byteArray;
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
			
			if ((chunk_register_vals == null) || (chunk_register_vals.length <= 0)) {
				ret = false;
			} else {
				Register[] chunk_rvals = null;
				
				chunk_rvals = (Register[]) Arrays.copyOfRange(chunk_register_vals, offset, offset + size);
				byte[] rbvals = RegisterArrayToByteArray(chunk_rvals);

				switch (data_type) {
				case BIT_FLOAT_32:
					d_val = ModbusUtil.registersToFloat(rbvals); // must
																	// be
																	// 4
																	// bytes
					break;
				case BIT_FLOAT_64:
					d_val = ModbusUtil.registersToDouble(rbvals); // must
																	// be
																	// 8
																	// bytes
					break;
				case INTEGER:
					d_val = ModbusUtil.registersToInt(rbvals); // must
																// be 4
																// bytes
					break;
				case SHORT:
					d_val = ModbusUtil.registerToShort(rbvals); // must
																// be 2
																// bytes
					break;
				case UNSIGNED_SHORT:
					d_val = ModbusUtil.registerToUnsignedShort(rbvals); // must
																		// be
																		// 2
																		// bytes
					break;
				case ASCII:
					// must be even number of bytes
					try {
						//if (data_type == OperandDataType.ASCII) {
							str_val = new String(rbvals, "utf-8");
						//}
					} catch (Exception e) {
						_logger.log(Level.SEVERE, String.format("Exception: %s", e.getMessage()));
						e.printStackTrace();
					}
					break;
				default:
					ret = false;
				}
				
				if (ret) {
					if (data_type == OperandDataType.ASCII) {
						operand_wrapper.SetStringVal(str_val);
					} else {
						operand_wrapper.SetDoubleVal(d_val);
					}
				}

			}
		} catch (Exception e) {
			ret = false;
			_logger.log(Level.SEVERE, String.format("Exception: %s", e.getMessage()));
			e.printStackTrace();
		}

		return ret;
	}


	public Register[] GetConvertedVal(String val, OperandDataType data_type) {

		byte[] bvals = null;
		Register[] rvals = null;
		
		try {

			switch (data_type) {
				case BIT_FLOAT_32:
					bvals = ModbusUtil.floatToRegisters(Float.parseFloat(val)); // must
																	// be
																	// 4
																	// bytes
					break;
				case BIT_FLOAT_64:
					bvals = ModbusUtil.doubleToRegisters(Double.parseDouble(val)); // must be
																	// 8
																	// bytes
					break;
				case INTEGER:
					bvals = ModbusUtil.intToRegisters(Integer.parseInt(val)); // must
																// be 4
																// bytes
					break;
				case SHORT:
					bvals = ModbusUtil.shortToRegister(Short.parseShort(val)); // must
																// be 2
																// bytes
					break;
				case UNSIGNED_SHORT:
					bvals = ModbusUtil.unsignedShortToRegister(Integer.parseInt(val)); // must
																		// be
																		// 2
																		// bytes
					break;
				case ASCII:
					// must be even number of bytes
					if (val.length() % 2 != 0)
					{
						val = val + " ";
					}
					try {
						bvals = val.getBytes("utf-8");
					} catch (UnsupportedEncodingException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					break;
			}
			rvals = byteArrayToRegister(bvals);
		} catch (Exception e) {
			_logger.log(Level.SEVERE, String.format("Exception: %s", e.getMessage()));
			e.printStackTrace();
		}

		return rvals;
	}

	@Override
	public boolean SetVal(int offset, OperandWrapper operand_wrapper, String val) {
		
		boolean ret = false;
		
		try {
			Register[] rval = GetConvertedVal(val,operand_wrapper.GetOperandDataType());
			if (rval != null)
			{
				for (int i = 0; i < rval.length; i++) {
					chunk_register_vals[offset+i].setValue(rval[i].toBytes());
	            }
			}
			
			if (operand_wrapper.GetOperandDataType() == OperandDataType.ASCII) {
				operand_wrapper.SetStringVal(val);
			} else {
				operand_wrapper.SetDoubleVal(Double.parseDouble(val));
			}
			
			ret = true;
		} catch (Exception e) {
			_logger.log(Level.SEVERE, String.format("Exception: %s", e.getMessage()));
			e.printStackTrace();
		}

		return ret;
	}


}
