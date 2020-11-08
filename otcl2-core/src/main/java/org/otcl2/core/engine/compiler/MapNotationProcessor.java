package org.otcl2.core.engine.compiler;

import org.otcl2.common.OtclConstants;
import org.otcl2.common.dto.OtclCommandDto;
import org.otcl2.core.engine.compiler.exception.SyntaxException;

final class MapNotationProcessor {

//	private static final Logger LOGGER = LoggerFactory.getLogger(MapNotationProcessor.class);

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
					"Oops... Syntax error in Script-block : " + scriptId + ". OTCL-token didn't pass Syntax-Checker check "
							+ "- Map notation not well-formed in '" + otclToken + "'");

		}
		int idxkeyValueNotation = otclToken.indexOf(OtclConstants.MAP_KEY_REF);
		if (idxkeyValueNotation < 0) {
			idxkeyValueNotation = otclToken.indexOf(OtclConstants.MAP_VALUE_REF);
			if (idxkeyValueNotation < 0) {
				throw new SyntaxException("", "Oops... Syntax error in Script-block : " + scriptId + 
						". OTCL-token didn't pass Syntax-Checker check - Map notation missing <K> / <V> indicator-suffix "
						+ "ending in '" + otclToken + "'");
			}
		}
		otclCommandDto.hasMapNotation = true;
//		logs.add("Map-notations processing : Okay for " + script.id);
		return true;
	}

}
