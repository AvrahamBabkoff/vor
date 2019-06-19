package il.co.vor.ApiObjectsCommon;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;

import il.co.vor.common.VosConfigResources;

public class ApiData<RESULTOBJECT> 
{
	private ArrayList<RESULTOBJECT> m_ResultSet;

	//@XmlElement(name=VosConfigResources.SERVICES_NAME)
	@XmlElement(name=VosConfigResources.OPERANDS_NAME)
	public ArrayList<RESULTOBJECT> getResultSet() {
		return m_ResultSet;
	}

	public void setResultSet(ArrayList<RESULTOBJECT> m_ResultSet) {
		this.m_ResultSet = m_ResultSet;
	}
}
