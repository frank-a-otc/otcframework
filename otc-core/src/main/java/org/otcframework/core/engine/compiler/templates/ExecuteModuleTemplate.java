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
import org.otcframework.core.engine.compiler.command.SourceOtcCommandContext;
import org.otcframework.core.engine.compiler.command.TargetOtcCommandContext;

/**
 * The Class ExecuteModuleTemplate.
 */
// TODO: Auto-generated Javadoc
public final class ExecuteModuleTemplate extends AbstractTemplate {

	/**
	 * Instantiates a new execute module template.
	 */
	private ExecuteModuleTemplate() {
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
		OtcCommandDto sourceOCD = sourceOCC.otcCommandDto;
		String targetVarName = createVarName(targetOCD, createNewVarName, varNamesSet, varNamesMap);
		String sourceVarName = createVarName(sourceOCD, createNewVarName, varNamesSet, varNamesMap);
		String otcNamespace = targetOCC.executeModuleOtcNamespace;
		String executeModuleCode = String.format(executeModuleTemplate, otcNamespace, sourceVarName, targetVarName);
		return executeModuleCode;
	}
}
