/**
* Copyright (c) otcframework.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*
* This file is part of the OTC framework.
* 
*  The OTC framework is free software: you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  the Free Software Foundation, version 3 of the License.
*
*  The OTC framework is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU General Public License for more details.
*
*  A copy of the GNU General Public License is made available as 'License.md' file, 
*  along with OTC framework project.  If not, see <https://www.gnu.org/licenses/>.
*
*/
package org.otcframework.core.engine.compiler.exception;

import org.otcframework.common.exception.OtcException;

/**
 * The Class SyntaxException.
 */
// TODO: Auto-generated Javadoc
public class SyntaxException extends OtcException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1808780652170188096L;

	/**
	 * Instantiates a new syntax exception.
	 *
	 * @param errorCode the error code
	 */
	public SyntaxException(String errorCode) {
		super(errorCode);
	}

	/**
	 * Instantiates a new syntax exception.
	 *
	 * @param errorCode the error code
	 * @param msg       the msg
	 */
	public SyntaxException(String errorCode, String msg) {
		super(errorCode, msg);
	}

	/**
	 * Instantiates a new syntax exception.
	 *
	 * @param cause the cause
	 */
	public SyntaxException(Throwable cause) {
		super(cause);
	}

	/**
	 * Instantiates a new syntax exception.
	 *
	 * @param errorCode the error code
	 * @param cause     the cause
	 */
	public SyntaxException(String errorCode, Throwable cause) {
		super(errorCode, cause);
	}

	/**
	 * Instantiates a new syntax exception.
	 *
	 * @param errorCode the error code
	 * @param msg       the msg
	 * @param cause     the cause
	 */
	public SyntaxException(String errorCode, String msg, Throwable cause) {
		super(errorCode, msg, cause);
	}
}
