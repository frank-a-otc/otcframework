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
import org.otcl2.common.dto.OtclCommandDto;
import org.otcl2.common.util.PackagesFilterUtil;
import org.otcl2.core.engine.compiler.command.TargetOtclCommandContext;
import org.otcl2.core.engine.compiler.exception.CodeGeneratorException;

// TODO: Auto-generated Javadoc
/**
 * The Class AddMapKeyTemplate.
 */
public final class AddMapKeyTemplate extends AbstractTemplate {

	/**
	 * Instantiates a new adds the map key template.
	 */
	private AddMapKeyTemplate() {}

	/**
	 * Generate code.
	 *
	 * @param targetOCC the target OCC
	 * @param sourceOCD the source OCD
	 * @param createNewVarName the create new var name
	 * @param key the key
	 * @param idx the idx
	 * @param varNamesSet the var names set
	 * @param varNamesMap the var names map
	 * @return the string
	 */
	public static String generateCode(TargetOtclCommandContext targetOCC, OtclCommandDto sourceOCD,
			boolean createNewVarName, String key, Integer idx, Set<String> varNamesSet, Map<String, String> varNamesMap) {
		return generateCode(targetOCC, sourceOCD, createNewVarName, key, idx, null, varNamesSet, varNamesMap);
	}
	
	/**
	 * Generate code.
	 *
	 * @param targetOCC the target OCC
	 * @param sourceOCD the source OCD
	 * @param createNewVarName the create new var name
	 * @param idxVar the idx var
	 * @param varNamesSet the var names set
	 * @param varNamesMap the var names map
	 * @return the string
	 */
	public static String generateCode(TargetOtclCommandContext targetOCC, OtclCommandDto sourceOCD, 
			boolean createNewVarName, String idxVar, Set<String> varNamesSet, Map<String, String> varNamesMap) {
		return generateCode(targetOCC, sourceOCD, createNewVarName, null, null, idxVar, varNamesSet, varNamesMap);
	}
	
	/**
	 * Generate code.
	 *
	 * @param targetOCC the target OCC
	 * @param sourceOCD the source OCD
	 * @param createNewVarName the create new var name
	 * @param key the key
	 * @param idx the idx
	 * @param idxVar the idx var
	 * @param varNamesSet the var names set
	 * @param varNamesMap the var names map
	 * @return the string
	 */
	private static String generateCode(TargetOtclCommandContext targetOCC, OtclCommandDto sourceOCD, 
			boolean createNewVarName, String key, Integer idx, String idxVar, Set<String> varNamesSet, Map<String, String> varNamesMap) {
		OtclCommandDto keyOCD = targetOCC.otclCommandDto;
		if (!keyOCD.isMapKey()) {
			throw new CodeGeneratorException("", "Invalid call to method in Script-block : " + targetOCC.scriptId + 
					". Command Object is not of Map-key type.");
		}
		String keyType = fetchFieldTypeName(targetOCC, null, keyOCD, createNewVarName, varNamesMap);
		String keyOrVar = fetchValueOrVar(targetOCC, sourceOCD, key, createNewVarName, varNamesSet, varNamesMap);
		OtclCommandDto parentOCD = keyOCD.parent;
		String mapVarName = createVarName(parentOCD, createNewVarName, varNamesSet, varNamesMap);
		String keyConcreteType = fetchConcreteTypeName(targetOCC, keyOCD);
		String keyVarName = createVarName(keyOCD, createNewVarName, varNamesSet, varNamesMap);
		String createNullVarCode = String.format(createInitVarTemplate, keyType, keyVarName, null);
		keyType = "";
		StringBuilder codeSectionBuilder = new StringBuilder(createNullVarCode);
		
		OtclCommandDto valueOCD = parentOCD.children.get(OtclConstants.MAP_VALUE_REF + parentOCD.fieldName);
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
				ifMapContainsKeyCode = ifMapContainsKeyCode.replace(CODE_TO_ADD_ELSE_MAPENTRY, retrieveMapKeyFromICDCode);
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
	 * @param targetOCC the target OCC
	 * @param createNewVarName the create new var name
	 * @param idx the idx
	 * @param idxVar the idx var
	 * @param varNamesSet the var names set
	 * @param varNamesMap the var names map
	 * @return the string
	 */
	public static String generatePostLoopCode(TargetOtclCommandContext targetOCC, boolean createNewVarName, Integer idx,
			String idxVar, Set<String> varNamesSet, Map<String, String> varNamesMap) {
		OtclCommandDto keyOCD = targetOCC.otclCommandDto;
		if (!keyOCD.isMapKey()) {
			throw new CodeGeneratorException("", "Invalid call to method in Script-block : " + targetOCC.scriptId + 
					". Command Object is not of Map-key type.");
		}
		String keyType = fetchFieldTypeName(targetOCC, null, keyOCD, createNewVarName, varNamesMap);
		OtclCommandDto parentOCD = keyOCD.parent;
		String mapVarName = createVarName(parentOCD, createNewVarName, varNamesSet, varNamesMap);
		String keyConcreteType = fetchConcreteTypeName(targetOCC, keyOCD);
		String keyVarName = createVarName(keyOCD, createNewVarName, varNamesSet, varNamesMap);

		OtclCommandDto valueOCD = parentOCD.children.get(OtclConstants.MAP_VALUE_REF + parentOCD.fieldName);
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
				keyVarName, idx, mapVarName, keyVarName, valueVarName, idx, icd, keyVarName, keyPcdId, icd, valueVarName,
				valuePcdId, keyVarName, keyConcreteType, idx);
		String mapKeyCode = null;
		if (PackagesFilterUtil.isFilteredPackage(keyOCD.fieldType)) {
			mapKeyCode = String.format(createInstanceTemplate, "", keyVarName, keyConcreteType);
		} else {
			//TODO - there is a mistake in this below line.
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
