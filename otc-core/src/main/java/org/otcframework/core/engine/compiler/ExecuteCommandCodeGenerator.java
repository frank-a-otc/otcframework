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

// TODO: Auto-generated Javadoc
/**
 * The Class ExecuteCommandCodeGenerator.
 */
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
			throw new CodeGeneratorException("", "Code Generation failure for OTC-command : " + scriptDto.command.id + 
					". Extensions are not applicable when both target and source contain collections.");
		}
		otcCommand.clearCache();
		if (((Execute) scriptDto.command).otcModule != null) {
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
			for (String execOrd : ((Execute) scriptDto.command).executionOrder) {
				if (OtcConstants.EXECUTE_OTC_CONVERTER.equals(execOrd)) {
					otcCommand.appendExecuteConverter(targetOCC, sourceOCC, false); 
				} else {
					otcCommand.appendExecuteModule(targetOCC, sourceOCC, false); 
				}
			}
		} else {
			if (((Execute) scriptDto.command).otcConverter != null) {
				otcCommand.appendExecuteConverter(targetOCC, sourceOCC, false); 
			} 
			if (((Execute) scriptDto.command).otcModule != null) {
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
		return ;
	}

}
