/**
* Copyright (c) otcframework.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*
* This file is part of the OTC framework.
* 
*  The OTC framework is free software: you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  the Free Software Foundation, version 3 of the License.
*
*  The OTC framework is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU General Public License for more details.
*
*  A copy of the GNU General Public License is made available as 'License.md' file, 
*  along with OTC framework project.  If not, see <https://www.gnu.org/licenses/>.
*
*/
package org.otcframework.core.engine.compiler.templates;

import java.util.Set;

import org.otcframework.common.dto.ClassDto;
import org.otcframework.common.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class ClassBeginTemplate.
 */
public final class ClassBeginTemplate extends AbstractTemplate {

	/**
	 * Instantiates a new class begin template.
	 */
	private ClassBeginTemplate() {}

	/**
	 * Generate main class code.
	 *
	 * @param mainClassDto the main class dto
	 * @param targetType the target type
	 * @param sourceType the source type
	 * @param otcChain the otc chain
	 * @param varNamesSet the var names set
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
		String classBeginBody = String.format(mainClassBeginCodeTemplate, packageName, mainClassName, sourceType, 
				targetType, targetType, sourceType, rootSourceVariable, targetType, rootTargetVariable, targetType);
		if (CommonUtils.isEmpty(packageName)) {
			classBeginBody = classBeginBody.replace("package ;\n", "");
		}
		return classBeginBody;
	}
	
	/**
	 * Generate factory class code.
	 *
	 * @param classDto the class dto
	 * @param sourceType the source type
	 * @param targetType the target type
	 * @param addLogger the add logger
	 * @param varNamesSet the var names set
	 * @return the string
	 */
	public static String generateFactoryClassCode(ClassDto classDto, String sourceType, String targetType, 
			boolean addLogger, Set<String> varNamesSet) {
		return generateClassCode(classDto, sourceType, targetType, addLogger, factoryClassBeginCodeTemplate, varNamesSet);
	}
	
	/**
	 * Generate module class code.
	 *
	 * @param classDto the class dto
	 * @param sourceType the source type
	 * @param targetType the target type
	 * @param addLogger the add logger
	 * @param varNamesSet the var names set
	 * @return the string
	 */
	public static String generateModuleClassCode(ClassDto classDto, String sourceType, String targetType,
			boolean addLogger, Set<String> varNamesSet) {
		return generateClassCode(classDto, sourceType, targetType, addLogger, factoryModuleClassBeginCodeTemplate,
				varNamesSet);
	}
	
	/**
	 * Generate class code.
	 *
	 * @param classDto the class dto
	 * @param sourceType the source type
	 * @param targetType the target type
	 * @param addLogger the add logger
	 * @param template the template
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
			String supportClassBeginTemplateCopy = template.replace(loggerInitTemplate, "");
			classBeginBody = String.format(supportClassBeginTemplateCopy, packageName, classDto.className, 
					sourceType, sourceVar, targetType, targetVar);
		} else {
			classDto.addImport(Logger.class.getName());
			classDto.addImport(LoggerFactory.class.getName());
			classBeginBody = String.format(template, packageName, classDto.className, classDto.className, 
					sourceType, sourceVar, targetType, targetVar);
		}
		if (CommonUtils.isEmpty(packageName)) {
			classBeginBody = classBeginBody.replace("package ;\n", "");
		}
		return classBeginBody;
	}

}
