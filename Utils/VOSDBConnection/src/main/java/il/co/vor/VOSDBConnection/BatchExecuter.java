package il.co.vor.VOSDBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;



public class BatchExecuter 
{
	private static final Logger logger = Logger.getLogger(BatchExecuter.class.getName());
	private PreparedStatement m_pstmt = null;
	
	public BatchExecuter ()
	{
		m_pstmt = null;
	}
	
	public void setStatement(String sStatement) throws Exception
	{
		Connection conn = null;
		
		if (null != m_pstmt)
		{
			conn = m_pstmt.getConnection();
		}
		else
		{
			conn = DBConnectionPool.getConnection();
		}
		
		try 
		{
			m_pstmt = conn.prepareStatement(sStatement);
		} 
		catch (Exception e) 
		{
			logger.log(Level.SEVERE, String.format("BatchExecuter threw an exception %s", e.getMessage()));
			//m_pstmt = null;
			throw new Exception(e.getMessage());
		}
	}
	
	public void addBatch (Object...parameters) throws Exception
	{
		int i = 0;
		if (null != m_pstmt)
		{
			try 
			{
				for (i = 0; i < parameters.length; i++)
				{
						m_pstmt.setObject(i + 1, parameters[i]);
				}
				m_pstmt.addBatch();
			}
			catch(Exception e)
			{
				logger.log(Level.SEVERE, String.format("addBatch threw an exception %s", e.getMessage()));
				//m_pstmt = null;
				throw e;				
			}
		}
	}
	
	public void addParameters (Object...parameters) throws Exception
	{
		if (null != m_pstmt)
		{
			setObjects(false, parameters);
		}
	}

	public void setObjects (boolean bAddBatch, Object [] parameters) throws Exception
	{
		int i = 0;
		if (null != m_pstmt)
		{
			try 
			{
				for (i = 0; i < parameters.length; i++)
				{
						m_pstmt.setObject(i + 1, parameters[i]);
				}
				if (true == bAddBatch)
				{
					m_pstmt.addBatch();
				}
			}
			catch(Exception e)
			{
				logger.log(Level.SEVERE, String.format("setObjects threw an exception %s", e.getMessage()));
				//m_pstmt = null;
				throw e;				
			}
		}
	}

	public void setObject(int parameterIndex, Object x) throws Exception {
		if (null != m_pstmt)
		{
			try 
			{
				m_pstmt.setObject(parameterIndex, x);
			}
			catch(Exception e)
			{
				logger.log(Level.SEVERE, String.format("setObject threw an exception %s", e.getMessage()));
				//m_pstmt = null;
				throw e;				
			}
		}
		
	}
	
	public void executeBatch(boolean bCommit) throws Exception
	{
		if (null != m_pstmt)
		{
			try 
			{
				m_pstmt.executeBatch();
				if (true == bCommit)
				{
					m_pstmt.getConnection().commit();
				}
			} 
			catch (SQLException e) 
			{
				logger.log(Level.SEVERE, String.format("executeBatch threw an exception %s", e.getMessage()));
				//m_pstmt = null;
				throw new Exception(e.getMessage());
			}
		}
	}

	public ResultSet executeQuery(boolean bCommit) throws Exception
	{
		ResultSet rs = null;
		if (null != m_pstmt)
		{
			try 
			{
				rs = m_pstmt.executeQuery();
				if (true == bCommit)
				{
					m_pstmt.getConnection().commit();
				}
			} 
			catch (SQLException e) 
			{
				logger.log(Level.SEVERE, String.format("executeQuery threw an exception %s", e.getMessage()));
				//m_pstmt = null;
				throw new Exception(e.getMessage());
			}
		}
		return rs;
	}
	
	public void commit ()
	{
		if (null != m_pstmt)
		{
			try 
			{
					m_pstmt.getConnection().commit();
			} 
			catch (Exception e) 
			{
				logger.log(Level.SEVERE, String.format("commit threw an exception %s", e.getMessage()));
			}
		}
		
	}
	
	public void rollback ()
	{
		if (null != m_pstmt)
		{
			try 
			{
					m_pstmt.getConnection().rollback();
			} 
			catch (Exception e) 
			{
				logger.log(Level.SEVERE, String.format("rollback threw an exception %s", e.getMessage()));
			}
		}		
	}
	
	public ResultSet getResultSet()
	{
		ResultSet rs = null;
		if (null != m_pstmt)
		{
			try 
			{
				if(m_pstmt.getMoreResults())
				{
					rs = m_pstmt.getResultSet();
				}
				else
				{
					rs = null;
				}
			} 
			catch (Exception e) 
			{
				logger.log(Level.SEVERE, String.format("getResultSet threw an exception %s", e.getMessage()));
			}
		}		
		return rs;
	}

	public void close ()
	{
		if (null != m_pstmt)
		{
			try 
			{
				m_pstmt.getConnection().close();
				m_pstmt.close();
			} 
			catch (Exception e) 
			{
				logger.log(Level.SEVERE, String.format("close threw an exception %s", e.getMessage()));
			}
			m_pstmt = null;
		}		
	}
}
