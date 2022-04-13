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
import org.otcframework.common.engine.compiler.OtcCommandContext;
import org.otcframework.core.engine.compiler.exception.SyntaxException;

/**
 * The Class AnchorNotationProcessor.
 */
// TODO: Auto-generated Javadoc
final class AnchorNotationProcessor {

	/**
	 * Process.
	 *
	 * @param scriptId      the script id
	 * @param otcCommandDto the otc command dto
	 * @param otcToken      the otc token
	 * @param otcChain      the otc chain
	 * @param otcTokens     the otc tokens
	 * @return true, if successful
	 */
	public static boolean process(String scriptId, OtcCommandDto otcCommandDto, String otcToken, String otcChain,
			String[] otcTokens) {
		int idxAnchor = otcToken.indexOf(OtcConstants.ANCHOR);
		boolean isAnchored = false;
		if (idxAnchor > 0) {
			if (!otcToken.contains(OtcConstants.PRE_ANCHOR) && !otcToken.contains(OtcConstants.POST_ANCHOR)
					&& !otcToken.contains(OtcConstants.MAP_PRE_ANCHOR)
					&& !otcToken.contains(OtcConstants.MAP_POST_ANCHOR)) {
				throw new SyntaxException("",
						"Oops... Syntax error in OTC-command : " + scriptId + ". OTC-token didn't pass Syntax-Checker "
								+ "- misplaced anchors found outside Collection/Map notation '[' and ']' boundaries.");
			}
			OtcCommandContext otcCommandContext = new OtcCommandContext();
			otcCommandContext.otcCommandDto = otcCommandDto;
			otcCommandContext.rawOtcTokens = otcTokens;
			boolean hasAnchoredDescendants = otcCommandContext.hasAnchoredDescendant();
			if (hasAnchoredDescendants) {
				throw new SyntaxException("", "Oops... Syntax error in OTC-command : " + scriptId
						+ ". OTC-token didn't pass Syntax-Checker! " + "Atmost only one anchor only can be defined.");
			}
			isAnchored = true;
		}
		return isAnchored;
	}
}
