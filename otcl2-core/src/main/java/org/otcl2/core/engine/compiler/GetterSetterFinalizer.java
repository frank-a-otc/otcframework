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
import org.otcl2.common.exception.OtclException;
import org.otcl2.common.util.CommonUtils;
import org.otcl2.core.engine.utils.OtclReflectionUtil;
import org.otcl2.core.engine.utils.OtclReflectionUtil.GETTER_SETTER;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class GetterSetterProcessor.
 */
final class GetterSetterFinalizer {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(GetterSetterFinalizer.class);

	/**
	 * Process.
	 *
	 * @param script the script
	 * @param otclCommandDto the otcl command dto
	 */	
	public static void process(Map<String, OtclCommandDto> parentOCDs, Class<?> factoryHelper) {	
		if (parentOCDs == null || parentOCDs.isEmpty()) {
			return;
		}
		for (OtclCommandDto childOCD : parentOCDs.values()) {
			process(childOCD, factoryHelper);
		}
		return;
	}
	
	private static void process(OtclCommandDto ocd, Class<?> factoryHelper) {	
		if (ocd == null) {
			return;
		}
		if (!ocd.isCollectionOrMapMember()) {
			if (ocd.getter == null) {
				String getter = null;
				if (Boolean.class.isAssignableFrom(ocd.fieldType)) { 
					getter = "is" + CommonUtils.initCap(ocd.fieldName);
				} else {
					getter = "get" + CommonUtils.initCap(ocd.fieldName);
				}
				ocd.getter = getter;
			}
			if (ocd.setter == null) {
				String setter = "set" + CommonUtils.initCap(ocd.fieldName);
				ocd.setter = setter;
			}
			initGetterSetter(factoryHelper, ocd);
		}
		process(ocd.children, factoryHelper);
		return;
	}

	/**
	 * Inits the getter setter.
	 *
	 * @param factoryHelper the factory helper
	 * @param otclCommandDto the otcl command dto
	 * @param script the script
	 */
	private static void initGetterSetter(Class<?> factoryHelper, OtclCommandDto otclCommandDto) {
		if (!otclCommandDto.isSetterInitialized) {
			if (TARGET_SOURCE.TARGET == otclCommandDto.enumTargetSource) {
				if (otclCommandDto.enableFactoryHelperSetter) {
					try {
						OtclReflectionUtil.findHelperMethodName(factoryHelper, GETTER_SETTER.SETTER, 
								otclCommandDto.setter, otclCommandDto);
						otclCommandDto.isSetterInitialized = true;
					} catch (OtclException e) {
						LOGGER.warn("FactoryHelper does not have setter for " + otclCommandDto.tokenPath
								+ ". "  + e.getMessage());
						OtclReflectionUtil.findSetterName(otclCommandDto);
						otclCommandDto.isSetterInitialized = true;
					}
				} else {
					try {
						if (!otclCommandDto.fieldName.equals(OtclConstants.ROOT)) {
							OtclReflectionUtil.findSetterName(otclCommandDto);
							otclCommandDto.isSetterInitialized = true;
						}
					} catch (OtclException ex) {
						LOGGER.warn("Both declaring-class (or concreteType if defined), does not have setter for " +
								otclCommandDto.tokenPath + ". "  + ex.getMessage());
						OtclReflectionUtil.findHelperMethodName(factoryHelper, GETTER_SETTER.SETTER, 
								otclCommandDto.setter, otclCommandDto);
						otclCommandDto.isSetterInitialized = true;
					}
				}
			}
		}
		if (!otclCommandDto.isGetterInitialized) {
			if (otclCommandDto.enableFactoryHelperGetter) {
				try {
					OtclReflectionUtil.findHelperMethodName(factoryHelper, GETTER_SETTER.GETTER, 
							otclCommandDto.getter, otclCommandDto);
				} catch (OtclException e) {
					LOGGER.warn("FactoryHelper does not have getter for " + otclCommandDto.tokenPath 
							+ ". "  + e.getMessage());
					OtclReflectionUtil.findGetterName(otclCommandDto);
					otclCommandDto.isGetterInitialized = true;
				}
			} else {
				try {
					if (!otclCommandDto.fieldName.equals(OtclConstants.ROOT)) {
						OtclReflectionUtil.findGetterName(otclCommandDto);
						otclCommandDto.isGetterInitialized = true;
					}
				} catch (OtclException ex) {
					LOGGER.warn("Both declaring-class (or concreteType if defined), does not have getter for " +
							otclCommandDto.tokenPath + ". "  + ex.getMessage());
					OtclReflectionUtil.findHelperMethodName(factoryHelper, GETTER_SETTER.GETTER, 
							otclCommandDto.getter, otclCommandDto);
					otclCommandDto.isGetterInitialized = true;
				}
			}
		}
	}
}
