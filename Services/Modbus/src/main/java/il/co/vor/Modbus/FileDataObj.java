package il.co.vor.Modbus;

import java.time.ZonedDateTime;

public abstract class FileDataObj {
	private ZonedDateTime log_data_time = null;
	private boolean m_is_failed = false; // true if could not write parameter value, false otherwise
	
	private int m_OL = 1;
	private int m_curr_OL = 1;
	
	public FileDataObj(int ExportFileMaxInterval, int RefreshDataInterval){
		setOL(ExportFileMaxInterval,RefreshDataInterval);
		initCurrOL();
	}
	
	public ZonedDateTime GetLogDataTime() {
		return log_data_time;
	}
	public void SetLogDataTime(ZonedDateTime log_data_time) {
		this.log_data_time = log_data_time;
	}
	

	public boolean IsFailed() {
		return m_is_failed;
	}

	public void setIsFailed(boolean _is_failed) {
		m_is_failed = _is_failed;
	}

	public int getOL() {
		return m_OL;
	}

	public void setOL(int ExportFileMaxInterval, int RefreshDataInterval) {
		
		m_OL = ((int) ExportFileMaxInterval / RefreshDataInterval);
		if (m_OL < 1)
		{
			m_OL = 1;
		}
	}

	public int getCurrOL() {
		return m_curr_OL;
	}

	public void setCurrOL(int _curr_OL) {
		m_curr_OL = _curr_OL;
	}
	
	public void decreaseCurrOL() {
		m_curr_OL--;
	}
	
	public void initCurrOL() {
		m_curr_OL = m_OL;
	}
	
	
}
