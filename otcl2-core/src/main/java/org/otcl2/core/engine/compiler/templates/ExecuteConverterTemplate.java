package org.otcl2.core.engine.compiler.templates;

import java.util.Map;
import java.util.Set;

import org.otcl2.common.dto.OtclCommandDto;
import org.otcl2.core.engine.compiler.command.SourceOtclCommandContext;
import org.otcl2.core.engine.compiler.command.TargetOtclCommandContext;

public final class ExecuteConverterTemplate extends AbstractTemplate {

	private ExecuteConverterTemplate() {}

	public static String generateCode(TargetOtclCommandContext targetOCC, SourceOtclCommandContext sourceOCC,
			boolean createNewVarName, Set<String> varNamesSet, Map<String, String> varNamesMap) {
		OtclCommandDto targetOCD = targetOCC.otclCommandDto;
		OtclCommandDto sourceOCD = sourceOCC.otclCommandDto;
		String targetVarName = createVarName(targetOCD, createNewVarName, varNamesSet, varNamesMap);
		String sourceVarName = createVarName(sourceOCD, createNewVarName, varNamesSet, varNamesMap);
		String otclConverter = targetOCC.executeOtclConverter;
		otclConverter = targetOCC.factoryClassDto.addImport(otclConverter);
		String otclConverterVarName = createVarName(otclConverter, varNamesSet, false);
		String executeConverterCode = String.format(executeConverterTemplate, otclConverterVarName, targetVarName,
				sourceVarName);
		return executeConverterCode;
	}
}
