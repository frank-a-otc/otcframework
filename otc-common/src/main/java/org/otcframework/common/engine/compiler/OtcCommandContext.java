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
package org.otcframework.common.engine.compiler;

import org.otcframework.common.OtcConstants;
import org.otcframework.common.dto.OtcCommandDto;
import org.otcframework.common.dto.ScriptDto;
import org.otcframework.common.engine.indexer.dto.IndexedCollectionsDto;

// TODO: Auto-generated Javadoc
/**
 * The Class OtcCommandContext.
 */
public class OtcCommandContext { 
	
	/** The script id. */
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
	 * Clone.
	 *
	 * @return the otc command context
	 */
	@Override
	public OtcCommandContext clone() {
		OtcCommandContext otcCommandContext = new OtcCommandContext();
		otcCommandContext.otcChain = otcChain;
		otcCommandContext.rawOtcTokens = rawOtcTokens;
		otcCommandContext.otcTokens = otcTokens;
		otcCommandContext.otcCommandDto = otcCommandDto;
		otcCommandContext.indexedCollectionsDto = indexedCollectionsDto;		
		return otcCommandContext;
	}

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
			if (childOCD.collectionDescriptor.isNormal()) {
				return true;
			}
			return false;
		} else if (otcCommandDto.otcTokenIndex == otcTokens.length - 1) {
			if (otcCommandDto.collectionDescriptor.isCollection() || otcCommandDto.collectionDescriptor.isMap()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if is leaf.
	 *
	 * @return true, if is leaf
	 */
	public boolean isLeaf() {
		if (otcCommandDto.otcTokenIndex >= otcTokens.length - 1) {
			if (otcCommandDto.collectionDescriptor.isNormal() || otcCommandDto.collectionDescriptor.isMapKey() || 
					otcCommandDto.collectionDescriptor.isMapValue()
					|| otcCommandDto.collectionDescriptor.isCollectionMember()) {
				return true;
			}
		}
		return false;
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
		if (rawOtcTokens[otcCommandDto.otcTokenIndex].contains(OtcConstants.MAP_VALUE_REF)) {
			return true;
		}
		return false;
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
	 * Checks if is anchored.
	 *
	 * @return true, if is anchored
	 */
	public boolean isCurrentTokenAnchored() {
		String otcToken = rawOtcTokens[otcCommandDto.otcTokenIndex];
		if (otcToken.contains(OtcConstants.ANCHOR)) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if is pre anchored.
	 *
	 * @return true, if is pre anchored
	 */
	public boolean isPreAnchored() {
		String otcToken = rawOtcTokens[otcCommandDto.otcTokenIndex];
		if (otcToken.contains(OtcConstants.PRE_ANCHOR) || otcToken.contains(OtcConstants.MAP_PRE_ANCHOR)) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if is post anchored.
	 *
	 * @return true, if is post anchored
	 */
	public boolean isPostAnchored() {
		String otcToken = rawOtcTokens[otcCommandDto.otcTokenIndex];
		if (otcToken.contains(OtcConstants.POST_ANCHOR) || otcToken.contains(OtcConstants.MAP_POST_ANCHOR)) {
			return true;
		}
		return false;
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
