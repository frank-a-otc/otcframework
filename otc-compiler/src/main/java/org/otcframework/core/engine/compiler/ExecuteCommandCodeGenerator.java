/**
* Copyright (c) otcframework.org
*
* @author  Franklin J Abel
* @version 1.0
* @since   2020-06-08 
*
* This file is part of the OTC framework.
* 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      https://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*/
package org.otcframework.core.engine.compiler;

import org.otcframework.common.OtcConstants;
import org.otcframework.common.OtcConstants.LogLevel;
import org.otcframework.common.dto.OtcChainDto;
import org.otcframework.common.dto.OtcCommandDto;
import org.otcframework.common.dto.ScriptDto;
import org.otcframework.common.dto.otc.OtcFileDto.Execute;
import org.otcframework.core.engine.compiler.command.ExecutionContext;
import org.otcframework.core.engine.compiler.command.OtcCommand;
import org.otcframework.core.engine.compiler.command.SourceOtcCommandContext;
import org.otcframework.core.engine.compiler.command.TargetOtcCommandContext;
import org.otcframework.core.engine.compiler.exception.CodeGeneratorException;
import org.otcframework.core.engine.compiler.templates.AbstractTemplate;

/**
 * The Class ExecuteCommandCodeGenerator.
 */
// TODO: Auto-generated Javadoc
final class ExecuteCommandCodeGenerator extends AbstractOtcCodeGenerator {

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
		OtcCommandDto targetOCD = null;
		int idx = 0;
		ScriptDto scriptDto = executionContext.targetOCC.scriptDto;
		OtcChainDto sourceOtcChainDto = scriptDto.sourceOtcChainDto;
		OtcCommandDto sourceOCD = sourceOCC.otcCommandDto;
		if (scriptDto.command.debug) {
			@SuppressWarnings("unused")
			int dummy = 0;
		}
		OtcChainDto targetOtcChainDto = scriptDto.targetOtcChainDto;
		int sourceCollectionCount = sourceOtcChainDto.collectionCount + sourceOtcChainDto.dictionaryCount;
		int targetCollectionCount = targetOtcChainDto.collectionCount + targetOtcChainDto.dictionaryCount;
		if (sourceCollectionCount > 0 && targetCollectionCount > 0) {
			throw new CodeGeneratorException("", "Code Generation failure for OTC-command : " + scriptDto.command.id
					+ ". Execute commmand cannot have collections in both target and source. "
					+ "Any one of them only may have collections.");
		}
		otcCommand.clearCache();
		if (((Execute) scriptDto.command).module != null) {
			otcCommand.appendBeginModuleClass(targetOCC, sourceOCC, targetClz, sourceClz, true);
		} else {
			otcCommand.appendBeginClass(targetOCC, sourceOCC, targetClz, sourceClz, true);
		}
		// -- source
		if (!sourceOCC.isLeaf()) {
			if (sourceCollectionCount > 0) {
				if (!sourceOCD.isCollectionOrMap()) {
					sourceOCD = OtcCommand.retrieveNextCollectionOrMapOCD(sourceOCC);
					sourceOCC.otcCommandDto = sourceOCD;
				}
				while (true) {
					otcCommand.appendForLoop(targetOCC, sourceOCC, AbstractTemplate.SOURCE_IDX, false, LogLevel.WARN);
					sourceOCD = sourceOCC.otcCommandDto;
					if (!sourceOCC.hasDescendantCollectionOrMap()) {
						sourceOCD = OtcCommand.retrieveNextOCD(sourceOCC);
						sourceOCC.otcCommandDto = sourceOCD;
						break;
					}
					sourceOCD = OtcCommand.retrieveNextCollectionOrMapOCD(sourceOCC);
					sourceOCC.otcCommandDto = sourceOCD;
				}
			}
			if (!sourceOCD.isCollectionOrMapMember()) {
				while (true) {
					if (sourceCollectionCount > 0) {
						otcCommand.appendIfNullSourceContinue(targetOCC, sourceOCC, LogLevel.WARN);
					} else {
						otcCommand.appendIfNullSourceReturn(targetOCC, sourceOCC, idx, LogLevel.WARN);
					}
					if (sourceOCC.isLeaf()) {
						break;
					}
					sourceOCD = OtcCommand.retrieveNextOCD(sourceOCC);
					sourceOCC.otcCommandDto = sourceOCD;
				}
			}
		}
		targetOCD = targetOCC.otcCommandDto;
		if (!targetOCC.isLeaf()) {
			if (targetCollectionCount > 0) {
				while (true) {
					if (targetOCD.isCollectionOrMap()) {
						otcCommand.appendForLoop(targetOCC, AbstractTemplate.TARGET_IDX, false, LogLevel.WARN);
						targetOCD = OtcCommand.retrieveNextOCD(targetOCC);
						targetOCC.otcCommandDto = targetOCD;
					}
					if (targetOCC.hasDescendantCollectionOrMap() || targetOCD.isCollectionOrMap()) {
						otcCommand.appendInitUptoNextCollectionWithReturnOrContinue(targetOCC, LogLevel.WARN);
						targetOCD = targetOCC.otcCommandDto;
						continue;
					} else {
						targetOCD = OtcCommand.retrieveNextOCD(targetOCC);
						break;
					}
				}
			}
			if (!targetOCD.isCollectionOrMapMember()) {
				while (true) {
					otcCommand.appendInit(targetOCC, null, false, LogLevel.WARN);
					if (targetOCC.isLeaf()) {
						break;
					}
					targetOCD = OtcCommand.retrieveNextOCD(targetOCC);
					targetOCC.otcCommandDto = targetOCD;
				}
			}
		}
		// innermost loop - if null continue code.
		if (scriptDto.hasExecutionOrder) {
//			for (String execOrd : ((Execute) scriptDto.command).executionOrder) {
			(((Execute) scriptDto.command).executionOrder).forEach(execOrd -> {
				if (OtcConstants.EXECUTE_OTC_CONVERTER.equals(execOrd)) {
					otcCommand.appendExecuteConverter(targetOCC, sourceOCC, false);
				} else if (OtcConstants.EXECUTE_OTC_MODULE.equals(execOrd)) {
					otcCommand.appendExecuteModule(targetOCC, sourceOCC, false);
				}
			});
		} else {
			if (((Execute) scriptDto.command).converter != null) {
				otcCommand.appendExecuteConverter(targetOCC, sourceOCC, false);
			}
			if (((Execute) scriptDto.command).module != null) {
				otcCommand.appendExecuteModule(targetOCC, sourceOCC, false);
			}
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
		otcCommand.createJavaFile(targetOCC, targetClz, sourceClz);
		return;
	}
}
