package org.otcl2.core.engine.compiler.templates;

import java.util.Map;
import java.util.Set;

import org.otcl.dateconverters.MutualDateTypesConverterFacade;
import org.otcl2.common.dto.OtclCommandDto;
import org.otcl2.common.util.CommonUtils;
import org.otcl2.common.util.PackagesFilterUtil;
import org.otcl2.core.engine.compiler.command.TargetOtclCommandContext;

public class SetterTemplate extends AbstractTemplate {

	private SetterTemplate() {}

	public static String generateCode(TargetOtclCommandContext targetOCC, boolean createNewVarName, String value,
			Set<String> varNamesSet, Map<String, String> varNamesMap) {
		OtclCommandDto otclCommandDto = targetOCC.otclCommandDto;
		String parentVarName = null;
		if (otclCommandDto.isRootNode) {
			parentVarName = CommonUtils.initLower(otclCommandDto.field.getDeclaringClass().getSimpleName());
			if (parentVarName.contains("$")) {
				parentVarName = parentVarName.replace("$", "");
			}
		} else {
			parentVarName = createVarName(otclCommandDto.parent, false, varNamesSet, varNamesMap);
		}
		String setterCode = null;
		if (PackagesFilterUtil.isFilteredPackage(otclCommandDto.fieldType)) {
			targetOCC.factoryClassDto.addImport(otclCommandDto.fieldType.getName());
		}
		value = createConvertExpression(otclCommandDto, value);
		if (otclCommandDto.enableFactoryHelperSetter) {
			String helper = targetOCC.factoryClassDto.addImport(targetOCC.helper);
			setterCode = String.format(helperSetterTemplate, helper, otclCommandDto.setter, parentVarName, value);
		} else {
			if (MutualDateTypesConverterFacade.isOfAnyDateType(otclCommandDto.fieldType)) {
//				String dateFormat = null;
//				if (targetOCC.scriptDto.copy.from.overrides != null) {
//					for (OtclFileDto.Override override : targetOCC.scriptDto.copy.from.overrides) {
//						if (override.dateFormat != null) {
//							dateFormat = override.dateFormat;
//							break;
//						}
//					}
//				}
				targetOCC.factoryClassDto.addImport(MutualDateTypesConverterFacade.class.getName());
				String clz = fetchSanitizedTypeName(targetOCC, otclCommandDto);
//				if (dateFormat != null) {
//					setterCode = String.format(formattedDateConverterTemplate, parentVarName, otclCommandDto.setter, value,
//							clz, dateFormat);
//				} else {
					setterCode = String.format(dateConverterTemplate, parentVarName, otclCommandDto.setter, value, clz);
//				}
			} else {
				setterCode = String.format(setterTemplate, parentVarName, otclCommandDto.setter, value);
			}
		}
		return setterCode;
	}
}
