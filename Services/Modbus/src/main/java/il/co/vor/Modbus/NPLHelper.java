package il.co.vor.Modbus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import il.co.vor.common.Constants;
import il.co.vor.common.Enums.OperandDataType;

public class NPLHelper {

	static Logger _logger = Logger.getLogger(NPLHelper.class.getName());

	static int GetFlags(ArrayList<Integer> arr) {

		int flags = 0;
		String s = "";
		int i_shift_by = 0;
		int i_tmp;

		if (arr.size() > 0 && arr.size() <= 4) {
			for (int i = arr.size() - 1; i >= 0; i--) {
				i_tmp = arr.get(i) & 0xFF;
				flags += (i_tmp << i_shift_by);
				i_shift_by += 8;

			}
		}

		return flags;
	}

	public static boolean isFlaged(int flags, int flagToCheck) {
		return ((flags & flagToCheck) == flagToCheck);
	}

	public static int GetMinVal(int a, int b, int c, int d) {
		int min_ab, min_dc, min;
		min_ab = a < b ? a : b;
		min_dc = c < d ? c : d;
		min = min_ab < min_dc ? min_ab : min_dc;

		return min;
	}

	public static ZonedDateTime GetRoundedHourTime(ZonedDateTime source, boolean floor) {

		ZonedDateTime ret = null;

		try {

			ret = (floor ? source: source.plusHours(1));
			ret = ret.minusMinutes(ret.getMinute()).minusSeconds(ret.getSecond()).minusNanos(ret.getNano());

			ZoneId zoneIdDefault = ZoneId.systemDefault();
			DateTimeFormatter.ofPattern(Constants.CSV_DATE_FORMAT).format(ret.withZoneSameInstant(zoneIdDefault));
			_logger.log(Level.INFO, String.format(
					"refresh time: %s rounded time: %s refresh local time: %s rounded local time: %s",
					DateTimeFormatter.ofPattern(Constants.CSV_DATE_FORMAT).format(source.withZoneSameInstant(ZoneOffset.UTC)),
					DateTimeFormatter.ofPattern(Constants.CSV_DATE_FORMAT).format(ret.withZoneSameInstant(ZoneOffset.UTC)),
					DateTimeFormatter.ofPattern(Constants.CSV_DATE_FORMAT).format(source.withZoneSameInstant(zoneIdDefault)),
					DateTimeFormatter.ofPattern(Constants.CSV_DATE_FORMAT).format(ret.withZoneSameInstant(zoneIdDefault))));
		} catch (Exception e) {
			ret = null;
			_logger.log(Level.SEVERE, String.format("Exception: %s", e.getMessage()));
			e.printStackTrace();
		}
		return ret;
	}

	public static int GetBooleanAsInt(boolean b) {
		int ret = b ? 1 : 0;
		return ret;
	}

	public static String GetDoubleValAsStr(double val) {
		String ret = "";
		try {
			// Double truncatedDouble = BigDecimal.valueOf(val)
			// .setScale(4, RoundingMode.HALF_UP)
			// .doubleValue();
			ret = BigDecimal.valueOf(val).setScale(Constants.NPL_DECIMAL_PRECISION_SCALE, RoundingMode.HALF_UP)
					.toPlainString();

			// ret = String.valueOf(truncatedDouble);
		} catch (Exception e) {
			ret = "";
			_logger.log(Level.SEVERE, String.format("Exception: %s", e.getMessage()));
			e.printStackTrace();
		}
		return ret;

	}

	public static String GetCSVNormalizedString(String str) {
		String ret = "";

		ret = str.replace(Constants.CSV_COMMA_DELIMITER, Constants.CSV_COMMA_DELIMITER_REPLACEMENT);

		return ret;
	}

	

	public static int GetIntParam(String val, int _DefVal) {
		int i = _DefVal;

		if (!val.isEmpty()) {
			i = Integer.parseInt(val);
		}

		return i;
	}

	public static String GetStrParam(String val, String _DefVal) {
		String s = _DefVal;

		if (!val.isEmpty()) {
			s = val;
		}

		return s;
	}
}
