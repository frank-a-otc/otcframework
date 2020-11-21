/**
* Copyright (c) otclfoundation.org - All Rights Reserved
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*/
package org.otcl2.common.config.exception;

import org.otcl2.common.exception.OtclException;

// TODO: Auto-generated Javadoc
/**
 * The Class OtclConfigException.
 */
public class OtclConfigException extends OtclException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -3222880717814289359L;

	/**
	 * Instantiates a new otcl config exception.
	 *
	 * @param errorCode the error code
	 */
	public OtclConfigException(String errorCode) {
		super(errorCode);
	}

	/**
	 * Instantiates a new otcl config exception.
	 *
	 * @param errorCode the error code
	 * @param msg the msg
	 */
	public OtclConfigException(String errorCode, String msg) {
		super(errorCode, msg);
	}

	/**
	 * Instantiates a new otcl config exception.
	 *
	 * @param cause the cause
	 */
	public OtclConfigException(Throwable cause) {
		super(cause);
	}

	/**
	 * Instantiates a new otcl config exception.
	 *
	 * @param errorCode the error code
	 * @param cause the cause
	 */
	public OtclConfigException(String errorCode, Throwable cause) {
		super(errorCode, cause);
	}

	/**
	 * Instantiates a new otcl config exception.
	 *
	 * @param errorCode the error code
	 * @param msg the msg
	 * @param cause the cause
	 */
	public OtclConfigException(String errorCode, String msg, Throwable cause) {
		super(errorCode, msg, cause);
	}
}
