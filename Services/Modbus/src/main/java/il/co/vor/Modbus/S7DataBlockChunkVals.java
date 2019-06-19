package il.co.vor.Modbus;

import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import il.co.vor.common.Enums.OperandDataType;
import il.co.vor.common.Enums.RegisterType;
/**
 * class S7DataBlockChunkVals implements IChunkVals for chunks of S7 registers.
 * Current implementation assumes that all data resides in data blocks, and that all registers
 * are 32 bit (4 byte) float values
 * 
 * Addresses of chunks as well as registers are assumed to be coded in the following way: <DB>.<Offset> where <DB> represents the data block number
 * and <Offset> represents the starting location of the chunk/register within the data block. Example:   "42.360"
 * 
 * The chunk is implement using a byte array
 **/

public class S7DataBlockChunkVals implements IChunkVals
{

	private byte[] m_byteArrayChunk;
    int m_iCount;
    int m_iDB;
    int m_iStartAddress;
	private Logger _logger = Logger.getLogger(S7DataBlockChunkVals.class.getName());
	public static final String S7_DB_ADDRESS_SEPERATOR = "\\.";


    
    public S7DataBlockChunkVals ()
    {
    	m_byteArrayChunk = null;
    	m_iCount = 0;
    }

	public int GetStartAddress()
	{
		return m_iStartAddress;
	}
	
	public int GetCount()
	{
		return m_iCount;
	}
    
	public int GetDB()
	{
		return m_iDB;
	}
	
	@Override
	public Object GetValsData() 
	{
		return m_byteArrayChunk;
	}

	@Override
	public void SetValsData(Object vals) 
	{
		
	}

	@Override
	public int IsInChunk(String address, int iSize, RegisterType register_type) 
	{
		int iResult = -1;
		String [] strings; 
	    int _iDB;
	    int _iStartAddress;

		if (register_type == RegisterType.S7_DATABLOCK && (4 == iSize))
		{
	        strings = address.split(S7_DB_ADDRESS_SEPERATOR);
	        if (strings.length == 2)
	        {
	        	try
	        	{
	        		_iDB = Integer.parseInt(strings[0]);
	        		_iStartAddress = Integer.parseInt(strings[1]);
	        		
	        		if ((_iDB == m_iDB) && (_iStartAddress >= m_iStartAddress) && ((_iStartAddress + iSize) <= (m_iStartAddress + m_iCount)))
	        		{
	        			iResult = _iStartAddress - m_iStartAddress;
	        		}
	        	}
	        	catch (NumberFormatException e)
	        	{
	    			_logger.log(Level.SEVERE,
	    					String.format("Chunk Properties: m_iDB = %d, m_iStartAddress = %d, m_iCount = %d. "
	    							+ "Input: address = %s, iSize = %d, register_type = %s. Exception: %s",
	    							m_iDB, m_iStartAddress, m_iCount, 
	    							address, iSize, register_type.toString(),
	    							e.getMessage()));	        		
	        	}
	        }        	
        }
		else
		{
			_logger.log(Level.SEVERE,
					String.format("Illegal input parameters for an S7 register. "
							+ "Chunk Properties: m_iDB = %d, m_iStartAddress = %d, m_iCount = %d. "
							+ "Input: address = %s, iSize = %d, register_type = %s",
							m_iDB, m_iStartAddress, m_iCount, 
							address, iSize, register_type.toString()));
			
		}
		
		return iResult;
	}

	@Override
	public boolean Init(String startAddress, int count, RegisterType register_type) 
	{
		boolean bResult = false;
		String [] strings; 
		
		_logger.log(Level.WARNING,
				String.format("Chunk Properties: startAddress = %s, count = %d, register_type = %s",
						startAddress, count, register_type.toString()));

		if (register_type == RegisterType.S7_DATABLOCK && (count > 0))
		{
	        strings = startAddress.split(S7_DB_ADDRESS_SEPERATOR);
	        if (strings.length == 2)
	        {
	        	try
	        	{
	        		m_iDB = Integer.parseInt(strings[0]);
	        		m_iStartAddress = Integer.parseInt(strings[1]);
	        		m_iCount = count;
	        		m_byteArrayChunk = new byte[m_iCount];
	        		bResult = true;
	        	}
	        	catch (NumberFormatException e)
	        	{
	    			_logger.log(Level.SEVERE,
	    					String.format("Exception: %s", e.getMessage()));
	        		
	        	}
	        }        	
        }
		return bResult;
	}

	@Override
	public boolean GetVal(int offset, int size, OperandWrapper operand_wrapper) 
	{
		boolean bResult = false;
		operand_wrapper.SetNotValid();
		
		OperandDataType data_type = operand_wrapper.GetOperandDataType();
		
		if ((4 == size) && ((offset + size) <= m_iCount) && (data_type == OperandDataType.BIT_FLOAT_32) && (null != m_byteArrayChunk))
		{
			operand_wrapper.SetDoubleVal((double)ByteBuffer.wrap(m_byteArrayChunk, offset, size).getFloat());
			bResult = true;
		}
		return bResult;
	}

	@Override
	public boolean SetVal(int offset, OperandWrapper operand_wrapper, String val) {
		boolean bResult = false;
		
		try
    	{
			float fVal = Float.parseFloat(val);
			byte[] bfVal = ByteBuffer.allocate(4).putFloat(fVal).array();
			
			if (bfVal != null)
			{
				for (int i = 0; i < bfVal.length; i++) {
					m_byteArrayChunk[offset+i]=bfVal[i];
	            }
				
				if ((operand_wrapper.GetOperandDataType() == OperandDataType.BIT_FLOAT_32) && (null != m_byteArrayChunk))
				{
					operand_wrapper.SetDoubleVal((double)fVal);
					bResult = true;
				}
			}
    	}
		catch (NumberFormatException e)
    	{
			_logger.log(Level.SEVERE,
					String.format("Exception: %s", e.getMessage()));
    		
    	}
		
		return bResult;
	}


}
