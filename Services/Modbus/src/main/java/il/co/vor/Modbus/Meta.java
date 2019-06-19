package il.co.vor.Modbus;

public class Meta {
	private int error;
	public int GetError() {
		return error;
	}
	public void SetError(int error) {
		this.error = error;
	}
	public String GetMessage() {
		return message;
	}
	public void SetMessage(String message) {
		this.message = message;
	}
	private String message;
}
