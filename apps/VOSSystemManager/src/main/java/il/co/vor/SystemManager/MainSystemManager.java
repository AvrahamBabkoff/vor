package il.co.vor.SystemManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import il.co.vor.ApiObjectsCommon.ApiMultiResultWrapper;
import il.co.vor.ApiObjectsCommon.LoggerInfo;
import il.co.vor.DalConfigClient.DalConfigClient;
import il.co.vor.DalConfigObjects.Service;
import il.co.vor.DalConfigObjects.Site;
import il.co.vor.DalDataClient.DalDataClient;
import il.co.vor.DalReportClient.DalReportClient;
import il.co.vor.FTPTMClient.FTPTMClient;
import il.co.vor.NPLClient.NPLClient;
import il.co.vor.VOSNetClient.GenClient;
import il.co.vor.VOSNetClient.GenClient.LoggerClient;
import il.co.vor.common.Enums;
import il.co.vor.common.VosConfigResources;
import il.co.vor.common.VosGenResources;
import il.co.vor.defines.Constants;
import il.co.vor.utilities.PropertyFileReader;
 
/**
 *
 *
 **/

public class MainSystemManager extends JPanel 
{

	private static final long serialVersionUID = 1L;


    JPopupMenu popupMenu;
    JButton _changeSystemButton;

    ServicesTableModel m_model = null;
    JTable m_table = null;
    volatile List<ServiceUIWraper> m_list = null;
    HashMap<Integer,String> m_siteNamesMap = null;
    
    static ScheduledThreadPoolExecutor m_executer = null;
    static JPanel m_myPanel;
	static JTextField m_ipField;
	static NumericTextField m_numberTextBox;
	
	JFrame m_frame;
	
	JScrollPane m_logScrollPane = null;
	final AtomicInteger m_selectedRow=new AtomicInteger(-1);
    final AtomicInteger m_selectedCol=new AtomicInteger(-1);
    static MainSystemManager m_instance = null;
    
    JMenuItem m_menuItemShowLogs = null;
    JMenu m_menuChangeLogLevel = null;
    JMenuItem m_menuItemStart = null;
    JMenuItem m_menuItemShutdown = null;
    
    Object m_syncObj = new Object();
    		
    public MainSystemManager(JFrame _frame) 
    {
        super(new BorderLayout());

        m_frame = _frame;
        CreateUIElements();
    }
 
 
    private void createPopupMenu()
    {
    	ActionListener menuShutdownListener; 
    	ActionListener menuStartListener;
    	ActionListener menuShowLogsListner;
    	ActionListener menuChangeLogLevelListner;
        JMenuItem inner_item;
    	
        menuShutdownListener = new ActionListener() 
        {
            public void actionPerformed(ActionEvent event) 
            {
            	ServiceUIWraper _cbpr = (ServiceUIWraper)m_list.get(m_table.getSelectedRow());
            	m_executer.schedule(new Runnable() {
                    public void run() {
                    	shutdownService(_cbpr);
                    	SwingUtilities.invokeLater (new Runnable() 
                        {
                        	
                            public void run() 
                            {
                            	m_model.fireTableCellUpdated(m_table.getSelectedRow(), m_model.GetStatusColumnNumber());
                            }
                        });
                    }
                }, 0, TimeUnit.SECONDS);
              
            }
        };
        
        menuStartListener = new ActionListener() 
        {
            public void actionPerformed(ActionEvent event) 
            {
            	ServiceUIWraper _cbpr = (ServiceUIWraper)m_list.get(m_table.getSelectedRow());
            	m_executer.schedule(new Runnable() {
                    public void run() {
                    	StartService(_cbpr);
                    	
                    	// remove remark when start service will work
                    	/*SwingUtilities.invokeLater (new Runnable() 
                        {
                        	
                            public void run() 
                            {
                            	m_model.fireTableCellUpdated(m_table.getSelectedRow(), m_model.GetStatusColumnNumber());
                            }
                        });*/
                    }
                }, 0, TimeUnit.SECONDS);
              
            }
        };
        
        // create callback for show log console command
        menuShowLogsListner = new ActionListener() 
        {
            public void actionPerformed(ActionEvent event) 
            {
            	displayConsole();
            }
        };
        
       

        // create callback for change log level command
        menuChangeLogLevelListner = new ActionListener() 
        {
            public void actionPerformed(ActionEvent event) 
            {
            	final String newLogLevel = event.getActionCommand();
            	ServiceUIWraper _cbpr = (ServiceUIWraper)m_list.get(m_table.getSelectedRow());
            	m_executer.schedule(new Runnable() {
                    public void run() {
                    	changeLogLevel(_cbpr,newLogLevel);
                    	SwingUtilities.invokeLater (new Runnable() 
                        {
                        	
                            public void run() 
                            {
                            	// after change level, console should brought to front in UI
                            	final Console console = (Console) _cbpr.getUiObject();
                            	if (null != console)
                		    	{
                		    		console.onSetLevel(_cbpr.getLogLevel());
                		    		console.toFront();
                		    	}
                            	m_model.fireTableCellUpdated(m_table.getSelectedRow(), m_model.GetLogLevelColumnNumber());
                            }
                        });
                    }
                }, 0, TimeUnit.SECONDS);
            	
            }
        };

        popupMenu = new JPopupMenu();
        m_menuItemShowLogs = new JMenuItem("Show Logs");
        popupMenu.add(m_menuItemShowLogs);
        m_menuItemShowLogs.addActionListener(menuShowLogsListner);
        
        m_menuChangeLogLevel = new JMenu("Change Log Level");        
        inner_item = new JMenuItem("ALL");
        m_menuChangeLogLevel.add(inner_item);
        inner_item.addActionListener(menuChangeLogLevelListner);
        inner_item = new JMenuItem("WARNING");
        m_menuChangeLogLevel.add(inner_item);
        inner_item.addActionListener(menuChangeLogLevelListner);
        inner_item = new JMenuItem("SEVERE");
        m_menuChangeLogLevel.add(inner_item);
        inner_item.addActionListener(menuChangeLogLevelListner);
        popupMenu.add(m_menuChangeLogLevel);
        //jm.setEnabled(false);
        popupMenu.add(new JPopupMenu.Separator());
        m_menuItemStart = new JMenuItem("Start");
        popupMenu.add(m_menuItemStart);
        m_menuItemStart.addActionListener(menuStartListener);
        m_menuItemShutdown = new JMenuItem("Shutdown");
        popupMenu.add(m_menuItemShutdown);
        m_menuItemShutdown.addActionListener(menuShutdownListener);
        
    }
    
    private void disableActions(boolean _activeService)
    {
		m_menuItemShowLogs.setEnabled(_activeService);
	    m_menuChangeLogLevel.setEnabled(_activeService);
	    m_menuItemStart.setEnabled(!_activeService);
	    m_menuItemShutdown.setEnabled(_activeService);
    }
    
    @SuppressWarnings("serial")
	private  void CreateUIElements() {
    	
    	createPopupMenu();
    	
/*    	m_executer = (ScheduledThreadPoolExecutor) Executors
				.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() * 2);
*/    	
    	m_model = new ServicesTableModel();
    	
    	m_table = new JTable(m_model){
            @Override
            public TableCellRenderer getCellRenderer(int row, int column)
            {
                int modelColumn = convertColumnIndexToModel(column);

                if (modelColumn == m_model.GetStatusColumnNumber())
                {
                    return getDefaultRenderer( Icon.class );
                }
                else
                    return super.getCellRenderer(row, column);
            }
        };
        
        m_table.addMouseListener( new MouseAdapter()
        {
            // display popup menu
            public void mouseReleased (MouseEvent me) 
            {          
            	
            	int r = m_table.rowAtPoint(me.getPoint());
                if (r >= 0 && r < m_table.getRowCount()) {
                    m_table.setRowSelectionInterval(r, r);
                } else {
                    m_table.clearSelection();
                }
                if (SwingUtilities.isRightMouseButton(me))
                {
                	System.out.println("in isRightMouseButton");
	                int row = m_table.rowAtPoint(me.getPoint());
	                int col = m_table.columnAtPoint(me.getPoint());
	                if (row >= 0 && col >= 0) {
	                	disableActions(((ServiceUIWraper)m_list.get(m_table.getSelectedRow())).isActive());
	                	popupMenu.show(m_table, me.getX(), me.getY());
	
	                }
                }
            }
        }); 

        m_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        _changeSystemButton = new JButton("Connect to system...", null);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(_changeSystemButton);
        
        _changeSystemButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
            	onChangeSystem();
            }
        });
        add(buttonPanel, BorderLayout.PAGE_START);
        
        m_logScrollPane = new JScrollPane(m_table);
        add(m_logScrollPane, BorderLayout.CENTER);
        
        fancyTable();
        
    }
    
    private void createList(List<Service> _services,List<Site> _sites)
    {

    	synchronized(m_syncObj)
    	{
    		m_table.clearSelection();
	    	
	        m_list = new ArrayList<ServiceUIWraper>();
	        m_siteNamesMap = new HashMap<Integer,String>();
	        for(Site s : _sites){
	        	m_siteNamesMap.put(s.getSiteId() , s.getSiteName());
	        }
	        
	        populateList(_services);
	        
	        tableCoumnAdjuster();
    	}
    }
    
    private void fancyTable()
    {
    	int gapWidth = 10;
        int gapHeight = 4;
        //DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        
    	m_table.setRowHeight(50);
        JTableHeader th = m_table.getTableHeader();
        th.setPreferredSize(new Dimension(10000, 50));
        th.setFont(new Font("Arial", Font.PLAIN, 15));
       
        //Border padding = BorderFactory.createEmptyBorder(0, 10, 0, 10);
        //headerRenderer.setBackground(new Color(232, 242, 254));
        //headerRenderer.setHorizontalAlignment(JLabel.CENTER);
        //headerRenderer.setBorder(padding);
        //for (int i = 0; i < m_table.getModel().getColumnCount(); i++) {
        //    m_table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
       // }
        m_table.setFont(new Font("Arial", Font.PLAIN, 15));
        
        m_table.setIntercellSpacing(new Dimension(gapWidth, gapHeight));
        
    }
    
    private void tableCoumnAdjuster()
    {
    	//m_table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    	TableColumnAdjuster tca = new TableColumnAdjuster(m_table);
    	tca.setColumnHeaderIncluded(true);
    	tca.setColumnDataIncluded(true);
    	tca.adjustColumns();

    }
    
    private void displayConsole()
    {
    	GenClient gc = null;
    	LoggerInfo li = null; 
    	ServiceUIWraper _cbpr;
    	ApiMultiResultWrapper<LoggerInfo> liResult;
    	Console console = null;
    	
    	_cbpr = m_list.get(m_table.getSelectedRow());

    	console = (Console) _cbpr.getUiObject();
    	
    	if (null != console)
    	{
    		console.toFront();
    	}    	
    	else
    	{
    		gc = _cbpr.getGenClient();
	    	if (null != gc)
	    	{
	    		liResult = gc.getLoggerClient().getLoggerInfoObject();
	    		li = liResult.getApiData().get(VosGenResources.LOG_INFO_NAME).get(0);
	    		console = new Console(_cbpr, li.getPort(), li.getLevel());
	    		_cbpr.setUiObject(console);
	    	}        	
    	}    	
    }


    private void StartService(ServiceUIWraper _cbpr)
    {

    	Service _service;
    	String _cmd;
    	String _cmdTemplate;
    	
    	try 
    	{
    		
    		_service = _cbpr.getService();
    		
    		_cmdTemplate = PropertyFileReader.getProperty(Constants.PROP_NAME_START_SERVICE_COMMAND, Constants.START_SERVICE_DEFAULT_COMMAND);
    		_cmd = String.format(_cmdTemplate,_service.getServiceAddressIp(),_service.getServiceName());
    		
    		Runtime rt = Runtime.getRuntime();
    		Process proc = rt.exec(_cmd);

    		BufferedReader stdInput = new BufferedReader(new 
    		     InputStreamReader(proc.getInputStream()));

    		BufferedReader stdError = new BufferedReader(new 
    		     InputStreamReader(proc.getErrorStream()));

    		// read the output from the command
    		System.out.println(String.format("Start service command: %s",_cmd));
    		String s = null;
    		while ((s = stdInput.readLine()) != null) {
    		    System.out.println(s);
    		}

    		// read any errors from the attempted command
    		System.out.println("Here is the standard error of the command (if any):\n");
    		while ((s = stdError.readLine()) != null) {
    		    System.out.println(s);
    		}
    		

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
    private void changeLogLevel(ServiceUIWraper _cbpr,String sLevel)
    {
    	GenClient gc = null;
    	LoggerInfo li = null; 
    	ApiMultiResultWrapper<LoggerInfo> liResult;


		gc = _cbpr.getGenClient();
    	if (null != gc)
    	{
    		liResult = gc.getLoggerClient().setLogLevel(sLevel);
    
        	if (null != liResult)
    		{
        		li = liResult.getApiData().get(VosGenResources.LOG_INFO_NAME).get(0);
        		_cbpr.setActive(true);
	    		_cbpr.setLogLevel(li.getLevel());
	    		
    		}
    	}  
    	
    }

    
    
    private void shutdownService(ServiceUIWraper _cbpr)
    {
    	GenClient gc = null;
    	int result = -1;
    	String sAlert = "";
    	Object[] options = {"OK", "Cancel"};
    	
    	sAlert = "Confirm shutdown of service " + _cbpr.getService().getServiceName();

    	// display confirm dialog with "cancel" button the default
    	result = JOptionPane.showOptionDialog(this,
    			sAlert,
    			"Shutdown Service",
    		    JOptionPane.OK_CANCEL_OPTION,
    		    JOptionPane.WARNING_MESSAGE,
    		    null,
    		    options,
    		    options[1]);
    	
    	if (result == JOptionPane.OK_OPTION)
    	{
    		gc = _cbpr.getGenClient();    	
    		gc.getStopServiceClient().doShutdown();
    	}
    	
    	_cbpr.setActive(false);
    	_cbpr.setLogLevel("");
    }
    
    private void populateList(List<Service> _services)
    {
    	String sIP;
    	int iPort;
    	ServiceUIWraper serviceObj;
    	Enums.ServiceType st = Enums.ServiceType.UNKNOWN;
    	GenClient gc = null;
    	for(Service _service: _services)
    	{
	    	sIP = _service.getServiceAddressIp();
	    	iPort = _service.getServiceAddressPort();
	    	st = Enums.ServiceType.values()[_service.getServiceType()];
	    	
	    	switch(st)
	    	{
	    		case NPL:
	    			gc = new NPLClient(sIP, iPort);
	    			break;
	    		case FTPTM:
	    			gc = new FTPTMClient(sIP, iPort);
	    			break;
	    		case DAL_CONFIG:
	    			gc = new DalConfigClient(sIP, iPort);
	    			break;
	    		case DAL_DATA:
	    			gc = new DalDataClient(sIP, iPort);
	    			break;
	    		case DAL_REPORT:
	    			gc = new DalReportClient(sIP, iPort);
	    			break;
	    		default:
	    			break;
	    	}
	    	serviceObj = new ServiceUIWraper(_service, gc);
	    	serviceObj.setSiteName(m_siteNamesMap.get(_service.getSiteId()));
    		m_list.add(serviceObj);
    	}
    	m_model.updateServices(m_list);
        
    }
    
    private void updateServicesData()
    {

    	GenClient gc = null;
    	LoggerInfo li = null; 
    	ApiMultiResultWrapper<LoggerInfo> liResult;
    	LoggerClient lc;
    	long lStart, lEnd;
    	
    	lStart =  System.currentTimeMillis();
    	
    	System.out.println("updateServicesData waitting!!!!");
    	synchronized(m_syncObj)
    	{
    		System.out.println("updateServicesData starting!!!!");
    	
			for(ServiceUIWraper _serviceObj: m_list)
			{
				try {
					gc = _serviceObj.getGenClient();
			    	if (null != gc)
			    	{
			    		lc = gc.getLoggerClient();
			    		if (lc != null)
			    		{
			    			liResult = lc.getLoggerInfoObject();
		    	    		if (liResult != null)
		    	    		{
		    	    			li = liResult.getApiData().get(VosGenResources.LOG_INFO_NAME).get(0);
		    	    			_serviceObj.setLogLevel(li.getLevel());
		    	    			_serviceObj.setActive(true);
		    	    		}
		    	    		else
		    	    		{
		    	    			_serviceObj.setActive(false);
		    	    			_serviceObj.setLogLevel("");
		    	    		}
			    		}
			    		//Thread.sleep(10000);
			    	}    	
				} catch (Exception e) {
					e.printStackTrace();
				}
		    	
			}
    	}
    	lEnd  = System.currentTimeMillis();
    	System.out.println(String.format("updateServicesData. Duration: %d milliseconds", (lEnd - lStart)));
    }
    
    private static boolean onChangeSystem()
    {
    	int iResult;
    	int iPort = 0;
    	String sIP = "";
    	boolean bRes = false;
    	
    	while(true)
    	{
    		bRes = false;
	    	iResult = JOptionPane.showConfirmDialog(null, m_myPanel, 
		               "IP and port of DAL Config service", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
	    	
	    	if (iResult == JOptionPane.OK_OPTION) 
			{
				sIP = m_ipField.getText();
				try
				{
					iPort = Integer.parseInt(m_numberTextBox.getText());
					
					System.out.println("changeSystem waitting!!!!");
					if (null != m_instance)
					{
						m_instance.setWaitCursor();
					}
	            	//synchronized(m_syncObj)
	            	//{
	            		System.out.println("changeSystem started!!!!");
	            		bRes = doChangeSystem(sIP,iPort);
	
	            		System.out.println("changeSystem finished!!!!");
	            	//}
					if (null != m_instance)
					{
						m_instance.setDefaultCursor();
					}
					if (true == bRes)
					{
						break;
					}
				}
				catch(NumberFormatException e)
				{
					System.out.println("onChangeSystem exception!!!!");
				}
			}
	    	else
	    	{
	    		break;
	    	}
    	}
    	return bRes;
    }
    
    void onChangeSystemComplete(String sTitle)
    {
    	m_frame.setTitle(sTitle);
    	m_frame.pack();

    }
    private static boolean doChangeSystem(String _ip, int _port)
    {
    	ApiMultiResultWrapper<Service> amrwrservice = null;
		ApiMultiResultWrapper<Site> amrwrsite = null;
		ArrayList<Service> _services = null;
		ArrayList<Site> _sites = null;
		JFrame _frame = null;
		
    	
    	String sTitle = "";
    	DalConfigClient dcc = null;

    	boolean bRes = false;

		dcc = new DalConfigClient(_ip, _port);
    	  
    	  
  		bRes = ((amrwrservice = dcc.getServices().getServicesObject(-1)) != null);

		
		if (false != bRes)
		{
			if (amrwrservice.getApiResult().getError() != 0 ||
			   (amrwrservice.getApiData() == null) ||
			   (_services = amrwrservice.getApiData().get(VosConfigResources.API_SERVICES_GET_RESOURCE_NAME)) == null)
			{
				bRes = false;
			}
		}
		
		amrwrsite = dcc.getSites().getSitesObject();
		bRes = (amrwrsite != null);

		if (false != bRes)
		{
			if (amrwrsite.getApiResult().getError() != 0 ||
			   (amrwrsite.getApiData() == null) ||
			   (_sites = amrwrsite.getApiData().get(VosConfigResources.API_SITES_GET_RESOURCE_NAME)) == null)
			{
				bRes = false;
			}
		}
		
		if (false != bRes)
		{
			//Add content to the window.
	    	if (m_instance == null)
	    	{
	        	m_executer = (ScheduledThreadPoolExecutor) Executors
	    				.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() * 2);

	        	_frame = new JFrame("System Manager");
	        	_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    		Console.initConsole();
	    		m_instance = new MainSystemManager(_frame);
	    		_frame.add(m_instance);
		        _frame.setPreferredSize(new Dimension(900, 600));
		        //Display the window.
		        _frame.setVisible(true);
	    		
	    	}
	        //Create and set up the window.
	    	sTitle = String.format("System Manager  -  IP: %s      PORT: %s", _ip, String.valueOf(_port));
/*	    	sTitle = String.format("System Manager  -  IP: %s      PORT: %s", _ip, String.valueOf(_port));
	    	_frame.setTitle(sTitle);
*/	        //JFrame frame = new JFrame("System Manager");
	    	
    		m_instance.createList(_services,_sites);
	    	m_instance.onChangeSystemComplete(sTitle);
		}
		return bRes;
    }
    
    private void onRefresh()
    {
		Console console;
    	System.out.println("Starting refresh");
    	int r = m_table.getSelectedRow();
    	System.out.println("calling refresh");
    	m_model.fireTableDataChanged();
    	System.out.println("finished refresh");
    	if (r >= 0)
    	{
    		m_table.changeSelection(r,0, false, false);
    	}
    	
		for(ServiceUIWraper _serviceObj: m_list)
		{
			try {
				console = (Console)_serviceObj.getUiObject();
				if (null != console)
				{
					System.out.println("console " + console.getTitle() + ": Rows = " + console.getRowCount() + ", Length = " + console.getTextLength());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
	    	
		}
    	
    	
    }
    
    private static void createAndShowGUI() 
    {
    	
		boolean bRes = false;

		try
		{
			UIManager.put ("swing.boldMetal", Boolean.FALSE);
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception e)
		{
			
		}
		
		m_ipField = new JTextField(12);

		m_numberTextBox = new NumericTextField(5, 0xffff);
		m_myPanel = new JPanel();

		m_myPanel.add(new JLabel("IP:"));
		m_myPanel.add(m_ipField);
		m_myPanel.add(Box.createHorizontalStrut(15));
		m_myPanel.add(new JLabel("Port:"));
		m_myPanel.add(m_numberTextBox);

        bRes = onChangeSystem();
        if (bRes)
        {
	        m_executer.scheduleAtFixedRate(new Runnable() {
                public void run() {
                	m_instance.updateServicesData();
                	SwingUtilities.invokeLater (new Runnable() 
                    {
                    	
                        public void run() 
                        {
                        	m_instance.onRefresh();
                        }
                    });
                }
            }, 0, Constants.REFRESH_INTERVAL,TimeUnit.SECONDS);
            
            m_executer.schedule(new Runnable() {
                public void run() {
                	m_instance.updateServicesData();
                	SwingUtilities.invokeLater (new Runnable() 
                    {
                    	
                        public void run() 
                        {
                        	m_instance.onRefresh();
/*                            	System.out.println("Starting refresh");
                            	int r = m_table.getSelectedRow();
                            	System.out.println("calling refresh");
                            	m_model.fireTableDataChanged();
                            	System.out.println("finished refresh");
                            	if (r >= 0)
                            	{
                            		m_table.changeSelection(r,0, false, false);
                            	}
*/                            }
                    });
                }
            }, 0, TimeUnit.SECONDS);
	        
	        //m_frame.setPreferredSize(new Dimension(900, 600));
	        //Display the window.
	        //m_frame.pack();
	        //m_frame.setVisible(true);
        } 

		if (false == bRes)
		{
			System.exit(0);
		}
		
    }
 
    public void setWaitCursor() {
        if (m_frame != null) {
            RootPaneContainer root = (RootPaneContainer) m_frame.getRootPane().getTopLevelAncestor();
            root.getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            root.getGlassPane().setVisible(true);
        }
    }

    public void setDefaultCursor() {
        if (m_frame != null) {
            RootPaneContainer root = (RootPaneContainer) m_frame.getRootPane().getTopLevelAncestor();
            root.getGlassPane().setCursor(Cursor.getDefaultCursor());
        }
    }

    

    public static void main (String[] args) 
    {
		
        SwingUtilities.invokeLater (new Runnable() 
        {
        	
            public void run() 
            {
                createAndShowGUI ();
            }
        });				
    }
}