package org.zq.fileimport.entity;

import java.util.Properties;
import java.util.ResourceBundle;

import org.zq.util.BundleParser;

/**
 * Ĭ�ϵ�������
 * 
 * @author EddyZhou(zhouqian1103@gmail.com)
 * 
 */
public class DefaultConfigure {

	private Properties validateRules = null;
	private Properties failFileAttrs = null;
	private static final DefaultConfigure config = new DefaultConfigure();

	/**
	 * ���캯��
	 */
	private DefaultConfigure() {
		ResourceBundle bundle = ResourceBundle.getBundle("org.zqsoft.fileimport.resource.fileImport");
		BundleParser dp = new BundleParser(bundle);
		validateRules = dp.getPropertyGroup("validate");
		failFileAttrs = dp.getPropertyGroup("failFile");
	}

	/**
	 * ��ʼ������
	 * 
	 * @return
	 */
	public static DefaultConfigure getInstance() {
		return config;
	}

	/**
	 * ��ȡ����ʧ���ļ����ļ�����
	 * 
	 * @return
	 */
	public String getFailFileAttribute(String attr) {
		return failFileAttrs.getProperty(attr);
	}

	/**
	 * ��ȡУ�����
	 * 
	 * @param type
	 * @return
	 */
	public String getValidateRule(String type) {
		return validateRules.getProperty(type);
	}

}
