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

import java.util.List;

import org.otcl2.common.OtclConstants;
import org.otcl2.common.dto.OtclCommandDto;
import org.otcl2.common.dto.ScriptDto;
import org.otcl2.common.dto.otcl.OtclFileDto.Copy;
import org.otcl2.common.dto.otcl.OtclFileDto.Execute;
import org.otcl2.common.dto.otcl.TargetDto;
import org.otcl2.core.engine.compiler.exception.SemanticsException;
import org.otcl2.core.engine.compiler.exception.SyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class ConcreteTypeNotationProcessor.
 */
final class ConcreteTypeNotationProcessor {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(OtclLexicalizer.class);

	/**
	 * Process.
	 *
	 * @param script the script
	 * @param otclCommandDto the otcl command dto
	 * @param rawOtclToken the raw otcl token
	 * @param otclChain the otcl chain
	 * @param isMapNotation the is map notation
	 * @param idxArrNotation the idx arr notation
	 * @return true, if successful
	 */
	public static boolean process(ScriptDto script, OtclCommandDto otclCommandDto) {
		if (!(script.command instanceof Copy)) {
			return true;
		}
		String commandId = script.command.id;
		List<TargetDto.Override> overrides = null;
		String otclChain = null;
		if (script.command instanceof Copy) {
			Copy copy = (Copy) script.command;
			otclChain = copy.to.otclChain;
			if (copy != null && copy.to != null && copy.to.overrides != null) {
				overrides = copy.to.overrides;
			}
		} else {
			Execute execute = (Execute) script.command;
			otclChain = execute.target.otclChain;
			if (execute != null && execute.target != null && execute.target.overrides != null) {
				overrides = execute.target.overrides;
			}
		}
		if (overrides == null) {
			return true;
		}
		otclCommandDto.concreteTypeName = null;
		for (TargetDto.Override override : overrides) {
			String tokenPath = override.tokenPath;
			if (tokenPath == null) {
				throw new SyntaxException("", "Oops... Syntax error in Command-block : " + commandId + 
						".  'overrides.tokenPath: ' is missing.");
			}
			if (tokenPath.contains(OtclConstants.ANCHOR)) {
				throw new SyntaxException("", "Oops... Syntax error in Command-block : " + commandId + 
						".  Anchor not allowed in 'overrides.tokenPath: '" + tokenPath + "'");
			}
			if (!otclChain.startsWith(tokenPath)) {
				throw new SemanticsException("", "Irrelevant tokenPath '" + tokenPath + 
						"' in target's overrides section in command : " + commandId);
			}
			if (!otclCommandDto.tokenPath.equals(tokenPath)) {
				continue;
			}
			String concreteType = override.concreteType;
			if (concreteType == null) {
				continue;
			}
			boolean isErr = false;
			if (tokenPath.endsWith(OtclConstants.MAP_KEY_REF)) {
				if (otclCommandDto.mapKeyConcreteType == null) {
					otclCommandDto.mapKeyConcreteType = concreteType;
				} else {
					isErr = true;
				}
			} else if (tokenPath.endsWith(OtclConstants.MAP_VALUE_REF)) {
				if (otclCommandDto.mapValueConcreteType == null) {
					otclCommandDto.mapValueConcreteType = concreteType;
				} else {
					isErr = true;
				}
			} else {
				if (otclCommandDto.concreteTypeName == null) {
					otclCommandDto.concreteTypeName = concreteType;
				} else {
					isErr = true;
				}
			}
			if (isErr) {
				LOGGER.warn("Oops... Error in OTCL-Command-Id : {} - 'overrides.concreteType' already set earlier for : '{}" +
						"' in one of these earlier commands : ", commandId, tokenPath, otclCommandDto.occursInCommands);
			}
		}
		return true;
	}
}
