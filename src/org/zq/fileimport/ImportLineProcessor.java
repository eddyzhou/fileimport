package org.zq.fileimport;

import org.zq.fileimport.entity.ColumnRule;
import org.zq.fileimport.entity.ImportResult;

/**
 * �м�¼�Ĵ���ӿ�
 * 
 * @author EddyZhou(zhouqian1103@gmail.com)
 * 
 */
public interface ImportLineProcessor {

	/**
	 * У���м�¼
	 * 
	 * @param columnRecord
	 * @param cRule
	 * @return
	 */
	public boolean isValid(String columnRecord, ColumnRule cRule);

	/**
	 * �����м�¼(eg:�������ݿ�)
	 * 
	 * @param tableName
	 * @param rowRecord
	 * @return
	 */
	public void process(String tableName, String[] lineRecord,
			ImportResult result);

	/**
	 * ����ʧ�ܵļ�¼
	 * 
	 * @param rowRecord
	 */
	public void processFailureRecord(String[] rowRecord, ImportResult result);

}
