package org.zq.fileimport.impl;

import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zq.fileimport.ImportDbProcessor;
import org.zq.fileimport.ImportConfigure;
import org.zq.fileimport.ImportLineProcessor;
import org.zq.fileimport.entity.ColumnRecord;
import org.zq.fileimport.entity.ColumnRule;
import org.zq.fileimport.entity.ImportResult;
import org.zq.fileimport.entity.RowRecord;
import org.zq.fileimport.entity.TableRule;
import org.zq.fileimport.entity.ColumnRule.FieldType;
import org.zq.fileimport.entity.ColumnRule.ValidateType;
import org.zq.util.RegexUtil;
import org.zq.util.StringUtil;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * 默认的行记录处理接口实现类
 * 
 * @author EddyZhou(zhouqian1103@gmail.com)
 * 
 */
public class DefaultImportLineProcessor implements ImportLineProcessor {
	/** 日志实例 */
	private static final Log logger = LogFactory
			.getLog(DefaultImportLineProcessor.class);

	/** 配置接口 */
	private ImportConfigure importConfigure = null;

	private ImportDbProcessor dbProcessor = null;

	public void setDbProcessor(ImportDbProcessor dbProcessor) {
		this.dbProcessor = dbProcessor;
	}

	public void setImportConfigure(ImportConfigure importConfigure) {
		this.importConfigure = importConfigure;
	}

	/**
	 * 构造函数
	 * 
	 * @param importConfigure
	 * @param dbProcessor
	 */
	public DefaultImportLineProcessor(ImportConfigure importConfigure,
			ImportDbProcessor dbProcessor) {
		this.importConfigure = importConfigure;
		this.dbProcessor = dbProcessor;
	}

	/**
	 * 处理行记录(eg:存入数据库)
	 * 
	 * @param tableName
	 * @param lineRecord
	 * @return
	 */
	public void process(String tableName, String[] lineRecord,
			ImportResult result) {
		TableRule tableRule = importConfigure.getTableRule();

		if (tableRule.getColumnRules().size() > lineRecord[0].length()) {
			logger.error("Columns of one row is less than configure file");
			processFailureRecord(lineRecord, result);
			return;
		}

		RowRecord rowRecord = new RowRecord();
		int col = 0;
		for (ColumnRule cRule : tableRule.getColumnRules()) {
			if (!isValid(lineRecord[col], cRule)) {
				logger.error("Column record[columnNo:"
						+ col + ",data:" + lineRecord[col] + "] not valid");
				processFailureRecord(lineRecord, result);
				col++;
				break;
			}
			ColumnRecord cRecord = new ColumnRecord(cRule, lineRecord[col]);
			rowRecord.addColumn(cRule.getColumnName(), cRecord);
			col++;
		}

		boolean isSuccess = dbProcessor.insertRecord(tableName, rowRecord);
		if (!isSuccess) {
			processFailureRecord(lineRecord, result);
			result.addFailure();
		}
		
	}

	/**
	 * 校验行记录
	 * 
	 * @param columnRecord
	 * @param cRule
	 * @return
	 */
	public boolean isValid(String columnRecord, ColumnRule rule) {
		if (columnRecord == null)
			return false;

		if ("".equals(columnRecord.trim())) {
			if (rule.canBeNull())
				return true;
			else
				return false;
		}

		if (rule.getMaxLength() > 0
				&& columnRecord.length() > rule.getMaxLength())
			return false;

		FieldType ftype = rule.getFieldType();
		ValidateType vtype = rule.getValidateType();
		String expression = rule.getExpression();
		switch (ftype) {
		case Number:
			if (StringUtil.isDigist(columnRecord))
				return true;
			else
				return false;
		case String:
			if (vtype == ValidateType.NULL) {
				if (StringUtil.isEmpty(expression)) {
					return true;
				} else {
					return RegexUtil.isMatched(expression, columnRecord,
							Pattern.UNICODE_CASE);
				}
			} else if (vtype == ValidateType.Unknown) {
				return RegexUtil.isMatched(expression, columnRecord,
						Pattern.UNICODE_CASE);
			} else {
				String regex = importConfigure.getValidateRule(vtype.getType());
				if (regex != null)
					return RegexUtil.isMatched(regex, columnRecord,
							Pattern.UNICODE_CASE);
				else if (expression != null)
					return RegexUtil.isMatched(regex, columnRecord,
							Pattern.UNICODE_CASE);
				else
					return true;
			}

		case Date:
			try {
				java.sql.Date.valueOf(columnRecord);
				return true;
			} catch (IllegalArgumentException e) {
				return false;
			}

		case Unknown:
			return false;
		}

		return RegexUtil.isMatched(rule.getExpression(), columnRecord,
				Pattern.CASE_INSENSITIVE);
	}

	/**
	 * 处理失败的记录
	 * 
	 * @param rowRecord
	 */
	public void processFailureRecord(String[] rowRecord, ImportResult result) {
		if (rowRecord == null) {
			return;
		}

		if (result.getFailFileName() == null) {
			result.setFailFileName(generateFailFileName());
		}

		FileWriter fileWriter = null;
		CSVWriter writer = null;
		try {
			fileWriter = new FileWriter(result.getFailFileName());
			writer = new CSVWriter(fileWriter, '\t');
			writer.writeNext(rowRecord);
		} catch (IOException e) {
			logger.error("Caugth an exception while write record to fail file",
					e);
		} finally {
			try {
				fileWriter.close();
				writer.close();
			} catch (IOException e) {
				logger.error("Close FileWriter failure", e);
			}
		}

	}

	/**
	 * 生成存储失败记录的文件的文件名
	 * 
	 * @return
	 */
	private String generateFailFileName() {
		String failFilePath = importConfigure.getFailFileDir();
		String failFileName = importConfigure.getFailFilePrefix();

		java.util.Date date = new java.util.Date(System.currentTimeMillis());
		java.text.SimpleDateFormat fdt = new java.text.SimpleDateFormat(
				"yyyyMMddHHmmssSSS");

		return (failFilePath.endsWith("/") ? failFilePath : failFilePath + "/")
				+ failFileName + "_" + fdt.format(date) + ".csv";
	}

}
