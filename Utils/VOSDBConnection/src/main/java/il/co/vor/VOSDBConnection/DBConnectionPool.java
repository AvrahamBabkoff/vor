package il.co.vor.VOSDBConnection;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.dbcp2.BasicDataSource;

import il.co.vor.common.Constants;
import il.co.vor.utilities.PropertyFileReader;

public class DBConnectionPool 
{
	private static Logger _logger = Logger.getLogger(DBConnectionPool.class.getName());

	private static BasicDataSource m_connectionPool;

	private static String m_strPreStatement = null;
	
	private static void setDBProperties()
	{
		PropertyFileReader.setPropertyFromParam(Constants.PROP_NAME_DB_URI);
		PropertyFileReader.setPropertyFromParam(Constants.PROP_NAME_DB_User);
		PropertyFileReader.setPropertyFromParam(Constants.PROP_NAME_DB_Password);
		PropertyFileReader.setPropertyFromParam(Constants.PROP_NAME_DB_PoolInitialSize);
		PropertyFileReader.setPropertyFromParam(Constants.PROP_NAME_DB_PoolMaxTotal);
		PropertyFileReader.setPropertyFromParam(Constants.PROP_NAME_DB_DriverClassName);
		PropertyFileReader.setPropertyFromParam(Constants.PROP_NAME_DB_PoolWaitForConnectionTimeout);
		PropertyFileReader.setPropertyFromParam(Constants.PROP_NAME_DB_SQL_ResultSetAsObject);
		PropertyFileReader.setPropertyFromParam(Constants.PROP_NAME_DB_SQL_Startements_Properties_File_Name);
	}

	public static void DBConnectionPoolInit(boolean bUseParameters)
	{
		_logger.log(Level.WARNING, "in DBConnectionPoolInit. bUseParameters = " + bUseParameters);
		if (true == bUseParameters)
		{
			setDBProperties ();
		}
		SQLStatements.init();
		
		String sURI = PropertyFileReader.getProperty(Constants.PROP_NAME_DB_URI);
		String sUser = PropertyFileReader.getProperty(Constants.PROP_NAME_DB_User);
		String sPassword = PropertyFileReader.getProperty(Constants.PROP_NAME_DB_Password);
		int iPoolSize = PropertyFileReader.getPropertyAsInt(Constants.PROP_NAME_DB_PoolInitialSize);
		int iMaxTotal = PropertyFileReader.getPropertyAsInt(Constants.PROP_NAME_DB_PoolMaxTotal);
		String sDriverClassName = PropertyFileReader.getProperty(Constants.PROP_NAME_DB_DriverClassName);
		int iMaxWaitMilis = PropertyFileReader.getPropertyAsInt(Constants.PROP_NAME_DB_PoolWaitForConnectionTimeout);
		m_strPreStatement = SQLStatements.getSqlStatement(Constants.CONNECTION_PRE_STATEMENT_PARAM_NAME);
		m_connectionPool = new BasicDataSource();

	    m_connectionPool.setUsername(sUser);
	    m_connectionPool.setPassword(sPassword);
	    m_connectionPool.setDriverClassName(sDriverClassName);
	    m_connectionPool.setUrl(sURI);
	    m_connectionPool.setInitialSize(iPoolSize);
	    m_connectionPool.setMaxTotal(iMaxTotal);
	    m_connectionPool.setMaxWaitMillis(iMaxWaitMilis);

	}
	
	public static void DBConnectionPoolClose()
	{
		_logger.log(Level.WARNING, "in DBConnectionPoolClose");
	    try 
	    {
			m_connectionPool.close();
		} 
	    catch (SQLException e) 
	    {
	    	_logger.log(Level.SEVERE, "DBConnectionPoolClose threw an exception: " + e.getMessage());
		}
	}

	public static Connection getConnection()
	{
		Connection connection = null;
		Statement st = null;
		try 
		{
			connection = m_connectionPool.getConnection();
			try
			{
				// rollback in case this connection was in the middle of a transaction when returned to pool
				connection.rollback();
			}
			catch (SQLException e) 
			{
				// Ignore this exception
				
				//StringWriter sw = new StringWriter();
				//PrintWriter pw = new PrintWriter(sw);
				//e.printStackTrace(pw);
				//_logger.log(Level.SEVERE, "connection.rollback threw an exception: " + e.getMessage());
				//_logger.log(Level.SEVERE, "trace: " + sw.toString());
			}
			
			connection.setAutoCommit(false);
			st = connection.createStatement();
			if (null != m_strPreStatement)
			{
				//st.executeUpdate("SET search_path = vos_config");
				st.executeUpdate(m_strPreStatement);
			}
			connection.commit();
		} 
		catch (SQLException e) 
		{
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			_logger.log(Level.SEVERE, "general exception: " + e.getMessage());
			_logger.log(Level.SEVERE, "trace: " + sw.toString());
		}
		
		return connection;
	}
}
