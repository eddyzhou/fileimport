package org.zq.fileimport;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * �����쳣
 * 
 * @author EddyZhou(zhouqian1103@gmail.com)
 * 
 */
public class ConfigurationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/** Throwableʵ�� */
	protected Throwable throwable;

	public ConfigurationException() {
		super();
	}

	/**
	 * ����ϸ��Ϣ�Ĺ�����
	 * 
	 * @param message
	 *            ��ϸ�Ĵ�����Ϣ
	 */
	public ConfigurationException(String message) {
		super(message);
	}

	/**
	 * ��Throwable�Ĺ�����
	 * 
	 * @param cause
	 */
	public ConfigurationException(Throwable cause) {
		this.throwable = cause;
	}

	public ConfigurationException(String message, Throwable cause) {
		super(message);
		this.throwable = cause;
	}

	public void printStackTrace(PrintStream ps) {
		super.printStackTrace(ps);
		if (throwable != null) {
			ps.println("with nested Exception:" + throwable);
			throwable.printStackTrace(ps);
		}
	}

	public void printStackTrace(PrintWriter pw) {
		super.printStackTrace(pw);
		if (throwable != null) {
			pw.println("with nested Exception:" + throwable);
			throwable.printStackTrace(pw);
		}
	}

	public String toString() {
		if (throwable == null) {
			return super.toString();
		} else {
			return super.toString() + "\n whith nested Exception:"
					+ throwable.toString();
		}
	}

	public Throwable getThrowable() {
		return throwable;
	}
}
