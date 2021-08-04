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
import org.otcframework.common.dto.OtcCommandDto;
import org.otcframework.common.dto.ScriptDto;
import org.otcframework.common.dto.otc.OtcFileDto.Copy;
import org.otcframework.common.dto.otc.OtcFileDto.Execute;
import org.otcframework.common.dto.otc.TargetDto;
import org.otcframework.core.engine.compiler.exception.SemanticsException;
import org.otcframework.core.engine.compiler.exception.SyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ConcreteTypeNotationProcessor.
 */
// TODO: Auto-generated Javadoc
final class ConcreteTypeNotationProcessor {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(OtcLexicalizer.class);

	/**
	 * Process.
	 *
	 * @param script        the script
	 * @param otcCommandDto the otc command dto
	 * @return true, if successful
	 */
	public static boolean process(ScriptDto script, OtcCommandDto otcCommandDto) {
		if (!(script.command instanceof Copy)) {
			return true;
		}
		String commandId = script.command.id;
		List<TargetDto.Override> overrides = null;
		String otcChain = null;
		if (script.command instanceof Copy) {
			Copy copy = (Copy) script.command;
			otcChain = copy.to.objectPath;
			if (copy != null && copy.to != null && copy.to.overrides != null) {
				overrides = copy.to.overrides;
			}
		} else {
			Execute execute = (Execute) script.command;
			otcChain = execute.target.objectPath;
			if (execute != null && execute.target != null && execute.target.overrides != null) {
				overrides = execute.target.overrides;
			}
		}
		if (overrides == null) {
			return true;
		}
		otcCommandDto.concreteTypeName = null;
		for (TargetDto.Override override : overrides) {
			String tokenPath = override.tokenPath;
			if (tokenPath == null) {
				throw new SyntaxException("", "Oops... Syntax error in Command-block : " + commandId
						+ ".  'overrides.tokenPath: ' is missing.");
			}
			if (tokenPath.contains(OtcConstants.ANCHOR)) {
				throw new SyntaxException("", "Oops... Syntax error in Command-block : " + commandId
						+ ".  Anchor not allowed in 'overrides.tokenPath: '" + tokenPath + "'");
			}
			if (!otcChain.startsWith(tokenPath)) {
				throw new SemanticsException("", "Irrelevant tokenPath '" + tokenPath
						+ "' in target's overrides section in command : " + commandId);
			}
			if (!otcCommandDto.tokenPath.equals(tokenPath)) {
				continue;
			}
			String concreteType = override.concreteType;
			if (concreteType == null) {
				continue;
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
								+ "' in one of these earlier commands : ",
						commandId, tokenPath, otcCommandDto.occursInCommands);
			}
		}
		return true;
	}
}
