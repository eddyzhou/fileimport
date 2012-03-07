package org.zq.fileimport;

import java.io.IOException;

import org.zq.fileimport.entity.ImportResult;

/**
 * �ļ����봦��ӿ�
 * 
 * @author EddyZhou(zhouqian1103@gmail.com)
 * 
 */
public interface ImportFileProcessor {

	/**
	 * �����ļ�
	 * 
	 * @param fileName
	 * @param importLineProcessor
	 * @throws IOException
	 */
	public ImportResult importFile(String fileName,
			ImportCallback importCallback) throws IOException;

	/**
	 * ע�����
	 * 
	 * @param listerner
	 */
	public void addListener(ImportListener listerner);

	/**
	 * ����ʧ�ܵļ�¼
	 * 
	 * @param rowRecord
	 */
	// public void processFailureRecord(String[] rowRecord);
	/**
	 * ����ʧ�ܼ�¼
	 * 
	 * @param response
	 * @throws IOException
	 */
	// public void downloadFailFile(HttpServletResponse response)
	// throws IOException;
	/**
	 * ��ȡ������
	 * 
	 * @return
	 */
	// public ImportResult getImportResult();
}
