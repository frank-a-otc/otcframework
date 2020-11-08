/**
* Copyright (c) otcl2.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*/
package org.otcl2.common.engine;

import java.util.Map;

public interface OtclEngine {

	void compileOtcl();

	void compileSourceCode();

	void deploy();

	<T, S> T executeOtcl(Class<T> targetClz, Map<String, Object> data);

	<T, S> T executeOtcl(S source, Class<T> targetClz, Map<String, Object> data);

	<T, S> T executeOtcl(String otclNamespace, S source, Class<T> targetClz, Map<String, Object> data);

}
