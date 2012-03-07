package org.zq.db;

/**
 * Database Configure parameters
 * 
 * @author EddyZhou(zhouqian1103@gmail.com)
 * 
 */
public class DBConfig {
	/** database config */
	public static final String DRIVER = "oracle.jdbc.driver.OracleDriver";
	public static final String URL = "jdbc:oracle:thin:@10.1.3.80:1521:ora9i";
	public static String USER = "mecs_test";
	public static String PASSWORD = "mecs";

	public static String getDriver() {
		return DRIVER;
	}

	public static String getUser() {
		return USER;
	}

	public static String getPassword() {
		return PASSWORD;
	}

	public static String getUrl() {
		return URL;
	}
}
