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
import org.otcframework.common.dto.ScriptDto;
import org.otcframework.common.dto.otc.OtcFileDto.Copy;
import org.otcframework.common.dto.otc.OtcFileDto.Execute;
import org.otcframework.common.dto.otc.OverrideDto;
import org.otcframework.common.dto.otc.TargetDto;
import org.otcframework.compiler.exception.SemanticsException;
import org.otcframework.compiler.exception.SyntaxException;

import java.util.List;

/**
 * The Class GetterSetterOverridesProcessor.
 */
final class GetterSetterOverridesProcessor {

	private GetterSetterOverridesProcessor() {}

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
		overrides.forEach(override -> {
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
				return;
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
				return;
			}
			if (override.setterHelper != null) {
				otcCommandDto.setter = override.setterHelper;
				otcCommandDto.enableSetterHelper = true;
				otcCommandDto.isSetterInitialized = false;
			} else if (override.setter != null) {
				otcCommandDto.setter = override.setter;
				otcCommandDto.isSetterInitialized = false;
			}
		});
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
		overrides.forEach(override -> {
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
				return;
			}
			if (override.getterHelper != null) {
				otcCommandDto.enableGetterHelper = true;
				otcCommandDto.isGetterInitialized = false;
				otcCommandDto.getter = override.getterHelper;
			} else if (override.getter != null) {
				otcCommandDto.getter = override.getter;
				otcCommandDto.isGetterInitialized = false;
			}
		});
	}
}
