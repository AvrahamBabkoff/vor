package il.co.vor.Modbus;

import il.co.vor.DalConfigObjects.Plc;
import il.co.vor.common.Enums;
import il.co.vor.common.Enums.OperandDataType;
import il.co.vor.common.Enums.RegisterType;

public interface IPLCIO {
	public boolean ReadChunk(IChunkVals cvals);
	public void Init(Plc _plc);
	public boolean CheckConnection();
	public IChunkVals CreateChunkVals(String startAddress, int count, RegisterType register_type);
	public void Shutdown();
	//public boolean GetVal(int startAddress, int size, OperandDataType data_type, IChunkVals chunk_vals, OperandVal operand_val);
	boolean WriteOperandVal(IChunkVals cvals, String operand_val, String address, OperandDataType data_type);

}
