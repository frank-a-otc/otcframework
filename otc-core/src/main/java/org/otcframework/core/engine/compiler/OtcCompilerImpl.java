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
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import org.msgpack.MessagePack;
import org.otcframework.common.OtcConstants;
import org.otcframework.common.config.OtcConfig;
import org.otcframework.common.dto.ClassDto;
import org.otcframework.common.dto.DeploymentDto;
import org.otcframework.common.dto.DeploymentDto.CompiledInfo;
import org.otcframework.common.dto.OtcCommandDto;
import org.otcframework.common.dto.OtcDto;
import org.otcframework.common.dto.ScriptDto;
import org.otcframework.common.engine.compiler.CompilationReport;
import org.otcframework.common.util.CommonUtils;
import org.otcframework.common.util.OtcUtils;
import org.otcframework.core.engine.compiler.command.JavaCodeStringObject;
import org.otcframework.core.engine.compiler.exception.CodeGeneratorException;
import org.otcframework.core.engine.compiler.exception.OtcCompilerException;
import org.otcframework.core.engine.exception.OtcEngineException;
import org.otcframework.core.engine.utils.CompilerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

// TODO: Auto-generated Javadoc
/**
 * The Class OtcCompilerImpl.
 */
public class OtcCompilerImpl implements OtcCompiler {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(OtcCompilerImpl.class);
	
	/** The Constant otcCompilerImpl. */
	private static final OtcCompilerImpl otcCompilerImpl = new OtcCompilerImpl();
	
	/** The Constant otcCodeGenerator. */
	private static final OtcCodeGenerator otcCodeGenerator = OtcCodeGeneratorImpl.getInstance();
	
	/** The Constant otcSrcDir. */
	private static final String otcSrcDir = OtcConfig.getOtcSourceLocation();
	
	/** The Constant srcDir. */
	private static final String srcDir = OtcConfig.getSourceCodeLocation();
	
	/** The Constant otcTargetDir. */
	private static final String otcTargetDir = OtcConfig.getCompiledCodeLocation();
	
	/** The Constant otcBinDir. */
	private static final String otcBinDir = OtcConfig.getOtcBinLocation();
	
	private static final boolean compilerSourcecodeFailonerror = OtcConfig.getCompilerSourcecodeFailonerror();

	/** The Constant otcFileFilter. */
	private static final FileFilter otcFileFilter = CommonUtils.createFilenameFilter(OtcConstants.OTC_SCRIPT_EXTN);
	
	/** The Constant depFileFilter. */
	private static final FileFilter depFileFilter = CommonUtils.createFilenameFilter(OtcConstants.OTC_TMD_EXTN);

	/** The Constant msgPack. */
	private static final MessagePack msgPack = new MessagePack();
	
	/** The Constant objectMapper. */
	private static final ObjectMapper objectMapper;

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
    		optionList.add(System.getProperty("java.class.path") + File.pathSeparator + otcTargetDir + 
    				otcLibClassPath.toString());
    	}
	}

	/**
	 * Gets the single instance of OtcCompilerImpl.
	 *
	 * @return single instance of OtcCompilerImpl
	 */
	public static OtcCompilerImpl getInstance() {
		return otcCompilerImpl;
	}

	/**
	 * Compile otc.
	 *
	 * @return the list
	 */
	@Override
	public List<CompilationReport> compileOtc() {
		long startTime = System.nanoTime();
		LOGGER.info("Initiating OTC file compilations in {}", otcSrcDir);
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
		LOGGER.info("Completed {}/{} OTC deployments, Failed : {}/{}. in {} millis.", 
				successful, total, failed, total, ((endTime - startTime) / 1000000.0) );
		if (successful == 0) {
			throw new OtcEngineException("", "Oops... Cannot continue due to 0 deployments!");
		}
		return compilationReports;
	}

	/**
	 * Compile otc.
	 *
	 * @param directory the directory
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
				File binDir = new File(otcBinDir);
				if (!binDir.exists()) {
					binDir.mkdirs();
					binDir = null;
				}
				depFileName = otcBinDir + depFileName;
				DeploymentDto deploymentDto = createDeploymentDto(compilationReport);
				deploymentDto.deploymentFileName = depFileName;
				createDeploymentFile(deploymentDto);
				compilationReports.add(compilationReport);
			}
		}
		return compilationReports;
	}
	
	private void createDeploymentFile(DeploymentDto deploymentDto) {
		FileOutputStream fos = null;
		try {
			String str = objectMapper.writeValueAsString(deploymentDto);
			fos = new FileOutputStream(deploymentDto.deploymentFileName);
	        msgPack.write(fos, str.getBytes());
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
	 * Creates the deployment dto.
	 *
	 * @param compilationReport the compilation report
	 * @return the deployment dto
	 */
	private DeploymentDto createDeploymentDto(CompilationReport compilationReport) {
		DeploymentDto deploymentDto = new DeploymentDto();
		OtcDto otcDto = compilationReport.otcDto;
		deploymentDto.mainClass = otcDto.mainClassDto.fullyQualifiedClassName;
		deploymentDto.sourceClz = otcDto.sourceClz;
		deploymentDto.targetClz = otcDto.targetClz;
		deploymentDto.otcNamespace = otcDto.otcNamespace;
		String otcNamespace = otcDto.otcNamespace;
		deploymentDto.otcFileName = otcDto.otcFileName;
		String deploymentId = otcDto.otcFileName;
		deploymentId = deploymentId.substring(0, deploymentId.lastIndexOf(OtcConstants.OTC_SCRIPT_EXTN));
		if (!CommonUtils.isEmpty(otcNamespace)) {
			deploymentId = otcNamespace + "." + deploymentId;
		}
		deploymentDto.deploymentId = deploymentId;
		List<ScriptDto> scriptDtos = otcDto.scriptDtos;
		for (ScriptDto scriptDto : scriptDtos) {
			if (deploymentDto.compiledInfos == null) {
				deploymentDto.compiledInfos = new LinkedHashMap<>();
			}
			String id = scriptDto.command.id;
			CompiledInfo compiledInfo = new CompiledInfo();
			deploymentDto.compiledInfos.put(id, compiledInfo);
			compiledInfo.factoryClassName = scriptDto.command.factoryClassName;
			if (otcDto.sourceOCDStems != null && scriptDto.sourceOtcChainDto != null) {
				compiledInfo.sourceOtcChainDto = scriptDto.sourceOtcChainDto;
				String[] otcTokens = scriptDto.sourceOtcChainDto.otcTokens;
				compiledInfo.sourceOCDStem = otcDto.sourceOCDStems.get(otcTokens[0]);
				nullifyFields(compiledInfo.sourceOCDStem);
				if (!deploymentDto.isProfilingRequried && (scriptDto.sourceOtcChainDto.collectionCount > 0 ||
						scriptDto.sourceOtcChainDto.dictionaryCount > 0)) {
					deploymentDto.isProfilingRequried = true;
				}
			}
			compiledInfo.targetOtcChainDto = scriptDto.targetOtcChainDto;
			String[] otcTokens = scriptDto.targetOtcChainDto.otcTokens;
			compiledInfo.targetOCDStem = otcDto.targetOCDStems.get(otcTokens[0]);
			nullifyFields(compiledInfo.targetOCDStem);
		}
		return deploymentDto;
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
			for (OtcCommandDto childOCD : otcCommandDto.children.values()) {
				nullifyFields(childOCD); 
			}
		}
	}
	
	/**
	 * Compile otc file.
	 *
	 * @param file the file
	 * @param otcNamespace the otc namespace
	 * @return the compilation report
	 */
	private CompilationReport compileOtcFile(File file, String otcNamespace) {
		OtcDto otcDto = null;
		String otcFileName = file.getName();
		otcNamespace = otcNamespace == null ? "" : otcNamespace;
		CompilationReport.Builder compilationReportBuilder = CompilationReport.newBuilder()
				.addOtcNamespace(otcNamespace)
				.addOtcFileName(otcFileName);
		String message = null;
		try {
			LOGGER.info("Compiling OTC file : {}->{}", otcNamespace, otcFileName);
			long startTime = System.nanoTime();
			otcDto = OtcLexicalizer.lexicalize(file, otcNamespace);
			if (otcDto.scriptDtos == null || otcDto.scriptDtos.size() == 0) {
				throw new CodeGeneratorException("", "No OTC commmands to execute! "
						+ "OTC-Scripts are missing or none are enabled.");
			}
			ClassDto mainClassDto = new ClassDto();
			otcDto.mainClassDto = mainClassDto;
			if (otcDto.otcFileDto != null && otcDto.otcFileDto.metadata != null && 
					otcDto.otcFileDto.metadata.entryClassName != null) {
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
			message = "Successfully compiled OTC file in " + ((endTime - startTime) / 1000000.0)
					 + " millis - OTC-Filename: " + otcNamespace + "->" + otcFileName;
			LOGGER.info(message);
			otcCodeGenerator.generateSourcecode(otcDto);
			compilationReportBuilder.addDidSucceed(true)
				.addOtcDto(otcDto)
				.addMessage(message);
		} catch (Exception ex) {
			message = "Error while compiling OTC file : " + otcNamespace + "->" + otcFileName;
			compilationReportBuilder.addDidSucceed(false)
				.addMessage(message)
				.addCause(ex);
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
		File binDir = new File(otcBinDir);
		List<DeploymentDto> deploymentDtos = null;
		Thread.currentThread().setContextClassLoader(OtcUtils.fetchCurrentURLClassLoader());
		for (File depFile : binDir.listFiles(depFileFilter)) {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(depFile);
				String str = msgPack.read(fis, String.class);
				DeploymentDto deploymentDto = objectMapper.readValue(str, DeploymentDto.class);
				if (deploymentDtos == null) {
					deploymentDtos = new ArrayList<>();
				}
				deploymentDtos.add(deploymentDto);
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
			createCompilationUnitsAndCompile(deploymentDtos, null);
		} catch (OtcCompilerException e) {
			LOGGER.error("", e);
			throw e;
		}
		long endTime = System.nanoTime();
		LOGGER.info("Completed source-Code file compilations in {} millis.", ((endTime - startTime) / 1000000.0));
		return;
	}
	
	/**
	 * Creates the compilation units.
	 *
	 * @param deploymentDtos the deployment dtos
	 * @param javaFileObjects the java file objects
	 * @return the list
	 */
	private List<JavaFileObject> createCompilationUnitsAndCompile(List<DeploymentDto> deploymentDtos, 
			List<JavaFileObject> javaFileObjects) {
		for (DeploymentDto deploymentDto : deploymentDtos) {
			String mainClz = deploymentDto.mainClass;
			String absoluteFileName = srcDir + File.separator + mainClz.replace(".", File.separator) +
					OtcConstants.OTC_GENERATEDCODE_EXTN;
			File file = new File(absoluteFileName);
			if (!file.exists()) {
				throw new OtcCompilerException("", "Main-class " + mainClz + " is missing!.");
			}
			if (javaFileObjects == null) {
				javaFileObjects = new ArrayList<>();
			}
			javaFileObjects.add(new JavaCodeStringObject(file));
			for (CompiledInfo compiledInfo : deploymentDto.compiledInfos.values()) {
				String factoryClassName = compiledInfo.factoryClassName;
				String otcNamespace = deploymentDto.otcNamespace;
				if (!CommonUtils.isEmpty(otcNamespace) && !factoryClassName.startsWith(otcNamespace)) {
					factoryClassName = otcNamespace + "." + factoryClassName;
				}
				absoluteFileName = srcDir + File.separator + factoryClassName.replace(".", File.separator) +
						OtcConstants.OTC_GENERATEDCODE_EXTN;
				file = new File(absoluteFileName);
				if (!file.exists()) {
					throw new OtcCompilerException("", "Factory-class " + factoryClassName + " is missing!.");
				}
				javaFileObjects.add(new JavaCodeStringObject(file));
			}
			// -- compile source-code files...
			compileSourceCode(javaFileObjects, deploymentDto);
		}
		return javaFileObjects;
	}
	
	/**
	 * Compile source code.
	 *
	 * @param javaFileObjects the java file objects
	 */
	private void compileSourceCode(List<JavaFileObject> javaFileObjects, DeploymentDto deploymentDto) {
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
				if (!deploymentDto.isError && diagnostic.getCode().contains("compiler.err")) {
					deploymentDto.isError = true;
				}
				System.out.println(diagnostic);
			});
			if (deploymentDto.isError) {
				createDeploymentFile(deploymentDto);
				if (compilerSourcecodeFailonerror) {
					throw new OtcCompilerException("", "Source code compilation failed.");
				}
			}
		} else {
			javaFileObjects.forEach((javaFile) -> {
				LOGGER.debug("Compiled source code : {}", javaFile.getName());
			});
		}
		return;
	}
}
