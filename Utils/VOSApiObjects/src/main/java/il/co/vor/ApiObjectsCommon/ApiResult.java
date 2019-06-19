package il.co.vor.ApiObjectsCommon;

import javax.xml.bind.annotation.XmlRootElement;
import il.co.vor.common.Constants;
@XmlRootElement(name = Constants.JSON_ROOT_META_PROP_NAME)
public class ApiResult 
{
	private int m_iError;
	private String m_sMessage;
	
	public int getError() {
		return m_iError;
	}
	public void setError(int m_iError) {
		this.m_iError = m_iError;
	}
	public String getMessage() {
		return m_sMessage;
	}
	public void setMessage(String m_sMessage) {
		this.m_sMessage = m_sMessage;
	}
	
}
