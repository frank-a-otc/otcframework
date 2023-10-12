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
package org.otcframework.compiler.templates;

import org.otcframework.common.OtcConstants.TARGET_SOURCE;
import org.otcframework.common.dto.OtcCommandDto;
import org.otcframework.common.util.CommonUtils;
import org.otcframework.common.util.PackagesFilterUtil;
import org.otcframework.compiler.command.TargetOtcCommandContext;

import java.util.Map;
import java.util.Set;

/**
 * The Class IfNullCreateAndSetTemplate.
 */
public final class IfNullCreateAndSetTemplate extends AbstractTemplate {

	private static final String inlineComments = "\n// ---- generator - " +
			IfNullCreateAndSetTemplate.class.getSimpleName() + "\n";

	/**
	 * Instantiates a new if null create and set template.
	 */
	private IfNullCreateAndSetTemplate() {
	}

	/**
	 * Generate code.
	 *
	 * @param targetOCC        the target OCC
	 * @param value            the value
	 * @param arraySize        the array size
	 * @param createNewVarName the create new var name
	 * @param varNamesSet      the var names set
	 * @param varNamesMap      the var names map
	 * @return the string
	 */
	public static String generateCode(TargetOtcCommandContext targetOCC, String value, Integer arraySize,
			boolean createNewVarName, Set<String> varNamesSet, Map<String, String> varNamesMap) {
		OtcCommandDto targetOCD = targetOCC.otcCommandDto;
		String concreteType = fetchConcreteTypeName(targetOCC, targetOCD);
		String varName = createVarName(targetOCD, createNewVarName, varNamesSet, varNamesMap);
		if (targetOCD.isArray()) {
			if (TARGET_SOURCE.TARGET == targetOCD.enumTargetSource) {
				if (arraySize != null) {
					concreteType = concreteType.replace("[]", "[" + arraySize + "]");
				} else {
					concreteType = concreteType.replace("[]", "[" + 1 + "]");
				}
			}
		}
		String parentVarName = null;
		if (targetOCD.isFirstNode) {
			parentVarName = CommonUtils.initLower(targetOCD.field.getDeclaringClass().getSimpleName());
		} else {
			parentVarName = createVarName(targetOCD.parent, createNewVarName, varNamesSet, varNamesMap);
		}
		String ifNullSetterCode = "";
		String setter = targetOCD.setter;
		if (PackagesFilterUtil.isFilteredPackage(targetOCD.fieldType) || targetOCD.isCollectionOrMap()) {
			if (targetOCD.enableSetterHelper) {
				String helper = targetOCC.factoryClassDto.addImport(targetOCC.helper);
				ifNullSetterCode = String.format(ifNullCreateAndHelperSetTemplate, varName, varName, concreteType,
						helper, setter, parentVarName, varName);
			} else {
				if (targetOCD.isEnum()) {
					value = createConvertExpression(targetOCD, value);
					ifNullSetterCode = String.format(ifNullEnumCreateAndSetTemplate, varName, varName, concreteType,
							value, parentVarName, setter, varName);
				} else {
					ifNullSetterCode = String.format(ifNullCreateAndSetTemplate, varName, varName, concreteType,
							parentVarName, setter, varName);
				}
			}
			if (targetOCD.isArray()) {
				ifNullSetterCode = ifNullSetterCode.replace("]()", "]");
			}
		}
		return addInlineComments(inlineComments, ifNullSetterCode);
	}
}
