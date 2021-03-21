/**
* Copyright (c) otclfoundation.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*/
package org.otcl2.common.engine.profiler.dto;

import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * The Class IndexedCollectionsDto.
 */
public class IndexedCollectionsDto {

	/** The id. */
	public String id;
	
	/** The profiled object. */
	public Object profiledObject;
	
	/** The children. */
	public Map<String, IndexedCollectionsDto> children;

}
