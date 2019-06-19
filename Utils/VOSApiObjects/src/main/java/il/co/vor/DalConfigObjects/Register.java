package il.co.vor.DalConfigObjects;

import javax.xml.bind.annotation.XmlElement;

import il.co.vor.common.VosConfigResources;

public class Register 
{
	private int m_registerType;
	private String m_registerName;
	private String m_registerDescription;
	private int m_registerSize;
	private String m_registerReference;
	private int m_registerId;
	
	
	@XmlElement(name=VosConfigResources.REGISTERS_PROP_REGISTER_TYPE_NAME)
	public int getRegisterType() 
	{
		return m_registerType;
	}
	
	public void setRegisterType(int registerType) 
	{
		this.m_registerType = registerType;
	}
	
	@XmlElement(name=VosConfigResources.REGISTERS_PROP_REGISTER_NAME_NAME)
	public String getRegisterName() 
	{
		return m_registerName;
	}
	
	public void setRegisterName(String registerName) 
	{
		this.m_registerName = registerName;
	}	
	
	@XmlElement(name=VosConfigResources.REGISTERS_PROP_REGISTER_DESCRIPTION_NAME)
	public String getRegisterDescription() 
	{
		return m_registerDescription;
	}
	
	public void setRegisterDescription(String registerDescription) 
	{
		this.m_registerDescription = registerDescription;
	}
	
	@XmlElement(name=VosConfigResources.REGISTERS_PROP_REGISTER_SIZE_NAME)
	public int getRegisterSize() 
	{
		return m_registerSize;
	}
	
	public void setRegisterSize(int registerSize) 
	{
		this.m_registerSize = registerSize;
	}
	
	@XmlElement(name=VosConfigResources.REGISTERS_PROP_REGISTER_REFERENCE_NAME)
	public String getRegisterReference() 
	{
		return m_registerReference;
	}
	
	public void setRegisterReference(String registerReference) 
	{
		this.m_registerReference = registerReference;
	}
	
	@XmlElement(name=VosConfigResources.REGISTERS_PROP_REGISTER_ID_NAME)
	public int getRegisterId() 
	{
		return m_registerId;
	}
	
	public void setRegisterId(int registerId) 
	{
		this.m_registerId = registerId;
	}	
	
}
