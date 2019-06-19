package il.co.vor.SystemManager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.TimerTask;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
//import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import il.co.vor.SocketLogRecord;
import il.co.vor.defines.Constants;

public class Console 
{
	private String sIP;
	private int iPort;
	
	private static BlockingDeque<SocketLogRecord> m_log_list = null;
	private static Style styleRed;
	private static Style styleYellow;
	private static Style styleGreen;
	
	private static int m_remove_rows_amount;
	
	private JFrame frame;
	private JTextPane textPane;
	private JTextField textField;
	private JScrollPane scrollPane;
	
	//private MessageConsole mc;
	private Socket m_socketToServer = null;
	private StyledDocument document;
	private ServiceUIWraper cbpr;
	private boolean frame_disposed;
	
	private int[] m_textChunks = null;
	
	private static LinkedList<Console> consoleslist = null;
	
	boolean trace = false;
	private boolean m_Logged = false;
	
	private int m_totalRowsNum = -1;
	private int m_lastChunkNum = 0;
	
	
	public boolean wasLogged() {
		return m_Logged;
	}

	public Console (ServiceUIWraper _cbpr, int _port, String sLevel)
	{
		iPort = _port;
		
		consoleslist.add(this);
		m_textChunks = new int[100/Constants.REMOVE_ROWS_AMOUNT_PERCENTAGE];
		for(int i=0; i<m_textChunks.length; i++) {
			m_textChunks[i] = 0;  
			  
		}
			
		m_totalRowsNum = -1;
		
		cbpr = _cbpr;
		frame_disposed = false;
		frame = new JFrame();
		onSetLevel(sLevel);
		sIP = cbpr.getService().getServiceAddressIp();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		//frame.dispose();
		frame.addWindowListener(new WindowAdapter() 
			{
	
				@Override
				public void windowClosing(WindowEvent e) 
				{
					frame_disposed = true;
					try {
						document.remove(0, document.getLength());
					} 
					catch (BadLocationException e2) {
						e2.printStackTrace();
					}
					try 
					{
						System.out.println("windowClosing event invoked...closing socket and setting UI to null");
						onDisconnect();
						if (null != m_socketToServer)
						{
							m_socketToServer.close();
						}
					} 
					catch (IOException e1) 
					{
					}
				}
			}
		);

		textPane = new JTextPane();
		textPane.setEditable(false);
		textPane.setFont(new Font("Courier New", Font.PLAIN, 16));
		textPane.setOpaque(false);
		document = textPane.getStyledDocument();
		
		textField = new JTextField();
		
		
		scrollPane = new JScrollPane(textPane);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		frame.add(textField, BorderLayout.SOUTH);
		frame.add(scrollPane, BorderLayout.CENTER);
		
		frame.getContentPane().setBackground(new Color(50, 50, 50));
		frame.setSize(660,  350);
		frame.setLocationRelativeTo(null);
		//frame.setResizable(false);
		frame.setVisible(true);
		//mc = new MessageConsole(textPane, true);
		//mc.redirectOut();
		//mc.setMessageLines(10000);
		
		try 
		{
			m_socketToServer = new Socket(sIP, iPort);
			
		} 
		catch (IOException e1) 
		{
			e1.printStackTrace();
		}
        new Thread()
        {
        	@Override
            public void run() 
            {
        		//ObjectInputStream inStream = null;
        		SocketLogRecord slr = null;
                try (ObjectInputStream inStream = new ObjectInputStream(m_socketToServer.getInputStream()))
                {
        			//inStream = new ObjectInputStream(m_socketToServer.getInputStream());
        			while (true)
        			{
        				slr =  (SocketLogRecord) inStream.readObject(); 
        				//inStream.reset();
        				//slr =  (SocketLogRecord) inStream.readUnshared();
        				
		    		/*	try {
							Thread.sleep(1000000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}*/
        				
        				//slr.setConsole(console);
        				slr.setConsole(Console.this);
        				m_log_list.offer(slr);        				
        			}
        		} 
                catch (IOException e) 
                {
                	System.out.println("Socket closed...exiting thread");
                	onDisconnect();
                	// print a message to the console
                	slr = new SocketLogRecord("#################### Disconnected from service...####################", Level.SEVERE);
                	slr.setConsole(Console.this);
                	
                	m_log_list.offer(slr);
                	// following code will cause window to close
                	/*
                	SwingUtilities.invokeLater (new Runnable() 
                	{
                     	
                		public void run() 
                		{
                			if(false == frame_disposed)
                			{
                				System.out.println("disposing frame due to socket disconnect");
                				frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
                				//frame.dispose();
                			}
                			else
                			{
                				System.out.println("NOT disposing frame due to socket disconnect");
                			}
                		}
                	});
                	*/
                	// 
        		} 
                catch (ClassNotFoundException e) 
                {
        			e.printStackTrace();
        		} 
            }

        }.start();
	}
	
	private void onDisconnect()
	{
		cbpr.setUiObject(null);
		consoleslist.remove(this);
	}
	public void onSetLevel (String sLevel)
	{
		String sServiceName = null;
		String sServiceIp = null;
		int iPort = 0;
		String sTitle = null;

		if (null != frame)
		{
			sServiceName = cbpr.getService().getServiceName();
			sServiceIp = cbpr.getService().getServiceAddressIp();
			iPort = cbpr.getService().getServiceAddressPort();
			sTitle = String.format("%s on %s:%d - %s", sServiceName, sServiceIp, iPort, sLevel);	
			frame.setTitle(sTitle);
		}
	}
	
	public int getRowCount ()
	{
		return document.getDefaultRootElement().getElementCount();
	}

	public String getTitle () {
		return frame.getTitle();
	}
	
	public int getTextLength()
	{
		return document.getLength();
	}
	
	void onLog(SocketLogRecord slr)
	{
    	String _sRecord;
    	Style _style;
    	Level _level;
    	
    	//JTextPane _console = null;
    	//StyledDocument _document;

    	if (false == frame_disposed)
    	{
			_level = slr.getLevel();
			_sRecord = slr.getRecord();
			//_console = (JTextPane) slr.getConsole();
			//_document = _console.getStyledDocument();
			if (_level.intValue() == Level.SEVERE.intValue())
			{
				_style = styleRed;
			}
			else if (_level.intValue() == Level.WARNING.intValue())
			{
				_style = styleYellow;
			}
			else
			{
				_style = styleGreen;
			}
			//try 
			//{
				insertLine(_sRecord, _style);
				m_Logged = true;
				//textPane.setCaretPosition(document.getLength());
				slr = null;
				//
				//System.out.println("document length: " + document.getLength() + ", rows number: " + document.getDefaultRootElement().getElementCount() + ", m_totalRowsNum: " + m_totalRowsNum);
			//} 
			//catch (BadLocationException e) 
			//{
				//e.printStackTrace();
			//}
    	}
		else
		{
			System.out.println("frame_disposed...ignoring log...");
		}
    	
	}
	
	private void insertLine(String _record, Style _style)
	{
		
		m_totalRowsNum++;
		
		int chunkNum = m_totalRowsNum / m_remove_rows_amount % m_textChunks.length;
		
		/*if ((chunkNum < 0) || (chunkNum >= m_textChunks.length))
		{
			System.out.println("chunkNum: "+chunkNum);
			System.out.println("m_totalRowsNum: "+m_totalRowsNum);
		}*/
		
		//if ((m_textChunks[chunkNum] != 0) && (m_totalRowsNum % m_remove_rows_amount == 0))
		if ((chunkNum != m_lastChunkNum) && (m_textChunks[chunkNum] != 0))
		{
			System.out.println(getTitle() + " - deleting chunk: m_totalRowsNum = " + m_totalRowsNum + ", chunkNum = " + chunkNum);
			try {
				document.remove(0, m_textChunks[chunkNum]);
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			m_textChunks[chunkNum] = 0;
		}
		
		try {
			document.insertString(document.getLength(), _record, _style);
			m_textChunks[chunkNum]  += _record.length();
			m_lastChunkNum = chunkNum;
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
	
	public static void initConsole()
	{
		consoleslist = new LinkedList<Console>();
		m_remove_rows_amount = Constants.MESSAGE_ROWS_LIMIT * Constants.REMOVE_ROWS_AMOUNT_PERCENTAGE / 100;
		
		m_log_list = new LinkedBlockingDeque<SocketLogRecord> ();
		JTextPane jtp = new JTextPane();
		styleRed = jtp.addStyle("styleRed", null);
		StyleConstants.setForeground(styleRed, Color.red);
		StyleConstants.setBold(styleRed, true);
		styleYellow = jtp.addStyle("styleYellow", null);
		StyleConstants.setForeground(styleYellow, Color.yellow);
		StyleConstants.setBold(styleYellow, true);
		styleGreen = jtp.addStyle("styleGreen", null);
		StyleConstants.setForeground(styleGreen, Color.green);
		StyleConstants.setBold(styleGreen, true);

		java.util.Timer jTimer = new java.util.Timer();
		jTimer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					EventQueue.invokeAndWait(new Runnable() {
					    @Override
					    public void run() {
					    	SocketLogRecord slr = null;
					    	//Style _style;
					    	//int iLogsPrinted = 0;
					    	//Level _level;
					    	//JTextPane _console = null;
					    	//StyledDocument _document;
					    	Console _console = null; 
							
							// TODO Auto-generated method stub
					    	for (slr = m_log_list.poll(); (null != slr); slr = m_log_list.poll())
					    	{
					    		_console = (Console) slr.getConsole();
					    		if (null != _console)
					    		{
					    			_console.onLog(slr);
					    		}
					    	}


				    		for(int i = 0; i < consoleslist.size(); i++)
				    	      {
				    			_console = consoleslist.get(i);
				    			if (_console.wasLogged())
				    			{
				    				_console.setCaretPosition();
				    			}
				    	      }
					    		
					    }
					});
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
			
		}, 0, 500);

		javax.swing.Timer timer = new Timer(500, new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e) {
		    	SocketLogRecord slr = null;
		    	//Style _style;
		    	//int iLogsPrinted = 0;
		    	//Level _level;
		    	//JTextPane _console = null;
		    	//StyledDocument _document;
		    	Console _console = null; 
				
				// TODO Auto-generated method stub
		    	for (slr = m_log_list.poll(); (null != slr); slr = m_log_list.poll())
		    	{
		    		_console = (Console) slr.getConsole();
		    		if (null != _console)
		    		{
		    			_console.onLog(slr);
		    		}
		    	}


	    		for(int i = 0; i < consoleslist.size(); i++)
	    	      {
	    			_console = consoleslist.get(i);
	    			if (_console.wasLogged())
	    			{
	    				_console.setCaretPosition();
	    			}
	    	      }
				
			}

		    public void actionPerformedOld(ActionEvent evt) 
		    {    
		    	 new Thread(new Runnable() {
		    		    @Override
		    		    public void run() {
		    	//String _sRecord;
		    	SocketLogRecord slr = null;
		    	//Style _style;
		    	//int iLogsPrinted = 0;
		    	//Level _level;
		    	//JTextPane _console = null;
		    	//StyledDocument _document;
		    	Console _console = null; 
		    	
		    	
		    	System.out.println("m_log_list length: " + m_log_list.size());
		    	for (slr = m_log_list.poll(); (null != slr); slr = m_log_list.poll())
		    	{
		    		_console = (Console) slr.getConsole();
		    		if (null != _console)
		    		{
		    			try {
							EventQueue.invokeAndWait(new LogConsole(_console, slr));
						} catch (InvocationTargetException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		    		}
		    		/*
					_level = slr.getLevel();
					_sRecord = slr.getRecord();
					_console = (JTextPane) slr.getConsole();
					_document = _console.getStyledDocument();
					if (_level.intValue() == Level.SEVERE.intValue())
					{
						_style = styleRed;
					}
					else if (_level.intValue() == Level.WARNING.intValue())
					{
						_style = styleYellow;
					}
					else
					{
						_style = styleGreen;
					}
					try 
					{
						_document.insertString(_document.getLength(), _sRecord, _style);
						_console.setCaretPosition(_document.getLength());
					} 
					catch (BadLocationException e) 
					{
						e.printStackTrace();
					}
		    		*/
		    	}


	    		for(int i = 0; i < consoleslist.size(); i++)
	    	      {
	    			_console = consoleslist.get(i);
	    			if (_console.wasLogged())
	    			{
	    				_console.setCaretPosition();
	    			}
	    	      }
		    	
//		    	Thread stockPicker = new Thread() {
//		    		public void run() {
//		    		try {
//		    		SwingUtilities.invokeAndWait(new Runnable() {
//			    		public void run() {
//			    			Console console = null;
//				    		for(int i = 0; i < consoleslist.size(); i++)
//				    	      {
//				    			console = consoleslist.get(i);
//				    			if (console.wasLogged())
//				    			{
//				    				console.setCaretPosition();
//				    			}
//				    	      }
//				    		}
//				    		});
//		    		}
//		    		catch (Exception e) {
//		    		e.printStackTrace();
//		    		}
//		    		//System.out.println("This will finish after pennyStockPicker thread because InvokeAndWait is block call" + Thread.currentThread());
//		    		}
//		    		};
//		    		stockPicker.start();
		    		    }
		    	  }).start();


		    }

		});
		//timer.start();
	}


	public void setCaretPosition() {
		textPane.setCaretPosition(document.getLength());
		m_Logged = false;
	}

	public void toFront() 
	{
		if (null != frame)
		{
			frame.setState(Frame.NORMAL);
			frame.toFront();
			frame.repaint();
		}
	}
}
