/**
* Copyright (c) otclfoundation.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*
* This file is part of the OTCL framework.
* 
*  The OTCL framework is free software: you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  the Free Software Foundation, version 3 of the License.
*
*  The OTCL framework is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU General Public License for more details.
*
*  A copy of the GNU General Public License is made available as 'License.md' file, 
*  along with OTCL framework project.  If not, see <https://www.gnu.org/licenses/>.
*
*/
package org.otcl2.core.engine.compiler;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.otcl2.common.OtclConstants;
import org.otcl2.common.OtclConstants.TARGET_SOURCE;
import org.otcl2.common.dto.OtclChainDto;
import org.otcl2.common.dto.OtclCommandDto;
import org.otcl2.common.dto.OtclDto;
import org.otcl2.common.dto.ScriptDto;
import org.otcl2.common.dto.otcl.OtclFileDto;
import org.otcl2.common.dto.otcl.OtclFileDto.Execute;
import org.otcl2.common.engine.compiler.OtclCommandContext;
import org.otcl2.common.exception.OtclException;
import org.otcl2.common.factory.OtclCommandDtoFactory;
import org.otcl2.common.util.CommonUtils;
import org.otcl2.common.util.OtclUtils;
import org.otcl2.core.engine.compiler.exception.LexicalizerException;
import org.otcl2.core.engine.compiler.exception.OtclExtensionsException;
import org.otcl2.core.engine.utils.CompilerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

// TODO: Auto-generated Javadoc
/**
 * The Class OtclLexicalizer.
 */
final class OtclLexicalizer {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(OtclLexicalizer.class);

	/** The Constant FROM_OTCLCHAIN_PATTERN. */
	private static final Pattern FROM_OTCLCHAIN_PATTERN = Pattern.compile(OtclConstants.REGEX_CHECK_OTCLCHAIN);

	/** The Constant objectMapper. */
	private static final ObjectMapper objectMapper;

	static {
		YAMLFactory yamlFactory = new YAMLFactory();
		yamlFactory.enable(YAMLGenerator.Feature.MINIMIZE_QUOTES);
		objectMapper = new ObjectMapper(yamlFactory);
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		objectMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
	}

	/**
	 * Lexicalize.
	 *
	 * @param file the file
	 * @param otclNamespace the otcl namespace
	 * @return the otcl dto
	 */
	static OtclDto lexicalize(File file, String otclNamespace) {
		OtclFileDto otclFileDto = loadOtcl(file);
		if (otclFileDto.otclCommands == null) {
			throw new LexicalizerException("", "No OTCL commmands to execute! OTCL-Scripts are missing.");
		}
		String fileName = file.getName();
		int idx = fileName.indexOf("_");
		String sourceClzName = null;
		Class<?> sourceClz = null;
		String targetClzName = null;
		if (idx <= 0) {
//			source-class-name can be null if no scripts with from/source : otclChain: is present.
			String json;
			try {
				json = objectMapper.writeValueAsString(otclFileDto);
			} catch (JsonProcessingException e) {
				throw new LexicalizerException("", e);
			}
			Matcher matcher = FROM_OTCLCHAIN_PATTERN.matcher(json);
			if (matcher.find()) {
				throw new LexicalizerException("", "Incorrect file-name! File-name should contain source and target class "
						+ "names.");
			}
			targetClzName = fileName.substring(0, fileName.lastIndexOf(OtclConstants.OTCL_SCRIPT_EXTN));
		} else {
			sourceClzName = fileName.substring(0, idx);
			sourceClz = OtclUtils.loadClass(sourceClzName);
			targetClzName = fileName.substring(idx + 1, fileName.lastIndexOf(OtclConstants.OTCL_SCRIPT_EXTN));
		}
		Class<?> targetClz = OtclUtils.loadClass(targetClzName);
		if (otclFileDto.metadata != null && otclFileDto.metadata.objectTypes != null) {
			String metadataSourceClzName = otclFileDto.metadata.objectTypes.source;
			if (metadataSourceClzName != null) {
				if (sourceClzName == null) {
					throw new LexicalizerException("", "Source class-name in filename and in metadata is not matching!");
				} else if (!sourceClzName.equals(metadataSourceClzName)) {
					LOGGER.warn("", "Ignoring source definition in metadata! Source class-name in filename and in metadata "
							+ "are not matching!");
				}
			}
			String metadataTargetClzName = otclFileDto.metadata.objectTypes.target;
			metadataTargetClzName = metadataTargetClzName != null ? metadataTargetClzName.trim() : metadataTargetClzName;
			if (metadataTargetClzName != null && !targetClzName.equals(metadataTargetClzName)) {
				LOGGER.warn("", "Ignoring target definition in metadata! Target class-name in filename and in metadata "
						+ "are not matching!");
			}
		}
		otclNamespace = otclNamespace == null ? "" : otclNamespace;
		Map<String, OtclFileDto.CommandCommonParams> mapOtclCommands = new HashMap<>();
		OtclDto otclDto = tokenize(otclNamespace, fileName, otclFileDto, targetClz, sourceClz, mapOtclCommands);
		Class<?> factoryHelper = fetchFactoryHelper(otclFileDto);
		GetterSetterFinalizer.process(otclDto.sourceOCDStems, factoryHelper, TARGET_SOURCE.SOURCE);
		GetterSetterFinalizer.process(otclDto.targetOCDStems, factoryHelper, TARGET_SOURCE.TARGET);
		
		GetterSetterFinalizer.resetLeafHelperTypes(mapOtclCommands, otclDto.sourceOCDStems, otclDto.targetOCDStems, factoryHelper);
		otclDto.otclFileDto = otclFileDto;
		return otclDto;
	}

	/**
	 * Load otcl.
	 *
	 * @param file the file
	 * @return the otcl file dto
	 */
	static OtclFileDto loadOtcl(File file) {
		OtclFileDto otclFileDto = null;
		try {
			otclFileDto = objectMapper.readValue(file, OtclFileDto.class);
		} catch (IOException e) {
			throw new LexicalizerException("", e);
		}
		return otclFileDto;
	}

	/**
	 * Tokenize.
	 *
	 * @param otclNamespace the otcl namespace
	 * @param fileName the file name
	 * @param otclFileDto the otcl file dto
	 * @param targetClz the target clz
	 * @param sourceClz the source clz
	 * @return the otcl dto
	 */
	private static OtclDto tokenize(String otclNamespace, String fileName, OtclFileDto otclFileDto, 
			Class<?> targetClz, Class<?> sourceClz, Map<String, OtclFileDto.CommandCommonParams> mapOtclCommands) {
		Set<String> factorClzNames = new HashSet<>();
		if (otclFileDto != null && otclFileDto.metadata != null && otclFileDto.metadata.entryClassName != null) {
			String mainClassName = otclFileDto.metadata.entryClassName;
			if (mainClassName.contains(".")) {
				LOGGER.warn("Discarding package name! Package should not be specified in 'metadata.mainClassName'.");
				mainClassName = mainClassName.substring(mainClassName.lastIndexOf(".") + 1);
				otclFileDto.metadata.entryClassName = mainClassName;
				factorClzNames.add(mainClassName);
			}
		}
		OtclDto.Builder builderDeploymentDto = OtclDto.newBuilder().addOtclNamespace(otclNamespace)
				.addOtclFileName(fileName)
				.addSourceClz(sourceClz)
				.addTargetClz(targetClz);
		Map<String, OtclCommandDto> mapTargetOCDs = new LinkedHashMap<>();
		Map<String, OtclCommandDto> mapSourceOCDs = new LinkedHashMap<>();
		Set<String> scriptIds = new HashSet<>();
		for (OtclFileDto.OtclCommands otclCommand : otclFileDto.otclCommands) {
			if ((otclCommand.copy != null && otclCommand.copy.debug) || 
					(otclCommand.execute != null && otclCommand.execute.debug)) {
				@SuppressWarnings("unused")
				int debugDummy = 0;
			}
			String commandId = null;
			if (otclCommand.copy != null) {
				commandId = otclCommand.copy.id;
				mapOtclCommands.put(commandId, otclCommand.copy);
			} else {
				commandId = otclCommand.execute.id;
				mapOtclCommands.put(commandId, otclCommand.execute);
			}
			if ((otclCommand.copy != null && otclCommand.copy.disable) || 
					(otclCommand.execute != null && otclCommand.execute.disable)) {
				LOGGER.warn("Ignoring disabled OTCL-command : {}", commandId);
				continue;
			}
			LOGGER.debug("Compiling OTCL-command : {}", commandId);
			validateScriptIds(scriptIds, commandId);
			scriptIds.add(commandId);
			String targetOtclChain = null;
			String factoryClassName = null;
			String sourceOtclChain = null;
			boolean isValues = false;
			boolean isExtensions = false;
			if (otclCommand.copy != null) {
				validateCopyCommand(otclCommand.copy, commandId); 
				targetOtclChain = otclCommand.copy.to.otclChain;
				factoryClassName = otclCommand.copy.factoryClassName;
				if (otclCommand.copy.from.otclChain != null) {
					sourceOtclChain = otclCommand.copy.from.otclChain;
				} else {
					isValues = true;
				}
			} else if (otclCommand.execute != null) {
				validateExecuteCommand(otclCommand.execute, commandId);
				targetOtclChain = otclCommand.execute.target.otclChain;
				factoryClassName = otclCommand.execute.factoryClassName;
				sourceOtclChain = otclCommand.execute.source.otclChain;
				isExtensions = true;
			}
			santizeFactoryClassName(factoryClassName, otclCommand, commandId, factorClzNames); 
			if (targetOtclChain.endsWith(".")) {
				targetOtclChain = targetOtclChain.substring(0, targetOtclChain.length() - 1);
			}
			if (!CommonUtils.isEmpty(sourceOtclChain) && sourceOtclChain.endsWith(".")) {
				sourceOtclChain = sourceOtclChain.substring(0, sourceOtclChain.length() - 1);
			}
			OtclChainDto.Builder builderTargetOtclChainDto = OtclChainDto.newBuilder();
			OtclChainDto.Builder builderSourceOtclChainDto = OtclChainDto.newBuilder();
			ScriptDto scriptDto = new ScriptDto(otclCommand);
			Execute execute = scriptDto.command instanceof Execute ? (Execute) scriptDto.command : null;
			if (isValues || (execute != null && (execute.otclConverter != null || execute.otclModule != null))) {
				OtclExtensionsValidator.validateExtensions(scriptDto, targetClz, builderTargetOtclChainDto, sourceClz,
						builderSourceOtclChainDto);
				if (isValues) {
					scriptDto.hasSetValues = true;
				}
			}
			try {
				OtclCommandDto targetStemOCD = tokenizeTargetChain(builderTargetOtclChainDto, targetOtclChain, scriptDto, 
						targetClz, mapTargetOCDs, builderDeploymentDto);
				OtclChainDto targetOtclChainDto = scriptDto.targetOtclChainDto;
				int targetCollectionsCount = targetOtclChainDto.collectionCount + targetOtclChainDto.dictionaryCount;
				
				int sourceCollectionsCount = 0;
				OtclChainDto sourceOtclChainDto = null;
				if (!CommonUtils.isEmpty(sourceOtclChain)) {
					OtclCommandDto sourceStemOCD = tokenizeSourceChain(builderSourceOtclChainDto, sourceOtclChain, 
							scriptDto, sourceClz, mapSourceOCDs, builderDeploymentDto);
					sourceOtclChainDto = scriptDto.sourceOtclChainDto;
					OtclCommandContext targetOCC = new OtclCommandContext();
					targetOCC.otclCommandDto = targetStemOCD;
					targetOCC.otclTokens = builderTargetOtclChainDto.getOtclTokens();
					targetOCC.rawOtclTokens = builderTargetOtclChainDto.getRawOtclTokens();
					OtclCommandContext sourceOCC = new OtclCommandContext();
					sourceOCC.otclCommandDto = sourceStemOCD;
					sourceOCC.otclTokens = builderSourceOtclChainDto.getOtclTokens();
					sourceOCC.rawOtclTokens = builderSourceOtclChainDto.getRawOtclTokens();
					OtclLeavesSemanticsChecker.checkLeavesSemantics(targetOCC, sourceOCC);
					sourceCollectionsCount = sourceOtclChainDto.collectionCount + sourceOtclChainDto.dictionaryCount;
				}
				
				String chainPathToParentLeaf = targetOtclChain;
				if ((targetCollectionsCount > 0 && sourceCollectionsCount > 0) || isValues || isExtensions) {
					if (sourceOtclChain != null) {
						chainPathToParentLeaf += "=" + sourceOtclChain;
					}
					chainPathToParentLeaf = OtclUtils.sanitizeOtcl(chainPathToParentLeaf);
				}
				if (scriptDto.command.factoryClassName == null) {
					scriptDto.command.factoryClassName = CompilerUtil.buildJavaClassName(otclNamespace, fileName,
							chainPathToParentLeaf);
				}
				builderDeploymentDto.addScriptDto(scriptDto);
			} catch (Exception ex) {
				LOGGER.error("", ex);
				if (ex instanceof OtclException) {
					throw ex;
				}
				throw new LexicalizerException("", "Otcl Lexicalizer-phase failure compiling Command-Id : " + commandId, ex);
			}
		}
		return builderDeploymentDto.build();
	}
	
	private static void validateScriptIds(Set<String> scriptIds, String commandId) {
		if (commandId == null) {
			throw new OtclExtensionsException("", "Otcl Lexicalizer-phase failure in Command with Id : " + commandId + 
					". The 'id' property is mandatory in every commmand-block but one or more are missing.");
		}
		if (scriptIds.contains(commandId)) {
			throw new OtclExtensionsException("", 
					"Otcl Lexicalizer-phase failure in Command with Id : " + commandId + ". Duplicate Command-Id : " +
							commandId + " found.");
		}
	}

	private static void validateCopyCommand(OtclFileDto.Copy copy, String commandId) {
		if (copy.to == null || copy.to.otclChain == null) {
			throw new OtclExtensionsException("", "Otcl Lexicalizer-phase failure in Command with Id : " + commandId
					+ ". The 'to: otclChain:' property/value is missing.");
		}
		if (copy.from == null || (copy.from.otclChain == null && 
				copy.from.values == null)) {
			throw new OtclExtensionsException("", "Otcl Lexicalizer-phase failure in Command with Id : " + commandId
					+ ". Either one of 'from: otclChain/values:' is mandatory - but both are missing.");
		}
		if (copy.from.otclChain != null && copy.from.values != null) {
			throw new OtclExtensionsException("", "Otcl Lexicalizer-phase failure in Command with Id : " + commandId
					+ ". Either one of 'from: otclChain/values:' should only be defined.");
		}
		if (copy.from.values != null && copy.from.overrides != null) {
			throw new OtclExtensionsException("", "Otcl Lexicalizer-phase failure in Command with Id : " + commandId
					+ ". Property 'copy: from: overrides:' cannot be defined for 'from: values:' property.");
		}
		return;
	}
	
	private static void validateExecuteCommand(OtclFileDto.Execute execute, String scriptId) {
		if (execute.target == null || execute.target.otclChain == null) {
			throw new OtclExtensionsException("", "Otcl Lexicalizer-phase failure in Command with Id : " + scriptId
					+ ". The 'target: otclChain' is missing.");
		}
		if (execute.source == null || execute.source.otclChain == null) {
			throw new OtclExtensionsException("", "Otcl Lexicalizer-phase failure in Command with Id : " + scriptId
					+ ". The 'source: otclChain' property/value is missing.");
		}
		if (execute.executionOrder != null) {
			for (String execExt : execute.executionOrder) {
				if (OtclConstants.EXECUTE_OTCL_MODULE.equals(execExt)) {
					if (execute.otclModule == null) {
						throw new LexicalizerException("", "Otcl Lexicalizer-phase failure in Command with Id : " +
								execute.id + ". 'executeOtclModule' definition "
								+ "is missing - but specified in 'executionOrder'");
					}
					break;
				}
				if (OtclConstants.EXECUTE_OTCL_CONVERTER.equals(execExt)) {
					if (execute.otclConverter == null) {
						throw new LexicalizerException("", "Otcl Lexicalizer-phase failure in Command with Id : " +
								execute.id + ". 'executeOtclConverter' definition is missing - "
								+ "but referenced in 'executionOrder'");
					}
					break;
				}
			}
		}
	}
	
	private static OtclCommandDto tokenizeTargetChain(OtclChainDto.Builder builderTargetOtclChainDto, String targetOtclChain, ScriptDto scriptDto,
			Class<?> targetClz, Map<String, OtclCommandDto> mapTargetOCDs, OtclDto.Builder builderDeploymentDto) { 
		builderTargetOtclChainDto.addOtclChain(targetOtclChain);
		// --- tokenize targetOtclChain
		OtclCommandDto targetStemOCD = tokenize(scriptDto, targetClz, targetOtclChain,
				mapTargetOCDs, builderTargetOtclChainDto, TARGET_SOURCE.TARGET, null);
		mapTargetOCDs.put(targetStemOCD.otclToken, targetStemOCD);
		builderDeploymentDto.addTargetOtclCommandDtoStem(targetStemOCD);
		OtclChainDto targetOtclChainDto = builderTargetOtclChainDto.build();
		scriptDto.targetOtclChainDto = targetOtclChainDto;
		targetStemOCD.isRootNode = targetStemOCD.fieldName.equals(OtclConstants.ROOT); 
		return targetStemOCD;
	}
	
	private static OtclCommandDto tokenizeSourceChain(OtclChainDto.Builder builderSourceOtclChainDto, String sourceOtclChain, ScriptDto scriptDto,
			Class<?> sourceClz, Map<String, OtclCommandDto> mapSourceOCDs, OtclDto.Builder builderDeploymentDto) { 
		if (sourceClz == null) {
			throw new LexicalizerException("", "Otcl Lexicalizer-phase failure! in Command with Id : " + scriptDto.command.id + 
					". The 'from.otclChain / source.otclChain' property is defined - but the OTCL filename is inconsistent due to missing source-type.");
		}
		builderSourceOtclChainDto.addOtclChain(sourceOtclChain);
		// --- tokenize sourceOtclChain
		OtclCommandDto sourceStemOCD = tokenize(scriptDto, sourceClz, sourceOtclChain,
				mapSourceOCDs, builderSourceOtclChainDto, TARGET_SOURCE.SOURCE, null);
		mapSourceOCDs.put(sourceStemOCD.otclToken, sourceStemOCD);
		builderDeploymentDto.addSourceOtclCommandDtoStem(sourceStemOCD);
		OtclChainDto sourceOtclChainDto = builderSourceOtclChainDto.build();
		scriptDto.sourceOtclChainDto = sourceOtclChainDto;
		sourceStemOCD.isRootNode = sourceStemOCD.fieldName.equals(OtclConstants.ROOT); 
		return sourceStemOCD;
	}
	
	private static void santizeFactoryClassName(String factoryClassName, OtclFileDto.OtclCommands otclScript, String scriptId,
			Set<String> factorClzNames) {
		if (factoryClassName == null) {
			return;
		}
		if (factoryClassName.contains(".")) {
			LOGGER.warn("Ignoring package name in Command with Id : {}. " +
					"Package should not be specified for factoryClassName:' property.", scriptId);
			factoryClassName = factoryClassName.substring(factoryClassName.lastIndexOf(".") + 1);
			if (otclScript.execute != null) {
				otclScript.execute.factoryClassName = factoryClassName;
			} else {
				otclScript.copy.factoryClassName = factoryClassName;
			}
		}
		if (factorClzNames.contains(factoryClassName)) {
			throw new OtclExtensionsException("", 
					"Otcl Lexicalizer-phase failure in Command with Id : " + scriptId +
					". Duplicate 'target: factoryClassName'" + factoryClassName + " found.");
		}
		factorClzNames.add(factoryClassName);
	}
	
	/**
	 * Tokenize.
	 *
	 * @param script the script
	 * @param factoryHelper the factory helper
	 * @param clz the clz
	 * @param otclChain the otcl chain
	 * @param mapOCDs the map OC ds
	 * @param builderOtclChainDto the builder otcl chain dto
	 * @param enumTargetOrSource the enum target or source
	 * @param logs the logs
	 * @return the otcl command dto
	 */
	private static OtclCommandDto tokenize(ScriptDto script, Class<?> clz, String otclChain, 
			Map<String, OtclCommandDto> stemMapOCDs, OtclChainDto.Builder builderOtclChainDto, 
			TARGET_SOURCE enumTargetOrSource, List<String> logs) {

		String[] otclTokens = builderOtclChainDto.getOtclTokens();
		if (otclTokens == null) {
			otclTokens = otclChain.split(OtclConstants.REGEX_OTCL_ON_DOT);
			builderOtclChainDto.addOtclTokens(otclTokens);
		}
		OtclCommandDto stemOCD = null;
		OtclCommandDto parentOCD = null;
		int length = otclTokens.length;
		Class<?> parentClz = clz;
		OtclCommandDto otclCommandDto = null;
		StringBuilder tokenPathBuilder = null;
		OtclCommandContext otclCommandContext = new OtclCommandContext();
		String commandId = script.command.id;
		Map<String, OtclCommandDto> mapOCDs = stemMapOCDs;
		for (int idx = 0; idx < length; idx++) {
			String rawOtclToken = otclTokens[idx];
			String otclToken = OtclUtils.sanitizeOtcl(rawOtclToken);
			if (tokenPathBuilder == null) {
				tokenPathBuilder = new StringBuilder(otclToken);
			} else {
				tokenPathBuilder.append(".").append(otclToken);
			}
			if (otclToken.contains(OtclConstants.MAP_KEY_REF)) {
				otclToken = otclToken.replace(OtclConstants.MAP_KEY_REF, "");
			} else if (otclToken.contains(OtclConstants.MAP_VALUE_REF)) {
				otclToken = otclToken.replace(OtclConstants.MAP_VALUE_REF, "");
			}
			otclCommandContext.otclTokens = otclTokens;
			if (mapOCDs.containsKey(otclToken)) {
				otclCommandDto = mapOCDs.get(otclToken);
				otclCommandDto.addCommandId(commandId);
				if (otclCommandDto.isFirstNode) {
					stemOCD = otclCommandDto;
				}
				OtclSytaxChecker.checkSyntax(script, parentClz, otclCommandDto, otclChain, otclTokens, rawOtclToken);
				if (otclCommandDto.isCollection()) {
					builderOtclChainDto.incrementCollectionCount();
					otclCommandDto = otclCommandDto.children.get(otclCommandDto.fieldName);
					otclTokens[idx] = otclToken;
				} else if (otclCommandDto.isMap()) {
					builderOtclChainDto.incrementDictionaryCount();
					OtclCommandDto memberOCD = null;
					if (rawOtclToken.contains(OtclConstants.MAP_KEY_REF)) {
						memberOCD = otclCommandDto.children.get(OtclConstants.MAP_KEY_REF + otclCommandDto.fieldName);
					} else {
						memberOCD = otclCommandDto.children.get(OtclConstants.MAP_VALUE_REF + otclCommandDto.fieldName);
					}
					otclCommandContext.otclCommandDto = memberOCD;
					boolean isLeaf = otclCommandContext.isLeaf();
					if (!isLeaf && memberOCD.children == null) {
						memberOCD.children = new LinkedHashMap<>();
					}
					otclTokens[idx] = otclToken;
					otclCommandDto = memberOCD;
				}
				otclCommandContext.otclCommandDto = otclCommandDto;
				if (!otclCommandContext.isLeaf()) {
					mapOCDs = otclCommandDto.children;
					parentOCD = otclCommandDto;
				}
				parentClz = otclCommandDto.fieldType;
				continue;
			}
			boolean isLeaf = false;
			if (idx == otclTokens.length - 1) {
				if (!rawOtclToken.contains(OtclConstants.OPEN_BRACKET)) {
					isLeaf = true;
				}
			}
			boolean isFirstNode = idx == 0 ? true : false;
			otclCommandDto = OtclCommandDtoFactory.create(commandId, enumTargetOrSource, otclToken, tokenPathBuilder.toString(),
					idx, null, null, isFirstNode, null, null, null, isLeaf);
			otclCommandDto.parent = parentOCD;
			OtclSytaxChecker.checkSyntax(script, parentClz, otclCommandDto, otclChain, otclTokens, rawOtclToken);
			if (parentOCD != null) {
				parentOCD.addChild(otclCommandDto);
			}
			if (otclCommandDto.isFirstNode) {
				stemOCD = otclCommandDto;
				mapOCDs.put(otclCommandDto.otclToken, otclCommandDto);
			}
			if (otclCommandDto.isCollection()) {
				builderOtclChainDto.incrementCollectionCount();
				otclCommandDto = otclCommandDto.children.get(otclCommandDto.fieldName);
			} else if (otclCommandDto.isMap()) {
				builderOtclChainDto.incrementDictionaryCount();
				if (rawOtclToken.contains(OtclConstants.MAP_KEY_REF)) {
					otclCommandDto = otclCommandDto.children.get(OtclConstants.MAP_KEY_REF + otclCommandDto.fieldName);
				} else if (rawOtclToken.contains(OtclConstants.MAP_VALUE_REF)) {
					otclCommandDto = otclCommandDto.children.get(OtclConstants.MAP_VALUE_REF + otclCommandDto.fieldName);
				}
			}
			mapOCDs = otclCommandDto.children;
			parentOCD = otclCommandDto;
			parentClz = otclCommandDto.fieldType;
		}
		return stemOCD;
	}

	/**
	 * Fetch factory helper.
	 *
	 * @param otclFileDto the otcl file dto
	 * @return the class
	 */
	private static Class<?> fetchFactoryHelper(OtclFileDto otclFileDto) {
		if (otclFileDto.metadata == null || otclFileDto.metadata.helper == null) {
			return null;
		}
		String factoryHelper = otclFileDto.metadata.helper;
		Class<?> clzFactoryHelper = null;
		try {
			clzFactoryHelper =  OtclUtils.loadClass(factoryHelper);
		} catch (OtclException otclException) {
			String msg = "Discarding 'metadata: helper'! Could not load class : " + factoryHelper;
			throw new OtclException("", msg, otclException);
		}
		return clzFactoryHelper;
	}
}
