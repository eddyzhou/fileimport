package org.zq.fileimport;

import java.sql.SQLException;

import org.zq.fileimport.entity.ImportResult;
import org.zq.fileimport.entity.RowRecord;

/**
 * 导入数据库处理接口
 * 
 * @author EddyZhou(zhouqian1103@gmail.com)
 * 
 */
public interface ImportDbProcessor {

	/**
	 * 插入行记录
	 * 
	 * @param tableName
	 * @param rowRecord
	 * @return
	 */
	public boolean insertRecord(String tableName, RowRecord rowRecord);

	/**
	 * 插入导入历史
	 * 
	 * @param result
	 * @throws SQLException
	 */
	public void insertImportHistory(ImportResult result) throws SQLException;

	/**
	 * 删除临时表
	 * 
	 * @param tempTableName
	 */
	public void dropTable(String tempTableName) throws SQLException;

	/**
	 * 调用存储过程
	 * 
	 * @param procedure
	 * @param objects
	 */
	public void callProcedure(String procedure, Object...inParms) throws SQLException;
	
	/**
	 * 关闭连接
	 */
	public void close();
}
