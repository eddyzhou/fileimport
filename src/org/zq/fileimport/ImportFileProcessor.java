package org.zq.fileimport;

import java.io.IOException;

import org.zq.fileimport.entity.ImportResult;

/**
 * 文件导入处理接口
 * 
 * @author EddyZhou(zhouqian1103@gmail.com)
 * 
 */
public interface ImportFileProcessor {

	/**
	 * 导入文件
	 * 
	 * @param fileName
	 * @param importLineProcessor
	 * @throws IOException
	 */
	public ImportResult importFile(String fileName,
			ImportCallback importCallback) throws IOException;

	/**
	 * 注册监听
	 * 
	 * @param listerner
	 */
	public void addListener(ImportListener listerner);

	/**
	 * 处理失败的记录
	 * 
	 * @param rowRecord
	 */
	// public void processFailureRecord(String[] rowRecord);
	/**
	 * 下载失败记录
	 * 
	 * @param response
	 * @throws IOException
	 */
	// public void downloadFailFile(HttpServletResponse response)
	// throws IOException;
	/**
	 * 获取导入结果
	 * 
	 * @return
	 */
	// public ImportResult getImportResult();
}
