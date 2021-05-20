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
import org.otcl2.common.util.OtclUtils;
import org.otcl2.common.util.PackagesFilterUtil;
import org.otcl2.core.engine.compiler.exception.SemanticsException;
import org.otcl2.core.engine.utils.OtclReflectionUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class OtclSemanticsChecker.
 */
final class OtclSemanticsChecker {

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
	static boolean checkSemantics(ScriptDto script, Class<?> clz, String otclChain, OtclCommandDto otclCommandDto, 
			String[] otclTokens) {
		try {
			checkNotations(script, clz, otclChain, otclCommandDto);
			String concreteTypeName = otclCommandDto.concreteTypeName;
			Class<?> concreteType = null;
			if (concreteTypeName != null && otclCommandDto.concreteTypeName == null) {
				concreteType = OtclUtils.loadClass(concreteTypeName);
				otclCommandDto.concreteType = concreteType;
			}
			GetterSetterOverridesProcessor.process(script, otclCommandDto);
			OtclCommandDtoFactory.createMembers(script.command.id, otclCommandDto, otclChain, otclTokens);
		} catch (Exception ex) {
			if (ex instanceof OtclException) {
				throw ex;
			} else {
				throw new SemanticsException("", "Oops... Semantics-Checker error in OTCL-command : " + script.command.id +
						(TARGET_SOURCE.SOURCE == otclCommandDto.enumTargetSource ? " in from/source field '" :
								" in to/target field '") + otclCommandDto.fieldName + "' not found.");
			}
		}
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
			throw new SemanticsException("", "Oops... Semantics-Checker error in OTCL-command : " + script.command.id +
					(TARGET_SOURCE.SOURCE == otclCommandDto.enumTargetSource ? " in from/source field '" :
						" in to/target field '") + otclCommandDto.fieldName + "' not found.");
		}
		Class<?> fieldType = field.getType();
		otclCommandDto.field = field;
		otclCommandDto.fieldType = fieldType;
		List<TargetDto.Override> targetOverrides = null;
		String targetOtclChain = null;
		List<OverrideDto> sourceOverrides = null;
		String sourceOtclChain = null;
		if (script.command instanceof Execute) {
			Execute execute = (Execute) script.command;
			if (execute.otclModule != null || execute.otclConverter != null) {
				String typeName = otclCommandDto.fieldType.getName();
				//TODO this seem to be right - may need correction
				if (!PackagesFilterUtil.isFilteredPackage(typeName) && !otclCommandDto.hasCollectionNotation &&
						!otclCommandDto.hasMapNotation) {
					throw new SemanticsException("", "Oops... OTCL-command didn't pass Semantics-Checker in Id : "
							+ execute.id + " - Type : '" + typeName + "' not included in filter found.");
				}
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
			throw new SemanticsException("", "Oops... OTCL-command didn't pass Semantics-Checker in Id : "
					+ script.command.id + " 'target: overrides' may be defined only if 'target: otclChain' is defined.");
		}
		if (sourceOverrides != null && sourceOtclChain == null) {
			throw new SemanticsException("", "Oops... OTCL-command didn't pass Semantics-Checker in Id : "
					+ script.command.id + " 'source: overrides' may be defined only if 'source: otclChain' is defined.");
		}
		if (otclCommandDto.hasCollectionNotation) {
			boolean isCollection = Collection.class.isAssignableFrom(fieldType);
			if (!isCollection) {
				boolean isArray = fieldType.isArray();
				if (!isArray) {
					throw new SemanticsException("", "Oops... OTCL-command didn't pass Semantics-Checker in Id : "
							+ script.command.id + ". Field is not a Collection/Array, but Collection-notation is found.");
				}
			}
		} else if (otclCommandDto.hasMapNotation) {
			boolean isMap = Map.class.isAssignableFrom(fieldType);
			if (!isMap) {
				throw new SemanticsException("", "Oops... OTCL-command didn't pass Semantics-Checker in Id : "
						+ script.command.id + ". Field is not a Map, but Map-notation is found.");
			}
		}
	}
}
