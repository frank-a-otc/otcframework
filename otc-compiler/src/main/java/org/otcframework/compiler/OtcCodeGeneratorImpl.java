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
package org.otcframework.compiler;

import org.otcframework.common.config.OtcConfig;
import org.otcframework.common.dto.ClassDto;
import org.otcframework.common.dto.OtcChainDto;
import org.otcframework.common.dto.OtcCommandDto;
import org.otcframework.common.dto.OtcDto;
import org.otcframework.common.dto.otc.OtcFileDto;
import org.otcframework.common.exception.OtcException;
import org.otcframework.common.util.CommonUtils;
import org.otcframework.compiler.command.*;
import org.otcframework.compiler.exception.CodeGeneratorException;
import org.otcframework.compiler.templates.ClassBeginTemplate;
import org.otcframework.compiler.templates.MethodEndTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.JavaFileObject;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * The Class OtcCodeGeneratorImpl.
 */
final class OtcCodeGeneratorImpl extends AbstractOtcCodeGenerator implements OtcCodeGenerator {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(OtcCodeGeneratorImpl.class);

	/** The otc code generator. */
	private static OtcCodeGenerator otcCodeGenerator = new OtcCodeGeneratorImpl();

	/** The Constant otcBinDir. */
	private static final String OTC_BIN_DIR = OtcConfig.getCompiledCodeLocation();

	/**
	 * Instantiates a new otc code generator impl.
	 */
	private OtcCodeGeneratorImpl() {

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
			String clzPackage = OTC_BIN_DIR.replace("/", File.separator);
			if (otcDto.otcNamespace != null) {
				clzPackage += otcDto.otcNamespace.replace(".", File.separator) + File.separator;
				file = new File(clzPackage);
				file.mkdirs();
			} else {
				new File(clzPackage);
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
		String classBeginBody = ClassBeginTemplate.generateMainClassCode(mainClassDto, targetType, sourceType,
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
		ExecutionContext executionContext = new ExecutionContext();
		otcDto.scriptDtos.forEach(scriptDto -> {
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
				LOGGER.debug("Generated source-code '{}.java' for Command-Id : {} {}", scriptDto.command.id,
						scriptDto.command.id, scriptDto.command.factoryClassName);
			} catch (Exception ex) {
				LOGGER.error("Error while compiling OTC-Command with Id : {}", scriptDto.command.id);
				throw new CodeGeneratorException(ex);
			}
		});
		String rootTargetVariable = CommonUtils.initLower(targetClz.getSimpleName());
		String endExecuteMethod = MethodEndTemplate.generateCode(rootTargetVariable);
		targetOCC.mainClassDto.codeBuilder.append(endExecuteMethod).append("\n}");
		String javaCode = targetOCC.mainClassDto.codeBuilder.toString();
		String fqClzName = mainClassDto.className;
		JavaCodeStringObject javaStringObject = new JavaCodeStringObject(fqClzName, javaCode);
		List<JavaFileObject> javaFileObjects = new ArrayList<>();
		javaFileObjects.add(javaStringObject);
		otcCommand.createJavaFile(mainClassDto);
	}
}
