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
 * �ļ���������ݿ������ <br>
 * Note: <br>
 * 1. ���в��������ʱ��û�йر����ӣ���Ϊ��¼��Ҫһ�������룬�����һ����¼�����쳣����д������ļ���
 * ����ع����ڵ��ø���ʱ�����ڴ������������ݿ�����󣬵���close�����ر�����.<br>
 * 2. ���಻�ܱ�֤�̰߳�ȫ����ע��Connection���������һ���߳�һ������
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
	 * ��ȡ���ݿ�����
	 * 
	 * @return
	 */
	public Connection getConn() {
		return conn;
	}

	/**
	 * �������ݿ�����
	 * 
	 * @param conn
	 */
	public void setConn(Connection conn) {
		initConnection(conn);
	}

	/**
	 * ��ʼ������
	 * 
	 * @param conn
	 */
	private void initConnection(Connection conn) {
		if (conn == null)
			throw new IllegalArgumentException("Connection can not be null.");
		this.conn = conn;
		try {
			conn.setAutoCommit(true); // �����Զ��ύ
			this.stmt = conn.createStatement();
		} catch (SQLException e) {
			final String message = "Init Connection and Statement failure.";
			logger.error(message, e);
		}
	}

	/**
	 * �ر�����
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
	 * �����м�¼
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
	 * ƴװ�����¼��SQL
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
	 * ���뵼����ʷ
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
	 * ��ȡ������ʷ��SQL���
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
	 * ���ô洢����
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
	 * ��ȡִ�д洢���̵Ľӿ�
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
		if (params == null || params.length == 0) { // û�в���
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
	 * ��������ֵ
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
	 * ɾ����ʱ��
	 * 
	 * @param tempTableName
	 */
	public void dropTable(String tableName) throws SQLException {
		String sql = "drop table " + tableName;
		stmt.execute(sql);

	}
}
