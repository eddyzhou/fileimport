package org.zq.fileimport;


import java.io.IOException;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zq.db.DataSourceFactory;
import org.zq.fileimport.impl.BaseImportCallback;
import org.zq.fileimport.impl.CsvImportFileProcessor;
import org.zq.fileimport.impl.DefaultImportDbProcessor;
import org.zq.fileimport.impl.DefaultImportLineProcessor;
import org.zq.fileimport.impl.XMLImportConfigureProvider;

/**
 * 该类提供文件同步导入或者异步导入方法，将csv文件或者txt文件的数据导入数据库。 <br>
 * 文件与表的对应关系由配置文件配置,可参考fileimport包resource目录下的xml配置文件 <br>
 * 
 * 文件导入的处理流程为：先创建一张临时表，将数据导入到临时表，然后将临时表merge到要导入的表，删除临时表，这样可以保证数据的完整性，
 * 所以你需要提供创建临时表和将数据从临时表merge到你需要导入的表的存储过程
 * 
 * @author EddyZhou(zhouqian1103@gmail.com)
 * 
 */
public class FileImport {
	/** 日志实例 */
	private static final Log logger = LogFactory.getLog(FileImport.class);

	/** 配置文件解析类 */
	private ImportConfigureProvider provider = new XMLImportConfigureProvider();

	/**
	 * 同步导入
	 * 
	 * @param configFile
	 *            配置文件名
	 * @param createTempTableProcedure
	 *            创建临时表的存储过程名
	 * @param mergeDataProcedure
	 *            merger临时表和要导入的表的数据的存储过程名
	 * @param importFile
	 *            要导入的文件名
	 * @throws SQLException
	 *             数据库异常
	 * @throws IOException
	 *             IO异常
	 */
	public void synchImport(String configFile,
			final String createTempTableProcedure,
			final String mergeDataProcedure, String importFile)
			throws SQLException, IOException {

		ImportConfigure config = provider.loadConfigure(configFile);

		final ImportFileProcessor fileProcessor = new CsvImportFileProcessor();
		ImportDbProcessor dbProcessor = new DefaultImportDbProcessor(
				DataSourceFactory.getDataSource().getConnection());
		ImportLineProcessor lineProcessor = new DefaultImportLineProcessor(
				config, dbProcessor);
		final ImportCallback importCallback = new BaseImportCallback(config,
				lineProcessor, dbProcessor) {

			@Override
			protected void createTempTable(String tempTableName)
					throws SQLException {
				dbProcessor.callProcedure(createTempTableProcedure,
						tempTableName);
			}

			@Override
			protected void mergeData(String tempTableName) throws SQLException {
				dbProcessor.callProcedure(mergeDataProcedure, tempTableName);

			}

		};

		// 同步导入
		fileProcessor.importFile(importFile, importCallback);
	}

	/**
	 * 同步导入
	 * 
	 * @param configFile
	 * @param importCallback
	 *            导入回调接口
	 * @param importFile
	 * @throws IOException
	 */
	public void synchImport(String configFile, ImportCallback importCallback,
			String importFile) throws IOException {
		ImportFileProcessor fileProcessor = new CsvImportFileProcessor();

		// 同步导入
		fileProcessor.importFile(importFile, importCallback);
	}

	/**
	 * 同步导入
	 * 
	 * @param importCallback
	 *            导入回调接口
	 * @param fileProcessor
	 * @param importFile
	 * @throws IOException
	 */
	public void synchImport(ImportFileProcessor fileProcessor,
			ImportCallback importCallback, String importFile)
			throws IOException {

		// 同步导入
		fileProcessor.importFile(importFile, importCallback);
	}

	/**
	 * 异步导入
	 * 
	 * @param configFile
	 *            配置文件
	 * @param createTempTableProcedure
	 *            创建临时表的存储过程名
	 * @param mergeDataProcedure
	 *            merger临时表和要导入的表的数据的存储过程名
	 * @param importFile
	 *            要导入的文件名
	 * @param listeners
	 *            导入监听
	 * @throws IOException
	 *             IO异常
	 * @throws SQLException
	 *             数据库异常
	 */
	public void asynchImport(String configFile,
			final String createTempTableProcedure,
			final String mergeDataProcedure, final String importFile,
			ImportListener... listeners) throws IOException, SQLException {

		ImportConfigure config = provider.loadConfigure(configFile);

		final ImportFileProcessor fileProcessor = new CsvImportFileProcessor();
		ImportDbProcessor dbProcessor = new DefaultImportDbProcessor(
				DataSourceFactory.getDataSource().getConnection());
		ImportLineProcessor lineProcessor = new DefaultImportLineProcessor(
				config, dbProcessor);
		final ImportCallback importCallback = new BaseImportCallback(config,
				lineProcessor, dbProcessor) {

			@Override
			protected void createTempTable(String tempTableName)
					throws SQLException {
				dbProcessor.callProcedure(createTempTableProcedure,
						tempTableName);
			}

			@Override
			protected void mergeData(String tempTableName) throws SQLException {
				dbProcessor.callProcedure(mergeDataProcedure, tempTableName);

			}

		};

		asynchImport(fileProcessor, importCallback, importFile, listeners);
	}

	/**
	 * 异步导入
	 * 
	 * @param configFile
	 * @param importCallback
	 *            导入回调接口
	 * @param importFile
	 *            导入文件
	 * @param listeners
	 *            导入监听
	 * @throws IOException
	 *             可能抛出IO异常
	 */
	public void asynchImport(String configFile,
			final ImportCallback importCallback, final String importFile,
			ImportListener... listeners) throws IOException {

		final ImportFileProcessor fileProcessor = new CsvImportFileProcessor();

		asynchImport(fileProcessor, importCallback, importFile, listeners);
	}

	/**
	 * 异步导入
	 * 
	 * @param importCallback
	 *            导入回调接口
	 * @param fileProcessor
	 *            导入处理接口
	 * @param importFile
	 *            导入文件
	 * @throws IOException
	 *             可能抛出IO异常
	 */
	public void asynchImport(final ImportFileProcessor fileProcessor,
			final ImportCallback importCallback, final String importFile,
			ImportListener... listeners) throws IOException {

		for (ImportListener listener : listeners) {
			fileProcessor.addListener(listener);
		}

		new Thread() {
			@Override
			public void run() {
				try {
					fileProcessor.importFile(importFile, importCallback);
				} catch (IOException e) {
					logger.error("Caught an IOException while import file", e);
				}
			}
		}.start();
	}

	public ImportConfigureProvider getProvider() {
		return provider;
	}

	public void setProvider(ImportConfigureProvider provider) {
		this.provider = provider;
	}
}
