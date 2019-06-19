package il.co.vor.DalConfigObjects;

import javax.xml.bind.annotation.XmlElement;

import il.co.vor.common.VosConfigResources;

public class Meter 
{
	private String m_meterDescription;
	private int m_meterTempType;
	private String m_meterName;
	private int m_meterType;
	private int m_meterId;
	private int m_maxConsumption;
	private Operand m_operand;
	
	@XmlElement(name=VosConfigResources.METERS_PROP_METER_DESCRIPTION_NAME)
	public String getMeterDescription() 
	{
		return m_meterDescription;
	}
	
	public void setMeterDescription(String meterDescription) 
	{
		this.m_meterDescription = meterDescription;
	}
	
	@XmlElement(name=VosConfigResources.METERS_PROP_METER_TEMP_TYPE_NAME)
	public int getMeterTempType() 
	{
		return m_meterTempType;
	}
		
	public void setMeterTempType(int meterTempType) 
	{
		this.m_meterTempType = meterTempType;
	}
	
	@XmlElement(name=VosConfigResources.METERS_PROP_METER_NAME_NAME)
	public String getMeterName() 
	{
		return m_meterName;
	}
	
	public void setMeterName(String meterName) 
	{
		this.m_meterName = meterName;
	}
	
	@XmlElement(name=VosConfigResources.METERS_PROP_METER_TYPE_NAME)
	public int getMeterType() 
	{
		return m_meterType;
	}
	
	public void setMeterType(int meterType) 
	{
		this.m_meterType = meterType;
	}
	
	@XmlElement(name=VosConfigResources.METERS_PROP_METER_ID_NAME)
	public int getMeterId() 
	{
		return m_meterId;
	}
	
	public void setMeterId(int meterId) 
	{
		this.m_meterId = meterId;
	}
	
	@XmlElement(name=VosConfigResources.METERS_PROP_MAX_CONSUMPTION_NAME)
	public int getMaxConsumption() 
	{
		return m_maxConsumption;
	}
	
	public void setMaxConsumption(int maxConsumption) 
	{
		this.m_maxConsumption = maxConsumption;
	}
		
	public Operand getOperand() 
	{
		return m_operand;
	}

	public void setOperand(Operand m_operand) 
	{
		this.m_operand = m_operand;
	}


}
