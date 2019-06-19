package il.co.vor.SystemManager;

import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

import il.co.vor.DalConfigObjects.Service;
import il.co.vor.common.Enums.ServiceType;
import il.co.vor.defines.Constants;

@SuppressWarnings("serial")
public class ServicesTableModel extends AbstractTableModel {

	private List<ServiceUIWraper> m_servicesData = null;
	private String[] m_serviceTypeNames = null;
	private String[] m_columnNames = null;
	private Icon m_errorIcon =  null;
	private Icon m_successIcon =  null;
	    
	public ServicesTableModel() {
		m_errorIcon = new ImageIcon(getClass().getResource(Constants.ERROR_IMAGE_PATH));
		m_successIcon = new ImageIcon(getClass().getResource(Constants.SUCCESS_IMAGE_PATH));
		
		m_columnNames = new String[9];
		m_columnNames[Constants.SERVICE_ID_COLUMN_NUMBER] = Constants.SERVICE_ID_COLUMN_NAME;
		m_columnNames[Constants.SERVICE_TYPE_NAME_COLUMN_NUMBER] = Constants.SERVICE_TYPE_NAME_COLUMN_NAME; 
		m_columnNames[Constants.SERVICE_NAME_COLUMN_NUMBER] = Constants.SERVICE_NAME_COLUMN_NAME;
		m_columnNames[Constants.SERVICE_DESCRIPTION_COLUMN_NUMBER] = Constants.SERVICE_DESCRIPTION_COLUMN_NAME;
		m_columnNames[Constants.SERVICE_ADDRESS_IP_COLUMN_NUMBER] = Constants.SERVICE_ADDRESS_IP_COLUMN_NAME;
		m_columnNames[Constants.SERVICE_PORT_COLUMN_NUMBER] = Constants.SERVICE_PORT_COLUMN_NAME;
		m_columnNames[Constants.SERVICE_STATUS_COLUMN_NUMBER] = Constants.SERVICE_STATUS_COLUMN_NAME;
		m_columnNames[Constants.SERVICE_LOG_LEVEL_NUMBER] = Constants.SERVICE_LOG_LEVEL_NAME;
		m_columnNames[Constants.SITE_NAME_COLUMN_NUMBER] = Constants.SITE_NAME_COLUMN_NAME;
		
		m_serviceTypeNames = new String[ServiceType.values().length];

		  int index = 0;

		  for (ServiceType type : ServiceType.values()) {
			  m_serviceTypeNames[index++] = type.toString();
		  }
	}
	
	public int GetStatusColumnNumber() {
		return Constants.SERVICE_STATUS_COLUMN_NUMBER;
	}
	
	public int GetLogLevelColumnNumber() {
		return Constants.SERVICE_LOG_LEVEL_NUMBER;
	}
	
	public void updateServices(List<ServiceUIWraper> _servicesData) {
		m_servicesData = _servicesData;
	}
	
	@Override
    public String getColumnName(int column) {
        return m_columnNames[column];
    }

    @Override
    public int getColumnCount() {
        return m_columnNames.length;
    }

    @Override
    public int getRowCount() {
        return ((null != m_servicesData) ? m_servicesData.size() : 0);
    }

    @Override
    public Object getValueAt(int row, int column) {
        Object serviceAttribute = null;

        if (null != m_servicesData)
        {
	        ServiceUIWraper serviceObjectWrapper = m_servicesData.get(row);
	        Service serviceObject = serviceObjectWrapper.getService();
	        switch(column) {
	            case Constants.SERVICE_ID_COLUMN_NUMBER: serviceAttribute = String.valueOf(serviceObject.getServiceId()); break;          
	            case Constants.SERVICE_TYPE_NAME_COLUMN_NUMBER: serviceAttribute = m_serviceTypeNames[serviceObject.getServiceType()]; break;
	            case Constants.SERVICE_NAME_COLUMN_NUMBER: serviceAttribute = serviceObject.getServiceName(); break;
	            case Constants.SERVICE_DESCRIPTION_COLUMN_NUMBER: serviceAttribute = serviceObject.getServiceDescription(); break;
	            case Constants.SERVICE_ADDRESS_IP_COLUMN_NUMBER: serviceAttribute = serviceObject.getServiceAddressIp(); break;
	            case Constants.SERVICE_PORT_COLUMN_NUMBER: serviceAttribute = String.valueOf(serviceObject.getServiceAddressPort()); break;
	            case Constants.SERVICE_STATUS_COLUMN_NUMBER: serviceAttribute = (serviceObjectWrapper.isActive()) ? m_successIcon: m_errorIcon; break;
	            case Constants.SERVICE_LOG_LEVEL_NUMBER: serviceAttribute = serviceObjectWrapper.getLogLevel(); break;
	            case Constants.SITE_NAME_COLUMN_NUMBER: serviceAttribute = serviceObjectWrapper.getSiteName(); break;
	            
	            default: break;
	        }
        }
        return serviceAttribute;
    }

}
