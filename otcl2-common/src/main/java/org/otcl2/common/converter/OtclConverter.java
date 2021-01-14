/**
* Copyright (c) otclfoundation.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*/
package org.otcl2.common.converter;

import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * The Interface OtclConverter.
 */
public interface OtclConverter {

	/**
	 * Convert.
	 *
	 * @param <T> the generic type
	 * @param <S> the generic type
	 * @param targetObject the target object
	 * @param sourceObject the source object
	 * @param data the data
	 * @return the t
	 */
	public <T, S> T convert(S sourceObject, T targetObject, Map<String, Object> data);
}
