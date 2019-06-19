package il.co.vor.Modbus;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.logging.Level;

import il.co.vor.DalConfigObjects.CalcOperand;
import il.co.vor.common.Enums.OperandDataType;
import net.wimpi.modbus.util.ModbusUtil;

public class CalcOperandWrapper {
	private int m_operand_id = -1;
	private int m_calcOperand_id = -1;
	
	private CalcOperand m_calc_operand = null;
	private ICalcOperand m_calc_operand_instance = null;
	
	public CalcOperandWrapper(int operandID, int calcOperandID, CalcOperand calcOper){
		m_operand_id = operandID;
		m_calcOperand_id = calcOperandID;
		
		m_calc_operand = calcOper;
				
	}
	
	public int GetOrdinal() {
		return m_calc_operand.getCalcOperandOrdinal();
	}
	
	public int GetOperandID() {
		return m_operand_id;
	}
	
	public void SetOperandID(int operand_id) {
		m_operand_id = operand_id;
	}
	
	public int GetCalcOperandID() {
		return m_calcOperand_id;
	}
	
	public void SetCalcOperandID(int calc_operand_id) {
		m_calcOperand_id = calc_operand_id;
	}
	
	public void SetCalcOperandInstance(ICalcOperand calc_operand_instance)
	{
		m_calc_operand_instance = calc_operand_instance;
	}
	
	public boolean GetVal(OperandWrapper ow, long ulReadCount, ZonedDateTime dtReadTime) {
		boolean ret = false;
		String str_val = "";
		
		ow.SetNotValid();
		
		if (m_calc_operand_instance != null)
		{
			str_val = m_calc_operand_instance.Calculate(ulReadCount, dtReadTime);
		
			if (ow.GetOperandDataType() == OperandDataType.ASCII) {
				ow.SetStringVal(str_val);
			} else {
				ow.SetDoubleVal(Double.parseDouble(str_val));
			}
			ret = true;
		}
		
		return ret;
	}
	
}
