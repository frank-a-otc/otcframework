package org.otcl2.core.engine.compiler.templates;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import org.otcl2.common.OtclConstants;
import org.otcl2.common.OtclConstants.LogLevel;
import org.otcl2.common.dto.OtclCommandDto;
import org.otcl2.common.util.CommonUtils;
import org.otcl2.common.util.PackagesFilterUtil;
import org.otcl2.core.engine.compiler.command.OtclCommand;
import org.otcl2.core.engine.compiler.command.TargetOtclCommandContext;
import org.otcl2.core.engine.compiler.exception.CodeGeneratorException;

public final class AddToCollectionTemplate extends AbstractTemplate {

	private AddToCollectionTemplate() {}

	public static String generateCode(TargetOtclCommandContext targetOCC, String value, OtclCommandDto sourceOCD,
			Integer idx, boolean createNewVarName, Set<String> varNamesSet, Map<String, String> varNamesMap) {
		return generateCode(targetOCC, sourceOCD, value, idx, null, createNewVarName, varNamesSet, varNamesMap);
	}
	
	public static String generateCode(TargetOtclCommandContext targetOCC, OtclCommandDto sourceOCD,
			String idxVar, boolean createNewVarName, Set<String> varNamesSet, Map<String, String> varNamesMap) {
		return generateCode(targetOCC, sourceOCD, null, null, idxVar, createNewVarName, varNamesSet, varNamesMap);
	}
	
	private static String generateCode(TargetOtclCommandContext targetOCC, OtclCommandDto sourceOCD, String value, 
			Integer idx, String idxVar, boolean createNewVarName, Set<String> varNamesSet,
			Map<String, String> varNamesMap) {
		OtclCommandDto memberOCD = targetOCC.otclCommandDto;
		if (!memberOCD.isCollectionMember()) {
			throw new CodeGeneratorException("", "Invalid call to method in Script-block : " + targetOCC.scriptId +
					". Type should be a collection member for target-otcl-chain : " + targetOCC.otclChain);
		}
		String memberType = fetchFieldTypeName(targetOCC, null, memberOCD, createNewVarName, varNamesMap);
		String codeToReplace = generateCodeToReplace(targetOCC, memberOCD, value, sourceOCD, idx, createNewVarName,
				varNamesSet, varNamesMap);
		String initMembers = null;
		String varName = createVarName(memberOCD, createNewVarName, varNamesSet, varNamesMap);
		String fieldTypecastType = fetchSanitizedTypeName(targetOCC, memberOCD);
		if (idxVar == null && idx == null) {
			String valOrVar = fetchValueOrVar(targetOCC, sourceOCD, value, createNewVarName, varNamesSet, varNamesMap);
			initMembers = String.format(addCollectionMemberAtEndTemplate, valOrVar);
		} else {
			String icdId = createIcdKey(memberOCD, idxVar, idx);
			initMembers = String.format(addCollectionMemberTemplate, memberType, varName, null, icdId, varName,
					fieldTypecastType, varName, varName, icdId);
		}
		initMembers = initMembers.replace(CODE_TO_REPLACE, codeToReplace);
		return initMembers;
	}
	
	public static String generateCodeToReplace(TargetOtclCommandContext targetOCC, OtclCommandDto memberOCD, String value,
			OtclCommandDto otherOCD, Integer idx, boolean createNewVarName, Set<String> varNamesSet,
			Map<String, String> varNamesMap) {
		String varName = createVarName(memberOCD, createNewVarName, varNamesSet, varNamesMap);
		String valOrVar = fetchValueOrVar(targetOCC, otherOCD, value, createNewVarName, varNamesSet, varNamesMap);
		String concreteType = null;
		if (memberOCD.isEnum()) {
			concreteType = valOrVar;
		} else {
			concreteType = fetchConcreteTypeName(targetOCC, memberOCD);
		}
		OtclCommandDto parentOCD = memberOCD.parent;
		String parentVarName = createVarName(parentOCD, createNewVarName, varNamesSet, varNamesMap);
		String codeToReplace = "";
		if (parentOCD.isArray()) {
			String collectionsParentVarName = null;
			if (parentOCD.isRootNode) {
				collectionsParentVarName = CommonUtils.initLower(parentOCD.field.getDeclaringClass().getSimpleName());
			} else {
				collectionsParentVarName = createVarName(parentOCD, createNewVarName, varNamesSet, varNamesMap);
			}
			targetOCC.factoryClassDto.addImport(Arrays.class.getName());
			if (idx == null) {
				codeToReplace += String.format(resizeArrayAndAddAtEndTemplate, parentVarName, parentVarName, parentVarName,
						parentVarName, valOrVar);					
				String setterCode = SetterTemplate.generateCode(targetOCC, createNewVarName, collectionsParentVarName, 
						varNamesSet, varNamesMap);
				codeToReplace = codeToReplace.replace(CODE_TO_REPLACE, setterCode);
			} else {
				String setter = parentOCD.setter;
				if (parentOCD.enableFactoryHelperSetter) {
					String helper = targetOCC.factoryClassDto.addImport(targetOCC.helper);
					codeToReplace += String.format(helperAddToArrayTemplate, parentVarName, idx, parentVarName,
							parentVarName, helper, setter, collectionsParentVarName, parentVarName, "", varName,
							concreteType, parentVarName, idx, valOrVar);
				} else {
					codeToReplace += String.format(addToArrayTemplate, parentVarName, idx, parentVarName, parentVarName, 
							collectionsParentVarName, setter, parentVarName, "", varName, concreteType, parentVarName, idx,
							valOrVar);
				}
			}
		} else {
			String memberType = fetchFieldTypeName(targetOCC, null, memberOCD, createNewVarName, varNamesMap);
			if (PackagesFilterUtil.isFilteredPackage(memberOCD.fieldType)) {
				if (memberOCD.isEnum()) {
					codeToReplace = String.format(createInitVarTemplate, memberType, varName, concreteType);
				} else {
					codeToReplace = String.format(createInstanceTemplate, memberType, varName, concreteType);
				}
			} else {
				codeToReplace = String.format(createInitVarTemplate, memberType, varName, valOrVar);
			}
			codeToReplace += String.format(addToCollectionTemplate, parentVarName, varName);
		}
		return codeToReplace;
	}

	public static String generatePostLoopCode(TargetOtclCommandContext targetOCC, boolean createNewVarName, Integer idx,
			String idxVar, LogLevel logLevel, String parentPcd, String memberPcd, Set<String> varNamesSet, Map<String,
			String> varNamesMap) {
		StringBuilder forLoopCodeBuilder = new StringBuilder();
		if (idx != null) {
			parentPcd = parentPcd + idx;
			memberPcd = memberPcd + idx;
		}
		OtclCommandDto targetOCD = targetOCC.otclCommandDto;
		OtclCommandDto memberOCD = OtclCommand.retrieveMemberOCD(targetOCC);
		String icdId = targetOCD.otclToken;
		boolean hasAncestor = targetOCC.hasAncestralCollectionOrMap();
		if (!hasAncestor) {
			memberPcd = "targetICD";
			icdId = targetOCD.tokenPath;
		} else {
			if (idx != null) {
				memberPcd = memberPcd + idx ;
			}
		}
		icdId = createIcdKey(memberOCD, idxVar, null);
		boolean hasMapValueInPath = memberOCD.isMapValue() || targetOCC.hasMapValueDescendant();
		if (hasMapValueInPath) {
			int endIdx = targetOCC.otclChain.lastIndexOf(OtclConstants.MAP_VALUE_REF) + 3;
			String mapValueTokenPath = targetOCC.otclChain.substring(0, endIdx);
			String logMsg = "Corresponding Map-key missing for path: '" + mapValueTokenPath + "'!";
			forLoopCodeBuilder.append(String.format(retrieveMemberIcd, idx, parentPcd, icdId));
			forLoopCodeBuilder.append(String.format(postLoopTemplate, idx, idx, idx, logLevel, logMsg));
			forLoopCodeBuilder.append(RetrieveMemberFromPcdTemplate.generateCode(targetOCC, createNewVarName,
					memberPcd, varNamesSet, varNamesMap));
		} else {
			String fieldType = fetchFieldTypeName(targetOCC, null, memberOCD, createNewVarName, varNamesMap);
			String varName = createVarName(memberOCD, createNewVarName, varNamesSet, varNamesMap);
			String concreteType = fetchConcreteTypeName(targetOCC, memberOCD);
			forLoopCodeBuilder.append(String.format(postTargetLoopTemplate, idx, parentPcd, icdId, fieldType,
					varName, idx, varName, concreteType, idx, idx, parentPcd, varName, icdId));
			String createMemberCode = String.format(createInstanceTemplate, "", varName, concreteType);
			String parentVarName = createVarName(targetOCD, createNewVarName, varNamesSet, varNamesMap);
			createMemberCode += String.format(addToCollectionTemplate, parentVarName, varName);
			int startIdx = forLoopCodeBuilder.indexOf(CODE_TO_REPLACE);
			forLoopCodeBuilder.replace(startIdx, startIdx + CODE_TO_REPLACE.length(), createMemberCode);
		}
		return forLoopCodeBuilder.toString();
	}
	
}
