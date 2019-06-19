package il.co.vor.Modbus;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import il.co.vor.ApiObjectsCommon.ApiMultiResultWrapper;
import il.co.vor.DalDataClient.DalDataClient;
import il.co.vor.DalDataObjects.CalcOperandProperty;
import il.co.vor.Modbus.ICalcOperand;
import il.co.vor.common.VosDataResources;

public final class CalculatedOperands1 {

	private static Logger _logger = Logger.getLogger(CalcOperandsCompiler.class.getName());

	private static Map<Integer, OperandWrapper> m_operands_values = null;
	private static Map<String, Integer> m_operands_id_name = null;
	private static Map<String, CalcOperandProperty> m_properties = new HashMap<String, CalcOperandProperty>();
	private static ApiMultiResultWrapper<CalcOperandProperty> amrwrproperties = null;

	public static NumberFormat formatter = new DecimalFormat("##.#");
	
	public static void SetOperandsVals(Map<Integer, OperandWrapper> m_curr_operand_values) {
		m_operands_values = m_curr_operand_values;

	}
	
	public static void SetOperandsNames(HashMap<String, Integer> _operands_id_name) {
		m_operands_id_name = _operands_id_name;
	}
	
	public static void SetProperties(HashMap <String, CalcOperandProperty> properties) {
		m_properties = properties;
	}

	public static boolean LoadProperties(int service_id) {
		boolean ret = false;
		ArrayList<CalcOperandProperty> _properties = null;
		CalcOperandProperty _property = null;

		try {
			m_properties.clear();
			DalDataClient dcc = DalDataClient.getInstance();
			amrwrproperties = dcc.getCalcOperandsProperties().getCalcOperandsPropertiesObject(service_id);
			if (amrwrproperties != null) {
				_properties = amrwrproperties.getApiData().get(VosDataResources.CALC_OPERANDS_PROPERTIES_NAME);
				if ((_properties != null) && (_properties.size() > 0)) {
					for (int i = 0; i < _properties.size(); i++) {
						_property = _properties.get(i);
						m_properties.put(_property.getPropertyName(), _property);
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			_logger.log(Level.SEVERE, String.format("Exception: %s", e.getMessage()));
		}
		return ret;
	}

	public static String GetPropertyVal(String name) {
		String ret = "";
		CalcOperandProperty cop = m_properties.get(name);
		if (cop == null) {
			_logger.log(Level.SEVERE, String.format("Cannot find property: %s", name));
		}
		ret = m_properties.get(name).getPropertyVal();
		return ret;
	}

	public static String GetOperandBasicVal(String name) {
		String ret = "";
		int i = m_operands_id_name.get(name);
		OperandWrapper op = m_operands_values.get(i);
		if (op == null) {
			_logger.log(Level.SEVERE, String.format("Cannot read value for operand: %s", name));
		}
		ret = op.GetLogValAsStr();
		return ret;
	}

	public static class Chiller_1_Load implements ICalcOperand {

		private static int m_operand_id = 3350;
		private static int m_calc_operand_id = 3;

		public int GetOperandID() {
			return m_operand_id;
		}

		public int GetCalcOperandID() {
			return m_calc_operand_id;
		}
		
		public String GetOperandBasicStrVal(String name) {
			return CalculatedOperands1.GetOperandBasicVal(name);
		}
		
		public double GetOperandBasicDoubleVal(String name) {
			return  Double.parseDouble(GetOperandBasicVal(name));
		}

		public String Format(double val)
		{
			return CalculatedOperands1.formatter.format(val);
		}
		
		@Override
		public String Calculate(long ulReadCount, ZonedDateTime dtReadTime) {
			String sRes = "";
			double dValue = 0.0;
			// Water_Inlet_Chiller_1 152
			// Water_Outlet_Chiller_1 153
			
			double Water_Inlet_Chiller_1 = GetOperandBasicDoubleVal("Water_Inlet_Chiller_1");
			double Water_Outlet_Chiller_1 = GetOperandBasicDoubleVal("Water_Outlet_Chiller_1");
			

			dValue = (100*((Water_Inlet_Chiller_1/10 - Water_Outlet_Chiller_1/10)*80*1000)/(486*860));
			sRes = Format(dValue);

			return sRes;
		}


		@Override
		public String Update(double dValue, ZonedDateTime dt, double dPrevValue) {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	public static class Chiller_2_Load implements ICalcOperand {
		private static int m_operand_id = 3351;
		private static int m_calc_operand_id = 4;


		public int GetOperandID() {
			return m_operand_id;
		}

		public int GetCalcOperandID() {
			return m_calc_operand_id;
		}
		
		public String GetOperandBasicStrVal(String name) {
			return CalculatedOperands1.GetOperandBasicVal(name);
		}
		
		public double GetOperandBasicDoubleVal(String name) {
			return  Double.parseDouble(GetOperandBasicVal(name));
		}

		public String Format(double val)
		{
			return CalculatedOperands1.formatter.format(val);
		}
		
		@Override
		public String Calculate(long ulReadCount, ZonedDateTime dtReadTime) {
			String sRes = "";
			double dValue = 0.0;
			// Water_Inlet_Chiller_2 154
			// Water_Outlet_Chiller_2 155
			
			double Water_Inlet_Chiller_2 = GetOperandBasicDoubleVal("Water_Inlet_Chiller_2");
			double Water_Outlet_Chiller_2 = GetOperandBasicDoubleVal("Water_Outlet_Chiller_2");
			
			dValue = (100*((Water_Inlet_Chiller_2/10 - Water_Outlet_Chiller_2/10)*80*1000)/(486*860));
			sRes = Format(dValue);

			return sRes;
		}

		@Override
		public String Update(double dValue, ZonedDateTime dt, double dPrevValue) {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	public static class CP1_5_Mode implements ICalcOperand {
		private static int m_operand_id = 3352;
		private static int m_calc_operand_id = 5;


		public int GetOperandID() {
			return m_operand_id;
		}

		public int GetCalcOperandID() {
			return m_calc_operand_id;
		}
		
		public String GetOperandBasicStrVal(String name) {
			return CalculatedOperands1.GetOperandBasicVal(name);
		}
		
		public double GetOperandBasicDoubleVal(String name) {
			return  Double.parseDouble(GetOperandBasicVal(name));
		}

		public String Format(double val)
		{
			return CalculatedOperands1.formatter.format(val);
		}
		
		@Override
		public String Calculate(long ulReadCount, ZonedDateTime dtReadTime) {
			String sRes = "";
			double dValue = 0.0;
			
			double dFlow = 0.0;
			
            if (GetOperandBasicDoubleVal("Run_circulation_pump_2") == 1)
            {
                dFlow = GetOperandBasicDoubleVal("Energy_Meter_5_Volum_Flow");
                if ((dFlow >= 149) && (dFlow <= 151))
                {
                    dValue = 150;
                }
                else
                {
                    dValue = dFlow;
                }
            }
            else
            {
                if (GetOperandBasicDoubleVal("sp_vsd__circulation_pump_2") < 3955)
                {
                    dValue = 149;
                }
                else if (GetOperandBasicDoubleVal("sp_vsd__circulation_pump_2") > 3955)
                {
                    dValue = 151;
                }
                else
                {
                    dValue = 150;
                }
            }
            sRes = Format(dValue);

			return sRes;
		}

		@Override
		public String Update(double dValue, ZonedDateTime dt, double dPrevValue) {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	public static class Chiller_1_DN_SP implements ICalcOperand {
		private static int m_operand_id = 3353;
		private static int m_calc_operand_id = 6;


		public int GetOperandID() {
			return m_operand_id;
		}

		public int GetCalcOperandID() {
			return m_calc_operand_id;
		}
		
		public String GetOperandBasicStrVal(String name) {
			return CalculatedOperands1.GetOperandBasicVal(name);
		}
		
		public double GetOperandBasicDoubleVal(String name) {
			return  Double.parseDouble(GetOperandBasicVal(name));
		}

		public String Format(double val)
		{
			return CalculatedOperands1.formatter.format(val);
		}
		
		@Override
		public String Calculate(long ulReadCount, ZonedDateTime dtReadTime) {
			String sRes = "";
			double dValue = 0.0;
			/* This is the C# code, was not tested in java
			dValue = 80;
            DateTime _now = DateTime.Now;
            ZonedDateTime _now = ZonedDateTime.now(ZoneOffset.UTC);
            
            DateTime _today =  new DateTime(_now.Year, _now.Month, _now.Day);
            int iHour = (_now.Hour + 1) % 24;
            int sp_delta = 0;
            DateTime anchor = new DateTime(2016, 01, 01);

            if (iHour >=0 && iHour <= 7)
            {
                sp_delta = (int)_today.Subtract(anchor).TotalDays + 1;
                // check if we are even or odd
                if (iHour > 0)
                {
                    sp_delta++;
                }
                sp_delta = sp_delta % 2;
            }
            dValue += (30 * sp_delta);*/
			/*Avi asked that SP will be 7 on 2017-04-05. So, the next line sets it hard coded */
			dValue = 70;

            sRes = Format(dValue);

			return sRes;
		}

		@Override
		public String Update(double dValue, ZonedDateTime dt, double dPrevValue) {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	public static class Hour_Of_Day implements ICalcOperand {
		private static int m_operand_id = 3354;
		private static int m_calc_operand_id = 7;


		public int GetOperandID() {
			return m_operand_id;
		}

		public int GetCalcOperandID() {
			return m_calc_operand_id;
		}
		
		public String GetOperandBasicStrVal(String name) {
			return CalculatedOperands1.GetOperandBasicVal(name);
		}
		
		public double GetOperandBasicDoubleVal(String name) {
			return  Double.parseDouble(GetOperandBasicVal(name));
		}

		public String Format(double val)
		{
			return CalculatedOperands1.formatter.format(val);
		}
		
		@Override
		public String Calculate(long ulReadCount, ZonedDateTime dtReadTime) {
			String sRes = "";
			double dValue = 0.0;
			
			ZonedDateTime _now = ZonedDateTime.now(ZoneId.systemDefault());
			dValue = (_now.getHour() + 1) % 24;

            sRes = Format(dValue);

			return sRes;
		}

		@Override
		public String Update(double dValue, ZonedDateTime dt, double dPrevValue) {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	public static class Power_5_Norm_Weight implements ICalcOperand {
		
		private static int m_operand_id = 3355;
		private static int m_calc_operand_id = 8;
		
		private long m_ulReadCounter = 0;
		private double m_dPrevValue = 0.0;
		
		public int GetOperandID() {
			return m_operand_id;
		}

		public int GetCalcOperandID() {
			return m_calc_operand_id;
		}
		
		public String GetOperandBasicStrVal(String name) {
			return CalculatedOperands1.GetOperandBasicVal(name);
		}
		
		public double GetOperandBasicDoubleVal(String name) {
			return  Double.parseDouble(GetOperandBasicVal(name));
		}

		public String Format(double val)
		{
			return CalculatedOperands1.formatter.format(val);
		}
		
		@Override
		public String Calculate(long ulReadCount, ZonedDateTime dtReadTime) {
			String sRes = "";
			double dValue = 0.0;
			
			if ((ulReadCount - m_ulReadCounter) > 1)
			{
				dValue = GetOperandBasicDoubleVal("Energy_Meter_5_Power");
			}
			else
			{
				dValue = m_dPrevValue*0.9 + GetOperandBasicDoubleVal("Energy_Meter_5_Power")*0.1;
			}
			m_ulReadCounter = ulReadCount;
			m_dPrevValue = dValue;
			sRes = Format(dValue);

			return sRes;
		}

		@Override
		public String Update(double dValue, ZonedDateTime dt, double dPrevValue) {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
}
