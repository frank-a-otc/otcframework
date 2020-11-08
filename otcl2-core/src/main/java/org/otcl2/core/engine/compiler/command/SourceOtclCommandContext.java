/**
* Copyright (c) otcl2.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*/
package org.otcl2.core.engine.compiler.command;

import org.otcl2.common.engine.compiler.OtclCommandContext;

public final class SourceOtclCommandContext extends OtclCommandContext {
	
	@Override
	public SourceOtclCommandContext clone() {
		SourceOtclCommandContext sourceOtclCommandContext = new SourceOtclCommandContext();
		sourceOtclCommandContext.otclChain = otclChain;
		sourceOtclCommandContext.rawOtclTokens = rawOtclTokens;
		sourceOtclCommandContext.otclTokens = otclTokens;
		sourceOtclCommandContext.otclCommandDto = otclCommandDto;
		sourceOtclCommandContext.profiledCollectionsDto = profiledCollectionsDto;		
		return sourceOtclCommandContext;
	}

}
