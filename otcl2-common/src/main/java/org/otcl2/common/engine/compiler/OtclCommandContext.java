/**
* Copyright (c) otcl2.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*/
package org.otcl2.common.engine.compiler;

import org.otcl2.common.OtclConstants;
import org.otcl2.common.dto.OtclCommandDto;
import org.otcl2.common.dto.ScriptDto;
import org.otcl2.common.engine.profiler.dto.IndexedCollectionsDto;

public class OtclCommandContext { 
	
	public String scriptId;
	public ScriptDto scriptDto;
	public String otclChain;
	public String[] otclTokens;
	public String[] rawOtclTokens;
	public OtclCommandDto otclCommandDto;
	public IndexedCollectionsDto profiledCollectionsDto;
		
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

//	public boolean isKeyPath() {
//		return otclTokens[otclCommandDto.otclTokenIndex].contains(OtclConstants.MAP_KEY_REF);
//	}

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

	public boolean hasAncestralCollectionOrMap() {
		if (otclTokens.length == 1 || otclCommandDto.isRootNode) {
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

	public boolean hasChildren() {
		if (otclCommandDto.isCollectionOrMap()) {
			return true;
		}
		return otclCommandDto.children != null && otclCommandDto.children.size() > 0; // && 
//				otclTokens.length > otclCommandDto.otclTokenIndex + 1;
	}
	
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

	public boolean hasMapValueMember() {
		if (rawOtclTokens[otclCommandDto.otclTokenIndex].contains(OtclConstants.MAP_VALUE_REF)) {
			return true;
		}
		return false;
	}

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

	public boolean isAnchored() {
		String otclToken = rawOtclTokens[otclCommandDto.otclTokenIndex];
		if (otclToken.contains(OtclConstants.ANCHOR)) {
			return true;
		}
		return false;
	}

	public boolean isPreAnchored() {
		String otclToken = rawOtclTokens[otclCommandDto.otclTokenIndex];
		if (otclToken.contains(OtclConstants.PRE_ANCHOR) || otclToken.contains(OtclConstants.MAP_PRE_ANCHOR)) {
			return true;
		}
		return false;
	}

	public boolean isPostAnchored() {
		String otclToken = rawOtclTokens[otclCommandDto.otclTokenIndex];
		if (otclToken.contains(OtclConstants.POST_ANCHOR) || otclToken.contains(OtclConstants.MAP_POST_ANCHOR)) {
			return true;
		}
		return false;
	}

	public int descendantsCollectionsCount() {
		if (rawOtclTokens.length == 1) {
			return 0;
		}
		int descendantsCollectionsCount = 0;
		int startIdx = otclCommandDto.otclTokenIndex + 1;
		for (int idx = startIdx; idx < rawOtclTokens.length; idx++) {
			String otclToken = rawOtclTokens[idx];
			if (otclToken.contains(OtclConstants.OPEN_BRACKET) && !(otclToken.contains(OtclConstants.MAP_BEGIN_REF)
					|| otclToken.contains(OtclConstants.MAP_PRE_ANCHOR))) {
				descendantsCollectionsCount++;
			}
		}
		return descendantsCollectionsCount;
	}

	public int descendantsMapsCount() {
		if (rawOtclTokens.length == 1) {
			return 0;
		}
		int descendantsMapssCount = 0;
		int startIdx = otclCommandDto.otclTokenIndex + 1;
		for (int idx = startIdx; idx < rawOtclTokens.length; idx++) {
			String otclToken = rawOtclTokens[idx];
			if (otclToken.contains(OtclConstants.MAP_BEGIN_REF) || otclToken.contains(OtclConstants.MAP_PRE_ANCHOR)) {
				descendantsMapssCount++;
			}
		}
		return descendantsMapssCount;
	}

}
