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
package org.otcframework.compiler.command;

import org.otcframework.common.OtcConstants;
import org.otcframework.common.OtcConstants.ALGORITHM_ID;
import org.otcframework.common.OtcConstants.LogLevel;
import org.otcframework.common.OtcConstants.TARGET_SOURCE;
import org.otcframework.common.compiler.OtcCommandContext;
import org.otcframework.common.config.OtcConfig;
import org.otcframework.common.dto.ClassDto;
import org.otcframework.common.dto.OtcCommandDto;
import org.otcframework.compiler.exception.CodeGeneratorException;
import org.otcframework.compiler.templates.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

/**
 * The Class OtcCommand.
 */
// TODO: Auto-generated Javadoc
public class OtcCommand {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(OtcCommand.class);

	/** The Constant CODE_TO_IMPORT. */
	public static final String CODE_TO_IMPORT = "CODE_TO_IMPORT";

	/** The Constant otcBinDir. */
	private static final String otcBinDir = OtcConfig.getCompiledCodeLocation();

	/** The Constant otcSourceDir. */
	private static final String otcSourceDir = OtcConfig.getOtcSourceLocation();

	/** The Constant sourceFileLocation. */
	private static final String sourceFileLocation = OtcConfig.getSourceCodeLocation();

	/** The var names set. */
	private Set<String> varNamesSet = new HashSet<>();

	/** The var names map. */
	private Map<String, String> varNamesMap = new HashMap<>();

	/**
	 * Clear cache.
	 */
	public void clearCache() {
		varNamesSet.clear();
		varNamesMap.clear();
	}

	/**
	 * Clear target cache.
	 */
	public void clearTargetCache() {
		Iterator<Entry<String, String>> iterator = varNamesMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, String> entry = iterator.next();
			if (entry.getKey().startsWith(TARGET_SOURCE.TARGET.name())) {
				iterator.remove();
			}
		}
	}

	/**
	 * Creates the java file.
	 *
	 * @param classDto the class dto
	 * @return the string
	 */
	public String createJavaFile(ClassDto classDto) {
		File file = new File(sourceFileLocation);
		if (!file.exists()) {
			file.mkdir();
		}
		String fileName = classDto.fullyQualifiedClassName.replace(".", File.separator) + ".java";
		String fileLocationAndName = sourceFileLocation + File.separator + fileName;
		file = new File(fileLocationAndName);
		FileOutputStream fileOutputStream = null;
		File dir = null;
		if (classDto.packageName == null) {
			dir = new File(sourceFileLocation);
		} else {
			dir = new File(sourceFileLocation + File.separator + classDto.packageName.replace(".", File.separator));
		}
		try {
			if (!dir.exists()) {
				dir.mkdirs();
			}
			if (file.createNewFile()) {
				fileOutputStream = new FileOutputStream(file);
				String javaCode = classDto.codeBuilder.toString();
				javaCode = JavaCodeFormatter.format(javaCode);
				fileOutputStream.write(javaCode.getBytes());
				fileOutputStream.flush();
			}
		} catch (IOException e) {
			LOGGER.warn("", e);
		} finally {
			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					LOGGER.warn("", e);
				}
			}
		}
		return fileName;
	}

	/**
	 * Creates the java file.
	 *
	 * @param targetOCC the target OCC
	 * @param targetClz the target clz
	 * @param sourceClz the source clz
	 * @return the java code string object
	 */
	public JavaCodeStringObject createJavaFile(TargetOtcCommandContext targetOCC, Class<?> targetClz,
			Class<?> sourceClz) {
		String methodEndCode = MethodEndTemplate.generateCode("");
		targetOCC.appendCode(methodEndCode);
		targetOCC.appendCode("\n}");
		StringBuilder importsBuilder = new StringBuilder();
//		for (String fqTypeName : targetOCC.factoryClassDto.retrieveImportFqNames()) {
//			importsBuilder.append("\nimport ").append(fqTypeName).append(";");
//		}
		targetOCC.factoryClassDto.retrieveImportFqNames().forEach(fqTypeName -> {
			importsBuilder.append("\nimport ").append(fqTypeName).append(";");
		});
		StringBuilder codeBuilder = targetOCC.factoryClassDto.codeBuilder;
		int idx = codeBuilder.indexOf(CODE_TO_IMPORT);
		codeBuilder.replace(idx, idx + CODE_TO_IMPORT.length(), importsBuilder.toString());
		createJavaFile(targetOCC.factoryClassDto);
		String fqClzName = targetOCC.factoryClassDto.className;
		String factoryClass = codeBuilder.toString();
		JavaCodeStringObject javaStringObject = new JavaCodeStringObject(fqClzName, factoryClass);
		return javaStringObject;
	}

	/**
	 * Retrieve member OCD.
	 *
	 * @param otcCommandContext the otc command context
	 * @return the otc command dto
	 */
	public static OtcCommandDto retrieveMemberOCD(OtcCommandContext otcCommandContext) {
		OtcCommandDto otcCommandDto = otcCommandContext.otcCommandDto;
		if (!otcCommandDto.isCollectionOrMap()) {
			throw new CodeGeneratorException("", "Invalid call to method! Token should be for a "
					+ "Map / collection / array in " + otcCommandDto.tokenPath);
		}
		String memberName = otcCommandDto.fieldName;
		if (otcCommandDto.isMap()) {
			String[] rawOtcTokens = otcCommandContext.rawOtcTokens;
			if (rawOtcTokens[otcCommandDto.otcTokenIndex].contains(OtcConstants.MAP_KEY_REF)) {
				memberName = OtcConstants.MAP_KEY_REF + memberName;
			} else {
				memberName = OtcConstants.MAP_VALUE_REF + memberName;
			}
		}
		otcCommandDto = otcCommandDto.children.get(memberName);
		return otcCommandDto;
	}

	/**
	 * Retrieve next OCD.
	 *
	 * @param otcCommandContext the otc command context
	 * @return the otc command dto
	 */
	public static OtcCommandDto retrieveNextOCD(OtcCommandContext otcCommandContext) {
		OtcCommandDto otcCommandDto = otcCommandContext.otcCommandDto;
		OtcCommandDto childOCD = otcCommandContext.otcCommandDto;
		if (childOCD.isCollectionOrMap()) {
			childOCD = retrieveMemberOCD(otcCommandContext);
			otcCommandContext.otcCommandDto = childOCD;
		}
		if (otcCommandContext.hasChildren()) {
			childOCD = childOCD.children.get(otcCommandContext.otcTokens[childOCD.otcTokenIndex + 1]);
			otcCommandContext.otcCommandDto = otcCommandDto;
		}
		return childOCD;
	}

	/**
	 * Retrieve next collection or map OCD.
	 *
	 * @param otcCommandContext the otc command context
	 * @return the otc command dto
	 */
	public static OtcCommandDto retrieveNextCollectionOrMapOCD(OtcCommandContext otcCommandContext) {
		if (!otcCommandContext.hasDescendantCollectionOrMap()) {
			throw new CodeGeneratorException("",
					"Invalid call to method! Token does not contain decendant Collection / Map.");
		}
		OtcCommandDto childOCD = otcCommandContext.otcCommandDto;
		while (true) {
			childOCD = retrieveNextOCD(otcCommandContext);
			otcCommandContext.otcCommandDto = childOCD;
			if (childOCD.isCollectionOrMap()) {
				break;
			}
		}
		return childOCD;
	}

	/**
	 * Retrieve leaf OCD.
	 *
	 * @param otcCommandContext the otc command context
	 * @return the otc command dto
	 */
	public static OtcCommandDto retrieveLeafOCD(OtcCommandContext otcCommandContext) {
		OtcCommandDto otcCommandDto = otcCommandContext.otcCommandDto;
		String[] otcTokens = otcCommandContext.otcTokens;
		String[] rawOtcTokens = otcCommandContext.rawOtcTokens;
		OtcCommandContext clonedOCC = otcCommandContext.clone();
		while (true) {
			if (otcCommandDto.isCollectionOrMap()) {
				String memberName = otcCommandDto.fieldName;
				if (otcCommandDto.isMap()) {
					if (rawOtcTokens[otcCommandDto.otcTokenIndex].contains(OtcConstants.MAP_KEY_REF)) {
						memberName = OtcConstants.MAP_KEY_REF + memberName;
					} else {
						memberName = OtcConstants.MAP_VALUE_REF + memberName;
					}
				}
				otcCommandDto = otcCommandDto.children.get(memberName);
				clonedOCC.otcCommandDto = otcCommandDto;
			}
			if (!clonedOCC.hasChildren() || clonedOCC.isLeaf()) {
				break;
			}
			otcCommandDto = otcCommandDto.children.get(otcTokens[otcCommandDto.otcTokenIndex + 1]);
			clonedOCC.otcCommandDto = otcCommandDto;
		}
		return otcCommandDto;
	}

	/**
	 * Append method call.
	 *
	 * @param targetOCC the target OCC
	 * @param targetClz the target clz
	 * @param sourceClz the source clz
	 */
	private void appendMethodCall(TargetOtcCommandContext targetOCC, Class<?> targetClz, Class<?> sourceClz) {
		StringBuilder executeMethodCallCodeBuilder = new StringBuilder("\n");
		String factoryMethodCallCode = null;
		String factoryClzName = targetOCC.factoryClassDto.fullyQualifiedClassName;
		factoryMethodCallCode = ExecuteFactoryMethodCallTemplate.generateCode(factoryClzName, targetClz, sourceClz);
		executeMethodCallCodeBuilder.append(factoryMethodCallCode);
		targetOCC.mainClassDto.codeBuilder.append(executeMethodCallCodeBuilder);
	}

	/**
	 * Append begin module class.
	 *
	 * @param targetOCC the target OCC
	 * @param sourceOCC the source OCC
	 * @param targetClz the target clz
	 * @param sourceClz the source clz
	 * @param addLogger the add logger
	 */
	public void appendBeginModuleClass(TargetOtcCommandContext targetOCC, SourceOtcCommandContext sourceOCC,
			Class<?> targetClz, Class<?> sourceClz, boolean addLogger) {
		appendBeginClass(targetOCC, sourceOCC, targetClz, sourceClz, addLogger, true);
	}

	/**
	 * Append begin class.
	 *
	 * @param targetOCC the target OCC
	 * @param sourceOCC the source OCC
	 * @param targetClz the target clz
	 * @param sourceClz the source clz
	 * @param addLogger the add logger
	 */
	public void appendBeginClass(TargetOtcCommandContext targetOCC, SourceOtcCommandContext sourceOCC,
			Class<?> targetClz, Class<?> sourceClz, boolean addLogger) {
		appendBeginClass(targetOCC, sourceOCC, targetClz, sourceClz, addLogger, false);
	}

	/**
	 * Append begin class.
	 *
	 * @param targetOCC the target OCC
	 * @param sourceOCC the source OCC
	 * @param targetClz the target clz
	 * @param sourceClz the source clz
	 * @param addLogger the add logger
	 * @param isModule  the is module
	 */
	private void appendBeginClass(TargetOtcCommandContext targetOCC, SourceOtcCommandContext sourceOCC,
			Class<?> targetClz, Class<?> sourceClz, boolean addLogger, boolean isModule) {
		String fileName = targetOCC.factoryClassDto.fullyQualifiedClassName.replace(".", File.separator);
		File file = new File(otcBinDir + fileName + ".class");
		if (file.exists()) {
			file.delete();
		}
		file = new File(otcSourceDir + fileName + ".java");
		if (file.exists()) {
			file.delete();
		}
		targetOCC.factoryClassDto.codeBuilder = new StringBuilder();
		targetOCC.factoryClassDto.clearImports();
		targetOCC.factoryClassDto.addImport(Map.class.getName());
		String factoryClassBegin = null;
		String targetType = targetOCC.factoryClassDto.addImport(targetClz.getName());
		String sourceType = null;
		if (sourceClz != null) {
			sourceType = targetOCC.factoryClassDto.addImport(sourceClz.getName());
		}
		if (isModule) {
			factoryClassBegin = ClassBeginTemplate.generateModuleClassCode(targetOCC.factoryClassDto, sourceType,
					targetType, addLogger, varNamesSet);
		} else {
			factoryClassBegin = ClassBeginTemplate.generateFactoryClassCode(targetOCC.factoryClassDto, sourceType,
					targetType, addLogger, varNamesSet);
		}
		factoryClassBegin += PcdInitTemplate.generateMemberPcdCode(targetOCC, sourceOCC, varNamesSet);
		appendMethodCall(targetOCC, targetClz, sourceClz);
		targetOCC.appendCode(factoryClassBegin);
		return;
	}

	/**
	 * Append preloop vars.
	 *
	 * @param targetOCC the target OCC
	 */
	public void appendPreloopVars(TargetOtcCommandContext targetOCC) {
		targetOCC.appendCode(PreloopVarsTemplate.generateCode());
		return;
	}

	/**
	 * Append assign anchored pcd to parent pcd.
	 *
	 * @param targetOCC the target OCC
	 */
	public void appendAssignAnchoredPcdToParentPcd(TargetOtcCommandContext targetOCC) {
		String assignAnchoredPcdToMemberPcdCode = PcdInitTemplate.generateAssignAnchoredPcdToParentPcdTemplateCode();
		targetOCC.appendCode(assignAnchoredPcdToMemberPcdCode);
		return;
	}

	/**
	 * Append assign parent pcd to anchored pcd.
	 *
	 * @param targetOCC the target OCC
	 */
	public void appendAssignParentPcdToAnchoredPcd(TargetOtcCommandContext targetOCC) {
		String assignAnchoredPcdToMemberPcdCode = PcdInitTemplate.generateAssignParentPcdToAnchoredPcdTemplateCode();
		targetOCC.appendCode(assignAnchoredPcdToMemberPcdCode);
		return;
	}

	/**
	 * Append retrieve next source collection or map parent.
	 *
	 * @param targetOCC        the target OCC
	 * @param sourceOCC        the source OCC
	 * @param idx              the idx
	 * @param createNewVarName the create new var name
	 * @param logLevel         the log level
	 */
	public void appendRetrieveNextSourceCollectionOrMapParent(TargetOtcCommandContext targetOCC,
			SourceOtcCommandContext sourceOCC, int idx, boolean createNewVarName, LogLevel logLevel) {
		OtcCommandDto sourceOCD = sourceOCC.otcCommandDto;
		if (!sourceOCD.isCollectionOrMap()) {
			if (!sourceOCC.hasDescendantCollectionOrMap()) {
				throw new CodeGeneratorException("", "Invalid call to method in OTC-Command : " + targetOCC.commandId
						+ "!. Token does not have descentant Collection/Map.");
			}
			sourceOCD = retrieveNextCollectionOrMapOCD(sourceOCC);
			sourceOCC.otcCommandDto = sourceOCD;
		}
		String icdCode = null;
		if (!sourceOCC.hasAncestralCollectionOrMap()) {
			icdCode = PcdInitTemplate.generateIfNullSourceRootPcdReturnCode(sourceOCC, logLevel);
		} else {
			icdCode = PcdInitTemplate.generateIfNullSourceParentPcdReturnCode(sourceOCC, logLevel);
		}
		StringBuilder icdCodeBuilder = new StringBuilder(icdCode);
		sourceOCD = retrieveMemberOCD(sourceOCC);
		sourceOCC.otcCommandDto = sourceOCD;
		icdCode = PcdInitTemplate.generateIfNullSourceMemberPcdReturnCode(sourceOCC, idx, logLevel);
		icdCodeBuilder.append(icdCode);
		if (!sourceOCC.hasDescendantCollectionOrMap()) {
			String retrieveMemberCode = RetrieveMemberFromPcdTemplate.generateCode(targetOCC, sourceOCC,
					createNewVarName, varNamesSet, varNamesMap);
			icdCodeBuilder.append(retrieveMemberCode);
		}
		targetOCC.appendCode(icdCodeBuilder);
		return;
	}

	/**
	 * Append if null source return.
	 *
	 * @param targetOCC the target OCC
	 * @param sourceOCC the source OCC
	 * @param idx       the idx
	 * @param logLevel  the log level
	 */
	public void appendIfNullSourceReturn(TargetOtcCommandContext targetOCC, SourceOtcCommandContext sourceOCC,
			Integer idx, LogLevel logLevel) {
		OtcCommandDto sourceOCD = sourceOCC.otcCommandDto;
		if (sourceOCD.isCollectionOrMapMember()) {
			throw new CodeGeneratorException("", "Invalid call to method in OTC-command : " + targetOCC.commandId
					+ "! Token should not be of a member for this operation.");
		}
		String ifNullReturnCode = GetterIfNullReturnTemplate.generateGetterIfNullReturnCode(targetOCC, sourceOCC, false,
				logLevel, varNamesSet, varNamesMap);
		targetOCC.appendCode(ifNullReturnCode);
		return;
	}

	/**
	 * Append if null source continue.
	 *
	 * @param targetOCC the target OCC
	 * @param sourceOCC the source OCC
	 * @param logLevel  the log level
	 */
	public void appendIfNullSourceContinue(TargetOtcCommandContext targetOCC, SourceOtcCommandContext sourceOCC,
			LogLevel logLevel) {
		OtcCommandDto sourceOCD = sourceOCC.otcCommandDto;
		if (sourceOCD.isCollectionOrMapMember()) {
			throw new CodeGeneratorException("", "Invalid call to method in OTC-command : " + targetOCC.commandId
					+ "! Token should not be of a Collection/Map member for this operation.");
		}
		StringBuilder ifNullReturnCodeBuilder = new StringBuilder();
		String ifNullContinueCode = IfNullContinueTemplate.generateCode(targetOCC, sourceOCC, false, logLevel,
				varNamesSet, varNamesMap);
		ifNullReturnCodeBuilder.append(ifNullContinueCode);
		if (sourceOCD.isCollectionOrMap()) {
			OtcCommandDto memberOCD = retrieveMemberOCD(sourceOCC);
			sourceOCC.otcCommandDto = memberOCD;
		}
		targetOCC.appendCode(ifNullReturnCodeBuilder);
		return;
	}

	/**
	 * Append for loop.
	 *
	 * @param targetOCC        the target OCC
	 * @param idxPrefix        the idx prefix
	 * @param createNewVarName the create new var name
	 * @param logLevel         the log level
	 */
	public void appendForLoop(TargetOtcCommandContext targetOCC, String idxPrefix, boolean createNewVarName,
			LogLevel logLevel) {
		appendForLoop(targetOCC, null, idxPrefix, createNewVarName, logLevel);
	}

	/**
	 * Append for loop.
	 *
	 * @param targetOCC        the target OCC
	 * @param sourceOCC        the source OCC
	 * @param idxPrefix        the idx prefix
	 * @param createNewVarName the create new var name
	 * @param logLevel         the log level
	 */
	public void appendForLoop(TargetOtcCommandContext targetOCC, SourceOtcCommandContext sourceOCC, String idxPrefix,
			boolean createNewVarName, LogLevel logLevel) {
		OtcCommandDto otcCommandDto = null;
		if (sourceOCC != null) {
			otcCommandDto = sourceOCC.otcCommandDto;
		} else {
			otcCommandDto = targetOCC.otcCommandDto;
		}
		if (!otcCommandDto.isCollectionOrMap()) {
			throw new CodeGeneratorException("",
					"Invalid call to method in OTC-command : " + targetOCC.commandId
							+ "! Token should be a collection/map for this operation for target:otcChain : "
							+ otcCommandDto.tokenPath);
		}
		String forLoopCode = null;
		if (TARGET_SOURCE.TARGET == otcCommandDto.enumTargetSource) {
			forLoopCode = ForLoopTemplate.generateTargetLoopCode(targetOCC, idxPrefix, createNewVarName, logLevel,
					varNamesSet, varNamesMap);
			targetOCC.currentCollectionTokenIndex++;
		} else {
			forLoopCode = ForLoopTemplate.generateSourceLoopCode(targetOCC, sourceOCC, idxPrefix, createNewVarName,
					logLevel, varNamesSet, varNamesMap);
			sourceOCC.currentCollectionTokenIndex++;
		}
		targetOCC.loopsCounter++;
		targetOCC.appendCode(forLoopCode);
		return;
	}

	/**
	 * Append init.
	 *
	 * @param targetOCC        the target OCC
	 * @param sourceOCC        the source OCC
	 * @param createNewVarName the create new var name
	 * @param logLevel         the log level
	 */
	public void appendInit(TargetOtcCommandContext targetOCC, SourceOtcCommandContext sourceOCC,
			boolean createNewVarName, LogLevel logLevel) {
		OtcCommandDto targetOCD = targetOCC.otcCommandDto;
		if (targetOCD.isCollectionOrMapMember() || (targetOCD.isEnum() && targetOCC.isLeaf())) {
			throw new CodeGeneratorException("", "Invalid call to method  in OTC-command : " + targetOCC.commandId
					+ "! Token should not be a Enum / Collection member / Map member for this operation.");
		}
		String ifNullCreateAndSetCode = null;
		StringBuilder ifNullReturnCodeBuilder = new StringBuilder();
		if (targetOCD.isArray()) {
			ifNullCreateAndSetCode = GetterIfNullCreateSetTemplate.generateCodeForArray(targetOCC, targetOCD, 1,
					createNewVarName, varNamesSet, varNamesMap);
		} else {
			if (targetOCD.isEnum() && ALGORITHM_ID.COPYVALUES != targetOCC.algorithmId) {
				ifNullCreateAndSetCode = GetSetTemplate.generateCode(targetOCC, sourceOCC, createNewVarName,
						varNamesSet, varNamesMap);
			} else {
				ifNullCreateAndSetCode = GetterIfNullCreateSetTemplate.generateCode(targetOCC, targetOCD,
						createNewVarName, varNamesSet, varNamesMap);
			}
		}
		ifNullReturnCodeBuilder.append(ifNullCreateAndSetCode);
		if (targetOCD.isCollectionOrMap()) {
			String icdCode = null;
			if (!targetOCC.hasAncestralCollectionOrMap()) {
				if (ALGORITHM_ID.MODULE != targetOCC.algorithmId
						&& ALGORITHM_ID.CONVERTER != targetOCC.algorithmId) {
					icdCode = PcdInitTemplate.generateIfNullTargetRootPcdCreateCode(targetOCC, varNamesSet,
							varNamesMap);
					ifNullReturnCodeBuilder.append(icdCode);
				}
			} else {
				icdCode = PcdInitTemplate.generateIfNullTargetParentPcdCreateCode(targetOCC, varNamesSet, varNamesMap);
				ifNullReturnCodeBuilder.append(icdCode);
			}
		}
		targetOCC.appendCode(ifNullReturnCodeBuilder);
		return;
	}

	/**
	 * Append init if null target return.
	 *
	 * @param targetOCC the target OCC
	 * @param logLevel  the log level
	 */
	public void appendInitIfNullTargetReturn(TargetOtcCommandContext targetOCC, LogLevel logLevel) {
		OtcCommandDto targetOCD = targetOCC.otcCommandDto;
		if (targetOCD.isCollectionOrMapMember()) {
			throw new CodeGeneratorException("", "Invalid call to method in OTC-command : " + targetOCC.commandId
					+ "! Token should not be of a member for this operation.");
		}
		StringBuilder ifNullReturnCodeBuilder = new StringBuilder();
		String ifNullReturnCode = GetterIfNullReturnTemplate.generateGetterIfNullReturnCode(targetOCC, false, logLevel,
				varNamesSet, varNamesMap);
		ifNullReturnCodeBuilder.append(ifNullReturnCode);
		if (targetOCD.isCollectionOrMap()) {
			String icdCode = null;
			if (!targetOCC.hasAncestralCollectionOrMap()) {
				if (ALGORITHM_ID.MODULE != targetOCC.algorithmId
						&& ALGORITHM_ID.CONVERTER != targetOCC.algorithmId) { // &&
					icdCode = PcdInitTemplate.generateIfNullTargetRootPcdReturnCode(targetOCC, logLevel);
					ifNullReturnCodeBuilder.append(icdCode);
				}
			} else {
				icdCode = PcdInitTemplate.generateIfNullTargetParentPcdReturnCode(targetOCC, logLevel);
				ifNullReturnCodeBuilder.append(icdCode);
			}
		}
		targetOCC.appendCode(ifNullReturnCodeBuilder);
		return;
	}

	/**
	 * Append init if null target continue.
	 *
	 * @param targetOCC the target OCC
	 * @param logLevel  the log level
	 */
	public void appendInitIfNullTargetContinue(TargetOtcCommandContext targetOCC, LogLevel logLevel) {
		OtcCommandDto targetOCD = targetOCC.otcCommandDto;
		if (targetOCD.isCollectionOrMapMember()) {
			throw new CodeGeneratorException("", "Invalid call to method in OTC-command : " + targetOCC.commandId
					+ "! Token should not be of a member for this operation.");
		}
		StringBuilder ifNullContinueCodeBuilder = new StringBuilder();
		String ifNullContinueCode = IfNullContinueTemplate.generateCode(targetOCC, null, false, logLevel, varNamesSet,
				varNamesMap);
		ifNullContinueCodeBuilder.append(ifNullContinueCode);
		targetOCC.appendCode(ifNullContinueCodeBuilder);
		return;
	}

	/**
	 * Append init member.
	 *
	 * @param targetOCC        the target OCC
	 * @param sourceOCC        the source OCC
	 * @param idx              the idx
	 * @param createNewVarName the create new var name
	 * @param logLevel         the log level
	 */
	public void appendInitMember(TargetOtcCommandContext targetOCC, OtcCommandContext sourceOCC, Integer idx,
			boolean createNewVarName, LogLevel logLevel) {
		OtcCommandDto memberOCD = targetOCC.otcCommandDto;
		if (!memberOCD.isCollectionOrMapMember() || (memberOCD.isEnum() && targetOCC.isLeaf())) {
			throw new CodeGeneratorException("", "Invalid call to method in OTC-command : " + targetOCC.commandId
					+ "! Type should be of a member of Collection/Map.");
		}
		OtcCommandDto sourceOCD = null;
		if (sourceOCC != null) {
			sourceOCD = sourceOCC.otcCommandDto;
		}
		String addToCollectionCode = null;
		if (memberOCD.isMapKey()) {
			addToCollectionCode = AddMapKeyTemplate.generateCode(targetOCC, sourceOCD, createNewVarName, null, idx,
					varNamesSet, varNamesMap);
		} else if (memberOCD.isMapValue()) {
			addToCollectionCode = AddMapValueTemplate.generateCode(targetOCC, sourceOCC, createNewVarName, null, idx,
					logLevel, varNamesSet, varNamesMap);
		} else if (memberOCD.isCollectionMember()) {
			addToCollectionCode = AddToCollectionTemplate.generateCode(targetOCC, null, sourceOCD, idx,
					createNewVarName, varNamesSet, varNamesMap);
		}
		targetOCC.appendCode(addToCollectionCode);
		return;
	}

	/**
	 * Append init member.
	 *
	 * @param targetOCC        the target OCC
	 * @param sourceOCC        the source OCC
	 * @param idxVar           the idx var
	 * @param createNewVarName the create new var name
	 * @param logLevel         the log level
	 */
	public void appendInitMember(TargetOtcCommandContext targetOCC, OtcCommandContext sourceOCC, String idxVar,
			boolean createNewVarName, LogLevel logLevel) {
		OtcCommandDto memberOCD = targetOCC.otcCommandDto;
		if (!memberOCD.isCollectionOrMapMember() || (memberOCD.isEnum() && targetOCC.isLeaf())) {
			throw new CodeGeneratorException("", "Invalid call to method in OTC-command : " + targetOCC.commandId
					+ "! Type should be of a member of Collection/Map.");
		}
		OtcCommandDto sourceOCD = sourceOCC.otcCommandDto;
		String addToCollectionCode = null;
		if (memberOCD.isMapKey()) {
			addToCollectionCode = AddMapKeyTemplate.generateCode(targetOCC, sourceOCD, createNewVarName, idxVar,
					varNamesSet, varNamesMap);
		} else if (memberOCD.isMapValue()) {
			addToCollectionCode = AddMapValueTemplate.generateCode(targetOCC, sourceOCC, createNewVarName, idxVar,
					logLevel, varNamesSet, varNamesMap);
		} else if (memberOCD.isCollectionMember()) {
			addToCollectionCode = AddToCollectionTemplate.generateCode(targetOCC, sourceOCD, idxVar, createNewVarName,
					varNamesSet, varNamesMap);
		}
		targetOCC.appendCode(addToCollectionCode);
		return;
	}

	/**
	 * Append get set.
	 *
	 * @param targetOCC        the target OCC
	 * @param sourceOCC        the source OCC
	 * @param createNewVarName the create new var name
	 */
	public void appendGetSet(TargetOtcCommandContext targetOCC, SourceOtcCommandContext sourceOCC,
			boolean createNewVarName) {
		String getSetCode = GetSetTemplate.generateCode(targetOCC, sourceOCC, createNewVarName, varNamesSet,
				varNamesMap);
		targetOCC.appendCode(getSetCode);
		return;
	}

	/**
	 * Append add map key.
	 *
	 * @param targetOCC the target OCC
	 * @param sourceOCD the source OCD
	 * @param value     the value
	 * @param idx       the idx
	 */
	public void appendAddMapKey(TargetOtcCommandContext targetOCC, OtcCommandDto sourceOCD, String value, Integer idx) {
		String addMapKeysCode = AddMapKeyTemplate.generateCode(targetOCC, sourceOCD, false, value, idx, varNamesSet,
				varNamesMap);
		targetOCC.appendCode(addMapKeysCode);
		return;
	}

	/**
	 * Append add map value.
	 *
	 * @param targetOCC the target OCC
	 * @param sourceOCC the source OCC
	 * @param value     the value
	 * @param idx       the idx
	 * @param logLevel  the log level
	 */
	public void appendAddMapValue(TargetOtcCommandContext targetOCC, SourceOtcCommandContext sourceOCC, String value,
			Integer idx, LogLevel logLevel) {
		String addMapValueCode = AddMapValueTemplate.generateCode(targetOCC, sourceOCC, false, value, idx, logLevel,
				varNamesSet, varNamesMap);
		targetOCC.appendCode(addMapValueCode);
		return;
	}

	/**
	 * Append if null target pcd return.
	 *
	 * @param targetOCC the target OCC
	 * @param logLevel  the log level
	 */
	public void appendIfNullTargetPcdReturn(TargetOtcCommandContext targetOCC, LogLevel logLevel) {
		String ifNullChildPcdReturnCode = PcdInitTemplate.generateIfNullTargetParentPcdReturnCode(targetOCC, logLevel);
		targetOCC.appendCode(ifNullChildPcdReturnCode);
	}

	/**
	 * Append if null target member pcd return.
	 *
	 * @param targetOCC the target OCC
	 * @param idx       the idx
	 * @param logLevel  the log level
	 */
	public void appendIfNullTargetMemberPcdReturn(TargetOtcCommandContext targetOCC, Integer idx, LogLevel logLevel) {
		String ifNullChildPcdReturnCode = PcdInitTemplate.generateIfNullTargetMemberPcdReturnCode(targetOCC, idx,
				logLevel);
		targetOCC.appendCode(ifNullChildPcdReturnCode);
	}

	/**
	 * Append add to collection.
	 *
	 * @param targetOCC the target OCC
	 * @param sourceOCD the source OCD
	 * @param idx       the idx
	 * @param value     the value
	 */
	public void appendAddToCollection(TargetOtcCommandContext targetOCC, OtcCommandDto sourceOCD, Integer idx,
			String value) {
		String addToCollectionCode = AddToCollectionTemplate.generateCode(targetOCC, value, sourceOCD, idx, false,
				varNamesSet, varNamesMap);
		targetOCC.appendCode(addToCollectionCode);
	}

	/**
	 * Append setter.
	 *
	 * @param targetOCC the target OCC
	 * @param value     the value
	 */
	public void appendSetter(TargetOtcCommandContext targetOCC, String value) {
		String setterCode = SetterTemplate.generateCode(targetOCC, false, value, varNamesSet, varNamesMap);
		targetOCC.appendCode(setterCode);
	}

	/**
	 * Append getter.
	 *
	 * @param targetOCC        the target OCC
	 * @param otcCommandDto    the otc command dto
	 * @param createNewVarName the create new var name
	 */
	public void appendGetter(TargetOtcCommandContext targetOCC, OtcCommandDto otcCommandDto, boolean createNewVarName) {
		String getter = GetterIfNullReturnTemplate.generateCode(targetOCC, otcCommandDto, createNewVarName, varNamesSet,
				varNamesMap);
		targetOCC.appendCode(getter);
	}

	/**
	 * Append execute module.
	 *
	 * @param targetOCC        the target OCC
	 * @param sourceOCC        the source OCC
	 * @param createNewVarName the create new var name
	 */
	public void appendExecuteModule(TargetOtcCommandContext targetOCC, SourceOtcCommandContext sourceOCC,
			boolean createNewVarName) {
		String executeModuleCode = ExecuteModuleTemplate.generateCode(targetOCC, sourceOCC, createNewVarName,
				varNamesSet, varNamesMap);
		targetOCC.appendCode(executeModuleCode);
	}

	/**
	 * Append execute converter.
	 *
	 * @param targetOCC        the target OCC
	 * @param sourceOCC        the source OCC
	 * @param createNewVarName the create new var name
	 */
	public void appendExecuteConverter(TargetOtcCommandContext targetOCC, SourceOtcCommandContext sourceOCC,
			boolean createNewVarName) {
		String executeModuleCode = ExecuteConverterTemplate.generateCode(targetOCC, sourceOCC, createNewVarName,
				varNamesSet, varNamesMap);
		targetOCC.appendCode(executeModuleCode);
	}

	/**
	 * Append init upto anchored or last collection or leaf.
	 *
	 * @param targetOCC      the target OCC
	 * @param idx            the idx
	 * @param uptoLeafParent the upto leaf parent
	 * @param logLevel       the log level
	 */
	public void appendInitUptoAnchoredOrLastCollectionOrLeaf(TargetOtcCommandContext targetOCC, Integer idx,
			boolean uptoLeafParent, LogLevel logLevel) {
		OtcCommandDto targetOCD = targetOCC.otcCommandDto;
		while (targetOCC.hasChildren()) {
			boolean hasMapValueInPath = targetOCC.hasMapValueMember() || targetOCC.hasMapValueDescendant();
			if (hasMapValueInPath) {
				appendInitIfNullTargetReturn(targetOCC, logLevel);
			} else {
				appendInit(targetOCC, null, false, LogLevel.WARN);
			}
			if (targetOCD.isCollectionOrMap()) {
				boolean isAnchoredOrHavingCollections = targetOCC.isCurrentTokenAnchored()
						|| !targetOCC.hasDescendantCollectionOrMap();
				if (isAnchoredOrHavingCollections && !uptoLeafParent) {
					break;
				}
				targetOCD = OtcCommand.retrieveMemberOCD(targetOCC);
				targetOCC.otcCommandDto = targetOCD;
				if (hasMapValueInPath) {
					appendIfNullTargetMemberPcdReturn(targetOCC, idx, logLevel);
					String retrieveTargetObjectFromPcdCode = RetrieveMemberFromPcdTemplate.generateCode(targetOCC,
							false, AbstractTemplate.MEMBER_TARGET_ICD, varNamesSet, varNamesMap);
					targetOCC.appendCode(retrieveTargetObjectFromPcdCode);
				} else {
					if (targetOCC.hasDescendantCollectionOrMap()
							|| (targetOCD.isCollectionOrMapMember() && targetOCC.hasChildren())) {
						appendInitMember(targetOCC, null, 0, false, logLevel);
					}
				}
			}
			if ((targetOCC.isLeafParent() && !targetOCD.isCollectionOrMap()) || !targetOCC.isLeaf()) {
				targetOCD = retrieveNextOCD(targetOCC);
				targetOCC.otcCommandDto = targetOCD;
			}
		}
	}

	/**
	 * Append init upto next collection with return or continue.
	 *
	 * @param targetOCC the target OCC
	 * @param logLevel  the log level
	 */
	public void appendInitUptoNextCollectionWithReturnOrContinue(TargetOtcCommandContext targetOCC, LogLevel logLevel) {
		OtcCommandDto targetOCD = targetOCC.otcCommandDto;
		while (targetOCC.hasChildren()) {
			boolean hasMapValueInPath = targetOCC.hasMapValueMember() || targetOCC.hasMapValueDescendant();
			if (hasMapValueInPath) {
				if (targetOCC.hasAncestralCollectionOrMap()) {
					appendInitIfNullTargetContinue(targetOCC, logLevel);
				} else {
					appendInitIfNullTargetReturn(targetOCC, logLevel);
				}
			} else {
				if (targetOCC.hasAncestralCollectionOrMap()) {
					appendGetterIfNullCreateSet(targetOCC, null);
				} else {
					appendInit(targetOCC, null, false, LogLevel.WARN);
				}
			}
			if (targetOCD.isCollectionOrMap()) {
				break;
			}
			if ((targetOCC.isLeafParent() && !targetOCD.isCollectionOrMap()) || !targetOCC.isLeaf()) {
				targetOCD = retrieveNextOCD(targetOCC);
				targetOCC.otcCommandDto = targetOCD;
			}
		}
	}

	/**
	 * Append getter if null create set.
	 *
	 * @param targetOCC the target OCC
	 * @param value     the value
	 */
	public void appendGetterIfNullCreateSet(TargetOtcCommandContext targetOCC, String value) {
		OtcCommandDto targetOCD = targetOCC.otcCommandDto;
		String ifNullCreateAndSetCode = null;
		if (targetOCD.isArray()) {
			ifNullCreateAndSetCode = GetterIfNullCreateSetTemplate.generateCodeForArray(targetOCC, targetOCD, 1, false,
					varNamesSet, varNamesMap);
		} else if (targetOCD.isEnum()) {
			ifNullCreateAndSetCode = GetterIfNullCreateSetTemplate.generateCodeForEnum(targetOCC, targetOCD, value,
					null, false, varNamesSet, varNamesMap);
		} else {
			ifNullCreateAndSetCode = GetterIfNullCreateSetTemplate.generateCode(targetOCC, targetOCD, false,
					varNamesSet, varNamesMap);
		}
		targetOCC.appendCode(ifNullCreateAndSetCode);
	}

	/**
	 * Append init upto next collection with continue.
	 *
	 * @param targetOCC the target OCC
	 * @param logLevel  the log level
	 */
	public void appendInitUptoNextCollectionWithContinue(TargetOtcCommandContext targetOCC, LogLevel logLevel) {
		OtcCommandDto targetOCD = targetOCC.otcCommandDto;
		if (targetOCC.collectionsCount - targetOCC.currentCollectionTokenIndex > 0) {
			targetOCC.currentCollectionTokenIndex++;
		}
		while (targetOCC.hasChildren()) {
			boolean hasMapValueInPath = targetOCC.hasMapValueMember() || targetOCC.hasMapValueDescendant();
			if (hasMapValueInPath) {
				appendInitIfNullTargetContinue(targetOCC, logLevel);
				if (targetOCD.isCollectionOrMap()) {
					String icdCode = null;
					if (targetOCC.hasAncestralCollectionOrMap()) {
						icdCode = PcdInitTemplate.generateIfNullTargetParentPcdCreateCode(targetOCC, varNamesSet,
								varNamesMap);
					} else if (ALGORITHM_ID.MODULE != targetOCC.algorithmId
							&& ALGORITHM_ID.CONVERTER != targetOCC.algorithmId) {
						icdCode = PcdInitTemplate.generateIfNullTargetRootPcdCreateCode(targetOCC, varNamesSet,
								varNamesMap);
					}
					targetOCC.appendCode(icdCode);
				}
			} else {
				appendInit(targetOCC, null, false, LogLevel.WARN);
			}
			if (targetOCD.isCollectionOrMap()) {
				break;
			}
			if ((targetOCC.isLeafParent() && !targetOCD.isCollectionOrMap()) || !targetOCC.isLeaf()) {
				targetOCD = retrieveNextOCD(targetOCC);
				targetOCC.otcCommandDto = targetOCD;
			}
		}
	}

	/**
	 * Append increment offset idx.
	 *
	 * @param targetOCC the target OCC
	 */
	public void appendIncrementOffsetIdx(TargetOtcCommandContext targetOCC) {
		targetOCC.appendCode(AbstractTemplate.incrementOffsetIdx);
	}

	/**
	 * Append init offset idx.
	 *
	 * @param targetOCC the target OCC
	 */
	public void appendInitOffsetIdx(TargetOtcCommandContext targetOCC) {
		targetOCC.appendCode(AbstractTemplate.initOffsetIdx);
	}
}
