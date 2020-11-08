package org.otcl2.core.engine.compiler.templates;

import java.util.Map;
import java.util.Set;

import org.otcl2.common.OtclConstants;
import org.otcl2.common.OtclConstants.LogLevel;
import org.otcl2.common.dto.OtclCommandDto;
import org.otcl2.core.engine.compiler.command.OtclCommand;
import org.otcl2.core.engine.compiler.command.SourceOtclCommandContext;
import org.otcl2.core.engine.compiler.command.TargetOtclCommandContext;

public final class ForLoopTemplate extends AbstractTemplate {

	private ForLoopTemplate() {}

	public static String generateSourceLoopCode(TargetOtclCommandContext targetOCC, SourceOtclCommandContext sourceOCC,
			String idxPrefix, boolean createNewVarName, LogLevel logLevel, Set<String> varNamesSet,
			Map<String, String> varNamesMap) {
		OtclCommandDto sourceOCD = sourceOCC.otclCommandDto;
		varNamesSet.add(idxPrefix);
		String idxVar = sanitizeVarName(idxPrefix, varNamesSet);
		int idx = 0;
		if (!idxVar.equals(idxPrefix)) {
			idx = Integer.valueOf(idxVar.substring(idxPrefix.length()));
		}
		String parentPcd = "parentICD" + idx;
		String memberPcd = null;
		String icdId = sourceOCD.otclToken;
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
			forLoopCodeBuilder.append(String.format(preSourceLoopTemplateCopy, idx, memberPcd, icdId,
					idx, idx, idx, logLevel, logMsg, idx, idx, idx));
		}
		forLoopCodeBuilder.append(String.format(forLoopTemplate, idxVar, idxVar, idx, idxVar));
		OtclCommandDto memberOCD = OtclCommand.retrieveMemberOCD(sourceOCC);
		sourceOCC.otclCommandDto = memberOCD;
		String fieldType = fetchFieldTypeName(targetOCC, sourceOCC, memberOCD, createNewVarName, varNamesMap);
		String varName = createVarName(memberOCD, createNewVarName, varNamesSet, varNamesMap);
		String concreteType = fetchConcreteTypeName(targetOCC, memberOCD);
		icdId = createIcdKey(memberOCD, idxVar, null);
		forLoopCodeBuilder.append(String.format(retrieveMemberIcd, idx, parentPcd, icdId));
		memberPcd = "memberICD" + idx;
		if (!sourceOCC.hasDescendantCollectionOrMap()) {
			forLoopCodeBuilder.append(String.format(lastPostSourceLoopTemplate, idx, logLevel, logMsg, fieldType, varName,
					concreteType, memberPcd));			
		} else {
			forLoopCodeBuilder.append(String.format(postLoopTemplate, idx, idx, idx, logLevel, logMsg));
			if (!sourceOCC.hasDescendantCollectionOrMap()) {
				forLoopCodeBuilder.append(String.format(retrieveMemberFromIcdTemplate, fieldType, varName,
						concreteType, memberPcd));
			}
		}
		return forLoopCodeBuilder.toString();
	}
	
	public static String generateTargetLoopCode(TargetOtclCommandContext targetOCC, String idxPrefix,
			boolean createNewVarName, LogLevel logLevel, Set<String> varNamesSet, Map<String, String> varNamesMap) {
		OtclCommandDto targetOCD = targetOCC.otclCommandDto;
		varNamesSet.add(idxPrefix);
		String idxVar = sanitizeVarName(idxPrefix, varNamesSet);
		targetOCC.idxVar = idxVar;
		int idx = 0;
		if (!idxVar.equals(idxPrefix)) {
			idx = Integer.valueOf(idxVar.substring(idxPrefix.length()));
		}
		OtclCommandDto memberOCD = OtclCommand.retrieveMemberOCD(targetOCC);
		String memberPcd = null;
		String icdId = targetOCD.otclToken;
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
			int endIdx = targetOCC.otclChain.lastIndexOf(OtclConstants.MAP_VALUE_REF) + 3;
			String mapValueTokenPath = targetOCC.otclChain.substring(0, endIdx);
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
			forLoopCodeBuilder.append(String.format(preLoopTemplateCopy, idx, memberPcd, icdId, idx, idx, idx,
					logLevel, logMsg, idx, idx));
		} else {
			String varName = createVarName(targetOCD, createNewVarName, varNamesSet, varNamesMap);
			if (targetOCD.isMap()) {
				forLoopCodeBuilder.append(String.format(preTargetLoopTemplate.replace(".size();", ".size() / 2;"), idx,
						idx, memberPcd, icdId, idx, idx, memberPcd, varName, icdId, idx, idx, idx));
			} else {
				forLoopCodeBuilder.append(String.format(preTargetLoopTemplate, idx,
						idx, memberPcd, icdId, idx, idx, memberPcd, varName, icdId, idx, idx, idx));
			}
		}
		forLoopCodeBuilder.append(String.format(forLoopTemplate, idxVar, idxVar, idx, idxVar));
		targetOCC.otclCommandDto = memberOCD;
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
		} 
		return forLoopCodeBuilder.toString();
	}
}
