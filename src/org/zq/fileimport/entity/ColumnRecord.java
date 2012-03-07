package org.zq.fileimport.entity;

/**
 * One column record of one row in csv or txt file
 * 
 * @author EddyZhou(zhouqian1103@gmail.com)
 * 
 */
public class ColumnRecord {
	/** �ֶι��� */
	private ColumnRule rule;
	
	/** �ֶ�ֵ */
	private Object value;

	public ColumnRecord() {
		super();
	}

	public ColumnRecord(ColumnRule rule, Object value) {
		super();
		this.rule = rule;
		this.value = value;
	}

	public ColumnRule getRule() {
		return rule;
	}

	public void setRule(ColumnRule rule) {
		this.rule = rule;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
