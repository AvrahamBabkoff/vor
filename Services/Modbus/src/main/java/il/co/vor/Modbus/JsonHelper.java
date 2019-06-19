package il.co.vor.Modbus;
import org.json.JSONArray;
import org.json.JSONObject;

public class JsonHelper {

	static int GetFlags(JSONArray arr) {

		int flags = -1;
		int ascii_code = -1;
		char ascii_char = ' ';
		String s = "";

		if (arr.length() > 0) {
			StringBuilder sb = new StringBuilder(arr.length());
			for (int i = 0; i < arr.length(); i++) {
				ascii_code = arr.getInt(i);
				ascii_char = (char) ascii_code;
				sb.append(ascii_char);
				// barr[i] = (byte) arr.getInt(i);

			}
			if ((arr.length() % 2) == 1)
				sb.append('0');
			s = sb.toString();
			
			flags = Integer.parseInt(s, 16);
			//barr = hexStringToByteArray(sb.toString());
			/*if (arr.length() > 0) {
				int l = arr.length() / 2;
				if ((l % 2) == 1) {
					l++;
				}

				barr = new byte[l];
				for (int i = 0; i < arr.length(); i++) {
					ascii_code = arr.getInt(i);
					ascii_char = (char) ascii_code;
					barr[i] = (byte) arr.getInt(i);

				}

			}*/
		}
		return flags;
	}

	public static byte[] hexStringToByteArray(final String s) {
		if (s == null || (s.length() % 2) == 1)
			throw new IllegalArgumentException();
		final char[] chars = s.toCharArray();
		final int len = chars.length;
		final byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(chars[i], 16) << 4) + Character.digit(chars[i + 1], 16));
		}
		return data;
	}

	static String GetJsonNullOrString(JSONObject jsono, String param_name) {
		return (jsono.isNull(param_name) ? null : jsono.getString(param_name));
	}

	static double GetJsonNullOrDouble(JSONObject jsono, String param_name) {
		return (jsono.isNull(param_name) ? null : jsono.getDouble(param_name));
	}

	static int GetJsonNullOrInt(JSONObject jsono, String param_name) {
		return (jsono.isNull(param_name) ? null : jsono.getInt(param_name));
	}

}
