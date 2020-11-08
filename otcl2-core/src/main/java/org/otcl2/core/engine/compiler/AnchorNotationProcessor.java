package org.otcl2.core.engine.compiler;

import org.otcl2.common.OtclConstants;
import org.otcl2.common.dto.OtclCommandDto;
import org.otcl2.common.engine.compiler.OtclCommandContext;
import org.otcl2.core.engine.compiler.exception.SyntaxException;

final class AnchorNotationProcessor {

//	private static final Logger LOGGER = LoggerFactory.getLogger(MapNotationProcessor.class);

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
