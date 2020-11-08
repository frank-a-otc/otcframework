/**
* Copyright (c) otcl2.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*/
package org.otcl2.core.engine;

import java.util.Map;

interface OtclExecutor {

	<T, S> T executeOtcl(Class<T> targetClz, Map<String, Object> data);

	<T, S> T executeOtcl(S source, Class<T> targetClz, Map<String, Object> data);

	<T, S> T executeOtcl(String otclNamespace, S source, Class<T> targetClz, Map<String, Object> data);

	<T> T executeOtcl(String otclNamespace, Class<T> targetClz, Map<String, Object> data);
}
