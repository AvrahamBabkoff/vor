package il.co.vor.Modbus;

import il.co.vor.DalConfigObjects.Plc;
import il.co.vor.common.Enums.OperandDataType;
import il.co.vor.common.Enums.RegisterType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;



public class PLCS7IO implements IPLCIO
{
	/**
	 * class PLCS7IO implements IPLCIO Siemens S7-300 PLC tcp protocol.
	 * Current implementation assumes that all data resides in data blocks
	 * Official documentation of the protocol was not found. The implementation was derived
	 * from a C# library from https://s7net.codeplex.com/ 
	 **/
	
	public static final String S7_DB_ADDRESS_SEPERATOR = "\\.";
	
	private Plc m_plc = null;
	private Logger _logger = Logger.getLogger(PLCS7IO.class.getName());

	enum DataType
	{
		Input (0x81),
		Output (0x82),
		Marker (0x83),
		DataBlock (0x84),
		Timer (0x1d),
		Counter (0x1c);
		private final int m_DataType;
		private DataType (int iDataType)
		{
			this.m_DataType = iDataType;
		}
		public int value () 
		{ 
			return m_DataType; 
		}
	}

	private byte[] m_byteArrayConnect1 = 
	{ 
			0x03, 0x00, 0x00, 0x16, 0x11, (byte)0xe0, 0x00, 0x00, 0x00, 0x2e, 
			0x00, (byte)0xc1, 0x02, 0x01, 0x00, (byte)0xc2, 0x02, 0x03, 0x00, (byte)0xc0, 
		    0x01, 0x09 
	};
	private byte[] m_byteArrayConnect2 = 
    { 
    		0x03, 0x00, 0x00, 0x19, 0x02, (byte)0xf0, (byte)0x80, 0x32, 0x01, 0x00, 
    		0x00, (byte)0xff, (byte)0xff, 0x00, 0x08, 0x00, 0x00, (byte)0xf0, 0x00, 0x00, 
    		0x03, 0x00, 0x03, 0x01, 0x00 
    };

	private byte[] m_byteArrayGetDataBlock = 
    {
    		0x03, 0x00, 0x00, 0x1f,  0x02, (byte)0xf0, (byte)0x80, 0x32, 0x01, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x0e, 0x00, 0x00, 0x04, 0x01, 0x12, 0x0a, 0x10, 0x02, 0x00, 0x00, 0x00, 
            0x00, (byte)DataType.DataBlock.value(), 0x00, 0x00, 0x00
	};
	
	private final int m_iMaxBytesToRead = 220;
	private final int m_iTimeout = 3000;
	private final int m_iResponseSizeConnect1 = 22;
	private final int m_iResponseSizeConnect2 = 27;
	private final int m_iResponseSizeWrite = 513;
	private final int m_iReadDataBlockHeaderResponseSize = 25;
	private final int m_iReceiveWriteBufferSize = 35;
	private final int m_iReceiveBufferSize = Math.max(m_iResponseSizeConnect1, 
													 (Math.max(m_iReadDataBlockHeaderResponseSize, 
															 m_iResponseSizeConnect2)));
	

	private Socket m_socket;
	private OutputStream m_os;
	private InputStream m_is;
	private InetSocketAddress m_plcAddress;
	private byte[] m_byteArrayReceive;
	
	
	private void Close ()
	{
		try 
		{
			if (null != m_socket)
			{
				m_socket.close();
			}
		} 
		catch (IOException e) 
		{
			_logger.log(Level.SEVERE,
					String.format("Exception: %s", e.getMessage()));
		}
		m_socket = null;
		m_os = null;
		m_is = null;
	}
	
	@Override
	public boolean ReadChunk(IChunkVals cvals) 
	{
		boolean bResult = false;
		int iBytesRead;
	    int iDB;
	    int iStartAddress;
        int iBytesToRead = 0;
        int iPosition = 0;
        int iBytesLeft;
        byte [] byteArray;
        int iResponseSize;
		S7DataBlockChunkVals _cvals = (S7DataBlockChunkVals)cvals;
		
		if (null != m_socket)
		{
			try
			{				
				iBytesLeft = _cvals.GetCount();
				iStartAddress = _cvals.GetStartAddress();
				iDB = _cvals.GetDB();
				byteArray = (byte[]) _cvals.GetValsData();

				m_byteArrayGetDataBlock[25] =  (byte) ((iDB >> 8)&0xff);
				m_byteArrayGetDataBlock[26] = (byte) (iDB & 0xff);


	                
				iBytesToRead = Math.min(m_iMaxBytesToRead, iBytesLeft);

                while (iBytesLeft > 0)
                {
                	iBytesRead = -1;
                	m_byteArrayGetDataBlock[23] = (byte) ((iBytesToRead >> 8)&0xff);
	    			m_byteArrayGetDataBlock[24] = (byte) (iBytesToRead & 0xff);
	    			
	    			m_byteArrayGetDataBlock[29] = (byte) (((short)((iStartAddress + iPosition)*8) >> 8)&0xff);
	    			m_byteArrayGetDataBlock[30] = (byte) ((short)((iStartAddress + iPosition)*8) & 0xff);

	    			m_os.write(m_byteArrayGetDataBlock);
	    			m_os.flush();
	    			// read 25 bytes
	    			iBytesRead = m_is.read(m_byteArrayReceive, 0, m_iReadDataBlockHeaderResponseSize);
	    			if (m_iReadDataBlockHeaderResponseSize != iBytesRead)
	    			{
	    				throw new Exception ("Initial response: Expected 25 bytes. received " + iBytesRead + "bytes");
	    			}
	    			else
	    			{
	    				iResponseSize = m_byteArrayReceive[3];
	    				
	    				if ((byte)iResponseSize != (byte)(iBytesToRead + m_iReadDataBlockHeaderResponseSize))
	    				{
	    					throw new Exception ("m_byteArrayReceive[3] has value of  " + iResponseSize + ". Expected " + (iBytesToRead + m_iReadDataBlockHeaderResponseSize));
	    				}
	    				else
	    				{
	    					iBytesRead = m_is.read(byteArray, iPosition, iBytesToRead);
	    					
	    					if (iBytesRead != iBytesToRead)
	    					{
	    						throw new Exception ("Expected " + iBytesToRead + " bytes. received " + iBytesRead + "bytes");
	    					}
	    				}
	    			}

                    iBytesLeft -= iBytesToRead;
                    iPosition += iBytesToRead;
                    iBytesToRead = Math.min(m_iMaxBytesToRead, iBytesLeft);
                }
                bResult = true;
			}
			catch (Exception e)
			{
				_logger.log(Level.SEVERE, String.format("Closing connection. Exception: %s", e.getMessage()));
				Close ();
			}
		}
		return bResult;
	}

	@Override
	public boolean WriteOperandVal(IChunkVals cvals, String operand_val, String start_address, OperandDataType data_type) {
		
		boolean bResult = true;
		
		
		long lStart = 0;
		long lEnd = 0;

		lStart = System.currentTimeMillis();
		_logger.log(Level.INFO, "PLCS7IO WriteOperandVal Start");
		
		if (null != m_socket)
		{
			try
			{
				//parse address
				String [] strings = start_address.split(S7_DB_ADDRESS_SEPERATOR);
				int address = Integer.parseInt(strings[1]);
				int iDB = ((S7DataBlockChunkVals)cvals).GetDB();
				
				float fVal = Float.parseFloat(operand_val);
				byte[] bfVal = ByteBuffer.allocate(4).putFloat(fVal).array();
				int iBytesWrite;
				byte[] bReceive = new byte[m_iResponseSizeWrite];
				
				byte[] ba = 
				    {
				    		0x03, 0x00, 0x00, //given
				    		0x27 , // size=39=m_iReceiveWriteBufferSize + bfVal.length;
				    		0x02, (byte)0xf0, (byte)0x80, 0x32, 0x01, 0x00, 0x00, //given
				    		0x00,0x03, //count-1=3
				    		0x00, 0x0e, //given
				    		0x00, 0x08, //count+4=8
				    		0x05, 0x01, 0x12, 0x0a, 0x10, 0x02, //given 
				    		0x00, 0x04, //count
				    		0x00, 0x00, 
				    		(byte)DataType.DataBlock.value(),
				    		0x00, //given
				    		0x00, 0x00, //address*8
				    		0x00, 0x04, //given 
				    		0x00, 0x20
					};
			
				ba[25] =  (byte) ((iDB >> 8)&0xff);
				ba[26] = (byte) (iDB & 0xff);
				
				ba[29] = (byte) (((short)(address*8) >> 8)&0xff);
    			ba[30] = (byte) ((short)(address*8) & 0xff);
    			
    			
				byte[] byteArrayWriteDataBlock = ByteBuffer.wrap(new byte[ba.length + bfVal.length]).put(ba).put(bfVal).array();
			
				m_os.write(byteArrayWriteDataBlock);
				m_os.flush();
				
				iBytesWrite = m_is.read(bReceive, 0, m_iResponseSizeWrite-1);
	
				if (bReceive[21] != (byte)0xff)
				{
					bResult = false;
					_logger.log(Level.SEVERE, String.format("Failed to WriteOperandVal. startAddress=%s val=%s",
							String.valueOf(address),operand_val));
				}
			}
			catch (Exception e)
			{
				_logger.log(Level.SEVERE, String.format("Closing connection. Exception: %s", e.getMessage()));
				Close ();
			}
		}
		
		_logger.log(Level.INFO, "PLCS7IO WriteOperandVal End");
		lEnd = System.currentTimeMillis();
		_logger.log(Level.INFO, String.format("PLCS7IO WriteOperandVal Duration: %d milliseconds", (lEnd - lStart)));
		return bResult;
	}
	
	
	@Override
	public void Init(Plc _plc) 
	{
		m_plc = _plc;
		
		m_plcAddress = new InetSocketAddress (m_plc.getPlcAddressIp () , m_plc.getPlcAddressPort ());
		m_socket = null;
		m_byteArrayReceive = new byte[m_iReceiveBufferSize];
	}

	@Override
	public boolean CheckConnection() 
	{
		int iBytesRead;
		boolean bResult = true;
		if (null == m_socket)
		{			
			m_socket = new Socket ();
			try 
			
			{
				m_socket.connect(m_plcAddress, m_iTimeout);
			
				m_socket.setSoTimeout(m_iTimeout);
				// declare 5000 as constant
	
				m_os = m_socket.getOutputStream();
				m_is = m_socket.getInputStream();

				// write first connect command
				m_os.write(m_byteArrayConnect1);
				m_os.flush();
				// get response and check size
				// declare 22 as constant
				iBytesRead = m_is.read(m_byteArrayReceive, 0, m_iResponseSizeConnect1);
				if (m_iResponseSizeConnect1 != iBytesRead)
				{
					throw new Exception("failed to send and receive first connect command");
				}
				m_os.write(m_byteArrayConnect2);
				m_os.flush();
				// get response and check size
				// declare 27 as constant
				iBytesRead = m_is.read(m_byteArrayReceive, 0, m_iResponseSizeConnect2);
				if (m_iResponseSizeConnect2 != iBytesRead)
				{
					throw new Exception("failed to send and receive second connect command");
				}
			} 
			catch (Exception e) 
			{
				_logger.log(Level.SEVERE, String.format("Failed to connect. Exception: %s", e.getMessage()));
				bResult = false;
				Close ();
			}
		}		
		return bResult;
	}

	@Override
	public IChunkVals CreateChunkVals(String startAddress, int count, RegisterType register_type) {
		// TODO Auto-generated method stub
		IChunkVals chunk_vals = null;

		try {
			switch (register_type) {
			case S7_DATABLOCK:
				chunk_vals = new S7DataBlockChunkVals();
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
		_logger.log(Level.WARNING, "Close PLC Connection.");
		Close();
		
	}

	

}
