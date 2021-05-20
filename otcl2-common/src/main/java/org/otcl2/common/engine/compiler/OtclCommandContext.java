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
package org.otcl2.common.engine.compiler;

import org.otcl2.common.OtclConstants;
import org.otcl2.common.dto.OtclCommandDto;
import org.otcl2.common.dto.ScriptDto;
import org.otcl2.common.engine.profiler.dto.IndexedCollectionsDto;

// TODO: Auto-generated Javadoc
/**
 * The Class OtclCommandContext.
 */
public class OtclCommandContext { 
	
	/** The script id. */
	public String commandId;
	
	/** The script dto. */
	public ScriptDto scriptDto;
	
	/** The otcl chain. */
	public String otclChain;
	
	/** The otcl tokens. */
	public String[] otclTokens;
	
	/** The raw otcl tokens. */
	public String[] rawOtclTokens;
	
	/** The otcl command dto. */
	public OtclCommandDto otclCommandDto;
	
	/** The profiled collections dto. */
	public IndexedCollectionsDto profiledCollectionsDto;
		
	/** The collections count. */
	public int collectionsCount = 0;

	/** The current collection token index. */
	public int currentCollectionTokenIndex = 0;
	
	/**
	 * Clone.
	 *
	 * @return the otcl command context
	 */
	@Override
	public OtclCommandContext clone() {
		OtclCommandContext otclCommandContext = new OtclCommandContext();
		otclCommandContext.otclChain = otclChain;
		otclCommandContext.rawOtclTokens = rawOtclTokens;
		otclCommandContext.otclTokens = otclTokens;
		otclCommandContext.otclCommandDto = otclCommandDto;
		otclCommandContext.profiledCollectionsDto = profiledCollectionsDto;		
		return otclCommandContext;
	}

	/**
	 * Checks if is leaf parent.
	 *
	 * @return true, if is leaf parent
	 */
	public boolean isLeafParent() {
		if (otclCommandDto.otclTokenIndex == otclTokens.length - 2) {
			if (otclCommandDto.collectionDescriptor.isCollection() || otclCommandDto.collectionDescriptor.isMap()) {
				return false;
			}
			String otclToken = otclTokens[otclCommandDto.otclTokenIndex + 1];
			OtclCommandDto childOCD = otclCommandDto.children.get(otclToken);
			if (childOCD.collectionDescriptor.isNormal()) {
				return true;
			}
			return false;
		} else if (otclCommandDto.otclTokenIndex == otclTokens.length - 1) {
			if (otclCommandDto.collectionDescriptor.isCollection() || otclCommandDto.collectionDescriptor.isMap()) {
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
		if (otclCommandDto.otclTokenIndex >= otclTokens.length - 1) {
			if (otclCommandDto.collectionDescriptor.isNormal() || otclCommandDto.collectionDescriptor.isMapKey() || 
					otclCommandDto.collectionDescriptor.isMapValue()
					|| otclCommandDto.collectionDescriptor.isCollectionMember()) {
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
		if (otclTokens.length == 1 || otclCommandDto.isFirstNode) {
			return false;
		}
		int startIdx = otclCommandDto.otclTokenIndex - 1;
		for (int idx = startIdx; idx >= 0; idx--) {
			String otclToken = otclTokens[idx];
			if (otclToken.contains(OtclConstants.OPEN_BRACKET)) {
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
		if (otclTokens.length == 1) {
			return false;
		}
		int startIdx = otclCommandDto.otclTokenIndex + 1;
		for (int idx = startIdx; idx < otclTokens.length; idx++) {
			String otclToken = otclTokens[idx];
			if (otclToken.contains(OtclConstants.OPEN_BRACKET)) {
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
		if (otclCommandDto.isCollectionOrMap()) {
			return true;
		}
		return (otclTokens.length > otclCommandDto.otclTokenIndex + 1);
	}
	
	/**
	 * Checks for map value descendant.
	 *
	 * @return true, if successful
	 */
	public boolean hasMapValueDescendant() {
		if (rawOtclTokens.length == 1) {
			return false;
		}
		int startIdx = otclCommandDto.otclTokenIndex + 1;
		for (int idx = startIdx; idx < rawOtclTokens.length; idx++) {
			String otclToken = rawOtclTokens[idx];
			if (otclToken.contains(OtclConstants.MAP_VALUE_REF)) {
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
		if (rawOtclTokens[otclCommandDto.otclTokenIndex].contains(OtclConstants.MAP_VALUE_REF)) {
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
		if (rawOtclTokens.length == 1) {
			return false;
		}
		int startIdx = otclCommandDto.otclTokenIndex + 1;
		for (int idx = startIdx; idx < rawOtclTokens.length; idx++) {
			String otclToken = rawOtclTokens[idx];
			if (otclToken.contains(OtclConstants.ANCHOR)) {
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
		String otclToken = rawOtclTokens[otclCommandDto.otclTokenIndex];
		if (otclToken.contains(OtclConstants.ANCHOR)) {
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
		String otclToken = rawOtclTokens[otclCommandDto.otclTokenIndex];
		if (otclToken.contains(OtclConstants.PRE_ANCHOR) || otclToken.contains(OtclConstants.MAP_PRE_ANCHOR)) {
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
		String otclToken = rawOtclTokens[otclCommandDto.otclTokenIndex];
		if (otclToken.contains(OtclConstants.POST_ANCHOR) || otclToken.contains(OtclConstants.MAP_POST_ANCHOR)) {
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
		int startIdx = otclCommandDto.otclTokenIndex;
		for (int idx = startIdx; idx < otclTokens.length; idx++) {
			String otclToken = otclTokens[idx];
			if (otclToken.contains(OtclConstants.OPEN_BRACKET)) {
				descendantsCollectionsCount++;
			}
		}
		return descendantsCollectionsCount;
	}

}
