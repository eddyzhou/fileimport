package org.zq.fileimport.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zq.fileimport.ImportDbProcessor;
import org.zq.fileimport.entity.ImportResult;
import org.zq.fileimport.entity.RowRecord;
import org.zq.util.StringUtil;

/**
 * 文件导入的数据库操作类 <br>
 * Note: <br>
 * 1. 进行插入操作的时候，没有关闭连接，因为记录需要一条条插入，如果有一条记录插入异常，则写入错误文件，
 * 不会回滚，在调用该类时，需在处理完所有数据库操作后，调用close方法关闭连接.<br>
 * 2. 本类不能保证线程安全，请注意Connection参数，最好一个线程一个连接
 * 
 * @author EddyZhou(zhouqian1103@gmail.com)
 * 
 */
public class DefaultImportDbProcessor implements ImportDbProcessor {
	private static final Log logger = LogFactory
			.getLog(DefaultImportDbProcessor.class);

	private Connection conn = null;
	private Statement stmt = null;

	public DefaultImportDbProcessor(Connection conn) {
		initConnection(conn);
	}

	/**
	 * 获取数据库连接
	 * 
	 * @return
	 */
	public Connection getConn() {
		return conn;
	}

	/**
	 * 设置数据库连接
	 * 
	 * @param conn
	 */
	public void setConn(Connection conn) {
		initConnection(conn);
	}

	/**
	 * 初始化连接
	 * 
	 * @param conn
	 */
	private void initConnection(Connection conn) {
		if (conn == null)
			throw new IllegalArgumentException("Connection can not be null.");
		this.conn = conn;
		try {
			conn.setAutoCommit(true); // 开启自动提交
			this.stmt = conn.createStatement();
		} catch (SQLException e) {
			final String message = "Init Connection and Statement failure.";
			logger.error(message, e);
		}
	}

	/**
	 * 关闭连接
	 */
	public void close() {
		if (stmt != null) {
			try {
				stmt.close();
				stmt = null;
			} catch (SQLException e) {
				logger.error("Close Statement failure.");
			}
		}
		if (conn != null) {
			try {
				if (!conn.getAutoCommit())
					conn.commit();
				conn.close();
				conn = null;
			} catch (SQLException e) {
				logger.error("Close database connection failure.");
			}
		}

	}

	/**
	 * 插入行记录
	 * 
	 * @param tableName
	 * @param rowRecord
	 * @return
	 */
	public boolean insertRecord(String tableName, RowRecord record) {
		String sql = prepareSQL(tableName, record);
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			logger.info("Insert record failure", e);
			return false;
		}

		return true;
	}

	/**
	 * 拼装插入记录的SQL
	 * 
	 * @param tableName
	 * @param record
	 * @return
	 */
	private String prepareSQL(String tableName, RowRecord record) {
		if (StringUtil.isEmpty(tableName) || record == null)
			throw new IllegalArgumentException(
					"Table name and record can not be null");

		StringBuilder sb = new StringBuilder("insert into ");
		sb.append(tableName);

		StringBuilder cBuffer = new StringBuilder();
		StringBuilder vBuffer = new StringBuilder();
		Iterator<String> iter = record.getColumns().keySet().iterator();
		String columnName = iter.next();
		cBuffer.append(columnName);
		vBuffer.append("'" + record.getColumnValue(columnName) + "'");
		while (iter.hasNext()) {
			columnName = iter.next();
			cBuffer.append(",");
			cBuffer.append(columnName);
			vBuffer.append(",");
			vBuffer.append("'" + record.getColumnValue(columnName) + "'");
		}

		sb.append(" (").append(cBuffer.toString()).append(") values (").append(
				vBuffer.toString()).append(")");

		return sb.toString();
	}

	/**
	 * 插入导入历史
	 * 
	 * @param result
	 * @throws SQLException
	 */
	public void insertImportHistory(ImportResult result) throws SQLException {
		// initConnection();
		String sql = prepareHistorySQL(result);
		stmt.executeUpdate(sql);
	}

	/**
	 * 获取导入历史的SQL语句
	 * 
	 * @param result
	 * @return
	 */
	private String prepareHistorySQL(ImportResult result) {
		StringBuilder sb = new StringBuilder();
		sb
				.append("insert into import_history(file_name, fail_count, total_count, fail_file_name, spend_time, log_time) values (");
		sb.append("'" + result.getFileName() + "'");
		sb.append(",");
		sb.append(result.getFailCount());
		sb.append(",");
		sb.append(result.getTatalCount());
		sb.append(",");
		sb.append(result.getFailFileName() == null ? null : "'"
				+ result.getFailFileName() + "'");
		sb.append(",");
		sb.append(result.getProcessTime());
		sb.append(", sysdate)");
		return sb.toString();
	}

	/**
	 * 调用存储过程
	 * 
	 * @param procedure
	 * @param objects
	 */
	public void callProcedure(String procedure, Object... params)
			throws SQLException {
		CallableStatement cstmt = prepareStatement(procedure, params);
		fillSQLParamter(cstmt, params);
		cstmt.execute();
	}

	/**
	 * 获取执行存储过程的接口
	 * 
	 * @param procedure
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	private CallableStatement prepareStatement(String procedure,
			Object... params) throws SQLException {
		StringBuilder sql = new StringBuilder("{call " + procedure + "(");
		CallableStatement cstmt = null;
		if (params == null || params.length == 0) { // 没有参数
			sql.append(")}");
			cstmt = conn.prepareCall(sql.toString());
		} else {
			for (int i = 0; i < params.length; i++) {
				if (i != 0) {
					sql.append(",");
				}
				sql.append("?");
			}
			sql.append(")}");
			cstmt = conn.prepareCall(sql.toString());
		}

		return cstmt;
	}

	/**
	 * 给参数赋值
	 * 
	 * @param stmt
	 * @param objs
	 * @throws SQLException
	 */
	private void fillSQLParamter(CallableStatement stmt, Object... objs)
			throws SQLException {
		if (objs == null) {
			throw new IllegalArgumentException("NO SQL Paramter");
		}

		for (int i = 0; i < objs.length; i++) {
			stmt.setObject(i + 1, objs[i]);
		}
	}

	/**
	 * 删除临时表
	 * 
	 * @param tempTableName
	 */
	public void dropTable(String tableName) throws SQLException {
		String sql = "drop table " + tableName;
		stmt.execute(sql);

	}
}
