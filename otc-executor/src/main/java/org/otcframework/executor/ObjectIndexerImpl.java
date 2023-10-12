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
package org.otcframework.executor;

import org.otcframework.common.OtcConstants;
import org.otcframework.common.OtcConstants.TARGET_SOURCE;
import org.otcframework.common.compiler.OtcCommandContext;
import org.otcframework.common.dto.OtcChainDto;
import org.otcframework.common.dto.OtcCommandDto;
import org.otcframework.common.dto.RegistryDto;
import org.otcframework.common.dto.RegistryDto.CompiledInfo;
import org.otcframework.common.engine.indexer.dto.IndexedCollectionsDto;
import org.otcframework.common.indexer.IndexedCollectionsDtoFactory;
import org.otcframework.common.util.CommonUtils;
import org.otcframework.common.util.OtcReflectionUtil;
import org.otcframework.common.util.OtcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.Map.Entry;

/**
 * The Class ObjectIndexerImpl.
 */
final class ObjectIndexerImpl implements ObjectIndexer {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(ObjectIndexerImpl.class);

	/** The object indexer impl. */
	private static final ObjectIndexerImpl objectIndexerImpl = new ObjectIndexerImpl();

	/**
	 * Instantiates a new object indexer impl.
	 */
	private ObjectIndexerImpl() {
	}

	/**
	 * Gets the single instance of ObjectIndexerImpl.
	 *
	 * @return single instance of ObjectIndexerImpl
	 */
	public static ObjectIndexerImpl getInstance() {
		return objectIndexerImpl;
	}

	/**
	 * Index object.
	 *
	 * @param registryDto    the registry dto
	 * @param enumTargetSource the enum target source
	 * @param indexedObject    the indexed object
	 * @return the indexed collections dto
	 */

	@Override
	public IndexedCollectionsDto indexObject(RegistryDto registryDto, TARGET_SOURCE enumTargetSource,
											 Object indexedObject) {
		LOGGER.trace("Initiating object-indexing for instance of {}", indexedObject.getClass().getName());
		long startTime = System.nanoTime();
		Set<String> loadedOtcTokens = null;
		IndexedCollectionsDto rootICD = null;
		OtcCommandContext otcCommandContext = new OtcCommandContext();
		for (Entry<String, CompiledInfo> entry : registryDto.compiledInfos.entrySet()) {
			CompiledInfo compiledInfo = entry.getValue();
			OtcChainDto otcChainDto;
			OtcCommandDto otcCommandDto;
			if (TARGET_SOURCE.SOURCE == enumTargetSource) {
				otcChainDto = compiledInfo.sourceOtcChainDto;
				otcCommandDto = compiledInfo.sourceOCDStem;
			} else {
				otcChainDto = compiledInfo.targetOtcChainDto;
				otcCommandDto = compiledInfo.targetOCDStem;
			}
			if (otcChainDto == null || CommonUtils.isEmpty(otcChainDto.otcChain)) {
				continue;
			}
			String otcChain = otcChainDto.otcChain;
			if (loadedOtcTokens != null && loadedOtcTokens.contains(otcChain)) {
				continue;
			}
			int collectionsCount = otcChainDto.collectionCount + otcChainDto.dictionaryCount;
			if (collectionsCount == 0) {
				continue;
			}
			String[] otcTokens = otcChainDto.otcTokens;
			Object value = OtcReflectionUtil.readFieldValue(otcCommandDto.field, indexedObject);
			if (value == null) {
				continue;
			}
			String key = otcChain.substring(0, otcChain.indexOf("]") + 1);
			if (TARGET_SOURCE.TARGET == otcCommandDto.enumTargetSource) {
				key = OtcUtils.sanitizeOtc(key);
			}
			otcCommandContext.otcTokens = otcTokens;
			otcCommandContext.rawOtcTokens = otcChainDto.rawOtcTokens;
			otcCommandContext.otcCommandDto = otcCommandDto;
			IndexedCollectionsDto parentICD = indexObject(otcCommandContext, value, rootICD);
			if (parentICD != null) {
				if (loadedOtcTokens == null) {
					loadedOtcTokens = new HashSet<>();
				}
				loadedOtcTokens.add(otcChain);
				if (rootICD == null) {
					rootICD = IndexedCollectionsDtoFactory.create(null, value, null, true);
				}
				if (!rootICD.children.containsKey(key)) {
					rootICD.children.put(key, parentICD);
				}
			}
		}
		LOGGER.debug("Completed object-indexing for instance of '{}' in {} millis.", indexedObject.getClass().getName(),
				((System.nanoTime() - startTime) / 1000000.0));
		return rootICD;
	}

	/**
	 * Index object.
	 *
	 * @param otcCommandContext the otc command context
	 * @param indexedObject     the indexed object
	 * @param parentICD         the parent ICD
	 * @return the indexed collections dto
	 */
	private static IndexedCollectionsDto indexObject(OtcCommandContext otcCommandContext, Object indexedObject,
													 IndexedCollectionsDto parentICD) {
		String[] otcTokens = otcCommandContext.otcTokens;
		OtcCommandDto otcCommandDto = otcCommandContext.otcCommandDto;
		Object value;
		while (!otcCommandDto.isCollectionOrMap()) {
			int idx = otcCommandDto.otcTokenIndex + 1;
			String otcToken = otcTokens[idx];
			otcCommandDto = otcCommandDto.children.get(otcToken);
			value = OtcReflectionUtil.readFieldValue(otcCommandDto.field, indexedObject);
			if (value == null) {
				return null;
			}
			indexedObject = value;
			otcCommandContext.otcCommandDto = otcCommandDto;
		}
		if (indexedObject instanceof Collection) {
			if (((Collection) indexedObject).isEmpty()) {
				return null;
			}
		} else if (indexedObject instanceof Map) {
			if (((Map) indexedObject).isEmpty()) {
				return null;
			}
		}
		String key;
		if (otcCommandContext.hasAncestralCollectionOrMap()) {
			key = otcCommandDto.otcToken;
		} else {
			key = otcCommandDto.tokenPath.substring(0, otcCommandDto.tokenPath.indexOf("]") + 1);
		}
		IndexedCollectionsDto nextParentICD = null;
		if (parentICD != null) {
			nextParentICD = parentICD.children.get(key);
		}
		if (nextParentICD == null) {
			nextParentICD = IndexedCollectionsDtoFactory.create(parentICD, indexedObject, key, true);
		}
		if (otcCommandDto.isCollection()) {
			indexTheCollection(otcCommandContext, indexedObject, nextParentICD);
		} else if (otcCommandDto.isMap()) {
			indexTheMap(otcCommandContext, indexedObject, nextParentICD);
		}
		return nextParentICD;
	}

	/**
	 * Index collection.
	 *
	 * @param otcCommandContext the otc command context
	 * @param indexedObject     the indexed object
	 * @param parentICD         the parent ICD
	 */
	private static void indexTheCollection(OtcCommandContext otcCommandContext, Object indexedObject,
										   IndexedCollectionsDto parentICD) {
		OtcCommandDto otcCommandDto = otcCommandContext.otcCommandDto;
		int size;
		Object[] objArr = null;
		Iterator<?> iter = null;
		if (otcCommandDto.isArray()) {
			objArr = ((Object[]) indexedObject);
			size = objArr.length;
		} else {
			Collection<?> collection = ((Collection<?>) indexedObject);
			iter = collection.iterator();
			size = collection.size();
		}
		if (size == 0) {
			return;
		}
		OtcCommandDto memberOCD = otcCommandDto.children.get(otcCommandDto.fieldName);
		otcCommandContext.otcCommandDto = memberOCD;
		for (int idx = 0; idx < size; idx++) {
			Object member;
			String key = Integer.toString(idx);
			IndexedCollectionsDto memberICD = parentICD.children.get(key);
			if (memberICD == null) {
				if (objArr != null) {
					member = objArr[idx];
				} else {
					member = iter.next();
				}
				memberICD = IndexedCollectionsDtoFactory.create(parentICD, member, key, true);
			}
			if (otcCommandContext.hasDescendantCollectionOrMap()) {
				indexObject(otcCommandContext.clone(), memberICD.indexedObject, memberICD);
			}
		}
	}

	/**
	 * Index map.
	 *
	 * @param otcCommandContext the otc command context
	 * @param indexedObject     the indexed object
	 * @param parentICD         the parent ICD
	 */
	@SuppressWarnings("unchecked")
	private static void indexTheMap(OtcCommandContext otcCommandContext, Object indexedObject,
									IndexedCollectionsDto parentICD) {
		Map<?, ?> map = (Map<?, ?>) indexedObject;
		int size = map.size();
		if (size == 0) {
			return;
		}
		Set<?> entrySet = map.entrySet();
		Iterator<Entry<?, ?>> iter = (Iterator<Entry<?, ?>>) entrySet.iterator();
		OtcCommandDto otcCommandDto = otcCommandContext.otcCommandDto;
		int otcTokenIndex = otcCommandDto.otcTokenIndex;
		String rawOtcToken = otcCommandContext.rawOtcTokens[otcTokenIndex];
		OtcCommandDto memberOCD;
		if (rawOtcToken.contains(OtcConstants.MAP_KEY_REF)) {
			memberOCD = otcCommandDto.children.get(OtcConstants.MAP_KEY_REF + otcCommandDto.fieldName);
		} else {
			memberOCD = otcCommandDto.children.get(OtcConstants.MAP_VALUE_REF + otcCommandDto.fieldName);
		}
		otcCommandContext.otcCommandDto = memberOCD;
		for (int idx = 0; idx < size; idx++) {
			String key;
			if (memberOCD.isMapKey()) {
				key = idx + OtcConstants.MAP_KEY_REF;
			} else {
				key = idx + OtcConstants.MAP_VALUE_REF;
			}
			IndexedCollectionsDto memberICD = parentICD.children.get(key);
			if (memberICD == null) {
				Entry<?, ?> entry = iter.next();
				Object member;
				if (memberOCD.isMapKey()) {
					member = entry.getKey();
				} else {
					member = entry.getValue();
				}
				memberICD = IndexedCollectionsDtoFactory.create(parentICD, member, key, true);
			}
			if (otcCommandContext.hasDescendantCollectionOrMap()) {
				indexObject(otcCommandContext.clone(), memberICD.indexedObject, memberICD);
			}
		}
	}
}