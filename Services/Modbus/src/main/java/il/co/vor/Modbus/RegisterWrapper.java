package il.co.vor.Modbus;
import il.co.vor.DalConfigObjects.Register;

public class RegisterWrapper {
	private int m_operand_id = -1;
	private int m_register_id = -1;
	
	private Register m_register = null;
	private int m_register_chunk_pos = -1;
	private int m_register_chunk_offset = -1;
	
	public RegisterWrapper(int operandID, int registerID, Register regist){
		m_operand_id = operandID;
		m_register_id = registerID;
		
		m_register = regist;
				
	}
	
	public int GetOperandID() {
		return m_operand_id;
	}
	public void SetOperandID(int operand_id) {
		m_operand_id = operand_id;
	}
	public int GetRegisterID() {
		return m_register_id;
	}
	public void SetRegisterID(int register_id) {
		m_register_id = register_id;
	}
	public String GetRegisterName() {
		return m_register.getRegisterName();
	}
	public void SetRegisterName(String register_name) {
		m_register.setRegisterName(register_name);
	}
	public String GetRegisterDescription() {
		return m_register.getRegisterDescription();
	}
	public void SetRegisterDescription(String register_description) {
		m_register.setRegisterDescription(register_description);
	}
	public String GetRegisterRef() {
		return m_register.getRegisterReference();
	}
	public void SetRegisterRef(String register_ref) {
		m_register.setRegisterReference(register_ref);
	}
	public int GetRegisterSize() {
		return m_register.getRegisterSize();
	}
	public void SetRegisterSize(int register_size) {
		m_register.setRegisterSize(register_size);
	}
	public int GetRegisterType() {
		return m_register.getRegisterType();
	}
	public void SetRegisterType(int register_type) {
		m_register.setRegisterType(register_type);
	}
	public int GetChunkPos() {
		return m_register_chunk_pos;
	}
	public void SetChunkPos(int register_chunk) {
		m_register_chunk_pos = register_chunk;
	}
	public int GetOffsetInChunk() {
		return m_register_chunk_offset;
	}
	public void SetOffsetInChunk(int register_chunk_offset) {
		m_register_chunk_offset = register_chunk_offset;
	}
}
