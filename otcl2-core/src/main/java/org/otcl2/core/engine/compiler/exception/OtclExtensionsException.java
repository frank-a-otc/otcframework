/**
* Copyright (c) otclfoundation.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*/
package org.otcl2.core.engine.compiler.exception;

import org.otcl2.common.exception.OtclException;

// TODO: Auto-generated Javadoc
/**
 * The Class OtclExtensionsException.
 */
public class OtclExtensionsException extends OtclException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1808780652170188096L;

	/**
	 * Instantiates a new otcl extensions exception.
	 *
	 * @param errorCode the error code
	 */
	public OtclExtensionsException(String errorCode) {
		super(errorCode);
	}

	/**
	 * Instantiates a new otcl extensions exception.
	 *
	 * @param errorCode the error code
	 * @param msg the msg
	 */
	public OtclExtensionsException(String errorCode, String msg) {
		super(errorCode, msg);
	}

	/**
	 * Instantiates a new otcl extensions exception.
	 *
	 * @param cause the cause
	 */
	public OtclExtensionsException(Throwable cause) {
		super(cause);
	}

	/**
	 * Instantiates a new otcl extensions exception.
	 *
	 * @param errorCode the error code
	 * @param cause the cause
	 */
	public OtclExtensionsException(String errorCode, Throwable cause) {
		super(errorCode, cause);
	}

	/**
	 * Instantiates a new otcl extensions exception.
	 *
	 * @param errorCode the error code
	 * @param msg the msg
	 * @param cause the cause
	 */
	public OtclExtensionsException(String errorCode, String msg, Throwable cause) {
		super(errorCode, msg, cause);
	}
}
