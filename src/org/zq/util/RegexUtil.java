package org.zq.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则校验工具类
 * 
 * @author EddyZhou(zhouqian1103@gmail.com)
 * 
 */
public class RegexUtil {

	private RegexUtil() {
	}

	public static String getGroupStr(String regex, String text, int group) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(text);
		if (matcher.matches()) {
			return matcher.group(group);
		}
		return null;
	}

	public static boolean isMatched(String regex, String text, int flags) {
		Pattern pattern = Pattern.compile(regex, flags);
		Matcher matcher = pattern.matcher(text);
		return matcher.matches();
	}

	public static void main(String[] args) {
		/*String regex = "(^select )(.+)( from .+)";
		String text = "Calendar cal = Calendar.instance;cal.time = ?{FROM_DATE};\tcal.add(Calendar.MONTH, 1);return new java.sql.Date(cal.time)";
		System.out.println(isMatched(regex, text, Pattern.CASE_INSENSITIVE));

		String regex1 = ".*\\?\\{(\\w+)\\}.*";
		int group = 1;
		System.out.println(getGroupStr(regex1, text, group));*/
		//System.out.println(" aa    bb1        ccc4 ".trim().replaceAll("\\s{2,}", " "));
		String text = "\"aaaaa\"";
		System.out.println(getGroupStr("\"(.+)\"", text, 1));
	}
}
