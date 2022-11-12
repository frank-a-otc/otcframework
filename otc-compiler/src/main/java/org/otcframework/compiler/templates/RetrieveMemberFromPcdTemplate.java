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
import org.otcframework.compiler.command.SourceOtcCommandContext;
import org.otcframework.compiler.command.TargetOtcCommandContext;

/**
 * The Class RetrieveMemberFromPcdTemplate.
 */
// TODO: Auto-generated Javadoc
public final class RetrieveMemberFromPcdTemplate extends AbstractTemplate {

	/**
	 * Instantiates a new retrieve member from pcd template.
	 */
	private RetrieveMemberFromPcdTemplate() {
	}

	/**
	 * Generate code.
	 *
	 * @param targetOCC        the target OCC
	 * @param createNewVarName the create new var name
	 * @param icd              the icd
	 * @param varNamesSet      the var names set
	 * @param varNamesMap      the var names map
	 * @return the string
	 */
	public static String generateCode(TargetOtcCommandContext targetOCC, boolean createNewVarName, String icd,
			Set<String> varNamesSet, Map<String, String> varNamesMap) {
		OtcCommandDto memberOCD = targetOCC.otcCommandDto;
		String memberType = fetchFieldTypeName(targetOCC, null, memberOCD, createNewVarName, varNamesMap);
		String varName = createVarName(memberOCD, createNewVarName, varNamesSet, varNamesMap);
		String typecastType = fetchSanitizedTypeName(targetOCC, memberOCD);
		String retrieveTargetObjectFromPcdCode = String.format(retrieveMemberFromIcdTemplate, memberType, varName,
				typecastType, icd);
		return retrieveTargetObjectFromPcdCode;
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
		OtcCommandDto memberOCD = sourceOCC.otcCommandDto;
		String memberType = fetchFieldTypeName(targetOCC, sourceOCC, memberOCD, createNewVarName, varNamesMap);
		String varName = createVarName(memberOCD, createNewVarName, varNamesSet, varNamesMap);
		String typecastType = fetchSanitizedTypeName(targetOCC, memberOCD);
		String retrieveSourceObjectFromPcdCode = String.format(retrieveMemberFromIcdTemplate, memberType, varName,
				typecastType, MEMBER_SOURCE_ICD);
		return retrieveSourceObjectFromPcdCode;
	}
}
