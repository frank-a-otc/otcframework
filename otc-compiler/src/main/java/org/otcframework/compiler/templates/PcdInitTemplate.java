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

	private static final String INLINE_COMMENTS = "\n// ---- generator - " +
			PcdInitTemplate.class.getSimpleName() + "\n";

	private static final String IS_NULL = "' is null!";

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
		return ASSIGN_PARENT_ICD_TO_ANCHORED_ICD_TEMPLATE;
	}

	/**
	 * Generate assign anchored pcd to parent pcd template code.
	 *
	 * @return the string
	 */
	public static String generateAssignAnchoredPcdToParentPcdTemplateCode() {
		return ASSIGN_ANCHORED_ICD_TO_PARENT_ICD_TEMPLATE;
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
			memberPcdsCode.append(String.format(PARENT_TARGET_ICD_TEMPLATE));
			if (!targetOCC.hasExecuteModule && !targetOCC.hasExecuteConverter) {
				memberPcdsCode.append(MEMBER_TARGET_ICD_NULL);
			}
			isCollectionFound = true;
		}
		if (sourceOtcChain != null && sourceOtcChain.contains(OtcConstants.OPEN_BRACKET)) {
			memberPcdsCode.append(String.format(PARENT_SOURCE_ICD_TEMPLATE));
			if (!targetOCC.hasExecuteModule && !targetOCC.hasExecuteConverter) {
				memberPcdsCode.append(MEMBER_SOURCE_ICD_TEMPLATE);
			}
			isCollectionFound = true;
		}
		if (isCollectionFound) {
			if (targetOtcChain.contains(OtcConstants.MAP_KEY_REF)
					|| targetOtcChain.contains(OtcConstants.MAP_VALUE_REF)) {
				memberPcdsCode.append(KEY_TARGET_ICD_TEMPLATE);
				memberPcdsCode.append(VALUE_TARGET_ICD_TEMPLATE);
			}
			if (sourceOtcChain != null && (sourceOtcChain.contains(OtcConstants.MAP_KEY_REF)
					|| sourceOtcChain.contains(OtcConstants.MAP_VALUE_REF))) {
				memberPcdsCode.append(KEY_SOURCE_ICD_TEMPLATE);
				memberPcdsCode.append(VALUE_SOURCE_ICD_TEMPLATE);
			}
			memberPcdsCode.append(IDX_AND_LEN_TEMPLATE);
		}
		if (targetOCC.hasAnchorInChain) {
			memberPcdsCode.append(ANCHORED_ICD_TEMPLATE);
		}
		if (targetOCC.hasExecuteConverter) {
			String otcConverter = targetOCC.executeOtcConverter;
			otcConverter = targetOCC.factoryClassDto.addImport(otcConverter);
			String varName = createVarName(otcConverter, varNamesSet, false);
			memberPcdsCode.append(String.format(CREATE_INSTANCE_TEMPLATE, otcConverter, varName, otcConverter));
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
		return String.format(IF_NULL_TARGET_ROOT_ICD_CREATE_TEMPLATE, targetOCD.tokenPath, varName, targetOCD.tokenPath);
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
		return String.format(IF_NULL_TARGET_ICD_CREATE_TEMPLATE, PARENT_TARGET_ICD, MEMBER_TARGET_ICD,
				icdId, PARENT_TARGET_ICD, PARENT_TARGET_ICD, MEMBER_TARGET_ICD, varName, icdId);
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
		return String.format(IF_NULL_TARGET_ROOT_ICD_RETURN_TEMPLATE, targetOCD.tokenPath, logLevel, logMsg);
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
		String logMsg = "'" + targetOCD.tokenPath + IS_NULL;
		String ifNullParentPcdReturnCode = null;
		ifNullParentPcdReturnCode = String.format(IF_NULL_ICD_RETURN_TEMPLATE, PARENT_TARGET_ICD, MEMBER_TARGET_ICD,
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
			logMsg = "'" + targetOCD.tokenPath + IS_NULL;
		}
		String ifNullParentPcdReturnCode = null;
		String icdId = createIcdKey(targetOCD, null, idx);
		if (targetOCC.hasDescendantCollectionOrMap() && !targetOCD.isMapValue()) {
			ifNullParentPcdReturnCode = String.format(IF_NULL_ICD_RETURN_TEMPLATE, MEMBER_TARGET_ICD, PARENT_TARGET_ICD,
					icdId, MEMBER_TARGET_ICD, MEMBER_TARGET_ICD, MEMBER_TARGET_ICD, logLevel, logMsg);
		} else {
			ifNullParentPcdReturnCode = String.format(IF_NULL_LAST_ICD_RETURN_TEMPLATE, MEMBER_TARGET_ICD, PARENT_TARGET_ICD,
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
		return String.format(IF_NULL_SOURCE_ROOT_ICD_RETURN_TEMPLATE, sourceOCD.tokenPath, logLevel, logMsg);
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
		String logMsg = "'" + sourceOCD.tokenPath + IS_NULL;
		return String.format(IF_NULL_ICD_RETURN_TEMPLATE, PARENT_SOURCE_ICD, MEMBER_SOURCE_ICD,
				createIcdKey(sourceOCD.otcToken), PARENT_SOURCE_ICD, PARENT_SOURCE_ICD, PARENT_SOURCE_ICD, logLevel,
				logMsg);
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
		String logMsg = "'" + sourceOCD.tokenPath + IS_NULL;
		String ifNullPcdReturnCode = null;
		if (sourceOCC.hasDescendantCollectionOrMap()) {
			ifNullPcdReturnCode = String.format(IF_NULL_ICD_RETURN_TEMPLATE, MEMBER_SOURCE_ICD, PARENT_SOURCE_ICD,
					createIcdKey(sourceOCD, null, idx), MEMBER_SOURCE_ICD, MEMBER_SOURCE_ICD, MEMBER_SOURCE_ICD,
					logLevel, logMsg);
		} else {
			ifNullPcdReturnCode = String.format(IF_NULL_LAST_ICD_RETURN_TEMPLATE, MEMBER_SOURCE_ICD, PARENT_SOURCE_ICD,
					createIcdKey(sourceOCD, null, idx), MEMBER_SOURCE_ICD, logLevel, logMsg);
		}
		return ifNullPcdReturnCode;
	}
}
