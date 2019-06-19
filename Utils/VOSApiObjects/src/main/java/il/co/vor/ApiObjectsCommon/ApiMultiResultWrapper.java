package il.co.vor.ApiObjectsCommon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import il.co.vor.common.Constants;

@XmlRootElement
public class ApiMultiResultWrapper<RESULTOBJECT> 
{
	private ApiResult m_ApiResult;
	private Map<String, ArrayList<RESULTOBJECT>> m_ApiData;
	
	@XmlElement(name=Constants.JSON_ROOT_META_PROP_NAME)
	public ApiResult getApiResult() {
		return m_ApiResult;
	}

	public void setApiResult(ApiResult m_ApiResult) 
	{
		this.m_ApiResult = m_ApiResult;
	}

	@XmlElement(name=Constants.JSON_ROOT_DATA_PROP_NAME)
	public  Map<String, ArrayList<RESULTOBJECT>> getApiData() 
	{
		return m_ApiData;
	}

	public void setApiData(Map<String, ArrayList<RESULTOBJECT>> m_ApiData) 
	{
		this.m_ApiData = m_ApiData;
	}
	
	public static <RESULTOBJECT>ApiMultiResultWrapper<RESULTOBJECT> createApiMultiResultWrapper(int result_error, String result_message, RESULTOBJECT result_object, String result_object_name)
	{
		ApiResult ar;
		ArrayList<RESULTOBJECT> ll;
		ApiMultiResultWrapper<RESULTOBJECT> oRes;
		Map<String, ArrayList<RESULTOBJECT>> mMap;

		ar = new ApiResult();
		ar.setError(result_error);
		ar.setMessage(result_message);
		
		ll = new ArrayList<RESULTOBJECT>();
		if(null != result_object)
		{
			ll.add(result_object);
		}
		
		mMap = new HashMap<String, ArrayList<RESULTOBJECT>>();
		mMap.put(result_object_name, ll);
		
		oRes = new ApiMultiResultWrapper<RESULTOBJECT>();
		oRes.setApiResult(ar);
		oRes.setApiData(mMap);
		
		return oRes;
	}
}
