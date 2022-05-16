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
package org.otcframework.core.engine.compiler;

import org.otcframework.common.OtcConstants;
import org.otcframework.common.dto.OtcCommandDto;
import org.otcframework.core.engine.compiler.exception.SyntaxException;

/**
 * The Class MapNotationProcessor.
 */
// TODO: Auto-generated Javadoc
final class MapNotationProcessor {

	/**
	 * Process.
	 *
	 * @param scriptId       the script id
	 * @param otcCommandDto  the otc command dto
	 * @param otcToken       the otc token
	 * @param otcChain       the otc chain
	 * @param idxMapNotation the idx map notation
	 * @return true, if successful
	 */
	public static boolean process(String scriptId, OtcCommandDto otcCommandDto, String otcToken, String otcChain,
			int idxMapNotation) {
		if (!otcToken.contains(OtcConstants.MAP_BEGIN_REF) && !otcToken.contains(OtcConstants.MAP_PRE_ANCHOR)
				&& !otcToken.contains(OtcConstants.MAP_END_REF) && !otcToken.contains(OtcConstants.MAP_POST_ANCHOR)) {
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
					"Oops... Syntax error in OTC-command : " + scriptId
							+ ". OTC-token didn't pass Syntax-Checker check " + "- Map notation not well-formed in '"
							+ otcToken + "'");
		}
		int idxkeyValueNotation = otcToken.indexOf(OtcConstants.MAP_KEY_REF);
		if (idxkeyValueNotation < 0) {
			idxkeyValueNotation = otcToken.indexOf(OtcConstants.MAP_VALUE_REF);
			if (idxkeyValueNotation < 0) {
				throw new SyntaxException("", "Oops... Syntax error in OTC-command : " + scriptId
						+ ". OTC-token didn't pass Syntax-Checker check - Map notation missing <K> / <V> indicator-suffix "
						+ "ending in '" + otcToken + "'");
			}
		}
		otcCommandDto.hasMapNotation = true;
		return true;
	}
}
