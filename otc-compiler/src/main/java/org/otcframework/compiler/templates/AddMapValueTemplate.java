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
import org.otcframework.common.compiler.OtcCommandContext;
import org.otcframework.common.dto.OtcCommandDto;
import org.otcframework.common.util.PackagesFilterUtil;
import org.otcframework.compiler.command.TargetOtcCommandContext;
import org.otcframework.compiler.exception.CodeGeneratorException;

import java.util.Map;
import java.util.Set;

/**
 * The Class AddMapValueTemplate.
 */
public final class AddMapValueTemplate extends AbstractTemplate {

	private static final String INLINE_COMMENTS = "\n// ---- generator - " +
			AddMapValueTemplate.class.getSimpleName();

	/**
	 * Instantiates a new adds the map value template.
	 */
	private AddMapValueTemplate() {
	}

	/**
	 * Generate code.
	 *
	 * @param targetOCC        the target OCC
	 * @param sourceOCC        the source OCC
	 * @param createNewVarName the create new var name
	 * @param value            the value
	 * @param idx              the idx
	 * @param logLevel         the log level
	 * @param varNamesSet      the var names set
	 * @param varNamesMap      the var names map
	 * @return the string
	 */
	public static String generateCode(TargetOtcCommandContext targetOCC, OtcCommandContext sourceOCC,
			boolean createNewVarName, String value, Integer idx, LogLevel logLevel, Set<String> varNamesSet,
			Map<String, String> varNamesMap) {
		return generateCode(targetOCC, sourceOCC, createNewVarName, value, idx, null, logLevel, varNamesSet,
				varNamesMap);
	}

	/**
	 * Generate code.
	 *
	 * @param targetOCC        the target OCC
	 * @param sourceOCC        the source OCC
	 * @param createNewVarName the create new var name
	 * @param idxVar           the idx var
	 * @param logLevel         the log level
	 * @param varNamesSet      the var names set
	 * @param varNamesMap      the var names map
	 * @return the string
	 */
	public static String generateCode(TargetOtcCommandContext targetOCC, OtcCommandContext sourceOCC,
			boolean createNewVarName, String idxVar, LogLevel logLevel, Set<String> varNamesSet,
			Map<String, String> varNamesMap) {
		return generateCode(targetOCC, sourceOCC, createNewVarName, null, null, idxVar, logLevel, varNamesSet,
				varNamesMap);
	}

	/**
	 * Generate code.
	 *
	 * @param targetOCC        the target OCC
	 * @param sourceOCC        the source OCC
	 * @param createNewVarName the create new var name
	 * @param value            the value
	 * @param idx              the idx
	 * @param idxVar           the idx var
	 * @param logLevel         the log level
	 * @param varNamesSet      the var names set
	 * @param varNamesMap      the var names map
	 * @return the string
	 */
	private static String generateCode(TargetOtcCommandContext targetOCC, OtcCommandContext sourceOCC,
			boolean createNewVarName, String value, Integer idx, String idxVar, LogLevel logLevel,
			Set<String> varNamesSet, Map<String, String> varNamesMap) {
		OtcCommandDto valueOCD = targetOCC.otcCommandDto;
		if (!valueOCD.isMapValue()) {
			throw new CodeGeneratorException("", "Invalid call to method in OTC-command : " + targetOCC.commandId
					+ ". Command Object is not of Map-value type.");
		}
		StringBuilder codeSectionBuilder = new StringBuilder();
		OtcCommandDto parentOCD = valueOCD.parent;
		OtcCommandDto keyOCD = parentOCD.children.get(OtcConstants.MAP_KEY_REF + parentOCD.fieldName);
		String keyFieldType = fetchFieldTypeName(targetOCC, null, keyOCD, createNewVarName, varNamesMap);
		String keyFieldTypecastType = fetchSanitizedTypeName(targetOCC, keyOCD);
		String keyVarName = createVarName(keyOCD, createNewVarName, varNamesSet, varNamesMap);
		String getMapKeyValueICDCode = null;
		String keyPcdId = createIcdKey(keyOCD, idxVar, idx);
		String valuePcdId = createIcdKey(valueOCD, idxVar, idx);
		if (idxVar != null || idx != null) {
			int endIdx = targetOCC.otcChain.lastIndexOf(OtcConstants.MAP_VALUE_REF) + 3;
			String mapValueTokenPath = targetOCC.otcChain.substring(0, endIdx);
			String logMsg = "Corresponding Map-key missing for path: '" + mapValueTokenPath + "'!";
			if (targetOCC.loopsCounter > 0) {
				getMapKeyValueICDCode = String.format(IF_NULL_MAP_KEY_ICD_CONTINUE_TEMPLATE, keyPcdId, logLevel, logMsg,
						valuePcdId);
			} else {
				getMapKeyValueICDCode = String.format(IF_NULL_MAP_KEY_ICD_RETURN_TEMPLATE, keyPcdId, logLevel, logMsg,
						valuePcdId);
			}
			if (targetOCC.hasDescendantCollectionOrMap()) {
				getMapKeyValueICDCode += ASSIGN_VALUE_TO_MEMBER_ICD_TEMPLATE;
			}
		} else {
			getMapKeyValueICDCode = String.format(RETRIEVE_LAST_MAP_KEY_TEMPLATE, keyPcdId, valuePcdId);
		}
		codeSectionBuilder.append(getMapKeyValueICDCode);
		String retrieveMapKeyFromICDCode = String.format(RETRIEVE_MAP_KEY_FROM_ICD_TEMPLATE, keyFieldType, keyVarName,
				keyFieldTypecastType);
		codeSectionBuilder.append(retrieveMapKeyFromICDCode);
		String valueFieldType = fetchFieldTypeName(targetOCC, null, valueOCD, false, varNamesMap);
		OtcCommandDto sourceOCD = null;
		if (sourceOCC != null) {
			sourceOCD = sourceOCC.otcCommandDto;
		}
		String valOrVar = fetchValueOrVar(targetOCC, sourceOCD, value, createNewVarName, varNamesSet, varNamesMap);
		String valueFieldCastType = fetchSanitizedTypeName(targetOCC, valueOCD);
		String valueVarName = createVarName(valueOCD, false, varNamesSet, varNamesMap);
		String mapValueCode = null;
		if (targetOCC.isLeaf()) {
			String createInstanceTemplateCopy = CREATE_INSTANCE_TEMPLATE.replace("new %s()", valOrVar);
			mapValueCode = String.format(createInstanceTemplateCopy, valueFieldType, valueVarName);
			codeSectionBuilder.append(mapValueCode);
			String getterCode = GetterIfNullReturnTemplate.generateCode(targetOCC, valueOCD.parent, createNewVarName,
					varNamesSet, varNamesMap);
			codeSectionBuilder.append(getterCode);
			String mapVarName = createVarName(valueOCD.parent, createNewVarName, varNamesSet, varNamesMap);
			String addMapEntryUpdatePcdCode = String.format(ADD_MAP_ENTRY_UPDATE_ICD_TEMPLATE, mapVarName, keyVarName,
					valueVarName, valueVarName);
			codeSectionBuilder.append(addMapEntryUpdatePcdCode);
		} else if (PackagesFilterUtil.isFilteredPackage(valueOCD.fieldType)) {
			if (valueOCD.isEnum()) {
				String createInstanceTemplateCopy = CREATE_INSTANCE_TEMPLATE.replace("new %s()", valOrVar);
				mapValueCode = String.format(createInstanceTemplateCopy, valueFieldType, valueVarName);
			} else {
				mapValueCode = String.format(RETRIEVE_MAP_VALUE_FROM_ICD_TEMPLATE, valueFieldType, valueVarName,
						valueFieldCastType);
			}
			codeSectionBuilder.append(mapValueCode);
		}
		return addInlineComments(INLINE_COMMENTS, codeSectionBuilder.toString());
	}

	/**
	 * Generate post loop code.
	 *
	 * @param targetOCC        the target OCC
	 * @param createNewVarName the create new var name
	 * @param idx              the idx
	 * @param idxVar           the idx var
	 * @param logLevel         the log level
	 * @param varNamesSet      the var names set
	 * @param varNamesMap      the var names map
	 * @return the string
	 */
	public static String generatePostLoopCode(TargetOtcCommandContext targetOCC, boolean createNewVarName, Integer idx,
			String idxVar, LogLevel logLevel, Set<String> varNamesSet, Map<String, String> varNamesMap) {
		OtcCommandDto valueOCD = targetOCC.otcCommandDto;
		if (!valueOCD.isMapValue()) {
			throw new CodeGeneratorException("", "Invalid call to method in OTC-command : " + targetOCC.commandId
					+ ". Command Object is not of Map-key type.");
		}
		String valueType = fetchFieldTypeName(targetOCC, null, valueOCD, createNewVarName, varNamesMap);
		String valueVarName = createVarName(valueOCD, createNewVarName, varNamesSet, varNamesMap);
		String valueConcreteType = fetchConcreteTypeName(targetOCC, valueOCD);
		boolean hasMapValueInPath = valueOCD.isMapValue() || targetOCC.hasMapValueDescendant();
		String logMsg = null;
		if (hasMapValueInPath) {
			int endIdx = targetOCC.otcChain.lastIndexOf(OtcConstants.MAP_VALUE_REF) + 3;
			String mapValueTokenPath = targetOCC.otcChain.substring(0, endIdx);
			logMsg = "Corresponding Map-key missing for path: '" + mapValueTokenPath + "'!";
		} else {
			logMsg = "'" + valueOCD.tokenPath + "' is null!.";
		}
		String icd = null;
		if (targetOCC.hasAncestralCollectionOrMap()) {
			icd = "parentICD" + idx;
		} else {
			icd = "parentTargetICD";
		}
		String pdcId = createIcdKey(valueOCD, idxVar, null);
		String postTargetLoopMapValueCode = String.format(POST_TARGET_LOOP_MAP_VALUE_TEMPLATE, idx, icd, pdcId, idx,
				logLevel, logMsg, valueType, valueVarName, valueConcreteType, idx);
		return addInlineComments(INLINE_COMMENTS, postTargetLoopMapValueCode);
	}
}
