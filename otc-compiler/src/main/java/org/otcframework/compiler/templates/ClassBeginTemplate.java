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

import org.otcframework.common.dto.ClassDto;
import org.otcframework.common.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * The Class ClassBeginTemplate.
 */
public final class ClassBeginTemplate extends AbstractTemplate {

	private static final String inlineComments = "\n// ---- generator - " +
			ClassBeginTemplate.class.getSimpleName() + "\n";
	/**
	 * Instantiates a new class begin template.
	 */
	private ClassBeginTemplate() {
	}

	/**
	 * Generate main class code.
	 *
	 * @param mainClassDto the main class dto
	 * @param targetType   the target type
	 * @param sourceType   the source type
	 * @param otcChain     the otc chain
	 * @param varNamesSet  the var names set
	 * @return the string
	 */
	public static String generateMainClassCode(ClassDto mainClassDto, String targetType, String sourceType,
			String otcChain, Set<String> varNamesSet) {
		String rootTargetVariable = CommonUtils.initLower(targetType);
		rootTargetVariable = sanitizeVarName(rootTargetVariable, varNamesSet);
		String rootSourceVariable = null;
		if (sourceType != null) {
			rootSourceVariable = CommonUtils.initLower(sourceType);
			rootSourceVariable = sanitizeVarName(rootSourceVariable, varNamesSet);
		}
		String packageName = mainClassDto.packageName;
		String mainClassName = mainClassDto.className;
		if (packageName == null) {
			packageName = "";
		}
		if (sourceType == null) {
			sourceType = "Object";
			rootSourceVariable = "arg1";
		}
		String classBeginBody = String.format(MAIN_CLASS_BEGIN_CODE_TEMPLATE, packageName, mainClassName, sourceType,
				targetType, targetType, sourceType, rootSourceVariable, targetType, rootTargetVariable, targetType);
		if (CommonUtils.isTrimmedAndEmpty(packageName)) {
			classBeginBody = classBeginBody.replace("package ;\n", "");
		}
		return classBeginBody;
	}

	/**
	 * Generate factory class code.
	 *
	 * @param classDto    the class dto
	 * @param sourceType  the source type
	 * @param targetType  the target type
	 * @param addLogger   the add logger
	 * @param varNamesSet the var names set
	 * @return the string
	 */
	public static String generateFactoryClassCode(ClassDto classDto, String sourceType, String targetType,
			boolean addLogger, Set<String> varNamesSet) {
		return generateClassCode(classDto, sourceType, targetType, addLogger, FACTORY_CLASS_BEGIN_CODE_TEMPLATE,
				varNamesSet);
	}

	/**
	 * Generate module class code.
	 *
	 * @param classDto    the class dto
	 * @param sourceType  the source type
	 * @param targetType  the target type
	 * @param addLogger   the add logger
	 * @param varNamesSet the var names set
	 * @return the string
	 */
	public static String generateModuleClassCode(ClassDto classDto, String sourceType, String targetType,
			boolean addLogger, Set<String> varNamesSet) {
		return generateClassCode(classDto, sourceType, targetType, addLogger, FACTORY_CLASS_BEGIN_CODE_TEMPLATE,
				varNamesSet);
	}

	/**
	 * Generate class code.
	 *
	 * @param classDto    the class dto
	 * @param sourceType  the source type
	 * @param targetType  the target type
	 * @param addLogger   the add logger
	 * @param template    the template
	 * @param varNamesSet the var names set
	 * @return the string
	 */
	private static String generateClassCode(ClassDto classDto, String sourceType, String targetType, boolean addLogger,
			String template, Set<String> varNamesSet) {
		String targetVar = CommonUtils.initLower(targetType);
		targetVar = sanitizeVarName(targetVar, varNamesSet);
		String sourceVar = null;
		if (sourceType != null) {
			sourceVar = CommonUtils.initLower(sourceType);
		}
		sourceVar = sanitizeVarName(sourceVar, varNamesSet);
		String classBeginBody = null;
		String packageName = classDto.packageName;
		if (packageName == null) {
			packageName = "";
		}
		if (sourceType == null) {
			sourceType = "Object";
			sourceVar = "arg1";
		}
		if (!addLogger) {
			String supportClassBeginTemplateCopy = template.replace(LOGGER_INIT_TEMPLATE, "");
			classBeginBody = String.format(supportClassBeginTemplateCopy, packageName, classDto.className, sourceType,
					sourceVar, targetType, targetVar);
		} else {
			classDto.addImport(Logger.class.getName());
			classDto.addImport(LoggerFactory.class.getName());
			classBeginBody = String.format(template, packageName, classDto.className, classDto.className, sourceType,
					sourceVar, targetType, targetVar);
		}
		if (CommonUtils.isTrimmedAndEmpty(packageName)) {
			classBeginBody = classBeginBody.replace("package ;\n", "");
		}
		return classBeginBody;
	}
}
