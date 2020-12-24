/**
* Copyright (c) otclfoundation.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*/
package org.otcl2.core.engine.compiler;

import org.otcl2.common.OtclConstants.ALGORITHM_ID;
import org.otcl2.common.OtclConstants.LogLevel;
import org.otcl2.common.dto.OtclCommandDto;
import org.otcl2.common.dto.ScriptDto;
import org.otcl2.common.engine.compiler.OtclCommandContext;
import org.otcl2.core.engine.compiler.command.ExecutionContext;
import org.otcl2.core.engine.compiler.command.ExecutionContext.CHAINS_COLLECTION_COMPARISON_TYPE;
import org.otcl2.core.engine.compiler.command.OtclCommand;
import org.otcl2.core.engine.compiler.command.SourceOtclCommandContext;
import org.otcl2.core.engine.compiler.command.TargetOtclCommandContext;
import org.otcl2.core.engine.compiler.templates.AbstractTemplate;

// TODO: Auto-generated Javadoc
/**
 * The Class CopyCollectionPathsCodeGenerator.
 */
final class CopyCollectionPathsCodeGenerator extends AbstractOtclCodeGenerator {

//	private static final Logger LOGGER = LoggerFactory.getLogger(MapAndCollectionsPairCodeGenerator.class);
	
	/**
	 * Instantiates a new copy collection paths code generator.
	 */
	private CopyCollectionPathsCodeGenerator() { }

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
		
		targetOCC.algorithmId = ALGORITHM_ID.COLLECTIONS;
		boolean addLogger = true;
				
		ScriptDto scriptDto = executionContext.targetOCC.scriptDto;
		if (scriptDto.command.debug) {
			@SuppressWarnings("unused")
			int dummy = 0;
		}
		targetOCC.scriptDto = scriptDto;
		TargetOtclCommandContext clonedTargetOCC = targetOCC.clone();
		executionContext.targetOCC = clonedTargetOCC;
		otclCommand.clearCache();
		otclCommand.appendBeginClass(clonedTargetOCC, sourceOCC, targetClz, sourceClz, addLogger);
		otclCommand.appendPreloopVars(clonedTargetOCC);
		executeOtclRecursive(executionContext);
		if (clonedTargetOCC.loopsCounter > 0) {
			for (int bracesIdx = 0; bracesIdx < clonedTargetOCC.loopsCounter; bracesIdx++) {
				clonedTargetOCC.appendCode("\n}");
			}
		}
		otclCommand.createJavaFile(clonedTargetOCC, targetClz, sourceClz);
		return;
	}

	/**
	 * Execute otcl recursive.
	 *
	 * @param executionContext the execution context
	 */
	@SuppressWarnings("unchecked")
	private static void executeOtclRecursive(ExecutionContext executionContext) {

		OtclCommand otclCommand = executionContext.otclCommand;
		TargetOtclCommandContext targetOCC = executionContext.targetOCC;
		SourceOtclCommandContext sourceOCC = executionContext.sourceOCC;
		OtclCommandDto sourceOCD = sourceOCC.otclCommandDto;

		int sourceCollectionsCount = sourceOCC.collectionsCount;
		OtclCommandDto targetOCD = targetOCC.otclCommandDto;

		int targetCollectionsCount = targetOCC.collectionsCount;
		if (!sourceOCD.isCollectionOrMap()) {
			sourceOCD = OtclCommand.retrieveNextCollectionOrMapOCD(sourceOCC);
			sourceOCC.otclCommandDto = sourceOCD; 
		}
		while (true) {
			if (sourceCollectionsCount > 0) {
				otclCommand.appendForLoop(targetOCC, sourceOCC, AbstractTemplate.SOURCE_IDX, false, LogLevel.WARN);
				sourceCollectionsCount--;
			} else {
				otclCommand.appendIfNullSourceContinue(targetOCC, sourceOCC, LogLevel.WARN);
			}
			if (sourceCollectionsCount > 0) {
				sourceOCD = OtclCommand.retrieveNextCollectionOrMapOCD(sourceOCC);
			} else if (!sourceOCC.isLeaf()) {
				sourceOCD = OtclCommand.retrieveNextOCD(sourceOCC);
			} else {
				break;
			}
			sourceOCC.otclCommandDto = sourceOCD;
		}
		sourceCollectionsCount = sourceOCC.collectionsCount;
		sourceOCD = sourceOCC.otclCommandDto;
		while (targetCollectionsCount > 0) {
			otclCommand.appendInitUptoNextCollectionWithContinue(targetOCC, LogLevel.WARN);
			OtclCommandDto memberOCD = OtclCommand.retrieveMemberOCD(targetOCC);
			targetOCC.otclCommandDto = memberOCD;
			executionContext.shouldIncrementOffsetIdx = false;
			if (targetOCC.isCurrentTokenAnchored()) {
				appendInitAnchored(executionContext, otclCommand);
			} else if (targetOCC.hasAnchoredDescendant()) {
				otclCommand.appendInitMember(targetOCC, sourceOCD, 0, false, LogLevel.WARN);
			} else if (targetOCC.hasAnchorInChain) {
				appendInitHasAnchor(executionContext, otclCommand);
			} else {
				appendInitNonAnchored(executionContext, otclCommand);
			}
			if (executionContext.shouldIncrementOffsetIdx && !executionContext.isOffsetIdxAlreadyAdded) {
				otclCommand.appendIncrementOffsetIdx(targetOCC);
				executionContext.isOffsetIdxAlreadyAdded = true;
			}
			if (targetOCC.hasDescendantCollectionOrMap()) {
				targetOCD = OtclCommand.retrieveNextOCD(targetOCC);
				targetOCC.otclCommandDto = targetOCD;
			}
			targetCollectionsCount = targetOCC.collectionsCount - targetOCC.currentCollectionTokenIndex;
		}
		if (!targetOCC.isLeaf() && !targetOCD.isCollectionOrMapMember()) {
			targetOCD = OtclCommand.retrieveNextOCD(targetOCC);
			targetOCC.otclCommandDto = targetOCD;
			while (!targetOCC.isLeaf()) {
				otclCommand.appendInit(targetOCC, false, LogLevel.WARN);
				targetOCD = OtclCommand.retrieveNextOCD(targetOCC);
				targetOCC.otclCommandDto = targetOCD;
			}
		}
		if (!targetOCD.isCollectionOrMapMember()) {
			otclCommand.appendGetSet(targetOCC, sourceOCC, false);
		}
		return;
	}

	/**
	 * Append init anchored.
	 *
	 * @param executionContext the execution context
	 * @param otclCommand the otcl command
	 */
	private static void appendInitAnchored(ExecutionContext executionContext, OtclCommand otclCommand) {
		String idxVar = null;
		TargetOtclCommandContext targetOCC = executionContext.targetOCC;
		CHAINS_COLLECTION_COMPARISON_TYPE currentCollectionComparisonType = 
				executionContext.currentCollectionSizeType(targetOCC);
		if (CHAINS_COLLECTION_COMPARISON_TYPE.LARGE_TARGET == currentCollectionComparisonType ||
				CHAINS_COLLECTION_COMPARISON_TYPE.EQUAL_SIZE == currentCollectionComparisonType) {
			idxVar = AbstractTemplate.SOURCE_IDX + 0;
		} else if (CHAINS_COLLECTION_COMPARISON_TYPE.LARGE_SOURCE == currentCollectionComparisonType) {
			if (targetOCC.currentCollectionTokenIndex < targetOCC.collectionsCount) {
				idxVar = AbstractTemplate.SOURCE_IDX + 0;
			} else if (targetOCC.currentCollectionTokenIndex == targetOCC.collectionsCount) {
				idxVar = AbstractTemplate.OFFSET_IDX;
				executionContext.shouldIncrementOffsetIdx = true;
			}
		}
		OtclCommandDto sourceOCD = executionContext.sourceOCC.otclCommandDto;
		otclCommand.appendInitMember(targetOCC, sourceOCD, idxVar, false, LogLevel.WARN);
		targetOCC.anchorIndex = targetOCC.currentCollectionTokenIndex;
		return;
	}
	
	/**
	 * Append init has anchor.
	 *
	 * @param executionContext the execution context
	 * @param otclCommand the otcl command
	 */
	private static void appendInitHasAnchor(ExecutionContext executionContext, OtclCommand otclCommand) {
		String idxVar = null;
		OtclCommandContext sourceOCC = executionContext.sourceOCC;
		OtclCommandDto sourceOCD = sourceOCC.otclCommandDto;
		TargetOtclCommandContext targetOCC = executionContext.targetOCC;
		CHAINS_COLLECTION_COMPARISON_TYPE currentCollectionComparisonType = executionContext.currentCollectionSizeType(targetOCC);
		if (CHAINS_COLLECTION_COMPARISON_TYPE.LARGE_TARGET == currentCollectionComparisonType) {
			if (targetOCC.currentCollectionTokenIndex > sourceOCC.collectionsCount) {
				otclCommand.appendInitMember(targetOCC, sourceOCD, 0, false, LogLevel.WARN);
			} else {
				idxVar = AbstractTemplate.SOURCE_IDX + (targetOCC.currentCollectionTokenIndex - targetOCC.anchorIndex);
				otclCommand.appendInitMember(targetOCC, sourceOCD, idxVar, false, LogLevel.WARN);
			}
		} else if (CHAINS_COLLECTION_COMPARISON_TYPE.LARGE_SOURCE == currentCollectionComparisonType ||
				CHAINS_COLLECTION_COMPARISON_TYPE.EQUAL_SIZE == currentCollectionComparisonType) {
			if (targetOCC.currentCollectionTokenIndex < targetOCC.collectionsCount) {
				idxVar = AbstractTemplate.SOURCE_IDX + (targetOCC.currentCollectionTokenIndex - targetOCC.anchorIndex);
				otclCommand.appendInitMember(targetOCC, sourceOCD, idxVar, false, LogLevel.WARN);
			} else if (targetOCC.currentCollectionTokenIndex == targetOCC.collectionsCount) {
				if (executionContext.isLargeTarget()) {
					otclCommand.appendInitMember(targetOCC, sourceOCD, 0, false, LogLevel.WARN);
				} else {
					idxVar = AbstractTemplate.OFFSET_IDX;
					executionContext.shouldIncrementOffsetIdx = true;
					otclCommand.appendInitMember(targetOCC, sourceOCD, idxVar, false, LogLevel.WARN);
				}
			}
		}
		return;
	}
	
	/**
	 * Append init no anchor.
	 *
	 * @param executionContext the execution context
	 * @param otclCommand the otcl command
	 */
	private static void appendInitNonAnchored(ExecutionContext executionContext, OtclCommand otclCommand) {
		String idxVar = null;
		OtclCommandContext sourceOCC = executionContext.sourceOCC;
		OtclCommandDto sourceOCD = sourceOCC.otclCommandDto;
		TargetOtclCommandContext targetOCC = executionContext.targetOCC;
		if (executionContext.isLargeTarget()) {
			int remainingCollections = targetOCC.collectionsCount - targetOCC.currentCollectionTokenIndex;
			if (remainingCollections >= sourceOCC.collectionsCount) {
				otclCommand.appendInitMember(targetOCC, null, 0, false, LogLevel.WARN);
			} else {
				int id = sourceOCC.collectionsCount - (remainingCollections + 1);
				idxVar = AbstractTemplate.SOURCE_IDX + id;
				otclCommand.appendInitMember(targetOCC, sourceOCD, idxVar, false, LogLevel.WARN);
			}
		} else if (executionContext.isLargeSource()) {
			if (targetOCC.currentCollectionTokenIndex < targetOCC.collectionsCount) {
				idxVar = AbstractTemplate.SOURCE_IDX + (targetOCC.currentCollectionTokenIndex - 1);
			} else {
				idxVar = AbstractTemplate.OFFSET_IDX;
				executionContext.shouldIncrementOffsetIdx = true;
			}
			otclCommand.appendInitMember(targetOCC, sourceOCD, idxVar, false, LogLevel.WARN);
		} else {
			if (targetOCC.currentCollectionTokenIndex <= targetOCC.collectionsCount) {
				idxVar = AbstractTemplate.SOURCE_IDX + (targetOCC.currentCollectionTokenIndex - 1);
				otclCommand.appendInitMember(targetOCC, sourceOCD, idxVar, false, LogLevel.WARN);
			} else {
				otclCommand.appendInitMember(targetOCC, null, 0, false, LogLevel.WARN);
			}
		}
	}

}
