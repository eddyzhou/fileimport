package org.zq.fileimport.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * One row record of txt or csv file
 * 
 * @author EddyZhou(zhouqian1103@gmail.com)
 * 
 */
public class RowRecord {
	/** column 节点实体映射 */
	Map<String, ColumnRecord> recordMap = new HashMap<String, ColumnRecord>();

	public void addColumn(String columnName, ColumnRecord record) {
		if (columnName != null)
			recordMap.put(columnName, record);
	}

	public void removeColumn(String columnName) {
		if (columnName != null)
			recordMap.remove(columnName);
	}

	public Map<String, ColumnRecord> getColumns() {
		return recordMap;
	}

	public ColumnRecord getColumn(String columnName) {
		return columnName == null ? null : recordMap.get(columnName);
	}
	
	public Object getColumnValue(String columnName) {
		return columnName == null ? null : recordMap.get(columnName).getValue();
	}
}
