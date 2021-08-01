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
package org.otcframework.core.engine.compiler.command;

import org.otcframework.common.engine.compiler.OtcCommandContext;

// TODO: Auto-generated Javadoc
/**
 * The Class SourceOtcCommandContext.
 */
public final class SourceOtcCommandContext extends OtcCommandContext {
	
	/**
	 * Clone.
	 *
	 * @return the source otc command context
	 */
	@Override
	public SourceOtcCommandContext clone() {
		SourceOtcCommandContext sourceOtcCommandContext = new SourceOtcCommandContext();
		sourceOtcCommandContext.otcChain = otcChain;
		sourceOtcCommandContext.rawOtcTokens = rawOtcTokens;
		sourceOtcCommandContext.otcTokens = otcTokens;
		sourceOtcCommandContext.otcCommandDto = otcCommandDto;
		sourceOtcCommandContext.indexedCollectionsDto = indexedCollectionsDto;		
		sourceOtcCommandContext.collectionsCount = collectionsCount;
		sourceOtcCommandContext.currentCollectionTokenIndex = currentCollectionTokenIndex;
		return sourceOtcCommandContext;
	}

}
