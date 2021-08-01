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
import org.otcframework.common.util.PackagesFilterUtil;
import org.otcframework.core.engine.compiler.command.TargetOtcCommandContext;
import org.otcframework.dateconverters.DateConverterFacade;

// TODO: Auto-generated Javadoc
/**
 * The Class SetterTemplate.
 */
public class SetterTemplate extends AbstractTemplate {

	/**
	 * Instantiates a new setter template.
	 */
	private SetterTemplate() {}

	/**
	 * Generate code.
	 *
	 * @param targetOCC the target OCC
	 * @param createNewVarName the create new var name
	 * @param value the value
	 * @param varNamesSet the var names set
	 * @param varNamesMap the var names map
	 * @return the string
	 */
	public static String generateCode(TargetOtcCommandContext targetOCC, boolean createNewVarName, String value,
			Set<String> varNamesSet, Map<String, String> varNamesMap) {
		OtcCommandDto otcCommandDto = targetOCC.otcCommandDto;
		String parentVarName = null;
		if (otcCommandDto.isFirstNode) {
			parentVarName = CommonUtils.initLower(otcCommandDto.field.getDeclaringClass().getSimpleName());
			if (parentVarName.contains("$")) {
				parentVarName = parentVarName.replace("$", "");
			}
		} else {
			parentVarName = createVarName(otcCommandDto.parent, false, varNamesSet, varNamesMap);
		}
		String setterCode = null;
		if (PackagesFilterUtil.isFilteredPackage(otcCommandDto.fieldType)) {
			targetOCC.factoryClassDto.addImport(otcCommandDto.fieldType.getName());
		}
		value = createConvertExpression(otcCommandDto, value);
		if (otcCommandDto.enableSetterHelper) {
			String helper = targetOCC.factoryClassDto.addImport(targetOCC.helper);
			setterCode = String.format(helperSetterTemplate, helper, otcCommandDto.setter, parentVarName, value);
		} else {
			if (DateConverterFacade.isOfAnyDateType(otcCommandDto.fieldType)) {
				targetOCC.factoryClassDto.addImport(DateConverterFacade.class.getName());
				String clz = fetchSanitizedTypeName(targetOCC, otcCommandDto);
				setterCode = String.format(dateConverterTemplate, parentVarName, otcCommandDto.setter, value, clz);
			} else {
				setterCode = String.format(setterTemplate, parentVarName, otcCommandDto.setter, value);
			}
		}
		return setterCode;
	}
}
