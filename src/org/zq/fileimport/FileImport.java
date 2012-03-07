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
 * �����ṩ�ļ�ͬ����������첽���뷽������csv�ļ�����txt�ļ������ݵ������ݿ⡣ <br>
 * �ļ����Ķ�Ӧ��ϵ�������ļ�����,�ɲο�fileimport��resourceĿ¼�µ�xml�����ļ� <br>
 * 
 * �ļ�����Ĵ�������Ϊ���ȴ���һ����ʱ�������ݵ��뵽��ʱ��Ȼ����ʱ��merge��Ҫ����ı�ɾ����ʱ���������Ա�֤���ݵ������ԣ�
 * ��������Ҫ�ṩ������ʱ��ͽ����ݴ���ʱ��merge������Ҫ����ı�Ĵ洢����
 * 
 * @author EddyZhou(zhouqian1103@gmail.com)
 * 
 */
public class FileImport {
	/** ��־ʵ�� */
	private static final Log logger = LogFactory.getLog(FileImport.class);

	/** �����ļ������� */
	private ImportConfigureProvider provider = new XMLImportConfigureProvider();

	/**
	 * ͬ������
	 * 
	 * @param configFile
	 *            �����ļ���
	 * @param createTempTableProcedure
	 *            ������ʱ��Ĵ洢������
	 * @param mergeDataProcedure
	 *            merger��ʱ���Ҫ����ı�����ݵĴ洢������
	 * @param importFile
	 *            Ҫ������ļ���
	 * @throws SQLException
	 *             ���ݿ��쳣
	 * @throws IOException
	 *             IO�쳣
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

		// ͬ������
		fileProcessor.importFile(importFile, importCallback);
	}

	/**
	 * ͬ������
	 * 
	 * @param configFile
	 * @param importCallback
	 *            ����ص��ӿ�
	 * @param importFile
	 * @throws IOException
	 */
	public void synchImport(String configFile, ImportCallback importCallback,
			String importFile) throws IOException {
		ImportFileProcessor fileProcessor = new CsvImportFileProcessor();

		// ͬ������
		fileProcessor.importFile(importFile, importCallback);
	}

	/**
	 * ͬ������
	 * 
	 * @param importCallback
	 *            ����ص��ӿ�
	 * @param fileProcessor
	 * @param importFile
	 * @throws IOException
	 */
	public void synchImport(ImportFileProcessor fileProcessor,
			ImportCallback importCallback, String importFile)
			throws IOException {

		// ͬ������
		fileProcessor.importFile(importFile, importCallback);
	}

	/**
	 * �첽����
	 * 
	 * @param configFile
	 *            �����ļ�
	 * @param createTempTableProcedure
	 *            ������ʱ��Ĵ洢������
	 * @param mergeDataProcedure
	 *            merger��ʱ���Ҫ����ı�����ݵĴ洢������
	 * @param importFile
	 *            Ҫ������ļ���
	 * @param listeners
	 *            �������
	 * @throws IOException
	 *             IO�쳣
	 * @throws SQLException
	 *             ���ݿ��쳣
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
	 * �첽����
	 * 
	 * @param configFile
	 * @param importCallback
	 *            ����ص��ӿ�
	 * @param importFile
	 *            �����ļ�
	 * @param listeners
	 *            �������
	 * @throws IOException
	 *             �����׳�IO�쳣
	 */
	public void asynchImport(String configFile,
			final ImportCallback importCallback, final String importFile,
			ImportListener... listeners) throws IOException {

		final ImportFileProcessor fileProcessor = new CsvImportFileProcessor();

		asynchImport(fileProcessor, importCallback, importFile, listeners);
	}

	/**
	 * �첽����
	 * 
	 * @param importCallback
	 *            ����ص��ӿ�
	 * @param fileProcessor
	 *            ���봦��ӿ�
	 * @param importFile
	 *            �����ļ�
	 * @throws IOException
	 *             �����׳�IO�쳣
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
