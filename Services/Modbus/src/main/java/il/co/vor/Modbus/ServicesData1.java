package il.co.vor.Modbus;
import java.util.List;

public class ServicesData1 {
	private Meta meta;
	public Meta GetMeta() {
		return meta;
	}
	public void SetMeta(Meta meta) {
		this.meta = meta;
	}
	public List<Service1> GetServices() {
		return services;
	}
	public void SetServices(List<Service1> services) {
		this.services = services;
	}
	private List<Service1> services;
}
