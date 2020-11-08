/**
* Copyright (c) otcl2.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*/
package org.otcl2.core.engine.compiler;

import java.io.File;
import java.io.IOException;
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
import org.otcl2.common.dto.OtclFileDto;
import org.otcl2.common.dto.OtclFileDto.Execute;
import org.otcl2.common.dto.ScriptDto;
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

final class OtclLexicalizer {

	private static final Logger LOGGER = LoggerFactory.getLogger(OtclLexicalizer.class);

	private static final Pattern FROM_OTCLCHAIN_PATTERN = Pattern.compile(OtclConstants.REGEX_CHECK_OTCLCHAIN);

	private static final ObjectMapper objectMapper;

	static {
		YAMLFactory yamlFactory = new YAMLFactory();
		yamlFactory.enable(YAMLGenerator.Feature.MINIMIZE_QUOTES);
		objectMapper = new ObjectMapper(yamlFactory);
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		objectMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
	}

	static OtclDto lexicalize(File file, String otclNamespace) {
		OtclFileDto otclFileDto = loadOtcl(file);
		if (otclFileDto.otclScripts == null) {
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
			targetClzName = fileName.substring(0, fileName.lastIndexOf(OtclConstants.OTCL_FILE_EXTN));
		} else {
			sourceClzName = fileName.substring(0, idx);
			sourceClz = CommonUtils.loadClass(sourceClzName);
			targetClzName = fileName.substring(idx + 1, fileName.lastIndexOf(OtclConstants.OTCL_FILE_EXTN));
		}
		Class<?> targetClz = CommonUtils.loadClass(targetClzName);
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
		OtclDto otclDto = tokenize(otclNamespace, fileName, otclFileDto, targetClz, sourceClz);
		otclDto.otclFileDto = otclFileDto;
		return otclDto;
	}

	static OtclFileDto loadOtcl(File file) {
		OtclFileDto otclFileDto = null;
		try {
			otclFileDto = objectMapper.readValue(file, OtclFileDto.class);
		} catch (IOException e) {
			throw new LexicalizerException("", e);
		}
		return otclFileDto;
	}

	private static OtclDto tokenize(String otclNamespace, String fileName, OtclFileDto otclFileDto, 
			Class<?> targetClz, Class<?> sourceClz) {
		Class<?> factoryHelper = fetchFactoryHelper(otclFileDto);
		Set<String> factorClzNames = new HashSet<>();
		if (otclFileDto != null && otclFileDto.metadata != null && otclFileDto.metadata.mainClassName != null) {
			String mainClassName = otclFileDto.metadata.mainClassName;
			if ( mainClassName.contains(".")) {
				LOGGER.warn("Otcl Lexicalizer-phase failure! Discarding package name! "
						+ "Package should not be specified for Mainclass in 'metadata.mainClassName'.");
				mainClassName = mainClassName.substring(mainClassName.lastIndexOf(".") + 1);
				otclFileDto.metadata.mainClassName = mainClassName;
				factorClzNames.add(mainClassName);
			}
		}
//		Map<String, ScriptGroupDto> groupedScriptDtos = new LinkedHashMap<>();
		OtclDto.Builder builderDeploymentDto = OtclDto.newBuilder().addOtclNamespace(otclNamespace)
				.addOtclFileName(fileName)
				.addSourceClz(sourceClz)
				.addTargetClz(targetClz);
//				.addGroupedScriptDtos(groupedScriptDtos);
		Map<String, OtclCommandDto> mapTargetOCDs = new LinkedHashMap<>();
		Map<String, OtclCommandDto> mapSourceOCDs = new LinkedHashMap<>();
//		List<String> logs = new ArrayList<>();
		Set<String> scriptIds = new HashSet<>();
		for (OtclFileDto.OtclScript otclScript : otclFileDto.otclScripts) {
			if ((otclScript.copy != null && otclScript.copy.debug) || 
					(otclScript.execute != null && otclScript.execute.debug)) {
				@SuppressWarnings("unused")
				int debugDummy = 0;
			}
			String scriptId = null;
			if (otclScript.copy != null) {
				scriptId = otclScript.copy.id;
			} else {
				scriptId = otclScript.execute.id;
			}
			if ((otclScript.copy != null && otclScript.copy.disable) || 
					(otclScript.execute != null && otclScript.execute.disable)) {
				LOGGER.warn("Bypassing disabled script : " + scriptId);
				continue;
			}
			if (scriptId == null) {
				throw new OtclExtensionsException("", "Otcl Lexicalizer-phase failure in Script-block : " + scriptId + 
						". The 'id' property is mandatory in every script block but one or more are missing.");
			}
			if (scriptIds.contains(scriptId)) {
				throw new OtclExtensionsException("", 
						"Otcl Lexicalizer-phase failure in Script-block : " + scriptId + ". Duplicate Script-Id" +
								scriptId + " found.");
			}
			scriptIds.add(scriptId);
			String targetOtclChain = null;
			String factoryClassName = null;
			String sourceOtclChain = null;
			boolean isValues = false;
			boolean isExtensions = false;
			if (otclScript.copy != null) {
				if (otclScript.copy.to == null || otclScript.copy.to.otclChain == null) {
					throw new OtclExtensionsException("", "Otcl Lexicalizer-phase failure in Script-block : " + scriptId
							+ ". The 'to: otclChain:' property/value is missing.");
				}
				if (otclScript.copy.from == null || (otclScript.copy.from.otclChain == null && 
						otclScript.copy.from.values == null)) {
					throw new OtclExtensionsException("", "Otcl Lexicalizer-phase failure in Script-block : " + scriptId
							+ ". Either one of 'from: otclChain/values:' is mandatory - but both are missing.");
				}
				if (otclScript.copy.from.otclChain != null && otclScript.copy.from.values != null) {
					throw new OtclExtensionsException("", "Otcl Lexicalizer-phase failure in Script-block : " + scriptId
							+ ". Either one of 'from: otclChain/values:' should only be defined.");
				}
				if (otclScript.copy.from.values != null && otclScript.copy.from.overrides != null) {
					throw new OtclExtensionsException("", "Otcl Lexicalizer-phase failure in Script-block : " + scriptId
							+ ". Property 'copy: from: overrides:' cannot be defined for 'from: values:' property.");
				}
				targetOtclChain = otclScript.copy.to.otclChain;
				factoryClassName = otclScript.copy.factoryClassName;
				if (otclScript.copy.from.otclChain != null) {
					sourceOtclChain = otclScript.copy.from.otclChain;
				} else {
					isValues = true;
				}
			} else if (otclScript.execute != null) {
				if (otclScript.execute.target == null || otclScript.execute.target.otclChain == null) {
					throw new OtclExtensionsException("", "Otcl Lexicalizer-phase failure in Script-block : " + scriptId
							+ ". The 'target: otclChain' is missing.");
				}
				if (otclScript.execute.source == null || otclScript.execute.source.otclChain == null) {
					throw new OtclExtensionsException("", "Otcl Lexicalizer-phase failure in Script-block : " + scriptId
							+ ". The 'source: otclChain' property/value is missing.");
				}
				if (otclScript.execute.executionOrder != null) {
					for (String execExt : otclScript.execute.executionOrder) {
						if (OtclConstants.EXECUTE_OTCL_MODULE.equals(execExt)) {
							if (otclScript.execute.otclModule == null) {
								throw new LexicalizerException("", "Otcl Lexicalizer-phase failure in Script-block : " +
										otclScript.execute.id + ". 'executeOtclModule' definition "
										+ "is missing - but specified in 'executionOrder'");
							}
							break;
						}
						if (OtclConstants.EXECUTE_OTCL_CONVERTER.equals(execExt)) {
							if (otclScript.execute.otclConverter == null) {
								throw new LexicalizerException("", "Otcl Lexicalizer-phase failure in Script-block : " +
										otclScript.execute.id + ". 'executeOtclConverter' definition is missing - "
										+ "but referenced in 'executionOrder'");
							}
							break;
						}
					}
				}
				targetOtclChain = otclScript.execute.target.otclChain;
				factoryClassName = otclScript.execute.factoryClassName;
				sourceOtclChain = otclScript.execute.source.otclChain;
				isExtensions = true;
			}
			if (factoryClassName != null) {
				if (factoryClassName.contains(".")) {
					LOGGER.warn("Ignoring package name in Script-block : " + scriptId + 
							". Package should not be specified for factoryClassName:' property.");
					factoryClassName = factoryClassName.substring(factoryClassName.lastIndexOf(".") + 1);
					if (otclScript.execute != null) {
						otclScript.execute.factoryClassName = factoryClassName;
					} else {
						otclScript.copy.factoryClassName = factoryClassName;
					}
				}
				if (factorClzNames.contains(factoryClassName)) {
					throw new OtclExtensionsException("", 
							"Otcl Lexicalizer-phase failure in Script-block : " + scriptId +
							". Duplicate 'target: factoryClassName'" + factoryClassName + " found.");
				}
				factorClzNames.add(factoryClassName);
			}
			if (targetOtclChain.endsWith(".")) {
				targetOtclChain = targetOtclChain.substring(0, targetOtclChain.length() - 1);
			}
			if (!CommonUtils.isEmpty(sourceOtclChain) && sourceOtclChain.endsWith(".")) {
				sourceOtclChain = sourceOtclChain.substring(0, sourceOtclChain.length() - 1);
			}
			OtclChainDto.Builder builderTargetOtclChainDto = OtclChainDto.newBuilder();
			OtclChainDto.Builder builderSourceOtclChainDto = OtclChainDto.newBuilder();
			ScriptDto scriptDto = new ScriptDto(otclScript);
			Execute execute = scriptDto.command instanceof Execute ? (Execute) scriptDto.command : null;
			if (isValues || (execute != null && (execute.otclConverter != null || execute.otclModule != null))) {
				OtclExtensionsValidator.validateExtensions(scriptDto, targetClz, builderTargetOtclChainDto, sourceClz,
						builderSourceOtclChainDto);
				if (isValues) {
					scriptDto.hasSetValues = true;
				}
			}
			try {
				builderTargetOtclChainDto.addOtclChain(targetOtclChain);
				// --- tokenize targetOtclChain
				OtclCommandDto targetStemOCD = tokenize(scriptDto, factoryHelper, targetClz, targetOtclChain,
						mapTargetOCDs, builderTargetOtclChainDto, TARGET_SOURCE.TARGET, null);
				mapTargetOCDs.put(targetStemOCD.otclToken, targetStemOCD);
				builderDeploymentDto.addTargetOtclCommandDtoStem(targetStemOCD);
				OtclChainDto targetOtclChainDto = builderTargetOtclChainDto.build();
				scriptDto.targetOtclChainDto = targetOtclChainDto;

				int targetCollectionsCount = targetOtclChainDto.collectionCount + targetOtclChainDto.dictionaryCount;
				int sourceCollectionsCount = 0;
				OtclChainDto sourceOtclChainDto = null;
				if (!CommonUtils.isEmpty(sourceOtclChain)) {
					builderSourceOtclChainDto.addOtclChain(sourceOtclChain);
					// --- tokenize sourceOtclChain
					OtclCommandDto sourceStemOCD = tokenize(scriptDto, factoryHelper, sourceClz, sourceOtclChain,
							mapSourceOCDs, builderSourceOtclChainDto, TARGET_SOURCE.SOURCE, null);
					OtclCommandContext targetOCC = new OtclCommandContext();
					targetOCC.otclCommandDto = targetStemOCD;
					targetOCC.otclTokens = builderTargetOtclChainDto.getOtclTokens();
					targetOCC.rawOtclTokens = builderTargetOtclChainDto.getRawOtclTokens();
					OtclCommandContext sourceOCC = new OtclCommandContext();
					sourceOCC.otclCommandDto = sourceStemOCD;
					sourceOCC.otclTokens = builderSourceOtclChainDto.getOtclTokens();
					sourceOCC.rawOtclTokens = builderSourceOtclChainDto.getRawOtclTokens();
					OtclLeavesSemanticsChecker.checkLeavesSemantics(targetOCC, sourceOCC);
					mapSourceOCDs.put(sourceStemOCD.otclToken, sourceStemOCD);
					builderDeploymentDto.addSourceOtclCommandDtoStem(sourceStemOCD);
					sourceOtclChainDto = builderSourceOtclChainDto.build();
					scriptDto.sourceOtclChainDto = sourceOtclChainDto;
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
//					if (scriptDto.command.factoryClassName.contains(".")) {
//						int idx = scriptDto.command.factoryClassName.lastIndexOf(".");
//						scriptDto.command.factoryClassName = scriptDto.command.factoryClassName.substring(idx + 1);
//					}
				}
				builderDeploymentDto.addScriptDto(scriptDto);
			} catch (Exception ex) {
				LOGGER.error("", ex);
				if (ex instanceof OtclException) {
					throw ex;
				}
				throw new LexicalizerException("", "Otcl Lexicalizer-phase failure! " + ex);
			}
		}
		return builderDeploymentDto.build();
	}

//	private static ScriptGroupDto addScriptDtoToGroup(Map<String, ScriptGroupDto> groupedScriptDtos, String chainPath,
//			ScriptDto scriptDto, OtclChainDto targetOtclChainDto, OtclChainDto sourceOtclChainDto,
//			ALGORITHM_ID algorithmId) {
//		String key = chainPath;
//		if (ALGORITHM_ID.SETVALUE == algorithmId || ALGORITHM_ID.CONVERTER == algorithmId ||
//				ALGORITHM_ID.MODULE == algorithmId) {
//			if (scriptDto.hasExecutionOrder) {
//				scriptDto = scriptDto.clone();
//			}
//			if (ALGORITHM_ID.SETVALUE == algorithmId) {
//				key += "_SetValue";
//				scriptDto.hasExecuteModule = false;
//				scriptDto.hasExecuteConverter = false;
//			} else if (ALGORITHM_ID.CONVERTER == algorithmId) {
//				key += "_ExecuteConverter";
//				scriptDto.command.factoryClassName += "_ExecuteConverter";
//				((Execute) scriptDto.command).otclModule = null;
//				scriptDto.hasExecuteModule = false;
//				scriptDto.hasSetValues = false;
//			} else if (ALGORITHM_ID.MODULE == algorithmId) {
//				key += "_ExecuteModule";
//				scriptDto.command.factoryClassName += "_ExecuteModule";
//				((Execute) scriptDto.command).otclConverter = null;
//				scriptDto.hasExecuteConverter = false;
//				scriptDto.hasSetValues = false;
//			}
//		}
//		ScriptGroupDto scriptGroupDto = groupedScriptDtos.get(key);
////		-- check for duplicates ---
////		if (scriptGroupDto != null && !targetOtclChainDto.otclChain.contains(OtclConstants.ANCHOR)) { 
////			StringBuilder scriptIdBuilder = null;
////			for (ScriptDto duplicateScriptDto : scriptGroupDto.scriptDtos) {
////				if (scriptIdBuilder == null) {
////					scriptIdBuilder = new StringBuilder();
////				} else {
////					scriptIdBuilder.append(", ");
////				}
////				scriptIdBuilder.append(duplicateScriptDto.command.id);
////			}
////			String errMsg = null;
////			if (scriptDto.hasExecuteModule) {
////				errMsg = "Duplicate 'extensions: executeOtclModule' definition found in Script-Id : " + scriptDto.command.id;
////			} else if (scriptDto.hasExecuteConverter) {
////				errMsg = "Duplicate 'extensions: executeOtclConverter' definition found in Script-Id : " +
////						scriptDto.command.id;
////			} else {
////				errMsg = "Duplicate target / target-source combination found in Script-Id : " + scriptDto.command.id;
////			}
////			errMsg += ". Script-block conflicts with " + scriptIdBuilder.toString();
////			throw new LexicalizerException("", errMsg);
////		}
//		if (scriptGroupDto == null) {
//			scriptGroupDto = new ScriptGroupDto();
//			scriptGroupDto.scriptDtos = new ArrayList<>();
//			groupedScriptDtos.put(key, scriptGroupDto);
//		}
//		scriptGroupDto.scriptDtos.add(scriptDto);
//		if (scriptDto.command instanceof Execute) {
//			Execute execute = (Execute) scriptDto.command;
//			if (execute != null && execute.executionOrder != null) {
//				scriptDto.hasExecutionOrder = true;
//				for (String execExt : execute.executionOrder) {
//					if (ALGORITHM_ID.MODULE == algorithmId && OtclConstants.EXECUTE_OTCL_MODULE.equals(execExt)) {
//						if (execute.otclModule == null) {
//							throw new LexicalizerException("", "Otcl Lexicalizer-phase failure in Script-block : " +
//									scriptDto.command.id + ". 'executeOtclModule' definition "
//									+ "is missing - but specified in 'executionOrder'");
//						}
//						break;
//					}
//					if (ALGORITHM_ID.CONVERTER == algorithmId && OtclConstants.EXECUTE_OTCL_CONVERTER.equals(execExt)) {
//						if (execute.otclConverter == null) {
//							throw new LexicalizerException("", "Otcl Lexicalizer-phase failure in Script-block : " +
//									scriptDto.command.id + ". 'executeOtclConverter' definition is missing - "
//									+ "but referenced in 'executionOrder'");
//						}
//						break;
//					}
//				}
//			}
//		}
//		return scriptGroupDto;
//	}
	
	private static OtclCommandDto tokenize(ScriptDto script, Class<?> factoryHelper, Class<?> clz, String otclChain, 
			Map<String, OtclCommandDto> mapOCDs, OtclChainDto.Builder builderOtclChainDto, 
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
				if (otclCommandDto.isRootNode) {
					stemOCD = otclCommandDto;
				}
				OtclSytaxChecker.checkSyntax(script, parentClz, factoryHelper, otclCommandDto,
						otclChain, otclTokens, rawOtclToken);
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
			boolean isRootNode = idx == 0 ? true : false;
			otclCommandDto = OtclCommandDtoFactory.create(enumTargetOrSource, otclToken, tokenPathBuilder.toString(),
					idx, null, null, isRootNode, null, null, null, isLeaf);
			otclCommandDto.parent = parentOCD;
			OtclSytaxChecker.checkSyntax(script, parentClz, factoryHelper, otclCommandDto, otclChain,
					otclTokens, rawOtclToken);
			if (parentOCD != null) {
				parentOCD.addChild(otclCommandDto);
			}
			if (otclCommandDto.isRootNode) {
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

	private static Class<?> fetchFactoryHelper(OtclFileDto otclFileDto) {
		if (otclFileDto.metadata == null || otclFileDto.metadata.helper == null) {
			return null;
		}
		String factoryHelper = otclFileDto.metadata.helper;
		Class<?> clzFactoryHelper = null;
		try {
			clzFactoryHelper =  CommonUtils.loadClass(factoryHelper);
		} catch (OtclException otclException) {
			String msg = "Discarding 'metadata: helper'! Could not load class : " + factoryHelper;
			throw new OtclException("", msg, otclException);
		}
		return clzFactoryHelper;
	}
}
