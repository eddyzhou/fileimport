package org.zq.fileimport.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 表规则
 * 
 * @author EddyZhou(zhouqian1103@gmail.com)
 * 
 */
public class TableRule {
	/** 表名 */
	private String tableName;
	
	/** if txt or csv file has title */
	private boolean hasTitle;

	/** 字段规则集合 */
	private List<ColumnRule> columnConfigList = new ArrayList<ColumnRule>();
	//private Map<String, ColumnRule> columnConfigMap = new HashMap<String, ColumnRule>();

	public TableRule() {
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public boolean hasTitle() {
		return hasTitle;
	}

	public void setHasTitle(boolean hasTitle) {
		this.hasTitle = hasTitle;
	}

//	public void addColumnRule(String columnName, ColumnRule columnConfig) {
//		if (columnName != null)
//			columnConfigMap.put(columnName, columnConfig);
//	}
	
	public void addColumnRule(ColumnRule columnConfig) {
		if (columnConfig != null)
			columnConfigList.add(columnConfig);
	}

//	public Map<String, ColumnRule> getColumnRules() {
//		return columnConfigMap;
//	}
	
	public List<ColumnRule> getColumnRules() {
		return columnConfigList;
	}

//	public ColumnRule getColumnRule(String columnName) {
//		return columnName == null ? null : columnConfigMap.get(columnName);
//	}
	public ColumnRule getColumnRule(int i) {
		return columnConfigList.size() == 0 ? null : columnConfigList.get(i);
	}
}
