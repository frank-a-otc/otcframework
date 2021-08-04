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
import org.otcframework.core.engine.compiler.command.ExecutionContext;
import org.otcframework.core.engine.compiler.command.OtcCommand;
import org.otcframework.core.engine.compiler.command.SourceOtcCommandContext;
import org.otcframework.core.engine.compiler.command.TargetOtcCommandContext;

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
