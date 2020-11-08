/**
* Copyright (c) otcl2.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*/
package org.otcl2.common.exception;

public class PropertyConverterException extends OtclException {

	private static final long serialVersionUID = -7030529648109361953L;

	public PropertyConverterException(String errorCode) {
		super(errorCode);
	}

	public PropertyConverterException(String errorCode, String msg) {
		super(errorCode, msg);
	}

	public PropertyConverterException(Throwable cause) {
		super(cause);
	}

	public PropertyConverterException(String errorCode, Throwable cause) {
		super(errorCode, cause);
	}

	public PropertyConverterException(String errorCode, String msg, Throwable cause) {
		super(errorCode, msg, cause);
	}
}
