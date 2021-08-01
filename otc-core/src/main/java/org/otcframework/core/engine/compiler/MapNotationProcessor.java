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

import org.otcframework.common.OtcConstants;
import org.otcframework.common.dto.OtcCommandDto;
import org.otcframework.core.engine.compiler.exception.SyntaxException;

// TODO: Auto-generated Javadoc
/**
 * The Class MapNotationProcessor.
 */
final class MapNotationProcessor {

	/**
	 * Process.
	 *
	 * @param scriptId the script id
	 * @param otcCommandDto the otc command dto
	 * @param otcToken the otc token
	 * @param otcChain the otc chain
	 * @param idxMapNotation the idx map notation
	 * @return true, if successful
	 */
	public static boolean process(String scriptId, OtcCommandDto otcCommandDto, String otcToken,
			String otcChain, int idxMapNotation) {
		if (!otcToken.contains(OtcConstants.MAP_BEGIN_REF) && !otcToken.contains(OtcConstants.MAP_PRE_ANCHOR) &&
				!otcToken.contains(OtcConstants.MAP_END_REF) && !otcToken.contains(OtcConstants.MAP_POST_ANCHOR)) {
			return true;
		}
		int firstIdx = otcToken.indexOf(OtcConstants.MAP_BEGIN_REF);
		if (firstIdx < 0) {
			firstIdx = otcToken.indexOf(OtcConstants.MAP_POST_ANCHOR);
		}
		int secondIdx = otcToken.indexOf(OtcConstants.MAP_END_REF);
		if (secondIdx < 0) {
			secondIdx = otcToken.indexOf(OtcConstants.MAP_POST_ANCHOR);
		}
		if (secondIdx < firstIdx) {
			throw new SyntaxException("",
					"Oops... Syntax error in OTC-command : " + scriptId + ". OTC-token didn't pass Syntax-Checker check "
							+ "- Map notation not well-formed in '" + otcToken + "'");

		}
		int idxkeyValueNotation = otcToken.indexOf(OtcConstants.MAP_KEY_REF);
		if (idxkeyValueNotation < 0) {
			idxkeyValueNotation = otcToken.indexOf(OtcConstants.MAP_VALUE_REF);
			if (idxkeyValueNotation < 0) {
				throw new SyntaxException("", "Oops... Syntax error in OTC-command : " + scriptId + 
						". OTC-token didn't pass Syntax-Checker check - Map notation missing <K> / <V> indicator-suffix "
						+ "ending in '" + otcToken + "'");
			}
		}
		otcCommandDto.hasMapNotation = true;
		return true;
	}

}
