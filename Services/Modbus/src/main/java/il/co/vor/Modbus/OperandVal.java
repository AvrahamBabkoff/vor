package il.co.vor.Modbus;

public class OperandVal {

	private double double_val = -1;
	private String string_val = "";
	
	private boolean valid = false;
	
	public double GetDoubleVal()
	{
		return double_val;
	}
	
	public String GetStringVal()
	{
		return string_val;
	}
	
	public void SetDoubleVal(double double_val) {
		this.double_val = double_val;
		valid = true;
	}
	
	public void SetStringVal(String string_val) {
		this.string_val = string_val;
		valid = true;
	}
	
	public boolean IsValid()
	{
		return valid;
	}
	
	public void SetNotValid()
	{
		valid = false;
	}
}
