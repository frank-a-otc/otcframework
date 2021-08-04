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

import org.otcframework.common.OtcConstants;
import org.otcframework.common.OtcConstants.LogLevel;
import org.otcframework.common.dto.OtcCommandDto;
import org.otcframework.core.engine.compiler.command.OtcCommand;
import org.otcframework.core.engine.compiler.command.SourceOtcCommandContext;
import org.otcframework.core.engine.compiler.command.TargetOtcCommandContext;

/**
 * The Class ForLoopTemplate.
 */
// TODO: Auto-generated Javadoc
public final class ForLoopTemplate extends AbstractTemplate {

	/**
	 * Instantiates a new for loop template.
	 */
	private ForLoopTemplate() {
	}

	/**
	 * Generate source loop code.
	 *
	 * @param targetOCC        the target OCC
	 * @param sourceOCC        the source OCC
	 * @param idxPrefix        the idx prefix
	 * @param createNewVarName the create new var name
	 * @param logLevel         the log level
	 * @param varNamesSet      the var names set
	 * @param varNamesMap      the var names map
	 * @return the string
	 */
	public static String generateSourceLoopCode(TargetOtcCommandContext targetOCC, SourceOtcCommandContext sourceOCC,
			String idxPrefix, boolean createNewVarName, LogLevel logLevel, Set<String> varNamesSet,
			Map<String, String> varNamesMap) {
		OtcCommandDto sourceOCD = sourceOCC.otcCommandDto;
		varNamesSet.add(idxPrefix);
		String idxVar = sanitizeVarName(idxPrefix, varNamesSet);
		int idx = 0;
		if (!idxVar.equals(idxPrefix)) {
			idx = Integer.valueOf(idxVar.substring(idxPrefix.length()));
		}
		String parentPcd = "parentICD" + idx;
		String memberPcd = null;
		String icdId = sourceOCD.otcToken;
		if (!sourceOCC.hasAncestralCollectionOrMap()) {
			icdId = sourceOCD.tokenPath;
		}
		icdId = createIcdKey(icdId);
		String logMsg = "'" + sourceOCD.tokenPath + "' is null!.";
		StringBuilder forLoopCodeBuilder = new StringBuilder();
		String preSourceLoopTemplateCopy = preLoopTemplate;
		if (sourceOCD.isMap()) {
			preSourceLoopTemplateCopy = preSourceLoopTemplateCopy.replace(".size();", ".size() / 2;");
		}
		if (!sourceOCC.hasAncestralCollectionOrMap()) {
			memberPcd = "sourceICD";
			preSourceLoopTemplateCopy = preSourceLoopTemplateCopy.replace("continue;", "return;");
			forLoopCodeBuilder.append(ifNullSourceIcdReturnTemplate);
			forLoopCodeBuilder.append(String.format(preSourceLoopTemplateCopy, idx, memberPcd, icdId, idx, idx, idx,
					logLevel, logMsg, idx, idx));
		} else {
			memberPcd = "memberICD" + (idx - 1);
			forLoopCodeBuilder.append(String.format(preSourceLoopTemplateCopy, idx, memberPcd, icdId, idx, idx, idx,
					logLevel, logMsg, idx, idx, idx));
		}
		forLoopCodeBuilder.append(String.format(forLoopTemplate, idxVar, idxVar, idx, idxVar));
		OtcCommandDto memberOCD = OtcCommand.retrieveMemberOCD(sourceOCC);
		sourceOCC.otcCommandDto = memberOCD;
		String fieldType = fetchFieldTypeName(targetOCC, sourceOCC, memberOCD, createNewVarName, varNamesMap);
		String varName = createVarName(memberOCD, createNewVarName, varNamesSet, varNamesMap);
		String concreteType = fetchConcreteTypeName(targetOCC, memberOCD);
		icdId = createIcdKey(memberOCD, idxVar, null);
		forLoopCodeBuilder.append(String.format(retrieveMemberIcd, idx, parentPcd, icdId));
		memberPcd = "memberICD" + idx;
		if (!sourceOCC.hasDescendantCollectionOrMap()) {
			forLoopCodeBuilder.append(String.format(lastPostSourceLoopTemplate, idx, logLevel, logMsg, fieldType,
					varName, concreteType, memberPcd));
		} else {
			forLoopCodeBuilder.append(String.format(postLoopTemplate, idx, idx, idx, logLevel, logMsg));
			if (!sourceOCC.hasDescendantCollectionOrMap()) {
				forLoopCodeBuilder.append(
						String.format(retrieveMemberFromIcdTemplate, fieldType, varName, concreteType, memberPcd));
			}
		}
		return forLoopCodeBuilder.toString();
	}

	/**
	 * Generate target loop code.
	 *
	 * @param targetOCC        the target OCC
	 * @param idxPrefix        the idx prefix
	 * @param createNewVarName the create new var name
	 * @param logLevel         the log level
	 * @param varNamesSet      the var names set
	 * @param varNamesMap      the var names map
	 * @return the string
	 */
	public static String generateTargetLoopCode(TargetOtcCommandContext targetOCC, String idxPrefix,
			boolean createNewVarName, LogLevel logLevel, Set<String> varNamesSet, Map<String, String> varNamesMap) {
		OtcCommandDto targetOCD = targetOCC.otcCommandDto;
		varNamesSet.add(idxPrefix);
		String idxVar = sanitizeVarName(idxPrefix, varNamesSet);
		int idx = 0;
		if (!idxVar.equals(idxPrefix)) {
			idx = Integer.valueOf(idxVar.substring(idxPrefix.length()));
		}
		OtcCommandDto memberOCD = OtcCommand.retrieveMemberOCD(targetOCC);
		String memberPcd = null;
		String icdId = targetOCD.otcToken;
		boolean hasAncestor = targetOCC.hasAncestralCollectionOrMap();
		if (!hasAncestor) {
			memberPcd = "targetICD";
			icdId = targetOCD.tokenPath;
		} else {
			memberPcd = "memberICD" + (idx - 1);
		}
		icdId = createIcdKey(icdId);
		StringBuilder forLoopCodeBuilder = new StringBuilder();
		boolean hasMapValueInPath = memberOCD.isMapValue() || targetOCC.hasMapValueDescendant();
		String logMsg = null;
		if (hasMapValueInPath) {
			int endIdx = targetOCC.otcChain.lastIndexOf(OtcConstants.MAP_VALUE_REF) + 3;
			String mapValueTokenPath = targetOCC.otcChain.substring(0, endIdx);
			logMsg = "Corresponding Map-key missing for path: '" + mapValueTokenPath + "'!";
			String preLoopTemplateCopy = null;
			if (targetOCD.isMap()) {
				preLoopTemplateCopy = preLoopTemplate.replace(".size();", ".size() / 2;");
			} else {
				preLoopTemplateCopy = preLoopTemplate;
			}
			if (!hasAncestor) {
				preLoopTemplateCopy = preLoopTemplateCopy.replace("continue;", "return;");
			}
			forLoopCodeBuilder.append(String.format(preLoopTemplateCopy, idx, memberPcd, icdId, idx, idx, idx, logLevel,
					logMsg, idx, idx));
		} else {
			String varName = createVarName(targetOCD, createNewVarName, varNamesSet, varNamesMap);
			if (targetOCD.isMap()) {
				forLoopCodeBuilder.append(String.format(preTargetLoopTemplate.replace(".size();", ".size() / 2;"), idx,
						idx, memberPcd, icdId, idx, idx, memberPcd, varName, icdId, idx, idx, idx));
			} else {
				forLoopCodeBuilder.append(String.format(preTargetLoopTemplate, idx, idx, memberPcd, icdId, idx, idx,
						memberPcd, varName, icdId, idx, idx, idx));
			}
		}
		forLoopCodeBuilder.append(String.format(forLoopTemplate, idxVar, idxVar, idx, idxVar));
		targetOCC.otcCommandDto = memberOCD;
		if (targetOCD.isMap()) {
			String addMapKeyCode = null;
			if (memberOCD.isMapKey()) {
				addMapKeyCode = AddMapKeyTemplate.generatePostLoopCode(targetOCC, createNewVarName, idx, idxVar,
						varNamesSet, varNamesMap);
			} else {
				addMapKeyCode = AddMapValueTemplate.generatePostLoopCode(targetOCC, createNewVarName, idx, idxVar,
						LogLevel.WARN, varNamesSet, varNamesMap);
			}
			forLoopCodeBuilder.append(addMapKeyCode);
		} else {
			String postLoopCode = AddToCollectionTemplate.generatePostLoopCode(targetOCC, createNewVarName, idx, idxVar,
					logLevel, PARENT_ICD, MEMBER_ICD, varNamesSet, varNamesMap);
			forLoopCodeBuilder.append(postLoopCode);
			targetOCC.otcCommandDto = memberOCD;
		}
		return forLoopCodeBuilder.toString();
	}
}
