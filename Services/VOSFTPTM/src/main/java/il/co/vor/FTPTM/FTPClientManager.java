package il.co.vor.FTPTM;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.apache.commons.net.ftp.FTPReply;

import il.co.vor.DalConfigObjects.FtpPlaceHolder;
import il.co.vor.DalDataObjects.FtpFile;
import il.co.vor.common.Constants;
import il.co.vor.common.Enums;
import il.co.vor.common.Enums.FilePhase;
import il.co.vor.common.Enums.FilePhaseStatus;
import il.co.vor.common.Enums.FileType;
import il.co.vor.utilities.SafeParser;

public class FTPClientManager {
	
	private FTPClient _ftp ;
	//private FTPClientConfig _config;
	private Logger _logger = Logger.getLogger(FTPClientManager.class.getName());;

	public FTPClientManager() {
		_ftp = new FTPClient();
		_ftp.setDataTimeout(10000);
		/*try {
			_ftp.setFileType(FTP.BINARY_FILE_TYPE);
		} catch (IOException e) {
			_logger.log(Level.SEVERE, "IOException : " + e.getMessage());
		}*/
	}
	
	public void DoConnect(String server , int port) {
		// TODO Auto-generated method stub
		try
        {
            int reply;
            if (port > 0) {
                _ftp.connect(server, port);
            } else {
                _ftp.connect(server);
            }
            _logger.log(Level.INFO, "Connected to " + server + " on " + (port>0 ? port : _ftp.getDefaultPort()));

            // After connection attempt, you should check the reply code to verify
            // success.
            reply = _ftp.getReplyCode();

            if (!FTPReply.isPositiveCompletion(reply))
            {
                DoDisconnect();
                _logger.log(Level.INFO, "FTP server refused connection.");
                //System.exit(1);
            }
        }
        catch (IOException e)
        {
            if (_ftp.isConnected())
            {
            	DoDisconnect();
            }
            _logger.log(Level.SEVERE, "Could not connect to server: " + e.getMessage());
            //System.exit(1);
        }
	}
	
	public boolean CheckDirectoryExists(String dirPath) throws IOException {
		int returnCode;
		
		_ftp.changeWorkingDirectory(dirPath);
        returnCode = _ftp.getReplyCode();
        if (returnCode == 550) {
            return false;
        }
        return true;
    }
	
	public void DoMakeDirectory(String dirToCreate, boolean isDoLogout, boolean isDoDisconnect){
		
		boolean success;
		try {
			
			success = _ftp.makeDirectory(dirToCreate);
			ShowServerReply();
	        if (success) {
	        	_logger.log(Level.INFO, "Successfully created directory: " + dirToCreate);
	        } else {
	        	_logger.log(Level.SEVERE, "Failed to create directory. See server's reply.");
	        }
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			_logger.log(Level.SEVERE, "DoMakeDirectory := IOException . " + e.getMessage());
		}
        
		if(isDoLogout)
			DoLogout();
		if(isDoDisconnect)
			DoDisconnect();
    }
	
	private void ShowServerReply() {
		
		String[] replies = _ftp.getReplyStrings();
		
		if (replies != null && replies.length > 0) {
			for (String aReply : replies) {
				_logger.log(Level.INFO, "FTP SERVER: " + aReply);
			}
		}
	}

	public void DoLogin(String username , String password) {
		try
        {
            if (!_ftp.login(username, password))
            {
                DoLogout();
                _logger.log(Level.SEVERE, "Could not login. ");
            }else{
            	EnterLocalPassiveMode();
            	_ftp.setFileType(FTP.BINARY_FILE_TYPE);
            }
            
            _logger.log(Level.INFO, "Remote system is " + _ftp.getSystemType());
            

            //_ftp.noop(); // check that control connection is working OK

            //_ftp.logout();
        }
        catch (FTPConnectionClosedException e)
        {
        	_logger.log(Level.SEVERE, "Server closed connection. " + e.getMessage());
        }
        catch (IOException e)
        {
        	_logger.log(Level.SEVERE, "IOException . " + e.getMessage());
        }
        /*finally
        {
            if (_ftp.isConnected())
            {
                try
                {
                    DoDisconect();
                }
                catch (IOException f)
                {
                    // do nothing
                }
            }
        }*/

      //  System.exit(error ? 1 : 0);
     // end main
	}
	
	public FTPFile[] DoDir(String remotePath, String dirCommand, String serverRootPath, int fileType , int siteID, FTPFileFilter filter) {
		// Allow multiple list types for single invocation
		//mlsd 		- list directory details using MLSD (remote is used as the pathname if provided)
		//listFiles - list files using LIST (remote is used as the pathname if provided)
		//mdtm 		- list file details using MDTM (remote is used as the pathname if provided)
		//mlst		- list file details using MLST (remote is used as the pathname if provided)
		//listNames - list file names using NLST (remote is used as the pathname if provided)	
    	//ArrayList<FtpFile> result = new ArrayList<FtpFile>();
		FTPFile[] listedFiles = null;
		
		if (dirCommand.equals("listFiles") || dirCommand.equals("mlsd") || dirCommand.equals("mdtm")
				|| dirCommand.equals("mlst") || dirCommand.equals("listNames")) {
			try {
					_logger.log(Level.INFO, "Site ID: =  " + siteID + " Dir folder: = " + remotePath);				

					switch (dirCommand) {
					/*case "mlsd":
						listedFiles = _ftp.mlistDir(remotePath,filter);
						break;
					case "mdtm":
						f = _ftp.mdtmFile(remotePath);
						if (f != null) {
							System.out.println(f.getRawListing());
							// System.out.println(f.toFormattedString(displayTimeZoneId));
						} else {
							System.out.println("File not found");
						}
						break;
					case "mlst":
						f = _ftp.mlistFile(remotePath);
						if (f != null) {
							// System.out.println(f.toFormattedString(displayTimeZoneId));
						}
						break;
					case "listNames":
						for (String s : _ftp.listNames(remotePath)) {
							System.out.println(s);
						}
						break;*/
					case "listFiles":
						
						if(filter == null){
							filter = new FTPFileFilter() {
								
								@Override
								public boolean accept(FTPFile file) {
									return true;
								}
							};
						}
						listedFiles = _ftp.listFiles(remotePath,filter);
						//result = ConvertToResultArray(listedFiles,serverRootPath,remotePath,fileType,siteID);
						break;
					default:
						throw new IOException();
					}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				 _logger.log(Level.SEVERE, "FTP dir command failed " + e.getMessage());
			}
		}
		return listedFiles;
	}
	
	public ArrayList<FtpPlaceHolder> DoDirPlaceHolder(ArrayList<FtpPlaceHolder> _myServerPlaceHolders, int siteID) {
		FtpPlaceHolder cdrPlaceHolder = null;
		FtpPlaceHolder logdataPlaceHolder = null;
		FtpPlaceHolder meterPlaceHolder = null;		
		String placeHolder;
		
		if (_myServerPlaceHolders != null) {
			for (FtpPlaceHolder ph : _myServerPlaceHolders) {

				switch (ph.getFtpFileType()) {
				case 1:
					logdataPlaceHolder = ph;
					break;
				case 2:
					meterPlaceHolder = ph;
					break;
				case 3:
					cdrPlaceHolder = ph;
					break;
				default:
					break;
				}

			} 
		}else{
			_myServerPlaceHolders = new ArrayList<FtpPlaceHolder>();
		}
		try {
			
			if (logdataPlaceHolder == null) {
				placeHolder = FindNextPlaceHolder(Enums.FileType.OPERANDS_DATA.name(), "", siteID);
				
				if (placeHolder != null) {
					logdataPlaceHolder = new FtpPlaceHolder();
					logdataPlaceHolder.setFtpFileType(Enums.FileType.OPERANDS_DATA.ordinal());
					logdataPlaceHolder.setSiteId(siteID);
					logdataPlaceHolder.setFtpPlaceHolderFolder(placeHolder);
					_myServerPlaceHolders.add(logdataPlaceHolder);
				}
				
			}
				if (meterPlaceHolder == null) {
					placeHolder = FindNextPlaceHolder(Enums.FileType.METERS_DATA.name(), "", siteID);
					
					if (placeHolder != null) {
						meterPlaceHolder = new FtpPlaceHolder();
						meterPlaceHolder.setFtpFileType(Enums.FileType.METERS_DATA.ordinal());
						meterPlaceHolder.setSiteId(siteID);
						meterPlaceHolder.setFtpPlaceHolderFolder(placeHolder);
						_myServerPlaceHolders.add(meterPlaceHolder);
					}
					
				}
				
				if (cdrPlaceHolder == null) {
					placeHolder = FindNextPlaceHolder(Enums.FileType.CDR_DATA.name(), "", siteID);
					
					if (placeHolder != null) {
						cdrPlaceHolder = new FtpPlaceHolder();
						cdrPlaceHolder.setFtpFileType(Enums.FileType.CDR_DATA.ordinal());
						cdrPlaceHolder.setSiteId(siteID);
						cdrPlaceHolder.setFtpPlaceHolderFolder(placeHolder);
						_myServerPlaceHolders.add(cdrPlaceHolder);
					}
					
				} 
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			 _logger.log(Level.SEVERE, "FTP dir command failed : ." + e.getMessage());
		}
		//_myServerPlaceHolders.sort(Comparator.comparing(FtpPlaceHolder::getFtpPlaceHolderFolder));
		
		return _myServerPlaceHolders;
	}
				
	public String FindNextPlaceHolder(String remotePath, String currentDate, int siteID) {
		// TODO Auto-generated method stub
		String nextDate = null;
		FTPFile[] diredDirectories;	
		
		try {
			FTPFileFilter filter = new FTPFileFilter() {

				@Override
				public boolean accept(FTPFile ftpFile) {

					return (ftpFile.isDirectory() && IsDateFormat(ftpFile.getName()) && (currentDate.compareTo(ftpFile.getName()) <= 0) );
				}
			};

			diredDirectories = DoDir(remotePath, il.co.vor.FTP.defines.Constants.FTP_DIR_TYPE_COMMAND_LISTFILES, "",-1, siteID, filter);
			
			Arrays.sort(diredDirectories, Comparator.comparing(FTPFile::getName));
			
			if (currentDate.isEmpty() && diredDirectories.length > 0) {
				nextDate = diredDirectories[0].getName();
			}else{
				
				switch (diredDirectories.length) {
				case 0:
					nextDate = currentDate;
					break;
				case 1:
					nextDate = diredDirectories[0].getName();
					break;
				default:
					nextDate = diredDirectories[1].getName();
					break;
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			_logger.log(Level.SEVERE, "Failed in FindNextPlaceHolder");
		}
		
		return nextDate;
	}
	
	private boolean IsDateFormat(String text) {
		// TODO Auto-generated method stub
		boolean bRes = false;
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		df.setLenient(false);
		ParsePosition position = new ParsePosition(0);
	    Date date = df.parse(text,position);
	    
	    if ((null != date) && (position.getIndex() == text.length())){
	    	bRes = true;
	    }else{
	    	_logger.log(Level.WARNING, "Date could not be parsed: " + text + ", position: " + Integer.toString(position.getErrorIndex()));
	    }
	    
	    return bRes;    
	}

	public ArrayList<FtpFile> ConvertToResultArray(FTPFile[] ftpFiles, String serverRootPath,String remotePath , int fileType , int siteID) {
		// TODO Auto-generated method stub
		ArrayList<FtpFile> result = new ArrayList<FtpFile>();
		FtpFile newItem;
		String archiveFolder = remotePath.replace(FileType.values()[fileType].name(), "");
		String sFileName = "";
		String[] splitedFileName;
		int[] fileSizeAndRowQuantity;
		try {
			for (FTPFile ff : ftpFiles) {
				if (ff.isFile()) {
					sFileName = ff.getName();
					splitedFileName = sFileName.split("_");
					fileSizeAndRowQuantity = GetRowQuantityAndFileSize(splitedFileName);
					newItem = new FtpFile();
					newItem.setFtpFileSize(fileSizeAndRowQuantity[0]);
					newItem.setFtpFileName(sFileName);
					newItem.setFtpFileType(fileType);
					newItem.setFtpFileCurrentPhase(FilePhase.DIR.ordinal());
					newItem.setSiteId(siteID);
					newItem.setServiceId(ServiceParameters._myService.getServiceId());
					newItem.setFtpFileServerPath(serverRootPath + Constants.FILE_SEPARATOR + remotePath);
					newItem.setFtpFileRowQuantity(fileSizeAndRowQuantity[1]);
					newItem.setFtpFileDirPhaseStatus(FilePhaseStatus.DONE.ordinal());
					newItem.setFtpFileDwlPhaseStatus(FilePhaseStatus.NOTSTARTED.ordinal());
					newItem.setFtpFileUploadPhaseStatus(FilePhaseStatus.NOTSTARTED.ordinal());
					newItem.setFtpFileBulkPhaseStatus(FilePhaseStatus.NOTSTARTED.ordinal());
					newItem.setFtpFileArchivePhaseStatus(FilePhaseStatus.NOTSTARTED.ordinal());
					newItem.setFtpFileCleanPhaseStatus(FilePhaseStatus.NOTSTARTED.ordinal());
					newItem.setFtpFileArchiveFolder(archiveFolder);
					newItem.setFtpFileDestinationPath("");
					newItem.setFtpFileLocalPath("");
					result.add(newItem);
				}
			}
		} catch (NumberFormatException e) {
			_logger.log(Level.SEVERE, "FtpFileRowQuantity parse failed: " + e.getMessage());
		} catch (Exception e) {
			// TODO: handle exception
			_logger.log(Level.SEVERE, "ConvertToResultArray failed: " + e.getMessage());
		}
		return result;
	}

	private int[] GetRowQuantityAndFileSize(String[] splitedFileName) {
		// TODO Auto-generated method stub
		int[] res = new int[2];
		res[0] = -1;
		res[1] = -1;
		
		if(splitedFileName.length != 8){			
			return res;
		}
		
		if(SafeParser.tryParseInt(splitedFileName[splitedFileName.length - 2]))
			res[0] = Integer.parseInt(splitedFileName[splitedFileName.length - 2]);
		if(SafeParser.tryParseInt(splitedFileName[splitedFileName.length - 3]))
			res[1] = Integer.parseInt(splitedFileName[splitedFileName.length - 3]);
		return res;
	}

	public boolean DoStoreFile(String remotePath, String localPath) {
		InputStream input;
		boolean res = false;

		try {
			
			input = new FileInputStream(localPath);
			_ftp.storeFile(remotePath, input);		
			res = true;
			input.close();
			DoLogout();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			_logger.log(Level.SEVERE, "File " + localPath + " not found: " + e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			_logger.log(Level.SEVERE, "DoStoreFile: " + localPath + " IOException : " + e.getMessage());
		} finally {
		      if(_ftp.isConnected()) {
		          DoDisconnect();
		      }
		}
		return res;
	}
	
	public boolean DoRetrieveFile(String remotePath, String localPath) {
		OutputStream output;
		boolean res = false;

		try {

			output = new FileOutputStream(localPath);			
			_ftp.retrieveFile(remotePath, output);
			output.close();		
			res = true;
			DoLogout();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			_logger.log(Level.SEVERE, "File " + remotePath + " not found: " + e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			_logger.log(Level.SEVERE, "DoRetrieveFile: " + remotePath + " IOException : " + e.getMessage());
		} finally {
		      if(_ftp.isConnected()) {
		          DoDisconnect();
		      }
		}
		return res;
	}

	public void DoDisconnect() {
		try {
			  _ftp.disconnect();
		  } catch(IOException ioe) {
			  _logger.log(Level.INFO, "Disconect failed: " + ioe.getMessage());
		  }
	}
	
	public void DoLogout(){
		try {
			_ftp.logout();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			_logger.log(Level.INFO, "Logout failed: " + e.getMessage());
		}
	}
	
	public void EnterLocalPassiveMode(){
		_ftp.enterLocalPassiveMode();
	}
	
	public void EnterLocalActiveMode(){
		_ftp.enterLocalActiveMode();
	}
	
	public boolean ChangeWorkingDirectory(String dirPath){
		
		boolean res = false;
		@SuppressWarnings("unused")
		boolean _res = false;
		String sCurrentDir = null;
		try {
			sCurrentDir = _ftp.printWorkingDirectory();
			res = _ftp.changeWorkingDirectory(dirPath);
			_res = _ftp.changeWorkingDirectory(sCurrentDir);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			_logger.log(Level.INFO, "Change Working Directory failed: " + e.getMessage());
		}
		
		return res;
	}
	
	/**
     * Creates a nested directory structure on a FTP server
     * @param dirPath Path of the directory, i.e /projects/java/ftp/demo
     * @return true if the directory was created successfully, false otherwise
     * @throws IOException if any error occurred during client-server communication
     */
    public boolean DoMakeDirectories(String dirPath) throws IOException {
        String[] pathElements = dirPath.split("/");
        if (pathElements != null && pathElements.length > 0) {
            for (String singleDir : pathElements) {
                boolean existed = _ftp.changeWorkingDirectory(singleDir);
                if (!existed) {
                    boolean created = _ftp.makeDirectory(singleDir);
                    if (created) {
                    	_logger.log(Level.INFO, "CREATED directory: " + singleDir);
                        _ftp.changeWorkingDirectory(singleDir);
                    } else {
                    	_logger.log(Level.SEVERE, "COULD NOT create directory: " + singleDir);
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
