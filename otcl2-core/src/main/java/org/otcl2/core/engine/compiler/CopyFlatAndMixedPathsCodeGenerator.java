package org.otcl2.core.engine.compiler;

import org.otcl2.common.OtclConstants.ALGORITHM_ID;
import org.otcl2.common.OtclConstants.LogLevel;
import org.otcl2.common.dto.OtclCommandDto;
import org.otcl2.common.dto.ScriptDto;
import org.otcl2.core.engine.compiler.command.ExecutionContext;
import org.otcl2.core.engine.compiler.command.OtclCommand;
import org.otcl2.core.engine.compiler.command.SourceOtclCommandContext;
import org.otcl2.core.engine.compiler.command.TargetOtclCommandContext;

final class CopyFlatAndMixedPathsCodeGenerator extends AbstractOtclCodeGenerator {

	public static void generateSourceCode(ExecutionContext executionContext) {
//		Entry<String, ScriptGroupDto> entry = executionContext.entry;
		OtclCommand otclCommand = executionContext.otclCommand;
		Class<?> targetClz = executionContext.targetClz;
		TargetOtclCommandContext targetOCC = executionContext.targetOCC;
		Class<?> sourceClz = executionContext.sourceClz; 
		SourceOtclCommandContext sourceOCC = executionContext.sourceOCC;
//		List<JavaFileObject> javaFileObjects = executionContext.javaFileObjects;
		
//		ScriptGroupDto scriptGroupDto = entry.getValue();
//		List<ScriptDto> scriptDtos = scriptGroupDto.scriptDtos;
//		ScriptDto firstScriptDto = scriptDtos.get(0);
//		resetOCC(sourceOCC, firstScriptDto);
		ScriptDto scriptDto = executionContext.targetOCC.scriptDto;
		resetOCC(sourceOCC, scriptDto);

		OtclCommandDto targetOCD = null;
		TargetOtclCommandContext clonedTargetOCC = null;
		targetOCC.algorithmId = ALGORITHM_ID.FLAT;
//		for (ScriptDto scriptDto : scriptDtos) {
			OtclCommandDto sourceOCD = sourceOCC.otclCommandDto;
			resetOCC(sourceOCC, scriptDto);
			if (scriptDto.command.debug) {
				@SuppressWarnings("unused")
				int dummy = 0;
			}
			otclCommand.clearCache();
			boolean addLogger = true;
			resetOCC(targetOCC, scriptDto);
			clonedTargetOCC = targetOCC.clone();
			otclCommand.appendBeginClass(clonedTargetOCC, sourceOCC, targetClz, sourceClz, addLogger);
			if (sourceOCC.hasDescendantCollectionOrMap() && !sourceOCD.isCollectionOrMap()) {
				sourceOCD = OtclCommand.retrieveNextCollectionOrMapOCD(sourceOCC);
				sourceOCC.otclCommandDto = sourceOCD;
			}
			boolean createNewVarName = false;
			while (true) {
				if (sourceOCC.hasDescendantCollectionOrMap() || sourceOCD.isCollectionOrMap()) {
					otclCommand.appendRetrieveNextSourceCollectionOrMapParent(clonedTargetOCC, sourceOCC, 0, createNewVarName,
							LogLevel.WARN);
					sourceOCD = sourceOCC.otclCommandDto;
				} else {
					otclCommand.appendIfNullSourceReturn(clonedTargetOCC, sourceOCC, 0, LogLevel.WARN); 
				}
				if (sourceOCC.isLeaf()) {
					break;
				}
				if (sourceOCC.hasDescendantCollectionOrMap()) {
					sourceOCD = OtclCommand.retrieveNextCollectionOrMapOCD(sourceOCC);
				} else {
					sourceOCD = OtclCommand.retrieveNextOCD(sourceOCC);
				}
				sourceOCC.otclCommandDto = sourceOCD;
			}
			// --- start code-generation for target.
			targetOCD = clonedTargetOCC.otclCommandDto;
			otclCommand.clearTargetCache();
			boolean uptoLeafParent = true;
			otclCommand.appendInitUptoAnchoredOrLastCollectionOrLeaf(clonedTargetOCC, 0, uptoLeafParent, LogLevel.WARN);
			targetOCD = clonedTargetOCC.otclCommandDto;
			if (targetOCD.isCollectionOrMap()) {
				targetOCD = OtclCommand.retrieveMemberOCD(clonedTargetOCC);
				clonedTargetOCC.otclCommandDto = targetOCD;
			}
			if (clonedTargetOCC.hasChildren()) {
				targetOCD = OtclCommand.retrieveNextOCD(clonedTargetOCC);
				clonedTargetOCC.otclCommandDto = targetOCD;
			}
			while (clonedTargetOCC.hasChildren()) {
				if (targetOCD.isCollectionOrMapMember()) {
					if (targetOCD.isMapMember()) {
						Integer idx = null;
						otclCommand.appendInitMember(clonedTargetOCC, null, idx, false, LogLevel.WARN);
					} else {
						otclCommand.appendInitMember(clonedTargetOCC, null, 0, false, LogLevel.WARN);
					}
				} else {
					otclCommand.appendInit(clonedTargetOCC, false, LogLevel.WARN);
					targetOCD = clonedTargetOCC.otclCommandDto;
					if (targetOCD.isCollectionOrMap()) {
						continue;
					}
				}
				targetOCD = OtclCommand.retrieveNextOCD(clonedTargetOCC);
				clonedTargetOCC.otclCommandDto = targetOCD;
			}
			if (targetOCD.isCollectionOrMapMember()) {
				if (targetOCD.isMapKey()) {
					otclCommand.appendAddMapKey(clonedTargetOCC, sourceOCD, null, 0);
				} else if (targetOCD.isMapValue()) {
					otclCommand.appendAddMapValue(clonedTargetOCC, sourceOCD, null, 0, LogLevel.WARN);
				} else if (targetOCD.isCollectionMember()) {
					otclCommand.appendAddToCollection(clonedTargetOCC, sourceOCD, null, null);
				}
			} else {
				otclCommand.appendGetSet(clonedTargetOCC, sourceOCC, false);
			}
			otclCommand.createJavaFile(clonedTargetOCC, targetClz, sourceClz);
//			addJavaStringObject(javaFileObjects, javaStringObject);
//		}
		return;
	}
}
