package il.co.vor.Modbus;


public class OperandLogCounter {
	
	private int m_OL = 1;
	private int m_curr_OL = 0; //1;
	
	public OperandLogCounter(int refresh_data_interval, double operand_log_by_interval_value) {

		setOL(refresh_data_interval,operand_log_by_interval_value);
		//initCurrOL();
		//m_curr_OL++;
	}

	/*public int getOL() {
		return m_OL;
	}*/

	private void setOL(int refresh_data_interval, double operand_log_by_interval_value) 
	{
		
		m_OL = ((int) operand_log_by_interval_value / refresh_data_interval);
		if (m_OL < 1)
		{
			m_OL = 1;
		}
	}

	/*public int getCurrOL() {
		return m_curr_OL;
	}*/

	public boolean timeToLog(boolean is_valid)
	{
		boolean ret = false;
		
		decreaseCurrOL();
		if (is_valid)
		{
			if (m_curr_OL <= 0)
			{
				ret = true;
				initCurrOL();
			}
		}
		
		return ret;
	}
	/*public void setCurrOL(int _curr_OL) {
		m_curr_OL = _curr_OL;
	}*/
	
	private void decreaseCurrOL() {
		m_curr_OL--;
	}
	
	private void initCurrOL() {
		m_curr_OL = m_OL;
	}
}
