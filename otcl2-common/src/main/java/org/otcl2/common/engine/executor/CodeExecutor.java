/**
* Copyright (c) otclfoundation.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*/
package org.otcl2.common.engine.executor;

import java.util.Map;

import org.otcl2.common.engine.profiler.dto.IndexedCollectionsDto;

// TODO: Auto-generated Javadoc
/**
 * The Interface CodeExecutor.
 *
 * @param <S> the generic type
 * @param <T> the generic type
 */
public interface CodeExecutor<S, T> {

	/**
	 * Execute.
	 *
	 * @param srcObject the src object
	 * @param sourcePCD the source PCD
	 * @param config the config
	 * @return the t
	 */
	public T execute(S srcObject, IndexedCollectionsDto sourcePCD, Map<String, Object> config);
}
