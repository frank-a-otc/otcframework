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
package org.otcl2.core.engine.compiler;

import java.util.List;

import org.otcl2.common.OtclConstants;
import org.otcl2.common.OtclConstants.ALGORITHM_ID;
import org.otcl2.common.OtclConstants.LogLevel;
import org.otcl2.common.dto.OtclCommandDto;
import org.otcl2.common.dto.ScriptDto;
import org.otcl2.common.dto.otcl.OtclFileDto.Copy;
import org.otcl2.core.engine.compiler.command.ExecutionContext;
import org.otcl2.core.engine.compiler.command.OtclCommand;
import org.otcl2.core.engine.compiler.command.SourceOtclCommandContext;
import org.otcl2.core.engine.compiler.command.TargetOtclCommandContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class CopyValuesCommandCodeGenerator.
 */
final class CopyValuesCommandCodeGenerator extends AbstractOtclCodeGenerator {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(CopyValuesCommandCodeGenerator.class);

	/**
	 * Generate source code.
	 *
	 * @param executionContext the execution context
	 */
	public static void generateSourceCode(ExecutionContext executionContext) {
		OtclCommand otclCommand = executionContext.otclCommand;
		Class<?> targetClz = executionContext.targetClz;
		TargetOtclCommandContext targetOCC = executionContext.targetOCC;
		Class<?> sourceClz = executionContext.sourceClz; 
		SourceOtclCommandContext sourceOCC = executionContext.sourceOCC;

		ScriptDto scriptDto = executionContext.targetOCC.scriptDto;
		targetOCC.algorithmId = ALGORITHM_ID.COPYVALUES;
		boolean addLogger = false;
		if (targetOCC.otclChain.contains(OtclConstants.MAP_VALUE_REF)) {
			addLogger = true;
		}
		OtclCommandDto targetOCD = targetOCC.otclCommandDto;
		TargetOtclCommandContext clonedTargetOCC = null;

		int offsetIdx = 0;
		int scriptGroupIdx = 0;	

		if (scriptDto.command.debug) { 
			@SuppressWarnings("unused")
			int dummy = 0;
		}
		List<String> values = ((Copy) scriptDto.command).from.values;
		if (values == null) {
			LOGGER.warn("'values:' property in OTCL-command : {} is empty! Skiping Code-generation.", scriptDto.command.id);
			return;
		}
		otclCommand.clearCache();
		otclCommand.appendBeginClass(targetOCC, sourceOCC, targetClz, sourceClz, addLogger);
		clonedTargetOCC = targetOCC.clone();
		if (!clonedTargetOCC.isLeaf()) { 
			otclCommand.appendInitUptoAnchoredOrLastCollectionOrLeaf(clonedTargetOCC, 0, false, LogLevel.WARN);
			if (clonedTargetOCC.otclCommandDto.isCollectionOrMap()) {
				targetOCD = OtclCommand.retrieveMemberOCD(clonedTargetOCC);
				clonedTargetOCC.otclCommandDto = targetOCD;
			}
		} else if (!targetOCD.isFirstNode) {
			otclCommand.appendGetter(clonedTargetOCC, targetOCD, false);
		}
		offsetIdx = processRemainingPath(clonedTargetOCC, otclCommand, scriptDto, scriptGroupIdx, offsetIdx);
		targetOCD = targetOCC.otclCommandDto;
		scriptGroupIdx++;
		otclCommand.createJavaFile(targetOCC, targetClz, sourceClz);
		return;
	}
	
	/**
	 * Process remaining path.
	 *
	 * @param targetOCC the target OCC
	 * @param otclCommand the otcl command
	 * @param scriptDto the script dto
	 * @param scriptGroupIdx the script group idx
	 * @param offsetIdx the offset idx
	 * @return the int
	 */
	private static int processRemainingPath(TargetOtclCommandContext targetOCC, OtclCommand otclCommand,
			ScriptDto scriptDto, int scriptGroupIdx, int offsetIdx) {
		OtclCommandDto memberOCD = targetOCC.otclCommandDto;
		if (targetOCC.hasPreAnchor) {
			otclCommand.appendAssignParentPcdToAnchoredPcd(targetOCC); 
		}
		OtclCommandDto childOCD = memberOCD;
		OtclCommandDto lastMemberOCD = null;
		List<String> values = ((Copy) scriptDto.command).from.values;
		boolean isCurrentPreAnchored = targetOCC.isPreAnchored();
		boolean isCurrentPostAnchored = targetOCC.isPostAnchored();
		TargetOtclCommandContext clonedTargetOCC = targetOCC.clone();
		for (int idx = 0; idx < values.size(); idx++) {
			String value = values.get(idx);
			if (targetOCC.hasPreAnchor) {
				if (idx > 0) {
					otclCommand.appendAssignAnchoredPcdToParentPcd(targetOCC);
				}
				childOCD = memberOCD;
				targetOCC.otclCommandDto = childOCD;
			} else {
				if (idx > 0) {
					if (!clonedTargetOCC.isLeaf()) {
						childOCD = lastMemberOCD;
						targetOCC.otclCommandDto = childOCD;
					} else {
						childOCD = memberOCD;
						targetOCC.otclCommandDto = childOCD;
					}
				}
			}
			while (true) {
				if (childOCD.isCollectionOrMapMember()) {
					Integer memberIdx = getIndex(targetOCC, idx, scriptGroupIdx, offsetIdx);
					if (childOCD.isMapKey()) {
						if (targetOCC.hasDescendantCollectionOrMap()) { 
							if (targetOCC.hasMapValueDescendant()) {
								otclCommand.appendIfNullTargetMemberPcdReturn(targetOCC, memberIdx, LogLevel.WARN);
							} else {
								otclCommand.appendAddMapKey(targetOCC, null, value, memberIdx);
							}
						} else {
							otclCommand.appendAddMapKey(targetOCC, null, value, memberIdx);
						}
					} else if (childOCD.isMapValue()) {
						otclCommand.appendAddMapValue(targetOCC, null, value, memberIdx, LogLevel.WARN);
					} else if (childOCD.isCollectionMember()) {
						if (targetOCC.hasMapValueDescendant()) {
							if (isCurrentPreAnchored || (isCurrentPostAnchored && idx == 0) ||
									(!targetOCC.hasAnchorInChain && offsetIdx == 0) ||
									!targetOCC.hasDescendantCollectionOrMap() || offsetIdx == 0) {
								otclCommand.appendIfNullTargetMemberPcdReturn(targetOCC, memberIdx, LogLevel.WARN);
							}
						} else {
							if (isCurrentPreAnchored || (isCurrentPostAnchored && idx == 0) ||
									(!targetOCC.hasAnchorInChain && offsetIdx == 0) ||
									!targetOCC.hasDescendantCollectionOrMap() || offsetIdx == 0) {
								otclCommand.appendAddToCollection(targetOCC, null, memberIdx, value);
							}
						}
					}
				} else if (!targetOCC.isLeaf()) {
					if (targetOCC.hasMapValueMember() || targetOCC.hasMapValueDescendant()) {
						otclCommand.appendIfNullTargetPcdReturn(targetOCC, LogLevel.WARN);
					} else {
						if (childOCD.isEnum()) {
							otclCommand.appendGetterIfNullCreateSet(targetOCC, value);
						} else {
							otclCommand.appendInit(targetOCC, null, false, LogLevel.WARN);
						}
					}
				} else {
					otclCommand.appendSetter(targetOCC, value);
				}
				if (childOCD.children == null || childOCD.children.size() == 0) {
					break;
				}
				if (childOCD.isCollectionOrMap()) {
					childOCD = OtclCommand.retrieveMemberOCD(targetOCC);
					targetOCC.otclCommandDto = childOCD; 
					if (!targetOCC.hasDescendantCollectionOrMap()) {
						lastMemberOCD = childOCD;
					}
				} else {
					if ((childOCD.isCollectionMember() || childOCD.isMapMember()) && 
							!targetOCC.hasDescendantCollectionOrMap()) {
						lastMemberOCD = childOCD;
					}
					childOCD = OtclCommand.retrieveNextOCD(targetOCC);
					targetOCC.otclCommandDto = childOCD;
				}
			}
			offsetIdx++;
		}
		return offsetIdx;
	}
}
