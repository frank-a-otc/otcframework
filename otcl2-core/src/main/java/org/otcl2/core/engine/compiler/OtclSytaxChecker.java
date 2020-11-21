/**
* Copyright (c) otclfoundation.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*/
package org.otcl2.core.engine.compiler;

import org.otcl2.common.OtclConstants;
import org.otcl2.common.dto.OtclCommandDto;
import org.otcl2.common.dto.ScriptDto;
import org.otcl2.common.util.OtclUtils;
import org.otcl2.common.util.PropertyConverterUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class OtclSytaxChecker.
 */
final class OtclSytaxChecker {

//	private static final Logger LOGGER = LoggerFactory.getLogger(OtclSytaxChecker.class);

	/**
 * Check syntax.
 *
 * @param script the script
 * @param clz the clz
 * @param factoryHelper the factory helper
 * @param otclCommandDto the otcl command dto
 * @param otclChain the otcl chain
 * @param otclTokens the otcl tokens
 * @param rawOtclToken the raw otcl token
 * @return true, if successful
 */
static boolean checkSyntax(ScriptDto script, Class<?> clz, Class<?> factoryHelper, 
			OtclCommandDto otclCommandDto, String otclChain, String[] otclTokens, String rawOtclToken) {
		boolean isAnchored = rawOtclToken.contains(OtclConstants.ANCHOR);
		if (isAnchored) {
			AnchorNotationProcessor.process(script.command.id, otclCommandDto, rawOtclToken, otclChain, otclTokens);
		}
		int idx = otclCommandDto.otclTokenIndex;
		if (idx == 0) {
			otclCommandDto.isRootNode = true;
		}
		String fldName = rawOtclToken;
		int idxMapNotation = 0;
		int idxCollectionNotation = 0;
		boolean isMapNotation = false;
		boolean isCollectionNotation = false;
		if (rawOtclToken.contains(OtclConstants.MAP_BEGIN_REF) || rawOtclToken.contains(OtclConstants.MAP_PRE_ANCHOR)) {
			// then chain has a map
			// retrieve key-value notation - <K> / <V>
			idxMapNotation = rawOtclToken.indexOf(OtclConstants.CLOSE_BRACKET);
			MapNotationProcessor.process(script.command.id, otclCommandDto, rawOtclToken, otclChain, idxMapNotation);
			isMapNotation = true;
		} else if (rawOtclToken.contains(OtclConstants.OPEN_BRACKET)) {
			idxCollectionNotation = rawOtclToken.indexOf(OtclConstants.OPEN_BRACKET);
			if (idxCollectionNotation > 0) {
				// then chain has a collection
				isCollectionNotation = true;
				otclCommandDto.hasCollectionNotation = true;
				if (!rawOtclToken.contains(OtclConstants.ARR_REF)
						&& !rawOtclToken.contains(OtclConstants.APPEND_ARR_REF)
						&& !rawOtclToken.contains(OtclConstants.PRE_ANCHOR)
						&& !rawOtclToken.contains(OtclConstants.POST_ANCHOR)) {
					String idxCharacter = OtclUtils.retrieveIndexCharacter(rawOtclToken);
					// -- throw exception if cannot be converted to integer.
					PropertyConverterUtil.toInteger(idxCharacter);
				}
				rawOtclToken = OtclUtils.sanitizeOtcl(rawOtclToken);
			}
		}
		ConcreteTypeNotationProcessor.process(script, otclCommandDto, rawOtclToken, otclChain, isMapNotation,
				idxCollectionNotation);
		if (isMapNotation) {
			idxMapNotation = rawOtclToken.indexOf(OtclConstants.OPEN_BRACKET);
			fldName = rawOtclToken.substring(0, idxMapNotation);
		} else if (isCollectionNotation) {
			fldName = rawOtclToken.substring(0, idxCollectionNotation);
		}
		otclCommandDto.fieldName = fldName;
		GetterSetterProcessor.process(script, otclCommandDto);
		OtclSemanticsChecker.checkSemantics(factoryHelper, script, clz, otclChain, otclCommandDto, otclTokens);
		String otclToken = rawOtclToken;
		if (isCollectionNotation) {
			otclToken = fldName + OtclConstants.ARR_REF;
		} else if (isMapNotation) {
			otclToken = fldName + OtclConstants.MAP_REF;
		} else {
			otclToken = fldName;
		}
		otclTokens[idx] = otclToken;
		otclCommandDto.otclToken = otclToken;
//		logs.add("Syntax processing : Okay for " + script.id);
		return true;
	}

}
