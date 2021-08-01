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
package org.otcframework.core.engine.exception;

import org.otcframework.common.exception.OtcException;

// TODO: Auto-generated Javadoc
/**
 * The Class ObjectProfilerException.
 */
public class CollectionsIndexerException extends OtcException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -7030529648109361953L;

	/**
	 * Instantiates a new object indexer exception.
	 *
	 * @param errorCode the error code
	 */
	public CollectionsIndexerException(String errorCode) {
		super(errorCode);
	}

	/**
	 * Instantiates a new object indexer exception.
	 *
	 * @param errorCode the error code
	 * @param msg the msg
	 */
	public CollectionsIndexerException(String errorCode, String msg) {
		super(errorCode, msg);
	}

	/**
	 * Instantiates a new object indexer exception.
	 *
	 * @param cause the cause
	 */
	public CollectionsIndexerException(Throwable cause) {
		super(cause);
	}

	/**
	 * Instantiates a new object indexer exception.
	 *
	 * @param errorCode the error code
	 * @param cause the cause
	 */
	public CollectionsIndexerException(String errorCode, Throwable cause) {
		super(errorCode, cause);
	}

	/**
	 * Instantiates a new object indexer exception.
	 *
	 * @param errorCode the error code
	 * @param msg the msg
	 * @param cause the cause
	 */
	public CollectionsIndexerException(String errorCode, String msg, Throwable cause) {
		super(errorCode, msg, cause);
	}
}
