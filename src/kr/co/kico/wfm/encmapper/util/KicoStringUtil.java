package kr.co.kico.wfm.encmapper.util;

public class KicoStringUtil {
	public static String nvl(Object obj, String defaultVal) {
		if (obj == null) {
			return defaultVal;
		}

		return obj.toString().trim();
	}
}
