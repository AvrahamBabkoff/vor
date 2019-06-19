import java.util.logging.Level;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

import il.co.vor.ApiObjectsCommon.ApiMultiResultWrapper;
import il.co.vor.DalConfigClient.DalConfigClient;
import il.co.vor.DalConfigObjects.Service;

import il.co.vor.NPLClient.NPLClient;
import il.co.vor.NPLObjects.OperandSnapshot;
import il.co.vor.common.Constants;
import il.co.vor.common.VosConfigResources;
import il.co.vor.utilities.PropertyFileReader;

public class NPLServiceTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
/*		ApiMultiResultWrapper<Service> amrwrservice = null;
		Service service = null;
		String sServiceName = "";
		
		DalConfigClient dcc = DalConfigClient.getInstance();
		sServiceName = PropertyFileReader.getProperty(Constants.PROP_NAME_SERVICE_NAME);
		
		amrwrservice = dcc.getServices().getServiceObject(sServiceName);
		if (amrwrservice != null) { // read service data
			service =  amrwrservice.getApiData().get(VosConfigResources.API_SERVICES_GET_RESOURCE_NAME).get(0);
		}*/
		
		//NPLClient _npl_c = new NPLClient(service.getServiceAddressIp(),service.getServiceAddressPort());
		ObjectMapper m_mapper = null;
		ApiMultiResultWrapper<OperandSnapshot> snapshot_api = null;
		
		try {
			
		
			m_mapper = new ObjectMapper();
			// m_ref = new TypeReference<ApiMultiResultWrapper<OperandSnapshot>>() { };
			AnnotationIntrospector aiJaxb = new JaxbAnnotationIntrospector(TypeFactory.defaultInstance());
			AnnotationIntrospector aiJackson = new JacksonAnnotationIntrospector();
			// first Jaxb, second Jackson annotations
			m_mapper.setAnnotationIntrospector(AnnotationIntrospector.pair(aiJaxb, aiJackson));
			
			NPLClient _npl_c = new NPLClient("localhost",8090);
			
			snapshot_api = _npl_c.getOperands().GetOperandsSnapshot();
			String m_operands_snapshot_result = m_mapper.writeValueAsString(snapshot_api);
			
			System.out.println( m_operands_snapshot_result);
		} catch (Exception e) {
			System.out.println(String.format("Failed to create m_operands_snapshot_result. Exception: %s", e.getMessage()));
		}
		
	}

}
