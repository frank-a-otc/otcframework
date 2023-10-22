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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.otcframework.common.OtcConstants;
import org.otcframework.common.compiler.CompilationReport;
import org.otcframework.common.config.OtcConfig;
import org.otcframework.common.dto.*;
import org.otcframework.common.dto.RegistryDto.CompiledInfo;
import org.otcframework.common.util.CommonUtils;
import org.otcframework.common.util.OtcUtils;
import org.otcframework.compiler.command.JavaCodeStringObject;
import org.otcframework.compiler.exception.CodeGeneratorException;
import org.otcframework.compiler.exception.OtcCompilerException;
import org.otcframework.compiler.utils.CompilerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * The Class OtcCompilerImpl.
 */
public final class OtcsCompilerImpl implements OtcsCompiler {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(OtcsCompilerImpl.class);

	/** The Constant otcCompilerImpl. */
	private static final OtcsCompiler otcsCompiler = new OtcsCompilerImpl();

	/** The Constant otcCodeGenerator. */
	private static final OtcCodeGenerator otcCodeGenerator = OtcCodeGeneratorImpl.getInstance();

	/** The Constant otcSrcDir. */
	private static final String OTCS_SOURCE_LOCATION = OtcConfig.getOtcSourceLocation();

	/** The Constant srcDir. */
	private static final String SOURCE_CODE_LOCATION = OtcConfig.getSourceCodeLocation();

	/** The Constant otcTargetDir. */
	private static final String OTC_TARGET_LOCATION = OtcConfig.getCompiledCodeLocation();

	/** The Constant otcTmdDir. */
	private static final String OTC_TMD_LOCATION = OtcConfig.getOtcTmdLocation();

	/** The Constant compilerSourcecodeFailonerror. */
	private static final boolean COMPILER_SOURCECODE_FAILONERROR = OtcConfig.getCompilerSourcecodeFailonerror();

	/** The Constant otcFileFilter. */
	private static final FileFilter OTC_FILE_FILTER = CommonUtils.createFilenameFilter(OtcConstants.OTC_SCRIPT_EXTN);

	/** The Constant depFileFilter. */
	private static final FileFilter TMD_FILE_FILTER = CommonUtils.createFilenameFilter(OtcConstants.OTC_TMD_EXTN);

	/** The Constant objectMapper. */
	private static final ObjectMapper objectMapper;

	/** The Constant optionList. */
	private static final List<String> optionList = new ArrayList<>();
	
	static {
		objectMapper = new ObjectMapper();
		objectMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
		optionList.add("-classpath");
		String otcLibLocation = OtcConfig.getOtcLibLocation();
		File directory = new File(otcLibLocation);
		FileFilter fileFilter = CommonUtils.createFilenameFilter(".jar");
		StringBuilder otcLibClassPath = null;
		for (File file : directory.listFiles(fileFilter)) {
			if (otcLibClassPath == null) {
				otcLibClassPath = new StringBuilder();
			}
			if (file.getName().endsWith(".jar") || file.getName().endsWith(".class")) {
				otcLibClassPath.append(File.pathSeparator + file.getAbsolutePath());
			}
		}
		if (otcLibClassPath == null || otcLibClassPath.length() == 0) {
			optionList.add(System.getProperty("java.class.path") + File.pathSeparator + OTC_TARGET_LOCATION);
		} else {
			optionList.add(System.getProperty("java.class.path") + File.pathSeparator + OTC_TARGET_LOCATION
					+ otcLibClassPath.toString());
		}
	}

	/**
	 * Gets the single instance of OtcsCompilerImpl.
	 *
	 * @return single instance of OtcsCompilerImpl
	 */
	public static OtcsCompiler getInstance() {
		return otcsCompiler;
	}

	/**
	 * Compile otc.
	 *
	 * @return the list
	 */
	@Override
	public List<CompilationReport> compileOtcsFiles() {
		long startTime = System.nanoTime();
		LOGGER.info("Initiating OTCS file compilations in {}", OTCS_SOURCE_LOCATION);
		File otcSourceDirectory = new File(OTCS_SOURCE_LOCATION);
		List<CompilationReport> compilationReports = compileOtc(otcSourceDirectory, null);
		if (compilationReports == null) {
			LOGGER.info("No OTCS files to compile in '{}'", OTCS_SOURCE_LOCATION);
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
	private List<CompilationReport> compileOtc(File directory, String otcNamespace) {
		List<CompilationReport> compilationReports = null;
		for (File file : directory.listFiles(OTC_FILE_FILTER)) {
			if (file.isDirectory()) {
				String newOtcNamespacePackage = otcNamespace == null ? file.getName()
						: otcNamespace + "." + file.getName();
				if (compilationReports == null) {
					compilationReports = compileOtc(file, newOtcNamespacePackage);
				} else {
					List<CompilationReport> childCompilationReports = compileOtc(file, newOtcNamespacePackage);
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
				String depFileName = compilationReport.otcFileName.substring(0, idx) + OtcConstants.OTC_TMD_EXTN;
				if (!CommonUtils.isTrimmedAndEmpty(compilationReport.otcNamespace)) {
					depFileName = compilationReport.otcNamespace + "." + depFileName;
				}
				File binDir = new File(OTC_TMD_LOCATION);
				if (!binDir.exists()) {
					binDir.mkdirs();
				}
				depFileName = OTC_TMD_LOCATION + depFileName;
				RegistryDto registryDto = createRegistryDto(compilationReport);
				registryDto.registryFileName = depFileName;
				createRegistrationFile(registryDto);
				compilationReports.add(compilationReport);
			}
		}
		return compilationReports;
	}

	/**
	 * Creates the registration file.
	 *
	 * @param registryDto the registry dto
	 */
	private void createRegistrationFile(RegistryDto registryDto) {
		FileOutputStream fos = null;
		try {
			String str = objectMapper.writeValueAsString(registryDto);
			fos = new FileOutputStream(registryDto.registryFileName);
			fos.write(str.getBytes());
			fos.flush();
		} catch (IOException e) {
			throw new OtcCompilerException(e);
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					LOGGER.error(e.getMessage());
					throw new OtcCompilerException(e);
				}
			}
		}
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
			message = "Successfully compiled OTCS file in " + ((endTime - startTime) / 1000000.0)
					+ " millis - OTC-Filename: " + otcNamespace + "->" + otcFileName;
			LOGGER.info(message);
			otcCodeGenerator.generateSourcecode(otcDto);
			compilationReportBuilder.addDidSucceed(true).addOtcDto(otcDto).addMessage(message);
		} catch (Exception ex) {
			message = "Error while compiling OTCS file : " + otcNamespace + "->" + otcFileName;
			compilationReportBuilder.addDidSucceed(false).addMessage(message).addCause(ex);
			LOGGER.error(message, ex);
		}
		compilationReportBuilder.addMessage(message);
		return compilationReportBuilder.build();
	}

	/**
	 * Compile source code.
	 */
	@Override
	public void compileSourceCode() {
		LOGGER.info("Compiling source-code files. Please wait.......");
		long startTime = System.nanoTime();
		File binDir = new File(OTC_TMD_LOCATION);
		File[] files = binDir.listFiles(TMD_FILE_FILTER);
		if (files == null) {
			LOGGER.info("No Token-Metadata file(s) found in '{}' for registration", OTC_TMD_LOCATION);
			return;
		}
		List<RegistryDto> registryDtos = null;
		Thread.currentThread().setContextClassLoader(OtcUtils.fetchCurrentURLClassLoader());
		for (File depFile : files) {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(depFile);
				byte bytes[] = new byte[fis.available()];
				fis.read(bytes);
				String str = new String(bytes);
				RegistryDto registryDto = objectMapper.readValue(str, RegistryDto.class);
				if (registryDtos == null) {
					registryDtos = new ArrayList<>();
				}
				registryDtos.add(registryDto);
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e) {
						LOGGER.error(e.getMessage(), e);
					}
				}
			}
		}
		try {
			createCompilationUnitsAndCompile(registryDtos, null);
		} catch (OtcCompilerException e) {
			LOGGER.error(e.getMessage(), e);
			throw e;
		}
		long endTime = System.nanoTime();
		LOGGER.info("Completed source-Code compilation-phase in {} millis.", ((endTime - startTime) / 1000000.0));
	}

	/**
	 * Creates the compilation units and compile.
	 *
	 * @param registryDtos  the registry dtos
	 * @param javaFileObjects the java file objects
	 * @return the list
	 */
	private List<JavaFileObject> createCompilationUnitsAndCompile(List<RegistryDto> registryDtos,
			List<JavaFileObject> javaFileObjects) {
		if (registryDtos == null) {
			LOGGER.info("Registry has no record of Java source-code file(s) for compilation");
			return null;
		}
		for (RegistryDto registryDto : registryDtos) {
			String mainClz = registryDto.mainClass;
			String absoluteFileName = SOURCE_CODE_LOCATION + File.separator + mainClz.replace(".", File.separator)
					+ OtcConstants.OTC_GENERATEDCODE_EXTN;
			File file = new File(absoluteFileName);
			if (!file.exists()) {
				throw new OtcCompilerException("", "Main-class " + mainClz + " is missing!.");
			}
			if (javaFileObjects == null) {
				javaFileObjects = new ArrayList<>();
			}
			javaFileObjects.add(new JavaCodeStringObject(file));
			for (CompiledInfo compiledInfo : registryDto.compiledInfos.values()) {
				String factoryClassName = compiledInfo.factoryClassName;
				String otcNamespace = registryDto.otcNamespace;
				if (!CommonUtils.isTrimmedAndEmpty(otcNamespace) && !factoryClassName.startsWith(otcNamespace)) {
					factoryClassName = otcNamespace + "." + factoryClassName;
				}
				absoluteFileName = SOURCE_CODE_LOCATION + File.separator + factoryClassName.replace(".", File.separator)
						+ OtcConstants.OTC_GENERATEDCODE_EXTN;
				file = new File(absoluteFileName);
				if (!file.exists()) {
					throw new OtcCompilerException("", "Factory-class " + factoryClassName + " is missing!.");
				}
				javaFileObjects.add(new JavaCodeStringObject(file));
			}
			// -- compile source-code files...
			compileSourceCode(javaFileObjects, registryDto);
		}
		return javaFileObjects;
	}

	/**
	 * Compile source code.
	 *
	 * @param javaFileObjects the java file objects
	 * @param registryDto   the registry dto
	 */
	private void compileSourceCode(List<JavaFileObject> javaFileObjects, RegistryDto registryDto) {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
		File fileClzPathRoot = new File(OTC_TARGET_LOCATION);
		try {
			fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(fileClzPathRoot));
			File fileSrcPathRoot = new File(SOURCE_CODE_LOCATION);
			fileManager.setLocation(StandardLocation.SOURCE_OUTPUT, Arrays.asList(fileSrcPathRoot));
		} catch (IOException e) {
			LOGGER.error("Could not set root locations!. ", e);
		}
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
		JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, optionList, null,
				javaFileObjects);
		if (!task.call()) {
			diagnostics.getDiagnostics().forEach(diagnostic -> {
				if (!registryDto.hasError && diagnostic.getCode().contains("compiler.err")) {
					registryDto.hasError = true;
				}
				LOGGER.debug(diagnostic.toString());
			});
			if (registryDto.hasError && COMPILER_SOURCECODE_FAILONERROR) {
				throw new OtcCompilerException("", "Source code compilation failed.");
			}
			createRegistrationFile(registryDto);
		} else {
			javaFileObjects.forEach(javaFile ->
				LOGGER.debug("Compiled source code : {}", javaFile.getName()));
		}
	}
}
