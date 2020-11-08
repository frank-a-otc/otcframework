/**
* Copyright (c) otcl2.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*/
package org.otcl2.core.engine.compiler.exception;

import org.otcl2.common.exception.OtclException;

public class SemanticsException extends OtclException {

	private static final long serialVersionUID = 1808780652170188096L;

	public SemanticsException(String errorCode) {
		super(errorCode);
	}

	public SemanticsException(String errorCode, String msg) {
		super(errorCode, msg);
	}

	public SemanticsException(Throwable cause) {
		super(cause);
	}

	public SemanticsException(String errorCode, Throwable cause) {
		super(errorCode, cause);
	}

	public SemanticsException(String errorCode, String msg, Throwable cause) {
		super(errorCode, msg, cause);
	}
}
