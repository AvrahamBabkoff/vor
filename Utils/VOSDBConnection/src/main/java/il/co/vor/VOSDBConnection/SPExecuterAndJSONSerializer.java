package il.co.vor.VOSDBConnection;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
//import java.sql.Types;
import java.sql.SQLException;
import java.sql.Timestamp;
//import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import il.co.vor.common.Constants;
import il.co.vor.utilities.PropertyFileReader;

public class SPExecuterAndJSONSerializer 
{
	private String m_sSP;
	private Object[] m_Parameters;
	private String[] m_ResultSetNames;
	private String[] m_RootObjectProperties;
	private boolean m_ResultSetAsObject;
	private StringBuilder m_sbStatement = null;
	private int m_iParameterIndex;
	List<Object[]> m_ParamatersList;
	List<String> m_ResultSetNamesList;
 	
	private HashMap<String, String[]> m_NestedObjects; 
	private static final Logger _logger = Logger.getLogger(SPExecuterAndJSONSerializer.class.getName());
	
	public SPExecuterAndJSONSerializer ()
	{
		m_sSP = null;
		m_Parameters = null;
		m_ResultSetNames = null;
		m_RootObjectProperties = null;
		m_NestedObjects = new HashMap<String, String[]>();
		m_ResultSetAsObject = PropertyFileReader.getPropertyAsBoolean(Constants.PROP_NAME_DB_SQL_ResultSetAsObject);
		m_ParamatersList = new LinkedList<Object[]>();
		m_ResultSetNamesList = new LinkedList<String>();
	}
	
	public SPExecuterAndJSONSerializer appendStatement(String sStatement) {
		if (null == m_sbStatement) {
			m_sbStatement = new StringBuilder();
			//m_sbStatement.append('{');
		} else {
			m_sbStatement.append(';');	
		}
		m_sbStatement.append(sStatement);
		
		return this;
	}

	
	
	public SPExecuterAndJSONSerializer setSP (String sSP)
	{
		m_sSP = sSP;
		appendStatement(sSP);
		return this;
	}
	
	public SPExecuterAndJSONSerializer setParameters(Object...parameters)
	{
		m_Parameters = parameters;
		m_ParamatersList.add(parameters);
		return this;
	}
	
	
	public SPExecuterAndJSONSerializer setResultSetNames(String...resultSetNames)
	{
		int i;
		m_ResultSetNames = resultSetNames;
		
		for (i = 0; i < resultSetNames.length; i++) {
			m_ResultSetNamesList.add(resultSetNames[i]);
		}
		return this;
	}
	
	public SPExecuterAndJSONSerializer appendResultSetName(String resultSetName)
	{
		m_ResultSetNamesList.add(resultSetName);
		return this;
	}

	public SPExecuterAndJSONSerializer setRootObjectProperties(String...properties)
	{
		m_RootObjectProperties = properties;
		return this;
	}
	
	public SPExecuterAndJSONSerializer addObject(String sObject, String...properties)
	{
		m_NestedObjects.put(sObject, properties);
		return this;
	}

	private JSONArray resultsetToJSONArray(ResultSet rs)
	{
		JSONArray jsonResultSet = null;
		Object o = null;;
		try
		{
			jsonResultSet = new JSONArray();

		    ResultSetMetaData rsmd = rs.getMetaData();
		    int numColumns = rsmd.getColumnCount();
		    while (rs.next()) 
		    {
	
		        JSONObject obj = new JSONObject();
	
		        for (int i = 1; i < numColumns + 1; i++) 
		        {
		            String column_name = rsmd.getColumnName(i);
		            int iType = rsmd.getColumnType(i);
		            o = rs.getObject(i);
	
		            if(rs.wasNull() || (null == o))
		            {
		            	obj.put(column_name, JSONObject.NULL);
		            }
		            else
		            {
		            	if (iType == java.sql.Types.TIMESTAMP)
		            	{
		            		//o = new Date(((Timestamp)o).getTime());
		            		o = new Long(((Timestamp)o).getTime());
		            	}
		            	obj.put(column_name, o);
		            }
		            
		        }
	
		        jsonResultSet.put(obj);
		    }
		}
		catch(SQLException | JSONException  ex)
		{
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			ex.printStackTrace(pw);
			_logger.log(Level.SEVERE, "resultsetToJSONArray exception: " + ex.getMessage());
			_logger.log(Level.SEVERE, "trace: " + sw.toString());
			
		}
	    return jsonResultSet;		
	}
	
	private void ThrowError(String sError) throws Exception
	{
		throw new Exception(sError);
	}
	
	public JSONObject ExecuteAndSerializeAsJSONObject(BatchExecuter be) {
    	ResultSet rs = null;
    	ResultSet rs1 = null;
    	int iErrorCode = 0;
    	String sErrorDescription = "";
    	JSONArray ja = null;
		JSONObject jsonResult = null;
		JSONObject jsonMeta = null;
		JSONObject jsonData = null;
    	Object o;
    	int i = 0;

		jsonResult = new JSONObject();
		jsonMeta = new JSONObject();
		jsonData = new JSONObject();
		jsonResult.put(Constants.JSON_ROOT_META_PROP_NAME, jsonMeta);
		jsonResult.put(Constants.JSON_ROOT_DATA_PROP_NAME, jsonData);
		try
		{
	    	if ((0 == m_sbStatement.length())  || 0 == m_ResultSetNamesList.size())
	    	{
	    		throw new Exception("Failed to execute: illegel parameters");
	    	}
	    	if (null == be)
	    	{
	    		be = new BatchExecuter ();
	    	}
	    	m_sSP = m_sbStatement.toString();
	    	be.setStatement(m_sSP);
			if (m_ParamatersList.size() > 0)
			{
				i = 1;
				for(Object[] params: m_ParamatersList) {
					for(Object obj: params) {
						be.setObject(i++, obj);
					}
		      }
				
			}
			rs = be.executeQuery(false);
			
			for (String resultSetName: m_ResultSetNamesList)
			{
				
				if (m_ResultSetAsObject)
				{
					if (rs.next())
					{
						o = rs.getObject(1);
					}
					else
					{
						o = null;
					}
				}
				else
				{
					o = rs;
				}
								
				
				if ((o != null) && (o instanceof ResultSet))
				{
					rs1 = (ResultSet)o;
					ja = resultsetToJSONArray(rs1);
					rs1.close();
					jsonData.put(resultSetName, ja);
				}
				else
				{
					ThrowError ("Missing result set for " + resultSetName);
				}
				if (m_ResultSetAsObject == false)
				{
					rs = be.getResultSet();
				}
			}
			
		}
		catch (SQLException e) 
		{
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			iErrorCode = -1;
			sErrorDescription = e.getMessage();
			e.printStackTrace(pw);
			_logger.log(Level.SEVERE, "ExecuteAndSerializeAsJSONString SQLException: " + sErrorDescription);
			_logger.log(Level.SEVERE, "trace: " + sw.toString());

			jsonResult.put(Constants.JSON_ROOT_DATA_PROP_NAME, JSONObject.NULL);
		}
		catch (Exception e) 
		{
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			iErrorCode = -1;
			sErrorDescription = e.getMessage();			
			e.printStackTrace(pw);
			_logger.log(Level.SEVERE, "resultsetToJSONArray Exception: " + sErrorDescription);
			_logger.log(Level.SEVERE, "trace: " + sw.toString());
			
			jsonResult.put(Constants.JSON_ROOT_DATA_PROP_NAME, JSONObject.NULL);
		}
    	finally
    	{
    		if (null != be)
    		{
    			try 
    			{
    				if (0 == iErrorCode)
    				{
    					be.commit();
    				}
    				else
    				{
    					be.rollback();
    				}
        			if(null != rs) {
        				rs.close();
        			}
				} 
    			catch (Exception e) 
    			{
    				StringWriter sw = new StringWriter();
    				PrintWriter pw = new PrintWriter(sw);
    				e.printStackTrace(pw);
    				_logger.log(Level.SEVERE, "commit/rollback threw an exception: " + e.getMessage());
    				_logger.log(Level.SEVERE, "trace: " + sw.toString());
				}
    			be.close();
    		}
    	}
		jsonMeta.put(Constants.JSON_META_ERROR_PROP_NAME, iErrorCode);
		jsonMeta.put(Constants.JSON_META_MESSAGE_PROP_NAME, sErrorDescription);
		
		return jsonResult;
	}
	
	public String ExecuteAndSerializeAsJSONString(BatchExecuter be)
	{
		JSONObject jsonResult = null;
		String stringResult = ""; 
		jsonResult = ExecuteAndSerializeAsJSONObject(be);
		
		if (null != jsonResult) {
			stringResult = jsonResult.toString();
		}
		return stringResult;		
	}



	public String ExecuteAndSerializeNestedAsJSONString()
	{
		Connection conn = null;
    	PreparedStatement pstmt = null;
    	ResultSet rs = null;
    	ResultSet rs1 = null;
    	int iErrorCode = 0;
    	String sErrorDescription = "";
    	JSONArray ja = null;
		JSONObject jsonResult = null;
		JSONObject jsonMeta = null;
		JSONObject jsonData = null;
    	Object o = null;
    	int i = 0;

		jsonResult = new JSONObject();
		jsonMeta = new JSONObject();
		jsonData = new JSONObject();
		jsonResult.put(Constants.JSON_ROOT_META_PROP_NAME, jsonMeta);
		jsonResult.put(Constants.JSON_ROOT_DATA_PROP_NAME, jsonData);
		try
		{
	    	if (null == m_sSP || null == m_ResultSetNames || m_ResultSetNames.length != 1)
	    	{
	    		throw new Exception("Failed to execute: illegel parameters");
	    	}
			conn = DBConnectionPool.getConnection();
			pstmt = conn.prepareStatement(m_sSP);
			for (i = 0; i < m_Parameters.length; i++)
			{
				pstmt.setObject(i + 1, m_Parameters[i]);
			}
			rs = pstmt.executeQuery();
			
			
			if (m_ResultSetAsObject)
			{
				if (rs.next())
				{
					o = rs.getObject(1);
				}
				else
				{
					o = null;
				}
			}
			else
			{
				o = rs;
			}
			if ((o != null) && (o instanceof ResultSet))
			{
				rs1 = (ResultSet)o;
				ja = nestedResultsetToJSONArray(rs1);
				jsonData.put(m_ResultSetNames[0], ja);
			}
			else
			{
				throw new Exception("Missing result set for " + m_ResultSetNames[i]);
			}
				
			
		}
		catch (SQLException e) 
		{
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);

			iErrorCode = -1;
			sErrorDescription = e.getMessage();
			jsonResult.put(Constants.JSON_ROOT_DATA_PROP_NAME, JSONObject.NULL);
			e.printStackTrace(pw);
			_logger.log(Level.SEVERE, "commit/rollback threw an exception: " + sErrorDescription);
			_logger.log(Level.SEVERE, "trace: " + sw.toString());
		}
		catch (Exception e) 
		{
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			iErrorCode = -1;
			sErrorDescription = e.getMessage();			
			jsonResult.put(Constants.JSON_ROOT_DATA_PROP_NAME, JSONObject.NULL);
			e.printStackTrace(pw);
			_logger.log(Level.SEVERE, "commit/rollback threw an exception: " + sErrorDescription);
			_logger.log(Level.SEVERE, "trace: " + sw.toString());
		}
    	finally
    	{
    		if (null != conn)
    		{
    			try 
    			{
    				if (0 == iErrorCode)
    				{
    					conn.commit();
    				}
    				else
    				{
    					conn.rollback();
    				}
					conn.close();
				} 
    			catch (SQLException e) 
    			{
    				StringWriter sw = new StringWriter();
    				PrintWriter pw = new PrintWriter(sw);
    				e.printStackTrace(pw);
    				_logger.log(Level.SEVERE, "commit/rollback threw an exception: " + e.getMessage());
    				_logger.log(Level.SEVERE, "trace: " + sw.toString());
				}
    		}
    	}
		jsonMeta.put(Constants.JSON_META_ERROR_PROP_NAME, iErrorCode);
		jsonMeta.put(Constants.JSON_META_MESSAGE_PROP_NAME, sErrorDescription);
		
		return jsonResult.toString();		
	}

	private JSONArray nestedResultsetToJSONArray(ResultSet rs)
	{
		JSONArray jsonResultSet = null;
		Object o = null;
		int i = 0;
		int j = 0;
		JSONObject obj = null;
		JSONObject objNested = null;
		try
		{
			jsonResultSet = new JSONArray();

		    while (rs.next()) 
		    {
	
		        obj = new JSONObject();
		        // add root properties		        
		        for (i = 0; i < m_RootObjectProperties.length; i++) 
		        {
		            o = rs.getObject(m_RootObjectProperties[i]);
	
		            if(rs.wasNull() || (null == o))
		            {
		            	obj.put(m_RootObjectProperties[i], JSONObject.NULL);
		            }
		            else
		            {
		            	obj.put(m_RootObjectProperties[i], o);
		            }
		            
		        }
		        // iterate over all nested objects:
		        for (Entry<String, String[]> entry : m_NestedObjects.entrySet()) 
		        {
		            String sObjectName = entry.getKey();
		            String[] objectProperties = entry.getValue();
		            // get first property and check fo null. If it is null, add name with null
		            o = rs.getObject(objectProperties[0]);
		        	
		            if(rs.wasNull() || (null == o))
		            {
		            	obj.put(sObjectName, JSONObject.NULL);
		            }
		            else
		            {
		            	objNested = new JSONObject();
		            	objNested.put(objectProperties[0], o);
		            	// iterate through the rest of this nested object properties
		            	for (j = 1; j < objectProperties.length; j++)
		            	{
		            		
				            o = rs.getObject(objectProperties[j]);
				        	
				            if(rs.wasNull() || (null == o))
				            {
				            	objNested.put(objectProperties[j], JSONObject.NULL);
				            }
				            else
				            {
				            	objNested.put(objectProperties[j], o);
				            }
		            	}
		            	obj.put(sObjectName, objNested);
		            }
		            
		        }
		        jsonResultSet.put(obj);
		    }
		}
		catch(SQLException | JSONException  ex)
		{
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			ex.printStackTrace(pw);
			_logger.log(Level.SEVERE, "nestedResultsetToJSONArray exception: " + ex.getMessage());
			_logger.log(Level.SEVERE, "trace: " + sw.toString());
			
		}
	    return jsonResultSet;		
	}

}
