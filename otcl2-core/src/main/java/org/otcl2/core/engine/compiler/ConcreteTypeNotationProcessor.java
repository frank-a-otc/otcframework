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
	public static boolean process(ScriptDto script, OtclCommandDto otclCommandDto, String rawOtclToken,
			String otclChain, boolean isMapNotation, int idxArrNotation) {
		if (!(script.command instanceof Copy)) {
			return true;
		}
		String scriptId = null;
		List<TargetDto.Override> overrides = null;
		if (script.command instanceof Copy) {
			Copy copy = (Copy) script.command;
			if (copy != null && copy.to != null && copy.to.overrides != null) {
				overrides = copy.to.overrides;
			}
		} else {
			Execute execute = (Execute) script.command;
			if (execute != null && execute.target != null && execute.target.overrides != null) {
				overrides = execute.target.overrides;
			}
		}
		if (overrides == null) {
			return true;
		}
		otclCommandDto.concreteTypeName = null;
		for (TargetDto.Override override : overrides) {
			String concreteType = override.concreteType;
			if (concreteType == null) {
				continue;
			}
			String tokenPath = override.tokenPath;
			if (tokenPath.contains(OtclConstants.MAP_KEY_REF)) {
				if (otclCommandDto.mapKeyConcreteType == null) {
					otclCommandDto.mapKeyConcreteType = concreteType;
				} else {
					LOGGER.warn("Oops... Syntax error in Script-block : " + scriptId + ". Ignoring unexpected "
							+ "'override.concreteType' value '" + concreteType + "' found for Map-key <K>");
				}
			} else if (rawOtclToken.contains(OtclConstants.MAP_KEY_REF)) {
				if (otclCommandDto.mapValueConcreteType == null) {
					otclCommandDto.mapValueConcreteType = concreteType;
				} else {
					LOGGER.warn("Oops... Syntax error in Script-block : " + scriptId + ". Ignoring unexpected "
							+ "'override.concreteType' value '" + concreteType + "' found for Map-value <V>");
				}
			} else if (otclCommandDto.tokenPath.equals(tokenPath)) {
				if (otclCommandDto.concreteTypeName == null) {
					otclCommandDto.concreteTypeName = concreteType;
				} else {
					LOGGER.warn("Oops... Syntax error in Script-block : " + scriptId + ". Ignoring unexpected "
							+ "'override.concreteType' value '" + concreteType + "' found.");
				}
			}
		}
		return true;
	}
}
