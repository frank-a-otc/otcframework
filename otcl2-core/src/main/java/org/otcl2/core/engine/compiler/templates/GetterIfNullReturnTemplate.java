package org.otcl2.core.engine.compiler.templates;

import java.util.Map;
import java.util.Set;

import org.otcl2.common.OtclConstants;
import org.otcl2.common.OtclConstants.LogLevel;
import org.otcl2.common.OtclConstants.TARGET_SOURCE;
import org.otcl2.common.dto.OtclCommandDto;
import org.otcl2.common.util.CommonUtils;
import org.otcl2.core.engine.compiler.command.SourceOtclCommandContext;
import org.otcl2.core.engine.compiler.command.TargetOtclCommandContext;

public final class GetterIfNullReturnTemplate extends AbstractTemplate {

	private GetterIfNullReturnTemplate() {}

	public static String generateCode(TargetOtclCommandContext targetOCC, OtclCommandDto otclCommandDto,
			boolean createNewVarName, Set<String> varNamesSet, Map<String, String> varNamesMap) {

		String fieldType = fetchFieldTypeName(targetOCC, null, otclCommandDto, createNewVarName, varNamesMap);
		String varName = createVarName(otclCommandDto, createNewVarName, varNamesSet, varNamesMap);
		String parentVarName = null;
		if (otclCommandDto.isRootNode) {
			parentVarName = CommonUtils.initLower(otclCommandDto.field.getDeclaringClass().getSimpleName());
		} else {
			parentVarName = createVarName(otclCommandDto.parent, false, varNamesSet, varNamesMap);
		}
		String getterName = otclCommandDto.getter;
		String getterCode = null;
		if (otclCommandDto.enableFactoryHelperGetter) {
			String helper = targetOCC.factoryClassDto.addImport(targetOCC.helper);
			getterCode = String.format(helperGetterTemplate, fieldType, varName, helper, getterName, parentVarName);
		} else {
			getterCode = String.format(getterTemplate, fieldType, varName, parentVarName, getterName);
		}
		return getterCode;
	}

	public static String generateGetterIfNullReturnCode(TargetOtclCommandContext targetOCC, boolean createNewVarName,
			LogLevel logLevel, Set<String> varNamesSet, Map<String, String> varNamesMap) {
		return generateGetterIfNullReturnCode(targetOCC, null, createNewVarName, logLevel, varNamesSet,
				varNamesMap);
	}
	
	public static String generateGetterIfNullReturnCode(TargetOtclCommandContext targetOCC, 
			SourceOtclCommandContext sourceOCC, boolean createNewVarName, LogLevel logLevel,
			Set<String> varNamesSet, Map<String, String> varNamesMap) {
		OtclCommandDto otclCommandDto = null;
		boolean hasMapValueInPath = false;
		if (sourceOCC != null) {
			otclCommandDto = sourceOCC.otclCommandDto;
			hasMapValueInPath = sourceOCC.hasMapValueMember() || sourceOCC.hasMapValueDescendant();
		} else {
			otclCommandDto = targetOCC.otclCommandDto;
			hasMapValueInPath = targetOCC.hasMapValueMember() || targetOCC.hasMapValueDescendant();
		}
		String fieldType = fetchFieldTypeName(targetOCC, null, otclCommandDto, createNewVarName, varNamesMap);
		String varName = createVarName(otclCommandDto, createNewVarName, varNamesSet, varNamesMap);
		String parentVarName = null;
		if (otclCommandDto.isRootNode) {
			parentVarName = CommonUtils.initLower(otclCommandDto.field.getDeclaringClass().getSimpleName());
		} else {
			OtclCommandDto parentOCD = otclCommandDto.parent;
			parentVarName = createVarName(parentOCD, createNewVarName, varNamesSet, varNamesMap);
		}
		String getter = otclCommandDto.getter;
		String ifNotNullParentChildGetterCode = null;
		String logMsg = null;
		if (TARGET_SOURCE.SOURCE == otclCommandDto.enumTargetSource || !hasMapValueInPath) {
			logMsg = "'" + otclCommandDto.tokenPath + "' is null!.";
		} else {
			int endIdx = targetOCC.otclChain.lastIndexOf(OtclConstants.MAP_VALUE_REF) + 3;
			String mapValueTokenPath = targetOCC.otclChain.substring(0, endIdx);
			logMsg = "Corresponding Map-key missing for path: '" + mapValueTokenPath + "'!";
		}
		if (otclCommandDto.enableFactoryHelperGetter) {
			String helper = targetOCC.factoryClassDto.addImport(targetOCC.helper);
			ifNotNullParentChildGetterCode = String.format(helperGetIfNullReturnTemplate, fieldType, varName, 
					helper, getter, parentVarName, varName, logLevel, logMsg);
		} else {
			ifNotNullParentChildGetterCode = String.format(getterIfNullReturnTemplate, fieldType, varName, 
					parentVarName, getter, varName, logLevel, logMsg);
		}
		return ifNotNullParentChildGetterCode;
	}
}
