/**
* Copyright (c) otcl2.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*/
package org.otcl2.common.engine.profiler.dto;

import java.util.Map;

public class IndexedCollectionsDto {

	public String id;
	public Object profiledObject;
	public Map<String, IndexedCollectionsDto> children;

}
