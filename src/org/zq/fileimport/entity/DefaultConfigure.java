package org.zq.fileimport.entity;

import java.util.Properties;
import java.util.ResourceBundle;

import org.zq.util.BundleParser;

/**
 * 默认导入配置
 * 
 * @author EddyZhou(zhouqian1103@gmail.com)
 * 
 */
public class DefaultConfigure {

	private Properties validateRules = null;
	private Properties failFileAttrs = null;
	private static final DefaultConfigure config = new DefaultConfigure();

	/**
	 * 构造函数
	 */
	private DefaultConfigure() {
		ResourceBundle bundle = ResourceBundle.getBundle("org.zqsoft.fileimport.resource.fileImport");
		BundleParser dp = new BundleParser(bundle);
		validateRules = dp.getPropertyGroup("validate");
		failFileAttrs = dp.getPropertyGroup("failFile");
	}

	/**
	 * 初始化方法
	 * 
	 * @return
	 */
	public static DefaultConfigure getInstance() {
		return config;
	}

	/**
	 * 获取导入失败文件的文件属性
	 * 
	 * @return
	 */
	public String getFailFileAttribute(String attr) {
		return failFileAttrs.getProperty(attr);
	}

	/**
	 * 获取校验规则
	 * 
	 * @param type
	 * @return
	 */
	public String getValidateRule(String type) {
		return validateRules.getProperty(type);
	}

}
