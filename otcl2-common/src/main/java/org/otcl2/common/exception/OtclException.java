/**
* Copyright (c) otclfoundation.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*/
package org.otcl2.common.exception;

// TODO: Auto-generated Javadoc
/**
 * The Class OtclException.
 */
public class OtclException extends RuntimeException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6218106303823662498L;
	
	/** The error code. */
	private String errorCode;

	/**
	 * Instantiates a new otcl exception.
	 *
	 * @param errorCode the error code
	 */
	public OtclException(String errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * Instantiates a new otcl exception.
	 *
	 * @param errorCode the error code
	 * @param msg the msg
	 */
	public OtclException(String errorCode, String msg) {
		super(msg);
		this.errorCode = errorCode;
	}

	/**
	 * Instantiates a new otcl exception.
	 *
	 * @param cause the cause
	 */
	public OtclException(Throwable cause) {
		super(cause);
	}

	/**
	 * Instantiates a new otcl exception.
	 *
	 * @param errorCode the error code
	 * @param cause the cause
	 */
	public OtclException(String errorCode, Throwable cause) {
		super(cause);
		this.errorCode = errorCode;
	}

	/**
	 * Instantiates a new otcl exception.
	 *
	 * @param errorCode the error code
	 * @param msg the msg
	 * @param cause the cause
	 */
	public OtclException(String errorCode, String msg, Throwable cause) {
		super(msg, cause);
		this.errorCode = errorCode;
	}

	/**
	 * Gets the error code.
	 *
	 * @return the error code
	 */
	public String getErrorCode() {
		return errorCode;
	}
}
