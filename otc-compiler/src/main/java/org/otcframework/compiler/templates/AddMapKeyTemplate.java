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

import org.otcframework.common.OtcConstants;
import org.otcframework.common.dto.OtcCommandDto;
import org.otcframework.common.util.PackagesFilterUtil;
import org.otcframework.compiler.command.TargetOtcCommandContext;
import org.otcframework.compiler.exception.CodeGeneratorException;

/**
 * The Class AddMapKeyTemplate.
 */
// TODO: Auto-generated Javadoc
public final class AddMapKeyTemplate extends AbstractTemplate {

	/**
	 * Instantiates a new adds the map key template.
	 */
	private AddMapKeyTemplate() {
	}

	/**
	 * Generate code.
	 *
	 * @param targetOCC        the target OCC
	 * @param sourceOCD        the source OCD
	 * @param createNewVarName the create new var name
	 * @param key              the key
	 * @param idx              the idx
	 * @param varNamesSet      the var names set
	 * @param varNamesMap      the var names map
	 * @return the string
	 */
	public static String generateCode(TargetOtcCommandContext targetOCC, OtcCommandDto sourceOCD,
			boolean createNewVarName, String key, Integer idx, Set<String> varNamesSet,
			Map<String, String> varNamesMap) {
		return generateCode(targetOCC, sourceOCD, createNewVarName, key, idx, null, varNamesSet, varNamesMap);
	}

	/**
	 * Generate code.
	 *
	 * @param targetOCC        the target OCC
	 * @param sourceOCD        the source OCD
	 * @param createNewVarName the create new var name
	 * @param idxVar           the idx var
	 * @param varNamesSet      the var names set
	 * @param varNamesMap      the var names map
	 * @return the string
	 */
	public static String generateCode(TargetOtcCommandContext targetOCC, OtcCommandDto sourceOCD,
			boolean createNewVarName, String idxVar, Set<String> varNamesSet, Map<String, String> varNamesMap) {
		return generateCode(targetOCC, sourceOCD, createNewVarName, null, null, idxVar, varNamesSet, varNamesMap);
	}

	/**
	 * Generate code.
	 *
	 * @param targetOCC        the target OCC
	 * @param sourceOCD        the source OCD
	 * @param createNewVarName the create new var name
	 * @param key              the key
	 * @param idx              the idx
	 * @param idxVar           the idx var
	 * @param varNamesSet      the var names set
	 * @param varNamesMap      the var names map
	 * @return the string
	 */
	private static String generateCode(TargetOtcCommandContext targetOCC, OtcCommandDto sourceOCD,
			boolean createNewVarName, String key, Integer idx, String idxVar, Set<String> varNamesSet,
			Map<String, String> varNamesMap) {
		OtcCommandDto keyOCD = targetOCC.otcCommandDto;
		if (!keyOCD.isMapKey()) {
			throw new CodeGeneratorException("", "Invalid call to method in OTC-command : " + targetOCC.commandId
					+ ". Command Object is not of Map-key type.");
		}
		String keyType = fetchFieldTypeName(targetOCC, null, keyOCD, createNewVarName, varNamesMap);
		String keyOrVar = fetchValueOrVar(targetOCC, sourceOCD, key, createNewVarName, varNamesSet, varNamesMap);
		OtcCommandDto parentOCD = keyOCD.parent;
		String mapVarName = createVarName(parentOCD, createNewVarName, varNamesSet, varNamesMap);
		String keyConcreteType = fetchConcreteTypeName(targetOCC, keyOCD);
		String keyVarName = createVarName(keyOCD, createNewVarName, varNamesSet, varNamesMap);
		String createNullVarCode = String.format(createInitVarTemplate, keyType, keyVarName, null);
		keyType = "";
		StringBuilder codeSectionBuilder = new StringBuilder(createNullVarCode);
		OtcCommandDto valueOCD = parentOCD.children.get(OtcConstants.MAP_VALUE_REF + parentOCD.fieldName);
		String valueType = fetchFieldTypeName(targetOCC, null, valueOCD, createNewVarName, varNamesMap);
		String valueVarName = createVarName(valueOCD, createNewVarName, varNamesSet, varNamesMap);
		createNullVarCode = String.format(createInitVarTemplate, valueType, valueVarName, null);
		codeSectionBuilder.append(createNullVarCode);
		String ifMapContainsKeyCode = null;
		if (targetOCC.isLeaf()) {
			ifMapContainsKeyCode = String.format(ifNotContainsMapKeyTemplate, mapVarName, keyOrVar);
		} else {
			if (idxVar != null || idx != null) {
				String icdId = createIcdKey(keyOCD, idxVar, idx);
				ifMapContainsKeyCode = String.format(ifNullMapKeyICDTemplate, icdId);
				String retrieveMapKeyFromICDCode = String.format(retrieveMapKeyFromIcdTemplate, "", keyVarName,
						keyConcreteType);
				ifMapContainsKeyCode = ifMapContainsKeyCode.replace(CODE_TO_ADD_ELSE_MAPENTRY,
						retrieveMapKeyFromICDCode);
			} else {
				ifMapContainsKeyCode = CODE_TO_ADD_MAPENTRY;
			}
		}
		if (idxVar == null && idx != null) {
			idxVar = String.valueOf(idx);
		}
		String addMapEntryCode = String.format(addMapEntryTemplate, mapVarName, keyVarName, valueVarName, keyVarName,
				idxVar, valueVarName, idxVar);
		String createInstanceCode = null;
		if (PackagesFilterUtil.isFilteredPackage(keyOCD.fieldType)) {
			if (keyOCD.isEnum()) {
				String createInstanceTemplateCopy = createInstanceTemplate.replace("new %s()", keyOrVar);
				createInstanceCode = String.format(createInstanceTemplateCopy, "", keyVarName);
			} else {
				createInstanceCode = String.format(createInstanceTemplate, keyType, keyVarName, keyConcreteType);
			}
		} else {
			String createInstanceTemplateCopy = createInstanceTemplate.replace("new %s()", keyOrVar);
			createInstanceCode = String.format(createInstanceTemplateCopy, "", keyVarName);
		}
		addMapEntryCode = addMapEntryCode.replace(CODE_TO_CREATE_MAPKEY, createInstanceCode);
		if (PackagesFilterUtil.isFilteredPackage(valueOCD.fieldType)) {
			if (valueOCD.isEnum()) {
				addMapEntryCode = addMapEntryCode.replace(CODE_TO_CREATE_MAPVALUE, "");
			} else {
				String valueConcreteType = fetchConcreteTypeName(targetOCC, valueOCD);
				createInstanceCode = String.format(createInstanceTemplate, "", valueVarName, valueConcreteType);
				addMapEntryCode = addMapEntryCode.replace(CODE_TO_CREATE_MAPVALUE, createInstanceCode);
			}
		} else {
			addMapEntryCode = addMapEntryCode.replace(CODE_TO_CREATE_MAPVALUE, "");
		}
		if (ifMapContainsKeyCode != null) {
			addMapEntryCode = ifMapContainsKeyCode.replace(CODE_TO_ADD_MAPENTRY, addMapEntryCode);
		}
		codeSectionBuilder.append(addMapEntryCode);
		if (targetOCC.hasDescendantCollectionOrMap()) {
			codeSectionBuilder.append(assignKeyToMemberIcdTemplate);
		}
		return codeSectionBuilder.toString();
	}

	/**
	 * Generate post loop code.
	 *
	 * @param targetOCC        the target OCC
	 * @param createNewVarName the create new var name
	 * @param idx              the idx
	 * @param idxVar           the idx var
	 * @param varNamesSet      the var names set
	 * @param varNamesMap      the var names map
	 * @return the string
	 */
	public static String generatePostLoopCode(TargetOtcCommandContext targetOCC, boolean createNewVarName, Integer idx,
			String idxVar, Set<String> varNamesSet, Map<String, String> varNamesMap) {
		OtcCommandDto keyOCD = targetOCC.otcCommandDto;
		if (!keyOCD.isMapKey()) {
			throw new CodeGeneratorException("", "Invalid call to method in OTC-command : " + targetOCC.commandId
					+ ". Command Object is not of Map-key type.");
		}
		String keyType = fetchFieldTypeName(targetOCC, null, keyOCD, createNewVarName, varNamesMap);
		OtcCommandDto parentOCD = keyOCD.parent;
		String mapVarName = createVarName(parentOCD, createNewVarName, varNamesSet, varNamesMap);
		String keyConcreteType = fetchConcreteTypeName(targetOCC, keyOCD);
		String keyVarName = createVarName(keyOCD, createNewVarName, varNamesSet, varNamesMap);
		OtcCommandDto valueOCD = parentOCD.children.get(OtcConstants.MAP_VALUE_REF + parentOCD.fieldName);
		String valueType = fetchFieldTypeName(targetOCC, null, valueOCD, createNewVarName, varNamesMap);
		String valueVarName = createVarName(valueOCD, createNewVarName, varNamesSet, varNamesMap);
		String valueConcreteType = fetchConcreteTypeName(targetOCC, valueOCD);
		String icd = "parentICD" + idx;
		if (!targetOCC.hasAncestralCollectionOrMap()) {
			icd = "parentTargetICD";
		}
		String keyPcdId = createIcdKey(keyOCD, idxVar, null);
		String valuePcdId = createIcdKey(valueOCD, idxVar, null);
		String postTargetLoopMapKeyCode = String.format(postTargetLoopMapKeyTemplate, idx, icd, keyPcdId, keyType,
				keyVarName, idx, mapVarName, keyVarName, valueVarName, idx, icd, keyVarName, keyPcdId, icd,
				valueVarName, valuePcdId, keyVarName, keyConcreteType, idx);
		String mapKeyCode = null;
		if (PackagesFilterUtil.isFilteredPackage(keyOCD.fieldType)) {
			mapKeyCode = String.format(createInstanceTemplate, "", keyVarName, keyConcreteType);
		} else {
			// TODO - seems there is a mistake in this below line.
			mapKeyCode = String.format(createInstanceTemplate.replace("new %s()", keyVarName), "", keyVarName,
					keyConcreteType);
		}
		postTargetLoopMapKeyCode = postTargetLoopMapKeyCode.replace(CODE_TO_CREATE_MAPKEY, mapKeyCode);
		String mapValueCode = null;
		if (PackagesFilterUtil.isFilteredPackage(keyOCD.fieldType)) {
			mapValueCode = String.format(createInstanceTemplate, valueType, valueVarName, valueConcreteType);
		} else {
			mapValueCode = String.format(createInstanceTemplate.replace("new %s()", "null"), valueType, valueVarName);
		}
		postTargetLoopMapKeyCode = postTargetLoopMapKeyCode.replace(CODE_TO_CREATE_MAPVALUE, mapValueCode);
		return postTargetLoopMapKeyCode;
	}
}
