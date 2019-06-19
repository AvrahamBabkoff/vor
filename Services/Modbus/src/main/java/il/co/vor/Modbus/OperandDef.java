package il.co.vor.Modbus;
import il.co.vor.DalConfigObjects.Operand;
import il.co.vor.common.Enums.OperandDataType;

public class OperandDef {

    private int m_operand_id = -1;
	private int m_operand_flags = -1;
	
	private Operand m_operand = null;

	OperandDataType data_type = null;

	public OperandDef(Operand oper) {
		m_operand_id = oper.getOperandId();
		m_operand = oper;
		m_operand_flags = NPLHelper.GetFlags(oper.getOperandFlags());
		data_type = OperandDataType.values()[m_operand.getOperandDataType()];

	}

	public int GetOperandID() {
		return m_operand_id;
	}

	public OperandType GetOperandType() {
		return OperandType.fromInt(m_operand.getOperandType());
	}
	
	public OperandDataType GetOperandDataType() {
		return data_type;
	}

	public String GetOperandName() {
		return m_operand.getOperandName();
	}

	public boolean IsLogByInterval()
	{
		boolean ret = NPLHelper.isFlaged(m_operand_flags, OperandFlag.LOG_BY_INTERVAL.GetValue());
		
		return ret;
	}
	
	public boolean IsLogByPercentage()
	{
		boolean ret = NPLHelper.isFlaged(m_operand_flags, OperandFlag.LOG_BY_PERCENTAGE.GetValue());
		
		return ret;
	}
	
	public double GetOperandLogByPercentageValue()
	{
		return m_operand.getOperandLogByPercentageValue();
	}

}
