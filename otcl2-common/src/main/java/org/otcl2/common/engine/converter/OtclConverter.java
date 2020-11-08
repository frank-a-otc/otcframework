/**
* Copyright (c) otcl2.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*/
package org.otcl2.common.engine.converter;

import java.util.Map;

public interface OtclConverter {

	public <T, S> T convert(T targetObject, S sourceObject, Map<String, Object> config);
}
