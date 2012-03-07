package org.zq.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符处理工具类
 * 
 * @author EddyZhou(zhouqian1103@gmail.com)
 * 
 */
public class StringUtil {

	private StringUtil() {
	}

	public static boolean isEmpty(String text) {
		return text == null || "".equals(text.trim());
	}

	public static boolean isNotEmpty(String text) {
		if (text == null || "".equals(text.trim())) {
			return false;
		}
		return true;
	}

	public static String toGBK(String text) {
		if (isEmpty(text)) {
			return null;
		}

		String s = text;
		try {
			byte temp[] = s.getBytes("ISO-8859-1");
			s = new String(temp, "GB18030");
			return s;

		} catch (Exception e) {
			return text;
		}
	}

	/**
	 * 去除str的HTML格式
	 * 
	 * @param str
	 * @return
	 */
	public static String clearHtml(String str) {
		if (str == null) {
			return null;
		}

		// 将regex编译到模式,不区分大小写
		Pattern p = Pattern.compile("<[^>]+>|</[^>]+>",
				Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(str);
		return m.replaceAll("");
	}

	public static String collection2String(Collection<?> collection,
			String separator) {
		if (collection == null || collection.isEmpty()) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (Iterator<?> iter = collection.iterator(); iter.hasNext(); i++) {
			if (i != 0) {
				sb.append(separator);
			}
			sb.append(iter.next());
		}
		return sb.toString();
	}

	public static String array2String(Object[] objs, String separator) {
		if (objs == null || objs.length == 0) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < objs.length; i++) {
			if (i != 0) {
				sb.append(separator);
			}
			sb.append(objs[i]);
		}
		return sb.toString();
	}

	public static boolean isDigist(String str) {
		if (isEmpty(str))
			return false;

		Pattern pattern = Pattern.compile("^-?[1-9]\\d*$");
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}
}
