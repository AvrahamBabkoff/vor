package il.co.vor.Modbus;
import org.json.JSONObject;

import il.co.vor.common.VosConfigResources;

public class CalcOperand1 {
	private int operand_id = -1;
	private int calc_operand_id = -1;
	private String calc_operand_name = "";
	private String calc_operand_description = "";
	private String formula_prefix = "";
	private String formula = "";
	
	
	public CalcOperand1(int operandID, int calcOperandID, String calcOperandName, String calcOperandDescription, String formulaPrefix, String formulaCont){
		operand_id = operandID;
		calc_operand_id = calcOperandID;
		calc_operand_name = calcOperandName;
		calc_operand_description = calcOperandDescription;
		formula_prefix = formulaPrefix;
		formula = formulaCont;
	}
	
	public CalcOperand1(int operandID, JSONObject jsono)
	{
	     this(operandID, jsono.getInt(VosConfigResources.CALC_OPERANDS_PROP_CALC_OPERAND_ID_NAME),jsono.getString(VosConfigResources.CALC_OPERANDS_PROP_CALC_OPERAND_NAME_NAME),JsonHelper.GetJsonNullOrString(jsono,VosConfigResources.CALC_OPERANDS_PROP_CALC_OPERAND_DESCRIPTION_NAME),
	    		 JsonHelper.GetJsonNullOrString(jsono,VosConfigResources.CALC_OPERANDS_PROP_CALC_OPERAND_FORMULA_PREFIX_NAME),JsonHelper.GetJsonNullOrString(jsono,VosConfigResources.CALC_OPERANDS_PROP_CALC_OPERAND_FORMULA_NAME));
	}
	
	public int GetOperandID() {
		return operand_id;
	}
	public void SetOperandID(int operand_id) {
		this.operand_id = operand_id;
	}
	public int GetCalcOperandID() {
		return calc_operand_id;
	}
	public void SetCalcOperandID(int calc_operand_id) {
		this.calc_operand_id = calc_operand_id;
	}
	public String GetCalcOperandName() {
		return calc_operand_name;
	}
	public void SetCalcOperandName(String calc_operand_name) {
		this.calc_operand_name = calc_operand_name;
	}
	public String GetCalcOperandDescription() {
		return calc_operand_description;
	}
	public void SetCalcOperandDescription(String calc_operand_description) {
		this.calc_operand_description = calc_operand_description;
	}
	public String GetFormulaPrefix() {
		return formula_prefix;
	}
	public void SetFormulaPrefix(String formula_prefix) {
		this.formula_prefix = formula_prefix;
	}
	public String GetFormula() {
		return formula;
	}
	public void SetFormula(String formula) {
		this.formula = formula;
	}
	
}
