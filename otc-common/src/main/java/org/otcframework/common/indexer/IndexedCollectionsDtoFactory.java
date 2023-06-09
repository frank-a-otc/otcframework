/**
* Copyright (c) otcframework.org
*
* @author  Franklin J Abel
* @version 1.0
* @since   2020-06-08 
*
* This file is part of the OTC framework.
* 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      https://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*/
package org.otcframework.common.indexer;

import org.otcframework.common.compiler.OtcCommandContext;
import org.otcframework.common.dto.OtcCommandDto;
import org.otcframework.common.engine.indexer.dto.IndexedCollectionsDto;

import java.util.HashMap;

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
