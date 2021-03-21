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
 * The Class PropertyConverterException.
 */
public class PropertyConverterException extends OtclException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -7030529648109361953L;

	/**
	 * Instantiates a new property converter exception.
	 *
	 * @param errorCode the error code
	 */
	public PropertyConverterException(String errorCode) {
		super(errorCode);
	}

	/**
	 * Instantiates a new property converter exception.
	 *
	 * @param errorCode the error code
	 * @param msg the msg
	 */
	public PropertyConverterException(String errorCode, String msg) {
		super(errorCode, msg);
	}

	/**
	 * Instantiates a new property converter exception.
	 *
	 * @param cause the cause
	 */
	public PropertyConverterException(Throwable cause) {
		super(cause);
	}

	/**
	 * Instantiates a new property converter exception.
	 *
	 * @param errorCode the error code
	 * @param cause the cause
	 */
	public PropertyConverterException(String errorCode, Throwable cause) {
		super(errorCode, cause);
	}

	/**
	 * Instantiates a new property converter exception.
	 *
	 * @param errorCode the error code
	 * @param msg the msg
	 * @param cause the cause
	 */
	public PropertyConverterException(String errorCode, String msg, Throwable cause) {
		super(errorCode, msg, cause);
	}
}
