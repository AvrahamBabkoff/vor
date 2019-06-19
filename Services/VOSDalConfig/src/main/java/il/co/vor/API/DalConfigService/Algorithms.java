package il.co.vor.API.DalConfigService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

import il.co.vor.ApiObjectsCommon.ApiMultiResultWrapper;
import il.co.vor.DalDataObjects.FtpFile;
import il.co.vor.common.Constants;

import il.co.vor.common.VosConfigResources;
import il.co.vor.utilities.PropertyFileReader;
import il.co.vor.utilities.XMLParser;

@Path(VosConfigResources.ALGORITHMS_NAME)
public class Algorithms {
	private static final Logger logger = Logger.getLogger(Algorithms.class.getName());
	private static ObjectMapper m_mapper = null;
	private static TypeReference<ApiMultiResultWrapper<FtpFile>> m_ref = null;
	private static String OS = System.getProperty("os.name").toLowerCase();
	
	@SuppressWarnings("unused")
	private static final boolean bInitialize = _init();

	private static boolean _init() {
		m_mapper = new ObjectMapper();
		AnnotationIntrospector aiJaxb = new JaxbAnnotationIntrospector(TypeFactory.defaultInstance());
		AnnotationIntrospector aiJackson = new JacksonAnnotationIntrospector();
		// first Jaxb, second Jackson annotations
		m_mapper.setAnnotationIntrospector(AnnotationIntrospector.pair(aiJaxb, aiJackson));

		m_ref = new TypeReference<ApiMultiResultWrapper<FtpFile>>() {
		};

		return true;
	}

	private String GetConfigPath(int _site_id) {
		String sPath = "";
		String sCommand = "";
		String xmlConfigPath = "";
			
		try {
			String folderPath = System.getProperty("user.home") + Constants.FILE_SEPARATOR
					+ Constants.ALGO_CONFIG_ROOT_FOLDER;

			String xmlSourcesPath = folderPath
					+ PropertyFileReader.getProperty(Constants.PROP_NAME_ALGORITHMS_CONFIG_SOURCES);
			XMLParser file = new XMLParser(xmlSourcesPath);
			String sMountLetter = file.GetElementContentByExpression(
					"/Factories/Factory[@factory_id='" + String.valueOf(_site_id) + "']/MountLetter/text()");
			String sNetUseRootFolder = file.GetElementContentByExpression(
					"/Factories/Factory[@factory_id='" + String.valueOf(_site_id) + "']/NetUseRootFolder/text()");
			String sNetUseUser = file.GetElementContentByExpression(
					"/Factories/Factory[@factory_id='" + String.valueOf(_site_id) + "']/NetUseUser/text()");
			String sNetUsePass = file.GetElementContentByExpression(
					"/Factories/Factory[@factory_id='" + String.valueOf(_site_id) + "']/NetUsePass/text()");
			
			if (OS.indexOf("win") >= 0) // windows
			{
				xmlConfigPath = String.format("%s:%s", sMountLetter,
					PropertyFileReader.getProperty(Constants.PROP_NAME_ALGORITHMS_CONFIG_FILE));
			}
			else // linux
			{
				xmlConfigPath = String.format("%s%s%s", Constants.FILE_SEPARATOR, sMountLetter,
						PropertyFileReader.getProperty(Constants.PROP_NAME_ALGORITHMS_CONFIG_FILE));
			}

			File f = new File(xmlConfigPath);
			if (!f.exists()) { // mount directory
				if (OS.indexOf("win") >= 0) // windows
				{
					sCommand = String.format("%s %s %s /user:%s %s",
						folderPath + PropertyFileReader.getProperty(Constants.PROP_NAME_ALGORITHMS_WIN_MOUNT_FILE),
						sMountLetter, sNetUseRootFolder, sNetUseUser, sNetUsePass);
				}
				else // linux
				{
					String separator = "\\";
					String[] userparts = sNetUseUser.replaceAll(Pattern.quote(separator), "\\\\").split("\\\\");
					sCommand = String.format("%s '%s' %s '%s' '%s' '%s'",
							folderPath + PropertyFileReader.getProperty(Constants.PROP_NAME_ALGORITHMS_LINUX_MOUNT_FILE),
							sNetUseRootFolder, sMountLetter, userparts[1], sNetUsePass, userparts[0]);
				}
				Process p = Runtime.getRuntime().exec(sCommand);
				f = new File(xmlConfigPath);
				if (f.exists()) {
					sPath = xmlConfigPath;
				}

			} else {
				sPath = xmlConfigPath;
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE, String.format("Exception. %s", e.getMessage()));

		} catch (Exception e) {
			logger.log(Level.SEVERE, String.format("Exception. %s", e.getMessage()));
		}

		return sPath;
	}
	
	
	private JSONArray CorrectedResult(JSONObject _src) {

		String name = "";
		Double value;
		JSONObject valObj = null;
		JSONObject newItem = null;
		JSONArray newArray = new JSONArray();
		
		try {
			JSONObject obj = (JSONObject) _src.getJSONObject("ArrayOfConstParamModel");
			JSONArray objarr = (JSONArray) obj.getJSONArray("ConstParamModel");

			newItem = new JSONObject();
			
	        for (Object o : objarr) {
	            JSONObject item = (JSONObject) o;
	            name = item.getString("ParamKey");
	            valObj = item.getJSONObject("ParamValue");
	            value = valObj.getDouble("content");
	        
	            //newItem = new JSONObject();
	            newItem.put(name,value);
	            
	        }
	        newArray.put(newItem);
		} catch (Exception e) {
			logger.log(Level.SEVERE, String.format("Exception. %s", e.getMessage()));
		}

		return newArray;
	}
	

	@Path(VosConfigResources.API_ALGORITHMS_CONFIG)
	@GET
	@Produces("application/json")
	public String GetAlgorithmsConfig(
			@DefaultValue("-1") @QueryParam(VosConfigResources.SITES_PROP_SITE_ID_NAME) int siteId) {

		int iErrorCode = 0;
		String sErrorDescription = "";
		JSONObject jsonResult = null;
		JSONObject jsonMeta = null;
		JSONObject jsonData = null;
		JSONArray newData = null;

		jsonMeta = new JSONObject();
		jsonResult = new JSONObject();

		//jsonResult.put(Constants.JSON_ROOT_DATA_PROP_NAME, jsonData);
		jsonResult.put(Constants.JSON_ROOT_META_PROP_NAME, jsonMeta);
		
		String fileConfigPath = GetConfigPath(siteId);

		if (fileConfigPath != "") {
			try {
				String xml = null;
				File file = new File(fileConfigPath);
				FileInputStream fin;

				fin = new FileInputStream(file);
				byte[] xmlData = new byte[(int) file.length()];
				fin.read(xmlData);
				fin.close();
				xml = new String(xmlData, "UTF-16");
				jsonData = XML.toJSONObject(xml);
				newData = CorrectedResult(jsonData);
				jsonResult.put(Constants.JSON_ROOT_DATA_PROP_NAME, newData);
			} catch (FileNotFoundException e) {
				logger.log(Level.SEVERE, String.format("Exception. %s", e.getMessage()));
				iErrorCode = -1;
				sErrorDescription = e.getMessage();
				jsonResult.put(Constants.JSON_ROOT_DATA_PROP_NAME, JSONObject.NULL);
			} catch (IOException e) {
				logger.log(Level.SEVERE, String.format("Exception. %s", e.getMessage()));
				iErrorCode = -1;
				sErrorDescription = e.getMessage();
				jsonResult.put(Constants.JSON_ROOT_DATA_PROP_NAME, JSONObject.NULL);
			}
		}
		else
		{
			iErrorCode = -1;
			sErrorDescription = "Cannot find config data";
		}
		// System.out.println(org.json.XML.toJSONObject(xml).toString());
		jsonMeta.put(Constants.JSON_META_ERROR_PROP_NAME, iErrorCode);
		jsonMeta.put(Constants.JSON_META_MESSAGE_PROP_NAME, sErrorDescription);

		return jsonResult.toString();
	}

}
