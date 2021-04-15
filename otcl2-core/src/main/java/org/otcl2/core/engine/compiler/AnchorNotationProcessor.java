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
import org.otcl2.common.engine.compiler.OtclCommandContext;
import org.otcl2.core.engine.compiler.exception.SyntaxException;

// TODO: Auto-generated Javadoc
/**
 * The Class AnchorNotationProcessor.
 */
final class AnchorNotationProcessor {

//	private static final Logger LOGGER = LoggerFactory.getLogger(MapNotationProcessor.class);

	/**
 * Process.
 *
 * @param scriptId the script id
 * @param otclCommandDto the otcl command dto
 * @param otclToken the otcl token
 * @param otclChain the otcl chain
 * @param otclTokens the otcl tokens
 * @return true, if successful
 */
public static boolean process(String scriptId, OtclCommandDto otclCommandDto, String otclToken,
			String otclChain, String[] otclTokens) {
		int idxAnchor = otclToken.indexOf(OtclConstants.ANCHOR);
		boolean isAnchored = false;
		if (idxAnchor > 0) {
			if (!otclToken.contains(OtclConstants.PRE_ANCHOR) && !otclToken.contains(OtclConstants.POST_ANCHOR) &&
					!otclToken.contains(OtclConstants.MAP_PRE_ANCHOR) && !otclToken.contains(OtclConstants.MAP_POST_ANCHOR)) {
				throw new SyntaxException("",
						"Oops... Syntax error in Script-block : " + scriptId + ". OTCL-token didn't pass Syntax-Checker "
								+ "- misplaced anchors found outside Collection/Map notation '[' and ']' boundaries.");
			}
			OtclCommandContext otclCommandContext = new OtclCommandContext();
			otclCommandContext.otclCommandDto = otclCommandDto;
			otclCommandContext.rawOtclTokens = otclTokens;
			boolean hasAnchoredDescendants = otclCommandContext.hasAnchoredDescendant();
			if (hasAnchoredDescendants) {
				throw new SyntaxException("", "Oops... Syntax error in Script-block : " + scriptId + 
						". OTCL-token didn't pass Syntax-Checker! " + "Atmost only one anchor only can be defined.");
			}
			isAnchored = true;
		}
//		logs.add("Elastic-tree notations processing : Okay for " + script.id);
		return isAnchored;
	}

}
