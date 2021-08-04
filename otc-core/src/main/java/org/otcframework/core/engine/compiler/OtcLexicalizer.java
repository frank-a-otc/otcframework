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
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.otcframework.common.OtcConstants;
import org.otcframework.common.OtcConstants.TARGET_SOURCE;
import org.otcframework.common.dto.OtcChainDto;
import org.otcframework.common.dto.OtcCommandDto;
import org.otcframework.common.dto.OtcDto;
import org.otcframework.common.dto.ScriptDto;
import org.otcframework.common.dto.otc.OtcFileDto;
import org.otcframework.common.dto.otc.OtcFileDto.Execute;
import org.otcframework.common.engine.compiler.OtcCommandContext;
import org.otcframework.common.exception.OtcException;
import org.otcframework.common.factory.OtcCommandDtoFactory;
import org.otcframework.common.util.CommonUtils;
import org.otcframework.common.util.OtcUtils;
import org.otcframework.core.engine.compiler.exception.LexicalizerException;
import org.otcframework.core.engine.compiler.exception.OtcExtensionsException;
import org.otcframework.core.engine.utils.CompilerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

/**
 * The Class OtcLexicalizer.
 */
// TODO: Auto-generated Javadoc
final class OtcLexicalizer {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(OtcLexicalizer.class);

	/** The Constant FROM_OTCCHAIN_PATTERN. */
	private static final Pattern FROM_OTCCHAIN_PATTERN = Pattern.compile(OtcConstants.REGEX_CHECK_OTCCHAIN);

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
	 * @param file         the file
	 * @param otcNamespace the otc namespace
	 * @return the otc dto
	 */
	static OtcDto lexicalize(File file, String otcNamespace) {
		OtcFileDto otcFileDto = loadOtc(file);
		if (otcFileDto.otcCommands == null) {
			throw new LexicalizerException("", "No OTC commmands to execute! OTC-Scripts are missing.");
		}
		String fileName = file.getName();
		int idx = fileName.indexOf("_");
		String sourceClzName = null;
		Class<?> sourceClz = null;
		String targetClzName = null;
		if (idx <= 0) {
//			source-class-name can be null if no scripts with from/source : otcChain: is present.
			String json;
			try {
				json = objectMapper.writeValueAsString(otcFileDto);
			} catch (JsonProcessingException e) {
				throw new LexicalizerException("", e);
			}
			Matcher matcher = FROM_OTCCHAIN_PATTERN.matcher(json);
			if (matcher.find()) {
				throw new LexicalizerException("",
						"Incorrect file-name! File-name should contain source and target class " + "names.");
			}
			targetClzName = fileName.substring(0, fileName.lastIndexOf(OtcConstants.OTC_SCRIPT_EXTN));
		} else {
			sourceClzName = fileName.substring(0, idx);
			sourceClz = OtcUtils.loadClass(sourceClzName);
			targetClzName = fileName.substring(idx + 1, fileName.lastIndexOf(OtcConstants.OTC_SCRIPT_EXTN));
		}
		Class<?> targetClz = OtcUtils.loadClass(targetClzName);
		if (otcFileDto.metadata != null && otcFileDto.metadata.objectTypes != null) {
			String metadataSourceClzName = otcFileDto.metadata.objectTypes.source;
			if (metadataSourceClzName != null) {
				if (sourceClzName == null) {
					throw new LexicalizerException("",
							"Source class-name in filename and in metadata is not matching!");
				} else if (!sourceClzName.equals(metadataSourceClzName)) {
					LOGGER.warn("",
							"Ignoring source definition in metadata! Source class-name in filename and in metadata "
									+ "are not matching!");
				}
			}
			String metadataTargetClzName = otcFileDto.metadata.objectTypes.target;
			metadataTargetClzName = metadataTargetClzName != null ? metadataTargetClzName.trim()
					: metadataTargetClzName;
			if (metadataTargetClzName != null && !targetClzName.equals(metadataTargetClzName)) {
				LOGGER.warn("", "Ignoring target definition in metadata! Target class-name in filename and in metadata "
						+ "are not matching!");
			}
		}
		otcNamespace = otcNamespace == null ? "" : otcNamespace;
		Map<String, OtcFileDto.CommonCommandParams> mapOtcCommands = new HashMap<>();
		OtcDto otcDto = tokenize(otcNamespace, fileName, otcFileDto, targetClz, sourceClz, mapOtcCommands);
		Class<?> factoryHelper = fetchFactoryHelper(otcFileDto);
		GetterSetterFinalizer.process(otcDto.sourceOCDStems, factoryHelper, TARGET_SOURCE.SOURCE);
		GetterSetterFinalizer.process(otcDto.targetOCDStems, factoryHelper, TARGET_SOURCE.TARGET);
		GetterSetterFinalizer.resetLeafHelperTypes(mapOtcCommands, otcDto.sourceOCDStems, otcDto.targetOCDStems,
				factoryHelper);
		otcDto.otcFileDto = otcFileDto;
		return otcDto;
	}

	/**
	 * Load otc.
	 *
	 * @param file the file
	 * @return the otc file dto
	 */
	static OtcFileDto loadOtc(File file) {
		OtcFileDto otcFileDto = null;
		try {
			otcFileDto = objectMapper.readValue(file, OtcFileDto.class);
		} catch (IOException e) {
			throw new LexicalizerException("", e);
		}
		return otcFileDto;
	}

	/**
	 * Tokenize.
	 *
	 * @param otcNamespace   the otc namespace
	 * @param fileName       the file name
	 * @param otcFileDto     the otc file dto
	 * @param targetClz      the target clz
	 * @param sourceClz      the source clz
	 * @param mapOtcCommands the map otc commands
	 * @return the otc dto
	 */
	private static OtcDto tokenize(String otcNamespace, String fileName, OtcFileDto otcFileDto, Class<?> targetClz,
			Class<?> sourceClz, Map<String, OtcFileDto.CommonCommandParams> mapOtcCommands) {
		Set<String> factorClzNames = new HashSet<>();
		if (otcFileDto != null && otcFileDto.metadata != null && otcFileDto.metadata.entryClassName != null) {
			String mainClassName = otcFileDto.metadata.entryClassName;
			if (mainClassName.contains(".")) {
				LOGGER.warn("Discarding package name! Package should not be specified in 'metadata.mainClassName'.");
				mainClassName = mainClassName.substring(mainClassName.lastIndexOf(".") + 1);
				otcFileDto.metadata.entryClassName = mainClassName;
				factorClzNames.add(mainClassName);
			}
		}
		OtcDto.Builder builderDeploymentDto = OtcDto.newBuilder().addOtcNamespace(otcNamespace).addOtcFileName(fileName)
				.addSourceClz(sourceClz).addTargetClz(targetClz);
		Map<String, OtcCommandDto> mapTargetOCDs = new LinkedHashMap<>();
		Map<String, OtcCommandDto> mapSourceOCDs = new LinkedHashMap<>();
		Set<String> scriptIds = new HashSet<>();
		for (OtcFileDto.OtcCommands otcCommand : otcFileDto.otcCommands) {
			if ((otcCommand.copy != null && otcCommand.copy.debug)
					|| (otcCommand.execute != null && otcCommand.execute.debug)) {
				@SuppressWarnings("unused")
				int debugDummy = 0;
			}
			String commandId = null;
			if (otcCommand.copy != null) {
				commandId = otcCommand.copy.id;
				mapOtcCommands.put(commandId, otcCommand.copy);
			} else {
				commandId = otcCommand.execute.id;
				mapOtcCommands.put(commandId, otcCommand.execute);
			}
			if ((otcCommand.copy != null && otcCommand.copy.disable)
					|| (otcCommand.execute != null && otcCommand.execute.disable)) {
				LOGGER.warn("Ignoring disabled OTC-command : {}", commandId);
				continue;
			}
			LOGGER.debug("Compiling OTC-command : {}", commandId);
			validateScriptIds(scriptIds, commandId);
			scriptIds.add(commandId);
			String targetOtcChain = null;
			String factoryClassName = null;
			String sourceOtcChain = null;
			boolean isValues = false;
			boolean isExtensions = false;
			if (otcCommand.copy != null) {
				validateCopyCommand(otcCommand.copy, commandId);
				targetOtcChain = otcCommand.copy.to.objectPath;
				factoryClassName = otcCommand.copy.factoryClassName;
				if (otcCommand.copy.from.objectPath != null) {
					sourceOtcChain = otcCommand.copy.from.objectPath;
				} else {
					isValues = true;
				}
			} else if (otcCommand.execute != null) {
				validateExecuteCommand(otcCommand.execute, commandId);
				targetOtcChain = otcCommand.execute.target.objectPath;
				factoryClassName = otcCommand.execute.factoryClassName;
				sourceOtcChain = otcCommand.execute.source.objectPath;
				isExtensions = true;
			}
			santizeFactoryClassName(factoryClassName, otcCommand, commandId, factorClzNames);
			if (targetOtcChain.endsWith(".")) {
				targetOtcChain = targetOtcChain.substring(0, targetOtcChain.length() - 1);
			}
			if (!CommonUtils.isEmpty(sourceOtcChain) && sourceOtcChain.endsWith(".")) {
				sourceOtcChain = sourceOtcChain.substring(0, sourceOtcChain.length() - 1);
			}
			OtcChainDto.Builder builderTargetOtcChainDto = OtcChainDto.newBuilder();
			OtcChainDto.Builder builderSourceOtcChainDto = OtcChainDto.newBuilder();
			ScriptDto scriptDto = new ScriptDto(otcCommand);
			Execute execute = scriptDto.command instanceof Execute ? (Execute) scriptDto.command : null;
			if (isValues || (execute != null && (execute.otcConverter != null || execute.otcModule != null))) {
				OtcExtensionsValidator.validateExtensions(scriptDto, targetClz, builderTargetOtcChainDto, sourceClz,
						builderSourceOtcChainDto);
				if (isValues) {
					scriptDto.hasSetValues = true;
				}
			}
			try {
				OtcCommandDto targetStemOCD = tokenizeTargetChain(builderTargetOtcChainDto, targetOtcChain, scriptDto,
						targetClz, mapTargetOCDs, builderDeploymentDto);
				OtcChainDto targetOtcChainDto = scriptDto.targetOtcChainDto;
				int targetCollectionsCount = targetOtcChainDto.collectionCount + targetOtcChainDto.dictionaryCount;
				int sourceCollectionsCount = 0;
				OtcChainDto sourceOtcChainDto = null;
				if (!CommonUtils.isEmpty(sourceOtcChain)) {
					OtcCommandDto sourceStemOCD = tokenizeSourceChain(builderSourceOtcChainDto, sourceOtcChain,
							scriptDto, sourceClz, mapSourceOCDs, builderDeploymentDto);
					sourceOtcChainDto = scriptDto.sourceOtcChainDto;
					OtcCommandContext targetOCC = new OtcCommandContext();
					targetOCC.otcCommandDto = targetStemOCD;
					targetOCC.otcTokens = builderTargetOtcChainDto.getOtcTokens();
					targetOCC.rawOtcTokens = builderTargetOtcChainDto.getRawOtcTokens();
					OtcCommandContext sourceOCC = new OtcCommandContext();
					sourceOCC.otcCommandDto = sourceStemOCD;
					sourceOCC.otcTokens = builderSourceOtcChainDto.getOtcTokens();
					sourceOCC.rawOtcTokens = builderSourceOtcChainDto.getRawOtcTokens();
					OtcLeavesSemanticsChecker.checkLeavesSemantics(targetOCC, sourceOCC);
					sourceCollectionsCount = sourceOtcChainDto.collectionCount + sourceOtcChainDto.dictionaryCount;
				}
				String chainPathToParentLeaf = targetOtcChain;
				if ((targetCollectionsCount > 0 && sourceCollectionsCount > 0) || isValues || isExtensions) {
					if (sourceOtcChain != null) {
						chainPathToParentLeaf += "=" + sourceOtcChain;
					}
					chainPathToParentLeaf = OtcUtils.sanitizeOtc(chainPathToParentLeaf);
				}
				if (scriptDto.command.factoryClassName == null) {
					scriptDto.command.factoryClassName = CompilerUtil.buildJavaClassName(otcNamespace, fileName,
							chainPathToParentLeaf);
				}
				builderDeploymentDto.addScriptDto(scriptDto);
			} catch (Exception ex) {
				LOGGER.error("", ex);
				if (ex instanceof OtcException) {
					throw ex;
				}
				throw new LexicalizerException("", "Otc Lexicalizer-phase failure compiling Command-Id : " + commandId,
						ex);
			}
		}
		return builderDeploymentDto.build();
	}

	/**
	 * Validate script ids.
	 *
	 * @param scriptIds the script ids
	 * @param commandId the command id
	 */
	private static void validateScriptIds(Set<String> scriptIds, String commandId) {
		if (commandId == null) {
			throw new OtcExtensionsException("", "Otc Lexicalizer-phase failure in Command with Id : " + commandId
					+ ". The 'id' property is mandatory in every commmand-block but one or more are missing.");
		}
		if (scriptIds.contains(commandId)) {
			throw new OtcExtensionsException("", "Otc Lexicalizer-phase failure in Command with Id : " + commandId
					+ ". Duplicate Command-Id : " + commandId + " found.");
		}
	}

	/**
	 * Validate copy command.
	 *
	 * @param copy      the copy
	 * @param commandId the command id
	 */
	private static void validateCopyCommand(OtcFileDto.Copy copy, String commandId) {
		if (copy.to == null || copy.to.objectPath == null) {
			throw new OtcExtensionsException("", "Otc Lexicalizer-phase failure in Command with Id : " + commandId
					+ ". The 'to: otcChain:' property/value is missing.");
		}
		if (copy.from == null || (copy.from.objectPath == null && copy.from.values == null)) {
			throw new OtcExtensionsException("", "Otc Lexicalizer-phase failure in Command with Id : " + commandId
					+ ". Either one of 'from: otcChain/values:' is mandatory - but both are missing.");
		}
		if (copy.from.objectPath != null && copy.from.values != null) {
			throw new OtcExtensionsException("", "Otc Lexicalizer-phase failure in Command with Id : " + commandId
					+ ". Either one of 'from: otcChain/values:' should only be defined.");
		}
		if (copy.from.values != null && copy.from.overrides != null) {
			throw new OtcExtensionsException("", "Otc Lexicalizer-phase failure in Command with Id : " + commandId
					+ ". Property 'copy: from: overrides:' cannot be defined for 'from: values:' property.");
		}
		return;
	}

	/**
	 * Validate execute command.
	 *
	 * @param execute  the execute
	 * @param scriptId the script id
	 */
	private static void validateExecuteCommand(OtcFileDto.Execute execute, String scriptId) {
		if (execute.target == null || execute.target.objectPath == null) {
			throw new OtcExtensionsException("", "Otc Lexicalizer-phase failure in Command with Id : " + scriptId
					+ ". The 'target: otcChain' is missing.");
		}
		if (execute.source == null || execute.source.objectPath == null) {
			throw new OtcExtensionsException("", "Otc Lexicalizer-phase failure in Command with Id : " + scriptId
					+ ". The 'source: otcChain' property/value is missing.");
		}
		if (execute.executionOrder != null) {
			for (String execExt : execute.executionOrder) {
				if (OtcConstants.EXECUTE_OTC_MODULE.equals(execExt)) {
					if (execute.otcModule == null) {
						throw new LexicalizerException("",
								"Otc Lexicalizer-phase failure in Command with Id : " + execute.id
										+ ". 'executeOtcModule' definition "
										+ "is missing - but specified in 'executionOrder'");
					}
					break;
				}
				if (OtcConstants.EXECUTE_OTC_CONVERTER.equals(execExt)) {
					if (execute.otcConverter == null) {
						throw new LexicalizerException("",
								"Otc Lexicalizer-phase failure in Command with Id : " + execute.id
										+ ". 'executeOtcConverter' definition is missing - "
										+ "but referenced in 'executionOrder'");
					}
					break;
				}
			}
		}
	}

	/**
	 * Tokenize target chain.
	 *
	 * @param builderTargetOtcChainDto the builder target otc chain dto
	 * @param targetOtcChain           the target otc chain
	 * @param scriptDto                the script dto
	 * @param targetClz                the target clz
	 * @param mapTargetOCDs            the map target OC ds
	 * @param builderDeploymentDto     the builder deployment dto
	 * @return the otc command dto
	 */
	private static OtcCommandDto tokenizeTargetChain(OtcChainDto.Builder builderTargetOtcChainDto,
			String targetOtcChain, ScriptDto scriptDto, Class<?> targetClz, Map<String, OtcCommandDto> mapTargetOCDs,
			OtcDto.Builder builderDeploymentDto) {
		builderTargetOtcChainDto.addOtcChain(targetOtcChain);
		// --- tokenize targetOtcChain
		OtcCommandDto targetStemOCD = tokenize(scriptDto, targetClz, targetOtcChain, mapTargetOCDs,
				builderTargetOtcChainDto, TARGET_SOURCE.TARGET, null);
		mapTargetOCDs.put(targetStemOCD.otcToken, targetStemOCD);
		builderDeploymentDto.addTargetOtcCommandDtoStem(targetStemOCD);
		OtcChainDto targetOtcChainDto = builderTargetOtcChainDto.build();
		scriptDto.targetOtcChainDto = targetOtcChainDto;
		targetStemOCD.isRootNode = targetStemOCD.fieldName.equals(OtcConstants.ROOT);
		return targetStemOCD;
	}

	/**
	 * Tokenize source chain.
	 *
	 * @param builderSourceOtcChainDto the builder source otc chain dto
	 * @param sourceOtcChain           the source otc chain
	 * @param scriptDto                the script dto
	 * @param sourceClz                the source clz
	 * @param mapSourceOCDs            the map source OC ds
	 * @param builderDeploymentDto     the builder deployment dto
	 * @return the otc command dto
	 */
	private static OtcCommandDto tokenizeSourceChain(OtcChainDto.Builder builderSourceOtcChainDto,
			String sourceOtcChain, ScriptDto scriptDto, Class<?> sourceClz, Map<String, OtcCommandDto> mapSourceOCDs,
			OtcDto.Builder builderDeploymentDto) {
		if (sourceClz == null) {
			throw new LexicalizerException("", "Otc Lexicalizer-phase failure! in Command with Id : "
					+ scriptDto.command.id
					+ ". The 'from.otcChain / source.otcChain' property is defined - but the OTC filename is inconsistent due to missing source-type.");
		}
		builderSourceOtcChainDto.addOtcChain(sourceOtcChain);
		// --- tokenize sourceOtcChain
		OtcCommandDto sourceStemOCD = tokenize(scriptDto, sourceClz, sourceOtcChain, mapSourceOCDs,
				builderSourceOtcChainDto, TARGET_SOURCE.SOURCE, null);
		mapSourceOCDs.put(sourceStemOCD.otcToken, sourceStemOCD);
		builderDeploymentDto.addSourceOtcCommandDtoStem(sourceStemOCD);
		OtcChainDto sourceOtcChainDto = builderSourceOtcChainDto.build();
		scriptDto.sourceOtcChainDto = sourceOtcChainDto;
		sourceStemOCD.isRootNode = sourceStemOCD.fieldName.equals(OtcConstants.ROOT);
		return sourceStemOCD;
	}

	/**
	 * Santize factory class name.
	 *
	 * @param factoryClassName the factory class name
	 * @param otcScript        the otc script
	 * @param scriptId         the script id
	 * @param factorClzNames   the factor clz names
	 */
	private static void santizeFactoryClassName(String factoryClassName, OtcFileDto.OtcCommands otcScript,
			String scriptId, Set<String> factorClzNames) {
		if (factoryClassName == null) {
			return;
		}
		if (factoryClassName.contains(".")) {
			LOGGER.warn("Ignoring package name in Command with Id : {}. "
					+ "Package should not be specified for factoryClassName:' property.", scriptId);
			factoryClassName = factoryClassName.substring(factoryClassName.lastIndexOf(".") + 1);
			if (otcScript.execute != null) {
				otcScript.execute.factoryClassName = factoryClassName;
			} else {
				otcScript.copy.factoryClassName = factoryClassName;
			}
		}
		if (factorClzNames.contains(factoryClassName)) {
			throw new OtcExtensionsException("", "Otc Lexicalizer-phase failure in Command with Id : " + scriptId
					+ ". Duplicate 'target: factoryClassName'" + factoryClassName + " found.");
		}
		factorClzNames.add(factoryClassName);
	}

	/**
	 * Tokenize.
	 *
	 * @param script             the script
	 * @param clz                the clz
	 * @param otcChain           the otc chain
	 * @param stemMapOCDs        the stem map OC ds
	 * @param builderOtcChainDto the builder otc chain dto
	 * @param enumTargetOrSource the enum target or source
	 * @param logs               the logs
	 * @return the otc command dto
	 */
	private static OtcCommandDto tokenize(ScriptDto script, Class<?> clz, String otcChain,
			Map<String, OtcCommandDto> stemMapOCDs, OtcChainDto.Builder builderOtcChainDto,
			TARGET_SOURCE enumTargetOrSource, List<String> logs) {
		String[] otcTokens = builderOtcChainDto.getOtcTokens();
		if (otcTokens == null) {
			otcTokens = otcChain.split(OtcConstants.REGEX_OTC_ON_DOT);
			builderOtcChainDto.addOtcTokens(otcTokens);
		}
		OtcCommandDto stemOCD = null;
		OtcCommandDto parentOCD = null;
		int length = otcTokens.length;
		Class<?> parentClz = clz;
		OtcCommandDto otcCommandDto = null;
		StringBuilder tokenPathBuilder = null;
		OtcCommandContext otcCommandContext = new OtcCommandContext();
		String commandId = script.command.id;
		Map<String, OtcCommandDto> mapOCDs = stemMapOCDs;
		for (int idx = 0; idx < length; idx++) {
			String rawOtcToken = otcTokens[idx];
			String otcToken = OtcUtils.sanitizeOtc(rawOtcToken);
			if (tokenPathBuilder == null) {
				tokenPathBuilder = new StringBuilder(otcToken);
			} else {
				tokenPathBuilder.append(".").append(otcToken);
			}
			if (otcToken.contains(OtcConstants.MAP_KEY_REF)) {
				otcToken = otcToken.replace(OtcConstants.MAP_KEY_REF, "");
			} else if (otcToken.contains(OtcConstants.MAP_VALUE_REF)) {
				otcToken = otcToken.replace(OtcConstants.MAP_VALUE_REF, "");
			}
			otcCommandContext.otcTokens = otcTokens;
			if (mapOCDs.containsKey(otcToken)) {
				otcCommandDto = mapOCDs.get(otcToken);
				otcCommandDto.addCommandId(commandId);
				if (otcCommandDto.isFirstNode) {
					stemOCD = otcCommandDto;
				}
				OtcSytaxChecker.checkSyntax(script, parentClz, otcCommandDto, otcChain, otcTokens, rawOtcToken);
				if (otcCommandDto.isCollection()) {
					builderOtcChainDto.incrementCollectionCount();
					otcCommandDto = otcCommandDto.children.get(otcCommandDto.fieldName);
					otcTokens[idx] = otcToken;
				} else if (otcCommandDto.isMap()) {
					builderOtcChainDto.incrementDictionaryCount();
					OtcCommandDto memberOCD = null;
					if (rawOtcToken.contains(OtcConstants.MAP_KEY_REF)) {
						memberOCD = otcCommandDto.children.get(OtcConstants.MAP_KEY_REF + otcCommandDto.fieldName);
					} else {
						memberOCD = otcCommandDto.children.get(OtcConstants.MAP_VALUE_REF + otcCommandDto.fieldName);
					}
					otcCommandContext.otcCommandDto = memberOCD;
					boolean isLeaf = otcCommandContext.isLeaf();
					if (!isLeaf && memberOCD.children == null) {
						memberOCD.children = new LinkedHashMap<>();
					}
					otcTokens[idx] = otcToken;
					otcCommandDto = memberOCD;
				}
				otcCommandContext.otcCommandDto = otcCommandDto;
				if (!otcCommandContext.isLeaf()) {
					mapOCDs = otcCommandDto.children;
					parentOCD = otcCommandDto;
				}
				parentClz = otcCommandDto.fieldType;
				continue;
			}
			boolean isLeaf = false;
			if (idx == otcTokens.length - 1) {
				if (!rawOtcToken.contains(OtcConstants.OPEN_BRACKET)) {
					isLeaf = true;
				}
			}
			boolean isFirstNode = idx == 0 ? true : false;
			otcCommandDto = OtcCommandDtoFactory.create(commandId, enumTargetOrSource, otcToken,
					tokenPathBuilder.toString(), idx, null, null, isFirstNode, null, null, null, isLeaf);
			otcCommandDto.parent = parentOCD;
			OtcSytaxChecker.checkSyntax(script, parentClz, otcCommandDto, otcChain, otcTokens, rawOtcToken);
			if (parentOCD != null) {
				parentOCD.addChild(otcCommandDto);
			}
			if (otcCommandDto.isFirstNode) {
				stemOCD = otcCommandDto;
				mapOCDs.put(otcCommandDto.otcToken, otcCommandDto);
			}
			if (otcCommandDto.isCollection()) {
				builderOtcChainDto.incrementCollectionCount();
				otcCommandDto = otcCommandDto.children.get(otcCommandDto.fieldName);
			} else if (otcCommandDto.isMap()) {
				builderOtcChainDto.incrementDictionaryCount();
				if (rawOtcToken.contains(OtcConstants.MAP_KEY_REF)) {
					otcCommandDto = otcCommandDto.children.get(OtcConstants.MAP_KEY_REF + otcCommandDto.fieldName);
				} else if (rawOtcToken.contains(OtcConstants.MAP_VALUE_REF)) {
					otcCommandDto = otcCommandDto.children.get(OtcConstants.MAP_VALUE_REF + otcCommandDto.fieldName);
				}
			}
			mapOCDs = otcCommandDto.children;
			parentOCD = otcCommandDto;
			parentClz = otcCommandDto.fieldType;
		}
		return stemOCD;
	}

	/**
	 * Fetch factory helper.
	 *
	 * @param otcFileDto the otc file dto
	 * @return the class
	 */
	private static Class<?> fetchFactoryHelper(OtcFileDto otcFileDto) {
		if (otcFileDto.metadata == null || otcFileDto.metadata.helper == null) {
			return null;
		}
		String factoryHelper = otcFileDto.metadata.helper;
		Class<?> clzFactoryHelper = null;
		try {
			clzFactoryHelper = OtcUtils.loadClass(factoryHelper);
		} catch (OtcException otcException) {
			String msg = "Discarding 'metadata: helper'! Could not load class : " + factoryHelper;
			throw new OtcException("", msg, otcException);
		}
		return clzFactoryHelper;
	}
}
