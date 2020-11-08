/**
* Copyright (c) otcl2.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*/
package org.otcl2.common.exception;

public class OtclException extends RuntimeException {

	private static final long serialVersionUID = 6218106303823662498L;
	private String errorCode;

	public OtclException(String errorCode) {
		this.errorCode = errorCode;
	}

	public OtclException(String errorCode, String msg) {
		super(msg);
		this.errorCode = errorCode;
	}

	public OtclException(Throwable cause) {
		super(cause);
	}

	public OtclException(String errorCode, Throwable cause) {
		super(cause);
		this.errorCode = errorCode;
	}

	public OtclException(String errorCode, String msg, Throwable cause) {
		super(msg, cause);
		this.errorCode = errorCode;
	}

	public String getErrorCode() {
		return errorCode;
	}
}
