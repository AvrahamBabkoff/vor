package il.co.vor.common;

public class VosReportResources {
	public static final String USERS_NAME = "users";
	public static final String USERS_PROP_USER_ID_NAME = "user_id";
	public static final String USERS_PROP_USER_NAME_NAME = "user_name";
	public static final String USERS_PROP_USER_PASSWORD_NAME = "user_password";
	public static final String USERS_PROP_USER_LEVEL_NAME = "user_level";
	public static final String USERS_PROP_ENABLED_NAME = "user_enabled";	
	
	public static final String FACTORIES_NAME = "factories";
	public static final String FACTORIES_PROP_FACTORY_ID_NAME = "factory_id";
	public static final String FACTORIES_PROP_FACTORY_NAME_NAME = "factory_name";
	public static final String FACTORIES_PROP_FACTORY_REPORT_TEMPLATE_ID_NAME = "CalculationConfiguration";
	
	public static final String METERS_NAME = "meters";
	public static final String METERS_PROP_METER_ID_NAME = "MeterId";
	public static final String METERS_PROP_METER_NAME_NAME = "MeterName";
	public static final String METERS_PROP_METER_TYPE_NAME = "MeterType";
	public static final String METERS_PROP_METER_TYPE_DESCRIPTION_NAME = "MeterTypeDescription";
	public static final String METERS_PROP_METER_TEMP_TYPE_NAME = "MeterTempType";
	public static final String METERS_PROP_METER_TEMP_TYPE_DESCRIPTION_NAME = "MeterTempTypeDescription";
	public static final String METERS_PROP_METER_MAX_CONSUMING_PER_RECORD_NAME = "MaxConsumingPerRecord";
	
	public static final String METERS_DATA_NAME = "MetersData";
	
	public static final String AREA_NAME = "area";

	public static final String DEPARTMENTS = "departments";
	public static final String DEPARTMENTS_PROP_DEPARTMENT_ID_NAME = "DepartmentId";
	public static final String DEPARTMENTS_PROP_DEPARTMENT_NAME_NAME = "DepartmentName";
	

	public static final String API_USERS_GET_FACTORIES_URI_TEMPLATE = "{" + USERS_PROP_USER_ID_NAME + "}/" + FACTORIES_NAME;
	public static final String API_REPORTS_EXPORT_RAW_DATA = "export_raw_data";
	
	public static final String EXPORT_REPORT_INCLUDE_MONITOR = "monitor";
	
	public static final String REPORTS_NAME = "reports";
	public static final String GRAPHS_NAME = "graphs";
	
	public static final String EXECUTE_REPORT_FROM_DATE_NAME = "from";
	public static final String EXECUTE_REPORT_TO_DATE_NAME = "to";
	
	public static final String GRAPH_ID_NAME = "graph_id";
	
	public static final String AVERAGE_COP_NAME = "average_cop";
	public static final String AVERAGE_COLD_NAME = "average_cold";
	public static final String AVERAGE_HOT_NAME = "average_hot";
	
	public static final String COOLING_ENERGY_NAME = "cooling_energy";
	public static final String COOLING_ENERGY_TOTAL_NAME = "cooling_energy_total";
	public static final String COOLING_ELECTRICITY_NAME = "cooling_electricity";
	public static final String COOLING_ELECTRICITY_TOTAL_NAME = "cooling_electricity_total";
	public static final String COOLING_SAVINGS_NAME = "cooling_savings";
	

	public static final String HEATING_ENERGY_NAME = "heating_energy";
	public static final String HEATING_ENERGY_TOTAL_NAME = "heating_energy_total";
	public static final String HEATING_ELECTRICITY_NAME = "heating_electricity";
	public static final String HEATING_ELECTRICITY_TOTAL_NAME = "heating_electricity_total";
	public static final String HEATING_SAVINGS_NAME = "heating_savings";
	
	public static final String SAVINGS_TOTAL_NAME = "savings_total";
	public static final String SAVINGS_PERCENTAGE_NAME = "savings_percentage";
	
	public static final String PAYMENT_SUB_TOTALS__NAME = "payment_sub_totals";
	public static final String PAYMENT_GRAND_TOTAL_NAME = "payment_grand_total";
	public static final String PAYMENT_LABELS_NAME = "payment_labels";
	
	public static final String ENERGY_NAME = "energy";
	public static final String ENERGY_TOTAL_NAME = "energy_total";

	public static final String OPERATION_COST_NAME = "operation_cost";
	public static final String OPERATION_COST_TOTAL_NAME = "operation_cost_total";

	public static final String SUMMARY_NAME = "summary";
	public static final String SUMMARY_TOTAL_NAME = "summary_total";
	
	public static final String ENERGY_BY_AREA_NAME = "energy_by_area";
	
	public static final String ALGORITHMS_CURR_NAME = "current_algorithm";
	
}
