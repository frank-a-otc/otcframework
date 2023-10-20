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
import org.otcframework.compiler.command.TargetOtcCommandContext;
import org.otcframework.compiler.exception.CodeGeneratorException;

import java.util.Map;
import java.util.Set;

/**
 * The Class GetterIfNullCreateSetTemplate.
 */
public final class GetterIfNullCreateSetTemplate extends AbstractTemplate {

	private static final String inlineComments = "\n// ---- generator - " +
			GetterIfNullCreateSetTemplate.class.getSimpleName() + "\n";
	/**
	 * Instantiates a new getter if null create set template.
	 */
	private GetterIfNullCreateSetTemplate() {
	}

	/**
	 * Generate code.
	 *
	 * @param targetOCC        the target OCC
	 * @param otcCommandDto    the otc command dto
	 * @param createNewVarName the create new var name
	 * @param varNamesSet      the var names set
	 * @param varNamesMap      the var names map
	 * @return the string
	 */
	public static String generateCode(TargetOtcCommandContext targetOCC, OtcCommandDto otcCommandDto,
			boolean createNewVarName, Set<String> varNamesSet, Map<String, String> varNamesMap) {
		if (otcCommandDto.isArray()) {
			throw new CodeGeneratorException("", "Invalid call to method in OTC-command : " + targetOCC.commandId
					+ ". Type should not be an array.");
		}
		return generateCode(targetOCC, otcCommandDto, null, null, createNewVarName, varNamesSet, varNamesMap);
	}

	/**
	 * Generate code for array.
	 *
	 * @param targetOCC        the target OCC
	 * @param otcCommandDto    the otc command dto
	 * @param arraySize        the array size
	 * @param createNewVarName the create new var name
	 * @param varNamesSet      the var names set
	 * @param varNamesMap      the var names map
	 * @return the string
	 */
	public static String generateCodeForArray(TargetOtcCommandContext targetOCC, OtcCommandDto otcCommandDto,
			Integer arraySize, boolean createNewVarName, Set<String> varNamesSet, Map<String, String> varNamesMap) {
		if (!otcCommandDto.isArray()) {
			throw new CodeGeneratorException("",
					"Invalid call to method in OTC-command : " + targetOCC.commandId + ". Type should be an array.");
		}
		return generateCode(targetOCC, otcCommandDto, null, arraySize, createNewVarName, varNamesSet, varNamesMap);
	}

	/**
	 * Generate code for enum.
	 *
	 * @param targetOCC        the target OCC
	 * @param otcCommandDto    the otc command dto
	 * @param value            the value
	 * @param arraySize        the array size
	 * @param createNewVarName the create new var name
	 * @param varNamesSet      the var names set
	 * @param varNamesMap      the var names map
	 * @return the string
	 */
	public static String generateCodeForEnum(TargetOtcCommandContext targetOCC, OtcCommandDto otcCommandDto,
			String value, Integer arraySize, boolean createNewVarName, Set<String> varNamesSet,
			Map<String, String> varNamesMap) {
		if (!otcCommandDto.isEnum()) {
			throw new CodeGeneratorException("",
					"Invalid call to method in OTC-command : " + targetOCC.commandId + ". Type should be an array.");
		}
		return generateCode(targetOCC, otcCommandDto, value, arraySize, createNewVarName, varNamesSet, varNamesMap);
	}

	/**
	 * Generate code.
	 *
	 * @param targetOCC        the target OCC
	 * @param otcCommandDto    the otc command dto
	 * @param value            the value
	 * @param arraySize        the array size
	 * @param createNewVarName the create new var name
	 * @param varNamesSet      the var names set
	 * @param varNamesMap      the var names map
	 * @return the string
	 */
	private static String generateCode(TargetOtcCommandContext targetOCC, OtcCommandDto otcCommandDto, String value,
			Integer arraySize, boolean createNewVarName, Set<String> varNamesSet, Map<String, String> varNamesMap) {
		String concreteType = fetchConcreteTypeName(targetOCC, otcCommandDto);
		String fieldType = fetchFieldTypeName(targetOCC, null, otcCommandDto, createNewVarName, varNamesMap);
		String varName = createVarName(otcCommandDto, createNewVarName, varNamesSet, varNamesMap);
		if (otcCommandDto.isArray()) {
			if (TARGET_SOURCE.TARGET == otcCommandDto.enumTargetSource) {
				if (arraySize != null) {
					concreteType = concreteType.replace("[]", "[" + arraySize + "]");
				} else {
					concreteType = concreteType.replace("[]", "[" + 1 + "]");
				}
			}
		}
		String parentVarName = null;
		if (otcCommandDto.isFirstNode) {
			parentVarName = CommonUtils.initLower(otcCommandDto.field.getDeclaringClass().getSimpleName());
		} else {
			parentVarName = createVarName(otcCommandDto.parent, createNewVarName, varNamesSet, varNamesMap);
		}
		String getter = otcCommandDto.getter;
		String getterCode = null;
		if (otcCommandDto.enableGetterHelper) {
			String helper = targetOCC.factoryClassDto.addImport(targetOCC.helper);
			getterCode = String.format(HELPER_GETTER_TEMPLATE, fieldType, varName, helper, getter, parentVarName);
		} else {
			getterCode = String.format(GETTER_TEMPLATE, fieldType, varName, parentVarName, getter);
		}
		String ifNullCreateAndSetCode = IfNullCreateAndSetTemplate.generateCode(targetOCC, value, arraySize,
				createNewVarName, varNamesSet, varNamesMap);
		String getterWithIfNullCreateSet = getterCode + (ifNullCreateAndSetCode == null ? "" : ifNullCreateAndSetCode);
		return addInlineComments(inlineComments, getterWithIfNullCreateSet);
	}
}
