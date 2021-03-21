/**
* Copyright (c) otclfoundation.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*/
package org.otcl2.core.engine.compiler;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.otcl2.common.OtclConstants;
import org.otcl2.common.OtclConstants.TARGET_SOURCE;
import org.otcl2.common.dto.OtclCommandDto;
import org.otcl2.common.dto.ScriptDto;
import org.otcl2.common.dto.otcl.OtclFileDto.Copy;
import org.otcl2.common.dto.otcl.OtclFileDto.Execute;
import org.otcl2.common.dto.otcl.OverrideDto;
import org.otcl2.common.dto.otcl.TargetDto;
import org.otcl2.common.exception.OtclException;
import org.otcl2.common.factory.OtclCommandDtoFactory;
import org.otcl2.common.util.CommonUtils;
import org.otcl2.common.util.PackagesFilterUtil;
import org.otcl2.core.engine.compiler.exception.SemanticsException;
import org.otcl2.core.engine.utils.OtclReflectionUtil;
import org.otcl2.core.engine.utils.OtclReflectionUtil.GETTER_SETTER;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class OtclSemanticsChecker.
 */
final class OtclSemanticsChecker {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(OtclSemanticsChecker.class);

	/**
	 * Check semantics.
	 *
	 * @param factoryHelper the factory helper
	 * @param script the script
	 * @param clz the clz
	 * @param otclChain the otcl chain
	 * @param otclCommandDto the otcl command dto
	 * @param otclTokens the otcl tokens
	 * @return true, if successful
	 */
	static boolean checkSemantics(Class<?> factoryHelper, ScriptDto script, Class<?> clz,
			String otclChain, OtclCommandDto otclCommandDto, String[] otclTokens) {
		try {
			checkNotations(script, clz, otclChain, otclCommandDto);
			String concreteTypeName = otclCommandDto.concreteTypeName;
			Class<?> concreteType = null;
			if (concreteTypeName != null && otclCommandDto.concreteTypeName == null) {
				concreteType = CommonUtils.loadClass(concreteTypeName);
				otclCommandDto.concreteType = concreteType;
			}
			initGetterSetter(factoryHelper, otclCommandDto, script);
			GetterSetterProcessor.process(script, otclCommandDto);
			OtclCommandDtoFactory.createMembers(script.command.id, otclCommandDto, otclChain, otclTokens);
		} catch (Exception ex) {
			throw new SemanticsException("", "Oops... Semantics-Checker error in Script-block : " + script.command.id +
					(TARGET_SOURCE.SOURCE == otclCommandDto.enumTargetSource ? " in from/source field '" :
							" in to/target field '") + otclCommandDto.fieldName + "' not found.");
		}
	//		LOGGER.info("Semantics processing : Okay for " + script.id);
		return true;
	}
	
	/**
	 * Check notations.
	 *
	 * @param script the script
	 * @param clz the clz
	 * @param otclChain the otcl chain
	 * @param otclCommandDto the otcl command dto
	 */
	private static void checkNotations(ScriptDto script, Class<?> clz, String otclChain, OtclCommandDto otclCommandDto) {

		if (otclCommandDto.fieldName.equals(OtclConstants.ROOT)) {
			otclCommandDto.declaringClass = clz;
			return;
		}
		Field field = OtclReflectionUtil.findField(clz, otclCommandDto.fieldName);
		if (field == null) {
			throw new SemanticsException("", "Oops... Semantics-Checker error in Script-block : " + script.command.id +
					(TARGET_SOURCE.SOURCE == otclCommandDto.enumTargetSource ? " in from/source field '" :
						" in to/target field '") + otclCommandDto.fieldName + "' not found.");
		}
		Class<?> fieldType = field.getType();
		otclCommandDto.field = field;
		otclCommandDto.fieldType = fieldType;
		boolean hasExtensions = false;
		List<TargetDto.Override> targetOverrides = null;
		String targetOtclChain = null;
		List<OverrideDto> sourceOverrides = null;
		String sourceOtclChain = null;
		if (script.command instanceof Execute) {
			Execute execute = (Execute) script.command;
			if (execute != null && (execute.otclModule != null || execute.otclConverter != null)) {
				String typeName = otclCommandDto.fieldType.getName();
				//TODO this is not right - needs correction
				if (!PackagesFilterUtil.isFilteredPackage(typeName) && !otclCommandDto.hasCollectionNotation &&
						!otclCommandDto.hasMapNotation) {
					if (TARGET_SOURCE.TARGET == otclCommandDto.enumTargetSource) {
						throw new SemanticsException("", "Oops... Script didn't pass Semantics-Checker in Script-block : "
								+ execute.id + " - Type : '" + typeName + "' not included in filter found.");
					} else {
						throw new SemanticsException("", "Oops... Script didn't pass Semantics-Checker in Script-block : "
								+ execute.id + " - Type : '" + typeName + "' not included in filter found.");
					}
				}
				hasExtensions = true;
			}
			targetOverrides = execute.target.overrides;
			targetOtclChain = execute.target.otclChain;
			sourceOverrides = execute.source.overrides;
			sourceOtclChain = execute.source.otclChain;
		} else {
			Copy copy = (Copy) script.command;
			targetOverrides = copy.to.overrides;
			targetOtclChain = copy.to.otclChain;
			sourceOverrides = copy.from.overrides;
			sourceOtclChain = copy.from.otclChain;
		}
		if (targetOverrides != null && targetOtclChain == null) {
			throw new SemanticsException("", "Oops... Script didn't pass Semantics-Checker in Script-block : "
					+ script.command.id + " 'target: overrides' may be defined only if 'target: otclChain' is defined.");
		}
		if (sourceOverrides != null && sourceOtclChain == null) {
			throw new SemanticsException("", "Oops... Script didn't pass Semantics-Checker in Script-block : "
					+ script.command.id + " 'source: overrides' may be defined only if 'source: otclChain' is defined.");
		}
		if (otclCommandDto.hasCollectionNotation) {
			boolean isCollection = Collection.class.isAssignableFrom(fieldType);
			if (!isCollection) {
				boolean isArray = fieldType.isArray();
				if (!isArray) {
					throw new SemanticsException("", "Oops... Script didn't pass Semantics-Checker in Script-block : "
							+ script.command.id + ". Field is not a Collection/Array, but Collection-notation is found.");
				}
			}
		} else if (otclCommandDto.hasMapNotation) {
			boolean isMap = Map.class.isAssignableFrom(fieldType);
			if (!isMap) {
				throw new SemanticsException("", "Oops... Script didn't pass Semantics-Checker in Script-block : "
						+ script.command.id + ". Field is not a Map, but Map-notation is found.");
			}
		}
		if (hasExtensions) {
//			logs.add("Extensions processing : Okay for " + script.command.id);
		}
	}
	
	/**
	 * Inits the getter setter.
	 *
	 * @param factoryHelper the factory helper
	 * @param otclCommandDto the otcl command dto
	 * @param script the script
	 */
	private static void initGetterSetter(Class<?> factoryHelper, OtclCommandDto otclCommandDto,
			ScriptDto script) {
		if (!otclCommandDto.isSetterInitialized) {
			if (TARGET_SOURCE.TARGET == otclCommandDto.enumTargetSource) {
				if (otclCommandDto.enableFactoryHelperSetter) {
					try {
						OtclReflectionUtil.findHelperMethodName(factoryHelper, GETTER_SETTER.SETTER, 
								otclCommandDto.setter, otclCommandDto);
						otclCommandDto.isSetterInitialized = true;
					} catch (OtclException e) {
						LOGGER.warn("FactoryHelper does not have setter for " + otclCommandDto.tokenPath
								+ " in Script-block : " + script.command.id + 
								". Semantics-checker will introspect declaring-class (or concreteType if defined) for "
								+ "suitable setter. " + e.getMessage());
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
								otclCommandDto.tokenPath + " in Script-block : " + script.command.id + 
								". Semantics-checker will introspect FactoryHelper for suitable setter. " + ex.getMessage());
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
							+ " in Script-block : " + script.command.id + 
							". Semantics-checker will introspect declaring-class (or concreteType if defined) for"
							+ " suitable getter. " + e.getMessage());
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
							otclCommandDto.tokenPath + " in Script-block : " + script.command.id + 
							". Semantics-checker will introspect FactoryHelper for suitable getter. " + ex.getMessage());
					OtclReflectionUtil.findHelperMethodName(factoryHelper, GETTER_SETTER.GETTER, 
							otclCommandDto.getter, otclCommandDto);
					otclCommandDto.isGetterInitialized = true;
				}
			}
		}
	}
}
