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

import org.otcframework.common.OtcConstants;
import org.otcframework.common.OtcConstants.LogLevel;
import org.otcframework.common.dto.OtcCommandDto;
import org.otcframework.common.util.CommonUtils;
import org.otcframework.common.util.PackagesFilterUtil;
import org.otcframework.compiler.command.TargetOtcCommandContext;
import org.otcframework.compiler.exception.CodeGeneratorException;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * The Class AddToCollectionTemplate.
 */
public final class AddToCollectionTemplate extends AbstractTemplate {

	private static final String INLINE_COMMENTS = "\n// ---- generator - " +
			AddToCollectionTemplate.class.getSimpleName();

	/**
	 * Instantiates a new adds the to collection template.
	 */
	private AddToCollectionTemplate() {
	}

	/**
	 * Generate code.
	 *
	 * @param targetOCC        the target OCC
	 * @param value            the value
	 * @param sourceOCD        the source OCD
	 * @param idx              the idx
	 * @param createNewVarName the create new var name
	 * @param varNamesSet      the var names set
	 * @param varNamesMap      the var names map
	 * @return the string
	 */
	public static String generateCode(TargetOtcCommandContext targetOCC, String value, OtcCommandDto sourceOCD,
			Integer idx, boolean createNewVarName, Set<String> varNamesSet, Map<String, String> varNamesMap) {
		return generateCode(targetOCC, sourceOCD, value, idx, null, createNewVarName, varNamesSet, varNamesMap);
	}

	/**
	 * Generate code.
	 *
	 * @param targetOCC        the target OCC
	 * @param sourceOCD        the source OCD
	 * @param idxVar           the idx var
	 * @param createNewVarName the create new var name
	 * @param varNamesSet      the var names set
	 * @param varNamesMap      the var names map
	 * @return the string
	 */
	public static String generateCode(TargetOtcCommandContext targetOCC, OtcCommandDto sourceOCD, String idxVar,
			boolean createNewVarName, Set<String> varNamesSet, Map<String, String> varNamesMap) {
		return generateCode(targetOCC, sourceOCD, null, null, idxVar, createNewVarName, varNamesSet, varNamesMap);
	}

	/**
	 * Generate code.
	 *
	 * @param targetOCC        the target OCC
	 * @param sourceOCD        the source OCD
	 * @param value            the value
	 * @param idx              the idx
	 * @param idxVar           the idx var
	 * @param createNewVarName the create new var name
	 * @param varNamesSet      the var names set
	 * @param varNamesMap      the var names map
	 * @return the string
	 */
	private static String generateCode(TargetOtcCommandContext targetOCC, OtcCommandDto sourceOCD, String value,
			Integer idx, String idxVar, boolean createNewVarName, Set<String> varNamesSet,
			Map<String, String> varNamesMap) {
		OtcCommandDto memberOCD = targetOCC.otcCommandDto;
		if (!memberOCD.isCollectionMember()) {
			throw new CodeGeneratorException("", "Invalid call to method in OTC-command : " + targetOCC.commandId
					+ ". Type should be a collection member for target-otc-chain : " + targetOCC.otcChain);
		}
		String memberType = fetchFieldTypeName(targetOCC, null, memberOCD, createNewVarName, varNamesMap);
		String codeToReplace = generateCodeToReplace(targetOCC, memberOCD, value, sourceOCD, idx, createNewVarName,
				varNamesSet, varNamesMap);
		String initMembers = null;
		String varName = createVarName(memberOCD, createNewVarName, varNamesSet, varNamesMap);
		String fieldTypecastType = fetchSanitizedTypeName(targetOCC, memberOCD);
		if (idxVar == null && idx == null) {
			String valOrVar = fetchValueOrVar(targetOCC, sourceOCD, value, createNewVarName, varNamesSet, varNamesMap);
			initMembers = String.format(ADD_COLLECTION_MEMBER_AT_END_TEMPLATE, valOrVar);
		} else {
			String icdId = createIcdKey(memberOCD, idxVar, idx);
			initMembers = String.format(ADD_COLLECTION_MEMBER_TEMPLATE, memberType, varName, null, icdId, varName,
					fieldTypecastType, varName, varName, icdId);
		}
		initMembers = initMembers.replace(CODE_TO_REPLACE, codeToReplace);
		return addInlineComments(INLINE_COMMENTS, initMembers);
	}

	/**
	 * Generate code to replace.
	 *
	 * @param targetOCC        the target OCC
	 * @param memberOCD        the member OCD
	 * @param value            the value
	 * @param otherOCD         the other OCD
	 * @param idx              the idx
	 * @param createNewVarName the create new var name
	 * @param varNamesSet      the var names set
	 * @param varNamesMap      the var names map
	 * @return the string
	 */
	public static String generateCodeToReplace(TargetOtcCommandContext targetOCC, OtcCommandDto memberOCD, String value,
			OtcCommandDto otherOCD, Integer idx, boolean createNewVarName, Set<String> varNamesSet,
			Map<String, String> varNamesMap) {
		String varName = createVarName(memberOCD, createNewVarName, varNamesSet, varNamesMap);
		String valOrVar = fetchValueOrVar(targetOCC, otherOCD, value, createNewVarName, varNamesSet, varNamesMap);
		String concreteType = null;
		if (memberOCD.isEnum()) {
			concreteType = valOrVar;
		} else {
			concreteType = fetchConcreteTypeName(targetOCC, memberOCD);
		}
		OtcCommandDto parentOCD = memberOCD.parent;
		String parentVarName = createVarName(parentOCD, createNewVarName, varNamesSet, varNamesMap);
		String codeToReplace = "";
		if (parentOCD.isArray()) {
			String collectionsParentVarName = null;
			if (parentOCD.isFirstNode) {
				collectionsParentVarName = CommonUtils.initLower(parentOCD.field.getDeclaringClass().getSimpleName());
			} else {
				collectionsParentVarName = createVarName(parentOCD, createNewVarName, varNamesSet, varNamesMap);
			}
			targetOCC.factoryClassDto.addImport(Arrays.class.getName());
			if (idx == null) {
				codeToReplace += String.format(RESIZE_ARRAY_AND_ADD_AT_END_TEMPLATE, parentVarName, parentVarName,
						parentVarName, parentVarName, valOrVar);
				String setterCode = SetterTemplate.generateCode(targetOCC, collectionsParentVarName,
						varNamesSet, varNamesMap);
				codeToReplace = codeToReplace.replace(CODE_TO_REPLACE, setterCode);
			} else {
				String setter = parentOCD.setter;
				if (parentOCD.enableSetterHelper) {
					String helper = targetOCC.factoryClassDto.addImport(targetOCC.helper);
					codeToReplace += String.format(HELPER_ADD_TO_ARRAY_TEMPLATE, parentVarName, idx, parentVarName,
							parentVarName, helper, setter, collectionsParentVarName, parentVarName, "", varName,
							concreteType, parentVarName, idx, valOrVar);
				} else {
					codeToReplace += String.format(ADD_TO_ARRAY_TEMPLATE, parentVarName, idx, parentVarName, parentVarName,
							collectionsParentVarName, setter, parentVarName, "", varName, concreteType, parentVarName,
							idx, valOrVar);
				}
			}
		} else {
			String memberType = fetchFieldTypeName(targetOCC, null, memberOCD, createNewVarName, varNamesMap);
			if (PackagesFilterUtil.isFilteredPackage(memberOCD.fieldType)) {
				if (memberOCD.isEnum()) {
					codeToReplace = String.format(CREATE_INIT_VAR_TEMPLATE, memberType, varName, concreteType);
				} else {
					codeToReplace = String.format(CREATE_INSTANCE_TEMPLATE, memberType, varName, concreteType);
				}
			} else {
				codeToReplace = String.format(CREATE_INIT_VAR_TEMPLATE, memberType, varName, valOrVar);
			}
			codeToReplace += String.format(ADD_TO_COLLECTION_TEMPLATE, parentVarName, varName);
		}
		return addInlineComments(INLINE_COMMENTS, codeToReplace);
	}

	/**
	 * Generate post loop code.
	 *
	 * @param targetOCC        the target OCC
	 * @param createNewVarName the create new var name
	 * @param idx              the idx
	 * @param idxVar           the idx var
	 * @param logLevel         the log level
	 * @param parentPcd        the parent pcd
	 * @param memberPcd        the member pcd
	 * @param varNamesSet      the var names set
	 * @param varNamesMap      the var names map
	 * @return the string
	 */
	public static String generatePostLoopCode(TargetOtcCommandContext targetOCC, boolean createNewVarName, Integer idx,
			String idxVar, LogLevel logLevel, String parentPcd, String memberPcd, Set<String> varNamesSet,
			Map<String, String> varNamesMap) {
		StringBuilder forLoopCodeBuilder = new StringBuilder();
		if (idx != null) {
			parentPcd = parentPcd + idx;
			memberPcd = memberPcd + idx;
		}
		OtcCommandDto memberOCD = targetOCC.otcCommandDto;
		OtcCommandDto targetOCD = memberOCD.parent;
		boolean hasAncestor = targetOCC.hasAncestralCollectionOrMap();
		if (!hasAncestor) {
			memberPcd = "targetICD";
		} else {
			if (idx != null) {
				memberPcd = memberPcd + idx;
			}
		}
		String icdId = createIcdKey(memberOCD, idxVar, null);
		boolean hasMapValueInPath = memberOCD.isMapValue() || targetOCC.hasMapValueDescendant();
		if (hasMapValueInPath) {
			int endIdx = targetOCC.otcChain.lastIndexOf(OtcConstants.MAP_VALUE_REF) + 3;
			String mapValueTokenPath = targetOCC.otcChain.substring(0, endIdx);
			String logMsg = "Corresponding Map-key missing for path: '" + mapValueTokenPath + "'!";
			forLoopCodeBuilder.append(String.format(RETRIEVE_MEMBER_ICD, idx, parentPcd, icdId));
			forLoopCodeBuilder.append(String.format(POST_LOOP_TEMPLATE, idx, idx, idx, logLevel, logMsg));
			forLoopCodeBuilder.append(RetrieveMemberFromPcdTemplate.generateCode(targetOCC, createNewVarName, memberPcd,
					varNamesSet, varNamesMap));
		} else {
			String fieldType = fetchFieldTypeName(targetOCC, null, memberOCD, createNewVarName, varNamesMap);
			String varName = createVarName(memberOCD, createNewVarName, varNamesSet, varNamesMap);
			String concreteType = fetchConcreteTypeName(targetOCC, memberOCD);
			forLoopCodeBuilder.append(String.format(POST_TARGET_LOOP_TEMPLATE, idx, parentPcd, icdId, fieldType, varName,
					idx, varName, concreteType, idx, idx, parentPcd, varName, icdId));
			String createMemberCode = String.format(CREATE_INSTANCE_TEMPLATE, "", varName, concreteType);
			String parentVarName = createVarName(targetOCD, createNewVarName, varNamesSet, varNamesMap);
			createMemberCode += String.format(ADD_TO_COLLECTION_TEMPLATE, parentVarName, varName);
			int startIdx = forLoopCodeBuilder.indexOf(CODE_TO_REPLACE);
			forLoopCodeBuilder.replace(startIdx, startIdx + CODE_TO_REPLACE.length(), createMemberCode);
		}
		return addInlineComments(INLINE_COMMENTS, forLoopCodeBuilder.toString());
	}
}
