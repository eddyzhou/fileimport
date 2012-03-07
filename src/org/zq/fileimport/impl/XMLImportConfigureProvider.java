package org.zq.fileimport.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.zq.fileimport.ConfigurationException;
import org.zq.fileimport.ImportConfigure;
import org.zq.fileimport.ImportConfigureProvider;
import org.zq.fileimport.entity.ColumnRule;
import org.zq.fileimport.entity.DefaultConfigure;
import org.zq.fileimport.entity.TableRule;
import org.zq.fileimport.entity.ColumnRule.FieldType;
import org.zq.fileimport.entity.ColumnRule.ValidateType;
import org.zq.util.StringUtil;

/**
 * 解析XML文件定义的导入规则
 * 
 * @author EddyZhou(zhouqian1103@gmail.com)
 * 
 */
public class XMLImportConfigureProvider implements ImportConfigureProvider {
	/** 日志实例 */
	private static final Log logger = LogFactory
			.getLog(XMLImportConfigureProvider.class);

	/** 配置接口 */
	private ImportConfigure importConfigure = null;

	/**
	 * 默认构造函数
	 */
	public XMLImportConfigureProvider() {
	}

	/**
	 * 装载配置文件
	 * 
	 * @param configFileName
	 * @return
	 */
	public ImportConfigure loadConfigure(String configFileName) {
		if (StringUtil.isEmpty(configFileName))
			throw new IllegalArgumentException(
					"Config fileName can not be null or blank.");

		importConfigure = DefaultImportConfigure.getInstance(configFileName);
		if (importConfigure.getDefaultConfigure() == null)
			initDefaultConfig();

		if (importConfigure.getTableRule() != null)
			logger.info("The configure file [" + configFileName
					+ "] has been loaded.");
		else {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = null;
			try {
				db = dbf.newDocumentBuilder();
				loadConfigurationFile(configFileName, db);
			} catch (ParserConfigurationException e) {
				logger
						.error(
								"Could not load file import configuration file, failing",
								e);
				throw new ConfigurationException(
						"Error loading configuration file " + configFileName, e);
			}
		}

		return importConfigure;
	}

	/**
	 * 初始化默认的校验规则
	 */
	private void initDefaultConfig() {
		importConfigure.setDefaultConfigure(DefaultConfigure.getInstance());
	}

	/**
	 * 装载配置文件
	 * 
	 * @param fileName
	 * @param db
	 */
	private void loadConfigurationFile(String fileName, DocumentBuilder db) {
		if (logger.isDebugEnabled()) {
			logger.debug("Loading file import Configuration from: " + fileName);
		}

		InputStream is = null;
		Document doc = null;
		try {
			is = getInputStream(fileName);
			if (is == null) {
				throw new ConfigurationException("Could not open file: "
						+ fileName);
			}
			doc = db.parse(is);
		} catch (Exception e) {
			final String message = "Caught exception while loading file "
					+ fileName;
			logger.error(message, e);
			throw new ConfigurationException(message, e);
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException e) {
				logger.error("Unable to close input stream", e);
			}
		}

		Element rootElement = doc.getDocumentElement();
		NodeList children = rootElement.getChildNodes();
		int childSize = children.getLength();
		for (int i = 0; i < childSize; i++) {
			Node childNode = children.item(i);
			if (childNode instanceof Element) {
				Element child = (Element) childNode;
				String nodeName = child.getNodeName();
				if (nodeName.equalsIgnoreCase("tableConfigure")) {
					loadTableRule(child);
				} else if (nodeName.equalsIgnoreCase("include")) {
					String includeFileName = child.getAttribute("file");
					loadConfigurationFile(includeFileName, db);
				}
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Loaded import file configuration from: " + fileName);
		}
	}

	/**
	 * 装载表的规则
	 * 
	 * @param element
	 */
	private void loadTableRule(Element element) {
		TableRule tableRule = new TableRule();
		NodeList children = element.getChildNodes();
		int childSize = children.getLength();
		for (int i = 0; i < childSize; i++) {
			Node childNode = children.item(i);
			if (childNode instanceof Element) {
				Element child = (Element) childNode;
				String nodeName = child.getNodeName();
				if (nodeName.equalsIgnoreCase("tableName")) {
					tableRule.setTableName(getText(child).trim());
				} else if (nodeName.equalsIgnoreCase("hasTitle")) {
					tableRule.setHasTitle(new Boolean(getText(child).trim())
							.booleanValue());
				} else if (nodeName.equalsIgnoreCase("columns")) {
					loadColumnRules(tableRule, child);
				}
			}
		}

		importConfigure.setTableRule(tableRule);
	}

	/**
	 * 装载列的规则
	 * 
	 * @param tableRule
	 * @param element
	 */
	private void loadColumnRules(TableRule tableRule, Element element) {
		List<Element> columnList = getChildElementsByTagName(element, "column");
		for (Element child : columnList) {
			ColumnRule columnRule = new ColumnRule();
			columnRule.setColumnName(child.getAttribute("name"));

			columnRule.setFieldType(FieldType.getFieldType(child
					.getAttribute("type")));
			if (columnRule.getFieldType() == FieldType.Unknown) {
				final String message = "Field type not be supported. Please check the configure file";
				logger.error(message);
				throw new TypeNotPresentException(message, null);
			}

			columnRule.setValidateType("".equals(child
					.getAttribute("validateType")) ? ValidateType.NULL
					: ValidateType.getValidateType(child
							.getAttribute("validateType")));

			columnRule
					.setCanBeNull(new Boolean(child.getAttribute("canBeNull"))
							.booleanValue());
			if (StringUtil.isNotEmpty(child.getAttribute("maxlength")))
				columnRule.setMaxLength(Integer.parseInt(child
						.getAttribute("maxlength")));

			columnRule.setExpression(child.getAttribute("expression"));

			//tableRule.addColumnRule(columnRule.getColumnName(), columnRule);
			tableRule.addColumnRule(columnRule);
		}
	}

	/**
	 * 获取节点的内容信息
	 * 
	 * @param element
	 * @return
	 */
	private String getText(Element element) {
		StringBuilder sb = new StringBuilder();
		NodeList nodeList = element.getChildNodes();

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);

			switch (node.getNodeType()) {
			case Node.CDATA_SECTION_NODE:
			case Node.TEXT_NODE:
				sb.append(node.getNodeValue());
			}
		}

		return sb.toString();
	}

	/**
	 * 获取子结点的对象集合
	 * 
	 * @param element
	 * @param childEleName
	 * @return
	 */
	private List<Element> getChildElementsByTagName(Element element,
			String childEleName) {
		NodeList nodeList = element.getElementsByTagName(childEleName);
		List<Element> list = new ArrayList<Element>();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node instanceof Element
					&& childEleName.equals(node.getNodeName())) {
				list.add((Element) node);
			}
		}
		return list;
	}

	/**
	 * 获取指定文件的输入流
	 * 
	 * @param fileName
	 * @return
	 */
	private InputStream getInputStream(String fileName) {
		URL fileUrl = Thread.currentThread().getContextClassLoader()
				.getResource(fileName);
		if (fileUrl == null) {
			return null;
		}

		InputStream is;
		try {
			is = fileUrl.openStream();
			if (is == null) {
				throw new IllegalArgumentException("No file " + fileName
						+ "found as a resource");
			}
		} catch (IOException e) {
			throw new IllegalArgumentException("No file '" + fileName
					+ "' found as a resource");
		}

		return is;
	}

	/**
	 * 获取导入配置
	 * 
	 * @return
	 */
	// public ImportConfigure getImportConfigure() {
	// return importConfigure;
	// }
}
