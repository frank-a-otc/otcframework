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
// TODO: Auto-generated Javadoc
final public class OtcsCompilerImpl implements OtcsCompiler {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(OtcsCompilerImpl.class);

	/** The Constant otcCompilerImpl. */
	private static final OtcsCompiler otcsCompiler = new OtcsCompilerImpl();

	/** The Constant otcCodeGenerator. */
	private static final OtcCodeGenerator otcCodeGenerator = OtcCodeGeneratorImpl.getInstance();

	/** The Constant otcSrcDir. */
	private static final String otcSrcDir = OtcConfig.getOtcSourceLocation();

	/** The Constant srcDir. */
	private static final String srcDir = OtcConfig.getSourceCodeLocation();

	/** The Constant otcTargetDir. */
	private static final String otcTargetDir = OtcConfig.getCompiledCodeLocation();

	/** The Constant otcTmdDir. */
	private static final String otcTmdDir = OtcConfig.getOtcTmdLocation();

	/** The Constant compilerSourcecodeFailonerror. */
	private static final boolean compilerSourcecodeFailonerror = OtcConfig.getCompilerSourcecodeFailonerror();

	/** The Constant otcFileFilter. */
	private static final FileFilter otcFileFilter = CommonUtils.createFilenameFilter(OtcConstants.OTC_SCRIPT_EXTN);

	/** The Constant depFileFilter. */
	private static final FileFilter depFileFilter = CommonUtils.createFilenameFilter(OtcConstants.OTC_TMD_EXTN);

	/** The Constant msgPack. */
//	private static final MessagePack msgPack = new MessagePack();

	/** The Constant objectMapper. */
	private static final ObjectMapper objectMapper;

	/** The Constant optionList. */
	private static final List<String> optionList = new ArrayList<String>();
	
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
			optionList.add(System.getProperty("java.class.path") + File.pathSeparator + otcTargetDir);
		} else {
			optionList.add(System.getProperty("java.class.path") + File.pathSeparator + otcTargetDir
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
	public List<CompilationReport> compile() {
		long startTime = System.nanoTime();
		LOGGER.info("Initiating OTCS file compilations in {}", otcSrcDir);
		File otcSourceDirectory = new File(otcSrcDir);
		List<CompilationReport> compilationReports = compileOtc(otcSourceDirectory, null);
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
		for (File file : directory.listFiles(otcFileFilter)) {
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
				if (!CommonUtils.isEmpty(compilationReport.otcNamespace)) {
					depFileName = compilationReport.otcNamespace + "." + depFileName;
				}
				File binDir = new File(otcTmdDir);
				if (!binDir.exists()) {
					binDir.mkdirs();
					binDir = null;
				}
				depFileName = otcTmdDir + depFileName;
				RegistryDto registryDto = createregistryDto(compilationReport);
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
//			msgPack.write(fos, str.getBytes());
			fos.write(str.getBytes());
			fos.flush();
		} catch (IOException e) {
			throw new OtcCompilerException(e);
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
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
	private RegistryDto createregistryDto(CompilationReport compilationReport) {
		RegistryDto registryDto = new RegistryDto();
		OtcDto otcDto = compilationReport.otcDto;
		registryDto.mainClass = otcDto.mainClassDto.fullyQualifiedClassName;
		registryDto.sourceClz = otcDto.sourceClz;
		registryDto.targetClz = otcDto.targetClz;
		registryDto.otcNamespace = otcDto.otcNamespace;
		String otcNamespace = otcDto.otcNamespace;
		registryDto.otcFileName = otcDto.otcFileName;
		String registryId = otcDto.otcFileName;
		registryId = registryId.substring(0, registryId.lastIndexOf(OtcConstants.OTC_SCRIPT_EXTN));
		if (!CommonUtils.isEmpty(otcNamespace)) {
			registryId = otcNamespace + "." + registryId;
		}
		registryDto.registryId = registryId;
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
//			for (OtcCommandDto childOCD : otcCommandDto.children.values()) {
			otcCommandDto.children.values().forEach(childOCD -> {
				nullifyFields(childOCD);
			});
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
			if (otcDto.scriptDtos == null || otcDto.scriptDtos.size() == 0) {
				throw new CodeGeneratorException("",
						"No OTC commmands to execute! " + "OTC-Scripts are missing or none are enabled.");
			}
			ClassDto mainClassDto = new ClassDto();
			otcDto.mainClassDto = mainClassDto;
			if (otcDto.otcFileDto != null && otcDto.otcFileDto.metadata != null
					&& otcDto.otcFileDto.metadata.entryClassName != null) {
				mainClassDto.className = otcDto.otcFileDto.metadata.entryClassName;
				if (!CommonUtils.isEmpty(otcDto.otcNamespace)) {
					mainClassDto.packageName = otcDto.otcNamespace;
					mainClassDto.fullyQualifiedClassName = mainClassDto.packageName + "." + mainClassDto.className;
				} else {
					mainClassDto.fullyQualifiedClassName = mainClassDto.className;
				}
			} else {
				String mainClassName = CompilerUtil.buildJavaClassName(otcNamespace, otcFileName);
				mainClassDto.fullyQualifiedClassName = mainClassName;
				if (!CommonUtils.isEmpty(otcDto.otcNamespace)) {
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
		CompilationReport compilationReport = compilationReportBuilder.build();
		return compilationReport;
	}

	/**
	 * Compile source code.
	 */
	@Override
	public void compileSourceCode() {
		LOGGER.info("Compiling source-code files. Please wait.......");
		long startTime = System.nanoTime();
		File binDir = new File(otcTmdDir);
		List<RegistryDto> registryDtos = null;
		Thread.currentThread().setContextClassLoader(OtcUtils.fetchCurrentURLClassLoader());
		for (File depFile : binDir.listFiles(depFileFilter)) {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(depFile);
//				String str = msgPack.read(fis, String.class);
				byte bytes[] = new byte[fis.available()];
				fis.read(bytes);
				String str = new String(bytes);
				RegistryDto registryDto = objectMapper.readValue(str, RegistryDto.class);
				if (registryDtos == null) {
					registryDtos = new ArrayList<>();
				}
				registryDtos.add(registryDto);
			} catch (IOException e) {
				LOGGER.error("", e);
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e) {
						LOGGER.error("", e);
					}
				}
			}
		}
		try {
			createCompilationUnitsAndCompile(registryDtos, null);
		} catch (OtcCompilerException e) {
			LOGGER.error("", e);
			throw e;
		}
		long endTime = System.nanoTime();
		LOGGER.info("Completed source-Code file compilations in {} millis.", ((endTime - startTime) / 1000000.0));
		return;
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
		for (RegistryDto registryDto : registryDtos) {
			String mainClz = registryDto.mainClass;
			String absoluteFileName = srcDir + File.separator + mainClz.replace(".", File.separator)
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
				if (!CommonUtils.isEmpty(otcNamespace) && !factoryClassName.startsWith(otcNamespace)) {
					factoryClassName = otcNamespace + "." + factoryClassName;
				}
				absoluteFileName = srcDir + File.separator + factoryClassName.replace(".", File.separator)
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
		File fileClzPathRoot = new File(otcTargetDir);
		try {
			fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(fileClzPathRoot));
			File fileSrcPathRoot = new File(srcDir);
			fileManager.setLocation(StandardLocation.SOURCE_OUTPUT, Arrays.asList(fileSrcPathRoot));
		} catch (IOException e) {
			LOGGER.error("Could not set root locations!. ", e);
		}
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
		JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, optionList, null,
				javaFileObjects);
		if (!task.call()) {
			diagnostics.getDiagnostics().forEach((diagnostic) -> {
				if (!registryDto.hasError && diagnostic.getCode().contains("compiler.err")) {
					registryDto.hasError = true;
				}
				System.out.println(diagnostic);
			});
			if (registryDto.hasError) {
//				createRegistrationFile(registryDto);
				if (compilerSourcecodeFailonerror) {
					throw new OtcCompilerException("", "Source code compilation failed.");
				}
			}
			createRegistrationFile(registryDto);
		} else {
			javaFileObjects.forEach((javaFile) -> {
				LOGGER.debug("Compiled source code : {}", javaFile.getName());
			});
		}
		return;
	}
}
