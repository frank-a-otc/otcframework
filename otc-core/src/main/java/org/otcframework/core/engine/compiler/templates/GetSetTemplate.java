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
package org.otcframework.core.engine.compiler.templates;

import java.util.Map;
import java.util.Set;

import org.otcframework.common.dto.OtcCommandDto;
import org.otcframework.common.util.CommonUtils;
import org.otcframework.core.engine.compiler.command.SourceOtcCommandContext;
import org.otcframework.core.engine.compiler.command.TargetOtcCommandContext;
import org.otcframework.core.engine.compiler.exception.CodeGeneratorException;

import etree.dateconverters.MutualDateTypesConverterFacade;

/**
 * The Class GetSetTemplate.
 */
// TODO: Auto-generated Javadoc
public final class GetSetTemplate extends AbstractTemplate {

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
		return getSetCode;
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
		return getSetCode;
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
			if (MutualDateTypesConverterFacade.isOfAnyDateType(targetOCD.fieldType)) {
				targetOCC.factoryClassDto.addImport(MutualDateTypesConverterFacade.class.getName());
				if (MutualDateTypesConverterFacade.isOfAnyDateType(sourceOCD.fieldType)) {
					getSetCode = String.format(dateConverterTemplate, targetParentVarName, targetOCD.setter,
							sourceVarName, targetOCD.fieldType);
				} else {
					if (String.class != sourceOCD.fieldType) {
						throw new CodeGeneratorException("", sourceOCD.fieldType + " in from: cannot be converted to "
								+ targetOCD.fieldType + " in " + targetOCC.commandId);
					}
					getSetCode = String.format(dateConverterTemplate, targetParentVarName, targetOCD.setter,
							sourceVarName, sourceOCD.fieldType);
				}
			} else if (MutualDateTypesConverterFacade.isOfAnyDateType(sourceOCD.fieldType)) {
				targetOCC.factoryClassDto.addImport(MutualDateTypesConverterFacade.class.getName());
				if (String.class != targetOCD.fieldType) {
					throw new CodeGeneratorException("", sourceOCD.fieldType + " in from: cannot be converted to "
							+ targetOCD.fieldType + " in " + targetOCC.commandId);
				}
				getSetCode = String.format(dateToStringConverterTemplate, targetParentVarName, targetOCD.setter,
						sourceVarName);
			} else {
				getSetCode = String.format(setterTemplate, targetParentVarName, targetOCD.setter, sourceVarName);
			}
		}
		return getSetCode;
	}
}
