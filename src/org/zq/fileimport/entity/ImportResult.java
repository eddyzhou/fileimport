package org.zq.fileimport.entity;

import org.zq.util.StringUtil;

/**
 * ������
 * 
 * @author EddyZhou(zhouqian1103@gmail.com)
 * 
 */
public class ImportResult {
	/** �ļ��� */
	private String fileName;
	
	/** �����ܼ�¼�� */
	private int tatalCount = 0;
	
	/** ����ʧ�ܼ�¼�� */
	private int failCount = 0;
	
	/** ����ʧ�ܼ�¼���ļ��� */
	private String failFileName = null;
	
	/** ���봦��ʱ�� */
	private long processTime = 0L;

	public ImportResult() {
	}

	public void addTatal() {
		tatalCount++;
	}
	
	public void setTatalCount(int tatalCount) {
		this.tatalCount = tatalCount;
	}

	public void addFailure() {
		failCount++;
	}

	public String getFailFileName() {
		return failFileName;
	}

	public void setFailFileName(String failFileName) {
		if (StringUtil.isEmpty(failFileName)) 
			throw new IllegalArgumentException("Fail file name can not be null or empty.");
		
		this.failFileName = failFileName;
	}

	public int getTatalCount() {
		return tatalCount;
	}

	public int getFailCount() {
		return failCount;
	}

	public long getProcessTime() {
		return processTime;
	}

	public void setProcessTime(long processTime) {
		if (processTime < 0) 
			throw new IllegalArgumentException("Process time can not lt 0.");
		
		this.processTime = processTime;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		if (StringUtil.isEmpty(fileName)) 
			throw new IllegalArgumentException("File name can not be null or empty.");
		
		this.fileName = fileName;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Import record count : ");
		sb.append(tatalCount);
		sb.append("\n");
		sb.append("Success record count : ");
		sb.append(tatalCount - failCount);
		sb.append("\n");
		sb.append("Failure record count : ");
		sb.append(failCount);
		sb.append("\n");
		sb.append("Process time : ");
		sb.append(processTime + "s");
		return sb.toString();
	}
}
