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
package org.otcframework.core.engine.compiler.command;

import org.otcframework.common.engine.compiler.OtcCommandContext;

/**
 * The Class SourceOtcCommandContext.
 */
// TODO: Auto-generated Javadoc
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
