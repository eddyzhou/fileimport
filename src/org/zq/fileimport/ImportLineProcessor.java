package org.zq.fileimport;

import org.zq.fileimport.entity.ColumnRule;
import org.zq.fileimport.entity.ImportResult;

/**
 * 行记录的处理接口
 * 
 * @author EddyZhou(zhouqian1103@gmail.com)
 * 
 */
public interface ImportLineProcessor {

	/**
	 * 校验行记录
	 * 
	 * @param columnRecord
	 * @param cRule
	 * @return
	 */
	public boolean isValid(String columnRecord, ColumnRule cRule);

	/**
	 * 处理行记录(eg:存入数据库)
	 * 
	 * @param tableName
	 * @param rowRecord
	 * @return
	 */
	public void process(String tableName, String[] lineRecord,
			ImportResult result);

	/**
	 * 处理失败的记录
	 * 
	 * @param rowRecord
	 */
	public void processFailureRecord(String[] rowRecord, ImportResult result);

}
