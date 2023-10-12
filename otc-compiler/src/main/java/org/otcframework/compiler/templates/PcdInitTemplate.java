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
package org.otcframework.compiler.templates;

import org.otcframework.common.OtcConstants;
import org.otcframework.common.OtcConstants.LogLevel;
import org.otcframework.common.dto.OtcCommandDto;
import org.otcframework.common.indexer.IndexedCollectionsDtoFactory;
import org.otcframework.compiler.command.SourceOtcCommandContext;
import org.otcframework.compiler.command.TargetOtcCommandContext;

import java.util.Map;
import java.util.Set;

/**
 * The Class PcdInitTemplate.
 */
public final class PcdInitTemplate extends AbstractTemplate {

	private static final String inlineComments = "\n// ---- generator - " +
			PcdInitTemplate.class.getSimpleName() + "\n";
	/**
	 * Instantiates a new pcd init template.
	 */
	private PcdInitTemplate() {
	}

	/**
	 * Generate assign parent pcd to anchored pcd template code.
	 *
	 * @return the string
	 */
	public static String generateAssignParentPcdToAnchoredPcdTemplateCode() {
		return assignParentIcdToAnchoredIcdTemplate;
	}

	/**
	 * Generate assign anchored pcd to parent pcd template code.
	 *
	 * @return the string
	 */
	public static String generateAssignAnchoredPcdToParentPcdTemplateCode() {
		return assignAnchoredIcdToParentIcdTemplate;
	}

	/**
	 * Generate member pcd code.
	 *
	 * @param targetOCC   the target OCC
	 * @param sourceOCC   the source OCC
	 * @param varNamesSet the var names set
	 * @return the string
	 */
	public static String generateMemberPcdCode(TargetOtcCommandContext targetOCC, SourceOtcCommandContext sourceOCC,
			Set<String> varNamesSet) {
		StringBuilder memberPcdsCode = new StringBuilder("\n");
		String targetOtcChain = targetOCC.otcChain;
		String sourceOtcChain = sourceOCC.otcChain;
		boolean isCollectionFound = false;
		if (targetOtcChain.contains(OtcConstants.OPEN_BRACKET)) {
			targetOCC.factoryClassDto.addImport(IndexedCollectionsDtoFactory.class.getName());
			memberPcdsCode.append(String.format(parentTargetIcdTemplate));
			if (!targetOCC.hasExecuteModule && !targetOCC.hasExecuteConverter) {
				memberPcdsCode.append(memberTargetIcdTemplate);
			}
			isCollectionFound = true;
		}
		if (sourceOtcChain != null && sourceOtcChain.contains(OtcConstants.OPEN_BRACKET)) {
			memberPcdsCode.append(String.format(parentSourceIcdTemplate));
			if (!targetOCC.hasExecuteModule && !targetOCC.hasExecuteConverter) {
				memberPcdsCode.append(memberSourceIcdTemplate);
			}
			isCollectionFound = true;
		}
		if (isCollectionFound) {
			if (targetOtcChain.contains(OtcConstants.MAP_KEY_REF)
					|| targetOtcChain.contains(OtcConstants.MAP_VALUE_REF)) {
				memberPcdsCode.append(keyTargetIcdTemplate);
				memberPcdsCode.append(valueTargetIcdTemplate);
			}
			if (sourceOtcChain != null && (sourceOtcChain.contains(OtcConstants.MAP_KEY_REF)
					|| sourceOtcChain.contains(OtcConstants.MAP_VALUE_REF))) {
				memberPcdsCode.append(keySourceIcdTemplate);
				memberPcdsCode.append(valueSourceIcdTemplate);
			}
			memberPcdsCode.append(idxAndLenTemplate);
		}
		if (targetOCC.hasAnchorInChain) {
			memberPcdsCode.append(anchoredIcdTemplate);
		}
		if (targetOCC.hasExecuteConverter) {
			String otcConverter = targetOCC.executeOtcConverter;
			otcConverter = targetOCC.factoryClassDto.addImport(otcConverter);
			String varName = createVarName(otcConverter, varNamesSet, false);
			memberPcdsCode.append(String.format(createInstanceTemplate, otcConverter, varName, otcConverter));
		}
		return memberPcdsCode.toString();
	}

	/**
	 * Generate if null target root pcd create code.
	 *
	 * @param targetOCC   the target OCC
	 * @param varNamesSet the var names set
	 * @param varNamesMap the var names map
	 * @return the string
	 */
	// ------ Target ICD code
	public static String generateIfNullTargetRootPcdCreateCode(TargetOtcCommandContext targetOCC,
			Set<String> varNamesSet, Map<String, String> varNamesMap) {
		OtcCommandDto targetOCD = targetOCC.otcCommandDto;
		String varName = createVarName(targetOCD, false, varNamesSet, varNamesMap);
		return String.format(ifNullTargetRootIcdCreateTemplate, targetOCD.tokenPath, varName, targetOCD.tokenPath);
	}

	/**
	 * Generate if null target parent pcd create code.
	 *
	 * @param targetOCC   the target OCC
	 * @param varNamesSet the var names set
	 * @param varNamesMap the var names map
	 * @return the string
	 */
	public static String generateIfNullTargetParentPcdCreateCode(TargetOtcCommandContext targetOCC,
			Set<String> varNamesSet, Map<String, String> varNamesMap) {
		OtcCommandDto targetOCD = targetOCC.otcCommandDto;
		String varName = createVarName(targetOCD, false, varNamesSet, varNamesMap);
		String icdId = createIcdKey(targetOCD.otcToken);
		String retrieveICDCode = String.format(ifNullTargetIcdCreateTemplate, PARENT_TARGET_ICD, MEMBER_TARGET_ICD,
				icdId, PARENT_TARGET_ICD, PARENT_TARGET_ICD, MEMBER_TARGET_ICD, varName, icdId);
		return retrieveICDCode;
	}

	/**
	 * Generate if null target root pcd return code.
	 *
	 * @param targetOCC the target OCC
	 * @param logLevel  the log level
	 * @return the string
	 */
	public static String generateIfNullTargetRootPcdReturnCode(TargetOtcCommandContext targetOCC, LogLevel logLevel) {
		// -- this method is required when map-value is in the otc-chain.
		OtcCommandDto targetOCD = targetOCC.otcCommandDto;
		String logMsg = "'" + targetOCD.tokenPath + "' is null!.";
		return String.format(ifNullTargetRootIcdReturnTemplate, targetOCD.tokenPath, logLevel, logMsg);
	}

	/**
	 * Generate if null target parent pcd return code.
	 *
	 * @param targetOCC the target OCC
	 * @param logLevel  the log level
	 * @return the string
	 */
	public static String generateIfNullTargetParentPcdReturnCode(TargetOtcCommandContext targetOCC, LogLevel logLevel) {
		// -- this method is required when map-value is in the otc-chain.
		OtcCommandDto targetOCD = targetOCC.otcCommandDto;
		String logMsg = "'" + targetOCD.tokenPath + "' is null!";
		String ifNullParentPcdReturnCode = null;
		ifNullParentPcdReturnCode = String.format(ifNullIcdReturnTemplate, PARENT_TARGET_ICD, MEMBER_TARGET_ICD,
				createIcdKey(targetOCD.otcToken), PARENT_TARGET_ICD, PARENT_TARGET_ICD, PARENT_TARGET_ICD, logLevel,
				logMsg);
		return ifNullParentPcdReturnCode;
	}

	/**
	 * Generate if null target member pcd return code.
	 *
	 * @param targetOCC the target OCC
	 * @param idx       the idx
	 * @param logLevel  the log level
	 * @return the string
	 */
	public static String generateIfNullTargetMemberPcdReturnCode(TargetOtcCommandContext targetOCC, Integer idx,
			LogLevel logLevel) {
		// -- this method is required when map-value is in the otc-chain.
		OtcCommandDto targetOCD = targetOCC.otcCommandDto;
		String logMsg = null;
		boolean hasMapValueInPath = targetOCC.hasMapValueMember() || targetOCC.hasMapValueDescendant();
		if (hasMapValueInPath) {
			int endIdx = targetOCC.otcChain.lastIndexOf(OtcConstants.MAP_VALUE_REF) + 3;
			String mapValueTokenPath = targetOCC.otcChain.substring(0, endIdx);
			logMsg = "Corresponding Map-key missing for path: '" + mapValueTokenPath + "'!";
		} else {
			logMsg = "'" + targetOCD.tokenPath + "' is null!";
		}
		String ifNullParentPcdReturnCode = null;
		String icdId = createIcdKey(targetOCD, null, idx);
		if (targetOCC.hasDescendantCollectionOrMap() && !targetOCD.isMapValue()) {
			ifNullParentPcdReturnCode = String.format(ifNullIcdReturnTemplate, MEMBER_TARGET_ICD, PARENT_TARGET_ICD,
					icdId, MEMBER_TARGET_ICD, MEMBER_TARGET_ICD, MEMBER_TARGET_ICD, logLevel, logMsg);
		} else {
			ifNullParentPcdReturnCode = String.format(ifNullLastIcdReturnTemplate, MEMBER_TARGET_ICD, PARENT_TARGET_ICD,
					icdId, MEMBER_TARGET_ICD, logLevel, logMsg);
		}
		return ifNullParentPcdReturnCode;
	}

	/**
	 * Generate if null source root pcd return code.
	 *
	 * @param sourceOCC the source OCC
	 * @param logLevel  the log level
	 * @return the string
	 */
	// ------ Source ICD code
	public static String generateIfNullSourceRootPcdReturnCode(SourceOtcCommandContext sourceOCC, LogLevel logLevel) {
		OtcCommandDto sourceOCD = sourceOCC.otcCommandDto;
		String logMsg = "'" + sourceOCD.tokenPath + "' is null!.";
		return String.format(ifNullSourceRootIcdReturnTemplate, sourceOCD.tokenPath, logLevel, logMsg);
	}

	/**
	 * Generate if null source parent pcd return code.
	 *
	 * @param sourceOCC the source OCC
	 * @param logLevel  the log level
	 * @return the string
	 */
	public static String generateIfNullSourceParentPcdReturnCode(SourceOtcCommandContext sourceOCC, LogLevel logLevel) {
		OtcCommandDto sourceOCD = sourceOCC.otcCommandDto;
		String logMsg = "'" + sourceOCD.tokenPath + "' is null!";
		String ifNullParentPcdReturnCode = String.format(ifNullIcdReturnTemplate, PARENT_SOURCE_ICD, MEMBER_SOURCE_ICD,
				createIcdKey(sourceOCD.otcToken), PARENT_SOURCE_ICD, PARENT_SOURCE_ICD, PARENT_SOURCE_ICD, logLevel,
				logMsg);
		return ifNullParentPcdReturnCode;
	}

	/**
	 * Generate if null source member pcd return code.
	 *
	 * @param sourceOCC the source OCC
	 * @param idx       the idx
	 * @param logLevel  the log level
	 * @return the string
	 */
	public static String generateIfNullSourceMemberPcdReturnCode(SourceOtcCommandContext sourceOCC, Integer idx,
			LogLevel logLevel) {
		OtcCommandDto sourceOCD = sourceOCC.otcCommandDto;
		String logMsg = "'" + sourceOCD.tokenPath + "' is null!";
		String ifNullPcdReturnCode = null;
		if (sourceOCC.hasDescendantCollectionOrMap()) {
			ifNullPcdReturnCode = String.format(ifNullIcdReturnTemplate, MEMBER_SOURCE_ICD, PARENT_SOURCE_ICD,
					createIcdKey(sourceOCD, null, idx), MEMBER_SOURCE_ICD, MEMBER_SOURCE_ICD, MEMBER_SOURCE_ICD,
					logLevel, logMsg);
		} else {
			ifNullPcdReturnCode = String.format(ifNullLastIcdReturnTemplate, MEMBER_SOURCE_ICD, PARENT_SOURCE_ICD,
					createIcdKey(sourceOCD, null, idx), MEMBER_SOURCE_ICD, logLevel, logMsg);
		}
		return ifNullPcdReturnCode;
	}
}
