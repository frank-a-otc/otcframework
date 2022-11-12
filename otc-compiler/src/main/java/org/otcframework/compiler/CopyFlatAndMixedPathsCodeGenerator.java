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
package org.otcframework.compiler;

import org.otcframework.common.OtcConstants.ALGORITHM_ID;
import org.otcframework.common.OtcConstants.LogLevel;
import org.otcframework.common.dto.OtcCommandDto;
import org.otcframework.common.dto.ScriptDto;
import org.otcframework.compiler.command.ExecutionContext;
import org.otcframework.compiler.command.OtcCommand;
import org.otcframework.compiler.command.SourceOtcCommandContext;
import org.otcframework.compiler.command.TargetOtcCommandContext;

/**
 * The Class CopyFlatAndMixedPathsCodeGenerator.
 */
// TODO: Auto-generated Javadoc
final class CopyFlatAndMixedPathsCodeGenerator extends AbstractOtcCodeGenerator {

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
		OtcCommandDto targetOCD = null;
		TargetOtcCommandContext clonedTargetOCC = null;
		targetOCC.algorithmId = ALGORITHM_ID.FLAT;
		OtcCommandDto sourceOCD = sourceOCC.otcCommandDto;
		if (scriptDto.command.debug) {
			@SuppressWarnings("unused")
			int dummy = 0;
		}
		otcCommand.clearCache();
		boolean addLogger = true;
		clonedTargetOCC = targetOCC.clone();
		otcCommand.appendBeginClass(clonedTargetOCC, sourceOCC, targetClz, sourceClz, addLogger);
		if (sourceOCC.hasDescendantCollectionOrMap() && !sourceOCD.isCollectionOrMap()) {
			sourceOCD = OtcCommand.retrieveNextCollectionOrMapOCD(sourceOCC);
			sourceOCC.otcCommandDto = sourceOCD;
		}
		boolean createNewVarName = false;
		while (true) {
			if (sourceOCC.hasDescendantCollectionOrMap() || sourceOCD.isCollectionOrMap()) {
				otcCommand.appendRetrieveNextSourceCollectionOrMapParent(clonedTargetOCC, sourceOCC, 0,
						createNewVarName, LogLevel.WARN);
				sourceOCD = sourceOCC.otcCommandDto;
			} else {
				otcCommand.appendIfNullSourceReturn(clonedTargetOCC, sourceOCC, 0, LogLevel.WARN);
			}
			if (sourceOCC.isLeaf()) {
				break;
			}
			if (sourceOCC.hasDescendantCollectionOrMap()) {
				sourceOCD = OtcCommand.retrieveNextCollectionOrMapOCD(sourceOCC);
			} else {
				sourceOCD = OtcCommand.retrieveNextOCD(sourceOCC);
			}
			sourceOCC.otcCommandDto = sourceOCD;
		}
		// --- start code-generation for target.
		targetOCD = clonedTargetOCC.otcCommandDto;
		otcCommand.clearTargetCache();
		boolean uptoLeafParent = true;
		otcCommand.appendInitUptoAnchoredOrLastCollectionOrLeaf(clonedTargetOCC, 0, uptoLeafParent, LogLevel.WARN);
		targetOCD = clonedTargetOCC.otcCommandDto;
		if (targetOCD.isCollectionOrMap()) {
			targetOCD = OtcCommand.retrieveMemberOCD(clonedTargetOCC);
			clonedTargetOCC.otcCommandDto = targetOCD;
		}
		if (clonedTargetOCC.hasChildren()) {
			targetOCD = OtcCommand.retrieveNextOCD(clonedTargetOCC);
			clonedTargetOCC.otcCommandDto = targetOCD;
		}
		while (clonedTargetOCC.hasChildren()) {
			if (targetOCD.isCollectionOrMapMember()) {
				if (targetOCD.isMapMember()) {
					Integer idx = null;
					otcCommand.appendInitMember(clonedTargetOCC, null, idx, false, LogLevel.WARN);
				} else {
					otcCommand.appendInitMember(clonedTargetOCC, null, 0, false, LogLevel.WARN);
				}
			} else {
				otcCommand.appendInit(clonedTargetOCC, null, false, LogLevel.WARN);
				targetOCD = clonedTargetOCC.otcCommandDto;
				if (targetOCD.isCollectionOrMap()) {
					continue;
				}
			}
			targetOCD = OtcCommand.retrieveNextOCD(clonedTargetOCC);
			clonedTargetOCC.otcCommandDto = targetOCD;
		}
		if (targetOCD.isCollectionOrMapMember()) {
			if (targetOCD.isMapKey()) {
				otcCommand.appendAddMapKey(clonedTargetOCC, sourceOCD, null, 0);
			} else if (targetOCD.isMapValue()) {
				otcCommand.appendAddMapValue(clonedTargetOCC, sourceOCC, null, 0, LogLevel.WARN);
			} else if (targetOCD.isCollectionMember()) {
				otcCommand.appendAddToCollection(clonedTargetOCC, sourceOCD, null, null);
			}
		} else {
			otcCommand.appendGetSet(clonedTargetOCC, sourceOCC, false);
		}
		otcCommand.createJavaFile(clonedTargetOCC, targetClz, sourceClz);
		return;
	}
}
