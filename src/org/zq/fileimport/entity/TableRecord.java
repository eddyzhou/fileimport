package org.zq.fileimport.entity;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Record of table
 * 
 * @author EddyZhou(zhouqian1103@gmail.com)
 * 
 */
public class TableRecord {
	/** ���� */
	private String tableName;

	/** �м�¼����(���������������߶���) */
	private Queue<RowRecord> rowQueue = new ConcurrentLinkedQueue<RowRecord>();

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void addRow(RowRecord rowRecord) {
		if (rowRecord != null)
			rowQueue.offer(rowRecord);
	}

	public void pollRow() {
		rowQueue.poll();
	}
}
