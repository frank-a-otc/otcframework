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

import org.otcl2.common.OtclConstants.TARGET_SOURCE;
import org.otcl2.common.dto.OtclCommandDto;
import org.otcl2.common.util.CommonUtils;
import org.otcl2.core.engine.compiler.command.TargetOtclCommandContext;
import org.otcl2.core.engine.compiler.exception.CodeGeneratorException;

// TODO: Auto-generated Javadoc
/**
 * The Class GetterIfNullCreateSetTemplate.
 */
public final class GetterIfNullCreateSetTemplate extends AbstractTemplate {

	/**
	 * Instantiates a new getter if null create set template.
	 */
	private GetterIfNullCreateSetTemplate() {}

	/**
	 * Generate code.
	 *
	 * @param targetOCC the target OCC
	 * @param otclCommandDto the otcl command dto
	 * @param createNewVarName the create new var name
	 * @param varNamesSet the var names set
	 * @param varNamesMap the var names map
	 * @return the string
	 */
	public static String generateCode(TargetOtclCommandContext targetOCC, OtclCommandDto otclCommandDto,
			boolean createNewVarName, Set<String> varNamesSet, Map<String, String> varNamesMap) {
		if (otclCommandDto.isArray()) {
			throw new CodeGeneratorException("", "Invalid call to method in Script-block : " + targetOCC.scriptId +
					". Type should not be an array.");
		}
		return generateCode(targetOCC, otclCommandDto, null, createNewVarName, varNamesSet, varNamesMap);
	}
	
	/**
	 * Generate code for array.
	 *
	 * @param targetOCC the target OCC
	 * @param otclCommandDto the otcl command dto
	 * @param arraySize the array size
	 * @param createNewVarName the create new var name
	 * @param varNamesSet the var names set
	 * @param varNamesMap the var names map
	 * @return the string
	 */
	public static String generateCodeForArray(TargetOtclCommandContext targetOCC, OtclCommandDto otclCommandDto,
			Integer arraySize, boolean createNewVarName, Set<String> varNamesSet, Map<String, String> varNamesMap) {
		if (!otclCommandDto.isArray()) {
			throw new CodeGeneratorException("", "Invalid call to method in Script-block : " + targetOCC.scriptId + 
					". Type should be an array.");
		}
		return generateCode(targetOCC, otclCommandDto, arraySize, createNewVarName, varNamesSet, varNamesMap);
	}
	
	/**
	 * Generate code.
	 *
	 * @param targetOCC the target OCC
	 * @param otclCommandDto the otcl command dto
	 * @param arraySize the array size
	 * @param createNewVarName the create new var name
	 * @param varNamesSet the var names set
	 * @param varNamesMap the var names map
	 * @return the string
	 */
	private static String generateCode(TargetOtclCommandContext targetOCC, OtclCommandDto otclCommandDto,
			Integer arraySize, boolean createNewVarName, Set<String> varNamesSet, Map<String, String> varNamesMap) {
		String concreteType = fetchConcreteTypeName(targetOCC, otclCommandDto);
		String fieldType = fetchFieldTypeName(targetOCC, null, otclCommandDto, createNewVarName, varNamesMap);
		String varName = createVarName(otclCommandDto, createNewVarName, varNamesSet, varNamesMap);
		if (otclCommandDto.isArray()) {
			if (TARGET_SOURCE.TARGET == otclCommandDto.enumTargetSource) {
				if (arraySize != null) {
					concreteType = concreteType.replace("[]","[" + arraySize + "]");
				} else {
					concreteType = concreteType.replace("[]","[" + 1 + "]");
				}
			}
		}
		String parentVarName = null;
		if (otclCommandDto.isRootNode) {
			parentVarName = CommonUtils.initLower(otclCommandDto.field.getDeclaringClass().getSimpleName());
		} else {
			parentVarName = createVarName(otclCommandDto.parent, createNewVarName, varNamesSet, varNamesMap);
		}
		String getter = otclCommandDto.getter;
		String getterCode = null;
		if (otclCommandDto.enableFactoryHelperGetter) {
			String helper = targetOCC.factoryClassDto.addImport(targetOCC.helper);
			getterCode = String.format(helperGetterTemplate, fieldType, varName, helper, getter, parentVarName);
		} else {
			getterCode = String.format(getterTemplate, fieldType, varName, parentVarName, getter);
		}
		String ifNullCreateAndSetCode = IfNullCreateAndSetTemplate.generateCode(targetOCC, arraySize, createNewVarName,
				varNamesSet, varNamesMap);
		return getterCode + ifNullCreateAndSetCode;
	}

}
