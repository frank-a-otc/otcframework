/**
* Copyright (c) otcl2.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*/
package org.otcl2.common.engine.executor;

import java.util.Map;

import org.otcl2.common.engine.profiler.dto.IndexedCollectionsDto;

public interface CodeExecutor<S, T> {

	public T execute(S srcObject, IndexedCollectionsDto sourcePCD, Map<String, Object> config);
}
