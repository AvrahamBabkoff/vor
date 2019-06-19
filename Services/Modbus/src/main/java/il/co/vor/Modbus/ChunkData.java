package il.co.vor.Modbus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import il.co.vor.DalConfigObjects.ModbusChunk;
import il.co.vor.DalConfigObjects.Plc;
import il.co.vor.common.Enums;

import il.co.vor.common.Enums.OperandDataType;
import il.co.vor.common.Enums.PlcProtocolType;
import il.co.vor.common.Enums.RegisterType;

public class ChunkData {
	private static Logger _logger = Logger.getLogger(ChunkData.class.getName());

	private IPLCIO m_plc_io = null;
	private ChunkWrapper[] m_chunks = null;

	public boolean Init(List<ModbusChunk> _chunks, Map<Integer, RegisterWrapper> _registers, Plc _plc) {
		
		boolean ret = false;

		try {
			//RegisterWrapper _registerw = null;
			ModbusChunk chunk = null;
			ChunkWrapper chunkw = null;
			IChunkVals chunkv = null;
			int chunk_pos = -1;
			int offset_in_chunk = -1;
			
			List<ChunkWrapper> _db_chunks = null;
			List<ChunkWrapper> _chunks_tmp = null;

			switch (PlcProtocolType.values()[_plc.getPlcProtocolType()]) {
				case S7:
					m_plc_io = new PLCS7IO();
					break;
				case MODBUS:
					m_plc_io = new PLCModbusIO();					
					break;
				default:
					break;
			}
			
			if (null != m_plc_io)
			{
				m_plc_io.Init(_plc);
			}
			// update existing chunks
			if (_chunks == null) {
				_chunks = new ArrayList<ModbusChunk>();
			}
			else
			{
				_db_chunks = new ArrayList<ChunkWrapper>();
				for (int i = 0; i < _chunks.size(); i++) {
					try{
						chunk = _chunks.get(i);
						
						if (chunk != null)
						{
							chunkv = m_plc_io.CreateChunkVals(chunk.getChunkStartAddress(), chunk.getChunkSize(), RegisterType.values()[chunk.getChunkType()]);
							if (chunkv != null)
							{
								chunkw = new ChunkWrapper(chunk,chunkv);
								if (chunkw != null)
								{
									_db_chunks.add(chunkw);
									_logger.log(Level.INFO,String.format("Chunk added. address: %s type: %s size: %s",
											String.valueOf(chunkw.GetChunkStartAddress()),
											String.valueOf(chunkw.GetChunkType()), String.valueOf(chunkw.GetChunkSize())));
								}
							}
							else
							{
								_logger.log(Level.SEVERE, String.format("Failed to add chunk. IChunkVals is null. address: %s type: %s size: %s",
										String.valueOf(chunkw.GetChunkStartAddress()),
										String.valueOf(chunkw.GetChunkType()), String.valueOf(chunkw.GetChunkSize())));
							}
						}
						else
						{
							_logger.log(Level.SEVERE, String.format("Failed to add chunk. chunk is null. _chunks.get(i) i=%s",
									String.valueOf(i)));
						}
					
					} catch (Exception e) {
						_logger.log(Level.SEVERE, String.format("Failed to add chunk. _chunks.get(i) i=%s",
								String.valueOf(i)));
						_logger.log(Level.SEVERE, String.format("Exception: %s", e.getMessage()));
					}
				}
			}
			
			_chunks_tmp = new ArrayList<ChunkWrapper>();
			chunk_pos = -1;
			
			// update registers
			for (RegisterWrapper _registerw : _registers.values()) {

				
				
				offset_in_chunk = -1;
				
				// find register chunk (if exist)
				for (int j = 0; j < _db_chunks.size(); j++) {
					chunkw = _db_chunks.get(j);
					chunkv = chunkw.GetChunkVals();
					offset_in_chunk = chunkv.IsInChunk(_registerw.GetRegisterRef(), _registerw.GetRegisterSize(), RegisterType.values()[_registerw.GetRegisterType()]);
					if (offset_in_chunk != -1) {
						_registerw.SetOffsetInChunk(offset_in_chunk);
						if (chunkw.GetChunkPos() == -1) 
						{ // add chunk to list
							chunk_pos = chunk_pos + 1;
							chunkw.SetChunkPos(chunk_pos);
							_registerw.SetChunkPos(chunk_pos);
							_chunks_tmp.add(chunkw);
							
						}
						_registerw.SetChunkPos(chunkw.GetChunkPos());
						break;
					}
				}
				if (_registerw.GetChunkPos() == -1)
				 // register is not in an existing chunk,
				 // create new chunk for the register
				{
					chunk = new ModbusChunk();
					chunk.setChunkSize(_registerw.GetRegisterSize());
					chunk.setChunkStartAddress(_registerw.GetRegisterRef());
					chunk.setChunkType(_registerw.GetRegisterType());
					
					chunkv = m_plc_io.CreateChunkVals(chunk.getChunkStartAddress(), chunk.getChunkSize(), RegisterType.values()[chunk.getChunkType()]);
					chunkw = new ChunkWrapper(chunk,chunkv);

					chunk_pos = chunk_pos + 1;
					chunkw.SetChunkPos(chunk_pos);
					_registerw.SetChunkPos(chunkw.GetChunkPos());
					
					chunkv = chunkw.GetChunkVals();
					offset_in_chunk = chunkv.IsInChunk(_registerw.GetRegisterRef(), _registerw.GetRegisterSize(), RegisterType.values()[_registerw.GetRegisterType()]);
					_registerw.SetOffsetInChunk(offset_in_chunk);
					_chunks_tmp.add(chunkw);
					
				}
				
			}
			
			// convert to an array
			m_chunks = _chunks_tmp.toArray(new ChunkWrapper[_chunks_tmp.size()]);
			
			ret = ((m_chunks != null) && (m_chunks.length > 0));
			
		} catch (Exception e) {
			_logger.log(Level.SEVERE, String.format("Exception: %s", e.getMessage()));

		}
		
		return ret;

	}

	// read chunks values, if failed to read a chunk, abort
	public boolean RefreshData(){

		boolean ret = true;
		if (m_chunks != null) {
			if (m_plc_io.CheckConnection()) {
				IChunkVals vals = null;
				ChunkWrapper chunk = null;

				long lStart = 0;
				long lEnd = 0;

				lStart = System.currentTimeMillis();

				_logger.log(Level.INFO, String.format("Read chunk values start"));

				for (int i = 0; ret && (i < m_chunks.length); i++) {
					try {
						chunk = m_chunks[i];
						if (chunk != null) {
							vals = (IChunkVals) chunk.GetChunkVals();
							ret = ret & m_plc_io.ReadChunk(vals);

							if ((ret) && (vals != null))
							{
								chunk.SetChunkVals(vals);
							}
							else
							{
								ret = false;
								_logger.log(Level.SEVERE, String.format("Filed to read Chunk, abort. Chunk Pos: %s Chunk id: %s ", String.valueOf(i),String.valueOf((chunk != null) ? chunk.GetChunkID() : "?")));
							}
						}
						else
						{
							ret = false;
							_logger.log(Level.SEVERE, String.format("Chunk is null, abort. Chunk Pos: %s", String.valueOf(i)));
						}

					} catch (Exception e) {
						ret = false;

						_logger.log(Level.SEVERE,
								String.format("Could not read Chunk value, Chunk id: %s Exception: %s",
										String.valueOf((chunk != null) ? chunk.GetChunkID() : "?"), e.getMessage()));
						e.printStackTrace();

					}
					if (!ret) { // if read chunk data failed, abort.
						break;
					}
				}
				lEnd = System.currentTimeMillis();
				_logger.log(Level.INFO, String.format("Read chunk values. Duration: %d milliseconds", (lEnd - lStart)));
				_logger.log(Level.INFO,
						String.format("Read chunk values End. %s chunks", String.valueOf(m_chunks.length)));
			} else {
				ret = false;
				_logger.log(Level.SEVERE, String.format("Could not read Chunks values, Failed to connect"));
			}
		} else {
			ret = false;
			_logger.log(Level.SEVERE, String.format("Could not read Chunks values, Chunks are empty"));
		}

		return ret;
	}

	public boolean GetVal(RegisterWrapper registerw, OperandWrapper operandw) {
		
		boolean ret = false;
		int chunk_pos = -1;
		ChunkWrapper chunkw = null;
		IChunkVals chunkv = null;
		
		try {
			
			chunk_pos = registerw.GetChunkPos();
			chunkw = m_chunks[chunk_pos];
			chunkv = chunkw.GetChunkVals();
			if (chunkw != null) // read value from chunk
			{
				ret = chunkv.GetVal(registerw.GetOffsetInChunk(), registerw.GetRegisterSize(), operandw);
			}

		} catch (Exception e) {
			_logger.log(Level.SEVERE, String.format("Exception: %s", e.getMessage()));
			e.printStackTrace();
			_logger.log(Level.SEVERE, String.format(
					"failed to read value for operand: operand id: %s register id: %s type: %s name: %s size: %s ref: %s val: %s",
					String.valueOf(registerw.GetOperandID()), String.valueOf(registerw.GetRegisterID()),
					String.valueOf(registerw.GetRegisterType()), registerw.GetRegisterName(),
					String.valueOf(registerw.GetRegisterSize()), registerw.GetRegisterRef(),
					((!operandw.IsValid()) ? "?"
							: operandw.GetLogValAsStr())));
	
		}
		return ret;
	}
	
	public boolean SetVal(RegisterWrapper registerw, OperandWrapper operandw, String val) {
		
		boolean ret = false;
		int chunk_pos = -1;
		ChunkWrapper chunkw = null;
		IChunkVals chunkv = null;
		
		long lStart = 0;
		long lEnd = 0;

		lStart = System.currentTimeMillis();
		_logger.log(Level.INFO, "ChunkData SetVal Start");
		
		try {
			
			chunk_pos = registerw.GetChunkPos();
			chunkw = m_chunks[chunk_pos];
			chunkv = chunkw.GetChunkVals();
			if ((chunkw != null) && (m_plc_io.CheckConnection()))
			{
				
				ret = m_plc_io.WriteOperandVal(chunkv, val, registerw.GetRegisterRef(), operandw.GetOperandDataType());
			}

		} catch (Exception e) {
			ret = false;
			_logger.log(Level.SEVERE, String.format("Exception: %s", e.getMessage()));
			e.printStackTrace();
			_logger.log(Level.SEVERE, String.format(
					"failed to write value for operand: operand id: %s register id: %s type: %s name: %s size: %s ref: %s val: %s",
					String.valueOf(registerw.GetOperandID()), String.valueOf(registerw.GetRegisterID()),
					String.valueOf(registerw.GetRegisterType()), registerw.GetRegisterName(),
					String.valueOf(registerw.GetRegisterSize()), registerw.GetRegisterRef(),
					val));
	
		}
		
		_logger.log(Level.INFO, "ChunkData SetVal End");
		lEnd = System.currentTimeMillis();
		_logger.log(Level.INFO, String.format("ChunkData SetVal Duration: %d milliseconds", (lEnd - lStart)));
		
		return ret;
	}
	
	public void Shutdown()
	{
		m_plc_io.Shutdown();
	}
}
