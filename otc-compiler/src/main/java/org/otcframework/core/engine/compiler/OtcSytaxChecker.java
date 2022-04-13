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
import org.otcframework.common.dto.ScriptDto;
import org.otcframework.common.util.OtcUtils;
import org.otcframework.common.util.PropertyConverterUtil;

/**
 * The Class OtcSytaxChecker.
 */
// TODO: Auto-generated Javadoc
final class OtcSytaxChecker {

	/**
	 * Check syntax.
	 *
	 * @param script        the script
	 * @param clz           the clz
	 * @param otcCommandDto the otc command dto
	 * @param otcChain      the otc chain
	 * @param otcTokens     the otc tokens
	 * @param rawOtcToken   the raw otc token
	 * @return true, if successful
	 */
	static boolean checkSyntax(ScriptDto script, Class<?> clz, OtcCommandDto otcCommandDto, String otcChain,
			String[] otcTokens, String rawOtcToken) {
		boolean isAnchored = rawOtcToken.contains(OtcConstants.ANCHOR);
		if (isAnchored) {
			AnchorNotationProcessor.process(script.command.id, otcCommandDto, rawOtcToken, otcChain, otcTokens);
		}
		int idx = otcCommandDto.otcTokenIndex;
		if (idx == 0) {
			otcCommandDto.isFirstNode = true;
		}
		String fldName = rawOtcToken;
		int idxMapNotation = 0;
		int idxCollectionNotation = 0;
		boolean isMapNotation = false;
		boolean isCollectionNotation = false;
		if (rawOtcToken.contains(OtcConstants.MAP_BEGIN_REF) || rawOtcToken.contains(OtcConstants.MAP_PRE_ANCHOR)) {
			// then chain has a map
			// retrieve key-value notation - <K> / <V>
			idxMapNotation = rawOtcToken.indexOf(OtcConstants.CLOSE_BRACKET);
			MapNotationProcessor.process(script.command.id, otcCommandDto, rawOtcToken, otcChain, idxMapNotation);
			isMapNotation = true;
		} else if (rawOtcToken.contains(OtcConstants.OPEN_BRACKET)) {
			idxCollectionNotation = rawOtcToken.indexOf(OtcConstants.OPEN_BRACKET);
			if (idxCollectionNotation > 0) {
				// then chain has a collection
				isCollectionNotation = true;
				otcCommandDto.hasCollectionNotation = true;
				if (!rawOtcToken.contains(OtcConstants.ARR_REF) && !rawOtcToken.contains(OtcConstants.PRE_ANCHOR)
						&& !rawOtcToken.contains(OtcConstants.POST_ANCHOR)) {
					String idxCharacter = OtcUtils.retrieveIndexCharacter(rawOtcToken);
					// -- throw exception if cannot be converted to integer.
					PropertyConverterUtil.toInteger(idxCharacter);
				}
				rawOtcToken = OtcUtils.sanitizeOtc(rawOtcToken);
			}
		}
		ConcreteTypeNotationProcessor.process(script, otcCommandDto);
		if (isMapNotation) {
			idxMapNotation = rawOtcToken.indexOf(OtcConstants.OPEN_BRACKET);
			fldName = rawOtcToken.substring(0, idxMapNotation);
		} else if (isCollectionNotation) {
			fldName = rawOtcToken.substring(0, idxCollectionNotation);
		}
		otcCommandDto.fieldName = fldName;
		// --- process semantics
		OtcSemanticsChecker.checkSemantics(script, clz, otcChain, otcCommandDto, otcTokens);
		String otcToken = rawOtcToken;
		if (isCollectionNotation) {
			otcToken = fldName + OtcConstants.ARR_REF;
		} else if (isMapNotation) {
			otcToken = fldName + OtcConstants.MAP_REF;
		} else {
			otcToken = fldName;
		}
		otcTokens[idx] = otcToken;
		otcCommandDto.otcToken = otcToken;
		return true;
	}
}
