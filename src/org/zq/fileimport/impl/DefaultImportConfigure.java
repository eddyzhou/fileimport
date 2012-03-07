package org.zq.fileimport.impl;

import java.util.HashMap;
import java.util.Map;

import org.zq.fileimport.ImportConfigure;
import org.zq.fileimport.entity.DefaultConfigure;
import org.zq.fileimport.entity.TableRule;


/**
 * 导入配置接口的默认实现
 * 
 * @author EddyZhou(zhouqian1103@gmail.com)
 * 
 */
public class DefaultImportConfigure implements ImportConfigure {
	private DefaultConfigure defaultConfig;
	private TableRule tableRule = null;
	
	/**
     * Collection of Chat DefaultImportConfigure.
     */
    private static final Map<String, DefaultImportConfigure> configures = new HashMap<String, DefaultImportConfigure>();
    
    /**
     * Returns the DefaultImportConfigure object load by the specified fileName.
     * @param configure file name.
     * @return DefaultImportConfigure object with certain fileName.
     */
    public static DefaultImportConfigure getInstance(final String fileName) {
        synchronized(configures) {
            if (!configures.containsKey(fileName)) {
            	configures.put(fileName, new DefaultImportConfigure());
            }
            return configures.get(fileName);
        }
    }

	private DefaultImportConfigure() {
	}

	public TableRule getTableRule() {
		return tableRule;
	}

	public void setTableRule(TableRule tableRule) {
		this.tableRule = tableRule;

	}

	public DefaultConfigure getDefaultConfigure() {
		return defaultConfig;
	}

	public void setDefaultConfigure(DefaultConfigure config) {
		if (config == null)
			throw new NullPointerException("DefaultConfigure can not be null");
		defaultConfig = config;

	}

	/**
	 * 获取存放导入失败记录的文件的路径
	 * 
	 * @return
	 */
	public String getFailFileDir() {
		return defaultConfig == null ? null : defaultConfig
				.getFailFileAttribute("path");
	}

	/**
	 * 获取取存放导入失败记录的文件的前缀
	 * 
	 * @return
	 */
	public String getFailFilePrefix() {
		return defaultConfig == null ? null : defaultConfig
				.getFailFileAttribute("prefix");
	}

	/**
	 * 获取校验规则
	 * 
	 * @param key
	 * @return
	 */
	public String getValidateRule(String key) {
		return defaultConfig == null ? null : defaultConfig
				.getValidateRule(key);
	}
}
