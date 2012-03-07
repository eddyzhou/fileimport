package org.zq.fileimport;

/**
 * 配置文件解析接口
 * 
 * @author EddyZhou(zhouqian1103@gmail.com)
 * 
 */
public interface ImportConfigureProvider {
	/**
	 * 装载配置文件
	 * 
	 * @param configFileName
	 * @return
	 */
	public ImportConfigure loadConfigure(String configFileName);
}
