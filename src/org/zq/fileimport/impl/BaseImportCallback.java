package org.zq.fileimport.impl;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zq.db.DbException;
import org.zq.fileimport.ImportConfigure;
import org.zq.fileimport.ImportDbProcessor;
import org.zq.fileimport.ImportLineProcessor;
import org.zq.fileimport.ImportCallback;
import org.zq.fileimport.entity.ImportResult;
import org.zq.fileimport.entity.TableRule;
import org.zq.util.StringUtil;

import au.com.bytecode.opencsv.CSVReader;

/**
 * 导入回调的基础实现 <br>
 * 实现方式为：先创建临时表，将数据导入临时表，然后将临时表的数据merge到要导入的表
 * 之所以创建临时表，是为了保证数据完整性，大数据量导入时，为了考虑性能，每1000条数据(暂定)commit一次，
 * 若再第N次commit前发生数据库异常，这样就会破坏数据完整性，用临时表就可以解决这个问题，将所有导入的数据 merge到要导入的表后，drop临时表
 * 
 * @author EddyZhou(zhouqian1103@gmail.com)
 * 
 */
public abstract class BaseImportCallback implements ImportCallback {
	/** 日志实例 */
	protected static final Log logger = LogFactory
			.getLog(CsvImportFileProcessor.class);

	/** 导入结果 */
	protected ImportResult result = new ImportResult();

	/** 配置接口 */
	protected ImportConfigure importConfigure;

	protected ImportLineProcessor importLineProcessor;

	protected ImportDbProcessor dbProcessor = null;

	public void setDbProcessor(ImportDbProcessor dbProcessor) {
		this.dbProcessor = dbProcessor;
	}

	public void setImportLineProcessor(ImportLineProcessor importLineProcessor) {
		this.importLineProcessor = importLineProcessor;
	}

	public void setImportConfigure(ImportConfigure importConfigure) {
		this.importConfigure = importConfigure;
	}

	/**
	 * 构造函数
	 * 
	 * @param importConfigure
	 * @param importLineProcessor
	 * @param dbProcessor
	 */
	public BaseImportCallback(ImportConfigure importConfigure,
			ImportLineProcessor importLineProcessor,
			ImportDbProcessor dbProcessor) {
		this.importConfigure = importConfigure;
		this.importLineProcessor = importLineProcessor;
		this.dbProcessor = dbProcessor;
	}

	/** 临时表表名 */
	protected static ThreadLocal<String> tempTableNameLocal = new ThreadLocal<String>();

	/**
	 * 下载失败记录
	 * 
	 * @param response
	 * @throws IOException
	 */
	// public void downloadFailFile(HttpServletResponse response)
	// throws IOException {
	// FileUtil.downLoad(result.getFailFileName(), response);
	// }
	/**
	 * 导入文件
	 * 
	 * @param fileName
	 * @param importLineProcessor
	 * @throws IOException
	 */
	public ImportResult importFile(String fileName) throws IOException {
		if (StringUtil.isEmpty(fileName)) {
			throw new IllegalArgumentException("File name can not be null");
		}

		if (logger.isDebugEnabled()) {
			logger
					.debug("Import file [" + fileName + "] to table ["
							+ importConfigure.getTableRule().getTableName()
							+ "] start");
		}

		long beginTime = System.currentTimeMillis();
		CSVReader reader = null;
		FileReader fileReader = null;
		TableRule tableRule = importConfigure.getTableRule();
		//String tableName = tableRule.getTableName();
		result.setFileName(fileName);

		try {
			int rowNo = 0;
			fileReader = new FileReader(fileName);
			reader = new CSVReader(fileReader);

			String[] lineRecord;
			while ((lineRecord = reader.readNext()) != null) {
				if (rowNo == 0 && tableRule.hasTitle()) {
					rowNo++;
					continue;
				} else {
					importLineProcessor.process(tempTableNameLocal.get(), lineRecord, result);
					result.addTatal();
				}
				rowNo++;
			}
			
			long endTime = System.currentTimeMillis();
			result.setProcessTime((endTime - beginTime) / 1000);

			if (logger.isDebugEnabled()) {
				logger
						.debug("Import file ["
								+ fileName
								+ "] to table ["
								+ importConfigure.getTableRule()
										.getTableName() + "] end\nSpended "
								+ result.getProcessTime() + "s");
			}

		} catch (FileNotFoundException e) {
			final String message = "File[" + fileName + "] not found.";
			logger.error(message, e);
			IOException newEx = new IOException(message);
			newEx.initCause(e);
			throw newEx;
		} catch (IOException e) {
			logger.error("Caught an exception while import file", e);
			throw e;

		} finally {
			if (fileReader != null) {
				try {
					fileReader.close();
				} catch (IOException e) {
					logger.error("Close FileReader failure", e);
				}
			}

			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					logger.error("Close CsvReader failure", e);
				}
			}
		}

		return result;
	}

	/**
	 * 记录导入日志
	 */
	protected void logHistory() {
		try {
			dbProcessor.insertImportHistory(result);
		} catch (SQLException e) {
			logger.error("Log import history failure", e);
		}

	}

	/**
	 * 导入后处理
	 */
	public void afterImport() {
		try {
			mergeData(tempTableNameLocal.get());
			logHistory();
		} catch (SQLException e) {
			final String message = "Merge data failure";
			logger.error(message, e);
			throw new DbException(message, e);
		} 
	}

	/**
	 * 导入前处理
	 */
	public void beforeImport() {
		String tempTableName = importConfigure.getTableRule().getTableName()
				+ generateSuffix();
		tempTableNameLocal.set(tempTableName);
		try {
			createTempTable(tempTableName);
		} catch (SQLException e) {
			final String message = "Create temp table failure";
			logger.error(message, e);
			throw new DbException(message, e);
		}
	}
	
	/**
	 * 关闭资源
	 */
	public void close() {
		dropTempTable(tempTableNameLocal.get());
		dbProcessor.close();
	}

	/**
	 * 创建临时表
	 * 
	 * @param tempTableName
	 */
	protected abstract void createTempTable(String tempTableName) throws SQLException ;

	/**
	 * 将数据从临时表merge到要导入的表
	 * 
	 * @param tempTableName
	 */
	protected abstract void mergeData(String tempTableName) throws SQLException ;

	/**
	 * 删除临时表
	 * 
	 * @param tempTableName
	 */
	protected void dropTempTable(String tempTableName) {
		try {
			dbProcessor.dropTable(tempTableName);
		} catch (SQLException e) {
			final String message = "Drop table [" + tempTableName
					+ "] failure.";
			logger.error(message, e);
			throw new DbException(message, e);
		}
	}

	private String generateSuffix() {
		java.util.Date date = new java.util.Date(System.currentTimeMillis());
		java.text.SimpleDateFormat fdt = new java.text.SimpleDateFormat(
				"yyyyMMddHHmmssSSS");
		return "_" + fdt.format(date);
	}

}
