package il.co.vor.DalDataObjects;

import javax.xml.bind.annotation.XmlElement;

import il.co.vor.common.VosDataResources;

public class CalcOperandProperty {
	
	private int m_propertyId;
	private String m_propertyName;
	private int m_propertyType;
	private String m_propertyDescription;
	private String m_propertyVal;
	
	@XmlElement(name=VosDataResources.CALC_OPERANDS_PROPERTIES_PROPERTY_ID_NAME)
	public int getPropertyId() {
		return m_propertyId;
	}
	public void setPropertyId(int m_propertyId) {
		this.m_propertyId = m_propertyId;
	}
	
	@XmlElement(name=VosDataResources.CALC_OPERANDS_PROPERTIES_PROPERTY_NAME_NAME)
	public String getPropertyName() {
		return m_propertyName;
	}
	public void setPropertyName(String m_propertyName) {
		this.m_propertyName = m_propertyName;
	}
	
	@XmlElement(name=VosDataResources.CALC_OPERANDS_PROPERTIES_PROPERTY_TYPE_NAME)
	public int getPropertyType() {
		return m_propertyType;
	}
	public void setPropertyType(int m_propertyType) {
		this.m_propertyType = m_propertyType;
	}
	
	@XmlElement(name=VosDataResources.CALC_OPERANDS_PROPERTIES_PROPERTY_DESCRIPTION_NAME)
	public String getPropertyDescription() {
		return m_propertyDescription;
	}
	public void setPropertyDescription(String m_propertyDescription) {
		this.m_propertyDescription = m_propertyDescription;
	}
	
	@XmlElement(name=VosDataResources.CALC_OPERANDS_PROPERTIES_PROPERTY_VAL_NAME)
	public String getPropertyVal() {
		return m_propertyVal;
	}
	public void setPropertyVal(String m_propertyVal) {
		this.m_propertyVal = m_propertyVal;
	}

}
