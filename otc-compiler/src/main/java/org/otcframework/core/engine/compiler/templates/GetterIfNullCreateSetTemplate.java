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

import org.otcframework.common.OtcConstants.TARGET_SOURCE;
import org.otcframework.common.dto.OtcCommandDto;
import org.otcframework.common.util.CommonUtils;
import org.otcframework.core.engine.compiler.command.TargetOtcCommandContext;
import org.otcframework.core.engine.compiler.exception.CodeGeneratorException;

/**
 * The Class GetterIfNullCreateSetTemplate.
 */
// TODO: Auto-generated Javadoc
public final class GetterIfNullCreateSetTemplate extends AbstractTemplate {

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
			getterCode = String.format(helperGetterTemplate, fieldType, varName, helper, getter, parentVarName);
		} else {
			getterCode = String.format(getterTemplate, fieldType, varName, parentVarName, getter);
		}
		String ifNullCreateAndSetCode = IfNullCreateAndSetTemplate.generateCode(targetOCC, value, arraySize,
				createNewVarName, varNamesSet, varNamesMap);
		return getterCode + ifNullCreateAndSetCode;
	}
}
