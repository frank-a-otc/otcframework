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

import java.util.Map;

import org.otcl2.common.OtclConstants;
import org.otcl2.common.OtclConstants.TARGET_SOURCE;
import org.otcl2.common.dto.OtclCommandDto;
import org.otcl2.common.dto.otcl.OtclFileDto;
import org.otcl2.common.dto.otcl.OtclFileDto.Copy;
import org.otcl2.common.util.CommonUtils;
import org.otcl2.common.util.OtclUtils;
import org.otcl2.core.engine.compiler.exception.SemanticsException;
import org.otcl2.core.engine.utils.OtclReflectionUtil;
import org.otcl2.core.engine.utils.OtclReflectionUtil.GETTER_SETTER;

// TODO: Auto-generated Javadoc
/**
 * The Class GetterSetterProcessor.
 */
final class GetterSetterFinalizer {
	
	/**
	 * Process.
	 *
	 * @param script the script
	 * @param otclCommandDto the otcl command dto
	 */	
	public static void process(Map<String, OtclCommandDto> parentOCDs, Class<?> factoryHelper, TARGET_SOURCE targetSource) {	
		if (parentOCDs == null || parentOCDs.isEmpty()) {
			return;
		}
		for (OtclCommandDto childOCD : parentOCDs.values()) {
			if (childOCD.isRootNode) {
				continue;
			}
			process(childOCD, factoryHelper, targetSource);
		}
		return;
	}
	
	private static void process(OtclCommandDto otclCommandDto, Class<?> factoryHelper, TARGET_SOURCE targetSource) {	
		if (otclCommandDto == null) {
			return;
		}
		if (!otclCommandDto.isCollectionOrMapMember()) {
			String targetOrSource = TARGET_SOURCE.TARGET == targetSource ? "target" : "source";
			if (TARGET_SOURCE.TARGET == targetSource) {
				try {
					initSetter(factoryHelper, otclCommandDto);
				} catch (Exception ex) {
					String setterType = "setter";
					if (otclCommandDto.enableSetterHelper) {
						setterType = "setterHelper";
					}
					throw new SemanticsException("", "Error finalizing " + setterType + " in " + targetOrSource + " '" + otclCommandDto.setter + 
							"' for : '" + otclCommandDto.tokenPath  + "' - probable conflicts in command(s) " + 
							otclCommandDto.occursInCommands, ex);
				}
			}
			try {
				initGetter(factoryHelper, otclCommandDto);
			} catch (Exception ex) {
				String getterType = "getter";
				if (otclCommandDto.enableGetterHelper) {
					getterType = "getterHelper";
				}
				throw new SemanticsException("", "Error finalizing " + getterType + " in " + targetOrSource + " '" + otclCommandDto.getter + 
						"' for : " + otclCommandDto.tokenPath + "' - probable conflicts in command(s) " + 
						otclCommandDto.occursInCommands, ex);
			}
		}
		process(otclCommandDto.children, factoryHelper, targetSource);
		return;
	}

	public static void resetLeafHelperTypes(Map<String, OtclFileDto.CommandCommonParams> mapOtclCommands, 
			Map<String, OtclCommandDto> sourceOCDs, Map<String, OtclCommandDto> targetOCDs, Class<?> factoryHelper) {
		for (OtclCommandDto childOCD : targetOCDs.values()) {
			if (childOCD.isRootNode) {
				continue;
			}
			for (String commandId : childOCD.occursInCommands) {
				OtclFileDto.CommandCommonParams otclCommand = mapOtclCommands.get(commandId);
				Copy copy = null;
				if (!(otclCommand instanceof Copy)) {
					continue;
					// Execute type is not required here.
				}
				copy = (Copy) otclCommand;
				String targetOtclChain = copy.to.otclChain;
				OtclCommandDto leafTargetOCD = OtclUtils.retrieveLeafOCD(targetOCDs, targetOtclChain); 
				if (!leafTargetOCD.enableSetterHelper) {
					continue;
				}
				String sourceOtclChain = copy.from.otclChain;
				if (sourceOtclChain == null && copy.from.values != null) {
					continue;
				}
				OtclCommandDto leafSourceOCD = OtclUtils.retrieveLeafOCD(sourceOCDs, sourceOtclChain); 
				Class<?> fieldType = leafSourceOCD.fieldType;
				leafTargetOCD.isSetterInitialized = false;
				OtclReflectionUtil.findHelperMethodName(factoryHelper, GETTER_SETTER.SETTER, leafTargetOCD, fieldType);
				leafTargetOCD.isSetterInitialized = true;

			}
		}
	}
	
	
	/**
	 * Inits the getter setter.
	 *
	 * @param factoryHelper the factory helper
	 * @param otclCommandDto the otcl command dto
	 * @param script the script
	 */
	private static void initSetter(Class<?> factoryHelper, OtclCommandDto otclCommandDto) {
		if (otclCommandDto.isSetterInitialized || otclCommandDto.isCollectionOrMapMember() ||
				(otclCommandDto.parent != null && otclCommandDto.parent.isEnum()) ||
				TARGET_SOURCE.TARGET != otclCommandDto.enumTargetSource) {
			return;
		}
		if (otclCommandDto.setter == null) {
			String setter = "set" + CommonUtils.initCap(otclCommandDto.fieldName);
			otclCommandDto.setter = setter;
		}
		if (otclCommandDto.enableSetterHelper) {
			Class<?> fieldType = otclCommandDto.fieldType;
			try {
				OtclReflectionUtil.findHelperMethodName(factoryHelper, GETTER_SETTER.SETTER, otclCommandDto, fieldType);
				otclCommandDto.isSetterInitialized = true;
			} catch (Exception ex) {
				if (!(ex instanceof SemanticsException)) {
					throw ex;
				}
			}
		} else if (!otclCommandDto.fieldName.equals(OtclConstants.ROOT)) {
			OtclReflectionUtil.findSetterName(otclCommandDto);
			otclCommandDto.isSetterInitialized = true;
		}
	}

	/**
	 * Inits the getter setter.
	 *
	 * @param factoryHelper the factory helper
	 * @param otclCommandDto the otcl command dto
	 * @param script the script
	 */
	private static void initGetter(Class<?> factoryHelper, OtclCommandDto otclCommandDto) {
		if (otclCommandDto.isGetterInitialized || otclCommandDto.isCollectionOrMapMember()) {
			return;
		}
		if (otclCommandDto.getter == null) {
			String getter = null;
			if (Boolean.class.isAssignableFrom(otclCommandDto.fieldType)) { 
				getter = "is" + CommonUtils.initCap(otclCommandDto.fieldName);
			} else {
				getter = "get" + CommonUtils.initCap(otclCommandDto.fieldName);
			}
			otclCommandDto.getter = getter;
		}
		if (otclCommandDto.enableGetterHelper) {
			OtclReflectionUtil.findHelperMethodName(factoryHelper, GETTER_SETTER.GETTER, otclCommandDto, null);
		} else if (!otclCommandDto.fieldName.equals(OtclConstants.ROOT)) {
			OtclReflectionUtil.findGetterName(otclCommandDto);
			otclCommandDto.isGetterInitialized = true;
		}
	}
}
