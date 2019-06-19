package il.co.vor.Modbus;

import java.util.logging.Level;
import java.util.logging.Logger;

import il.co.vor.common.Enums.OperandDataType;

public class OperandWrapper {

	private static Logger _logger = Logger.getLogger(OperandManager.class.getName());
	private OperandDef operand_def = null;
	private OperandLogCounter operand_log_counter = null;
	private OperandVal operand_val = null;
	private String string_log_val = null;
	
	//private boolean m_is_failed = false; // true if could not read parameter value, false otherwise

	
	public OperandWrapper(OperandDef operandDef, OperandLogCounter operandLogCounter, OperandVal operandVal){
		operand_def = operandDef;
		operand_log_counter = operandLogCounter;
		operand_val = operandVal;
	}
	
	/*public boolean IsFailed() {
		return m_is_failed;
	}

	public void setIsFailed(boolean _is_failed) {
		m_is_failed = _is_failed;
	}*/
	
	public int GetOperandID() {
		return operand_def.GetOperandID();
	}
	
	public OperandType GetOperandType() {
		return operand_def.GetOperandType();
	}
	
	public String GetOperandName() {
		return operand_def.GetOperandName();
	}

	public OperandDataType GetOperandDataType() {
		return operand_def.GetOperandDataType();
	}
	
	public void SetOperandVal(OperandVal operand_val) {
		this.operand_val = operand_val;
	}
	
	public void SetNotValid() {
		string_log_val = null;
		operand_val.SetNotValid();
	}
	
	public boolean IsValid()
	{
		return operand_val.IsValid();
	}
	
	public void SetDoubleVal(double double_val) {
		string_log_val = null;
		operand_val.SetDoubleVal(double_val);
	}
	
	public void SetStringVal(String string_val) {
		string_log_val = null;
		operand_val.SetStringVal(string_val);
	}
	
	public double GetDoubleVal()
	{
		return operand_val.GetDoubleVal();
	}
	
	public boolean DataLogOperand(double oldVal, boolean valid_old_val){
		boolean ret = false;
		
		double percentage = -1;
		
		if (operand_def.IsLogByInterval())
		{
			if (operand_log_counter != null)
			{
				ret = operand_log_counter.timeToLog(IsValid());
			}
			else
			{
				_logger.log(Level.SEVERE, String.format("operand_log_counter is null when IsLogByInterval is true. operand id: %s",
						String.valueOf(GetOperandID())));
			}

		}

		if ((!ret) && (IsValid()) && (operand_def.GetOperandDataType() != OperandDataType.ASCII) && (operand_def.IsLogByPercentage()))
		{
			
			if (!valid_old_val) 
			{
				ret = true;
			}
			else
			{
				if (oldVal == 0)
				{
					if(GetDoubleVal() != 0)
					{
						ret = true;
					}
				}
				else
				{
					percentage = 100*(Math.abs( (GetDoubleVal() - oldVal) / oldVal ));
					if (percentage >= operand_def.GetOperandLogByPercentageValue())
					{
						ret = true;
					}
				}
			}
			
		}
		
		return ret;
	}
	
	public String GetLogValAsStr() {

		if ((IsValid()) && (string_log_val == null))
		{
			OperandDataType operand_data_type = GetOperandDataType();
			if (operand_data_type == OperandDataType.ASCII) {
				string_log_val = NPLHelper.GetCSVNormalizedString(operand_val.GetStringVal());
			} else {
				string_log_val = NPLHelper.GetDoubleValAsStr(operand_val.GetDoubleVal());
			}
		}
		
		// return null if not valid
		return string_log_val;
	}

}
