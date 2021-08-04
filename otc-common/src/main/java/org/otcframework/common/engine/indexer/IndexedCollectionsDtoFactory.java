/**
* Copyright (c) otcframework.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*
* This file is part of the OTC framework.
* 
*  The OTC framework is free software: you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  the Free Software Foundation, version 3 of the License.
*
*  The OTC framework is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU General Public License for more details.
*
*  A copy of the GNU General Public License is made available as 'License.md' file, 
*  along with OTC framework project.  If not, see <https://www.gnu.org/licenses/>.
*
*/
package org.otcframework.common.engine.indexer;

import java.util.HashMap;

import org.otcframework.common.dto.OtcCommandDto;
import org.otcframework.common.engine.compiler.OtcCommandContext;
import org.otcframework.common.engine.indexer.dto.IndexedCollectionsDto;

/**
 * A factory for creating IndexedCollectionsDto objects.
 */
// TODO: Auto-generated Javadoc
public class IndexedCollectionsDtoFactory {

	/**
	 * Creates a new IndexedCollectionsDto object.
	 *
	 * @param otcCommandDto the otc command dto
	 * @param hasChildren   the has children
	 * @param value         the value
	 * @param idxStr        the idx str
	 * @return the indexed collections dto
	 */
	public static IndexedCollectionsDto createRoot(OtcCommandDto otcCommandDto, boolean hasChildren, Object value,
			String idxStr) {
		IndexedCollectionsDto parentPCD = new IndexedCollectionsDto();
		parentPCD.indexedObject = value;
		parentPCD.id = idxStr;
		if (hasChildren) {
			parentPCD.children = new HashMap<>();
		}
		return parentPCD;
	}

	/**
	 * Creates the.
	 *
	 * @param otcCommandContext the otc command context
	 * @param parentPCD         the parent PCD
	 * @param value             the value
	 * @param idxStr            the idx str
	 * @return the indexed collections dto
	 */
	public static IndexedCollectionsDto create(OtcCommandContext otcCommandContext, IndexedCollectionsDto parentPCD,
			Object value, String idxStr) {
		IndexedCollectionsDto childPOD = new IndexedCollectionsDto();
		if (parentPCD != null) {
			parentPCD.children.put(idxStr, childPOD);
		}
		childPOD.indexedObject = value;
		childPOD.children = new HashMap<>();
		return childPOD;
	}

	/**
	 * Creates the.
	 *
	 * @param parentPCD   the parent PCD
	 * @param value       the value
	 * @param idxStr      the idx str
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
		childPOD.indexedObject = value;
		childPOD.id = idxStr;
		if (hasChildren) {
			childPOD.children = new HashMap<>();
		}
		return childPOD;
	}
}
