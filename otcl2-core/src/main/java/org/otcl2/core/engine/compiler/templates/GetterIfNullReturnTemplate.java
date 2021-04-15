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

import org.otcl2.common.OtclConstants;
import org.otcl2.common.OtclConstants.LogLevel;
import org.otcl2.common.OtclConstants.TARGET_SOURCE;
import org.otcl2.common.dto.OtclCommandDto;
import org.otcl2.common.util.CommonUtils;
import org.otcl2.core.engine.compiler.command.SourceOtclCommandContext;
import org.otcl2.core.engine.compiler.command.TargetOtclCommandContext;

// TODO: Auto-generated Javadoc
/**
 * The Class GetterIfNullReturnTemplate.
 */
public final class GetterIfNullReturnTemplate extends AbstractTemplate {

	/**
	 * Instantiates a new getter if null return template.
	 */
	private GetterIfNullReturnTemplate() {}

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

		String fieldType = fetchFieldTypeName(targetOCC, null, otclCommandDto, createNewVarName, varNamesMap);
		String varName = createVarName(otclCommandDto, createNewVarName, varNamesSet, varNamesMap);
		String parentVarName = null;
		if (otclCommandDto.isRootNode) {
			parentVarName = CommonUtils.initLower(otclCommandDto.field.getDeclaringClass().getSimpleName());
		} else {
			parentVarName = createVarName(otclCommandDto.parent, false, varNamesSet, varNamesMap);
		}
		String getterName = otclCommandDto.getter;
		String getterCode = null;
		if (otclCommandDto.enableFactoryHelperGetter) {
			String helper = targetOCC.factoryClassDto.addImport(targetOCC.helper);
			getterCode = String.format(helperGetterTemplate, fieldType, varName, helper, getterName, parentVarName);
		} else {
			getterCode = String.format(getterTemplate, fieldType, varName, parentVarName, getterName);
		}
		return getterCode;
	}

	/**
	 * Generate getter if null return code.
	 *
	 * @param targetOCC the target OCC
	 * @param createNewVarName the create new var name
	 * @param logLevel the log level
	 * @param varNamesSet the var names set
	 * @param varNamesMap the var names map
	 * @return the string
	 */
	public static String generateGetterIfNullReturnCode(TargetOtclCommandContext targetOCC, boolean createNewVarName,
			LogLevel logLevel, Set<String> varNamesSet, Map<String, String> varNamesMap) {
		return generateGetterIfNullReturnCode(targetOCC, null, createNewVarName, logLevel, varNamesSet,
				varNamesMap);
	}
	
	/**
	 * Generate getter if null return code.
	 *
	 * @param targetOCC the target OCC
	 * @param sourceOCC the source OCC
	 * @param createNewVarName the create new var name
	 * @param logLevel the log level
	 * @param varNamesSet the var names set
	 * @param varNamesMap the var names map
	 * @return the string
	 */
	public static String generateGetterIfNullReturnCode(TargetOtclCommandContext targetOCC, 
			SourceOtclCommandContext sourceOCC, boolean createNewVarName, LogLevel logLevel,
			Set<String> varNamesSet, Map<String, String> varNamesMap) {
		OtclCommandDto otclCommandDto = null;
		boolean hasMapValueInPath = false;
		if (sourceOCC != null) {
			otclCommandDto = sourceOCC.otclCommandDto;
			hasMapValueInPath = sourceOCC.hasMapValueMember() || sourceOCC.hasMapValueDescendant();
		} else {
			otclCommandDto = targetOCC.otclCommandDto;
			hasMapValueInPath = targetOCC.hasMapValueMember() || targetOCC.hasMapValueDescendant();
		}
		String fieldType = fetchFieldTypeName(targetOCC, null, otclCommandDto, createNewVarName, varNamesMap);
		String varName = createVarName(otclCommandDto, createNewVarName, varNamesSet, varNamesMap);
		String parentVarName = null;
		if (otclCommandDto.isRootNode) {
			parentVarName = CommonUtils.initLower(otclCommandDto.field.getDeclaringClass().getSimpleName());
		} else {
			OtclCommandDto parentOCD = otclCommandDto.parent;
			parentVarName = createVarName(parentOCD, createNewVarName, varNamesSet, varNamesMap);
		}
		String getter = otclCommandDto.getter;
		String ifNotNullParentChildGetterCode = null;
		String logMsg = null;
		if (TARGET_SOURCE.SOURCE == otclCommandDto.enumTargetSource || !hasMapValueInPath) {
			logMsg = "'" + otclCommandDto.tokenPath + "' is null!.";
		} else {
			int endIdx = targetOCC.otclChain.lastIndexOf(OtclConstants.MAP_VALUE_REF) + 3;
			String mapValueTokenPath = targetOCC.otclChain.substring(0, endIdx);
			logMsg = "Corresponding Map-key missing for path: '" + mapValueTokenPath + "'!";
		}
		if (otclCommandDto.enableFactoryHelperGetter) {
			String helper = targetOCC.factoryClassDto.addImport(targetOCC.helper);
			ifNotNullParentChildGetterCode = String.format(helperGetIfNullReturnTemplate, fieldType, varName, 
					helper, getter, parentVarName, varName, logLevel, logMsg);
		} else {
			ifNotNullParentChildGetterCode = String.format(getterIfNullReturnTemplate, fieldType, varName, 
					parentVarName, getter, varName, logLevel, logMsg);
		}
		return ifNotNullParentChildGetterCode;
	}
}
