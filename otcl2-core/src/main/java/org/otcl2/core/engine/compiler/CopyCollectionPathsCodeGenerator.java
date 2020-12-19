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
		executionContext.initCollectionSizeType();
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
		boolean isOffsetIdxInitialized = false;
		while (true) {
			if (sourceCollectionsCount > 0) {
				otclCommand.appendForLoop(targetOCC, sourceOCC, AbstractTemplate.SOURCE_IDX, false, LogLevel.WARN);
				sourceCollectionsCount--;
				if (executionContext.isLargeSource() && targetOCC.collectionsCount > 1
						&& !isOffsetIdxInitialized) {
					otclCommand.appendInitOffsetIdx(targetOCC);
					isOffsetIdxInitialized = true;
				}
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
		boolean isOffsetIdxAlreadyAdded = false;
		while (targetCollectionsCount > 0) {
			otclCommand.appendInitUptoNextCollectionWithContinue(targetOCC, LogLevel.WARN);
			OtclCommandDto memberOCD = OtclCommand.retrieveMemberOCD(targetOCC);
			targetOCC.otclCommandDto = memberOCD;
			String idxVar = null;
			executionContext.shouldIncrementOffsetIdx = false;
			CHAINS_COLLECTION_COMPARISON_TYPE currentCollectionComparisonType = executionContext.currentCollectionSizeType();
			if (targetOCC.isCurrentTokenAnchored()) {
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
				otclCommand.appendInitMember(targetOCC, sourceOCD, idxVar, false, LogLevel.WARN);
				targetOCC.anchorIndex = targetOCC.currentCollectionTokenIndex;
			} else if (targetOCC.hasAnchoredDescendant()) {
				otclCommand.appendInitMember(targetOCC, sourceOCD, 0, false, LogLevel.WARN);
			} else if (targetOCC.hasAnchorInChain) {
				if (CHAINS_COLLECTION_COMPARISON_TYPE.LARGE_TARGET == currentCollectionComparisonType) {
					if (targetOCC.currentCollectionTokenIndex > sourceOCC.collectionsCount) {
						otclCommand.appendInitMember(targetOCC, sourceOCD, 0, false, LogLevel.WARN);
					} else {
						idxVar = AbstractTemplate.SOURCE_IDX + (targetOCC.currentCollectionTokenIndex - targetOCC.anchorIndex);
						otclCommand.appendInitMember(targetOCC, sourceOCD, idxVar, false, LogLevel.WARN);
					}
				} else if (CHAINS_COLLECTION_COMPARISON_TYPE.LARGE_SOURCE == currentCollectionComparisonType) {
					if (targetOCC.currentCollectionTokenIndex < targetOCC.collectionsCount) {
						idxVar = AbstractTemplate.SOURCE_IDX + (targetOCC.currentCollectionTokenIndex - targetOCC.anchorIndex);
					} else if (targetOCC.currentCollectionTokenIndex == targetOCC.collectionsCount) {
						if (executionContext.isLargeTarget()) {
							otclCommand.appendInitMember(targetOCC, sourceOCD, 0, false, LogLevel.WARN);
						} else {
							idxVar = AbstractTemplate.OFFSET_IDX;
							executionContext.shouldIncrementOffsetIdx = true;
						}
					}
					otclCommand.appendInitMember(targetOCC, sourceOCD, idxVar, false, LogLevel.WARN);
				} else { // its equal
					
				}
			} else {
				appendInit(executionContext, otclCommand); 
			}
			executionContext.initCollectionSizeType();
			if (executionContext.shouldIncrementOffsetIdx && !isOffsetIdxAlreadyAdded) {
				otclCommand.appendIncrementOffsetIdx(targetOCC);
				isOffsetIdxAlreadyAdded = true;
			}
			targetOCD = OtclCommand.retrieveNextOCD(targetOCC);
			targetOCC.otclCommandDto = targetOCD;
			targetCollectionsCount = targetOCC.collectionsCount - targetOCC.currentCollectionTokenIndex;
			if (targetCollectionsCount == 0 && !targetOCC.isLeaf()) {
				otclCommand.appendInit(targetOCC, false, LogLevel.WARN);
			}
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
	 * Append init.
	 *
	 * @param nonFlatLogic the non flat logic
	 * @param executionContext the execution context
	 * @param otclCommand the otcl command
	 */
//	private static void appendInit(NonFlatLogic nonFlatLogic, ExecutionContext executionContext, OtclCommand otclCommand) {
//		String idxVar = null;
//		if (CHAINS_COLLECTION_COMPARISON_TYPE.EQUAL == nonFlatLogic.collectionSize) {
//			nonFlatLogic.postAnchorCounter++;
//			idxVar = AbstractTemplate.SOURCE_IDX + nonFlatLogic.postAnchorCounter;
//			otclCommand.appendInitMember(executionContext.targetOCC, executionContext.sourceOCC.otclCommandDto, idxVar,
//					false, LogLevel.WARN);
//		} else if (CHAINS_COLLECTION_COMPARISON_TYPE.LARGE_TARGET == nonFlatLogic.collectionSize) {
//			if (nonFlatLogic.targetDescendantsCollectionsCount > nonFlatLogic.initialSourceDescendantsCollectionsCount) {
//				otclCommand.appendInitMember(executionContext.targetOCC, null, 0, false, LogLevel.WARN);
//			} else if (nonFlatLogic.targetDescendantsCollectionsCount == 1) {
//				idxVar = AbstractTemplate.OFFSET_IDX;
//				nonFlatLogic.shouldIncrementOffsetIdx = true;
//				otclCommand.appendInitMember(executionContext.targetOCC, executionContext.sourceOCC.otclCommandDto, idxVar,
//						false, LogLevel.WARN);
//			} else {
//				nonFlatLogic.postAnchorCounter++;
//				idxVar = AbstractTemplate.SOURCE_IDX + nonFlatLogic.postAnchorCounter;
//				otclCommand.appendInitMember(executionContext.targetOCC, executionContext.sourceOCC.otclCommandDto, idxVar,
//						false, LogLevel.WARN);
//			}
//		} else {
//			if (nonFlatLogic.sourceDescendantsCollectionsCount > nonFlatLogic.initialTargetDescendantsCollectionsCount) {
//				if (nonFlatLogic.targetDescendantsCollectionsCount == 1) {
//					idxVar = AbstractTemplate.OFFSET_IDX;
//					nonFlatLogic.shouldIncrementOffsetIdx = true;
//				} else {
//					nonFlatLogic.postAnchorCounter++;
//					idxVar = AbstractTemplate.SOURCE_IDX + nonFlatLogic.postAnchorCounter;
//				}
//				otclCommand.appendInitMember(executionContext.targetOCC, executionContext.sourceOCC.otclCommandDto, idxVar,
//						false, LogLevel.WARN);
//			} else if (nonFlatLogic.targetDescendantsCollectionsCount == 1) {
//				idxVar = AbstractTemplate.OFFSET_IDX;
//				nonFlatLogic.shouldIncrementOffsetIdx = true;
//				otclCommand.appendInitMember(executionContext.targetOCC, executionContext.sourceOCC.otclCommandDto, idxVar,
//						false, LogLevel.WARN);
//			} else {
//				otclCommand.appendInitMember(executionContext.targetOCC, null, 0, false, LogLevel.WARN);
//			}
//		}
//	}
	
	/**
	 * Append init.
	 *
	 * @param executionContext the execution context
	 * @param otclCommand the otcl command
	 */
	private static void appendInit(ExecutionContext executionContext, OtclCommand otclCommand) {
		String idxVar = null;
		if (executionContext.isEqualSize()) {
			idxVar = AbstractTemplate.SOURCE_IDX + (executionContext.sourceOCC.currentCollectionTokenIndex - 1);
			otclCommand.appendInitMember(executionContext.targetOCC, executionContext.sourceOCC.otclCommandDto, idxVar,
					false, LogLevel.WARN);
		} else if (executionContext.isLargeTarget()) {
			int targetDescendantsCollectionsCount = executionContext.targetOCC.collectionsCount - executionContext.targetOCC.currentCollectionTokenIndex;
			if (targetDescendantsCollectionsCount > executionContext.sourceOCC.collectionsCount) {
				otclCommand.appendInitMember(executionContext.targetOCC, null, 0, false, LogLevel.WARN);
			} else if (targetDescendantsCollectionsCount == 1) {
				idxVar = AbstractTemplate.OFFSET_IDX;
				executionContext.shouldIncrementOffsetIdx = true;
				otclCommand.appendInitMember(executionContext.targetOCC, executionContext.sourceOCC.otclCommandDto, idxVar,
						false, LogLevel.WARN);
			} else {
				idxVar = AbstractTemplate.SOURCE_IDX + (executionContext.sourceOCC.currentCollectionTokenIndex - 1);
				otclCommand.appendInitMember(executionContext.targetOCC, executionContext.sourceOCC.otclCommandDto, idxVar,
						false, LogLevel.WARN);
			}
		} else {
			int sourceDescendantsCollectionsCount = executionContext.sourceOCC.collectionsCount - executionContext.sourceOCC.currentCollectionTokenIndex;
			int targetDescendantsCollectionsCount = executionContext.targetOCC.collectionsCount - executionContext.targetOCC.currentCollectionTokenIndex;
			if (sourceDescendantsCollectionsCount > executionContext.targetOCC.collectionsCount) {
				if (targetDescendantsCollectionsCount == 1) {
					idxVar = AbstractTemplate.OFFSET_IDX;
					executionContext.shouldIncrementOffsetIdx = true;
				} else {
					idxVar = AbstractTemplate.SOURCE_IDX + (executionContext.sourceOCC.currentCollectionTokenIndex - 1);
				}
				otclCommand.appendInitMember(executionContext.targetOCC, executionContext.sourceOCC.otclCommandDto, idxVar,
						false, LogLevel.WARN);
			} else if (targetDescendantsCollectionsCount == 1) {
				idxVar = AbstractTemplate.OFFSET_IDX;
				executionContext.shouldIncrementOffsetIdx = true;
				otclCommand.appendInitMember(executionContext.targetOCC, executionContext.sourceOCC.otclCommandDto, idxVar,
						false, LogLevel.WARN);
			} else {
				otclCommand.appendInitMember(executionContext.targetOCC, null, 0, false, LogLevel.WARN);
			}
		}
	}

}
