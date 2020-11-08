package org.otcl2.core.engine.compiler;

import org.otcl2.common.OtclConstants;
import org.otcl2.common.OtclConstants.ALGORITHM_ID;
import org.otcl2.common.OtclConstants.LogLevel;
import org.otcl2.common.dto.OtclChainDto;
import org.otcl2.common.dto.OtclCommandDto;
import org.otcl2.common.dto.OtclFileDto.Execute;
import org.otcl2.common.dto.ScriptDto;
import org.otcl2.core.engine.compiler.command.ExecutionContext;
import org.otcl2.core.engine.compiler.command.OtclCommand;
import org.otcl2.core.engine.compiler.command.SourceOtclCommandContext;
import org.otcl2.core.engine.compiler.command.TargetOtclCommandContext;
import org.otcl2.core.engine.compiler.exception.CodeGeneratorException;
import org.otcl2.core.engine.compiler.templates.AbstractTemplate;

final class ExecuteCommandCodeGenerator extends AbstractOtclCodeGenerator {

	public static void generateSourceCode(ExecutionContext executionContext) {
		
//		ScriptGroupDto scriptGroupDto = executionContext.entry.getValue();
//		for (ScriptDto scriptDto : scriptGroupDto.scriptDtos) {
		ScriptDto scriptDto = executionContext.targetOCC.scriptDto;
			
			if (scriptDto.hasExecutionOrder) {
				for (String execOrd : ((Execute) scriptDto.command).executionOrder) {
					ScriptDto clonedScriptDto = scriptDto.clone();
					if (OtclConstants.EXECUTE_OTCL_CONVERTER.equals(execOrd)) {
						clonedScriptDto.hasExecuteModule = false;
						clonedScriptDto.hasExecuteConverter = true;
						executionContext.targetOCC.algorithmId = ALGORITHM_ID.CONVERTER;
					} else {
						clonedScriptDto.hasExecuteModule = true;
						clonedScriptDto.hasExecuteConverter = false;
						executionContext.targetOCC.algorithmId = ALGORITHM_ID.MODULE;
					}
					executionContext.targetOCC.scriptDto = clonedScriptDto;
				}
			} else {
				executionContext.targetOCC.scriptDto = scriptDto;
				if (scriptDto.hasExecuteConverter) {
					executionContext.targetOCC.algorithmId = ALGORITHM_ID.CONVERTER;
					generateCodeForModuleAndConverter(executionContext);
				}
				if (scriptDto.hasExecuteModule) {
					executionContext.targetOCC.algorithmId = ALGORITHM_ID.MODULE;
					generateCodeForModuleAndConverter(executionContext);
				}
			}
//		}
		return;
	}
	
	private static void generateCodeForModuleAndConverter(ExecutionContext executionContext) {
//		Entry<String, ScriptGroupDto> entry = executionContext.entry;
		OtclCommand otclCommand = executionContext.otclCommand;
		Class<?> targetClz = executionContext.targetClz;
		TargetOtclCommandContext targetOCC = executionContext.targetOCC;
		Class<?> sourceClz = executionContext.sourceClz; 
		SourceOtclCommandContext sourceOCC = executionContext.sourceOCC;
//		List<JavaFileObject> javaFileObjects = executionContext.javaFileObjects;
		
//		ScriptGroupDto scriptGroupDto = entry.getValue();
//		List<ScriptDto> scriptDtos = scriptGroupDto.scriptDtos;
		OtclCommandDto targetOCD = null;
		int idx = 0;
//		ScriptDto scriptDto = scriptDtos.get(0);
		ScriptDto scriptDto = executionContext.targetOCC.scriptDto;
		OtclChainDto sourceOtclChainDto = scriptDto.sourceOtclChainDto;
		OtclCommandDto sourceOCD = sourceOCC.otclCommandDto;
		resetOCC(sourceOCC, scriptDto);
		if (scriptDto.command.debug) {
			@SuppressWarnings("unused")
			int dummy = 0;
		}
		OtclChainDto targetOtclChainDto = scriptDto.targetOtclChainDto;
		int sourceCollectionCount = sourceOtclChainDto.collectionCount + sourceOtclChainDto.dictionaryCount;
		int targetCollectionCount = targetOtclChainDto.collectionCount + targetOtclChainDto.dictionaryCount;
		if (sourceCollectionCount > 0 && targetCollectionCount > 0) {
			throw new CodeGeneratorException("", "Code Generation failure for Script-block : " + scriptDto.command.id + 
					". Extensions are not applicable when both target and source contain collections.");
		}
		otclCommand.clearCache();
		if (ALGORITHM_ID.CONVERTER == targetOCC.algorithmId) {
			otclCommand.appendBeginClass(targetOCC, sourceOCC, targetClz, sourceClz, true);
		} else if (ALGORITHM_ID.MODULE == targetOCC.algorithmId) {
			otclCommand.appendBeginModuleClass(targetOCC, sourceOCC, targetClz, sourceClz, true);
		}
		// -- source
		if (!sourceOCC.isLeaf()) {
			if (sourceCollectionCount > 0) {
				if (!sourceOCD.isCollectionOrMap()) {
					sourceOCD = OtclCommand.retrieveNextCollectionOrMapOCD(sourceOCC);
					sourceOCC.otclCommandDto = sourceOCD; 
				}
				while (true) {
					otclCommand.appendForLoop(targetOCC, sourceOCC, AbstractTemplate.SOURCE_IDX, false, LogLevel.WARN);
					sourceOCD = sourceOCC.otclCommandDto;
					if (!sourceOCC.hasDescendantCollectionOrMap()) {
						sourceOCD = OtclCommand.retrieveNextOCD(sourceOCC);
						sourceOCC.otclCommandDto = sourceOCD;
						break;
					}
					sourceOCD = OtclCommand.retrieveNextCollectionOrMapOCD(sourceOCC);
					sourceOCC.otclCommandDto = sourceOCD;
				}
			}
			if (!sourceOCD.isCollectionOrMapMember()) {
				while (true) {
					if (sourceCollectionCount > 0) {
						otclCommand.appendIfNullSourceContinue(targetOCC, sourceOCC, LogLevel.WARN);
					} else {
						otclCommand.appendIfNullSourceReturn(targetOCC, sourceOCC, idx, LogLevel.WARN);
					}
					if (sourceOCC.isLeaf()) {
						break;
					}
					sourceOCD = OtclCommand.retrieveNextOCD(sourceOCC);
					sourceOCC.otclCommandDto = sourceOCD;
				}
			}
		} 
		targetOCD = targetOCC.otclCommandDto;
		if (!targetOCC.isLeaf()) {
			if (targetCollectionCount > 0) {
				while (true) {
					if (targetOCD.isCollectionOrMap()) {
						otclCommand.appendForLoop(targetOCC, AbstractTemplate.TARGET_IDX, false, LogLevel.WARN);
						targetOCD = OtclCommand.retrieveNextOCD(targetOCC);
						targetOCC.otclCommandDto = targetOCD;
					}
					if (targetOCC.hasDescendantCollectionOrMap() || targetOCD.isCollectionOrMap()) {
						otclCommand.appendInitUptoNextCollectionWithReturnOrContinue(targetOCC, LogLevel.WARN);
						targetOCD = targetOCC.otclCommandDto;
						continue;
					} else {
						targetOCD = OtclCommand.retrieveNextOCD(targetOCC);
						break;
					}
				}
			}
			if (!targetOCD.isCollectionOrMapMember()) {
				while (true) {
					otclCommand.appendInit(targetOCC, false, LogLevel.WARN);
					if (targetOCC.isLeaf()) {
						break;
					}
					targetOCD = OtclCommand.retrieveNextOCD(targetOCC);
				}
			}
		}
		// innermost loop - if null continue code.
		if (ALGORITHM_ID.CONVERTER == targetOCC.algorithmId) {
			otclCommand.appendExecuteConverter(targetOCC, sourceOCC, false); 
		} 
		if (ALGORITHM_ID.MODULE == targetOCC.algorithmId) {
			otclCommand.appendExecuteModule(targetOCC, sourceOCC, false); 
		} 
		int loopCounter = 0;
		if (sourceCollectionCount > 0) {
			loopCounter = sourceCollectionCount;
		} else if (targetCollectionCount > 0) {
			loopCounter = targetCollectionCount;
		}
		if (loopCounter > 0) {
			for (int bracesIdx = 0; bracesIdx < loopCounter; bracesIdx++) {
				targetOCC.appendCode("\n}");
			}
		}
		otclCommand.createJavaFile(targetOCC, targetClz, sourceClz);
//		addJavaStringObject(javaFileObjects, javaStringObject);
		return ;
	}

}
