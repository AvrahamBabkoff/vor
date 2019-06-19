package il.co.vor.API.DalDataService;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

import il.co.vor.ApiObjectsCommon.ApiMultiResultWrapper;
import il.co.vor.DalDataObjects.FtpFile;
import il.co.vor.VOSDBConnection.*;
import il.co.vor.common.Constants;
import il.co.vor.common.Enums;
//import il.co.vor.VOSDBConnection.BatchExecuter;
//import il.co.vor.VOSDBConnection.SPExecuterAndJSONSerializer;
//import il.co.vor.VOSDBConnection.SQLStatements;
import il.co.vor.common.VosConfigResources;
import il.co.vor.common.VosDataResources;

@Path(VosDataResources.FTP_FILES_NAME)
public class FtpFiles 
{
	private static final Logger logger = Logger.getLogger(FtpFiles.class.getName());
	private static ObjectMapper m_mapper = null;
	private static TypeReference<ApiMultiResultWrapper<FtpFile>> m_ref = null;
	
	private static final String m_strSpPrepareFtpFilesInsert = SQLStatements.getSqlStatement(SQLStatementsParamNames.SP_PREPARE_FTP_FILES_INSERT_PARAM_NAME);
	private static final String m_strSpInsertTempFtpFiles = String.format(SQLStatements.getSqlStatement(SQLStatementsParamNames.SQL_INSERT_TEMP_FTP_FILES_PARAM_NAME),
			VosDataResources.TEMP_FTP_FILES_NAME,
			VosDataResources.FTP_FILES_PROP_FTP_FILE_NAME_NAME,
			VosDataResources.FTP_FILES_PROP_FTP_FILE_SIZE_NAME,
			VosDataResources.FTP_FILES_PROP_FTP_FILE_TYPE_NAME,
			VosDataResources.FTP_FILES_PROP_FTP_FILE_ROW_QUANTITY_NAME,
			VosDataResources.FTP_FILES_PROP_FTP_FILE_SERVER_PATH_NAME,
			VosDataResources.FTP_FILES_PROP_FTP_FILE_LOCAL_PATH_NAME,
			VosDataResources.FTP_FILES_PROP_FTP_FILE_DESTINATION_PATH_NAME,
			VosDataResources.FTP_FILES_PROP_FTP_FILE_ARCHIVE_FOLDER_NAME,
			VosDataResources.FTP_FILES_PROP_FTP_FILE_REATEMPT_COUNT_NAME,
			VosDataResources.FTP_FILES_PROP_FTP_FILE_REATEMPT_BULK_COUNT_NAME,
			VosDataResources.FTP_FILES_PROP_FTP_FILE_CURRENT_PHASE_NAME,
			VosDataResources.FTP_FILES_PROP_FTP_FILE_LAST_PHASE_CHANGE_TIMESTAMP_NAME,
			VosDataResources.FTP_FILES_PROP_FTP_FILE_DIR_PHASE_STATUS_NAME,
			VosDataResources.FTP_FILES_PROP_FTP_FILE_DWL_PHASE_STATUS_NAME,
			VosDataResources.FTP_FILES_PROP_FTP_FILE_UPLOAD_PHASE_STATUS_NAME,
			VosDataResources.FTP_FILES_PROP_FTP_FILE_BULK_PHASE_STATUS_NAME,
			VosDataResources.FTP_FILES_PROP_FTP_FILE_ARCHIVE_PHASE_STATUS_NAME,
			VosDataResources.FTP_FILES_PROP_FTP_FILE_CLEAN_PHASE_STATUS_NAME,
			VosDataResources.FTP_FILES_PROP_FTP_FILE_ERROR_CODE_NAME,
			VosConfigResources.SITES_PROP_SITE_ID_NAME,
			VosConfigResources.SERVICES_PROP_SERVICE_ID_NAME);

	private static final String m_strSpCommitFtpFilesFromTemp = SQLStatements.getSqlStatement(SQLStatementsParamNames.SP_COMMIT_FTP_FILES_FROM_TEMP_TABLE_PARAM_NAME);
	
	private static final String m_strSpGetFtpFilesForDownload = SQLStatements.getSqlStatement(SQLStatementsParamNames.SP_GET_FTP_FILES_FOR_DOWNLOAD_PARAM_NAME);
	private static final String m_strSpGetFtpFilesForUpload = SQLStatements.getSqlStatement(SQLStatementsParamNames.SP_GET_FTP_FILES_FOR_UPLOAD_PARAM_NAME);

	private static final String m_strSpUpdateFtpFilesDownloadCompleted = SQLStatements.getSqlStatement(SQLStatementsParamNames.SP_UPDATE_FTP_FILES_DOWNLOAD_COMPLETED_PARAM_NAME);
	private static final String m_strSpUpdateFtpFilesUploadCompleted = SQLStatements.getSqlStatement(SQLStatementsParamNames.SP_UPDATE_FTP_FILES_UPLOAD_COMPLETED_PARAM_NAME);

	private static final String m_strSpGetFtpFilesForBulk = SQLStatements.getSqlStatement(SQLStatementsParamNames.SP_GET_FTP_FILES_FOR_BULK_PARAM_NAME);
	private static final String m_strSpDoBulkInsert = SQLStatements.getSqlStatement(SQLStatementsParamNames.SP_DO_BULK_INSERT_PARAM_NAME);
	private static final String m_strSpUpdateOrphandFtpFiles = SQLStatements.getSqlStatement(SQLStatementsParamNames.SP_UPDATE_FTP_FILES_UPDATE_ORPHAND_FTP_FILES);
	
	private static String[] m_bulkStatement = null;
	@SuppressWarnings("unused")
	private static final boolean bInitialize = _init();

	private static boolean _init()
	{
		//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
		m_mapper = new ObjectMapper();
		 AnnotationIntrospector aiJaxb = new JaxbAnnotationIntrospector(TypeFactory.defaultInstance());
	     AnnotationIntrospector aiJackson = new JacksonAnnotationIntrospector();
	        // first Jaxb, second Jackson annotations
	    m_mapper.setAnnotationIntrospector(AnnotationIntrospector.pair(aiJaxb, aiJackson));
	    //m_mapper.setDateFormat(sdf);
	    m_ref = new TypeReference<ApiMultiResultWrapper<FtpFile>>() { };
	    m_bulkStatement = new String[Enums.FileType.values().length];
	    m_bulkStatement[Enums.FileType.CDR_DATA.ordinal()] = SQLStatements.getSqlStatement(SQLStatementsParamNames.SQL_BULK_INSERT_CDR_DATA_PARAM_NAME);
	    m_bulkStatement[Enums.FileType.METERS_DATA.ordinal()] = SQLStatements.getSqlStatement(SQLStatementsParamNames.SQL_BULK_INSERT_METERS_DATA_PARAM_NAME);
	    m_bulkStatement[Enums.FileType.OPERANDS_DATA.ordinal()] = SQLStatements.getSqlStatement(SQLStatementsParamNames.SQL_BULK_INSERT_OPERANDS_DATA_PARAM_NAME);
	    //m_bulkStatement.put(Enums.FileType.CDR.ordinal(), SQLStatements.getSqlStatement(SQLStatementsParamNames.SP_GET_FTP_FILES_FOR_BULK_PARAM_NAME));
	    //m_bulkStatement.put(Enums.FileType.METERSDATA.ordinal(), SQLStatements.getSqlStatement(SQLStatementsParamNames.SP_GET_FTP_FILES_FOR_BULK_PARAM_NAME));
		return true;
	}
	
/*	
	@GET
	@Produces ("application/json")
	public String getFtpServers () 
	{
    	String strJASONResponse = "";
    	SPExecuterAndJSONSerializer spExec = new SPExecuterAndJSONSerializer();
    	//strJASONResponse = spExec.setSP("SELECT * FROM get_parameters (?)").
    	strJASONResponse = spExec.setSP(m_strSpGetFtpFiles).
    					   setResultSetNames(VosDataResources.FTP_FILES_NAME).
    					   ExecuteAndSerializeAsJSONString(null);
        return strJASONResponse;
	}
*/
	
	@Path(VosDataResources.FTP_ORPHAND_FILES_NAME)
	@GET
	@Produces ("application/json")
	public String updateOrphandFtpFiles (@DefaultValue("-1") @QueryParam(VosConfigResources.SERVICES_PROP_SERVICE_ID_NAME) int serviceId,
								@DefaultValue("-1") @QueryParam(Constants.NET_VOS_TIMEOUT_PARAM_NAME) int timeout) 
	{
    	String strJASONResponse = "";
    	SPExecuterAndJSONSerializer spExec = new SPExecuterAndJSONSerializer();

		spExec = new SPExecuterAndJSONSerializer();


    	strJASONResponse = spExec.setSP(m_strSpUpdateOrphandFtpFiles).
				   setParameters(serviceId, 
							 timeout, 
							 Enums.FilePhase.DWL.ordinal(),
							 Enums.FilePhase.DIR.ordinal(),
							 Enums.FilePhase.UPLOAD.ordinal(),
							 Enums.FilePhase.DWL.ordinal(),
							 Enums.FilePhaseStatus.STARTED.ordinal()).
				   setResultSetNames(VosDataResources.FTP_ORPHAND_FILES_NAME).
				   ExecuteAndSerializeAsJSONString(null);
    	
    	return strJASONResponse;
	}
	
	@Path(VosDataResources.API_FTP_FILES_GET_BY_ACTION_URI_TEMPLATE)
	@POST
	@Produces ("application/json")
	public String getFtpFilesForActionType (@PathParam(VosDataResources.FTP_FILES_GET_ACTION_TYPE_NAME) int actionType,
										    @DefaultValue("-1") @QueryParam(VosConfigResources.SERVICES_PROP_SERVICE_ID_NAME) int serviceId,
										    @DefaultValue("-1") @QueryParam(Constants.NET_VOS_COUNT_PARAM_NAME) int nCount) 
	{
		Enums.FilePhase _action = Enums.FilePhase.values()[actionType];
		Enums.FilePhase _current = null;
		Enums.FilePhase _new = null;
    	String strJASONResponse = "";
    	SPExecuterAndJSONSerializer spExec = new SPExecuterAndJSONSerializer();
    	String sSP = null;
    	//strJASONResponse = spExec.setSP("SELECT * FROM get_parameters (?)").
    	
    	if (_action == Enums.FilePhase.DWL)
    	{
    		sSP = m_strSpGetFtpFilesForDownload;
    		_current = Enums.FilePhase.DIR;
    		_new = Enums.FilePhase.DWL;
    	}
    	else if (_action == Enums.FilePhase.UPLOAD)
    	{
    		sSP = m_strSpGetFtpFilesForUpload;
    		_current = Enums.FilePhase.DWL;
    		_new = Enums.FilePhase.UPLOAD;
    	}
    	strJASONResponse = spExec.setSP(sSP).
    					   setParameters(serviceId, 
    							   		 nCount, 
    							   		_current.ordinal(), 
    							   		 Enums.FilePhaseStatus.DONE.ordinal(), 
    							   		_new.ordinal(), 
    							   		 Enums.FilePhaseStatus.STARTED.ordinal()).
    					   setResultSetNames(VosDataResources.FTP_FILES_NAME).
    					   ExecuteAndSerializeAsJSONString(null);
        return strJASONResponse;
	}
	
	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public /*List<FtpFile>*/ String updateFtpFiles
	(
			List<FtpFile> ftpFiles, 
			@DefaultValue("-1") @QueryParam(VosDataResources.FTP_FILES_UPDATE_ACTION_NAME) int action
	) 
	{
    	String strJASONResponse = "";
    	if (action == Enums.FtpFileAction.INSERT.ordinal())
    	{
    		strJASONResponse = doInsert(ftpFiles);
    	}
    	else
    	{
    		strJASONResponse = doUpdate(ftpFiles, action);
    	}
/*    	
		int i = 0;
		BatchExecuter be = null;
		FtpFile ftpFile = null;
    	SPExecuterAndJSONSerializer spExec = null;
    	Date dateLastPhaseChangeTimestamp = null;
		
		be = new BatchExecuter();
		spExec = new SPExecuterAndJSONSerializer();
		try 
		{
			// call procedure to create temp table
			be.setStatement(m_strSpPrepareFtpFilesInsert);
			be.addBatch(VosDataResources.TEMP_FTP_FILES_NAME);
			be.executeBatch(false);
			
			// call procedure to insert records to temp table
			be.setStatement(m_strSpInsertTempFtpFiles);
			
			for (i = 0; i < ftpFiles.size(); i++)
			{
				ftpFile = ftpFiles.get(i);
				dateLastPhaseChangeTimestamp = ftpFile.getFtpFileLastPhaseChangeTimestamp();
				be.addBatch(ftpFile.getFtpFileName(),
							ftpFile.getFtpFileSize(),
							ftpFile.getFtpFileType(),
							ftpFile.getFtpFileRowQuantity(),
							ftpFile.getFtpFileServerPath(),
							ftpFile.getFtpFileLocalPath(),
							ftpFile.getFtpFileDestinationPath(),
							ftpFile.getFtpFileArchiveFolder(),
							ftpFile.getFtpFileReatemptCount(),
							ftpFile.getFtpFileReatemptBulkCount(),
							ftpFile.getFtpFileCurrentPhase(),
							new java.sql.Timestamp((dateLastPhaseChangeTimestamp != null)?dateLastPhaseChangeTimestamp.getTime():0),
							ftpFile.getFtpFileDirPhaseStatus(),
							ftpFile.getFtpFileDwlPhaseStatus(),
							ftpFile.getFtpFileUploadPhaseStatus(),
							ftpFile.getFtpFileBulkPhaseStatus(),
							ftpFile.getFtpFileArchivePhaseStatus(),
							ftpFile.getFtpFileCleanPhaseStatus(),
							ftpFile.getFtpFileErrorCode(),
							ftpFile.getSiteId(),
							ftpFile.getServiceId());
			}
			be.executeBatch(false);
	    	strJASONResponse = spExec.setSP(m_strSpCommitFtpFilesFromTemp).
					   setParameters(VosDataResources.TEMP_FTP_FILES_NAME).
					   setResultSetNames(VosDataResources.FTP_FILES_NAME).
					   ExecuteAndSerializeAsJSONString(be);
			
		} 
		catch (Exception e) 
		{
			
		}
		//return ftpFiles.get(3);
 */
    	//strJASONResponse = doInsert(ftpFiles);
		return strJASONResponse;
	}	

	
	
	private String doInsert(List<FtpFile> ftpFiles)
	{
		int i = 0;
		BatchExecuter be = null;
		FtpFile ftpFile = null;
    	String strJASONResponse = "";
    	SPExecuterAndJSONSerializer spExec = null;
    	Date dateLastPhaseChangeTimestamp = null;
    	String sErrorCode = "";
    	int iError = 0;

    	
		be = new BatchExecuter();
		spExec = new SPExecuterAndJSONSerializer();
		try 
		{
			// call procedure to create temp table
			be.setStatement(m_strSpPrepareFtpFilesInsert);
			be.addBatch(VosDataResources.TEMP_FTP_FILES_NAME);
			be.executeBatch(false);
			
			// call procedure to insert records to temp table
			be.setStatement(m_strSpInsertTempFtpFiles);
			
			for (i = 0; i < ftpFiles.size(); i++)
			{
				ftpFile = ftpFiles.get(i);
				dateLastPhaseChangeTimestamp = ftpFile.getFtpFileLastPhaseChangeTimestamp();
				be.addBatch(ftpFile.getFtpFileName(),
							ftpFile.getFtpFileSize(),
							ftpFile.getFtpFileType(),
							ftpFile.getFtpFileRowQuantity(),
							ftpFile.getFtpFileServerPath(),
							ftpFile.getFtpFileLocalPath(),
							ftpFile.getFtpFileDestinationPath(),
							ftpFile.getFtpFileArchiveFolder(),
							ftpFile.getFtpFileReatemptCount(),
							ftpFile.getFtpFileReatemptBulkCount(),
							ftpFile.getFtpFileCurrentPhase(),
							new java.sql.Timestamp((dateLastPhaseChangeTimestamp != null)?dateLastPhaseChangeTimestamp.getTime():0),
							ftpFile.getFtpFileDirPhaseStatus(),
							ftpFile.getFtpFileDwlPhaseStatus(),
							ftpFile.getFtpFileUploadPhaseStatus(),
							ftpFile.getFtpFileBulkPhaseStatus(),
							ftpFile.getFtpFileArchivePhaseStatus(),
							ftpFile.getFtpFileCleanPhaseStatus(),
							ftpFile.getFtpFileErrorCode(),
							ftpFile.getSiteId(),
							ftpFile.getServiceId());
			}
			be.executeBatch(false);
	    	strJASONResponse = spExec.setSP(m_strSpCommitFtpFilesFromTemp).
					   setParameters(VosDataResources.TEMP_FTP_FILES_NAME).
					   setResultSetNames(VosDataResources.FTP_FILES_NAME).
					   ExecuteAndSerializeAsJSONString(be);
			
		} 
		catch (Exception e) 
		{
			iError = -1;
			sErrorCode = e.getMessage();
			logger.log(Level.SEVERE, String.format("Exception. %s"), sErrorCode);
			strJASONResponse = String.format("{\"Meta\":{\"error\":%d,\"message\":\"%s\"}}", iError, sErrorCode);
		}

		finally
    	{
    		if (null != be)
    		{
				be.close();
    		}
    	}
		
		
		
		//return ftpFiles.get(3);
		return strJASONResponse;
		
	}
	
	private String doUpdate(List<FtpFile> ftpFiles, /*Enums.FtpFileAction*/int ftpFileAction)
	{
		int i = 0;
		BatchExecuter be = null;
		FtpFile ftpFile = null;
    	String strJASONResponse = "";
    	SPExecuterAndJSONSerializer spExec = null;
    	boolean bContinue = true;
    	int iStatus = 0;
    	int iFileId;
    	String sErrorCode = "";
    	String sFilePath = "";
    	int iError = 0;
		
		be = new BatchExecuter();
		spExec = new SPExecuterAndJSONSerializer();
		
		try 
		{
			if (ftpFileAction == Enums.FtpFileAction.UPDATE_DOWNLOAD_STATUS.ordinal())
			{
				be.setStatement(m_strSpUpdateFtpFilesDownloadCompleted);
			}
			else if (ftpFileAction == Enums.FtpFileAction.UPDATE_UPLOAD_STATUS.ordinal())
			{
				be.setStatement(m_strSpUpdateFtpFilesUploadCompleted);
			}
			else
			{
				bContinue = false;
			}
			
			if (false == bContinue)
			{
				throw new Exception("illegal update action type");
			}
				
			for (i = 0; i < ftpFiles.size(); i++)
			{
				ftpFile = ftpFiles.get(i);
				iFileId = ftpFile.getFtpFileId();

				if (ftpFileAction == Enums.FtpFileAction.UPDATE_DOWNLOAD_STATUS.ordinal())
				{
					iStatus = ftpFile.getFtpFileDwlPhaseStatus();
					sFilePath = ftpFile.getFtpFileLocalPath();
				}
				else
				{
					iStatus = ftpFile.getFtpFileUploadPhaseStatus();
					sFilePath = ftpFile.getFtpFileDestinationPath();
				}
				
				be.addBatch(iFileId, iStatus, sFilePath);
			}
			
			be.executeBatch(true);
			
		} 
		catch (Exception e) 
		{
			iError = -1;
			sErrorCode = e.getMessage();
			logger.log(Level.SEVERE, String.format("Exception. %s"), sErrorCode);
		}
		finally
    	{
    		if (null != be)
    		{
				be.close();
    		}
    	}
		
		strJASONResponse = String.format("{\"Meta\":{\"error\":%d,\"message\":\"%s\"}}", iError, sErrorCode);
		//return ftpFiles.get(3);
		return strJASONResponse;
		
	}

	
	
	@Path(VosDataResources.API_FTP_FILES_BULK_INSERT)
	@POST
	@Produces ("application/json")
	public String doBulkInsert (@DefaultValue("-1") @QueryParam(VosConfigResources.SERVICES_PROP_SERVICE_ID_NAME) int serviceId,
								@DefaultValue("-1") @QueryParam(Constants.NET_VOS_COUNT_PARAM_NAME) int nCount) 
	{
		BatchExecuter be = null;
    	String strJASONResponse = "";
    	SPExecuterAndJSONSerializer spExec = new SPExecuterAndJSONSerializer();
    	String sSP = null;
    	ApiMultiResultWrapper<FtpFile> amrwr = null;
    	List<FtpFile> aFilesToBulk = null;
    	boolean bContinue = true;
    	String sErrorCode = "";

    	//ResultSet rs = null;
    	//strJASONResponse = spExec.setSP("SELECT * FROM get_parameters (?)").
		be = new BatchExecuter();
		spExec = new SPExecuterAndJSONSerializer();

		
		try 
		{
/*			
			be.setStatement(m_strSpGetFtpFilesForBulk);
			be.addParameters(serviceId, 
							 nCount, 
							 Enums.FilePhase.UPLOAD.ordinal(),
							 Enums.FilePhaseStatus.DONE.ordinal(),
							 Enums.FilePhase.BULK.ordinal(),
							 Enums.FilePhaseStatus.STARTED.ordinal(),
							 -1);
*/
			// NOTE: we use this version in order not to include jdbc result set in this module, although it is a hit on performance!
	    	strJASONResponse = spExec.setSP(m_strSpGetFtpFilesForBulk).
					   setParameters(serviceId, 
								 nCount, 
								 Enums.FilePhase.UPLOAD.ordinal(),
								 Enums.FilePhaseStatus.DONE.ordinal(),
								 Enums.FilePhase.BULK.ordinal(),
								 Enums.FilePhaseStatus.STARTED.ordinal(),
								 -1).
					   setResultSetNames(VosDataResources.FTP_FILES_NAME).
					   ExecuteAndSerializeAsJSONString(null);
	    	
	    	if(strJASONResponse == null || strJASONResponse.isEmpty())
	    	{
	    		bContinue = false;
	    	}
	    	if (true == bContinue)
	    	{
	    		bContinue = ((amrwr = m_mapper.readValue(strJASONResponse, m_ref)) != null);
	    	}
	    	if (true == bContinue)
			{
				if (amrwr.getApiResult().getError() != 0 ||
				   (amrwr.getApiData() == null) ||
				   (aFilesToBulk = amrwr.getApiData().get(VosDataResources.FTP_FILES_NAME)) == null)
				{
					bContinue = false;
				}
			}
			
	    	if ((true == bContinue) && (aFilesToBulk.size() > 0))
	    	{
	    		be.setStatement(m_strSpDoBulkInsert);
	    		// iterate of files and for each do bulk insert
	    		for (FtpFile f: aFilesToBulk)
	    		{
	    			be.addBatch(f.getFtpFileId(),
	    					f.getFtpFileName(),
	    					f.getFtpFileDestinationPath(),
	    					m_bulkStatement[f.getFtpFileType()],
	    					Enums.FilePhaseStatus.DONE.ordinal(),
	    					Enums.FilePhaseStatus.FAILED.ordinal());
	    		}
	    		be.executeBatch(true);
	    	}
		} 
		catch (Exception e) 
		{
			sErrorCode = e.getMessage();
			logger.log(Level.SEVERE, String.format("Exception. %s"), sErrorCode);
		}
    	finally
    	{
    		if (null != be)
    		{
				be.close();
    		}
    	}
		
    	
        return strJASONResponse;
	}
	
}

