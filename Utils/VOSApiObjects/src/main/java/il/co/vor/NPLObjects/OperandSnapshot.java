package il.co.vor.NPLObjects;

import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import il.co.vor.common.VosConfigResources;
import il.co.vor.common.VosNPLResources;

@JsonPropertyOrder({ VosConfigResources.OPERANDS_PROP_OPERAND_ID_NAME, VosConfigResources.OPERANDS_PROP_OPERAND_NAME_NAME, VosNPLResources.OPERANDS_PROP_OPERAND_VALUE_NAME })
public class OperandSnapshot 
{
	private int m_operand_id;
	private String m_operand_name;
	private String m_operand_value;
	
	@XmlElement(name=VosConfigResources.OPERANDS_PROP_OPERAND_ID_NAME)
	public int getOperandId() 
	{
		return m_operand_id;
	}

	public void setOperandId(int _operand_id) 
	{
		this.m_operand_id = _operand_id;
	}

	@XmlElement(name=VosConfigResources.OPERANDS_PROP_OPERAND_NAME_NAME)
	public String getOperandName() 
	{
		return m_operand_name;
	}

	public void setOperandName(String _operand_name) 
	{
		this.m_operand_name = _operand_name;
	}

	@XmlElement(name=VosNPLResources.OPERANDS_PROP_OPERAND_VALUE_NAME)
	public String getOperandValue() 
	{
		return m_operand_value;
	}

	public void setOperandValue(String _operand_value) 
	{
		this.m_operand_value = _operand_value;
	}

	public void updateObject(int _operand_id, String _operand_name, String _operand_value)
	{
		m_operand_id = _operand_id;
		m_operand_name = _operand_name;
		m_operand_value = _operand_value;
	}
}