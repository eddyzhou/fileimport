package org.zq.fileimport;

/**
 * �����ļ������ӿ�
 * 
 * @author EddyZhou(zhouqian1103@gmail.com)
 * 
 */
public interface ImportConfigureProvider {
	/**
	 * װ�������ļ�
	 * 
	 * @param configFileName
	 * @return
	 */
	public ImportConfigure loadConfigure(String configFileName);
}
