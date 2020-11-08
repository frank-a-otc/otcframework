/**
* Copyright (c) otcl2.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*/
package org.otcl2.core.engine.compiler.exception;

import org.otcl2.common.exception.OtclException;

public class DeploymentContainerException extends OtclException {

	private static final long serialVersionUID = 6981095407201965987L;

	public DeploymentContainerException(String errorCode) {
		super(errorCode);
	}

	public DeploymentContainerException(String errorCode, String msg) {
		super(errorCode, msg);
	}

	public DeploymentContainerException(Throwable cause) {
		super(cause);
	}

	public DeploymentContainerException(String errorCode, Throwable cause) {
		super(errorCode, cause);
	}

	public DeploymentContainerException(String errorCode, String msg, Throwable cause) {
		super(errorCode, msg, cause);
	}
}
