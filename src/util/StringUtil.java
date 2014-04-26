package util;

public class StringUtil {
	public static String join(String[] arr, String sep) {
		String result = "";
		for (int i = 0; i < arr.length; ++i) {
			result += arr[i];
			
			if (i < arr.length - 1) {
				result += sep;
			}
		}
		return result;
	}
	
	public static String[] splice(String[] arr, int begin, int end) {
		if (begin >= end) {
			return null;
		}
		
		if (begin < 0 || begin >= arr.length) {
			return null;
		}
		
		if (end < 0 || end > arr.length) {
			return null;
		}
		
		String[] result = new String[end - begin];
		for (int i = begin; i < end; ++i) {
			result[i - begin] = arr[i];
		}
		return result;
	}
}
