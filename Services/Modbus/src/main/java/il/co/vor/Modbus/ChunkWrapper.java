package il.co.vor.Modbus;
import java.util.logging.Level;
import java.util.logging.Logger;

import il.co.vor.DalConfigObjects.ModbusChunk;

public class ChunkWrapper {

	static Logger _logger = Logger.getLogger(NPLHelper.class.getName());
	
	private int chunk_id = -1;
	private ModbusChunk modbus_chunk = null;
	private IChunkVals chunk_vals = null;
	private int chunk_pos = -1;
	
	public ChunkWrapper(ModbusChunk modbusChunk, IChunkVals _chunk_vals) {
		
		chunk_id = modbusChunk.getChunkId();
		modbus_chunk = modbusChunk;

		chunk_vals = _chunk_vals;

	}
	
	public int GetChunkSize() {
		return modbus_chunk.getChunkSize();
	}

	public int GetChunkID() {
		return chunk_id;
	}

	public String GetChunkStartAddress() {
		String address = modbus_chunk.getChunkStartAddress();
		//return Integer.valueOf(address);
		return address;
	}

	public void SetChunkStartAddress(int chunk_start_address) {
		modbus_chunk.setChunkStartAddress(String.valueOf(chunk_start_address));
	}

	public int GetChunkType() {
		return modbus_chunk.getChunkType();
	}

	public void SetChunkType(int chunk_type) {
		modbus_chunk.setChunkType(chunk_type);
	}
	

	public IChunkVals GetChunkVals() {
		return chunk_vals;
	}
	
	public void SetChunkVals(IChunkVals chunkVals) {
		this.chunk_vals = chunkVals;
	}
	
	public Object GetChunkValsData() {
		return chunk_vals.GetValsData();
	}
	
	public void SetChunkValsData(Object chunkValsData) {
		this.chunk_vals.SetValsData(chunkValsData);
	}
	
	public int GetChunkPos() {
	    return chunk_pos;
	}
	
	public void SetChunkPos(int chunk_pos) {
		this.chunk_pos = chunk_pos;
	}
	
}
