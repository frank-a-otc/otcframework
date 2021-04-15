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
package org.otcl2.core.engine.compiler.templates;

import java.util.Map;
import java.util.Set;

import org.otcl2.common.OtclConstants;
import org.otcl2.common.OtclConstants.LogLevel;
import org.otcl2.common.dto.OtclCommandDto;
import org.otcl2.common.engine.profiler.IndexedCollectionsDtoFactory;
import org.otcl2.core.engine.compiler.command.SourceOtclCommandContext;
import org.otcl2.core.engine.compiler.command.TargetOtclCommandContext;

// TODO: Auto-generated Javadoc
/**
 * The Class PcdInitTemplate.
 */
public final class PcdInitTemplate extends AbstractTemplate {

	/**
	 * Instantiates a new pcd init template.
	 */
	private PcdInitTemplate() {}

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
	 * @param targetOCC the target OCC
	 * @param sourceOCC the source OCC
	 * @param varNamesSet the var names set
	 * @return the string
	 */
	public static String generateMemberPcdCode(TargetOtclCommandContext targetOCC, SourceOtclCommandContext sourceOCC,
			Set<String> varNamesSet) {
		StringBuilder memberPcdsCode = new StringBuilder("\n");
		String targetOtclChain = targetOCC.otclChain;
		String sourceOtclChain = sourceOCC.otclChain;
		boolean isCollectionFound = false;
		if (targetOtclChain.contains(OtclConstants.OPEN_BRACKET)) {
			targetOCC.factoryClassDto.addImport(IndexedCollectionsDtoFactory.class.getName());
			memberPcdsCode.append(String.format(parentTargetIcdTemplate));
			if (!targetOCC.hasExecuteModule && !targetOCC.hasExecuteConverter ) {
				memberPcdsCode.append(memberTargetIcdTemplate);
			}
			isCollectionFound = true;
		}
		if (sourceOtclChain != null && sourceOtclChain.contains(OtclConstants.OPEN_BRACKET)) {
			memberPcdsCode.append(String.format(parentSourceIcdTemplate));
			if (!targetOCC.hasExecuteModule && !targetOCC.hasExecuteConverter ) {
				memberPcdsCode.append(memberSourceIcdTemplate);
			}
			isCollectionFound = true;
		}
		if (isCollectionFound) {
			if (targetOtclChain.contains(OtclConstants.MAP_KEY_REF) || 
					targetOtclChain.contains(OtclConstants.MAP_VALUE_REF)) {
				memberPcdsCode.append(keyTargetIcdTemplate);
				memberPcdsCode.append(valueTargetIcdTemplate);
			}
			if (sourceOtclChain != null && (sourceOtclChain.contains(OtclConstants.MAP_KEY_REF) ||
							sourceOtclChain.contains(OtclConstants.MAP_VALUE_REF))) {
				memberPcdsCode.append(keySourceIcdTemplate);
				memberPcdsCode.append(valueSourceIcdTemplate);
			}
			memberPcdsCode.append(idxAndLenTemplate);
		}
		if (targetOCC.hasAnchorInChain) {
			memberPcdsCode.append(anchoredIcdTemplate);
		}
		if (targetOCC.hasExecuteConverter) {
			String otclConverter = targetOCC.executeOtclConverter;
			otclConverter = targetOCC.factoryClassDto.addImport(otclConverter);
			String varName = createVarName(otclConverter, varNamesSet, false);
			memberPcdsCode.append(String.format(createInstanceTemplate, otclConverter, varName, otclConverter));
		}
		return memberPcdsCode.toString();
	}
	
	/**
	 * Generate if null target root pcd create code.
	 *
	 * @param targetOCC the target OCC
	 * @param varNamesSet the var names set
	 * @param varNamesMap the var names map
	 * @return the string
	 */
	//------ Target ICD code
	public static String generateIfNullTargetRootPcdCreateCode(TargetOtclCommandContext targetOCC, Set<String> varNamesSet,
			Map<String, String> varNamesMap) {
		OtclCommandDto targetOCD = targetOCC.otclCommandDto;
		String varName = createVarName(targetOCD, false, varNamesSet, varNamesMap);
		return String.format(ifNullTargetRootIcdCreateTemplate, targetOCD.tokenPath, varName, targetOCD.tokenPath);
	}

	/**
	 * Generate if null target parent pcd create code.
	 *
	 * @param targetOCC the target OCC
	 * @param varNamesSet the var names set
	 * @param varNamesMap the var names map
	 * @return the string
	 */
	public static String generateIfNullTargetParentPcdCreateCode(TargetOtclCommandContext targetOCC,
			Set<String> varNamesSet, Map<String, String> varNamesMap) {
		OtclCommandDto targetOCD = targetOCC.otclCommandDto;
		String varName = createVarName(targetOCD, false, varNamesSet, varNamesMap);
		String icdId = createIcdKey(targetOCD.otclToken);
		String retrieveICDCode = String.format(ifNullTargetIcdCreateTemplate, PARENT_TARGET_ICD, MEMBER_TARGET_ICD, 
				icdId, PARENT_TARGET_ICD, PARENT_TARGET_ICD, MEMBER_TARGET_ICD, varName, icdId);
		return retrieveICDCode;
	}

	/**
	 * Generate if null target root pcd return code.
	 *
	 * @param targetOCC the target OCC
	 * @param logLevel the log level
	 * @return the string
	 */
	public static String generateIfNullTargetRootPcdReturnCode(TargetOtclCommandContext targetOCC, LogLevel logLevel) {
		//-- this method is required when map-value is in the otcl-chain.
		OtclCommandDto targetOCD = targetOCC.otclCommandDto;
		String logMsg = "'" + targetOCD.tokenPath + "' is null!.";
		return String.format(ifNullTargetRootIcdReturnTemplate, targetOCD.tokenPath, logLevel, logMsg);
	}

	/**
	 * Generate if null target parent pcd return code.
	 *
	 * @param targetOCC the target OCC
	 * @param logLevel the log level
	 * @return the string
	 */
	public static String generateIfNullTargetParentPcdReturnCode(TargetOtclCommandContext targetOCC, LogLevel logLevel) {
		//-- this method is required when map-value is in the otcl-chain.
		OtclCommandDto targetOCD = targetOCC.otclCommandDto;
		String logMsg = "'" + targetOCD.tokenPath + "' is null!";
		String ifNullParentPcdReturnCode = null;
		ifNullParentPcdReturnCode = String.format(ifNullIcdReturnTemplate, PARENT_TARGET_ICD, MEMBER_TARGET_ICD, 
				createIcdKey(targetOCD.otclToken), PARENT_TARGET_ICD, PARENT_TARGET_ICD, PARENT_TARGET_ICD, logLevel,
				logMsg);
		return ifNullParentPcdReturnCode;
	}
	
	/**
	 * Generate if null target member pcd return code.
	 *
	 * @param targetOCC the target OCC
	 * @param idx the idx
	 * @param logLevel the log level
	 * @return the string
	 */
	public static String generateIfNullTargetMemberPcdReturnCode(TargetOtclCommandContext targetOCC, Integer idx,
			LogLevel logLevel) {
		//-- this method is required when map-value is in the otcl-chain.
		OtclCommandDto targetOCD = targetOCC.otclCommandDto;
		String logMsg = null;
		boolean hasMapValueInPath = targetOCC.hasMapValueMember() || targetOCC.hasMapValueDescendant();
		if (hasMapValueInPath) { 
			int endIdx = targetOCC.otclChain.lastIndexOf(OtclConstants.MAP_VALUE_REF) + 3;
			String mapValueTokenPath = targetOCC.otclChain.substring(0, endIdx);
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
	 * @param logLevel the log level
	 * @return the string
	 */
	//------ Source ICD code
	public static String generateIfNullSourceRootPcdReturnCode(SourceOtclCommandContext sourceOCC, LogLevel logLevel) {
		OtclCommandDto sourceOCD = sourceOCC.otclCommandDto;
		String logMsg = "'" + sourceOCD.tokenPath + "' is null!.";
		return String.format(ifNullSourceRootIcdReturnTemplate, sourceOCD.tokenPath, logLevel, logMsg);
	}

	/**
	 * Generate if null source parent pcd return code.
	 *
	 * @param sourceOCC the source OCC
	 * @param logLevel the log level
	 * @return the string
	 */
	public static String generateIfNullSourceParentPcdReturnCode(SourceOtclCommandContext sourceOCC, LogLevel logLevel) {
		OtclCommandDto sourceOCD = sourceOCC.otclCommandDto;
		String logMsg = "'" + sourceOCD.tokenPath + "' is null!";
		String ifNullParentPcdReturnCode = String.format(ifNullIcdReturnTemplate, PARENT_SOURCE_ICD, MEMBER_SOURCE_ICD, 
				createIcdKey(sourceOCD.otclToken), PARENT_SOURCE_ICD, PARENT_SOURCE_ICD, PARENT_SOURCE_ICD, logLevel,
				logMsg);
		return ifNullParentPcdReturnCode;
	}
	
	/**
	 * Generate if null source member pcd return code.
	 *
	 * @param sourceOCC the source OCC
	 * @param idx the idx
	 * @param logLevel the log level
	 * @return the string
	 */
	public static String generateIfNullSourceMemberPcdReturnCode(SourceOtclCommandContext sourceOCC, Integer idx,
			LogLevel logLevel) {
		OtclCommandDto sourceOCD = sourceOCC.otclCommandDto;
		String logMsg = "'" + sourceOCD.tokenPath + "' is null!";
		String ifNullPcdReturnCode = null;
		if (sourceOCC.hasDescendantCollectionOrMap()) {
			ifNullPcdReturnCode = String.format(ifNullIcdReturnTemplate, MEMBER_SOURCE_ICD, PARENT_SOURCE_ICD, 
					createIcdKey(sourceOCD, null, idx), MEMBER_SOURCE_ICD, MEMBER_SOURCE_ICD, MEMBER_SOURCE_ICD, logLevel,
					logMsg);
		} else {
			ifNullPcdReturnCode = String.format(ifNullLastIcdReturnTemplate, MEMBER_SOURCE_ICD, PARENT_SOURCE_ICD, 
					createIcdKey(sourceOCD, null, idx), MEMBER_SOURCE_ICD, logLevel, logMsg);
		}
		return ifNullPcdReturnCode;
	}

}
