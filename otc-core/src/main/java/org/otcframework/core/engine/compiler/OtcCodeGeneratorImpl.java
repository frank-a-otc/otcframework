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
package org.otcframework.core.engine.compiler;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.tools.JavaFileObject;

import org.otcframework.common.config.OtcConfig;
import org.otcframework.common.dto.ClassDto;
import org.otcframework.common.dto.OtcChainDto;
import org.otcframework.common.dto.OtcCommandDto;
import org.otcframework.common.dto.OtcDto;
import org.otcframework.common.dto.ScriptDto;
import org.otcframework.common.dto.otc.OtcFileDto;
import org.otcframework.common.exception.OtcException;
import org.otcframework.common.util.CommonUtils;
import org.otcframework.core.engine.compiler.command.ExecutionContext;
import org.otcframework.core.engine.compiler.command.JavaCodeStringObject;
import org.otcframework.core.engine.compiler.command.OtcCommand;
import org.otcframework.core.engine.compiler.command.SourceOtcCommandContext;
import org.otcframework.core.engine.compiler.command.TargetOtcCommandContext;
import org.otcframework.core.engine.compiler.exception.CodeGeneratorException;
import org.otcframework.core.engine.compiler.templates.ClassBeginTemplate;
import org.otcframework.core.engine.compiler.templates.MethodEndTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class OtcCodeGeneratorImpl.
 */
// TODO: Auto-generated Javadoc
final class OtcCodeGeneratorImpl extends AbstractOtcCodeGenerator implements OtcCodeGenerator {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(OtcCodeGeneratorImpl.class);

	/** The otc code generator. */
	private static OtcCodeGenerator otcCodeGenerator = new OtcCodeGeneratorImpl();

	/** The Constant otcBinDir. */
	private static final String otcBinDir = OtcConfig.getCompiledCodeLocation();

	/**
	 * Instantiates a new otc code generator impl.
	 */
	private OtcCodeGeneratorImpl() {
		otcCodeGenerator = this;
	}

	/**
	 * Gets the single instance of OtcCodeGeneratorImpl.
	 *
	 * @return single instance of OtcCodeGeneratorImpl
	 */
	public static OtcCodeGenerator getInstance() {
		return otcCodeGenerator;
	}

	/**
	 * Generate sourcecode.
	 *
	 * @param otcDto the otc dto
	 */
	@Override
	public void generateSourcecode(OtcDto otcDto) {
		LOGGER.info("Starting Source-Code generator. Please wait.......");
		long startTime = System.nanoTime();
		OtcFileDto otcFileDto = otcDto.otcFileDto;
		ClassDto mainClassDto = otcDto.mainClassDto;
		try {
			File file = null;
			String clzPackage = otcBinDir.replace("/", File.separator);
			if (otcDto.otcNamespace != null) {
				clzPackage += otcDto.otcNamespace.replace(".", File.separator) + File.separator;
				file = new File(clzPackage);
				file.mkdirs();
			} else {
				file = new File(clzPackage);
			}
			generateSourceCode(otcDto, otcFileDto, mainClassDto);
		} catch (Exception e) {
			if (!(e instanceof OtcException)) {
				throw new CodeGeneratorException(e);
			} else {
				throw (OtcException) e;
			}
		}
		long endTime = System.nanoTime();
		LOGGER.info("Source-Code generation completed in {} millis.", ((endTime - startTime) / 1000000.0));
		return;
	}

	/**
	 * Generate source code.
	 *
	 * @param otcDto       the otc dto
	 * @param otcFileDto   the otc file dto
	 * @param mainClassDto the main class dto
	 */
	private static void generateSourceCode(OtcDto otcDto, OtcFileDto otcFileDto, ClassDto mainClassDto) {
		Map<String, OtcCommandDto> sourceOCDStems = otcDto.sourceOCDStems;
		Map<String, OtcCommandDto> targetOCDStems = otcDto.targetOCDStems;
		Class<?> sourceClz = otcDto.sourceClz;
		Class<?> targetClz = otcDto.targetClz;
		SourceOtcCommandContext sourceOCC = new SourceOtcCommandContext();
		TargetOtcCommandContext targetOCC = new TargetOtcCommandContext();
		targetOCC.mainClassDto = mainClassDto;
		OtcCommand otcCommand = new OtcCommand();
		String otcNamespace = otcDto.otcNamespace;
		String targetType = targetOCC.factoryClassDto.addImport(targetClz.getName());
		String sourceType = null;
		if (sourceClz != null) {
			sourceType = targetOCC.factoryClassDto.addImport(sourceClz.getName());
		}
		String classBeginBody = ClassBeginTemplate.generateMainClassCode(mainClassDto, targetType, sourceType, null,
				new HashSet<>());
		String codeToImport = "\nimport " + targetClz.getName() + ";";
		if (sourceClz != null) {
			codeToImport += "\nimport " + sourceClz.getName() + ";";
		}
		classBeginBody = classBeginBody.replace(OtcCommand.CODE_TO_IMPORT, codeToImport);
		targetOCC.mainClassDto.codeBuilder.append(classBeginBody);
		if (otcFileDto.metadata != null) {
			targetOCC.helper = otcFileDto.metadata.helper;
		}
		List<JavaFileObject> javaFileObjects = null;
		ExecutionContext executionContext = new ExecutionContext();
		for (ScriptDto scriptDto : otcDto.scriptDtos) {
			try {
				if (scriptDto.command.debug) {
					@SuppressWarnings("unused")
					int dummy = 0;
					// -- not guaranteed to be on first iteration - coz this may not be the
					// --- first scriptDto in the group marked as debug
				}
				targetOCC.factoryClassDto.packageName = otcNamespace;
				resetOCC(targetOCC, scriptDto);
				OtcCommandDto targetOCD = targetOCDStems.get(targetOCC.otcTokens[0]);
				targetOCC.otcCommandDto = targetOCD;
				OtcChainDto targetOtcChainDto = scriptDto.targetOtcChainDto;
				targetOCC.collectionsCount = targetOtcChainDto.collectionCount + targetOtcChainDto.dictionaryCount;
				resetOCC(sourceOCC, scriptDto);
				OtcChainDto sourceOtcChainDto = scriptDto.sourceOtcChainDto;
				if (sourceOtcChainDto != null) {
					OtcCommandDto sourceOCD = sourceOCDStems.get(sourceOCC.otcTokens[0]);
					sourceOCC.otcCommandDto = sourceOCD;
					sourceOCC.collectionsCount = sourceOtcChainDto.collectionCount + sourceOtcChainDto.dictionaryCount;
				}
				boolean isCopyValues = false;
				boolean isExtensions = false;
				if (javaFileObjects == null) {
					javaFileObjects = new ArrayList<>();
				}
				executionContext.otcCommand = otcCommand;
				executionContext.targetClz = targetClz;
				executionContext.sourceClz = sourceClz;
				executionContext.targetOCC = targetOCC;
				executionContext.sourceOCC = sourceOCC;
				if (scriptDto.hasSetValues) {
					CopyValuesCommandCodeGenerator.generateSourceCode(executionContext);
					isCopyValues = true;
				}
				if (scriptDto.hasExecuteModule || scriptDto.hasExecuteConverter) {
					ExecuteCommandCodeGenerator.generateSourceCode(executionContext);
					isExtensions = true;
				}
				if (!isCopyValues && !isExtensions) {
					if (targetOCC.collectionsCount > 0 && sourceOCC.collectionsCount > 0) {
						executionContext.initCollectionSizeType();
						CopyCollectionPathsCodeGenerator.generateSourceCode(executionContext);
					} else {
						CopyFlatAndMixedPathsCodeGenerator.generateSourceCode(executionContext);
					}
				}
				LOGGER.debug(
						"Generated source-code '{}.java' for Command-Id : {} " + scriptDto.command.factoryClassName,
						scriptDto.command.id);
			} catch (Exception ex) {
				LOGGER.error("Error while compiling OTC-Command with Id : {}", scriptDto.command.id);
				throw new CodeGeneratorException(ex);
			}
		}
		String rootTargetVariable = CommonUtils.initLower(targetClz.getSimpleName());
		String endExecuteMethod = MethodEndTemplate.generateCode(rootTargetVariable);
		targetOCC.mainClassDto.codeBuilder.append(endExecuteMethod).append("\n}");
		String javaCode = targetOCC.mainClassDto.codeBuilder.toString();
		String fqClzName = mainClassDto.className;
		JavaCodeStringObject javaStringObject = new JavaCodeStringObject(fqClzName, javaCode);
		javaFileObjects.add(javaStringObject);
		otcCommand.createJavaFile(mainClassDto);
		return;
	}
}
