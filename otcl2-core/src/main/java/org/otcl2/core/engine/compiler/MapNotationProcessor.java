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

import org.otcl2.common.OtclConstants;
import org.otcl2.common.dto.OtclCommandDto;
import org.otcl2.core.engine.compiler.exception.SyntaxException;

// TODO: Auto-generated Javadoc
/**
 * The Class MapNotationProcessor.
 */
final class MapNotationProcessor {

	/**
	 * Process.
	 *
	 * @param scriptId the script id
	 * @param otclCommandDto the otcl command dto
	 * @param otclToken the otcl token
	 * @param otclChain the otcl chain
	 * @param idxMapNotation the idx map notation
	 * @return true, if successful
	 */
	public static boolean process(String scriptId, OtclCommandDto otclCommandDto, String otclToken,
			String otclChain, int idxMapNotation) {
		if (!otclToken.contains(OtclConstants.MAP_BEGIN_REF) && !otclToken.contains(OtclConstants.MAP_PRE_ANCHOR) &&
				!otclToken.contains(OtclConstants.MAP_END_REF) && !otclToken.contains(OtclConstants.MAP_POST_ANCHOR)) {
			return true;
		}
		int firstIdx = otclToken.indexOf(OtclConstants.MAP_BEGIN_REF);
		if (firstIdx < 0) {
			firstIdx = otclToken.indexOf(OtclConstants.MAP_POST_ANCHOR);
		}
		int secondIdx = otclToken.indexOf(OtclConstants.MAP_END_REF);
		if (secondIdx < 0) {
			secondIdx = otclToken.indexOf(OtclConstants.MAP_POST_ANCHOR);
		}
		if (secondIdx < firstIdx) {
			throw new SyntaxException("",
					"Oops... Syntax error in OTCL-command : " + scriptId + ". OTCL-token didn't pass Syntax-Checker check "
							+ "- Map notation not well-formed in '" + otclToken + "'");

		}
		int idxkeyValueNotation = otclToken.indexOf(OtclConstants.MAP_KEY_REF);
		if (idxkeyValueNotation < 0) {
			idxkeyValueNotation = otclToken.indexOf(OtclConstants.MAP_VALUE_REF);
			if (idxkeyValueNotation < 0) {
				throw new SyntaxException("", "Oops... Syntax error in OTCL-command : " + scriptId + 
						". OTCL-token didn't pass Syntax-Checker check - Map notation missing <K> / <V> indicator-suffix "
						+ "ending in '" + otclToken + "'");
			}
		}
		otclCommandDto.hasMapNotation = true;
		return true;
	}

}
