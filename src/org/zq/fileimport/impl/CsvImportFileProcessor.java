package org.zq.fileimport.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.zq.fileimport.ImportFileProcessor;
import org.zq.fileimport.ImportCallback;
import org.zq.fileimport.ImportListener;
import org.zq.fileimport.entity.ImportResult;


/**
 * CSV导入处理类
 * 
 * @author EddyZhou(zhouqian1103@gmail.com)
 * 
 */
public class CsvImportFileProcessor implements ImportFileProcessor {
	private ImportResult result = null;
	
//	private static final CsvImportFileProcessor processor = new CsvImportFileProcessor();
//	
//	public static CsvImportFileProcessor getInstance() {
//		return processor;
//	}
	
	public CsvImportFileProcessor() {
	}
	
	private final List<ImportListener> listeners = new ArrayList<ImportListener>();
	
	public void addListener(ImportListener listerner) {
		if (listerner != null)
			listeners.add(listerner);
	}
	
	public void finishImport() {
		for (ImportListener listener : listeners) {
			listener.finishImport(result);
		}
	}

	/**
	 * 获取导入结果
	 */
//	public ImportResult getImportResult() {
//		return result;
//	}

	/**
	 * 导入文件
	 */
	public ImportResult importFile(String fileName, ImportCallback importCallback) throws IOException {
		//ImportResult result = null;
		
		try {
			importCallback.beforeImport();
			result = importCallback.importFile(fileName);
			importCallback.afterImport();
		} finally {
			importCallback.close();
		}
		
		finishImport();
		
		return result;
	}
}
