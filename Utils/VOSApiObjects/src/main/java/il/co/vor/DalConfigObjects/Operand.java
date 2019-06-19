package il.co.vor.DalConfigObjects;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import il.co.vor.common.VosConfigResources;

@XmlRootElement
public class Operand 
{
	private String m_operandFormat;
	private Meter m_meter;
	private String m_operandDescription;
	private CalcOperand m_calcOperand;
	private int m_operandDataType;
	private int m_operandType;
	private int m_physicalUnitId;
	private ArrayList<Integer> m_operandFlags;
	private double m_operandLogByPercentageValue;
	private int m_operandId;
	private String m_operandName;//private "operand_name": "flow_op",
	private double m_operandLogByIntervalValue;
	private int m_equipmentId;
	private Register m_register;
	
	@XmlElement(name=VosConfigResources.OPERANDS_PROP_OPERAND_FORMAT_NAME)
	public String getOperandFormat() 
	{
		return m_operandFormat;
	}
	
	public void setOperandFormat(String operandFormat) 
	{
		this.m_operandFormat = operandFormat;
	}
	
	@XmlElement(name=VosConfigResources.METER_OBJECT_NAME)
	public Meter getMeter() 
	{
		return m_meter;
	}
	
	public void setMeter(Meter meter) 
	{
		this.m_meter = meter;
		if (meter != null)
		{
			meter.setOperand(this);
		}
	}
	
	@XmlElement(name=VosConfigResources.OPERANDS_PROP_OPERAND_DESCRIPTION_NAME)
	public String getOperandDescription() 
	{
		return m_operandDescription;
	}
	
	public void setOperandDescription(String operandDescription) 
	{
		this.m_operandDescription = operandDescription;
	}
	
	@XmlElement(name=VosConfigResources.CALC_OPERAND_OBJECT_NAME)
	public CalcOperand getCalcOperand() 
	{
		return m_calcOperand;
	}
	
	public void setCalcOperand(CalcOperand calcOperand) 
	{
		this.m_calcOperand = calcOperand;
	}
	
	@XmlElement(name=VosConfigResources.OPERANDS_PROP_OPERAND_DATA_TYPE_NAME)
	public int getOperandDataType() 
	{
		return m_operandDataType;
	}
	
	public void setOperandDataType(int operandDataType) 
	{
		this.m_operandDataType = operandDataType;
	}
	
	@XmlElement(name=VosConfigResources.OPERANDS_PROP_OPERAND_TYPE_NAME)
	public int getOperandType() 
	{
		return m_operandType;
	}
	
	public void setOperandType(int operandType) 
	{
		this.m_operandType = operandType;
	}
	
	@XmlElement(name=VosConfigResources.PHYSICAL_UNITS_PROP_PHYSICAL_UNIT_ID_NAME)
	public int getPhysicalUnitId() 
	{
		return m_physicalUnitId;
	}
	
	public void setPhysicalUnitId(int physicalUnitId) 
	{
		this.m_physicalUnitId = physicalUnitId;
	}
	
	@XmlElement(name=VosConfigResources.OPERANDS_PROP_OPERAND_FLAGS_NAME)
	public ArrayList<Integer> getOperandFlags() 
	{
		return m_operandFlags;
	}
	
	public void setOperandFlags(ArrayList<Integer> operandFlags) 
	{
		this.m_operandFlags = operandFlags;
	}
	
	@XmlElement(name=VosConfigResources.OPERANDS_PROP_OPERAND_LOG_BY_PERCENTAGE_VALUE_NAME)
	public double getOperandLogByPercentageValue() 
	{
		return m_operandLogByPercentageValue;
	}
	
	public void setOperandLogByPercentageValue(double operandLogByPercentageValue) 
	{
		this.m_operandLogByPercentageValue = operandLogByPercentageValue;
	}
	
	@XmlElement(name=VosConfigResources.OPERANDS_PROP_OPERAND_ID_NAME)
	public int getOperandId() 
	{
		return m_operandId;
	}
		
	public void setOperandId(int operandId) 
	{
		this.m_operandId = operandId;
	}
	
	@XmlElement(name=VosConfigResources.OPERANDS_PROP_OPERAND_NAME_NAME)
	public String getOperandName() 
	{
		return m_operandName;
	}
	
	public void setOperandName(String operandName) 
	{
		this.m_operandName = operandName;
	}
	
	@XmlElement(name=VosConfigResources.OPERANDS_PROP_OPERAND_LOG_BY_INTERVAL_VALUE_NAME)
	public double getOperandLogByIntervalValue() 
	{
		return m_operandLogByIntervalValue;
	}
	
	public void setOperandLogByIntervalValue(double operandLogByIntervalValue) 
	{
		this.m_operandLogByIntervalValue = operandLogByIntervalValue;
	}
	
	@XmlElement(name=VosConfigResources.EQUIPMENTS_PROP_EQUIPMENT_ID)
	public int getEquipmentId() 
	{
		return m_equipmentId;
	}
	
	public void setEquipmentId(int equipmentId) 
	{
		this.m_equipmentId = equipmentId;
	}
	
	@XmlElement(name=VosConfigResources.REGISTER_OBJECT_NAME)
	public Register getRegister() 
	{
		return m_register;
	}
	
	public void setRegister(Register register) 
	{
		this.m_register = register;
	}


}
