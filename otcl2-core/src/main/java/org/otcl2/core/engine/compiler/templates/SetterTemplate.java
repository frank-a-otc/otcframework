/**
* Copyright (c) otclfoundation.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*
* This file is part of the OTCL framework.
* 
*  The OTCL framework is free software: you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  the Free Software Foundation, version 3 of the License.
*
*  The OTCL framework is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU General Public License for more details.
*
*  A copy of the GNU General Public License is made available as 'License.md' file, 
*  along with OTCL framework project.  If not, see <https://www.gnu.org/licenses/>.
*
*/
package org.otcl2.core.engine.compiler.templates;

import java.util.Map;
import java.util.Set;

import org.otcl2.common.dto.OtclCommandDto;
import org.otcl2.common.util.CommonUtils;
import org.otcl2.common.util.PackagesFilterUtil;
import org.otcl2.core.engine.compiler.command.TargetOtclCommandContext;
import org.otclfoundation.dateconverters.DateConverterFacade;

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
	public static String generateCode(TargetOtclCommandContext targetOCC, boolean createNewVarName, String value,
			Set<String> varNamesSet, Map<String, String> varNamesMap) {
		OtclCommandDto otclCommandDto = targetOCC.otclCommandDto;
		String parentVarName = null;
		if (otclCommandDto.isRootNode) {
			parentVarName = CommonUtils.initLower(otclCommandDto.field.getDeclaringClass().getSimpleName());
			if (parentVarName.contains("$")) {
				parentVarName = parentVarName.replace("$", "");
			}
		} else {
			parentVarName = createVarName(otclCommandDto.parent, false, varNamesSet, varNamesMap);
		}
		String setterCode = null;
		if (PackagesFilterUtil.isFilteredPackage(otclCommandDto.fieldType)) {
			targetOCC.factoryClassDto.addImport(otclCommandDto.fieldType.getName());
		}
		value = createConvertExpression(otclCommandDto, value);
		if (otclCommandDto.enableFactoryHelperSetter) {
			String helper = targetOCC.factoryClassDto.addImport(targetOCC.helper);
			setterCode = String.format(helperSetterTemplate, helper, otclCommandDto.setter, parentVarName, value);
		} else {
			if (DateConverterFacade.isOfAnyDateType(otclCommandDto.fieldType)) {
//				String dateFormat = null;
//				if (targetOCC.scriptDto.copy.from.overrides != null) {
//					for (OtclFileDto.Override override : targetOCC.scriptDto.copy.from.overrides) {
//						if (override.dateFormat != null) {
//							dateFormat = override.dateFormat;
//							break;
//						}
//					}
//				}
				targetOCC.factoryClassDto.addImport(DateConverterFacade.class.getName());
				String clz = fetchSanitizedTypeName(targetOCC, otclCommandDto);
//				if (dateFormat != null) {
//					setterCode = String.format(formattedDateConverterTemplate, parentVarName, otclCommandDto.setter, value,
//							clz, dateFormat);
//				} else {
					setterCode = String.format(dateConverterTemplate, parentVarName, otclCommandDto.setter, value, clz);
//				}
			} else {
				setterCode = String.format(setterTemplate, parentVarName, otclCommandDto.setter, value);
			}
		}
		return setterCode;
	}
}
