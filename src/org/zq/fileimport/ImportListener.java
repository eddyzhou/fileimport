package org.zq.fileimport;

import org.zq.fileimport.entity.ImportResult;

/**
 * 文件导入的监听器
 * 
 * @author EddyZhou(zhouqian1103@gmail.com)
 * 
 */
public interface ImportListener {
	/**
	 * 导入完成
	 * 
	 * @param result
	 */
	void finishImport(ImportResult result);
}
