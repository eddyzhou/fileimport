package org.zq.fileimport;

import java.io.IOException;

import org.zq.fileimport.entity.ImportResult;

/**
 * ����ص�
 * 
 * @author EddyZhou(zhouqian1103@gmail.com)
 * 
 */
public interface ImportCallback {

	/**
	 * ����ǰ�Ĵ���
	 */
	public void beforeImport();

	/**
	 * �����ļ�
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public ImportResult importFile(String fileName) throws IOException;

	/**
	 * �����Ĵ���
	 */
	public void afterImport();
	
	/**
	 * �ر���Դ
	 */
	public void close();
}
