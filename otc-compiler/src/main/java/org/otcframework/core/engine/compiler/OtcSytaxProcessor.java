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
import org.otcframework.common.dto.ScriptDto;
import org.otcframework.common.util.OtcUtils;
import org.otcframework.common.util.PropertyConverterUtil;

/**
 * The Class OtcSytaxChecker.
 */
// TODO: Auto-generated Javadoc
final class OtcSytaxProcessor {

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
	static boolean process(ScriptDto script, Class<?> clz, OtcCommandDto otcCommandDto, String otcChain,
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
		OtcSemanticsProcessor.process(script, clz, otcChain, otcCommandDto, otcTokens);
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
