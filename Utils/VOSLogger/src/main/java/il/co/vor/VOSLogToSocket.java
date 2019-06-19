package il.co.vor;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;


import il.co.vor.common.Constants;

/**
 * The VOSLogToSocket class allows clients to connect via tcp (telnet) and receive logs:
 * Notes:
 *  1. Class implements Runnable.
 *  2. Thread function does the following:
 *  	a. creates an instance of ServerSocket
 *		b. binds to an available port
 *		c. stores the port in a property (available through getPort())
 *		d. waits for clients to connect (call to "accept")
 *		e. when a new client connects, if a connection exists it is closed
 *		f. an instance of PrintWriter is created and stored in a member variable
 *		g. for termination purposes, the thread catches IOException exceptions
 *	3. class exposes following public methods: 
 *		a. logToSocket
 *		b. getPort
 *		c. Terminate
 *	
 */

public class VOSLogToSocket  implements Runnable
{

	private volatile Socket m_socket;
	private volatile ObjectOutputStream m_o = null;
	private int m_port;
	private ServerSocket m_s;
	private boolean m_bTerminated;
	//private boolean m_bOutStreamError;
	private boolean m_isInWrite;
	private AtomicInteger m_waitWriteCounter;
	private Thread m_threadMonitor = null;
	private static Logger _logger = Logger.getLogger(VOSLogToSocket.class.getName());
	/**
	 * Constructor:
	 * Initialize all members
	 * **/
	VOSLogToSocket()
	{
		m_port = -1;
		m_socket = null;
		m_s = null;
		m_bTerminated = false;
		m_isInWrite = false;
		m_waitWriteCounter = new AtomicInteger();
		
		
		m_threadMonitor = new Thread()
        {
            public void run() {
                MonitorWrite();
            }
        }; 
        m_threadMonitor.start();
        
	}
	
	public void MonitorWrite()
	{
		while (!m_bTerminated)
		{
			if (m_isInWrite)
			{
				if (m_waitWriteCounter.get() > Constants.MAX_WAIT_WRITE_COUNTER)
				{
					_logger.log(Level.SEVERE, "Client is stuck!!! disconnect client!!!");
					DisconnectClient(m_o);
				}
				else
				{
					m_waitWriteCounter.incrementAndGet();
				}
			}
			
			try {
				Thread.sleep(Constants.WRITE_MONITOR_SLEEP_TIME);
			} catch (InterruptedException e) {
				System.out.println("il.co.vor.VOSLogToSocket.MonitorWrite: interupted in InterruptedException!!!");
			}
		}
		System.out.println("il.co.vor.VOSLogToSocket.MonitorWrite: Exiting!!!");
		
	}
	
/**
 * Thread function does the following:
 *  	a. creates an instance of ServerSocket
 *		b. binds to an available port
 *		c. stores the port in a property (available through getPort())
 *		d. waits for clients to connect (call to "accept")
 *		e. when a new client connects, if a connection exists it is closed
 *		f. an instance of PrintWriter is created and stored in a member variable
 *		g. for termination purposes, the thread catches IOException exceptions
 **/	
	@Override
	public void run() 
	{
		// listen to available port
		//ServerSocket s = null;
		boolean bBreak = false;
		Socket _socket = null;

		try 
		{
			m_s = new ServerSocket(0);
			m_port = m_s.getLocalPort();
			_logger.log(Level.INFO,"listening on port: " + m_port);
			System.out.println("listening on port: " + m_port);		
			while (true)
			{
				try
				{
					
					_socket = m_s.accept();
					OnClientConnected(_socket);
					_logger.log(Level.INFO,"going to sleep!!!");
					System.out.println("going to sleep!!!");
					Thread.sleep(0);
				}
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
					//e.printStackTrace();
					if (Thread.interrupted())
					{
						System.out.println("il.co.vor.VOSLogToSocket.run: interupted in IOException. Exitting!!!");
						bBreak = true;
					}
					if(m_bTerminated)
					{
						System.out.println("il.co.vor.VOSLogToSocket.run: m_bTerminated = true in IOException. Exitting!!!");
						bBreak = true;						
					}
					if (true == bBreak)
					{
						break;
					}
				}
				catch (InterruptedException e) 
				{
					System.out.println("il.co.vor.VOSLogToSocket.run: interupted in InterruptedException. Exitting!!!");
					break;
				} 
			}
		} 
		catch (IOException e) 
		{
			_logger.log(Level.SEVERE, "Failed to create ServerSocket!!! Exiting Thread!!!");
			System.out.println("il.co.vor.VOSLogToSocket.run: Failed to create ServerSocket!!!");
		}				
	}

	/**
	 * getPort:
	 * return logging port that we are bound to. Note that this port is selected from available ports and is not fixed 
	 * 
	 * **/
	public int getPort() 
	{
		return m_port;
	}

	/**
	 * logToSocket:
	 * method receives a string to write to the connected client.
	 * check if we have a valid PrintWriter instance. If yes, write the string and flush
	 * if there are any errors, a flag is set which indicates that the PrintWriter object is not valid for subsequent calls to logToSocket.
	 * Note if the PrintWriter object has been invalidated, it can become valid only upon a new client connecting (see run() method)
	 * 
	 * **/
	public void logToSocket(String str, Level level)
	{
		ObjectOutputStream _o = m_o;
		SocketLogRecord slr = new SocketLogRecord(str, level);
		
		
		if (null != _o /*&& (false == m_bOutStreamError)*/)
		{
			try
			{
				m_isInWrite = true;
				_o.writeObject(slr);
				//_o.writeUnshared(slr);
				_o.flush();
				_o.reset();
				m_isInWrite = false;
				m_waitWriteCounter.set(0);
			}
			catch(Exception e)
			{
				DisconnectClient(_o);
				_logger.log(Level.SEVERE, "exception: " + e.getMessage());
				System.out.println("in VOSLogToSocket.logToSocket exception: " + e.getMessage());
				//m_bOutStreamError = true;
			}
		}
	}
	
	/**
	 * Terminate:
	 * signal thread method (run()) to terminate. 
	 * run method blocks on a call to accept. The way to unblock this call is by calling close on the ServerSocket object
	 * 
	 * 
	 * **/
	public void Terminate()
	{
		m_bTerminated = true;
		if (null != m_s)
		{
			System.out.println("in VOSLogToSocket.Termonate!!!");
			try 
			{
				System.out.println("in VOSLogToSocket.Termonate. calling close");
				m_s.close();
			} 
			catch (IOException e) 
			{
				System.out.println("in VOSLogToSocket.Terminate exception: " + e.getMessage());
				e.printStackTrace();
			}
		}
		else
		{
			System.out.println("in VOSLogToSocket.Termonate, ServerSocket is null!!!");
		}
		
		if (null != m_threadMonitor)
		{
			System.out.println("in VOSLogToSocket.Termonate, going to interupt monitor thread!!!");
			m_threadMonitor.interrupt();
		}
	}
	
	public synchronized void OnClientConnected(Socket _socket) throws IOException
	{
		try {
			if (null != m_socket)
			{
				_logger.log(Level.SEVERE, "closing existing socket!!!");
				System.out.println("client connected!!! closing existing socket!!!");
				m_socket.close();
			}
		} catch (IOException e1) {
			_logger.log(Level.SEVERE, "exception: " + e1.getMessage());
			e1.printStackTrace();
		}
		finally {
			m_socket = null;
			m_o = null;
			//m_bOutStreamError = false;
			m_isInWrite = false;
			m_waitWriteCounter.set(0);			
		}
		m_socket = _socket;
		m_o = new ObjectOutputStream(_socket.getOutputStream());

	}
	
	public synchronized void DisconnectClient(ObjectOutputStream _o)
	{
		
		if ((m_socket != null) && (_o == m_o))
		{
			try
			{
				System.out.println("in VOSLogToSocket DisconnectClient: going to close socket");
				_logger.log(Level.SEVERE, "going to close socket");
				m_socket.close();
			}
			catch (IOException e1) {
				_logger.log(Level.SEVERE, "exception: " + e1.getMessage());
				e1.printStackTrace();
			}
			finally {
				m_socket = null;
				m_o = null;
				//m_bOutStreamError = false;
				m_isInWrite = false;
				m_waitWriteCounter.set(0);			
			}
		}
		else
		{
			_logger.log(Level.SEVERE, "not (!) closing socket!!!!");
			System.out.println("in VOSLogToSocket DisconnectClient: not (!) closing socket!!!!");
		}
	}
}
