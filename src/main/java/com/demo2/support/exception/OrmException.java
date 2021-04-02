/**
 * 
 */
package com.demo2.support.exception;

/**
 * @author fangang
 */
public class OrmException extends RuntimeException {

	private static final long serialVersionUID = -6657470429383742870L;

	/**
	 * 
	 */
	public OrmException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public OrmException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public OrmException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public OrmException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public OrmException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
