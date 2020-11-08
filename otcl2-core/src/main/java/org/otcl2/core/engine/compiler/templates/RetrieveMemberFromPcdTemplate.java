package org.otcl2.core.engine.compiler.templates;

import java.util.Map;
import java.util.Set;

import org.otcl2.common.dto.OtclCommandDto;
import org.otcl2.core.engine.compiler.command.SourceOtclCommandContext;
import org.otcl2.core.engine.compiler.command.TargetOtclCommandContext;

public final class RetrieveMemberFromPcdTemplate extends AbstractTemplate {

	private RetrieveMemberFromPcdTemplate() {}

	public static String generateCode(TargetOtclCommandContext targetOCC, boolean createNewVarName, String icd,
			Set<String> varNamesSet, Map<String, String> varNamesMap) {
		OtclCommandDto memberOCD = targetOCC.otclCommandDto;
		String memberType = fetchFieldTypeName(targetOCC, null, memberOCD, createNewVarName, varNamesMap);
		String varName = createVarName(memberOCD, createNewVarName, varNamesSet, varNamesMap);
		String typecastType = fetchSanitizedTypeName(targetOCC, memberOCD);
		
		String retrieveTargetObjectFromPcdCode = String.format(retrieveMemberFromIcdTemplate, memberType, varName,
				typecastType, icd);
		return retrieveTargetObjectFromPcdCode;
	}

	public static String generateCode(TargetOtclCommandContext targetOCC, SourceOtclCommandContext sourceOCC, 
			boolean createNewVarName, Set<String> varNamesSet, Map<String, String> varNamesMap) {
		OtclCommandDto memberOCD = sourceOCC.otclCommandDto;
		String memberType = fetchFieldTypeName(targetOCC, sourceOCC, memberOCD, createNewVarName, varNamesMap);
		String varName = createVarName(memberOCD, createNewVarName, varNamesSet, varNamesMap);
		String typecastType = fetchSanitizedTypeName(targetOCC, memberOCD);
		String retrieveSourceObjectFromPcdCode = String.format(retrieveMemberFromIcdTemplate, memberType, varName,
				typecastType, MEMBER_SOURCE_ICD);
		return retrieveSourceObjectFromPcdCode;
	}
}
