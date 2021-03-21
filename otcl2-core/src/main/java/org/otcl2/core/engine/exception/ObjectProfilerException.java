/**
* Copyright (c) otclfoundation.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*/
package org.otcl2.core.engine.exception;

import org.otcl2.common.exception.OtclException;

// TODO: Auto-generated Javadoc
/**
 * The Class ObjectProfilerException.
 */
public class ObjectProfilerException extends OtclException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -7030529648109361953L;

	/**
	 * Instantiates a new object profiler exception.
	 *
	 * @param errorCode the error code
	 */
	public ObjectProfilerException(String errorCode) {
		super(errorCode);
	}

	/**
	 * Instantiates a new object profiler exception.
	 *
	 * @param errorCode the error code
	 * @param msg the msg
	 */
	public ObjectProfilerException(String errorCode, String msg) {
		super(errorCode, msg);
	}

	/**
	 * Instantiates a new object profiler exception.
	 *
	 * @param cause the cause
	 */
	public ObjectProfilerException(Throwable cause) {
		super(cause);
	}

	/**
	 * Instantiates a new object profiler exception.
	 *
	 * @param errorCode the error code
	 * @param cause the cause
	 */
	public ObjectProfilerException(String errorCode, Throwable cause) {
		super(errorCode, cause);
	}

	/**
	 * Instantiates a new object profiler exception.
	 *
	 * @param errorCode the error code
	 * @param msg the msg
	 * @param cause the cause
	 */
	public ObjectProfilerException(String errorCode, String msg, Throwable cause) {
		super(errorCode, msg, cause);
	}
}
