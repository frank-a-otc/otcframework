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

import java.util.Map;

import org.otcframework.common.OtcConstants;
import org.otcframework.common.OtcConstants.TARGET_SOURCE;
import org.otcframework.common.dto.OtcCommandDto;
import org.otcframework.common.dto.otc.OtcFileDto;
import org.otcframework.common.dto.otc.OtcFileDto.Copy;
import org.otcframework.common.util.CommonUtils;
import org.otcframework.common.util.OtcUtils;
import org.otcframework.core.engine.compiler.exception.SemanticsException;
import org.otcframework.core.engine.utils.OtcReflectionUtil;
import org.otcframework.core.engine.utils.OtcReflectionUtil.GETTER_SETTER;

// TODO: Auto-generated Javadoc
/**
 * The Class GetterSetterProcessor.
 */
final class GetterSetterFinalizer {
	
	/**
	 * Process.
	 *
	 * @param script the script
	 * @param otcCommandDto the otc command dto
	 */	
	public static void process(Map<String, OtcCommandDto> parentOCDs, Class<?> factoryHelper, TARGET_SOURCE targetSource) {	
		if (parentOCDs == null || parentOCDs.isEmpty()) {
			return;
		}
		for (OtcCommandDto childOCD : parentOCDs.values()) {
			if (childOCD.isRootNode) {
				continue;
			}
			process(childOCD, factoryHelper, targetSource);
		}
		return;
	}
	
	private static void process(OtcCommandDto otcCommandDto, Class<?> factoryHelper, TARGET_SOURCE targetSource) {	
		if (otcCommandDto == null) {
			return;
		}
		if (!otcCommandDto.isCollectionOrMapMember()) {
			String targetOrSource = TARGET_SOURCE.TARGET == targetSource ? "target" : "source";
			if (TARGET_SOURCE.TARGET == targetSource) {
				try {
					initSetter(factoryHelper, otcCommandDto);
				} catch (Exception ex) {
					String setterType = "setter";
					if (otcCommandDto.enableSetterHelper) {
						setterType = "setterHelper";
					}
					throw new SemanticsException("", "Error finalizing " + setterType + " in " + targetOrSource + " '" + otcCommandDto.setter + 
							"' for : '" + otcCommandDto.tokenPath  + "' - probable conflicts in command(s) " + 
							otcCommandDto.occursInCommands, ex);
				}
			}
			try {
				initGetter(factoryHelper, otcCommandDto);
			} catch (Exception ex) {
				String getterType = "getter";
				if (otcCommandDto.enableGetterHelper) {
					getterType = "getterHelper";
				}
				throw new SemanticsException("", "Error finalizing " + getterType + " in " + targetOrSource + " '" + otcCommandDto.getter + 
						"' for : " + otcCommandDto.tokenPath + "' - probable conflicts in command(s) " + 
						otcCommandDto.occursInCommands, ex);
			}
		}
		process(otcCommandDto.children, factoryHelper, targetSource);
		return;
	}

	public static void resetLeafHelperTypes(Map<String, OtcFileDto.CommandCommonParams> mapOtcCommands, 
			Map<String, OtcCommandDto> sourceOCDs, Map<String, OtcCommandDto> targetOCDs, Class<?> factoryHelper) {
		for (OtcCommandDto childOCD : targetOCDs.values()) {
			if (childOCD.isRootNode) {
				continue;
			}
			for (String commandId : childOCD.occursInCommands) {
				OtcFileDto.CommandCommonParams otcCommand = mapOtcCommands.get(commandId);
				Copy copy = null;
				if (!(otcCommand instanceof Copy)) {
					continue;
					// Execute type is not required here.
				}
				copy = (Copy) otcCommand;
				String targetOtcChain = copy.to.otcChain;
				OtcCommandDto leafTargetOCD = OtcUtils.retrieveLeafOCD(targetOCDs, targetOtcChain); 
				if (!leafTargetOCD.enableSetterHelper) {
					continue;
				}
				String sourceOtcChain = copy.from.otcChain;
				if (sourceOtcChain == null && copy.from.values != null) {
					continue;
				}
				OtcCommandDto leafSourceOCD = OtcUtils.retrieveLeafOCD(sourceOCDs, sourceOtcChain); 
				Class<?> fieldType = leafSourceOCD.fieldType;
				leafTargetOCD.isSetterInitialized = false;
				OtcReflectionUtil.findHelperMethodName(factoryHelper, GETTER_SETTER.SETTER, leafTargetOCD, fieldType);
				leafTargetOCD.isSetterInitialized = true;

			}
		}
	}
	
	
	/**
	 * Inits the getter setter.
	 *
	 * @param factoryHelper the factory helper
	 * @param otcCommandDto the otc command dto
	 * @param script the script
	 */
	private static void initSetter(Class<?> factoryHelper, OtcCommandDto otcCommandDto) {
		if (otcCommandDto.isSetterInitialized || otcCommandDto.isCollectionOrMapMember() ||
				(otcCommandDto.parent != null && otcCommandDto.parent.isEnum()) ||
				TARGET_SOURCE.TARGET != otcCommandDto.enumTargetSource) {
			return;
		}
		if (otcCommandDto.setter == null) {
			String setter = "set" + CommonUtils.initCap(otcCommandDto.fieldName);
			otcCommandDto.setter = setter;
		}
		if (otcCommandDto.enableSetterHelper) {
			Class<?> fieldType = otcCommandDto.fieldType;
			try {
				OtcReflectionUtil.findHelperMethodName(factoryHelper, GETTER_SETTER.SETTER, otcCommandDto, fieldType);
				otcCommandDto.isSetterInitialized = true;
			} catch (Exception ex) {
				if (!(ex instanceof SemanticsException)) {
					throw ex;
				}
			}
		} else if (!otcCommandDto.fieldName.equals(OtcConstants.ROOT)) {
			OtcReflectionUtil.findSetterName(otcCommandDto);
			otcCommandDto.isSetterInitialized = true;
		}
	}

	/**
	 * Inits the getter setter.
	 *
	 * @param factoryHelper the factory helper
	 * @param otcCommandDto the otc command dto
	 * @param script the script
	 */
	private static void initGetter(Class<?> factoryHelper, OtcCommandDto otcCommandDto) {
		if (otcCommandDto.isGetterInitialized || otcCommandDto.isCollectionOrMapMember()) {
			return;
		}
		if (otcCommandDto.getter == null) {
			String getter = null;
			if (Boolean.class.isAssignableFrom(otcCommandDto.fieldType)) { 
				getter = "is" + CommonUtils.initCap(otcCommandDto.fieldName);
			} else {
				getter = "get" + CommonUtils.initCap(otcCommandDto.fieldName);
			}
			otcCommandDto.getter = getter;
		}
		if (otcCommandDto.enableGetterHelper) {
			OtcReflectionUtil.findHelperMethodName(factoryHelper, GETTER_SETTER.GETTER, otcCommandDto, null);
		} else if (!otcCommandDto.fieldName.equals(OtcConstants.ROOT)) {
			OtcReflectionUtil.findGetterName(otcCommandDto);
			otcCommandDto.isGetterInitialized = true;
		}
	}
}
