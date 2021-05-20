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

import org.otcl2.common.OtclConstants;
import org.otcl2.common.OtclConstants.LogLevel;
import org.otcl2.common.dto.OtclChainDto;
import org.otcl2.common.dto.OtclCommandDto;
import org.otcl2.common.dto.ScriptDto;
import org.otcl2.common.dto.otcl.OtclFileDto.Execute;
import org.otcl2.core.engine.compiler.command.ExecutionContext;
import org.otcl2.core.engine.compiler.command.OtclCommand;
import org.otcl2.core.engine.compiler.command.SourceOtclCommandContext;
import org.otcl2.core.engine.compiler.command.TargetOtclCommandContext;
import org.otcl2.core.engine.compiler.exception.CodeGeneratorException;
import org.otcl2.core.engine.compiler.templates.AbstractTemplate;

// TODO: Auto-generated Javadoc
/**
 * The Class ExecuteCommandCodeGenerator.
 */
final class ExecuteCommandCodeGenerator extends AbstractOtclCodeGenerator {

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
		OtclCommandDto targetOCD = null;
		int idx = 0;
		ScriptDto scriptDto = executionContext.targetOCC.scriptDto;
		OtclChainDto sourceOtclChainDto = scriptDto.sourceOtclChainDto;
		OtclCommandDto sourceOCD = sourceOCC.otclCommandDto;
		if (scriptDto.command.debug) {
			@SuppressWarnings("unused")
			int dummy = 0;
		}
		OtclChainDto targetOtclChainDto = scriptDto.targetOtclChainDto;
		int sourceCollectionCount = sourceOtclChainDto.collectionCount + sourceOtclChainDto.dictionaryCount;
		int targetCollectionCount = targetOtclChainDto.collectionCount + targetOtclChainDto.dictionaryCount;
		if (sourceCollectionCount > 0 && targetCollectionCount > 0) {
			throw new CodeGeneratorException("", "Code Generation failure for OTCL-command : " + scriptDto.command.id + 
					". Extensions are not applicable when both target and source contain collections.");
		}
		otclCommand.clearCache();
		if (((Execute) scriptDto.command).otclModule != null) {
			otclCommand.appendBeginModuleClass(targetOCC, sourceOCC, targetClz, sourceClz, true);
		} else {
			otclCommand.appendBeginClass(targetOCC, sourceOCC, targetClz, sourceClz, true);
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
					otclCommand.appendInit(targetOCC, null, false, LogLevel.WARN);
					if (targetOCC.isLeaf()) {
						break;
					}
					targetOCD = OtclCommand.retrieveNextOCD(targetOCC);
					targetOCC.otclCommandDto = targetOCD;
				}
			}
		}
		// innermost loop - if null continue code.
		if (scriptDto.hasExecutionOrder) {
			for (String execOrd : ((Execute) scriptDto.command).executionOrder) {
				if (OtclConstants.EXECUTE_OTCL_CONVERTER.equals(execOrd)) {
					otclCommand.appendExecuteConverter(targetOCC, sourceOCC, false); 
				} else {
					otclCommand.appendExecuteModule(targetOCC, sourceOCC, false); 
				}
			}
		} else {
			if (((Execute) scriptDto.command).otclConverter != null) {
				otclCommand.appendExecuteConverter(targetOCC, sourceOCC, false); 
			} 
			if (((Execute) scriptDto.command).otclModule != null) {
				otclCommand.appendExecuteModule(targetOCC, sourceOCC, false); 
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
		otclCommand.createJavaFile(targetOCC, targetClz, sourceClz);
		return ;
	}

}
