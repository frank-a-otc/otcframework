/**
* Copyright (c) otcl2.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*/
package org.otcl2.core.engine.exception;

import org.otcl2.common.exception.OtclException;

public class ObjectProfilerException extends OtclException {

	private static final long serialVersionUID = -7030529648109361953L;

	public ObjectProfilerException(String errorCode) {
		super(errorCode);
	}

	public ObjectProfilerException(String errorCode, String msg) {
		super(errorCode, msg);
	}

	public ObjectProfilerException(Throwable cause) {
		super(cause);
	}

	public ObjectProfilerException(String errorCode, Throwable cause) {
		super(errorCode, cause);
	}

	public ObjectProfilerException(String errorCode, String msg, Throwable cause) {
		super(errorCode, msg, cause);
	}
}
