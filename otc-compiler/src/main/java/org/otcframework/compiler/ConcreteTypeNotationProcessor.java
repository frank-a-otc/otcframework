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
import org.otcframework.common.dto.OtcCommandDto;
import org.otcframework.common.dto.ScriptDto;
import org.otcframework.common.dto.otc.OtcFileDto.Copy;
import org.otcframework.common.dto.otc.OtcFileDto.Execute;
import org.otcframework.common.dto.otc.TargetDto;
import org.otcframework.compiler.exception.SemanticsException;
import org.otcframework.compiler.exception.SyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * The Class ConcreteTypeNotationProcessor.
 */
final class ConcreteTypeNotationProcessor {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(ConcreteTypeNotationProcessor.class);

	private ConcreteTypeNotationProcessor() {}
	/**
	 * Process.
	 *
	 * @param script        the script
	 * @param otcCommandDto the otc command dto
	 */
	public static void process(ScriptDto script, OtcCommandDto otcCommandDto) {
		String commandId = script.command.id;
		List<TargetDto.Override> overrides = null;
		String objectPath;
		if (script.command instanceof Copy) {
			Copy copy = (Copy) script.command;
			objectPath = copy.to.objectPath;
			if (copy.to.overrides != null) {
				overrides = copy.to.overrides;
			}
		} else {
			Execute execute = (Execute) script.command;
			objectPath = execute.target.objectPath;
			if (execute.target.overrides != null) {
				overrides = execute.target.overrides;
			}
		}
		if (overrides == null) {
			return;
		}
		otcCommandDto.concreteTypeName = null;
		overrides.forEach(override -> {
			String tokenPath = override.tokenPath;
			if (tokenPath == null) {
				throw new SyntaxException("", "Oops... Syntax error in Command-block : " + commandId
						+ ".  'overrides.tokenPath: ' is missing.");
			}
			if (tokenPath.contains(OtcConstants.ANCHOR)) {
				throw new SyntaxException("", "Oops... Syntax error in Command-block : " + commandId
						+ ".  Anchor not allowed in 'overrides.tokenPath: '" + tokenPath + "'");
			}
			if (!objectPath.startsWith(tokenPath)) {
				throw new SemanticsException("", "Irrelevant tokenPath '" + tokenPath
						+ "' in target's overrides section in command : " + commandId);
			}
			if (!otcCommandDto.tokenPath.equals(tokenPath)) {
				return;
			}
			String concreteType = override.concreteType;
			if (concreteType == null) {
				return;
			}
			boolean isErr = false;
			if (tokenPath.endsWith(OtcConstants.MAP_KEY_REF)) {
				if (otcCommandDto.mapKeyConcreteType == null) {
					otcCommandDto.mapKeyConcreteType = concreteType;
				} else {
					isErr = true;
				}
			} else if (tokenPath.endsWith(OtcConstants.MAP_VALUE_REF)) {
				if (otcCommandDto.mapValueConcreteType == null) {
					otcCommandDto.mapValueConcreteType = concreteType;
				} else {
					isErr = true;
				}
			} else {
				if (otcCommandDto.concreteTypeName == null) {
					otcCommandDto.concreteTypeName = concreteType;
				} else {
					isErr = true;
				}
			}
			if (isErr) {
				LOGGER.warn(
						"Oops... Error in OTC-Command-Id : {} - 'overrides.concreteType' already set earlier for : '{}"
								+ "' in one of these earlier commands : {}",
						commandId, tokenPath, otcCommandDto.occursInCommands);
			}
		});
	}
}
