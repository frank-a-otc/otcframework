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
import org.otcframework.common.util.PackagesFilterUtil;
import org.otcframework.core.engine.compiler.command.TargetOtcCommandContext;

// TODO: Auto-generated Javadoc
/**
 * The Class IfNullCreateAndSetTemplate.
 */
public final class IfNullCreateAndSetTemplate extends AbstractTemplate {

	/**
	 * Instantiates a new if null create and set template.
	 */
	private IfNullCreateAndSetTemplate() {}

	/**
	 * Generate code.
	 *
	 * @param targetOCC the target OCC
	 * @param value the value
	 * @param arraySize the array size
	 * @param createNewVarName the create new var name
	 * @param varNamesSet the var names set
	 * @param varNamesMap the var names map
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
					concreteType = concreteType.replace("[]","[" + arraySize + "]");
				} else {
					concreteType = concreteType.replace("[]","[" + 1 + "]");
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
					ifNullSetterCode = String.format(ifNullEnumCreateAndSetTemplate, varName, varName, concreteType, value,
							parentVarName, setter, varName);
				} else {
					ifNullSetterCode = String.format(ifNullCreateAndSetTemplate, varName, varName, concreteType, parentVarName,
						setter, varName);
				}
			}
			if (targetOCD.isArray()) {
				ifNullSetterCode = ifNullSetterCode.replace("]()", "]");
			}
		}
		return ifNullSetterCode;
	}

}
