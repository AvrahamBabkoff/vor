package il.co.vor.DalConfigObjects;

import javax.xml.bind.annotation.XmlElement;

import il.co.vor.common.VosConfigResources;

public class ModbusChunk 
{
	private int m_chunkSize;
	private int m_plcId;
	private int m_chunkId;
	private String m_chunkStartAddress;
	private int m_chunkType;
	

	@XmlElement(name=VosConfigResources.MODBUS_CHUNKS_PROP_CHUNK_SIZE_NAME)
	public int getChunkSize() 
	{
		return m_chunkSize;
	}
	
	public void setChunkSize(int chunkSize) 
	{
		this.m_chunkSize = chunkSize;
	}
	
	@XmlElement(name=VosConfigResources.PLCS_PROP_PLC_ID_NAME)
	public int getPlcId() 
	{
		return m_plcId;
	}
	
	public void setPlcId(int plcId) 
	{
		this.m_plcId = plcId;
	}
	
	@XmlElement(name=VosConfigResources.MODBUS_CHUNKS_PROP_CHUNK_ID_NAME)
	public int getChunkId() 
	{
		return m_chunkId;
	}
	
	public void setChunkId(int chunkId) 
	{
		this.m_chunkId = chunkId;
	}
	
	@XmlElement(name=VosConfigResources.MODBUS_CHUNKS_PROP_CHUNK_START_ADDRESS_NAME)
	public String getChunkStartAddress() 
	{
		return m_chunkStartAddress;
	}
		
	public void setChunkStartAddress(String chunkStartAddress) 
	{
		this.m_chunkStartAddress = chunkStartAddress;
	}
	
	@XmlElement(name=VosConfigResources.MODBUS_CHUNKS_PROP_CHUNK_TYPE_NAME)
	public int getChunkType() 
	{
		return m_chunkType;
	}
	
	public void setChunkType(int chunkType) 
	{
		this.m_chunkType = chunkType;
	}		
}
