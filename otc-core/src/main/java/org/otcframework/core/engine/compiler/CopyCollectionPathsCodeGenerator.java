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

import org.otcframework.common.OtcConstants.ALGORITHM_ID;
import org.otcframework.common.OtcConstants.LogLevel;
import org.otcframework.common.dto.OtcCommandDto;
import org.otcframework.common.dto.ScriptDto;
import org.otcframework.common.engine.compiler.OtcCommandContext;
import org.otcframework.core.engine.compiler.command.ExecutionContext;
import org.otcframework.core.engine.compiler.command.ExecutionContext.CHAINS_COLLECTION_COMPARISON_TYPE;
import org.otcframework.core.engine.compiler.command.OtcCommand;
import org.otcframework.core.engine.compiler.command.SourceOtcCommandContext;
import org.otcframework.core.engine.compiler.command.TargetOtcCommandContext;
import org.otcframework.core.engine.compiler.templates.AbstractTemplate;

/**
 * The Class CopyCollectionPathsCodeGenerator.
 */
// TODO: Auto-generated Javadoc
final class CopyCollectionPathsCodeGenerator extends AbstractOtcCodeGenerator {

	/**
	 * Instantiates a new copy collection paths code generator.
	 */
	private CopyCollectionPathsCodeGenerator() {
	}

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
		targetOCC.algorithmId = ALGORITHM_ID.COLLECTIONS;
		boolean addLogger = true;
		ScriptDto scriptDto = executionContext.targetOCC.scriptDto;
		if (scriptDto.command.debug) {
			@SuppressWarnings("unused")
			int dummy = 0;
		}
		targetOCC.scriptDto = scriptDto;
		TargetOtcCommandContext clonedTargetOCC = targetOCC.clone();
		executionContext.targetOCC = clonedTargetOCC;
		otcCommand.clearCache();
		otcCommand.appendBeginClass(clonedTargetOCC, sourceOCC, targetClz, sourceClz, addLogger);
		otcCommand.appendPreloopVars(clonedTargetOCC);
		generateCode(executionContext);
		if (clonedTargetOCC.loopsCounter > 0) {
			for (int bracesIdx = 0; bracesIdx < clonedTargetOCC.loopsCounter; bracesIdx++) {
				clonedTargetOCC.appendCode("\n}");
			}
		}
		otcCommand.createJavaFile(clonedTargetOCC, targetClz, sourceClz);
		return;
	}

	/**
	 * Generate code.
	 *
	 * @param executionContext the execution context
	 */
	@SuppressWarnings("unchecked")
	private static void generateCode(ExecutionContext executionContext) {
		OtcCommand otcCommand = executionContext.otcCommand;
		TargetOtcCommandContext targetOCC = executionContext.targetOCC;
		SourceOtcCommandContext sourceOCC = executionContext.sourceOCC;
		OtcCommandDto sourceOCD = sourceOCC.otcCommandDto;
		int sourceCollectionsCount = sourceOCC.collectionsCount;
		OtcCommandDto targetOCD = targetOCC.otcCommandDto;
		int targetCollectionsCount = targetOCC.collectionsCount;
		if (!sourceOCD.isCollectionOrMap()) {
			sourceOCD = OtcCommand.retrieveNextCollectionOrMapOCD(sourceOCC);
			sourceOCC.otcCommandDto = sourceOCD;
		}
		while (true) {
			if (sourceCollectionsCount > 0) {
				otcCommand.appendForLoop(targetOCC, sourceOCC, AbstractTemplate.SOURCE_IDX, false, LogLevel.WARN);
				sourceCollectionsCount--;
			} else {
				otcCommand.appendIfNullSourceContinue(targetOCC, sourceOCC, LogLevel.WARN);
			}
			if (sourceCollectionsCount > 0) {
				sourceOCD = OtcCommand.retrieveNextCollectionOrMapOCD(sourceOCC);
			} else if (!sourceOCC.isLeaf()) {
				sourceOCD = OtcCommand.retrieveNextOCD(sourceOCC);
			} else {
				break;
			}
			sourceOCC.otcCommandDto = sourceOCD;
		}
		sourceCollectionsCount = sourceOCC.collectionsCount;
		sourceOCD = sourceOCC.otcCommandDto;
		OtcCommandDto memberOCD = null;
		while (targetCollectionsCount > 0) {
			otcCommand.appendInitUptoNextCollectionWithContinue(targetOCC, LogLevel.WARN);
			memberOCD = OtcCommand.retrieveMemberOCD(targetOCC);
			targetOCC.otcCommandDto = memberOCD;
			executionContext.shouldIncrementOffsetIdx = false;
			if (targetOCC.isCurrentTokenAnchored()) {
				appendInitAnchored(executionContext, otcCommand);
			} else if (targetOCC.hasAnchoredDescendant()) {
				otcCommand.appendInitMember(targetOCC, sourceOCC, 0, false, LogLevel.WARN);
			} else if (targetOCC.hasAnchorInChain) {
				appendInitHasAnchor(executionContext, otcCommand);
			} else {
				appendInitNonAnchored(executionContext, otcCommand);
			}
			if (executionContext.shouldIncrementOffsetIdx && !executionContext.isOffsetIdxAlreadyAdded) {
				otcCommand.appendIncrementOffsetIdx(targetOCC);
				executionContext.isOffsetIdxAlreadyAdded = true;
			}
			targetCollectionsCount = targetOCC.collectionsCount - targetOCC.currentCollectionTokenIndex;
			if (targetOCC.hasDescendantCollectionOrMap()) {
				targetOCD = OtcCommand.retrieveNextOCD(targetOCC);
				targetOCC.otcCommandDto = targetOCD;
			}
		}
		if (!targetOCC.isLeaf() && !targetOCD.isCollectionOrMapMember()) {
			targetOCD = OtcCommand.retrieveNextOCD(targetOCC);
			targetOCC.otcCommandDto = targetOCD;
			while (!targetOCC.isLeaf()) {
				otcCommand.appendInit(targetOCC, sourceOCC, false, LogLevel.WARN);
				if (targetOCD.isEnum() && targetOCC.isLeafParent()) {
					break;
				}
				targetOCD = OtcCommand.retrieveNextOCD(targetOCC);
				targetOCC.otcCommandDto = targetOCD;
			}
		}
		if (memberOCD != null) {
			targetOCC.otcCommandDto = memberOCD;
			if (targetOCC.isLeaf()) {
				targetOCD = memberOCD;
			} else {
				targetOCC.otcCommandDto = targetOCD;
			}
		}
		if (!targetOCD.isCollectionOrMapMember()) {
			if ((targetOCD.parent != null && !targetOCD.parent.isEnum()) && !targetOCC.isLeafParent()) {
				otcCommand.appendGetSet(targetOCC, sourceOCC, false);
			}
		}
		return;
	}

	/**
	 * Append init anchored.
	 *
	 * @param executionContext the execution context
	 * @param otcCommand       the otc command
	 */
	private static void appendInitAnchored(ExecutionContext executionContext, OtcCommand otcCommand) {
		String idxVar = null;
		TargetOtcCommandContext targetOCC = executionContext.targetOCC;
		CHAINS_COLLECTION_COMPARISON_TYPE currentCollectionComparisonType = executionContext
				.currentCollectionSizeType(targetOCC);
		if (CHAINS_COLLECTION_COMPARISON_TYPE.LARGE_TARGET == currentCollectionComparisonType
				|| CHAINS_COLLECTION_COMPARISON_TYPE.EQUAL_SIZE == currentCollectionComparisonType) {
			idxVar = AbstractTemplate.SOURCE_IDX + 0;
		} else if (CHAINS_COLLECTION_COMPARISON_TYPE.LARGE_SOURCE == currentCollectionComparisonType) {
			if (targetOCC.currentCollectionTokenIndex < targetOCC.collectionsCount) {
				idxVar = AbstractTemplate.SOURCE_IDX + 0;
			} else if (targetOCC.currentCollectionTokenIndex == targetOCC.collectionsCount) {
				idxVar = AbstractTemplate.OFFSET_IDX;
				executionContext.shouldIncrementOffsetIdx = true;
			}
		}
		SourceOtcCommandContext sourceOCC = executionContext.sourceOCC;
		otcCommand.appendInitMember(targetOCC, sourceOCC, idxVar, false, LogLevel.WARN);
		targetOCC.anchorIndex = targetOCC.currentCollectionTokenIndex;
		return;
	}

	/**
	 * Append init has anchor.
	 *
	 * @param executionContext the execution context
	 * @param otcCommand       the otc command
	 */
	private static void appendInitHasAnchor(ExecutionContext executionContext, OtcCommand otcCommand) {
		String idxVar = null;
		OtcCommandContext sourceOCC = executionContext.sourceOCC;
		TargetOtcCommandContext targetOCC = executionContext.targetOCC;
		CHAINS_COLLECTION_COMPARISON_TYPE currentCollectionComparisonType = executionContext
				.currentCollectionSizeType(targetOCC);
		if (CHAINS_COLLECTION_COMPARISON_TYPE.LARGE_TARGET == currentCollectionComparisonType) {
			if (targetOCC.currentCollectionTokenIndex > sourceOCC.collectionsCount) {
				otcCommand.appendInitMember(targetOCC, sourceOCC, 0, false, LogLevel.WARN);
			} else {
				idxVar = AbstractTemplate.SOURCE_IDX + (targetOCC.currentCollectionTokenIndex - targetOCC.anchorIndex);
				otcCommand.appendInitMember(targetOCC, sourceOCC, idxVar, false, LogLevel.WARN);
			}
		} else if (CHAINS_COLLECTION_COMPARISON_TYPE.LARGE_SOURCE == currentCollectionComparisonType
				|| CHAINS_COLLECTION_COMPARISON_TYPE.EQUAL_SIZE == currentCollectionComparisonType) {
			if (targetOCC.currentCollectionTokenIndex < targetOCC.collectionsCount) {
				idxVar = AbstractTemplate.SOURCE_IDX + (targetOCC.currentCollectionTokenIndex - targetOCC.anchorIndex);
				otcCommand.appendInitMember(targetOCC, sourceOCC, idxVar, false, LogLevel.WARN);
			} else if (targetOCC.currentCollectionTokenIndex == targetOCC.collectionsCount) {
				if (executionContext.isLargeTarget()) {
					otcCommand.appendInitMember(targetOCC, sourceOCC, 0, false, LogLevel.WARN);
				} else {
					idxVar = AbstractTemplate.OFFSET_IDX;
					executionContext.shouldIncrementOffsetIdx = true;
					otcCommand.appendInitMember(targetOCC, sourceOCC, idxVar, false, LogLevel.WARN);
				}
			}
		}
		return;
	}

	/**
	 * Append init non anchored.
	 *
	 * @param executionContext the execution context
	 * @param otcCommand       the otc command
	 */
	private static void appendInitNonAnchored(ExecutionContext executionContext, OtcCommand otcCommand) {
		String idxVar = null;
		OtcCommandContext sourceOCC = executionContext.sourceOCC;
		TargetOtcCommandContext targetOCC = executionContext.targetOCC;
		if (executionContext.isLargeTarget()) {
			int remainingCollections = targetOCC.collectionsCount - targetOCC.currentCollectionTokenIndex;
			if (remainingCollections >= sourceOCC.collectionsCount) {
				otcCommand.appendInitMember(targetOCC, null, 0, false, LogLevel.WARN);
			} else {
				int id = sourceOCC.collectionsCount - (remainingCollections + 1);
				idxVar = AbstractTemplate.SOURCE_IDX + id;
				otcCommand.appendInitMember(targetOCC, sourceOCC, idxVar, false, LogLevel.WARN);
			}
		} else if (executionContext.isLargeSource()) {
			if (targetOCC.currentCollectionTokenIndex < targetOCC.collectionsCount) {
				idxVar = AbstractTemplate.SOURCE_IDX + (targetOCC.currentCollectionTokenIndex - 1);
			} else {
				idxVar = AbstractTemplate.OFFSET_IDX;
				executionContext.shouldIncrementOffsetIdx = true;
			}
			otcCommand.appendInitMember(targetOCC, sourceOCC, idxVar, false, LogLevel.WARN);
		} else {
			if (targetOCC.currentCollectionTokenIndex <= targetOCC.collectionsCount) {
				idxVar = AbstractTemplate.SOURCE_IDX + (targetOCC.currentCollectionTokenIndex - 1);
				otcCommand.appendInitMember(targetOCC, sourceOCC, idxVar, false, LogLevel.WARN);
			} else {
				otcCommand.appendInitMember(targetOCC, null, 0, false, LogLevel.WARN);
			}
		}
	}
}
