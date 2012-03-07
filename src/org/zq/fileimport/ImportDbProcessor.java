package org.zq.fileimport;

import java.sql.SQLException;

import org.zq.fileimport.entity.ImportResult;
import org.zq.fileimport.entity.RowRecord;

/**
 * �������ݿ⴦��ӿ�
 * 
 * @author EddyZhou(zhouqian1103@gmail.com)
 * 
 */
public interface ImportDbProcessor {

	/**
	 * �����м�¼
	 * 
	 * @param tableName
	 * @param rowRecord
	 * @return
	 */
	public boolean insertRecord(String tableName, RowRecord rowRecord);

	/**
	 * ���뵼����ʷ
	 * 
	 * @param result
	 * @throws SQLException
	 */
	public void insertImportHistory(ImportResult result) throws SQLException;

	/**
	 * ɾ����ʱ��
	 * 
	 * @param tempTableName
	 */
	public void dropTable(String tempTableName) throws SQLException;

	/**
	 * ���ô洢����
	 * 
	 * @param procedure
	 * @param objects
	 */
	public void callProcedure(String procedure, Object...inParms) throws SQLException;
	
	/**
	 * �ر�����
	 */
	public void close();
}
