package il.co.vor.Modbus;

import il.co.vor.common.Enums.OperandDataType;
import il.co.vor.common.Enums.RegisterType;

/**
 * 
 */

/**
 * @author noga
 * 
 *
 */
public interface IChunkVals {
	
	 public Object GetValsData();
	 public void SetValsData(Object vals);
	 public int IsInChunk(String address, int iSize, RegisterType register_type);
	 //public boolean GetVal(int offset, int size, OperandDataType data_type, OperandVal operand_val);
	 public boolean Init(String startAddress, int count, RegisterType register_type);
	 /*public String GetStartAddress();
	 public int GetCount();*/
	 public boolean GetVal(int offset, int size, OperandWrapper operand_wrapper);
	 public boolean SetVal(int offset, OperandWrapper operand_wrapper, String val);
}
