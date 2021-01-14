/**
* Copyright (c) otclfoundation.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*/
package org.otcl2.core.engine.compiler;

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
import org.otcl2.common.OtclConstants;
import org.otcl2.common.config.OtclConfig;
import org.otcl2.common.dto.ClassDto;
import org.otcl2.common.dto.DeploymentDto;
import org.otcl2.common.dto.DeploymentDto.CompiledInfo;
import org.otcl2.common.dto.OtclCommandDto;
import org.otcl2.common.dto.OtclDto;
import org.otcl2.common.dto.ScriptDto;
import org.otcl2.common.engine.compiler.CompilationReport;
import org.otcl2.common.util.CommonUtils;
import org.otcl2.core.engine.compiler.command.JavaCodeStringObject;
import org.otcl2.core.engine.compiler.exception.CodeGeneratorException;
import org.otcl2.core.engine.compiler.exception.OtclCompilerException;
import org.otcl2.core.engine.exception.OtclEngineException;
import org.otcl2.core.engine.utils.CompilerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

// TODO: Auto-generated Javadoc
/**
 * The Class OtclCompilerImpl.
 */
public class OtclCompilerImpl implements OtclCompiler {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(OtclCompilerImpl.class);
	
	/** The Constant otclCompilerImpl. */
	private static final OtclCompilerImpl otclCompilerImpl = new OtclCompilerImpl();
	
	/** The Constant otclCodeGenerator. */
	private static final OtclCodeGenerator otclCodeGenerator = OtclCodeGeneratorImpl.getInstance();
	
	/** The Constant otclSrcDir. */
	private static final String otclSrcDir = OtclConfig.getOtclSourceLocation();
	
	/** The Constant srcDir. */
	private static final String srcDir = OtclConfig.getSourceCodeLocation();
	
	/** The Constant otclTargetDir. */
	private static final String otclTargetDir = OtclConfig.getCompiledCodeLocation();
	
	/** The Constant otclBinDir. */
	private static final String otclBinDir = OtclConfig.getOtclBinLocation();

	/** The Constant otclFileFilter. */
	private static final FileFilter otclFileFilter = CommonUtils.createFilenameFilter(OtclConstants.OTCL_SCRIPT_EXTN);
	
	/** The Constant depFileFilter. */
	private static final FileFilter depFileFilter = CommonUtils.createFilenameFilter(OtclConstants.OTCL_TMD_EXTN);

	/** The Constant msgPack. */
	private static final MessagePack msgPack = new MessagePack();
	
	/** The Constant objectMapper. */
	private static final ObjectMapper objectMapper;
	
	/** The Constant diagnostics. */
	private static final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

	static {
		objectMapper = new ObjectMapper();
		objectMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
	}

	/**
	 * Gets the single instance of OtclCompilerImpl.
	 *
	 * @return single instance of OtclCompilerImpl
	 */
	public static OtclCompilerImpl getInstance() {
		return otclCompilerImpl;
	}

	/**
	 * Compile otcl.
	 *
	 * @return the list
	 */
	@Override
	public List<CompilationReport> compileOtcl() {
		long startTime = System.nanoTime();
		LOGGER.info("Initiating OTCL compilations in " + otclSrcDir);
		File otclSourceDirectory = new File(otclSrcDir);
		List<CompilationReport> compilationReports = compileOtcl(otclSourceDirectory, null);
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
		LOGGER.info("Completed " + successful + "/" + total + " OTCL deployments, Failed : " + failed + "/" 
				+ total + ". in " + ((System.nanoTime() - startTime) / 1000000.0) + " millis.");
		if (successful == 0) {
			throw new OtclEngineException("", "Oops... Cannot continue due to 0 deployments!");
		}
		return compilationReports;
	}

	/**
	 * Compile otcl.
	 *
	 * @param directory the directory
	 * @param otclNamespace the otcl namespace
	 * @return the list
	 */
	private List<CompilationReport> compileOtcl(File directory, String otclNamespace) {
		List<CompilationReport> compilationReports = null;
		for (File file : directory.listFiles(otclFileFilter)) {
			if (file.isDirectory()) {
				String newOtclNamespacePackage = otclNamespace == null ? file.getName()
						: otclNamespace + "." + file.getName();
				if (compilationReports == null) {
					compilationReports = compileOtcl(file, newOtclNamespacePackage);
				} else {
					List<CompilationReport> childCompilationReports = compileOtcl(file, newOtclNamespacePackage);
					if (childCompilationReports != null) {
						compilationReports.addAll(childCompilationReports);
					}
				}
			} else {
				CompilationReport compilationReport = compileOtclFile(file, otclNamespace);
				if (compilationReports == null) {
					compilationReports = new ArrayList<>();
				}
				int idx = compilationReport.otclFileName.lastIndexOf(OtclConstants.OTCL_SCRIPT_EXTN);
				String depFileName = compilationReport.otclFileName.substring(0, idx) + OtclConstants.OTCL_TMD_EXTN;
				if (!CommonUtils.isEmpty(compilationReport.otclNamespace)) {
					depFileName = compilationReport.otclNamespace + "." + depFileName;
				}
				File binDir = new File(otclBinDir);
				if (!binDir.exists()) {
					binDir.mkdirs();
					binDir = null;
				}
				depFileName = otclBinDir + depFileName;
				FileOutputStream fos = null;
				DeploymentDto deploymentDto = createDeploymentDto(compilationReport);
				try {
					String str = objectMapper.writeValueAsString(deploymentDto);
					fos = new FileOutputStream(depFileName);
			        msgPack.write(fos, str.getBytes());
					fos.flush();
					compilationReports.add(compilationReport);
				} catch (IOException e) {
					throw new OtclCompilerException(e);
				} finally {
					if (fos != null) {
						try {
							fos.close();
						} catch (IOException e) {
							throw new OtclCompilerException(e);
						}
					}
				}
			}
		}
		return compilationReports;
	}

	/**
	 * Creates the deployment dto.
	 *
	 * @param compilationReport the compilation report
	 * @return the deployment dto
	 */
	private DeploymentDto createDeploymentDto(CompilationReport compilationReport) {
		DeploymentDto deploymentDto = new DeploymentDto();
		OtclDto otclDto = compilationReport.otclDto;
		deploymentDto.mainClass = otclDto.mainClassDto.fullyQualifiedClassName;
		deploymentDto.sourceClz = otclDto.sourceClz;
		deploymentDto.targetClz = otclDto.targetClz;
		deploymentDto.otclNamespace = otclDto.otclNamespace;
		String otclNamespace = otclDto.otclNamespace;
		String deploymentId = otclDto.otclFileName;
		deploymentId = deploymentId.substring(0, deploymentId.lastIndexOf(OtclConstants.OTCL_SCRIPT_EXTN));
		if (!CommonUtils.isEmpty(otclNamespace)) {
			deploymentId = otclNamespace + "." + deploymentId;
		}
		deploymentDto.deploymentId = deploymentId;
		List<ScriptDto> scriptDtos = otclDto.scriptDtos;
		for (ScriptDto scriptDto : scriptDtos) {
			if (deploymentDto.compiledInfos == null) {
				deploymentDto.compiledInfos = new LinkedHashMap<>();
			}
			String id = scriptDto.command.id;
			CompiledInfo compiledInfo = new CompiledInfo();
			deploymentDto.compiledInfos.put(id, compiledInfo);
			compiledInfo.factoryClassName = scriptDto.command.factoryClassName;
			if (otclDto.sourceOCDStems != null && scriptDto.sourceOtclChainDto != null) {
				compiledInfo.sourceOtclChainDto = scriptDto.sourceOtclChainDto;
				String[] otclTokens = scriptDto.sourceOtclChainDto.otclTokens;
				compiledInfo.sourceOCDStem = otclDto.sourceOCDStems.get(otclTokens[0]);
				nullifyFields(compiledInfo.sourceOCDStem);
				if (!deploymentDto.isProfilingRequried && (scriptDto.sourceOtclChainDto.collectionCount > 0 ||
						scriptDto.sourceOtclChainDto.dictionaryCount > 0)) {
					deploymentDto.isProfilingRequried = true;
				}
			}
			compiledInfo.targetOtclChainDto = scriptDto.targetOtclChainDto;
			String[] otclTokens = scriptDto.targetOtclChainDto.otclTokens;
			compiledInfo.targetOCDStem = otclDto.targetOCDStems.get(otclTokens[0]);
			nullifyFields(compiledInfo.targetOCDStem);
		}
		return deploymentDto;
	}
	
	/**
	 * Nullify fields.
	 *
	 * @param otclCommandDto the otcl command dto
	 */
	private void nullifyFields(OtclCommandDto otclCommandDto) {
		otclCommandDto.field = null;
		otclCommandDto.parent = null;
		if (otclCommandDto.children != null) {
			for (OtclCommandDto childOCD : otclCommandDto.children.values()) {
				nullifyFields(childOCD); 
			}
		}
	}
	
	/**
	 * Compile otcl file.
	 *
	 * @param file the file
	 * @param otclNamespace the otcl namespace
	 * @return the compilation report
	 */
	private CompilationReport compileOtclFile(File file, String otclNamespace) {
		OtclDto otclDto = null;
		String otclFileName = file.getName();
		otclNamespace = otclNamespace == null ? "" : otclNamespace;
		CompilationReport.Builder compilationReportBuilder = CompilationReport.newBuilder()
				.addOtclNamespace(otclNamespace)
				.addOtclFileName(otclFileName);
		String message = null;
		try {
			LOGGER.info("Compiling OTCL file : " + otclNamespace + "->" + otclFileName);
			otclDto = OtclLexicalizer.lexicalize(file, otclNamespace);
			if (otclDto.scriptDtos == null || otclDto.scriptDtos.size() == 0) {
				throw new CodeGeneratorException("", "No OTCL commmands to execute! "
						+ "OTCL-Scripts are missing or none are enabled.");
			}
			ClassDto mainClassDto = new ClassDto();
			otclDto.mainClassDto = mainClassDto;
			if (otclDto.otclFileDto != null && otclDto.otclFileDto.metadata != null && 
					otclDto.otclFileDto.metadata.entryClassName != null) {
				mainClassDto.className = otclDto.otclFileDto.metadata.entryClassName;
				if (!CommonUtils.isEmpty(otclDto.otclNamespace)) {
					mainClassDto.packageName = otclDto.otclNamespace;
					mainClassDto.fullyQualifiedClassName = mainClassDto.packageName + "." + mainClassDto.className;
				} else {
					mainClassDto.fullyQualifiedClassName = mainClassDto.className;
				}
			} else {
				String mainClassName = CompilerUtil.buildJavaClassName(otclNamespace, otclFileName);
				mainClassDto.fullyQualifiedClassName = mainClassName;
				if (!CommonUtils.isEmpty(otclDto.otclNamespace)) {
					mainClassDto.packageName = otclNamespace;
					mainClassDto.className = mainClassName.substring(mainClassName.lastIndexOf(".") + 1); 
				}
			}
			otclCodeGenerator.generateSourcecode(otclDto);
			message = "Successfully compiled OTCL file : " + otclNamespace + "->" + otclFileName;
			compilationReportBuilder.addDidSucceed(true)
				.addOtclDto(otclDto)
				.addMessage(message);
			LOGGER.info(message);
		} catch (Exception ex) {
			message = "Error while compiling OTCL file : " + otclNamespace + "->" + otclFileName;
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
		File binDir = new File(otclBinDir);
		List<DeploymentDto> deploymentDtos = null;
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
		List<JavaFileObject> javaFileObjects = createCompilationUnits(deploymentDtos, null);
		compileSourceCode(javaFileObjects);
		return;
	}
	
	/**
	 * Creates the compilation units.
	 *
	 * @param deploymentDtos the deployment dtos
	 * @param javaFileObjects the java file objects
	 * @return the list
	 */
	private List<JavaFileObject> createCompilationUnits(List<DeploymentDto> deploymentDtos, 
			List<JavaFileObject> javaFileObjects) {
		for (DeploymentDto deploymentDto : deploymentDtos) {
			String mainClz = deploymentDto.mainClass;
			String absoluteFileName = srcDir + File.separator + mainClz.replace(".", File.separator) +
					OtclConstants.OTCL_GENERATEDCODE_EXTN;
			File file = new File(absoluteFileName);
			if (!file.exists()) {
				throw new OtclCompilerException("", "Main-class " + mainClz + " is missing!.");
			}
			if (javaFileObjects == null) {
				javaFileObjects = new ArrayList<>();
			}
			javaFileObjects.add(new JavaCodeStringObject(file));
			for (CompiledInfo compiledInfo : deploymentDto.compiledInfos.values()) {
				String factoryClassName = compiledInfo.factoryClassName;
				String otclNamespace = deploymentDto.otclNamespace;
				if (!CommonUtils.isEmpty(otclNamespace) && !factoryClassName.startsWith(otclNamespace)) {
					factoryClassName = otclNamespace + "." + factoryClassName;
				}
				absoluteFileName = srcDir + File.separator + factoryClassName.replace(".", File.separator) +
						OtclConstants.OTCL_GENERATEDCODE_EXTN;
				file = new File(absoluteFileName);
				if (!file.exists()) {
					throw new OtclCompilerException("", "Factory-class " + factoryClassName + " is missing!.");
				}
				javaFileObjects.add(new JavaCodeStringObject(file));
			}
		}
		return javaFileObjects;
	}
	
	/**
	 * Compile source code.
	 *
	 * @param javaFileObjects the java file objects
	 */
	private void compileSourceCode(List<JavaFileObject> javaFileObjects) {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
		File fileClzPathRoot = new File(otclTargetDir);
		try {
			fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(fileClzPathRoot));
			File fileSrcPathRoot = new File(srcDir);
			fileManager.setLocation(StandardLocation.SOURCE_OUTPUT, Arrays.asList(fileSrcPathRoot));
		} catch (IOException e) {
			LOGGER.error("Could not set root locations!. ", e);
		}
		List<String> options = Arrays.asList("-classpath", otclTargetDir);
		JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, options, null,
				javaFileObjects);
		if (!task.call()) {
			diagnostics.getDiagnostics().forEach(System.out::println);
		}
		return;
	}
}
