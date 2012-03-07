package org.zq.fileimport;

import java.io.IOException;

import org.zq.fileimport.entity.ImportResult;

/**
 * 导入回调
 * 
 * @author EddyZhou(zhouqian1103@gmail.com)
 * 
 */
public interface ImportCallback {

	/**
	 * 导入前的处理
	 */
	public void beforeImport();

	/**
	 * 导入文件
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public ImportResult importFile(String fileName) throws IOException;

	/**
	 * 导入后的处理
	 */
	public void afterImport();
	
	/**
	 * 关闭资源
	 */
	public void close();
}
