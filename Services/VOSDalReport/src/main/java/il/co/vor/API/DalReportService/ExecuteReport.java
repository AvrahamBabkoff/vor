package il.co.vor.API.DalReportService;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import il.co.vor.VOSDBConnection.SPExecuterAndJSONSerializer;
import il.co.vor.VOSDBConnection.SQLStatements;
import il.co.vor.common.Constants;
import il.co.vor.common.Enums;
import il.co.vor.common.VosConfigResources;
import il.co.vor.common.VosReportResources;

@Path(VosReportResources.REPORTS_NAME)
public class ExecuteReport {
	private static final Logger logger = Logger.getLogger(ExecuteReport.class.getName());

	private static final String m_strSpExecuteDefaultReportData = SQLStatements
			.getSqlStatement(SQLStatementsParamNames.SP_EXECUTE_DEFAULT_REPORT_DATA_NAME);
	private static final String m_strSpExecuteSavingsReportDataForReport = SQLStatements
			.getSqlStatement(SQLStatementsParamNames.SP_EXECUTE_SAVINGS_REPORT_DATA_FOR_REPORT_NAME);
	private static final String m_strSpExecuteSavingsReportData = SQLStatements
			.getSqlStatement(SQLStatementsParamNames.SP_EXECUTE_SAVINGS_REPORT_DATA_NAME);
	private static final String m_strSpExecuteEnergyReportData = SQLStatements
			.getSqlStatement(SQLStatementsParamNames.SP_EXECUTE_ENERGY_REPORT_DATA_NAME);
	private static final String m_strSpGetFactory = SQLStatements
			.getSqlStatement(SQLStatementsParamNames.SP_GET_FACTORY_NAME);
	private static final String m_strSpGetAllMeters = SQLStatements
			.getSqlStatement(SQLStatementsParamNames.SP_GET_ALL_METERS_NAME);
	private static final String m_strSpGetRawMeterData = SQLStatements
			.getSqlStatement(SQLStatementsParamNames.SP_GET_RAW_METER_DATA_NAME);
	private static final String m_strSpGetRawMeterDataCold = SQLStatements
			.getSqlStatement(SQLStatementsParamNames.SP_GET_RAW_METER_DATA_COLD_NAME);
	private static final String m_strSpGetRawMeterDataHot = SQLStatements
			.getSqlStatement(SQLStatementsParamNames.SP_GET_RAW_METER_DATA_HOT_NAME);
	private static final String m_strSpGetRawAreaData = SQLStatements
			.getSqlStatement(SQLStatementsParamNames.SP_GET_RAW_AREA_DATA_NAME);

	// SP_GET_FACTORY_NAME
	@GET
	@Produces("application/json")
	public Response executeReport(
			@DefaultValue("-1") @QueryParam(VosReportResources.FACTORIES_PROP_FACTORY_ID_NAME) int factoryId,
			@DefaultValue("01/01/2000") @QueryParam(VosReportResources.EXECUTE_REPORT_FROM_DATE_NAME) String fromDate,
			@DefaultValue("01/01/2000") @QueryParam(VosReportResources.EXECUTE_REPORT_TO_DATE_NAME) String toDate,
			@DefaultValue("-1") @QueryParam(VosReportResources.GRAPH_ID_NAME) int graphId) {
		String strJASONResponse = "";
		JSONObject factoryResult;
		JSONObject reportDataResult = null;
		JSONObject meta;
		JSONObject data;
		JSONArray jasonArrayFactories = null;
		JSONArray jsonArrayGraph = null;
		int templateId = -1;
		Response response;

		// System.out.println("fromDate: " + fromDate + ", toDate: " + toDate);
		logger.log(Level.SEVERE, String.format("fromDate: %s, toDate: %s", fromDate, toDate));
		SPExecuterAndJSONSerializer spExecFactory = new SPExecuterAndJSONSerializer();
		SPExecuterAndJSONSerializer spExecReportData = new SPExecuterAndJSONSerializer();

		// we need to get the report template for the given factory
		factoryResult = spExecFactory.setSP(m_strSpGetFactory).setParameters(factoryId)
				.setResultSetNames(VosReportResources.FACTORIES_NAME).ExecuteAndSerializeAsJSONObject(null);

		if ((null != factoryResult)
				&& ((meta = (JSONObject) factoryResult.get(Constants.JSON_ROOT_META_PROP_NAME)) != null)
				&& (meta.getInt(Constants.JSON_META_ERROR_PROP_NAME) == 0)
				&& ((data = (JSONObject) factoryResult.get(Constants.JSON_ROOT_DATA_PROP_NAME)) != null)
				&& (((jasonArrayFactories = data.getJSONArray(VosReportResources.FACTORIES_NAME)) != null))
				&& (jasonArrayFactories.length() == 1)) {
			templateId = jasonArrayFactories.getJSONObject(0)
					.getInt(VosReportResources.FACTORIES_PROP_FACTORY_REPORT_TEMPLATE_ID_NAME);
			logger.log(Level.SEVERE, String.format("template id: %d", templateId));
		} else {
			logger.log(Level.SEVERE, String.format("template id: %s", "failed to obtain"));
		}

		if (graphId > 0) // this is not a factory configuration calculation report, it is a graph report
		{
			jsonArrayGraph = new JSONArray();
			JSONObject obj = new JSONObject();
			obj.put(VosReportResources.GRAPH_ID_NAME, graphId);
			
			jsonArrayGraph.put(obj);
			
			if (graphId == 100) {
				reportDataResult = spExecReportData.setSP(m_strSpExecuteDefaultReportData)
						.setParameters(factoryId, fromDate, toDate, -1)
						.setResultSetNames(VosReportResources.AVERAGE_COP_NAME, VosReportResources.AVERAGE_COLD_NAME,
								VosReportResources.AVERAGE_HOT_NAME)
						.ExecuteAndSerializeAsJSONObject(null);
			}
		}
		else if (templateId >= 0) {
			// note: correction id sent as last parameter should be defined as a constant
			// or sent as a parameter to the API
			// note: should be switch, not if
			if (templateId == 3) {
				reportDataResult = spExecReportData.setSP(m_strSpExecuteSavingsReportDataForReport)
						.setParameters(factoryId, fromDate, toDate, -1)
						.setResultSetNames(VosReportResources.COOLING_ENERGY_NAME,
								VosReportResources.COOLING_ELECTRICITY_NAME,
								VosReportResources.COOLING_ELECTRICITY_TOTAL_NAME,
								VosReportResources.COOLING_SAVINGS_NAME, VosReportResources.HEATING_ENERGY_NAME,
								VosReportResources.HEATING_ELECTRICITY_NAME,
								VosReportResources.HEATING_ELECTRICITY_TOTAL_NAME,
								VosReportResources.HEATING_SAVINGS_NAME, VosReportResources.SAVINGS_TOTAL_NAME,
								VosReportResources.SAVINGS_PERCENTAGE_NAME)
						.ExecuteAndSerializeAsJSONObject(null);

			} else if (templateId == 2) {
				reportDataResult = spExecReportData.setSP(m_strSpExecuteSavingsReportData)
						.setParameters(factoryId, fromDate, toDate, -1)
						.setResultSetNames(VosReportResources.COOLING_ENERGY_NAME,
								VosReportResources.COOLING_ENERGY_TOTAL_NAME,
								VosReportResources.COOLING_ELECTRICITY_NAME,
								VosReportResources.COOLING_ELECTRICITY_TOTAL_NAME,
								VosReportResources.HEATING_ENERGY_NAME, VosReportResources.HEATING_ELECTRICITY_NAME,
								VosReportResources.HEATING_ELECTRICITY_TOTAL_NAME,
								VosReportResources.PAYMENT_SUB_TOTALS__NAME,
								VosReportResources.PAYMENT_GRAND_TOTAL_NAME, VosReportResources.PAYMENT_LABELS_NAME)
						.ExecuteAndSerializeAsJSONObject(null);

			} else if (templateId == 0) {
				reportDataResult = spExecReportData.setSP(m_strSpExecuteEnergyReportData)
						.setParameters(factoryId, fromDate, toDate, -1)
						.setResultSetNames(VosReportResources.ENERGY_NAME, VosReportResources.ENERGY_TOTAL_NAME,
								VosReportResources.OPERATION_COST_NAME, VosReportResources.OPERATION_COST_TOTAL_NAME,
								VosReportResources.SUMMARY_NAME, VosReportResources.SUMMARY_TOTAL_NAME,
								VosReportResources.ENERGY_BY_AREA_NAME)
						.ExecuteAndSerializeAsJSONObject(null);

			}
		}
		if ((null != reportDataResult) && (null != strJASONResponse)
				&& ((meta = (JSONObject) reportDataResult.get(Constants.JSON_ROOT_META_PROP_NAME)) != null)
				&& (meta.getInt(Constants.JSON_META_ERROR_PROP_NAME) == 0)
				&& ((data = (JSONObject) reportDataResult.get(Constants.JSON_ROOT_DATA_PROP_NAME)) != null)) {
			data.put(VosReportResources.FACTORIES_NAME, jasonArrayFactories);
			
			if (graphId >0)
			{
				data.put(VosReportResources.GRAPHS_NAME, jsonArrayGraph);
			}
			
			strJASONResponse = reportDataResult.toString();
		}

		// note: correction id sent as last parameter should be defined as a constant
		// strJASONResponse = spExecReportData.setSP(m_strSpExecuteSavingsReport).
		// setParameters(factoryId, fromDate, toDate, -1).
		// setResultSetNames(VosReportResources.COOLING_ENERGY_NAME,
		// VosReportResources.COOLING_ELECTRICITY_NAME,
		// VosReportResources.COOLING_ELECTRICITY_TOTAL_NAME,
		// VosReportResources.COOLING_SAVINGS_NAME,
		// VosReportResources.HEATING_ENERGY_NAME,
		// VosReportResources.HEATING_ELECTRICITY_NAME,
		// VosReportResources.HEATING_ELECTRICITY_TOTAL_NAME,
		// VosReportResources.HEATING_SAVINGS_NAME,
		// VosReportResources.SAVINGS_TOTAL_NAME,
		// VosReportResources.SAVINGS_PERCENTAGE_NAME).
		// ExecuteAndSerializeAsJSONString(null);
		// return strJASONResponse;
		if (!strJASONResponse.isEmpty()) {
			response = Response.status(Response.Status.OK).entity(strJASONResponse).build();
		} else {
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();

		}
		// return Response.status(Response.Status.OK)
		// .entity(strJASONResponse).build();
		return response;
		// return Response.ok(strJASONResponse).build();
	}

	private JSONObject copyMeterObject(JSONObject meter, Enums.MeterTempType tempType,
			SPExecuterAndJSONSerializer spExecSamplingData, String fromDate, String toDate) {
		int meterId;
		int _tempType;
		String meterDataResultSetName;
		JSONObject meterResult = new JSONObject();
		String sql = "";

		if (tempType == Enums.MeterTempType.None) {
			_tempType = meter.getInt(VosReportResources.METERS_PROP_METER_TEMP_TYPE_NAME);
			tempType = Enums.MeterTempType.getMeterTempTypeFromCode(_tempType);
			sql = m_strSpGetRawMeterData;
		} else if (tempType == Enums.MeterTempType.Cold) {
			sql = m_strSpGetRawMeterDataCold;
		} else if (tempType == Enums.MeterTempType.Hot) {
			sql = m_strSpGetRawMeterDataHot;
		}
		meterId = meter.getInt(VosReportResources.METERS_PROP_METER_ID_NAME);
		meterDataResultSetName = meterId + "_" + tempType.name();
		meterResult.put(VosReportResources.METERS_PROP_METER_ID_NAME, meterId);
		meterResult.put(VosReportResources.METERS_PROP_METER_NAME_NAME,
				meter.getString(VosReportResources.METERS_PROP_METER_NAME_NAME));
		meterResult.put(VosReportResources.METERS_PROP_METER_TYPE_DESCRIPTION_NAME,
				meter.getString(VosReportResources.METERS_PROP_METER_TYPE_DESCRIPTION_NAME));
		meterResult.put(VosReportResources.METERS_PROP_METER_TEMP_TYPE_DESCRIPTION_NAME, tempType.getDesc());
		meterResult.put("arrayName", meterDataResultSetName);
		spExecSamplingData.setParameters(meterId, fromDate, toDate, -1);
		spExecSamplingData.appendResultSetName(meterDataResultSetName);
		spExecSamplingData.appendStatement(sql);

		return meterResult;
	}

	@Path(VosReportResources.API_REPORTS_EXPORT_RAW_DATA)
	@GET
	@Produces("application/json")
	public Response exportRawReportData(
			@DefaultValue("-1") @QueryParam(VosReportResources.FACTORIES_PROP_FACTORY_ID_NAME) int factoryId,
			@DefaultValue("01/01/2000") @QueryParam(VosReportResources.EXECUTE_REPORT_FROM_DATE_NAME) String fromDate,
			@DefaultValue("01/01/2000") @QueryParam(VosReportResources.EXECUTE_REPORT_TO_DATE_NAME) String toDate,
			@DefaultValue("false") @QueryParam(VosReportResources.EXPORT_REPORT_INCLUDE_MONITOR) boolean includeMonitor) {

		String strJASONResponse = "";
		JSONObject metersResult;
		JSONObject samplingDataResult = null;
		JSONObject meta;
		JSONObject data;
		JSONObject meter;
		JSONArray jasonArrayMeters = null;
		JSONObject meterResult;
		JSONArray jasonArrayMetersResult = null;
		JSONObject meterMeta;
		JSONObject meterData;
		JSONArray jasonArrayMeterData = null;
		int tempType;
		int meterType;
		int meterId;
		String meterDataResultSetName;

		int i = 0;
		Response response;
		SPExecuterAndJSONSerializer spExecMeters;
		SPExecuterAndJSONSerializer spExecSamplingData;

		spExecMeters = new SPExecuterAndJSONSerializer();
		// SPExecuterAndJSONSerializer spExecSamplingData = new
		// SPExecuterAndJSONSerializer();
		// System.out.println("fromDate: " + fromDate + ", toDate: " + toDate);
		logger.log(Level.SEVERE, String.format("fromDate: %s, toDate: %s", fromDate, toDate));

		metersResult = spExecMeters.setSP(m_strSpGetAllMeters).setParameters(factoryId)
				.setResultSetNames(VosReportResources.METERS_NAME).ExecuteAndSerializeAsJSONObject(null);

		if ((null != metersResult)
				&& ((meta = (JSONObject) metersResult.get(Constants.JSON_ROOT_META_PROP_NAME)) != null)
				&& (meta.getInt(Constants.JSON_META_ERROR_PROP_NAME) == 0)
				&& ((data = (JSONObject) metersResult.get(Constants.JSON_ROOT_DATA_PROP_NAME)) != null)
				&& (((jasonArrayMeters = data.getJSONArray(VosReportResources.METERS_NAME)) != null))
				&& (jasonArrayMeters.length() > 0)) {
			jasonArrayMetersResult = new JSONArray();
			spExecSamplingData = new SPExecuterAndJSONSerializer();
			// for (i = 0; i < jasonArrayMeters.length(); i++) {
			// spExecSamplingData.appendStatement(m_strSpGetRawMeterData);
			// }
			// add query for area
			spExecSamplingData.appendStatement(m_strSpGetRawAreaData);
			spExecSamplingData.setParameters(factoryId, fromDate, toDate, -1);
			spExecSamplingData.appendResultSetName(VosReportResources.AREA_NAME);

			for (i = 0; i < jasonArrayMeters.length(); i++) {
				meter = jasonArrayMeters.getJSONObject(i);
				meterType = meter.getInt(VosReportResources.METERS_PROP_METER_TYPE_NAME);
				if ((false == includeMonitor) && (meterType == Enums.MeterType.ElectricityForMonitor.ordinal()
						|| meterType == Enums.MeterType.EnergyForMonitor.ordinal())) {
					continue;
				}
				meterId = meter.getInt(VosReportResources.METERS_PROP_METER_ID_NAME);
				tempType = meter.getInt(VosReportResources.METERS_PROP_METER_TEMP_TYPE_NAME);
				if (tempType == Enums.MeterTempType.SelectByPlc.getCode()) {
					meterResult = copyMeterObject(meter, Enums.MeterTempType.Cold, spExecSamplingData, fromDate,
							toDate);
					jasonArrayMetersResult.put(meterResult);

					meterResult = copyMeterObject(meter, Enums.MeterTempType.Hot, spExecSamplingData, fromDate, toDate);
					jasonArrayMetersResult.put(meterResult);
				} else {
					meterResult = copyMeterObject(meter, Enums.MeterTempType.None, spExecSamplingData, fromDate,
							toDate);
					jasonArrayMetersResult.put(meterResult);
				}
			}
			samplingDataResult = spExecSamplingData.ExecuteAndSerializeAsJSONObject(null);
			if ((null != samplingDataResult)
					&& ((meterMeta = (JSONObject) samplingDataResult.get(Constants.JSON_ROOT_META_PROP_NAME)) != null)
					&& (meterMeta.getInt(Constants.JSON_META_ERROR_PROP_NAME) == 0)
					&& ((meterData = (JSONObject) samplingDataResult
							.get(Constants.JSON_ROOT_DATA_PROP_NAME)) != null)) {
				meterData.put(VosReportResources.METERS_NAME, jasonArrayMetersResult);
				strJASONResponse = samplingDataResult.toString();
			}
			// strJASONResponse = metersResult.toString();
			// logger.log(Level.SEVERE, String.format("number of meters id: %d",
			// jasonArrayMeters.length()));
		} else {
			logger.log(Level.SEVERE, String.format("template id: %s", "failed to obtain"));
		}

		if (!strJASONResponse.isEmpty()) {
			response = Response.status(Response.Status.OK).entity(strJASONResponse).build();
		} else {
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();

		}
		// return Response.status(Response.Status.OK)
		// .entity(strJASONResponse).build();
		return response;

	}

	// @Path(VosReportResources.API_REPORTS_EXPORT_RAW_DATA)
	// @GET
	// @Produces ("application/json")
	public Response exportRawReportDataOrig(
			@DefaultValue("-1") @QueryParam(VosReportResources.FACTORIES_PROP_FACTORY_ID_NAME) int factoryId,
			@DefaultValue("01/01/2000") @QueryParam(VosReportResources.EXECUTE_REPORT_FROM_DATE_NAME) String fromDate,
			@DefaultValue("01/01/2000") @QueryParam(VosReportResources.EXECUTE_REPORT_TO_DATE_NAME) String toDate,
			@DefaultValue("false") @QueryParam(VosReportResources.EXPORT_REPORT_INCLUDE_MONITOR) boolean includeMonitor) {
		String strJASONResponse = "";
		JSONObject metersResult;
		JSONObject samplingDataResult = null;
		JSONObject meta;
		JSONObject data;
		JSONObject meter;
		JSONArray jasonArrayMeters = null;
		JSONObject meterMeta;
		JSONObject meterData;
		JSONArray jasonArrayMeterData = null;
		int tempType;
		int meterId;
		String meterDataResultSetName;

		int i = 0;
		Response response;
		SPExecuterAndJSONSerializer spExecMeters;
		SPExecuterAndJSONSerializer spExecSamplingData;

		spExecMeters = new SPExecuterAndJSONSerializer();
		// SPExecuterAndJSONSerializer spExecSamplingData = new
		// SPExecuterAndJSONSerializer();
		// System.out.println("fromDate: " + fromDate + ", toDate: " + toDate);
		logger.log(Level.SEVERE, String.format("fromDate: %s, toDate: %s", fromDate, toDate));

		metersResult = spExecMeters.setSP(m_strSpGetAllMeters).setParameters(factoryId)
				.setResultSetNames(VosReportResources.METERS_NAME).ExecuteAndSerializeAsJSONObject(null);

		if ((null != metersResult)
				&& ((meta = (JSONObject) metersResult.get(Constants.JSON_ROOT_META_PROP_NAME)) != null)
				&& (meta.getInt(Constants.JSON_META_ERROR_PROP_NAME) == 0)
				&& ((data = (JSONObject) metersResult.get(Constants.JSON_ROOT_DATA_PROP_NAME)) != null)
				&& (((jasonArrayMeters = data.getJSONArray(VosReportResources.METERS_NAME)) != null))
				&& (jasonArrayMeters.length() > 0)) {
			for (i = 0; i < jasonArrayMeters.length(); i++) {
				meter = jasonArrayMeters.getJSONObject(i);
				meterId = meter.getInt(VosReportResources.METERS_PROP_METER_ID_NAME);
				tempType = meter.getInt(VosReportResources.METERS_PROP_METER_TEMP_TYPE_NAME);
				meterDataResultSetName = meterId + "_" + tempType;
				meter.put("arrayName", meterDataResultSetName);
				spExecSamplingData = new SPExecuterAndJSONSerializer();
				samplingDataResult = spExecSamplingData.setSP(m_strSpGetRawMeterData)
						.setParameters(meterId, fromDate, toDate, -1)
						.setResultSetNames(VosReportResources.METERS_DATA_NAME).ExecuteAndSerializeAsJSONObject(null);
				if ((null != samplingDataResult)
						&& ((meterMeta = (JSONObject) samplingDataResult
								.get(Constants.JSON_ROOT_META_PROP_NAME)) != null)
						&& (meterMeta.getInt(Constants.JSON_META_ERROR_PROP_NAME) == 0)
						&& ((meterData = (JSONObject) samplingDataResult
								.get(Constants.JSON_ROOT_DATA_PROP_NAME)) != null)
						&& (((jasonArrayMeterData = meterData
								.getJSONArray(VosReportResources.METERS_DATA_NAME)) != null))) {
					data.put(meterDataResultSetName, jasonArrayMeterData);
				}
			}
			strJASONResponse = metersResult.toString();
			// logger.log(Level.SEVERE, String.format("number of meters id: %d",
			// jasonArrayMeters.length()));
		} else {
			logger.log(Level.SEVERE, String.format("template id: %s", "failed to obtain"));
		}

		if (!strJASONResponse.isEmpty()) {
			response = Response.status(Response.Status.OK).entity(strJASONResponse).build();
		} else {
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();

		}
		// return Response.status(Response.Status.OK)
		// .entity(strJASONResponse).build();
		return response;

	}

}
