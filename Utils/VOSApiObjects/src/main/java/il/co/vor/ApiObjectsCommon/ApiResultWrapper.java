package il.co.vor.ApiObjectsCommon;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import il.co.vor.common.Constants;

@XmlRootElement//(name = Constants.JSON_ROOT_DATA_PROP_NAME)
public class ApiResultWrapper<RESULTOBJECT>
{
	private ApiResult m_ApiResult;
	private ApiData<RESULTOBJECT> m_ApiData;
	

	/*
	private List<RESULTOBJECT> m_ResultSet;
*/
	@XmlElement(name=Constants.JSON_ROOT_META_PROP_NAME)
	public ApiResult getApiResult() {
		return m_ApiResult;
	}

	public void setApiResult(ApiResult m_ApiResult) {
		this.m_ApiResult = m_ApiResult;
	}

	@XmlElement(name=Constants.JSON_ROOT_DATA_PROP_NAME)
	public ApiData<RESULTOBJECT> getApiData() {
		return m_ApiData;
	}

	public void setApiData(ApiData<RESULTOBJECT> m_ApiData) {
		this.m_ApiData = m_ApiData;
	}

/*
	@XmlElement(name=VosConfigResources.SERVICES_NAME)
	public List<RESULTOBJECT> getResultSet() {
		return m_ResultSet;
	}

	public void setResultSet(List<RESULTOBJECT> m_ResultSet) {
		this.m_ResultSet = m_ResultSet;
	}
*/	
	
}
