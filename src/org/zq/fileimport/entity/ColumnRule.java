package org.zq.fileimport.entity;

import org.zq.util.StringUtil;

/**
 * 字段(列)的校验规则
 * 
 * @author EddyZhou(zhouqian1103@gmail.com)
 * 
 */
public class ColumnRule {
	/** 列名 */
	private String columnName;

	/** 字段类型 */
	private FieldType fieldType;

	/** 是否可以为空 */
	private boolean canBeNull;
	
	/** 字段最大长度 */
	private int maxLength = Long.SIZE;

	/** 校验类型 */
	private ValidateType validateType;

	/** 校验的正则表达式 */
	private String expression;

	public ColumnRule() {
		super();
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		if (StringUtil.isEmpty(columnName)) 
			throw new IllegalArgumentException("Column name can not be null or empty.");
		
		this.columnName = columnName;
	}

	public FieldType getFieldType() {
		return fieldType;
	}

	public void setFieldType(FieldType fieldType) {
		if (fieldType == null || fieldType == FieldType.Unknown)
			throw new IllegalArgumentException("Field type not valid");
		
		this.fieldType = fieldType;
	}

	public boolean canBeNull() {
		return canBeNull;
	}

	public void setCanBeNull(boolean canBeNull) {
		this.canBeNull = canBeNull;
	}

	public ValidateType getValidateType() {
		return validateType;
	}

	public void setValidateType(ValidateType validateType) {
		this.validateType = validateType;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}
	
	public int getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	/**
	 * 校验的枚举类型
	 * 
	 * @author zhouqian
	 * 
	 */
	public static enum ValidateType {
		Email("email", "To validate email address"), 
		NULL("null", "No need to validate"), 
		Unknown("unkown", "Unkown type");

		private String type;
		private String description;

		private ValidateType(String type, String description) {
			this.type = type;
			this.description = description;
		}

		public String getType() {
			return type;
		}

		public String getDescription() {
			return description;
		}

		public static ValidateType getValidateType(String type) {
			for (ValidateType vtype : ValidateType.values()) {
				if (vtype.getType().equals(type)) {
					return vtype;
				}
			}

			return Unknown;
		}
	}

	/**
	 * 字段类型
	 * 
	 * @author x_zhouqian
	 * 
	 */
	public static enum FieldType {
		String("string", "char, varchar, text..."), 
		Number("number", "int, float..."), 
		Date("date", "date, time"), 
		Unknown("unkown", "unkown type");

		private String type;
		private String description;

		private FieldType(String type, String description) {
			this.type = type;
			this.description = description;
		}

		public String getType() {
			return type;
		}

		public String getDescription() {
			return description;
		}

		public static FieldType getFieldType(String type) {
			for (FieldType ftype : FieldType.values()) {
				if (ftype.getType().equals(type)) {
					return ftype;
				}
			}

			return Unknown;
		}
	}
}
