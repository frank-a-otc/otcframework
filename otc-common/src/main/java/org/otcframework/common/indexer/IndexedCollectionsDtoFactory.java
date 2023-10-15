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

import org.otcframework.common.engine.indexer.dto.IndexedCollectionsDto;

import java.util.HashMap;

/**
 * A factory for creating IndexedCollectionsDto objects.
 */
public class IndexedCollectionsDtoFactory {

	private IndexedCollectionsDtoFactory() {}

	/**
	 *
	 * @param parentICD
	 * @param value
	 * @param idxStr
	 * @param hasChildren
	 * @return IndexedCollectionsDto
	 */
	public static IndexedCollectionsDto create(IndexedCollectionsDto parentICD, Object value, String idxStr, boolean hasChildren) {
		IndexedCollectionsDto childICD = new IndexedCollectionsDto();
		if (parentICD != null && idxStr != null) {
			parentICD.children.put(idxStr, childICD);
		}
		childICD.id = idxStr;
		childICD.indexedObject = value;
		if (hasChildren && childICD.children == null) {
			childICD.children = new HashMap<>();
		}
		return childICD;
	}
}
