/**
* Copyright (c) otclfoundation.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*
* This file is part of the OTCL framework.
* 
*  The OTCL framework is free software: you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  the Free Software Foundation, version 3 of the License.
*
*  The OTCL framework is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU General Public License for more details.
*
*  A copy of the GNU General Public License is made available as 'License.md' file, 
*  along with OTCL framework project.  If not, see <https://www.gnu.org/licenses/>.
*
*/
package org.otcl2.core.engine;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.otcl2.common.OtclConstants;
import org.otcl2.common.OtclConstants.TARGET_SOURCE;
import org.otcl2.common.dto.DeploymentDto;
import org.otcl2.common.dto.DeploymentDto.CompiledInfo;
import org.otcl2.common.dto.OtclChainDto;
import org.otcl2.common.dto.OtclCommandDto;
import org.otcl2.common.engine.compiler.OtclCommandContext;
import org.otcl2.common.engine.profiler.IndexedCollectionsDtoFactory;
import org.otcl2.common.engine.profiler.dto.IndexedCollectionsDto;
import org.otcl2.common.util.CommonUtils;
import org.otcl2.common.util.OtclUtils;
import org.otcl2.core.engine.utils.OtclReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class ObjectProfilerImpl.
 */
final class ObjectProfilerImpl implements ObjectProfiler {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(ObjectProfilerImpl.class);

	/** The object profiler impl. */
	private static ObjectProfilerImpl objectProfilerImpl = new ObjectProfilerImpl();;

	/**
	 * Instantiates a new object profiler impl.
	 */
	private ObjectProfilerImpl() {
	}

	/**
	 * Gets the single instance of ObjectProfilerImpl.
	 *
	 * @return single instance of ObjectProfilerImpl
	 */
	public static ObjectProfilerImpl getInstance() {
		return objectProfilerImpl;
	}

	/**
	 * Profile object.
	 *
	 * @param deploymentDto the deployment dto
	 * @param enumTargetSource the enum target source
	 * @param profiledObject the profiled object
	 * @return the indexed collections dto
	 */
	@Override
	public IndexedCollectionsDto profileObject(DeploymentDto deploymentDto, TARGET_SOURCE enumTargetSource, 
			Object profiledObject) {
		LOGGER.trace("Initiating object-profiling for instance of " + profiledObject.getClass().getName());

		long startTime = System.nanoTime();
		Set<String> loadedOtclTokens = null;
		IndexedCollectionsDto rootICD = null;
		Map<String, IndexedCollectionsDto> mapICDs = null;
		OtclCommandContext otclCommandContext = new OtclCommandContext();
		for (Entry<String, CompiledInfo> entry : deploymentDto.compiledInfos.entrySet()) {
			CompiledInfo compiledInfo = entry.getValue();
			OtclChainDto otclChainDto = null;
			OtclCommandDto otclCommandDto = null;
			if (TARGET_SOURCE.SOURCE == enumTargetSource) {
				otclChainDto = compiledInfo.sourceOtclChainDto;
				otclCommandDto = compiledInfo.sourceOCDStem;
			} else {
				otclChainDto = compiledInfo.targetOtclChainDto;
				otclCommandDto = compiledInfo.targetOCDStem;
			}
			if (otclChainDto == null || CommonUtils.isEmpty(otclChainDto.otclChain)) {
				continue;
			}
			int collectionsCount = otclChainDto.collectionCount + otclChainDto.dictionaryCount;
			if (collectionsCount == 0) {
				continue;
			}
			String[] otclTokens = otclChainDto.otclTokens;
			Object value = OtclReflectionUtil.readFieldValue(otclCommandDto.field, profiledObject); 
			if (value == null) {
				continue;
			}
			String otclChain = otclChainDto.otclChain;
			if (loadedOtclTokens != null && loadedOtclTokens.contains(otclChain)) {
				continue;
			}
			String profileId = otclChain.substring(0, otclChain.indexOf("]") + 1);
			if (TARGET_SOURCE.TARGET == otclCommandDto.enumTargetSource) {
				profileId = OtclUtils.sanitizeOtcl(profileId);
			}
			IndexedCollectionsDto parentICD = null;
			if (mapICDs != null) {
				parentICD = mapICDs.get(profileId);
			}
			otclCommandContext.otclTokens = otclTokens;
			otclCommandContext.rawOtclTokens = otclChainDto.rawOtclTokens;
			otclCommandContext.otclCommandDto = otclCommandDto;
			parentICD = profileObject(otclCommandContext, value, parentICD);
			if (parentICD != null) {
				if (loadedOtclTokens == null) {
					loadedOtclTokens = new HashSet<>();
				}
				loadedOtclTokens.add(otclChain);
				if (rootICD == null) {
					rootICD = IndexedCollectionsDtoFactory.createRoot(otclCommandDto, true, value, null);
					mapICDs = rootICD.children; 
				}
				if (!mapICDs.containsKey(profileId)) {
					mapICDs.put(profileId, parentICD);
				}
			}
		}
		LOGGER.trace("Completed object-profiling for instance of '" + profiledObject.getClass().getName() + "' in "
					+ ((System.nanoTime() - startTime) / 1000000.0)  + " millis.");
		return rootICD;
	}

	/**
	 * Profile object.
	 *
	 * @param otclCommandContext the otcl command context
	 * @param profiledObject the profiled object
	 * @param parentICD the parent ICD
	 * @return the indexed collections dto
	 */
	private static IndexedCollectionsDto profileObject(OtclCommandContext otclCommandContext, Object profiledObject,
			IndexedCollectionsDto parentICD) {
		String[] otclTokens = otclCommandContext.otclTokens;
		OtclCommandDto otclCommandDto = otclCommandContext.otclCommandDto; 
		Object value = null;
		while (!otclCommandDto.isCollectionOrMap()) {
			int idx = otclCommandDto.otclTokenIndex + 1;
			String otclToken = otclTokens[idx];
			otclCommandDto = otclCommandDto.children.get(otclToken);
			value = OtclReflectionUtil.readFieldValue(otclCommandDto.field, profiledObject);
			if (value == null) {
				return null;
			}
			profiledObject = value;
			otclCommandContext.otclCommandDto = otclCommandDto;
		}
		if (otclCommandDto.isCollection()) {				
			parentICD = profileCollection(otclCommandContext, profiledObject, parentICD);
		} else if (otclCommandDto.isMap()) {
			parentICD = profileMap(otclCommandContext, profiledObject, parentICD);
		}
		return parentICD;
	}
	
	/**
	 * Profile collection.
	 *
	 * @param otclCommandContext the otcl command context
	 * @param profiledObject the profiled object
	 * @param parentICD the parent ICD
	 * @return the indexed collections dto
	 */
	private static IndexedCollectionsDto profileCollection(OtclCommandContext otclCommandContext, Object profiledObject, 
			IndexedCollectionsDto parentICD) {
		OtclCommandDto otclCommandDto = otclCommandContext.otclCommandDto; 
		IndexedCollectionsDto memberICD = null;
		int size = 0;
		Object[] objArr = null;
		Iterator<?> iter = null;
		if (otclCommandDto.isArray()) {
			objArr = ((Object[]) profiledObject);
			size = objArr.length;
		} else {
			Collection<?> collection = ((Collection<?>) profiledObject);
			iter = collection.iterator();
			size = collection.size();
		}
		if (size == 0) {
			return null;
		}
		OtclCommandDto memberOCD = otclCommandDto.children.get(otclCommandDto.fieldName);
		if (parentICD == null) {
			String profileId = null;
			if (otclCommandContext.hasAncestralCollectionOrMap()) {
				profileId = otclCommandDto.otclToken;
			} else {
				profileId = otclCommandDto.tokenPath.substring(0, otclCommandDto.tokenPath.indexOf("]") + 1);
			}
			parentICD = IndexedCollectionsDtoFactory.createRoot(otclCommandDto, true, profiledObject, profileId);
		}
		otclCommandContext.otclCommandDto = memberOCD;
		for (int idx = 0; idx < size; idx++) {  
			Object member = null;
			memberICD = parentICD.children.get("" + idx);
			if (memberICD == null) {
				if (objArr != null) {
					member = objArr[idx];
				} else {
					member = iter.next();
				}
				memberICD = IndexedCollectionsDtoFactory.create(otclCommandContext, parentICD, member, "" + idx);
			} else {
				member = memberICD.profiledObject;
			}
			if (otclCommandContext.hasDescendantCollectionOrMap()) {
				IndexedCollectionsDto nextICD = profileObject(otclCommandContext.clone(), member, null);
				if (nextICD != null) {
					memberICD.children.put(nextICD.id, nextICD);
				}
			}
		}
		return parentICD;
	}
	
	/**
	 * Profile map.
	 *
	 * @param otclCommandContext the otcl command context
	 * @param profiledObject the profiled object
	 * @param parentICD the parent ICD
	 * @return the indexed collections dto
	 */
	@SuppressWarnings("unchecked")
	private static IndexedCollectionsDto profileMap(OtclCommandContext otclCommandContext, Object profiledObject,
			IndexedCollectionsDto parentICD) {
		String[] rawOtclTokens = otclCommandContext.rawOtclTokens; 
		OtclCommandDto otclCommandDto = otclCommandContext.otclCommandDto; 
		Map<?,?> map = (Map<?,?>) profiledObject;
		int size = map.size();
		if (size == 0) {
			return null;
		}
		Set<?> entrySet = map.entrySet();
		Iterator<Entry<?,?>> iter = (Iterator<Entry<?, ?>>) entrySet.iterator();
		String keyOtclToken = OtclConstants.MAP_KEY_REF + otclCommandDto.fieldName;
		String valueOtclToken = OtclConstants.MAP_VALUE_REF + otclCommandDto.fieldName;
		OtclCommandDto keyOCD = otclCommandDto.children.get(keyOtclToken);
		OtclCommandDto valueOCD = otclCommandDto.children.get(valueOtclToken);
		OtclCommandDto memberOCD = null;
		int otclTokenIndex = otclCommandDto.otclTokenIndex;
		String rawOtclToken = rawOtclTokens[otclTokenIndex];
		if (rawOtclToken.contains(OtclConstants.MAP_KEY_REF)) {
			memberOCD = keyOCD;
		} else {
			memberOCD = valueOCD;
		}
		if (parentICD == null) {
			String profileId = null;
			if (otclCommandContext.hasAncestralCollectionOrMap()) {
				profileId = otclCommandDto.otclToken;
			} else {
				profileId = otclCommandDto.tokenPath.substring(0, otclCommandDto.tokenPath.indexOf("]") + 1);
			}
			parentICD = IndexedCollectionsDtoFactory.createRoot(otclCommandDto, true, profiledObject, profileId);
		}
		for (int idx = 0; idx < size; idx++) {
			IndexedCollectionsDto keyICD = null;
			IndexedCollectionsDto valueICD = null;
			if (parentICD.children.size() > 0) {
				keyICD = parentICD.children.get(idx + OtclConstants.MAP_KEY_REF);
				valueICD = parentICD.children.get(idx + OtclConstants.MAP_VALUE_REF);
			}
			if (keyICD == null) {
				Entry<?,?> entry = iter.next();
				otclCommandContext.otclCommandDto = keyOCD;
				keyICD = IndexedCollectionsDtoFactory.create(otclCommandContext.clone(), parentICD, entry.getKey(),
						idx + OtclConstants.MAP_KEY_REF);
				otclCommandContext.otclCommandDto = valueOCD;
				valueICD = IndexedCollectionsDtoFactory.create(otclCommandContext.clone(), parentICD, entry.getValue(), 
						idx + OtclConstants.MAP_VALUE_REF);
			}
			IndexedCollectionsDto memberICD = null;
			if (memberOCD.isMapKey()) {
				memberICD = keyICD;
				otclCommandContext.otclCommandDto = keyOCD;
			} else {
				memberICD = valueICD;
				otclCommandContext.otclCommandDto = valueOCD;
			}
			if (otclCommandContext.hasDescendantCollectionOrMap()) {
				Object member = memberICD.profiledObject;
				IndexedCollectionsDto nextICD = profileObject(otclCommandContext, member, null);
				if (nextICD != null) {
					memberICD.children.put(nextICD.id, nextICD);
				}
			}
		}
		return parentICD;
	}
}
