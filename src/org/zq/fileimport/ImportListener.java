package org.zq.fileimport;

import org.zq.fileimport.entity.ImportResult;

/**
 * �ļ�����ļ�����
 * 
 * @author EddyZhou(zhouqian1103@gmail.com)
 * 
 */
public interface ImportListener {
	/**
	 * �������
	 * 
	 * @param result
	 */
	void finishImport(ImportResult result);
}
