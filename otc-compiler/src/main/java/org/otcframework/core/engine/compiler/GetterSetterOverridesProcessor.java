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

import java.util.List;

import org.otcframework.common.OtcConstants;
import org.otcframework.common.OtcConstants.TARGET_SOURCE;
import org.otcframework.common.dto.OtcCommandDto;
import org.otcframework.common.dto.ScriptDto;
import org.otcframework.common.dto.otc.OtcFileDto.Copy;
import org.otcframework.common.dto.otc.OtcFileDto.Execute;
import org.otcframework.common.dto.otc.OverrideDto;
import org.otcframework.common.dto.otc.TargetDto;
import org.otcframework.core.engine.compiler.exception.SemanticsException;
import org.otcframework.core.engine.compiler.exception.SyntaxException;

/**
 * The Class GetterSetterOverridesProcessor.
 */
// TODO: Auto-generated Javadoc
final class GetterSetterOverridesProcessor {

	/**
	 * Process.
	 *
	 * @param script        the script
	 * @param otcCommandDto the otc command dto
	 */
	public static void process(ScriptDto script, OtcCommandDto otcCommandDto) {
		if (OtcConstants.ROOT.equals(otcCommandDto.fieldName)) {
			return;
		}
		String sourceOtcChain = null;
		String targetOtcChain = null;
		String commandId = script.command.id;
		List<OverrideDto> fromOverrides = null;
		List<TargetDto.Override> toOverrides = null;
		if (script.command instanceof Copy) {
			Copy copy = (Copy) script.command;
			sourceOtcChain = copy.from.objectPath;
			targetOtcChain = copy.to.objectPath;
			fromOverrides = copy.from.overrides;
			toOverrides = copy.to.overrides;
		} else {
			Execute execute = (Execute) script.command;
			sourceOtcChain = execute.source.objectPath;
			targetOtcChain = execute.target.objectPath;
			fromOverrides = execute.source.overrides;
			toOverrides = execute.target.overrides;
		}
		if (toOverrides == null && fromOverrides == null) {
			return;
		}
		if (otcCommandDto.getter == null && fromOverrides != null) {
			initFromGetter(otcCommandDto, sourceOtcChain, commandId, fromOverrides);
		}
		if (TARGET_SOURCE.TARGET == otcCommandDto.enumTargetSource && otcCommandDto.setter == null
				&& toOverrides != null) {
			initToSetterGetter(otcCommandDto, targetOtcChain, commandId, toOverrides);
		}
		return;
	}

	/**
	 * Inits the to setter getter.
	 *
	 * @param otcCommandDto  the otc command dto
	 * @param targetOtcChain the target otc chain
	 * @param commandId      the command id
	 * @param overrides      the overrides
	 */
	private static void initToSetterGetter(OtcCommandDto otcCommandDto, String targetOtcChain, String commandId,
			List<TargetDto.Override> overrides) {
		if (overrides == null || TARGET_SOURCE.TARGET != otcCommandDto.enumTargetSource
				|| (otcCommandDto.getter != null && otcCommandDto.setter != null)) {
			return;
		}
		if (TARGET_SOURCE.TARGET != otcCommandDto.enumTargetSource) {
			return;
		}
		for (TargetDto.Override override : overrides) {
			String tokenPath = override.tokenPath;
			if (tokenPath == null) {
				throw new SyntaxException("", "Oops... Syntax error in Command-block : " + commandId
						+ ".  'overrides.tokenPath: ' is missing.");
			}
			if (!targetOtcChain.startsWith(tokenPath)) {
				throw new SemanticsException("", "Irrelevant tokenPath '" + tokenPath
						+ "' in targets overrides section in command : " + commandId);
			}
			if (!otcCommandDto.tokenPath.equals(tokenPath)) {
				continue;
			}
			if (otcCommandDto.getter == null) {
				if (override.getterHelper != null) {
					otcCommandDto.getter = override.getterHelper;
					otcCommandDto.enableGetterHelper = true;
					otcCommandDto.isGetterInitialized = false;
				} else if (override.getter != null) {
					otcCommandDto.getter = override.getter;
					otcCommandDto.isGetterInitialized = false;
				}
			}
			if (otcCommandDto.setter != null) {
				continue;
			}
			if (override.setterHelper != null) {
				otcCommandDto.setter = override.setterHelper;
				otcCommandDto.enableSetterHelper = true;
				otcCommandDto.isSetterInitialized = false;
			} else if (override.setter != null) {
				otcCommandDto.setter = override.setter;
				otcCommandDto.isSetterInitialized = false;
			}
		}
	}

	/**
	 * Inits the from getter.
	 *
	 * @param otcCommandDto the otc command dto
	 * @param otcChain      the otc chain
	 * @param commandId     the command id
	 * @param overrides     the overrides
	 */
	private static void initFromGetter(OtcCommandDto otcCommandDto, String otcChain, String commandId,
			List<OverrideDto> overrides) {
		if (overrides == null || otcCommandDto.getter != null) {
			return;
		}
		for (OverrideDto override : overrides) {
			String tokenPath = override.tokenPath;
			if (tokenPath == null) {
				throw new SyntaxException("", "Oops... Syntax error in Command-block : " + commandId
						+ ".  'overrides.tokenPath: ' is missing.");
			}
			if (!otcChain.startsWith(tokenPath)) {
				throw new SemanticsException("", "Irrelevant tokenPath '" + tokenPath
						+ "' in source's overrides section in command : " + commandId);
			}
			if (!otcCommandDto.tokenPath.equals(tokenPath)) {
				continue;
			}
			if (override.getterHelper != null) {
				otcCommandDto.enableGetterHelper = true;
				otcCommandDto.isGetterInitialized = false;
				otcCommandDto.getter = override.getterHelper;
			} else if (override.getter != null) {
				otcCommandDto.getter = override.getter;
				otcCommandDto.isGetterInitialized = false;
			}
		}
	}
}
