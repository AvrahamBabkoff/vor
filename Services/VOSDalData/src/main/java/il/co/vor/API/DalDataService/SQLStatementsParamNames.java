package il.co.vor.API.DalDataService;

public class SQLStatementsParamNames 
{	
	public static final String SP_GET_FTP_FILES_PARAM_NAME = "SP.GetFtpFiles";
	public static final String SP_PREPARE_FTP_FILES_INSERT_PARAM_NAME = "SP.PrepareFtpFilesInsert";
	public static final String SQL_INSERT_TEMP_FTP_FILES_PARAM_NAME = "SQL.InsertTempFtpFiles";
	public static final String SP_COMMIT_FTP_FILES_FROM_TEMP_TABLE_PARAM_NAME = "SP.CommitFtpFilesFromTempTable";
	public static final String SP_GET_FTP_FILES_FOR_DOWNLOAD_PARAM_NAME = "SP.GetFtpFilesForDownload";
	public static final String SP_GET_FTP_FILES_FOR_UPLOAD_PARAM_NAME = "SP.GetFtpFilesForUpload";
	public static final String SP_GET_FTP_FILES_FOR_BULK_PARAM_NAME = "SP.GetFtpFilesForBulk";
	public static final String SQL_BULK_INSERT_METERS_DATA_PARAM_NAME = "SQL.BulkInsertMetersData";
	public static final String SQL_BULK_INSERT_CDR_DATA_PARAM_NAME = "SQL.BulkInsertCdrData";
	public static final String SQL_BULK_INSERT_OPERANDS_DATA_PARAM_NAME = "SQL.BulkInsertOperandsData";
	public static final String SP_DO_BULK_INSERT_PARAM_NAME = "SP.DoBulkInsert";
	public static final String SP_UPDATE_FTP_FILES_DOWNLOAD_COMPLETED_PARAM_NAME = "SP.UpdateFtpFilesDownloadCompleted";
	public static final String SP_UPDATE_FTP_FILES_UPLOAD_COMPLETED_PARAM_NAME = "SP.UpdateFtpFilesUploadCompleted";
	public static final String SP_UPDATE_FTP_FILES_UPDATE_ORPHAND_FTP_FILES = "SP.UpdateOrphandFtpFiles";
	
	public static final String SP_GET_CALC_OPERANDS_PROPERTIES_NAME = "SP.GetCalcOperandsProperties";
	public static final String SP_ARCHIVE_PROPERTIES_PARAM_NAME = "SP.ArchiveCalcOperandsProperties";
	public static final String SQL_INSERT_PROPERTIES_PARAM_NAME = "SQL.InsertCalcOperandsProperties";
}
