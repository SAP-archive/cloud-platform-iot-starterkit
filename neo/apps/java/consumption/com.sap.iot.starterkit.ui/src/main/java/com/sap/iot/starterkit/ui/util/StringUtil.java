package com.sap.iot.starterkit.ui.util;

/**
 * String utilities
 */
public class StringUtil {

	/**
	 * Disjoins the specified {@code postfix} from the given {@code origin} string object in case it
	 * ends with it. If the length of the {@code origin} or {@code postfix} is 0, then this
	 * {@code origin} string object is returned.
	 * 
	 * @param origin
	 *            the string to disjoin the {@code postfix} from
	 * @param postfix
	 *            an "ending" part to disjoin from {@code origin}
	 * @return a string that represents the disjoined object
	 */
	public static String disjoin(String origin, String postfix) {
		if (origin == null) {
			return origin;
		}
		if (postfix == null || postfix.isEmpty()) {
			return origin;
		}
		if (origin.endsWith(postfix)) {
			origin = origin.substring(0, origin.length() - postfix.length());
		}
		return origin;
	}

}
