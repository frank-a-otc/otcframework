/**
* Copyright (c) otcl2.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*/
package org.otcl2.core.engine.compiler.exception;

import org.otcl2.common.exception.OtclException;

public class SyntaxException extends OtclException {

	private static final long serialVersionUID = 1808780652170188096L;

	public SyntaxException(String errorCode) {
		super(errorCode);
	}

	public SyntaxException(String errorCode, String msg) {
		super(errorCode, msg);
	}

	public SyntaxException(Throwable cause) {
		super(cause);
	}

	public SyntaxException(String errorCode, Throwable cause) {
		super(errorCode, cause);
	}

	public SyntaxException(String errorCode, String msg, Throwable cause) {
		super(errorCode, msg, cause);
	}
}
