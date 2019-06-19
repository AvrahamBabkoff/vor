package il.co.vor.defines;

public class Constants {
	
	public static final String SITE_NAME_COLUMN_NAME = "Site Name";
	public static final String SERVICE_TYPE_NAME_COLUMN_NAME = "Type";
	public static final String SERVICE_ID_COLUMN_NAME = "Service ID";
	public static final String SERVICE_NAME_COLUMN_NAME = "Service Name";
	public static final String SERVICE_DESCRIPTION_COLUMN_NAME = "Description";
	public static final String SERVICE_ADDRESS_IP_COLUMN_NAME = "Address IP";
	public static final String SERVICE_PORT_COLUMN_NAME = "Port";
	public static final String SERVICE_STATUS_COLUMN_NAME = "Status";
	public static final String SERVICE_LOG_LEVEL_NAME = "Log Level";
	
	public static final int SERVICE_ID_COLUMN_NUMBER = 0;
	public static final int SERVICE_TYPE_NAME_COLUMN_NUMBER = 1;
	public static final int SERVICE_NAME_COLUMN_NUMBER = 2;
	public static final int SERVICE_DESCRIPTION_COLUMN_NUMBER = 3;
	public static final int SERVICE_ADDRESS_IP_COLUMN_NUMBER = 4;
	public static final int SERVICE_PORT_COLUMN_NUMBER = 5;
	public static final int SERVICE_STATUS_COLUMN_NUMBER = 6;
	public static final int SERVICE_LOG_LEVEL_NUMBER = 7;
	public static final int SITE_NAME_COLUMN_NUMBER = 8;
	

	public static final int REFRESH_INTERVAL = 10;
	public static final String ERROR_IMAGE_PATH = "/error.png";
	public static final String SUCCESS_IMAGE_PATH = "/success.png";	
	public static final String START_SERVICE_DEFAULT_COMMAND = "SC.exe \\\\%s Start %s";
	public static final String PROP_NAME_START_SERVICE_COMMAND = "SM.StartServiceCommand";
	
	public static final int MESSAGE_ROWS_LIMIT = 10000;
	public static final int REMOVE_ROWS_AMOUNT_PERCENTAGE = 10;

}
