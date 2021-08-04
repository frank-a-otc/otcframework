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
package org.otcframework.core.engine.compiler;

import java.util.List;

import org.otcframework.common.OtcConstants;
import org.otcframework.common.OtcConstants.ALGORITHM_ID;
import org.otcframework.common.OtcConstants.LogLevel;
import org.otcframework.common.dto.OtcCommandDto;
import org.otcframework.common.dto.ScriptDto;
import org.otcframework.common.dto.otc.OtcFileDto.Copy;
import org.otcframework.core.engine.compiler.command.ExecutionContext;
import org.otcframework.core.engine.compiler.command.OtcCommand;
import org.otcframework.core.engine.compiler.command.SourceOtcCommandContext;
import org.otcframework.core.engine.compiler.command.TargetOtcCommandContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class CopyValuesCommandCodeGenerator.
 */
// TODO: Auto-generated Javadoc
final class CopyValuesCommandCodeGenerator extends AbstractOtcCodeGenerator {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(CopyValuesCommandCodeGenerator.class);

	/**
	 * Generate source code.
	 *
	 * @param executionContext the execution context
	 */
	public static void generateSourceCode(ExecutionContext executionContext) {
		OtcCommand otcCommand = executionContext.otcCommand;
		Class<?> targetClz = executionContext.targetClz;
		TargetOtcCommandContext targetOCC = executionContext.targetOCC;
		Class<?> sourceClz = executionContext.sourceClz;
		SourceOtcCommandContext sourceOCC = executionContext.sourceOCC;
		ScriptDto scriptDto = executionContext.targetOCC.scriptDto;
		targetOCC.algorithmId = ALGORITHM_ID.COPYVALUES;
		boolean addLogger = false;
		if (targetOCC.otcChain.contains(OtcConstants.MAP_VALUE_REF)) {
			addLogger = true;
		}
		OtcCommandDto targetOCD = targetOCC.otcCommandDto;
		TargetOtcCommandContext clonedTargetOCC = null;
		int offsetIdx = 0;
		int scriptGroupIdx = 0;
		if (scriptDto.command.debug) {
			@SuppressWarnings("unused")
			int dummy = 0;
		}
		List<String> values = ((Copy) scriptDto.command).from.values;
		if (values == null) {
			LOGGER.warn("'values:' property in OTC-command : {} is empty! Skiping Code-generation.",
					scriptDto.command.id);
			return;
		}
		otcCommand.clearCache();
		otcCommand.appendBeginClass(targetOCC, sourceOCC, targetClz, sourceClz, addLogger);
		clonedTargetOCC = targetOCC.clone();
		if (!clonedTargetOCC.isLeaf()) {
			otcCommand.appendInitUptoAnchoredOrLastCollectionOrLeaf(clonedTargetOCC, 0, false, LogLevel.WARN);
			if (clonedTargetOCC.otcCommandDto.isCollectionOrMap()) {
				targetOCD = OtcCommand.retrieveMemberOCD(clonedTargetOCC);
				clonedTargetOCC.otcCommandDto = targetOCD;
			}
		} else if (!targetOCD.isFirstNode) {
			otcCommand.appendGetter(clonedTargetOCC, targetOCD, false);
		}
		offsetIdx = processRemainingPath(clonedTargetOCC, otcCommand, scriptDto, scriptGroupIdx, offsetIdx);
		targetOCD = targetOCC.otcCommandDto;
		scriptGroupIdx++;
		otcCommand.createJavaFile(targetOCC, targetClz, sourceClz);
		return;
	}

	/**
	 * Process remaining path.
	 *
	 * @param targetOCC      the target OCC
	 * @param otcCommand     the otc command
	 * @param scriptDto      the script dto
	 * @param scriptGroupIdx the script group idx
	 * @param offsetIdx      the offset idx
	 * @return the int
	 */
	private static int processRemainingPath(TargetOtcCommandContext targetOCC, OtcCommand otcCommand,
			ScriptDto scriptDto, int scriptGroupIdx, int offsetIdx) {
		OtcCommandDto memberOCD = targetOCC.otcCommandDto;
		if (targetOCC.hasPreAnchor) {
			otcCommand.appendAssignParentPcdToAnchoredPcd(targetOCC);
		}
		OtcCommandDto childOCD = memberOCD;
		OtcCommandDto lastMemberOCD = null;
		List<String> values = ((Copy) scriptDto.command).from.values;
		boolean isCurrentPreAnchored = targetOCC.isPreAnchored();
		boolean isCurrentPostAnchored = targetOCC.isPostAnchored();
		TargetOtcCommandContext clonedTargetOCC = targetOCC.clone();
		for (int idx = 0; idx < values.size(); idx++) {
			String value = values.get(idx);
			if (targetOCC.hasPreAnchor) {
				if (idx > 0) {
					otcCommand.appendAssignAnchoredPcdToParentPcd(targetOCC);
				}
				childOCD = memberOCD;
				targetOCC.otcCommandDto = childOCD;
			} else {
				if (idx > 0) {
					if (!clonedTargetOCC.isLeaf()) {
						childOCD = lastMemberOCD;
						targetOCC.otcCommandDto = childOCD;
					} else {
						childOCD = memberOCD;
						targetOCC.otcCommandDto = childOCD;
					}
				}
			}
			while (true) {
				if (childOCD.isCollectionOrMapMember()) {
					Integer memberIdx = getIndex(targetOCC, idx, scriptGroupIdx, offsetIdx);
					if (childOCD.isMapKey()) {
						if (targetOCC.hasDescendantCollectionOrMap()) {
							if (targetOCC.hasMapValueDescendant()) {
								otcCommand.appendIfNullTargetMemberPcdReturn(targetOCC, memberIdx, LogLevel.WARN);
							} else {
								otcCommand.appendAddMapKey(targetOCC, null, value, memberIdx);
							}
						} else {
							otcCommand.appendAddMapKey(targetOCC, null, value, memberIdx);
						}
					} else if (childOCD.isMapValue()) {
						otcCommand.appendAddMapValue(targetOCC, null, value, memberIdx, LogLevel.WARN);
					} else if (childOCD.isCollectionMember()) {
						if (targetOCC.hasMapValueDescendant()) {
							if (isCurrentPreAnchored || (isCurrentPostAnchored && idx == 0)
									|| (!targetOCC.hasAnchorInChain && offsetIdx == 0)
									|| !targetOCC.hasDescendantCollectionOrMap() || offsetIdx == 0) {
								otcCommand.appendIfNullTargetMemberPcdReturn(targetOCC, memberIdx, LogLevel.WARN);
							}
						} else {
							if (isCurrentPreAnchored || (isCurrentPostAnchored && idx == 0)
									|| (!targetOCC.hasAnchorInChain && offsetIdx == 0)
									|| !targetOCC.hasDescendantCollectionOrMap() || offsetIdx == 0) {
								otcCommand.appendAddToCollection(targetOCC, null, memberIdx, value);
							}
						}
					}
				} else if (!targetOCC.isLeaf()) {
					if (targetOCC.hasMapValueMember() || targetOCC.hasMapValueDescendant()) {
						otcCommand.appendIfNullTargetPcdReturn(targetOCC, LogLevel.WARN);
					} else {
						if (childOCD.isEnum()) {
							otcCommand.appendGetterIfNullCreateSet(targetOCC, value);
						} else {
							otcCommand.appendInit(targetOCC, null, false, LogLevel.WARN);
						}
					}
				} else {
					otcCommand.appendSetter(targetOCC, value);
				}
				if (childOCD.children == null || childOCD.children.size() == 0) {
					break;
				}
				if (childOCD.isCollectionOrMap()) {
					childOCD = OtcCommand.retrieveMemberOCD(targetOCC);
					targetOCC.otcCommandDto = childOCD;
					if (!targetOCC.hasDescendantCollectionOrMap()) {
						lastMemberOCD = childOCD;
					}
				} else {
					if ((childOCD.isCollectionMember() || childOCD.isMapMember())
							&& !targetOCC.hasDescendantCollectionOrMap()) {
						lastMemberOCD = childOCD;
					}
					childOCD = OtcCommand.retrieveNextOCD(targetOCC);
					targetOCC.otcCommandDto = childOCD;
				}
			}
			offsetIdx++;
		}
		return offsetIdx;
	}
}
