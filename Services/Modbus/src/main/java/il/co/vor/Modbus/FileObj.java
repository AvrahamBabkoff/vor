package il.co.vor.Modbus;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;
import java.util.logging.Logger;

import il.co.vor.VOSLogger;
import il.co.vor.common.Constants;
import il.co.vor.common.Enums.FileType;

public class FileObj {

	private static final Logger logger = VOSLogger.getInstance();

	Logger _logger = Logger.getLogger(NPL.class.getName());

	private BlockingDeque<IFileDataObj> m_Data_list = null;
	private int m_written_rows = 0;
	private int m_max_lines = 0;
	private ZonedDateTime m_next_close_time = null;
	private BufferedWriter m_bw = null;
	private StringBuilder m_sb = null;
	private String m_temp_path = "";
	private String m_final_path = "";
	private String m_root_path = "";
	private int m_write_file_method = -1;
	private String m_date_folder = "";
	private String m_file_name = "";
	private String m_file_extension = "";

	private ZonedDateTime m_first_write_time = null;
	private ZonedDateTime m_last_write_time = null;
	private FileType m_file_type = FileType.UNKNOWN;
	private int m_service_id = -1;
	
	private boolean m_is_failed = true; // true if could not read parameter value, false otherwise
	
	private int m_OL = 1;
	private int m_curr_OL = 1;

	public FileObj(FileType fileType, String fileExt, int serviceID, String ExportFileMaxIntervalStr, int RefreshDataInterval) {
		int ExportFileMaxInterval = Constants.NPL_DEFAULT_FILE_MAX_INTERVAL;
		
		if (m_Data_list == null)
			m_Data_list = new LinkedBlockingDeque<IFileDataObj>();

		m_file_type = fileType;
		m_file_extension = fileExt;
		m_service_id = serviceID;
		
		m_sb = new StringBuilder();
		
		if (!ExportFileMaxIntervalStr.isEmpty())
		{
			ExportFileMaxInterval = Integer.parseInt(ExportFileMaxIntervalStr);
		}
		setOL(ExportFileMaxInterval,RefreshDataInterval);
		initCurrOL();
	}

	public BlockingDeque<IFileDataObj> getDataList() {
		return m_Data_list;
	}

	public void setDataList(BlockingDeque<IFileDataObj> Data_list) {
		m_Data_list = Data_list;
	}

	public ZonedDateTime getNextCloseTime() {
		return m_next_close_time;
	}

	public void setNextCloseTime(ZonedDateTime next_close_time) {
		m_next_close_time = next_close_time;
	}

	public int getWrittenRows() {
		return m_written_rows;
	}

	public void setWrittenRows(int written_rows) {
		m_written_rows = written_rows;
	}

	public BufferedWriter getBW() {
		return m_bw;
	}

	public void initBW() {
		m_bw = null;
		m_first_write_time = null;
		m_last_write_time = null;
		m_date_folder = "";
		m_file_name = "";
		m_temp_path = "";
		m_final_path = "";
		m_written_rows = 0;
		m_next_close_time = null;
	}

	/*
	public void setBW(BufferedWriter bw, int writeMethod, String dateFolder, String fileName, ZonedDateTime refreshTime,
			String tempPath, String finalPath, String rootPath) {

		m_write_file_method = writeMethod;
		m_bw = bw;
		m_date_folder = dateFolder;
		m_file_name = fileName;
		m_temp_path = tempPath;
		m_final_path = finalPath;
		m_root_path = rootPath;
	}*/

	public String getTempPath() {
		return m_temp_path;
	}

	/*
	 * public static void setFilePath(String file_path) { FileObj.file_path =
	 * file_path; }
	 */

	public int getMaxLines() {
		return m_max_lines;
	}

	public void setMaxLines(int max_lines) {
		m_max_lines = max_lines;
	}

	public int getWriteFileMethod() {
		return m_write_file_method;
	}

	/*
	 * public void setWriteFileMethod(int write_file_method) {
	 * this.write_file_method = write_file_method; }
	 */

	public boolean ToCloseFile(ZonedDateTime refresh_time) {

		boolean ret = false;

		//Instant refresh_Instant = null;
		//Instant next_Instant = null;

		if (m_bw != null) {
			if (getWrittenRows() >= m_max_lines) {
				ret = true;
			} else {
				//decreaseCurrOL();
				//if (!IsFailed())
				//{
					if (m_curr_OL <= 0)
					{
						ret = true;
						initCurrOL();
					}
				//}
				
				/*refresh_Instant = refresh_time.toInstant();
				next_Instant = getNextCloseTime().toInstant();

				if (!refresh_Instant.isBefore(next_Instant)) {
					ret = true;
				}*/
				
			}
		}
		
		if (ret)
		{
			_logger.log(Level.WARNING, String.format("Close file: %s refresh_time: %s next_close_time: %s written rows: %s max rows: %s IsFailed: %s m_curr_OL: %s m_OL: %s", m_file_name, 
					DateTimeFormatter.ofPattern(Constants.CSV_DATE_FORMAT).format(refresh_time.withZoneSameInstant(ZoneOffset.UTC)), 
					DateTimeFormatter.ofPattern(Constants.CSV_DATE_FORMAT).format(getNextCloseTime().withZoneSameInstant(ZoneOffset.UTC)), 
					String.valueOf(m_written_rows), String.valueOf(m_max_lines),String.valueOf(IsFailed()),String.valueOf(m_curr_OL),String.valueOf(m_OL)));
		}
		return ret;
	}

	public String getFinalPath() {
		return m_final_path;
	}

	public String getFinalFilePath(ZonedDateTime refreshTime, boolean withFilename, boolean onlyFilename) {
		String ret = "";
		StringBuilder sbd = null;
		UUID idOne = null;

		try {
			String DateDirectory = DateTimeFormatter.ofPattern("yyyyMMdd")
					.format(refreshTime.withZoneSameInstant(ZoneOffset.UTC));
			String DateTimeStrf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
					.format(m_first_write_time.withZoneSameInstant(ZoneOffset.UTC));
			String DateTimeStrl = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
					.format(m_last_write_time.withZoneSameInstant(ZoneOffset.UTC));
			String filename = "";
			idOne = UUID.randomUUID();

			String FileTypeDir = getFileTypeDirectory();
			long size = Files.size(new File(getTempFilePath(true)).toPath());
			sbd = new StringBuilder();
			sbd.append(FileTypeDir).append(Constants.CSV_NAME_PARTS_DELIMITER).append(String.valueOf(m_service_id)).append(Constants.CSV_NAME_PARTS_DELIMITER).append(DateTimeStrf).append(Constants.CSV_NAME_PARTS_DELIMITER).append(DateTimeStrl)
			.append(Constants.CSV_NAME_PARTS_DELIMITER).append(String.valueOf(m_written_rows)).append(Constants.CSV_NAME_PARTS_DELIMITER).append(String.valueOf(size)).append(Constants.CSV_NAME_PARTS_DELIMITER).append(idOne.toString());

			StringBuilder sd = new StringBuilder(sbd.toString());
			sd.append(m_file_extension);
			filename = sd.toString();

			sbd = new StringBuilder();
			sbd.append(m_root_path).append(Constants.FILE_SEPARATOR).append(m_final_path).append(Constants.FILE_SEPARATOR).append(FileTypeDir).append(Constants.FILE_SEPARATOR).append(DateDirectory);
			CreateFoldersIfNeeded(sbd.toString());

			if (withFilename) {
				sbd.append(Constants.FILE_SEPARATOR).append(filename);
			}

			ret = sbd.toString();

			if (onlyFilename) {
				ret = filename;
			}
		} catch (Exception e) {
			_logger.log(Level.WARNING, String.format("Exception: %s", e.getMessage()));
			e.printStackTrace();
			ret = "";
		}

		return ret;
	}

	public String getTempFilePath(boolean withFilename) {
		String ret = "";

		// String FileTypeDir = getFileTypeDirectory();
		StringBuilder sbd = new StringBuilder();
		sbd.append(m_root_path).append(Constants.FILE_SEPARATOR).append(m_temp_path); // .add(FileTypeDir).add(m_date_folder);

		if (withFilename) {
			sbd.append(Constants.FILE_SEPARATOR).append(m_file_name);
		}
		ret = sbd.toString();

		return ret;
	}

	public void setFinalPath(String final_path) {
		m_final_path = final_path;
	}

	public String getDateFolder() {
		return m_date_folder;
	}

	public String getFileName() {
		return m_file_name;
	}

	public void setFileName(String filename) {
		m_file_name = filename;
	}

	public String getFileExtension() {
		return m_file_extension;
	}

	public String getFileTypeDirectory() {
		return m_file_type.toString();
	}

	public void CloseFileIfNeeded(ZonedDateTime refresh_time) throws IOException {
		if (ToCloseFile(refresh_time)) {
			CloseFile(refresh_time);
			
		}
	}

	public void CreateFoldersIfNeeded(String strpath) throws IOException {

		Path path = Paths.get(strpath);
		if (!Files.exists(path)) {
			Files.createDirectories(path);
		}

	}

	public void OpenFileIfNeeded(int writeMethod, ZonedDateTime refreshTime, String tempPath, String finalPath,
			String rootPath, int DataExportFileMaxInterval) throws IOException {

		//String DateDirectory = "";
		String FileTypeDir = "";
		String filename = "";
		String filenamepath = "";
		StringBuilder sbd = null;
		StringBuilder sd = null;
		UUID idOne = null;

		if (m_bw == null) // no file is open, open file
		{
			initCurrOL();
			//sb = new StringBuilder();
			m_write_file_method = writeMethod;
			m_temp_path = tempPath;
			m_final_path = finalPath;
			m_root_path = rootPath;
			m_written_rows = 0;
			m_date_folder = DateTimeFormatter.ofPattern("yyyyMMdd")
					.format(refreshTime.withZoneSameInstant(ZoneOffset.UTC));

			FileTypeDir = getFileTypeDirectory();
			filename = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
					.format(refreshTime.withZoneSameInstant(ZoneOffset.UTC));
			idOne = UUID.randomUUID();

			sbd = new StringBuilder();
			sbd.append(FileTypeDir).append(Constants.CSV_NAME_PARTS_DELIMITER).append(String.valueOf(m_service_id)).append(Constants.CSV_NAME_PARTS_DELIMITER).append(filename).append(Constants.CSV_NAME_PARTS_DELIMITER).append(idOne.toString());
			filename = sbd.toString();

			sd = new StringBuilder(filename);
			sd.append(Constants.CSV_EXTENSION);

			filename = sd.toString();
			m_file_name = filename;

			// joiner = new StringJoiner("\\"); //Separator
			// joiner.add(m_root_path).add(m_temp_path).add(FileTypeDir).add(DateDirectory);

			CreateFoldersIfNeeded(getTempFilePath(false));

			filenamepath = getTempFilePath(true);
			m_bw = new BufferedWriter(new FileWriter(filenamepath));

			m_next_close_time = refreshTime.plusSeconds(DataExportFileMaxInterval);
			_logger.log(Level.WARNING, String.format("Created new buffer: %s next_close_time: %s refreshTime: %s", 
					filenamepath, 
					DateTimeFormatter.ofPattern(Constants.CSV_DATE_FORMAT).format(m_next_close_time.withZoneSameInstant(ZoneOffset.UTC)),
					DateTimeFormatter.ofPattern(Constants.CSV_DATE_FORMAT).format(refreshTime.withZoneSameInstant(ZoneOffset.UTC))));
		} else if (FileWriteMethod.fromInt(getWriteFileMethod()) == FileWriteMethod.INTERVAL_CLOSE) {
			filenamepath = getTempFilePath(true);
			m_bw = new BufferedWriter(new FileWriter(filenamepath));
		}
		
	}

	private void writeToBW() throws IOException
	{
		if ((m_sb != null)&& (m_sb.length()>0))
		{
			m_bw.write(m_sb.toString());
			m_sb.setLength(0);
		}
	}
	
	public void UpdateItems(int writeMethod, ZonedDateTime refreshTime, String tempPath, String finalPath,
			String rootPath, int ExportFileMaxInterval) throws IOException {
		
		long lStart = 0;
		long lEnd = 0;
		
		//StringBuilder sb = null;
		
		lStart = System.currentTimeMillis();
		_logger.log(Level.INFO, String.format("UpdateItems Start. file_type: %s refresh_time: %s written rows: %s max rows: %s IsFailed: %s m_curr_OL: %s m_OL: %s",m_file_type.toString(),DateTimeFormatter.ofPattern(Constants.CSV_DATE_FORMAT).format(refreshTime.withZoneSameInstant(ZoneOffset.UTC)), 
				String.valueOf(m_written_rows), String.valueOf(m_max_lines),String.valueOf(IsFailed()),String.valueOf(m_curr_OL),String.valueOf(m_OL)));
		
		//String line = "";
		
		CloseFileIfNeeded(refreshTime);

		if (!m_Data_list.isEmpty()) // need to add
		{
			// write all lines
			for (IFileDataObj obj = m_Data_list.poll(); (obj != null) ; obj = m_Data_list.poll()) {
				//setIsFailed(true);
				try{
					OpenFileIfNeeded(writeMethod, refreshTime, tempPath, finalPath, rootPath, ExportFileMaxInterval);

					m_sb = obj.toString(m_sb);
					// line = String.format("%s %s %s %s",
					// Constants.CSV_DOUBLE_QUOTES,line,Constants.CSV_DOUBLE_QUOTES,Constants.CSV_NEW_LINE_SEPARATOR);
					//line = String.format("%s%s", line, Constants.CSV_NEW_LINE_SEPARATOR);
					m_sb.append(Constants.CSV_NEW_LINE_SEPARATOR);
					
					//m_bw.write(line);
					UpdateItem(obj);
					CloseFileIfNeeded(refreshTime);
					setIsFailed(false);
				} catch (Exception e) {
					setIsFailed(true);
					_logger.log(Level.SEVERE, String.format("failed to write value for operand: %s",obj.toString()));
					e.printStackTrace();
				}
			}
			
			writeToBW();
			
			if (FileWriteMethod.fromInt(getWriteFileMethod()) == FileWriteMethod.INTERVAL_FLUSH) {	
				m_bw.flush();
			} else if (FileWriteMethod.fromInt(getWriteFileMethod()) == FileWriteMethod.INTERVAL_CLOSE) {
				m_bw.close();
			}
			else if (FileWriteMethod.fromInt(getWriteFileMethod()) == FileWriteMethod.FINAL_CLOSE) { 
				// do nothing
			}
		}
		decreaseCurrOL();
		_logger.log(Level.INFO, "UpdateItems End");
		lEnd = System.currentTimeMillis();
		_logger.log(Level.INFO, String.format("UpdateItems Duration: %d milliseconds", (lEnd - lStart)));
	}

	public void CloseFile(ZonedDateTime refresh_time) throws IOException {
		Path oldFile = null;
		Path newFile = null;
		Path zipFile = null;
		Path newzipFile = null;
		
		StringBuilder sbd = null;
		
		String newfilepath = "";
		String zipfilepath = "";
		String newfilename = "";

		Map<String, String> env = null;

		if (m_bw != null) {

			writeToBW();
			m_bw.close();
			if (getWrittenRows() > 0) {
				oldFile = new File(getTempFilePath(true)).toPath();
				// newFile = new
				// File(getFinalFilePath(refresh_time,true,false)).toPath();

				newfilename = getFinalFilePath(refresh_time, false, true);
				sbd = new StringBuilder();
				sbd.append(getTempFilePath(false)).append(Constants.FILE_SEPARATOR).append(newfilename);
				newfilepath = sbd.toString();
				newFile = new File(newfilepath).toPath();
				Files.move(oldFile, newFile);
				setFileName(newFile.getFileName().toString());
				zipfilepath = newfilepath.substring(0, newfilepath.lastIndexOf('.'));
				zipfilepath = zipfilepath + Constants.ZIP_EXTENSION;

				zipFile = new File(zipfilepath).toPath();

				env = new HashMap<>();
				env.put("create", "true");
				// locate file system by using the syntax
				// defined in java.net.JarURLConnection

				// URI uri =
				// URI.create("jar:file:/codeSamples/zipfs/zipfstest.zip");

				final URI uri = URI.create("jar:" + zipFile.toUri());

				try (FileSystem zipfs = FileSystems.newFileSystem(uri, env)) {
					Path externalTxtFile = newFile;// Paths.get("/codeSamples/zipfs/SomeTextFile.txt");
					Path pathInZipfile = zipfs.getPath("/" + newFile.getFileName().toString());
					// copy a file into the zip file
					// Files.copy( externalTxtFile,pathInZipfile,
					// StandardCopyOption.REPLACE_EXISTING );
					Files.move(externalTxtFile, pathInZipfile, StandardCopyOption.REPLACE_EXISTING);
				}
				setFileName(zipFile.getFileName().toString());

				sbd = new StringBuilder();
				sbd.append(getFinalFilePath(refresh_time, false, false)).append(Constants.FILE_SEPARATOR).append(zipFile.getFileName().toString());
				newzipFile = new File(sbd.toString()).toPath();

				Files.move(zipFile, newzipFile);
				chmodGroup(newzipFile);
			}
		
			initBW();
		}
	}

	/**
     * Set permissions on the specified file equivalent to file mode 0600.
     *
     * NOTE: This method will only set permissions on files that exist on filesystems which support POSIX file
     * permissions.  Manipulation of permissions is silently skipped for filesystems that do not support POSIX file
     * permissions (such as Windows NTFS).
     *
     * @param file The file to set permissions on
     * @throws IOException if an I/O error occurs
     */
	public static void chmodGroup(Path filePath) throws IOException {
	   // Path filePath = file.toPath();
	    PosixFileAttributeView posixFileAttributeView = Files.getFileAttributeView(filePath, PosixFileAttributeView.class);
	    if (posixFileAttributeView != null) {
	        Files.setPosixFilePermissions(filePath, EnumSet.of(PosixFilePermission.GROUP_READ,PosixFilePermission.OWNER_READ,PosixFilePermission.OWNER_WRITE,PosixFilePermission.GROUP_EXECUTE,PosixFilePermission.GROUP_WRITE,PosixFilePermission.OWNER_EXECUTE));
	    }
	}

	private void SetFilename() {
		// TODO Auto-generated method stub

	}

	public void UpdateItem(IFileDataObj obj) {
		m_written_rows = m_written_rows + 1;
		if (m_written_rows == 1) {
			m_first_write_time = obj.GetLogTime();
		}
		m_last_write_time = obj.GetLogTime();
	}
	
	public boolean IsFailed() {
		return m_is_failed;
	}

	public void setIsFailed(boolean _is_failed) {
		m_is_failed = _is_failed;
	}

	public int getOL() {
		return m_OL;
	}

	public void setOL(int ExportFileMaxInterval, int RefreshDataInterval) {
		
		m_OL = ((int) ExportFileMaxInterval / RefreshDataInterval);
		if (m_OL < 1)
		{
			m_OL = 1;
		}
	}

	public int getCurrOL() {
		return m_curr_OL;
	}

	public void setCurrOL(int _curr_OL) {
		m_curr_OL = _curr_OL;
	}
	
	public void decreaseCurrOL() {
		m_curr_OL--;
	}
	
	public void initCurrOL() {
		m_curr_OL = m_OL;
	}


}
