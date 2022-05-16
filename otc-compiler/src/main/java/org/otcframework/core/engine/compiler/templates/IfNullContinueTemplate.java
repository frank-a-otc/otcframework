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
package org.otcframework.core.engine.compiler.templates;

import java.util.Map;
import java.util.Set;

import org.otcframework.common.OtcConstants;
import org.otcframework.common.OtcConstants.LogLevel;
import org.otcframework.common.OtcConstants.TARGET_SOURCE;
import org.otcframework.common.dto.OtcCommandDto;
import org.otcframework.common.util.CommonUtils;
import org.otcframework.core.engine.compiler.command.SourceOtcCommandContext;
import org.otcframework.core.engine.compiler.command.TargetOtcCommandContext;

/**
 * The Class IfNullContinueTemplate.
 */
// TODO: Auto-generated Javadoc
public class IfNullContinueTemplate extends AbstractTemplate {

	/**
	 * Instantiates a new if null continue template.
	 */
	private IfNullContinueTemplate() {
	}

	/**
	 * Generate code.
	 *
	 * @param targetOCC        the target OCC
	 * @param sourceOCC        the source OCC
	 * @param createNewVarName the create new var name
	 * @param logLevel         the log level
	 * @param varNamesSet      the var names set
	 * @param varNamesMap      the var names map
	 * @return the string
	 */
	public static String generateCode(TargetOtcCommandContext targetOCC, SourceOtcCommandContext sourceOCC,
			boolean createNewVarName, LogLevel logLevel, Set<String> varNamesSet, Map<String, String> varNamesMap) {
		OtcCommandDto otcCommandDto = null;
		if (sourceOCC != null) {
			otcCommandDto = sourceOCC.otcCommandDto;
		} else {
			otcCommandDto = targetOCC.otcCommandDto;
		}
		String fieldType = fetchFieldTypeName(targetOCC, sourceOCC, otcCommandDto, createNewVarName, varNamesMap);
		String varName = createVarName(otcCommandDto, createNewVarName, varNamesSet, varNamesMap);
		String parentVarName = null;
		if (otcCommandDto.isFirstNode) {
			parentVarName = CommonUtils.initLower(otcCommandDto.field.getDeclaringClass().getSimpleName());
		} else {
			OtcCommandDto parentOCD = otcCommandDto.parent;
			parentVarName = createVarName(parentOCD, createNewVarName, varNamesSet, varNamesMap);
		}
		String getter = otcCommandDto.getter;
		String ifNotNullParentChildGetterCode = null;
		boolean hasMapValueInPath = targetOCC.hasMapValueMember() || targetOCC.hasMapValueDescendant();
		String logMsg = null;
		if (TARGET_SOURCE.SOURCE == otcCommandDto.enumTargetSource || !hasMapValueInPath) {
			logMsg = "'" + otcCommandDto.tokenPath + "' is null!.";
		} else {
			int endIdx = targetOCC.otcChain.lastIndexOf(OtcConstants.MAP_VALUE_REF) + 3;
			String mapValueTokenPath = targetOCC.otcChain.substring(0, endIdx);
			logMsg = "Corresponding Map-key missing for path: '" + mapValueTokenPath + "'!";
		}
		if (otcCommandDto.enableGetterHelper) {
			String helper = targetOCC.factoryClassDto.addImport(targetOCC.helper);
			ifNotNullParentChildGetterCode = String.format(helperGetIfNullContinueTemplate, fieldType, varName, helper,
					getter, parentVarName, varName, logLevel, logMsg);
		} else {
			ifNotNullParentChildGetterCode = String.format(getterIfNullContinueTemplate, fieldType, varName,
					parentVarName, getter, varName, logLevel, logMsg);
		}
		return ifNotNullParentChildGetterCode;
	}
}
