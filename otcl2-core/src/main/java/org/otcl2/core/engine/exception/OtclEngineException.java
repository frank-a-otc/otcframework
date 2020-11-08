/**
* Copyright (c) otcl2.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*/
package org.otcl2.core.engine.exception;

import org.otcl2.common.exception.OtclException;

public class OtclEngineException extends OtclException {

	private static final long serialVersionUID = -3222880717814289359L;

	public OtclEngineException(String errorCode) {
		super(errorCode);
	}

	public OtclEngineException(String errorCode, String msg) {
		super(errorCode, msg);
	}

	public OtclEngineException(Throwable cause) {
		super(cause);
	}

	public OtclEngineException(String errorCode, Throwable cause) {
		super(errorCode, cause);
	}

	public OtclEngineException(String errorCode, String msg, Throwable cause) {
		super(errorCode, msg, cause);
	}
}
