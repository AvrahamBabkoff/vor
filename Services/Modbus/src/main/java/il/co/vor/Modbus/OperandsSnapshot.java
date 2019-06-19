package il.co.vor.Modbus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;


import il.co.vor.ApiObjectsCommon.ApiMultiResultWrapper;
import il.co.vor.ApiObjectsCommon.ApiResult;
import il.co.vor.NPLObjects.OperandSnapshot;
import il.co.vor.common.VosNPLResources;

public class OperandsSnapshot {
	
	private static final Logger _logger = Logger.getLogger(OperandsSnapshot.class.getName());
	
	private volatile String m_operands_snapshot_result = null;
	
	
	private ApiMultiResultWrapper<OperandSnapshot> m_operands_snapshot_api = null;
	private ObjectMapper m_mapper = null;
	private ArrayList<OperandSnapshot> m_operands_snapshot = null;
	private HashMap<Integer, OperandSnapshot> m_operands_snapshot_map = null;
	private ApiResult m_ar = null;
	
	public OperandsSnapshot(int operands_num)
	{
		try {
			
			OperandSnapshot oper_snapshot = null;//new OperandSnapshot();

			m_operands_snapshot_api = ApiMultiResultWrapper.createApiMultiResultWrapper(0, "", oper_snapshot,
					VosNPLResources.API_SANPSHOT_OPERANDS_GET_RESOURCE_NAME);
			m_ar = m_operands_snapshot_api.getApiResult();
			m_operands_snapshot = m_operands_snapshot_api.getApiData()
					.get(VosNPLResources.API_SANPSHOT_OPERANDS_GET_RESOURCE_NAME);
			//m_operands_snapshot.clear();
			m_mapper = new ObjectMapper();
			// m_ref = new TypeReference<ApiMultiResultWrapper<OperandSnapshot>>() { };
			AnnotationIntrospector aiJaxb = new JaxbAnnotationIntrospector(TypeFactory.defaultInstance());
			AnnotationIntrospector aiJackson = new JacksonAnnotationIntrospector();
			// first Jaxb, second Jackson annotations
			m_mapper.setAnnotationIntrospector(AnnotationIntrospector.pair(aiJaxb, aiJackson));

			m_operands_snapshot_map = new HashMap<Integer, OperandSnapshot>();
			
//			for (int i = 0; i < operands_num; i++) {
//				oper_snapshot = new OperandSnapshot();
//				m_operands_snapshot.add(oper_snapshot);
//				
//				
//			}

		} catch (Exception e) {

			_logger.log(Level.SEVERE,
					String.format("Filed to create OperandsSnapshot, abort. Exception: %s", e.getMessage()));

		}
		
	}
	
	public String GetOperandsSnapshotResult() {
		return m_operands_snapshot_result;
	}

	
	public boolean UpdateOperandSnapshot(int operand_id, String operand_name, String operand_val, boolean updateAPI)
	{
		boolean ret = false;
		OperandSnapshot oper_snapshot = null;
		
		try {
			oper_snapshot = m_operands_snapshot_map.get(operand_id);
			if (oper_snapshot == null)
			{
				oper_snapshot = new OperandSnapshot();
				m_operands_snapshot.add(oper_snapshot);
				m_operands_snapshot_map.put(operand_id, oper_snapshot);
			}
	
			oper_snapshot.updateObject(operand_id, operand_name, operand_val);
			
			if (updateAPI) // CDR
			{
				m_operands_snapshot_result = m_mapper.writeValueAsString(m_operands_snapshot_api);
			}
			ret = true;
			
		} catch (Exception e) {
			_logger.log(Level.SEVERE, String.format("Exception: %s", e.getMessage()));
			_logger.log(Level.SEVERE, String.format("Filed to update Operand Snapshot, abort. operand_id: %s operand_name: %s operand_val: %s", String.valueOf(operand_id),operand_name,operand_val ));
		}
		
		return ret;
		
	}
	
	public boolean CreateOperandsSnapshotResult(int code, String message)
	{
		boolean ret = false;
		
		try {
			
			m_ar.setError(code);
			m_ar.setMessage(message);
			
			m_operands_snapshot_result = m_mapper.writeValueAsString(m_operands_snapshot_api);
			
			ret = true;
		
		} catch (Exception e) {
			_logger.log(Level.SEVERE, String.format("Failed to create m_operands_snapshot_result. Exception: %s", e.getMessage()));
		}
		
		return ret;
	}

}
