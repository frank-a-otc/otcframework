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

import etree.dateconverters.DateConverterFacade;
import org.otcframework.common.dto.OtcCommandDto;
import org.otcframework.common.util.CommonUtils;
import org.otcframework.compiler.command.SourceOtcCommandContext;
import org.otcframework.compiler.command.TargetOtcCommandContext;
import org.otcframework.compiler.exception.CodeGeneratorException;

import java.util.Map;
import java.util.Set;

/**
 * The Class GetSetTemplate.
 */
// TODO: Auto-generated Javadoc
public final class GetSetTemplate extends AbstractTemplate {

	private static final String inlineComments = "\n// ---- generator - " +
			GetSetTemplate.class.getSimpleName() + "\n";

	/**
	 * Instantiates a new gets the set template.
	 */
	private GetSetTemplate() {
	}

	/**
	 * Generate code.
	 *
	 * @param targetOCC        the target OCC
	 * @param sourceOCC        the source OCC
	 * @param createNewVarName the create new var name
	 * @param varNamesSet      the var names set
	 * @param varNamesMap      the var names map
	 * @return the string
	 */
	public static String generateCode(TargetOtcCommandContext targetOCC, SourceOtcCommandContext sourceOCC,
			boolean createNewVarName, Set<String> varNamesSet, Map<String, String> varNamesMap) {
		OtcCommandDto targetOCD = targetOCC.otcCommandDto;
		if (targetOCD.isCollectionOrMap() || targetOCD.isCollectionOrMapMember()) {
			throw new CodeGeneratorException("", "Invalid call to method in OTC-command : " + targetOCC.commandId
					+ ". Type should not be a Collecton or Map member.");
		}
		OtcCommandDto sourceOCD = sourceOCC.otcCommandDto;
		String getSetCode = null;
		if (targetOCD.enableSetterHelper || sourceOCD.enableGetterHelper) {
			getSetCode = generateCodeForHelper(targetOCC, sourceOCC, createNewVarName, varNamesSet, varNamesMap);
		} else {
			getSetCode = generateCodeForGetterSetter(targetOCC, sourceOCC, createNewVarName, varNamesSet, varNamesMap);
		}
		return addInlineComments(inlineComments, getSetCode);
	}

	/**
	 * Generate code for helper.
	 *
	 * @param targetOCC        the target OCC
	 * @param sourceOCC        the source OCC
	 * @param createNewVarName the create new var name
	 * @param varNamesSet      the var names set
	 * @param varNamesMap      the var names map
	 * @return the string
	 */
	private static String generateCodeForHelper(TargetOtcCommandContext targetOCC, SourceOtcCommandContext sourceOCC,
			boolean createNewVarName, Set<String> varNamesSet, Map<String, String> varNamesMap) {
		OtcCommandDto targetOCD = targetOCC.otcCommandDto;
		OtcCommandDto sourceOCD = sourceOCC.otcCommandDto;
		String getSetCode = null;
		String sourceParentVarName = null;
		String targetParentVarName = null;
		if (targetOCD.isFirstNode) {
			targetParentVarName = CommonUtils.initLower(targetOCD.field.getDeclaringClass().getSimpleName());
		} else {
			targetParentVarName = createVarName(targetOCD.parent, createNewVarName, varNamesSet, varNamesMap);
		}
		if (sourceOCD.isFirstNode) {
			sourceParentVarName = CommonUtils.initLower(sourceOCD.field.getDeclaringClass().getSimpleName());
		} else {
			if (targetOCD.enableSetterHelper && sourceOCD.isCollectionOrMapMember()) {
				sourceParentVarName = createVarName(sourceOCD, createNewVarName, varNamesSet, varNamesMap);
			} else {
				sourceParentVarName = createVarName(sourceOCD.parent, createNewVarName, varNamesSet, varNamesMap);
			}
		}
		String helper = targetOCC.factoryClassDto.addImport(targetOCC.helper);
		if (targetOCD.enableSetterHelper && sourceOCD.enableGetterHelper) {
			if (sourceOCD.isCollectionOrMapMember()) {
				getSetCode = String.format(helperSetterTemplate, helper, targetOCD.setter, targetParentVarName,
						sourceParentVarName);
			} else {
				getSetCode = String.format(setHelperGetHelperTemplate, helper, targetOCD.setter, targetParentVarName,
						helper, sourceOCD.getter, sourceParentVarName);
			}
		} else if (targetOCD.enableSetterHelper) {
			if (sourceOCD.isCollectionOrMapMember()) {
				getSetCode = String.format(helperSetterTemplate, helper, targetOCD.setter, targetParentVarName,
						sourceParentVarName);
			} else {
				getSetCode = String.format(setHelperTemplate, helper, targetOCD.setter, targetParentVarName,
						sourceParentVarName, sourceOCD.getter);
			}
		} else {
			String targetVarName = null;
			if (targetOCD.isFirstNode) {
				targetVarName = CommonUtils.initLower(targetOCD.field.getDeclaringClass().getSimpleName());
			} else {
				targetVarName = createVarName(targetOCD, createNewVarName, varNamesSet, varNamesMap);
			}
			getSetCode = String.format(getHelperTemplate, targetVarName, targetOCD.setter, targetParentVarName, helper,
					sourceOCD.getter, sourceParentVarName);
		}
		return addInlineComments(inlineComments, getSetCode);
	}

	/**
	 * Generate code for getter setter.
	 *
	 * @param targetOCC        the target OCC
	 * @param sourceOCC        the source OCC
	 * @param createNewVarName the create new var name
	 * @param varNamesSet      the var names set
	 * @param varNamesMap      the var names map
	 * @return the string
	 */
	private static String generateCodeForGetterSetter(TargetOtcCommandContext targetOCC,
			SourceOtcCommandContext sourceOCC, boolean createNewVarName, Set<String> varNamesSet,
			Map<String, String> varNamesMap) {
		if (!sourceOCC.isLeaf()) {
			throw new CodeGeneratorException("",
					"Invalid call to method in OTC-command : " + targetOCC.commandId + ". Source token is not a leaf.");
		}
		OtcCommandDto targetOCD = targetOCC.otcCommandDto;
		OtcCommandDto sourceOCD = sourceOCC.otcCommandDto;
		String sourceVarName = createVarName(sourceOCD, createNewVarName, varNamesSet, varNamesMap);
		String targetParentVarName = null;
		if (targetOCD.isFirstNode) {
			targetParentVarName = CommonUtils.initLower(targetOCD.field.getDeclaringClass().getSimpleName());
		} else {
			targetParentVarName = createVarName(targetOCD.parent, createNewVarName, varNamesSet, varNamesMap);
		}
		String getSetCode = null;
		if (targetOCD.isEnum() && sourceOCD.isEnum()) {
			String targetEnumType = fetchSanitizedTypeName(targetOCC, targetOCD);
			getSetCode = String.format(setterBothEnumTemplate, targetParentVarName, targetOCD.setter, targetEnumType,
					sourceVarName);
		} else if (sourceOCD.isEnum()) {
			getSetCode = String.format(setterSourceEnumTemplate, targetParentVarName, targetOCD.setter, sourceVarName);
		} else if (targetOCD.isEnum()) {
			String targetEnumType = fetchSanitizedTypeName(targetOCC, targetOCD);
			getSetCode = String.format(setterTargetEnumTemplate, targetParentVarName, targetOCD.setter, targetEnumType,
					sourceVarName);
		} else {
			if (DateConverterFacade.isOfAnyDateType(targetOCD.fieldType)) {
				targetOCC.factoryClassDto.addImport(DateConverterFacade.class.getName());
				if (DateConverterFacade.isOfAnyDateType(sourceOCD.fieldType)) {
					getSetCode = String.format(dateConverterTemplate, targetParentVarName, targetOCD.setter,
							sourceVarName, targetOCD.fieldType.getName());
				} else {
					if (String.class != sourceOCD.fieldType) {
						throw new CodeGeneratorException("", sourceOCD.fieldType + " in from: cannot be converted to "
								+ targetOCD.fieldType.getName() + " in " + targetOCC.commandId);
					}
					getSetCode = String.format(dateConverterTemplate, targetParentVarName, targetOCD.setter,
							sourceVarName, sourceOCD.fieldType.getName());
				}
			} else if (DateConverterFacade.isOfAnyDateType(sourceOCD.fieldType)) {
				targetOCC.factoryClassDto.addImport(DateConverterFacade.class.getName());
				if (String.class != targetOCD.fieldType) {
					throw new CodeGeneratorException("", sourceOCD.fieldType + " in from: cannot be converted to "
							+ targetOCD.fieldType.getName() + " in " + targetOCC.commandId);
				}
				getSetCode = String.format(dateToStringConverterTemplate, targetParentVarName, targetOCD.setter,
						sourceVarName);
			} else {
				getSetCode = String.format(setterTemplate, targetParentVarName, targetOCD.setter, sourceVarName);
			}
		}
		return addInlineComments(inlineComments, getSetCode);
	}
}
