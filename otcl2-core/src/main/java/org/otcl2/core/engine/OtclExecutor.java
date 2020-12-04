/**
* Copyright (c) otclfoundation.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*/
package org.otcl2.core.engine;

import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * The Interface OtclExecutor.
 */
interface OtclExecutor {

	/**
	 * Execute otcl.
	 *
	 * @param <T> the generic type
	 * @param <S> the generic type
	 * @param otclNamespace the otcl namespace
	 * @param source the source
	 * @param targetClz the target clz
	 * @param data the data
	 * @return the t
	 */
	<T, S> T executeOtcl(String otclNamespace, S source, Class<T> targetClz, Map<String, Object> data);

	/**
	 * Execute otcl.
	 *
	 * @param <T> the generic type
	 * @param otclNamespace the otcl namespace
	 * @param targetClz the target clz
	 * @param data the data
	 * @return the t
	 */
	<T> T executeOtcl(String otclNamespace, Class<T> targetClz, Map<String, Object> data);
}
