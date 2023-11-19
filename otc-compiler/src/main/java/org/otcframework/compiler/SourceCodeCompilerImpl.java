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
import org.otcframework.common.config.OtcConfig;
import org.otcframework.common.dto.*;
import org.otcframework.common.dto.RegistryDto.CompiledInfo;
import org.otcframework.common.util.CommonUtils;
import org.otcframework.common.util.OtcUtils;
import org.otcframework.compiler.command.JavaCodeStringObject;
import org.otcframework.compiler.exception.OtcCompilerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The Class OtcCompilerImpl.
 */
public final class SourceCodeCompilerImpl extends AbstractCompiler implements SourceCodeCompiler {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(SourceCodeCompilerImpl.class);

	/** The Constant otcCompilerImpl. */
	private static final SourceCodeCompiler SOURCE_CODE_COMPILER = new SourceCodeCompilerImpl();

	/** The Constant srcDir. */
	private static final String SOURCE_CODE_LOCATION = OtcConfig.getSourceCodeDirectoryPath();

	/** The Constant otcTargetDir. */
	private static final String OTC_TARGET_LOCATION = OtcConfig.getTargetDirectoryPath();

	/** The Constant depFileFilter. */
	private static final FileFilter TMD_FILE_FILTER = CommonUtils.createFilenameFilter(OtcConstants.OTC_TMD_EXTN);

	/** The Constant optionList. */
	private static final List<String> optionList = new ArrayList<>();
	
	static {
		optionList.add("-classpath");
		String otcLibLocation = OtcConfig.getOtcLibDirectoryPath();
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
	public static SourceCodeCompiler getInstance() {
		return SOURCE_CODE_COMPILER;
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
			try (FileInputStream fis = new FileInputStream(depFile)) {
				byte[] bytes = new byte[fis.available()];
				fis.read(bytes);
				String str = new String(bytes);
				RegistryDto registryDto = OBJECT_MAPPER.readValue(str, RegistryDto.class);
				if (registryDtos == null) {
					registryDtos = new ArrayList<>();
				}
				registryDtos.add(registryDto);
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
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
			String absoluteFileName = SOURCE_CODE_LOCATION + mainClz.replace(".", File.separator)
					+ OtcConstants.SOURCE_CODE_EXTN;
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
						+ OtcConstants.SOURCE_CODE_EXTN;
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
		if (Boolean.FALSE.equals(task.call())) {
			diagnostics.getDiagnostics().forEach(diagnostic -> {
				if (!registryDto.hasError && diagnostic.getCode().contains("compiler.err")) {
					registryDto.hasError = true;
				}
				LOGGER.debug(diagnostic.toString());
			});
			if (registryDto.hasError) {
				throw new OtcCompilerException("", "Source code compilation failed.");
			}
			createRegistrationFile(registryDto);
		} else {
			javaFileObjects.forEach(javaFile ->
				LOGGER.debug("Compiled source code : {}", javaFile.getName()));
		}
	}
}
