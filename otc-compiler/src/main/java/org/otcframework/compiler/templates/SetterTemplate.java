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

import java.util.Map;
import java.util.Set;

import org.otcframework.common.dto.OtcCommandDto;
import org.otcframework.common.util.CommonUtils;
import org.otcframework.common.util.PackagesFilterUtil;
import org.otcframework.compiler.command.TargetOtcCommandContext;

import etree.dateconverters.DateConverterFacade;


/**
 * The Class SetterTemplate.
 */
// TODO: Auto-generated Javadoc
public class SetterTemplate extends AbstractTemplate {

	/**
	 * Instantiates a new setter template.
	 */
	private SetterTemplate() {
	}

	/**
	 * Generate code.
	 *
	 * @param targetOCC        the target OCC
	 * @param createNewVarName the create new var name
	 * @param value            the value
	 * @param varNamesSet      the var names set
	 * @param varNamesMap      the var names map
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
