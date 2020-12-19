/**
* Copyright (c) otclfoundation.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*/
package org.otcl2.core.engine.compiler.command;

import org.otcl2.common.engine.compiler.OtclCommandContext;

// TODO: Auto-generated Javadoc
/**
 * The Class SourceOtclCommandContext.
 */
public final class SourceOtclCommandContext extends OtclCommandContext {
	
	/**
	 * Clone.
	 *
	 * @return the source otcl command context
	 */
	@Override
	public SourceOtclCommandContext clone() {
		SourceOtclCommandContext sourceOtclCommandContext = new SourceOtclCommandContext();
		sourceOtclCommandContext.otclChain = otclChain;
		sourceOtclCommandContext.rawOtclTokens = rawOtclTokens;
		sourceOtclCommandContext.otclTokens = otclTokens;
		sourceOtclCommandContext.otclCommandDto = otclCommandDto;
		sourceOtclCommandContext.profiledCollectionsDto = profiledCollectionsDto;		
		sourceOtclCommandContext.collectionsCount = collectionsCount;
		sourceOtclCommandContext.currentCollectionTokenIndex = currentCollectionTokenIndex;
		return sourceOtclCommandContext;
	}

}
