package org.zq.fileimport;

import org.zq.fileimport.entity.DefaultConfigure;
import org.zq.fileimport.entity.TableRule;

/**
 * 导入配置接口
 * 
 * @author EddyZhou(zhouqian1103@gmail.com)
 * 
 */
public interface ImportConfigure {

	/**
	 * 设置默认配置
	 * 
	 * @param type
	 * @param expression
	 */
	public void setDefaultConfigure(DefaultConfigure config);

	/**
	 * 获取默认配置
	 * 
	 * @return
	 */
	public DefaultConfigure getDefaultConfigure();

	/**
	 * 设置表规则
	 * 
	 * @param tableRule
	 */
	public void setTableRule(TableRule tableRule);

	/**
	 * 获取表规则
	 * 
	 * @return
	 */
	public TableRule getTableRule();

	/**
	 * 获取存放导入失败记录的文件的路径
	 * 
	 * @return
	 */
	public String getFailFileDir();

	/**
	 * 获取取存放导入失败记录的文件的前缀
	 * 
	 * @return
	 */
	public String getFailFilePrefix();

	/**
	 * 获取校验规则
	 * 
	 * @param key
	 * @return
	 */
	public String getValidateRule(String key);
}
