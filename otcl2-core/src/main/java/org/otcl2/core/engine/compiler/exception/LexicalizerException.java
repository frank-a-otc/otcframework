/**
* Copyright (c) otcl2.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*/
package org.otcl2.core.engine.compiler.exception;

import org.otcl2.common.exception.OtclException;

public class LexicalizerException extends OtclException {

	private static final long serialVersionUID = 1808780652170188096L;

	public LexicalizerException(String errorCode) {
		super(errorCode);
	}

	public LexicalizerException(String errorCode, String msg) {
		super(errorCode, msg);
	}

	public LexicalizerException(Throwable cause) {
		super(cause);
	}

	public LexicalizerException(String errorCode, Throwable cause) {
		super(errorCode, cause);
	}

	public LexicalizerException(String errorCode, String msg, Throwable cause) {
		super(errorCode, msg, cause);
	}
}
