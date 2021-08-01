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
package org.otcframework.core.engine;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.otcframework.common.OtcConstants;
import org.otcframework.common.OtcConstants.TARGET_SOURCE;
import org.otcframework.common.dto.DeploymentDto;
import org.otcframework.common.dto.DeploymentDto.CompiledInfo;
import org.otcframework.common.dto.OtcChainDto;
import org.otcframework.common.dto.OtcCommandDto;
import org.otcframework.common.engine.compiler.OtcCommandContext;
import org.otcframework.common.engine.indexer.IndexedCollectionsDtoFactory;
import org.otcframework.common.engine.indexer.dto.IndexedCollectionsDto;
import org.otcframework.common.util.CommonUtils;
import org.otcframework.common.util.OtcUtils;
import org.otcframework.core.engine.utils.OtcReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class ObjectIndexerImpl.
 */
final class ObjectIndexerImpl implements ObjectIndexer {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(ObjectIndexerImpl.class);

	/** The object indexer impl. */
	private static ObjectIndexerImpl objectIndexerImpl = new ObjectIndexerImpl();;

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

	@Override
	public IndexedCollectionsDto indexObject(DeploymentDto deploymentDto, TARGET_SOURCE enumTargetSource, 
			Object indexedObject) {
		LOGGER.trace("Initiating object-indexing for instance of {}", indexedObject.getClass().getName());

		long startTime = System.nanoTime();
		Set<String> loadedOtcTokens = null;
		IndexedCollectionsDto rootICD = null;
		Map<String, IndexedCollectionsDto> mapICDs = null;
		OtcCommandContext otcCommandContext = new OtcCommandContext();
		for (Entry<String, CompiledInfo> entry : deploymentDto.compiledInfos.entrySet()) {
			CompiledInfo compiledInfo = entry.getValue();
			OtcChainDto otcChainDto = null;
			OtcCommandDto otcCommandDto = null;
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
			int collectionsCount = otcChainDto.collectionCount + otcChainDto.dictionaryCount;
			if (collectionsCount == 0) {
				continue;
			}
			String[] otcTokens = otcChainDto.otcTokens;
			Object value = OtcReflectionUtil.readFieldValue(otcCommandDto.field, indexedObject); 
			if (value == null) {
				continue;
			}
			String otcChain = otcChainDto.otcChain;
			if (loadedOtcTokens != null && loadedOtcTokens.contains(otcChain)) {
				continue;
			}
			String indexId = otcChain.substring(0, otcChain.indexOf("]") + 1);
			if (TARGET_SOURCE.TARGET == otcCommandDto.enumTargetSource) {
				indexId = OtcUtils.sanitizeOtc(indexId);
			}
			IndexedCollectionsDto parentICD = null;
			if (mapICDs != null) {
				parentICD = mapICDs.get(indexId);
			}
			otcCommandContext.otcTokens = otcTokens;
			otcCommandContext.rawOtcTokens = otcChainDto.rawOtcTokens;
			otcCommandContext.otcCommandDto = otcCommandDto;
			parentICD = indexObject(otcCommandContext, value, parentICD);
			if (parentICD != null) {
				if (loadedOtcTokens == null) {
					loadedOtcTokens = new HashSet<>();
				}
				loadedOtcTokens.add(otcChain);
				if (rootICD == null) {
					rootICD = IndexedCollectionsDtoFactory.createRoot(otcCommandDto, true, value, null);
					mapICDs = rootICD.children; 
				}
				if (!mapICDs.containsKey(indexId)) {
					mapICDs.put(indexId, parentICD);
				}
			}
		}
		LOGGER.debug("Completed object-indexing for instance of '{}' in millis.", indexedObject.getClass().getName(),
					((System.nanoTime() - startTime) / 1000000.0));
		return rootICD;
	}

	private static IndexedCollectionsDto indexObject(OtcCommandContext otcCommandContext, Object indexedObject,
			IndexedCollectionsDto parentICD) {
		String[] otcTokens = otcCommandContext.otcTokens;
		OtcCommandDto otcCommandDto = otcCommandContext.otcCommandDto; 
		Object value = null;
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
		if (otcCommandDto.isCollection()) {				
			parentICD = indexCollection(otcCommandContext, indexedObject, parentICD);
		} else if (otcCommandDto.isMap()) {
			parentICD = indexMap(otcCommandContext, indexedObject, parentICD);
		}
		return parentICD;
	}
	
	private static IndexedCollectionsDto indexCollection(OtcCommandContext otcCommandContext, Object indexedObject, 
			IndexedCollectionsDto parentICD) {
		OtcCommandDto otcCommandDto = otcCommandContext.otcCommandDto; 
		IndexedCollectionsDto memberICD = null;
		int size = 0;
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
			return null;
		}
		OtcCommandDto memberOCD = otcCommandDto.children.get(otcCommandDto.fieldName);
		if (parentICD == null) {
			String indexId = null;
			if (otcCommandContext.hasAncestralCollectionOrMap()) {
				indexId = otcCommandDto.otcToken;
			} else {
				indexId = otcCommandDto.tokenPath.substring(0, otcCommandDto.tokenPath.indexOf("]") + 1);
			}
			parentICD = IndexedCollectionsDtoFactory.createRoot(otcCommandDto, true, indexedObject, indexId);
		}
		otcCommandContext.otcCommandDto = memberOCD;
		for (int idx = 0; idx < size; idx++) {  
			Object member = null;
			memberICD = parentICD.children.get("" + idx);
			if (memberICD == null) {
				if (objArr != null) {
					member = objArr[idx];
				} else {
					member = iter.next();
				}
				memberICD = IndexedCollectionsDtoFactory.create(otcCommandContext, parentICD, member, "" + idx);
			} else {
				member = memberICD.indexeddObject;
			}
			if (otcCommandContext.hasDescendantCollectionOrMap()) {
				IndexedCollectionsDto nextICD = indexObject(otcCommandContext.clone(), member, null);
				if (nextICD != null) {
					memberICD.children.put(nextICD.id, nextICD);
				}
			}
		}
		return parentICD;
	}
	
	@SuppressWarnings("unchecked")
	private static IndexedCollectionsDto indexMap(OtcCommandContext otcCommandContext, Object indexedObject,
			IndexedCollectionsDto parentICD) {
		String[] rawOtcTokens = otcCommandContext.rawOtcTokens; 
		OtcCommandDto otcCommandDto = otcCommandContext.otcCommandDto; 
		Map<?,?> map = (Map<?,?>) indexedObject;
		int size = map.size();
		if (size == 0) {
			return null;
		}
		Set<?> entrySet = map.entrySet();
		Iterator<Entry<?,?>> iter = (Iterator<Entry<?, ?>>) entrySet.iterator();
		String keyOtcToken = OtcConstants.MAP_KEY_REF + otcCommandDto.fieldName;
		String valueOtcToken = OtcConstants.MAP_VALUE_REF + otcCommandDto.fieldName;
		OtcCommandDto keyOCD = otcCommandDto.children.get(keyOtcToken);
		OtcCommandDto valueOCD = otcCommandDto.children.get(valueOtcToken);
		OtcCommandDto memberOCD = null;
		int otcTokenIndex = otcCommandDto.otcTokenIndex;
		String rawOtcToken = rawOtcTokens[otcTokenIndex];
		if (rawOtcToken.contains(OtcConstants.MAP_KEY_REF)) {
			memberOCD = keyOCD;
		} else {
			memberOCD = valueOCD;
		}
		if (parentICD == null) {
			String indexId = null;
			if (otcCommandContext.hasAncestralCollectionOrMap()) {
				indexId = otcCommandDto.otcToken;
			} else {
				indexId = otcCommandDto.tokenPath.substring(0, otcCommandDto.tokenPath.indexOf("]") + 1);
			}
			parentICD = IndexedCollectionsDtoFactory.createRoot(otcCommandDto, true, indexedObject, indexId);
		}
		for (int idx = 0; idx < size; idx++) {
			IndexedCollectionsDto keyICD = null;
			IndexedCollectionsDto valueICD = null;
			if (parentICD.children.size() > 0) {
				keyICD = parentICD.children.get(idx + OtcConstants.MAP_KEY_REF);
				valueICD = parentICD.children.get(idx + OtcConstants.MAP_VALUE_REF);
			}
			if (keyICD == null) {
				Entry<?,?> entry = iter.next();
				otcCommandContext.otcCommandDto = keyOCD;
				keyICD = IndexedCollectionsDtoFactory.create(otcCommandContext.clone(), parentICD, entry.getKey(),
						idx + OtcConstants.MAP_KEY_REF);
				otcCommandContext.otcCommandDto = valueOCD;
				valueICD = IndexedCollectionsDtoFactory.create(otcCommandContext.clone(), parentICD, entry.getValue(), 
						idx + OtcConstants.MAP_VALUE_REF);
			}
			IndexedCollectionsDto memberICD = null;
			if (memberOCD.isMapKey()) {
				memberICD = keyICD;
				otcCommandContext.otcCommandDto = keyOCD;
			} else {
				memberICD = valueICD;
				otcCommandContext.otcCommandDto = valueOCD;
			}
			if (otcCommandContext.hasDescendantCollectionOrMap()) {
				Object member = memberICD.indexeddObject;
				IndexedCollectionsDto nextICD = indexObject(otcCommandContext, member, null);
				if (nextICD != null) {
					memberICD.children.put(nextICD.id, nextICD);
				}
			}
		}
		return parentICD;
	}
}
