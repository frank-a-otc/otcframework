package org.otcl2.core.engine.compiler.templates;

import java.util.Map;
import java.util.Set;

import org.otcl2.common.dto.OtclCommandDto;
import org.otcl2.core.engine.compiler.command.SourceOtclCommandContext;
import org.otcl2.core.engine.compiler.command.TargetOtclCommandContext;

// TODO: Auto-generated Javadoc
/**
 * The Class ExecuteConverterTemplate.
 */
public final class ExecuteConverterTemplate extends AbstractTemplate {

	/**
	 * Instantiates a new execute converter template.
	 */
	private ExecuteConverterTemplate() {}

	/**
	 * Generate code.
	 *
	 * @param targetOCC the target OCC
	 * @param sourceOCC the source OCC
	 * @param createNewVarName the create new var name
	 * @param varNamesSet the var names set
	 * @param varNamesMap the var names map
	 * @return the string
	 */
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
