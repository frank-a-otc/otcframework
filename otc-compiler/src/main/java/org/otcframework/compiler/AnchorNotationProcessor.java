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
import org.otcframework.common.compiler.OtcCommandContext;
import org.otcframework.common.dto.OtcCommandDto;
import org.otcframework.compiler.exception.SyntaxException;

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
