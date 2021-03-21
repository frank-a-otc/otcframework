/**
* Copyright (c) otclfoundation.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*/
package org.otcl2.common.engine.profiler;

import java.util.HashMap;

import org.otcl2.common.dto.OtclCommandDto;
import org.otcl2.common.engine.compiler.OtclCommandContext;
import org.otcl2.common.engine.profiler.dto.IndexedCollectionsDto;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating IndexedCollectionsDto objects.
 */
public class IndexedCollectionsDtoFactory {

	/**
	 * Creates a new IndexedCollectionsDto object.
	 *
	 * @param otclCommandDto the otcl command dto
	 * @param hasChildren the has children
	 * @param value the value
	 * @param idxStr the idx str
	 * @return the indexed collections dto
	 */
	public static IndexedCollectionsDto createRoot(OtclCommandDto otclCommandDto, boolean hasChildren, Object value,
			String idxStr) {
		IndexedCollectionsDto parentPCD = new IndexedCollectionsDto();
		parentPCD.profiledObject = value;
		parentPCD.id = idxStr;
		if (hasChildren) {
			parentPCD.children = new HashMap<>();
		}
		return parentPCD;
	}

	/**
	 * Creates the.
	 *
	 * @param otclCommandContext the otcl command context
	 * @param parentPCD the parent PCD
	 * @param value the value
	 * @param idxStr the idx str
	 * @return the indexed collections dto
	 */
	public static IndexedCollectionsDto create(OtclCommandContext otclCommandContext, IndexedCollectionsDto parentPCD,
			Object value, String idxStr) {
		IndexedCollectionsDto childPOD = new IndexedCollectionsDto();
		if (parentPCD != null) {
			parentPCD.children.put(idxStr, childPOD);
		}
		childPOD.profiledObject = value;
//		if (otclCommandDto.hasDescendantCollectionOrMap(otclTokens)) {
//		if (otclCommandContext.hasDescendantCollectionOrMap()) {
			childPOD.children = new HashMap<>();
//		}
		return childPOD;
	}

	/**
	 * Creates the.
	 *
	 * @param parentPCD the parent PCD
	 * @param value the value
	 * @param idxStr the idx str
	 * @param hasChildren the has children
	 * @return the indexed collections dto
	 */
	public static IndexedCollectionsDto create(IndexedCollectionsDto parentPCD, Object value, String idxStr,
			boolean hasChildren) {
		if (parentPCD.children == null) {
			parentPCD.children = new HashMap<>();
		}
		IndexedCollectionsDto childPOD = new IndexedCollectionsDto();
		parentPCD.children.put(idxStr, childPOD);			
		childPOD.profiledObject = value;
		childPOD.id = idxStr;
		if (hasChildren) {
			childPOD.children = new HashMap<>();
		}
		return childPOD;
	}
}
