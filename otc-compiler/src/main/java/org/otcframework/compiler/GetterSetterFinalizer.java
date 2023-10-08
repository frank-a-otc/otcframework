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
package org.otcframework.compiler;

import org.otcframework.common.OtcConstants;
import org.otcframework.common.OtcConstants.TARGET_SOURCE;
import org.otcframework.common.dto.OtcCommandDto;
import org.otcframework.common.dto.otc.OtcFileDto;
import org.otcframework.common.dto.otc.OtcFileDto.Copy;
import org.otcframework.common.util.CommonUtils;
import org.otcframework.common.util.OtcReflectionUtil;
import org.otcframework.common.util.OtcReflectionUtil.GETTER_SETTER;
import org.otcframework.common.util.OtcUtils;
import org.otcframework.compiler.exception.SemanticsException;

import java.util.Map;

/**
 * The Class GetterSetterFinalizer.
 */
final class GetterSetterFinalizer {

	private GetterSetterFinalizer() {}
	/**
	 * Process.
	 *
	 * @param parentOCDs    the parent OC ds
	 * @param factoryHelper the factory helper
	 * @param targetSource  the target source
	 */
	public static void process(Map<String, OtcCommandDto> parentOCDs, Class<?> factoryHelper,
			TARGET_SOURCE targetSource) {
		if (parentOCDs == null || parentOCDs.isEmpty()) {
			return;
		}
		parentOCDs.values().forEach(childOCD -> {
			if (childOCD.isRootNode) {
				return;
			}
			process(childOCD, factoryHelper, targetSource);
		});
	}

	/**
	 * Process.
	 *
	 * @param otcCommandDto the otc command dto
	 * @param factoryHelper the factory helper
	 * @param targetSource  the target source
	 */
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
					throw new SemanticsException("",
							"Error finalizing " + setterType + " in " + targetOrSource + " '" + otcCommandDto.setter
									+ "' for : '" + otcCommandDto.tokenPath + "' - probable conflicts in command(s) "
									+ otcCommandDto.occursInCommands,
							ex);
				}
			}
			try {
				initGetter(factoryHelper, otcCommandDto);
			} catch (Exception ex) {
				String getterType = "getter";
				if (otcCommandDto.enableGetterHelper) {
					getterType = "getterHelper";
				}
				throw new SemanticsException("",
						"Error finalizing " + getterType + " in " + targetOrSource + " '" + otcCommandDto.getter
								+ "' for : " + otcCommandDto.tokenPath + "' - probable conflicts in command(s) "
								+ otcCommandDto.occursInCommands,
						ex);
			}
		}
		process(otcCommandDto.children, factoryHelper, targetSource);
	}

	/**
	 * Reset leaf helper types.
	 *
	 * @param mapOtcCommands the map otc commands
	 * @param sourceOCDs     the source OC ds
	 * @param targetOCDs     the target OC ds
	 * @param factoryHelper  the factory helper
	 */
	public static void resetLeafHelperTypes(Map<String, OtcFileDto.CommonCommandParams> mapOtcCommands,
			Map<String, OtcCommandDto> sourceOCDs, Map<String, OtcCommandDto> targetOCDs, Class<?> factoryHelper) {
		targetOCDs.values().forEach(childOCD -> {
			if (childOCD.isRootNode) {
				return;
			}
			childOCD.occursInCommands.forEach(commandId -> {
				OtcFileDto.CommonCommandParams otcCommand = mapOtcCommands.get(commandId);
				Copy copy = null;
				if (!(otcCommand instanceof Copy)) {
					return;
					// Execute type is not required here.
				}
				copy = (Copy) otcCommand;
				String targetOtcChain = copy.to.objectPath;
				OtcCommandDto leafTargetOCD = OtcUtils.retrieveLeafOCD(targetOCDs, targetOtcChain);
				if (!leafTargetOCD.enableSetterHelper) {
					return;
				}
				String sourceOtcChain = copy.from.objectPath;
				if (sourceOtcChain == null && copy.from.values != null) {
					return;
				}
				OtcCommandDto leafSourceOCD = OtcUtils.retrieveLeafOCD(sourceOCDs, sourceOtcChain);
				Class<?> fieldType = leafSourceOCD.fieldType;
				leafTargetOCD.isSetterInitialized = false;
				OtcReflectionUtil.findHelperMethodName(factoryHelper, GETTER_SETTER.SETTER, leafTargetOCD, fieldType);
				leafTargetOCD.isSetterInitialized = true;
			});
		});
	}

	/**
	 * Inits the setter.
	 *
	 * @param factoryHelper the factory helper
	 * @param otcCommandDto the otc command dto
	 */
	private static void initSetter(Class<?> factoryHelper, OtcCommandDto otcCommandDto) {
		if (otcCommandDto.isSetterInitialized || otcCommandDto.isCollectionOrMapMember()
				|| (otcCommandDto.parent != null && otcCommandDto.parent.isEnum())
				|| TARGET_SOURCE.TARGET != otcCommandDto.enumTargetSource) {
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
	 * Inits the getter.
	 *
	 * @param factoryHelper the factory helper
	 * @param otcCommandDto the otc command dto
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
