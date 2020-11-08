/**
* Copyright (c) otcl2.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*/
package org.otcl2.common.config.exception;

import org.otcl2.common.exception.OtclException;

public class OtclConfigException extends OtclException {

	private static final long serialVersionUID = -3222880717814289359L;

	public OtclConfigException(String errorCode) {
		super(errorCode);
	}

	public OtclConfigException(String errorCode, String msg) {
		super(errorCode, msg);
	}

	public OtclConfigException(Throwable cause) {
		super(cause);
	}

	public OtclConfigException(String errorCode, Throwable cause) {
		super(errorCode, cause);
	}

	public OtclConfigException(String errorCode, String msg, Throwable cause) {
		super(errorCode, msg, cause);
	}
}
