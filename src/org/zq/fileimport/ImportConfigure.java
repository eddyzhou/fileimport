package org.zq.fileimport;

import org.zq.fileimport.entity.DefaultConfigure;
import org.zq.fileimport.entity.TableRule;

/**
 * �������ýӿ�
 * 
 * @author EddyZhou(zhouqian1103@gmail.com)
 * 
 */
public interface ImportConfigure {

	/**
	 * ����Ĭ������
	 * 
	 * @param type
	 * @param expression
	 */
	public void setDefaultConfigure(DefaultConfigure config);

	/**
	 * ��ȡĬ������
	 * 
	 * @return
	 */
	public DefaultConfigure getDefaultConfigure();

	/**
	 * ���ñ����
	 * 
	 * @param tableRule
	 */
	public void setTableRule(TableRule tableRule);

	/**
	 * ��ȡ�����
	 * 
	 * @return
	 */
	public TableRule getTableRule();

	/**
	 * ��ȡ��ŵ���ʧ�ܼ�¼���ļ���·��
	 * 
	 * @return
	 */
	public String getFailFileDir();

	/**
	 * ��ȡȡ��ŵ���ʧ�ܼ�¼���ļ���ǰ׺
	 * 
	 * @return
	 */
	public String getFailFilePrefix();

	/**
	 * ��ȡУ�����
	 * 
	 * @param key
	 * @return
	 */
	public String getValidateRule(String key);
}
