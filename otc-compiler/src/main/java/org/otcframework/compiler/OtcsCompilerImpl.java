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

import org.otcframework.common.OtcConstants;
import org.otcframework.common.compiler.CompilationReport;
import org.otcframework.common.config.OtcConfig;
import org.otcframework.common.dto.*;
import org.otcframework.common.dto.RegistryDto.CompiledInfo;
import org.otcframework.common.exception.OtcException;
import org.otcframework.common.util.CommonUtils;
import org.otcframework.common.util.OtcUtils;
import org.otcframework.compiler.exception.CodeGeneratorException;
import org.otcframework.compiler.exception.OtcCompilerException;
import org.otcframework.compiler.utils.CompilerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * The Class OtcCompilerImpl.
 */
public final class OtcsCompilerImpl extends AbstractCompiler implements OtcsCompiler {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(OtcsCompilerImpl.class);

	/** The Constant otcCompilerImpl. */
	private static final OtcsCompiler OTCS_COMPILER = new OtcsCompilerImpl();

	/** The Constant otcCodeGenerator. */
	private static final OtcCodeGenerator OTC_CODE_GENERATOR = OtcCodeGeneratorImpl.getInstance();

	/** The Constant otcSrcDir. */
	private static final String UNIT_TEST_LOCATION = OtcConfig.getUnitTestDirectoryPath();

	/** The Constant otcFileFilter. */
	private static final FileFilter OTC_FILE_FILTER = CommonUtils.createFilenameFilter(OtcConstants.OTC_SCRIPT_EXTN);

	/**
	 * Gets the single instance of OtcsCompilerImpl.
	 *
	 * @return single instance of OtcsCompilerImpl
	 */
	public static OtcsCompiler getInstance() {
		return OTCS_COMPILER;
	}

	/**
	 * Compile otc.
	 *
	 * @return the list
	 */
	@Override
	public List<CompilationReport> compileOtcsFiles() {
		long startTime = System.nanoTime();
		LOGGER.info("Initiating OTCS file compilations in {}", UNIT_TEST_LOCATION);
		File unitTestDirectory = new File(UNIT_TEST_LOCATION);
		if (!unitTestDirectory.exists()) {
			throw new OtcCompilerException("", String.format("Missing '%s' folder.", UNIT_TEST_LOCATION));
		}
		List<CompilationReport> compilationReports = compileAll(unitTestDirectory, null);
		if (compilationReports == null) {
			LOGGER.info("No OTCS files to compile in '{}'", UNIT_TEST_LOCATION);
			return null;
		}
		int successful = 0;
		int failed = 0;
		for (CompilationReport compilationReport : compilationReports) {
			if (compilationReport.didSucceed) {
				successful++;
			} else {
				failed++;
			}
		}
		int total = successful + failed;
		long endTime = System.nanoTime();
		LOGGER.info("Completed {}/{} OTCS compilation(s), Failed : {}/{}. in {} millis.", successful, total, failed, total,
				((endTime - startTime) / 1000000.0));
		if (successful == 0) {
			throw new OtcCompilerException("", "Oops... Cannot continue due to 0 registrations!");
		}
		return compilationReports;
	}

	/**
	 * Compile otc.
	 *
	 * @param directory    the directory
	 * @param otcNamespace the otc namespace
	 * @return the list
	 */
	private List<CompilationReport> compileAll(File directory, String otcNamespace) {
		List<CompilationReport> compilationReports = null;
		for (File file : directory.listFiles(OTC_FILE_FILTER)) {
			if (file.isDirectory()) {
				String newOtcNamespacePackage = otcNamespace == null ? file.getName()
						: otcNamespace + "." + file.getName();
				if (compilationReports == null) {
					compilationReports = compileAll(file, newOtcNamespacePackage);
				} else {
					List<CompilationReport> childCompilationReports = compileAll(file, newOtcNamespacePackage);
					if (childCompilationReports != null) {
						compilationReports.addAll(childCompilationReports);
					}
				}
			} else {
				CompilationReport compilationReport = compileOtcFile(file, otcNamespace);
				if (compilationReports == null) {
					compilationReports = new ArrayList<>();
				}
				int idx = compilationReport.otcFileName.lastIndexOf(OtcConstants.OTC_SCRIPT_EXTN);
				String tmdFileName = compilationReport.otcFileName.substring(0, idx) + OtcConstants.OTC_TMD_EXTN;
				if (!CommonUtils.isTrimmedAndEmpty(compilationReport.otcNamespace)) {
					tmdFileName = compilationReport.otcNamespace + "." + tmdFileName;
				}
				tmdFileName = OTC_TMD_LOCATION + tmdFileName;
				RegistryDto registryDto = createRegistryDto(compilationReport);
				registryDto.registryFileName = tmdFileName;
				createRegistrationFile(registryDto);
				compilationReports.add(compilationReport);
			}
		}
		return compilationReports;
	}

	/**
	 * Creates the registry dto.
	 *
	 * @param compilationReport the compilation report
	 * @return the registry dto
	 */
	private RegistryDto createRegistryDto(CompilationReport compilationReport) {
		RegistryDto registryDto = new RegistryDto();
		OtcDto otcDto = compilationReport.otcDto;
		registryDto.mainClass = otcDto.mainClassDto.fullyQualifiedClassName;
		registryDto.sourceClz = otcDto.sourceClz;
		registryDto.targetClz = otcDto.targetClz;
		registryDto.otcNamespace = otcDto.otcNamespace;
		String otcNamespace = otcDto.otcNamespace;
		registryDto.otcFileName = otcDto.otcFileName;
		registryDto.registryId =
				OtcUtils.createRegistryId(otcNamespace, registryDto.sourceClz, registryDto.targetClz);
		List<ScriptDto> scriptDtos = otcDto.scriptDtos;
		// loop through scriptDtos and register in registry
		scriptDtos.forEach(scriptDto -> {
			if (registryDto.compiledInfos == null) {
				registryDto.compiledInfos = new LinkedHashMap<>();
			}
			String id = scriptDto.command.id;
			CompiledInfo compiledInfo = new CompiledInfo();
			registryDto.compiledInfos.put(id, compiledInfo);
			compiledInfo.factoryClassName = scriptDto.command.factoryClassName;
			if (otcDto.sourceOCDStems != null && scriptDto.sourceOtcChainDto != null) {
				compiledInfo.sourceOtcChainDto = scriptDto.sourceOtcChainDto;
				String[] otcTokens = scriptDto.sourceOtcChainDto.otcTokens;
				compiledInfo.sourceOCDStem = otcDto.sourceOCDStems.get(otcTokens[0]);
				nullifyFields(compiledInfo.sourceOCDStem);
				if (!registryDto.isProfilingRequried && (scriptDto.sourceOtcChainDto.collectionCount > 0
						|| scriptDto.sourceOtcChainDto.dictionaryCount > 0)) {
					registryDto.isProfilingRequried = true;
				}
			}
			compiledInfo.targetOtcChainDto = scriptDto.targetOtcChainDto;
			String[] otcTokens = scriptDto.targetOtcChainDto.otcTokens;
			compiledInfo.targetOCDStem = otcDto.targetOCDStems.get(otcTokens[0]);
			nullifyFields(compiledInfo.targetOCDStem);
		});
		return registryDto;
	}

	/**
	 * Nullify fields.
	 *
	 * @param otcCommandDto the otc command dto
	 */
	private void nullifyFields(OtcCommandDto otcCommandDto) {
		otcCommandDto.field = null;
		otcCommandDto.parent = null;
		if (otcCommandDto.children != null) {
			otcCommandDto.children.values().forEach(this::nullifyFields);
		}
	}

	/**
	 * Compile otc file.
	 *
	 * @param file         the file
	 * @param otcNamespace the otc namespace
	 * @return the compilation report
	 */
	private CompilationReport compileOtcFile(File file, String otcNamespace) {
		OtcDto otcDto = null;
		String otcFileName = file.getName();
		otcNamespace = otcNamespace == null ? "" : otcNamespace;
		CompilationReport.Builder compilationReportBuilder = CompilationReport.newBuilder()
				.addOtcNamespace(otcNamespace).addOtcFileName(otcFileName);
		String message = null;
		try {
			LOGGER.info("Compiling OTCS file : {}->{}", otcNamespace, otcFileName);
			long startTime = System.nanoTime();
			otcDto = OtcLexicalizer.lexicalize(file, otcNamespace);
			if (otcDto.scriptDtos == null || otcDto.scriptDtos.isEmpty()) {
				throw new CodeGeneratorException("",
						"No OTC commmands to execute! " + "OTC-Scripts are missing or none are enabled.");
			}
			ClassDto mainClassDto = new ClassDto();
			otcDto.mainClassDto = mainClassDto;
			if (otcDto.otcFileDto != null && otcDto.otcFileDto.metadata != null
					&& otcDto.otcFileDto.metadata.entryClassName != null) {
				mainClassDto.className = otcDto.otcFileDto.metadata.entryClassName;
				if (!CommonUtils.isTrimmedAndEmpty(otcDto.otcNamespace)) {
					mainClassDto.packageName = otcDto.otcNamespace;
					mainClassDto.fullyQualifiedClassName = mainClassDto.packageName + "." + mainClassDto.className;
				} else {
					mainClassDto.fullyQualifiedClassName = mainClassDto.className;
				}
			} else {
				String mainClassName = CompilerUtil.buildJavaClassName(otcNamespace, otcFileName);
				mainClassDto.fullyQualifiedClassName = mainClassName;
				if (!CommonUtils.isTrimmedAndEmpty(otcDto.otcNamespace)) {
					mainClassDto.packageName = otcNamespace;
					mainClassDto.className = mainClassName.substring(mainClassName.lastIndexOf(".") + 1);
				}
			}
			long endTime = System.nanoTime();
			message = String.format("Successfully compiled OTCS file in %s millis - OTC-Filename: %s -> %s",
					((endTime - startTime) / 1000000.0), otcNamespace, otcFileName);
			LOGGER.info(message);
			OTC_CODE_GENERATOR.generateSourcecode(otcDto);
			compilationReportBuilder.addDidSucceed(true).addOtcDto(otcDto).addMessage(message);
		} catch (OtcException ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw ex;
		} catch (Exception ex) {
			message = String.format("Error while compiling OTCS file : %s -> %s! %s", otcNamespace, otcFileName, ex.getMessage());
			LOGGER.error(message, ex);
			throw new OtcCompilerException(message, ex);
		}
		compilationReportBuilder.addMessage(message);
		return compilationReportBuilder.build();
	}

}
