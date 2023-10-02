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
package org.otcframework.common.compiler;

import org.otcframework.common.OtcConstants;
import org.otcframework.common.dto.OtcCommandDto;
import org.otcframework.common.dto.ScriptDto;
import org.otcframework.common.engine.indexer.dto.IndexedCollectionsDto;

/**
 * The Class OtcCommandContext.
 */
public class OtcCommandContext {

	/** The command id. */
	public String commandId;

	/** The script dto. */
	public ScriptDto scriptDto;

	/** The otc chain. */
	public String otcChain;

	/** The otc tokens. */
	public String[] otcTokens;

	/** The raw otc tokens. */
	public String[] rawOtcTokens;

	/** The otc command dto. */
	public OtcCommandDto otcCommandDto;

	/** The indexed collections dto. */
	public IndexedCollectionsDto indexedCollectionsDto;

	/** The collections count. */
	public int collectionsCount = 0;

	/** The current collection token index. */
	public int currentCollectionTokenIndex = 0;

	/**
	 * Checks if is leaf parent.
	 *
	 * @return true, if is leaf parent
	 */
	public boolean isLeafParent() {
		if (otcCommandDto.otcTokenIndex == otcTokens.length - 2) {
			if (otcCommandDto.collectionDescriptor.isCollection() || otcCommandDto.collectionDescriptor.isMap()) {
				return false;
			}
			String otcToken = otcTokens[otcCommandDto.otcTokenIndex + 1];
			OtcCommandDto childOCD = otcCommandDto.children.get(otcToken);
			return childOCD.collectionDescriptor.isNormal();
		} else if (otcCommandDto.otcTokenIndex == otcTokens.length - 1 &&
				otcCommandDto.collectionDescriptor.isCollection() || otcCommandDto.collectionDescriptor.isMap()) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if is leaf.
	 *
	 * @return true, if is leaf
	 */
	public boolean isLeaf() {
		return (otcCommandDto.otcTokenIndex >= otcTokens.length - 1 &&
			otcCommandDto.collectionDescriptor.isNormal() || otcCommandDto.collectionDescriptor.isMapKey()
				|| otcCommandDto.collectionDescriptor.isMapValue()
				|| otcCommandDto.collectionDescriptor.isCollectionMember());
	}

	/**
	 * Checks for ancestral collection or map.
	 *
	 * @return true, if successful
	 */
	public boolean hasAncestralCollectionOrMap() {
		if (otcTokens.length == 1 || otcCommandDto.isFirstNode) {
			return false;
		}
		int startIdx = otcCommandDto.otcTokenIndex - 1;
		for (int idx = startIdx; idx >= 0; idx--) {
			String otcToken = otcTokens[idx];
			if (otcToken.contains(OtcConstants.OPEN_BRACKET)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks for descendant collection or map.
	 *
	 * @return true, if successful
	 */
	public boolean hasDescendantCollectionOrMap() {
		if (otcTokens.length == 1) {
			return false;
		}
		int startIdx = otcCommandDto.otcTokenIndex + 1;
		for (int idx = startIdx; idx < otcTokens.length; idx++) {
			String otcToken = otcTokens[idx];
			if (otcToken.contains(OtcConstants.OPEN_BRACKET)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks for children.
	 *
	 * @return true, if successful
	 */
	public boolean hasChildren() {
		if (otcCommandDto.isCollectionOrMap()) {
			return true;
		}
		return (otcTokens.length > otcCommandDto.otcTokenIndex + 1);
	}

	/**
	 * Checks for map value descendant.
	 *
	 * @return true, if successful
	 */
	public boolean hasMapValueDescendant() {
		if (rawOtcTokens.length == 1) {
			return false;
		}
		int startIdx = otcCommandDto.otcTokenIndex + 1;
		for (int idx = startIdx; idx < rawOtcTokens.length; idx++) {
			String otcToken = rawOtcTokens[idx];
			if (otcToken.contains(OtcConstants.MAP_VALUE_REF)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks for map value member.
	 *
	 * @return true, if successful
	 */
	public boolean hasMapValueMember() {
		return rawOtcTokens[otcCommandDto.otcTokenIndex].contains(OtcConstants.MAP_VALUE_REF);
	}

	/**
	 * Checks for anchored descendant.
	 *
	 * @return true, if successful
	 */
	public boolean hasAnchoredDescendant() {
		if (rawOtcTokens.length == 1) {
			return false;
		}
		int startIdx = otcCommandDto.otcTokenIndex + 1;
		for (int idx = startIdx; idx < rawOtcTokens.length; idx++) {
			String otcToken = rawOtcTokens[idx];
			if (otcToken.contains(OtcConstants.ANCHOR)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if is current token anchored.
	 *
	 * @return true, if is current token anchored
	 */
	public boolean isCurrentTokenAnchored() {
		String otcToken = rawOtcTokens[otcCommandDto.otcTokenIndex];
		return otcToken.contains(OtcConstants.ANCHOR);
	}

	/**
	 * Checks if is pre anchored.
	 *
	 * @return true, if is pre anchored
	 */
	public boolean isPreAnchored() {
		String otcToken = rawOtcTokens[otcCommandDto.otcTokenIndex];
		return (otcToken.contains(OtcConstants.PRE_ANCHOR) || otcToken.contains(OtcConstants.MAP_PRE_ANCHOR));
	}

	/**
	 * Checks if is post anchored.
	 *
	 * @return true, if is post anchored
	 */
	public boolean isPostAnchored() {
		String otcToken = rawOtcTokens[otcCommandDto.otcTokenIndex];
		return (otcToken.contains(OtcConstants.POST_ANCHOR) || otcToken.contains(OtcConstants.MAP_POST_ANCHOR));
	}

	/**
	 * Descendants collections count inclusive.
	 *
	 * @return the int
	 */
	public int descendantsCollectionsCountInclusive() {
		int descendantsCollectionsCount = 0;
		int startIdx = otcCommandDto.otcTokenIndex;
		for (int idx = startIdx; idx < otcTokens.length; idx++) {
			String otcToken = otcTokens[idx];
			if (otcToken.contains(OtcConstants.OPEN_BRACKET)) {
				descendantsCollectionsCount++;
			}
		}
		return descendantsCollectionsCount;
	}
}
