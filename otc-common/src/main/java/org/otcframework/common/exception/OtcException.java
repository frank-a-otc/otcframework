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
package org.otcframework.common.exception;

// TODO: Auto-generated Javadoc
/**
 * The Class OtcException.
 */
public class OtcException extends RuntimeException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6218106303823662498L;
	
	/** The error code. */
	private String errorCode;

	/**
	 * Instantiates a new otc exception.
	 *
	 * @param errorCode the error code
	 */
	public OtcException(String errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * Instantiates a new otc exception.
	 *
	 * @param errorCode the error code
	 * @param msg the msg
	 */
	public OtcException(String errorCode, String msg) {
		super(msg);
		this.errorCode = errorCode;
	}

	/**
	 * Instantiates a new otc exception.
	 *
	 * @param cause the cause
	 */
	public OtcException(Throwable cause) {
		super(cause);
	}

	/**
	 * Instantiates a new otc exception.
	 *
	 * @param errorCode the error code
	 * @param cause the cause
	 */
	public OtcException(String errorCode, Throwable cause) {
		super(cause);
		this.errorCode = errorCode;
	}

	/**
	 * Instantiates a new otc exception.
	 *
	 * @param errorCode the error code
	 * @param msg the msg
	 * @param cause the cause
	 */
	public OtcException(String errorCode, String msg, Throwable cause) {
		super(msg, cause);
		this.errorCode = errorCode;
	}

	/**
	 * Gets the error code.
	 *
	 * @return the error code
	 */
	public String getErrorCode() {
		return errorCode;
	}
}
