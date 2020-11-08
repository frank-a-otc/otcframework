/**
* Copyright (c) otcl2.org
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
import org.otcl2.core.engine.compiler.command.OtclCommand;
import org.otcl2.core.engine.compiler.command.SourceOtclCommandContext;
import org.otcl2.core.engine.compiler.command.TargetOtclCommandContext;
import org.otcl2.core.engine.compiler.templates.AbstractTemplate;

final class CopyCollectionPathsCodeGenerator extends AbstractOtclCodeGenerator {

//	private static final Logger LOGGER = LoggerFactory.getLogger(MapAndCollectionsPairCodeGenerator.class);
	
	enum CHAIN_COLLECTION_SIZE {LARGE_SOURCE, LARGE_TARGET, EQUAL};

	private CopyCollectionPathsCodeGenerator() { }
	
	private static class NonFlatLogic {
		int initialSourceDescendantsCollectionsCount;
		int sourceDescendantsCollectionsCount;
		int initialTargetDescendantsCollectionsCount;
		int targetDescendantsCollectionsCount;
		CHAIN_COLLECTION_SIZE collectionSize;
		int postAnchorCounter;
		boolean shouldIncrementOffsetIdx;
	}
	
	public static void generateSourceCode(ExecutionContext executionContext) {
		OtclCommand otclCommand = executionContext.otclCommand;
		Class<?> targetClz = executionContext.targetClz;
		TargetOtclCommandContext targetOCC = executionContext.targetOCC;
		Class<?> sourceClz = executionContext.sourceClz; 
		SourceOtclCommandContext sourceOCC = executionContext.sourceOCC;
		
//		ScriptGroupDto scriptGroupDto = entry.getValue();
//		List<ScriptDto> scriptDtos = scriptGroupDto.scriptDtos;
		targetOCC.algorithmId = ALGORITHM_ID.COLLECTIONS;
		boolean addLogger = true;
		
//		for (ScriptDto scriptDto : scriptDtos) {
		
		ScriptDto scriptDto = executionContext.targetOCC.scriptDto;
		if (scriptDto.command.debug) {
			@SuppressWarnings("unused")
			int dummy = 0;
		}
		targetOCC.scriptDto = scriptDto;
		TargetOtclCommandContext clonedTargetOCC = targetOCC.clone();
		executionContext.targetOCC = clonedTargetOCC;
		resetOCC(sourceOCC, scriptDto);
		otclCommand.clearCache();
		otclCommand.appendBeginClass(clonedTargetOCC, sourceOCC, targetClz, sourceClz, addLogger);
		otclCommand.appendPreloopVars(clonedTargetOCC);
		executeOtclRecursive(executionContext);
		if (clonedTargetOCC.loopCounter > 0) {
			for (int bracesIdx = 0; bracesIdx < clonedTargetOCC.loopCounter; bracesIdx++) {
				clonedTargetOCC.appendCode("\n}");
			}
		}
		otclCommand.createJavaFile(clonedTargetOCC, targetClz, sourceClz);
		return;
	}

	@SuppressWarnings("unchecked")
	private static void executeOtclRecursive(ExecutionContext executionContext) {

		OtclCommand otclCommand = executionContext.otclCommand;
		TargetOtclCommandContext targetOCC = executionContext.targetOCC;
		SourceOtclCommandContext sourceOCC = executionContext.sourceOCC;
		OtclCommandDto sourceOCD = sourceOCC.otclCommandDto;
		int sourceDescendantsCollectionsCount = sourceOCC.descendantsCollectionsCount() +
				sourceOCC.descendantsMapsCount();
		if (sourceOCD.isCollectionOrMap()) {
			sourceDescendantsCollectionsCount++;
		}
		int initialSourceDescendantsCollectionsCount = sourceDescendantsCollectionsCount;
		OtclCommandDto targetOCD = targetOCC.otclCommandDto;
		int targetDescendantsCollectionsCount = targetOCC.descendantsCollectionsCount() +
				targetOCC.descendantsMapsCount();
		if (targetOCD.isCollectionOrMap()) {
			targetDescendantsCollectionsCount++;
		}
		int initialTargetDescendantsCollectionsCount = targetDescendantsCollectionsCount;
		CHAIN_COLLECTION_SIZE collectionSize = decideChainCollectionSize(targetDescendantsCollectionsCount,
				sourceDescendantsCollectionsCount);
		if (!sourceOCD.isCollectionOrMap()) {
			sourceOCD = OtclCommand.retrieveNextCollectionOrMapOCD(sourceOCC);
			sourceOCC.otclCommandDto = sourceOCD; 
		}
		boolean isOffsetIdxInitialized = false;
		while (true) {
			if (sourceDescendantsCollectionsCount > 0) {
				otclCommand.appendForLoop(targetOCC, sourceOCC, AbstractTemplate.SOURCE_IDX, false, LogLevel.WARN);
				sourceDescendantsCollectionsCount--;
				if (CHAIN_COLLECTION_SIZE.LARGE_SOURCE == collectionSize && initialTargetDescendantsCollectionsCount > 1
						&& !isOffsetIdxInitialized) {
					otclCommand.appendInitOffsetIdx(targetOCC);
					isOffsetIdxInitialized = true;
				}
			} else {
				otclCommand.appendIfNullSourceContinue(targetOCC, sourceOCC, LogLevel.WARN);
			}
			if (sourceDescendantsCollectionsCount > 0) {
				sourceOCD = OtclCommand.retrieveNextCollectionOrMapOCD(sourceOCC);
			} else if (!sourceOCC.isLeaf()) {
				sourceOCD = OtclCommand.retrieveNextOCD(sourceOCC);
			} else {
				break;
			}
			sourceOCC.otclCommandDto = sourceOCD;
		}
		sourceDescendantsCollectionsCount = initialSourceDescendantsCollectionsCount;
		sourceOCD = sourceOCC.otclCommandDto;
		int postAnchorCounter = -1;
		boolean isOffsetIdxAlreadyAdded = false;
		NonFlatLogic nonFlatLogic = new NonFlatLogic();
		nonFlatLogic.postAnchorCounter = postAnchorCounter;
		nonFlatLogic.targetDescendantsCollectionsCount = targetDescendantsCollectionsCount;
		nonFlatLogic.initialTargetDescendantsCollectionsCount = initialTargetDescendantsCollectionsCount;
		nonFlatLogic.collectionSize = collectionSize;
		nonFlatLogic.sourceDescendantsCollectionsCount = sourceDescendantsCollectionsCount;
		nonFlatLogic.initialSourceDescendantsCollectionsCount = initialSourceDescendantsCollectionsCount;

		while (targetDescendantsCollectionsCount > 0) {
			otclCommand.appendInitUptoNextCollectionWithContinue(targetOCC, LogLevel.WARN);
			OtclCommandDto memberOCD = OtclCommand.retrieveMemberOCD(targetOCC);
			targetOCC.otclCommandDto = memberOCD;
			boolean isAnchored = false;
			boolean hasAnchoredDescendant = false;
			if (targetOCC.hasAnchor) {
				isAnchored = targetOCC.isAnchored();
				hasAnchoredDescendant = targetOCC.hasAnchoredDescendant();
			}
			String idxVar = null;
			boolean shouldIncrementOffsetIdx = false;
			nonFlatLogic.shouldIncrementOffsetIdx = shouldIncrementOffsetIdx;
			if (isAnchored) {
				if (targetDescendantsCollectionsCount == 1) {
					idxVar = AbstractTemplate.OFFSET_IDX;
					nonFlatLogic.shouldIncrementOffsetIdx = true;
				} else {
					postAnchorCounter++;
					idxVar = AbstractTemplate.SOURCE_IDX + postAnchorCounter;
				}
				otclCommand.appendInitMember(targetOCC, sourceOCD, idxVar, false, LogLevel.WARN);
				int tempTargetDCCount = targetOCC.descendantsCollectionsCount() + targetOCC.descendantsMapsCount();
				int tempSourceDCCount = sourceOCC.descendantsCollectionsCount() + sourceOCC.descendantsMapsCount();
				collectionSize = decideChainCollectionSize(tempTargetDCCount, tempSourceDCCount);
				nonFlatLogic.collectionSize = collectionSize;
				nonFlatLogic.targetDescendantsCollectionsCount = tempTargetDCCount;
				nonFlatLogic.sourceDescendantsCollectionsCount = tempSourceDCCount;
			} else if (hasAnchoredDescendant) {
				otclCommand.appendInitMember(targetOCC, sourceOCD, 0, false, LogLevel.WARN);
			} else if (targetOCC.hasAnchor) {
				appendInit(nonFlatLogic, executionContext, otclCommand); 

			} else {
				appendInit(nonFlatLogic, executionContext, otclCommand); 
			}
			if (nonFlatLogic.shouldIncrementOffsetIdx && !isOffsetIdxAlreadyAdded) {
				otclCommand.appendIncrementOffsetIdx(targetOCC);
				isOffsetIdxAlreadyAdded = true;
			}
			targetOCD = OtclCommand.retrieveNextOCD(targetOCC);
			targetOCC.otclCommandDto = targetOCD;
			targetDescendantsCollectionsCount--;
			if (sourceDescendantsCollectionsCount > 0) {
				sourceDescendantsCollectionsCount--;
			}
			if (targetDescendantsCollectionsCount == 0 && !targetOCC.isLeaf()) {
				otclCommand.appendInit(targetOCC, false, LogLevel.WARN);
			}
			nonFlatLogic.targetDescendantsCollectionsCount = targetDescendantsCollectionsCount;
			nonFlatLogic.sourceDescendantsCollectionsCount = sourceDescendantsCollectionsCount;
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

	private static CHAIN_COLLECTION_SIZE decideChainCollectionSize(int targetDescendantsCollectionsCount,
			int sourceDescendantsCollectionsCount) {
		CHAIN_COLLECTION_SIZE collectionSize = null;
		if (targetDescendantsCollectionsCount > sourceDescendantsCollectionsCount) {
			collectionSize = CHAIN_COLLECTION_SIZE.LARGE_TARGET;
		} else if (targetDescendantsCollectionsCount == sourceDescendantsCollectionsCount) {
			collectionSize = CHAIN_COLLECTION_SIZE.EQUAL;
		} else {
			collectionSize = CHAIN_COLLECTION_SIZE.LARGE_SOURCE;
		}
		return collectionSize;
	}
	
	private static void appendInit(NonFlatLogic nonFlatLogic, ExecutionContext executionContext, OtclCommand otclCommand) {
		String idxVar = null;
		if (CHAIN_COLLECTION_SIZE.EQUAL == nonFlatLogic.collectionSize) {
			nonFlatLogic.postAnchorCounter++;
			idxVar = AbstractTemplate.SOURCE_IDX + nonFlatLogic.postAnchorCounter;
			otclCommand.appendInitMember(executionContext.targetOCC, executionContext.sourceOCC.otclCommandDto, idxVar,
					false, LogLevel.WARN);
		} else if (CHAIN_COLLECTION_SIZE.LARGE_TARGET == nonFlatLogic.collectionSize) {
			if (nonFlatLogic.targetDescendantsCollectionsCount > nonFlatLogic.initialSourceDescendantsCollectionsCount) {
				otclCommand.appendInitMember(executionContext.targetOCC, null, 0, false, LogLevel.WARN);
			} else if (nonFlatLogic.targetDescendantsCollectionsCount == 1) {
				idxVar = AbstractTemplate.OFFSET_IDX;
				nonFlatLogic.shouldIncrementOffsetIdx = true;
				otclCommand.appendInitMember(executionContext.targetOCC, executionContext.sourceOCC.otclCommandDto, idxVar,
						false, LogLevel.WARN);
			} else {
				nonFlatLogic.postAnchorCounter++;
				idxVar = AbstractTemplate.SOURCE_IDX + nonFlatLogic.postAnchorCounter;
				otclCommand.appendInitMember(executionContext.targetOCC, executionContext.sourceOCC.otclCommandDto, idxVar,
						false, LogLevel.WARN);
			}
		} else {
			if (nonFlatLogic.sourceDescendantsCollectionsCount > nonFlatLogic.initialTargetDescendantsCollectionsCount) {
				if (nonFlatLogic.targetDescendantsCollectionsCount == 1) {
					idxVar = AbstractTemplate.OFFSET_IDX;
					nonFlatLogic.shouldIncrementOffsetIdx = true;
				} else {
					nonFlatLogic.postAnchorCounter++;
					idxVar = AbstractTemplate.SOURCE_IDX + nonFlatLogic.postAnchorCounter;
				}
				otclCommand.appendInitMember(executionContext.targetOCC, executionContext.sourceOCC.otclCommandDto, idxVar,
						false, LogLevel.WARN);
			} else if (nonFlatLogic.targetDescendantsCollectionsCount == 1) {
				idxVar = AbstractTemplate.OFFSET_IDX;
				nonFlatLogic.shouldIncrementOffsetIdx = true;
				otclCommand.appendInitMember(executionContext.targetOCC, executionContext.sourceOCC.otclCommandDto, idxVar,
						false, LogLevel.WARN);
			} else {
				otclCommand.appendInitMember(executionContext.targetOCC, null, 0, false, LogLevel.WARN);
			}
		}
	}
}
