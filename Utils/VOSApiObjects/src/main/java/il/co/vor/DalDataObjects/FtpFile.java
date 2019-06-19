package il.co.vor.DalDataObjects;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;

import il.co.vor.common.VosConfigResources;
import il.co.vor.common.VosDataResources;

public class FtpFile 
{
	private int m_ftpFileId;
	private String m_ftpFileName;
	private int m_ftpFileSize;
	private int m_ftpFileType;
	private int m_ftpFileRowQuantity;
	private String m_ftpFileServerPath;
	private String m_ftpFileLocalPath;
	private String m_ftpFileDestinationPath;
	private String m_ftpFileArchiveFolder;
	private int m_ftpFileReatemptCount;
	private int m_ftpFileReatemptBulkCount;
	private int m_ftpFileCurrentPhase;
	private Date m_ftpFileLastPhaseChangeTimestamp;
	private Date m_ftpFileCreationTime;
	private int m_ftpFileDirPhaseStatus;
	private int m_ftpFileDwlPhaseStatus;
	private int m_ftpFileUploadPhaseStatus;
	private int m_ftpFileBulkPhaseStatus;
	private int m_ftpFileArchivePhaseStatus;
	private int m_ftpFileCleanPhaseStatus;
	private int m_ftpFileErrorCode;
	private int m_siteId;
	private int m_serviceId;
	private String m_ftpFileErrorDescription;
	
	@XmlElement(name=VosDataResources.FTP_FILES_PROP_FTP_FILE_ID_NAME)
	public int getFtpFileId() 
	{
		return m_ftpFileId;
	}
	
	public void setFtpFileId(int ftpFileId) 
	{
		this.m_ftpFileId = ftpFileId;
	}
	
	@XmlElement(name=VosDataResources.FTP_FILES_PROP_FTP_FILE_NAME_NAME)
	public String getFtpFileName() 
	{
		return m_ftpFileName;
	}
	
	public void setFtpFileName(String ftpFileName) 
	{
		this.m_ftpFileName = ftpFileName;
	}
	
	@XmlElement(name=VosDataResources.FTP_FILES_PROP_FTP_FILE_SIZE_NAME)
	public int getFtpFileSize() 
	{
		return m_ftpFileSize;
	}
	
	public void setFtpFileSize(int ftpFileSize) 
	{
		this.m_ftpFileSize = ftpFileSize;
	}
	
	@XmlElement(name=VosDataResources.FTP_FILES_PROP_FTP_FILE_TYPE_NAME)
	public int getFtpFileType() 
	{
		return m_ftpFileType;
	}
	
	public void setFtpFileType(int ftpFileType) 
	{
		this.m_ftpFileType = ftpFileType;
	}
	
	@XmlElement(name=VosDataResources.FTP_FILES_PROP_FTP_FILE_ROW_QUANTITY_NAME)
	public int getFtpFileRowQuantity() 
	{
		return m_ftpFileRowQuantity;
	}
	
	public void setFtpFileRowQuantity(int ftpFileRowQuantity) 
	{
		this.m_ftpFileRowQuantity = ftpFileRowQuantity;
	}
	
	@XmlElement(name=VosDataResources.FTP_FILES_PROP_FTP_FILE_SERVER_PATH_NAME)
	public String getFtpFileServerPath() 
	{
		return m_ftpFileServerPath;
	}
	
	public void setFtpFileServerPath(String ftpFileServerPath) 
	{
		this.m_ftpFileServerPath = ftpFileServerPath;
	}
	
	@XmlElement(name=VosDataResources.FTP_FILES_PROP_FTP_FILE_LOCAL_PATH_NAME)
	public String getFtpFileLocalPath() 
	{
		return m_ftpFileLocalPath;
	}
	
	public void setFtpFileLocalPath(String ftpFileLocalPath) 
	{
		this.m_ftpFileLocalPath = ftpFileLocalPath;
	}
	
	@XmlElement(name=VosDataResources.FTP_FILES_PROP_FTP_FILE_DESTINATION_PATH_NAME)
	public String getFtpFileDestinationPath() 
	{
		return m_ftpFileDestinationPath;
	}
	
	public void setFtpFileDestinationPath(String ftpFileDestinationPath) 
	{
		this.m_ftpFileDestinationPath = ftpFileDestinationPath;
	}
	
	@XmlElement(name=VosDataResources.FTP_FILES_PROP_FTP_FILE_ARCHIVE_FOLDER_NAME)
	public String getFtpFileArchiveFolder() 
	{
		return m_ftpFileArchiveFolder;
	}
	
	public void setFtpFileArchiveFolder(String ftpFileArchiveFolder) 
	{
		this.m_ftpFileArchiveFolder = ftpFileArchiveFolder;
	}
	
	@XmlElement(name=VosDataResources.FTP_FILES_PROP_FTP_FILE_REATEMPT_COUNT_NAME)
	public int getFtpFileReatemptCount() 
	{
		return m_ftpFileReatemptCount;
	}
	
	public void setFtpFileReatemptCount(int ftpFileReatemptCount) 
	{
		this.m_ftpFileReatemptCount = ftpFileReatemptCount;
	}
	
	@XmlElement(name=VosDataResources.FTP_FILES_PROP_FTP_FILE_REATEMPT_BULK_COUNT_NAME)
	public int getFtpFileReatemptBulkCount() 
	{
		return m_ftpFileReatemptBulkCount;
	}
	
	public void setFtpFileReatemptBulkCount(int ftpFileReatemptBulkCount) 
	{
		this.m_ftpFileReatemptBulkCount = ftpFileReatemptBulkCount;
	}
	
	@XmlElement(name=VosDataResources.FTP_FILES_PROP_FTP_FILE_CURRENT_PHASE_NAME)
	public int getFtpFileCurrentPhase() 
	{
		return m_ftpFileCurrentPhase;
	}
	
	public void setFtpFileCurrentPhase(int ftpFileCurrentPhase) 
	{
		this.m_ftpFileCurrentPhase = ftpFileCurrentPhase;
	}
	
	@XmlElement(name=VosDataResources.FTP_FILES_PROP_FTP_FILE_LAST_PHASE_CHANGE_TIMESTAMP_NAME)
	public Date getFtpFileLastPhaseChangeTimestamp() 
	{
		return m_ftpFileLastPhaseChangeTimestamp;
	}
	
	public void setFtpFileLastPhaseChangeTimestamp(Date ftpFileLastPhaseChangeTimestamp) 
	{
		this.m_ftpFileLastPhaseChangeTimestamp = ftpFileLastPhaseChangeTimestamp;
	}
	
	@XmlElement(name=VosDataResources.FTP_FILES_PROP_FTP_FILE_CREATION_TIME_NAME)
	public Date getFtpFileCreationTime() 
	{
		return m_ftpFileCreationTime;
	}
	
	public void setFtpFileCreationTime(Date ftpFileCreationTime) 
	{
		this.m_ftpFileCreationTime = ftpFileCreationTime;
	}
	
	@XmlElement(name=VosDataResources.FTP_FILES_PROP_FTP_FILE_DIR_PHASE_STATUS_NAME)
	public int getFtpFileDirPhaseStatus() 
	{
		return m_ftpFileDirPhaseStatus;
	}
	
	public void setFtpFileDirPhaseStatus(int ftpFileDirPhaseStatus) 
	{
		this.m_ftpFileDirPhaseStatus = ftpFileDirPhaseStatus;
	}
	
	@XmlElement(name=VosDataResources.FTP_FILES_PROP_FTP_FILE_DWL_PHASE_STATUS_NAME)
	public int getFtpFileDwlPhaseStatus() 
	{
		return m_ftpFileDwlPhaseStatus;
	}
	
	public void setFtpFileDwlPhaseStatus(int ftpFileDwlPhaseStatus) 
	{
		this.m_ftpFileDwlPhaseStatus = ftpFileDwlPhaseStatus;
	}
	
	@XmlElement(name=VosDataResources.FTP_FILES_PROP_FTP_FILE_UPLOAD_PHASE_STATUS_NAME)
	public int getFtpFileUploadPhaseStatus() 
	{
		return m_ftpFileUploadPhaseStatus;
	}
	
	public void setFtpFileUploadPhaseStatus(int ftpFileUploadPhaseStatus) 
	{
		this.m_ftpFileUploadPhaseStatus = ftpFileUploadPhaseStatus;
	}
	
	@XmlElement(name=VosDataResources.FTP_FILES_PROP_FTP_FILE_BULK_PHASE_STATUS_NAME)
	public int getFtpFileBulkPhaseStatus() 
	{
		return m_ftpFileBulkPhaseStatus;
	}
	
	public void setFtpFileBulkPhaseStatus(int ftpFileBulkPhaseStatus) 
	{
		this.m_ftpFileBulkPhaseStatus = ftpFileBulkPhaseStatus;
	}
	
	@XmlElement(name=VosDataResources.FTP_FILES_PROP_FTP_FILE_ARCHIVE_PHASE_STATUS_NAME)
	public int getFtpFileArchivePhaseStatus() 
	{
		return m_ftpFileArchivePhaseStatus;
	}
	
	public void setFtpFileArchivePhaseStatus(int ftpFileArchivePhaseStatus) 
	{
		this.m_ftpFileArchivePhaseStatus = ftpFileArchivePhaseStatus;
	}
	
	@XmlElement(name=VosDataResources.FTP_FILES_PROP_FTP_FILE_CLEAN_PHASE_STATUS_NAME)
	public int getFtpFileCleanPhaseStatus() 
	{
		return m_ftpFileCleanPhaseStatus;
	}
	
	public void setFtpFileCleanPhaseStatus(int ftpFileCleanPhaseStatus) 
	{
		this.m_ftpFileCleanPhaseStatus = ftpFileCleanPhaseStatus;
	}
	
	@XmlElement(name=VosDataResources.FTP_FILES_PROP_FTP_FILE_ERROR_CODE_NAME)
	public int getFtpFileErrorCode() 
	{
		return m_ftpFileErrorCode;
	}
	
	public void setFtpFileErrorCode(int ftpFileErrorCode) 
	{
		this.m_ftpFileErrorCode = ftpFileErrorCode;
	}
	
	@XmlElement(name=VosConfigResources.SITES_PROP_SITE_ID_NAME)
	public int getSiteId() 
	{
		return m_siteId;
	}
	
	public void setSiteId(int siteId) 
	{
		this.m_siteId = siteId;
	}
	
	@XmlElement(name=VosConfigResources.SERVICES_PROP_SERVICE_ID_NAME)
	public int getServiceId() 
	{
		return m_serviceId;
	}
	
	public void setServiceId(int serviceId) 
	{
		this.m_serviceId = serviceId;
	}
	
	@XmlElement(name=VosDataResources.FTP_FILES_PROP_FTP_FILE_ERROR_DESCRIPTION_NAME)
	public String getFtpFileErrorDescription() 
	{
		return m_ftpFileErrorDescription;
	}

	public void setFtpFileErrorDescription(String ftpFileErrorDescription) 
	{
		this.m_ftpFileErrorDescription = ftpFileErrorDescription;
	}
	
}
