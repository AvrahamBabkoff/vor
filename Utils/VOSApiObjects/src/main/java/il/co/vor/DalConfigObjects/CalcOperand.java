package il.co.vor.DalConfigObjects;

import javax.xml.bind.annotation.XmlElement;

import il.co.vor.common.VosConfigResources;

public class CalcOperand 
{
	private String m_calcOperandFormulaPrefix;
	private String m_calcOperandFormula;
	private String m_calcOperandName;
	private String m_calcOperandDescription;
	private String m_calcOperandUpdate;
	private int m_calcOperandId;
	private int m_calcOperandOrdinal;
	
	@XmlElement(name=VosConfigResources.CALC_OPERANDS_PROP_CALC_OPERAND_FORMULA_PREFIX_NAME)
	public String getCalcOperandFormulaPrefix() 
	{
		return m_calcOperandFormulaPrefix;
	}
	
	public void setCalcOperandFormulaPrefix(String calcOperandFormulaPrefix) 
	{
		this.m_calcOperandFormulaPrefix = calcOperandFormulaPrefix;
	}
	
	@XmlElement(name=VosConfigResources.CALC_OPERANDS_PROP_CALC_OPERAND_FORMULA_NAME)
	public String getCalcOperandFormula() 
	{
		return m_calcOperandFormula;
	}
	
	public void setCalcOperandFormula(String calcOperandFormula) 
	{
		this.m_calcOperandFormula = calcOperandFormula;
	}
	
	@XmlElement(name=VosConfigResources.CALC_OPERANDS_PROP_CALC_OPERAND_NAME_NAME)
	public String getCalcOperandName() 
	{
		return m_calcOperandName;
	}
	
	public void setCalcOperandName(String calcOperandName) 
	{
		this.m_calcOperandName = calcOperandName;
	}
	
	@XmlElement(name=VosConfigResources.CALC_OPERANDS_PROP_CALC_OPERAND_DESCRIPTION_NAME)
	public String getCalcOperandDescription() 
	{
		return m_calcOperandDescription;
	}
	
	public void setCalcOperandDescription(String calcOperandDescription) 
	{
		this.m_calcOperandDescription = calcOperandDescription;
	}
	
	@XmlElement(name=VosConfigResources.CALC_OPERANDS_PROP_CALC_OPERAND_ID_NAME)
	public int getCalcOperandId() 
	{
		return m_calcOperandId;
	}
	
	public void setCalcOperandId(int calcOperandId) 
	{
		this.m_calcOperandId = calcOperandId;
	}

	@XmlElement(name=VosConfigResources.CALC_OPERANDS_PROP_CALC_OPERAND_ORDINAL_NAME)
	public int getCalcOperandOrdinal() {
		return m_calcOperandOrdinal;
	}

	public void setCalcOperandOrdinal(int m_calcOperandOrdinal) {
		this.m_calcOperandOrdinal = m_calcOperandOrdinal;
	}

	@XmlElement(name=VosConfigResources.CALC_OPERANDS_PROP_CALC_OPERAND_UPDATE_NAME)
	public String getCalcOperandUpdate() {
		return m_calcOperandUpdate;
	}

	public void setCalcOperandUpdate(String m_calcOperandUpdate) {
		this.m_calcOperandUpdate = m_calcOperandUpdate;
	}		
}
