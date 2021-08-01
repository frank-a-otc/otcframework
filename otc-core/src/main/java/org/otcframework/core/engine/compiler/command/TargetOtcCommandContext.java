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

import org.otcframework.common.OtcConstants.ALGORITHM_ID;
import org.otcframework.common.dto.ClassDto;
import org.otcframework.common.engine.compiler.OtcCommandContext;

// TODO: Auto-generated Javadoc
/**
 * The Class TargetOtcCommandContext.
 */
public final class TargetOtcCommandContext extends OtcCommandContext {

	/** The helper. */
	public String helper;
		
	/** The main class dto. */
	public ClassDto mainClassDto;
	
	/** The factory class dto. */
	public ClassDto factoryClassDto;
	
	/** The has anchor. */
	public boolean hasAnchorInChain;
	
	/** The has pre anchor. */
	public boolean hasPreAnchor;
	
	/** The has post anchor. */
	public boolean hasPostAnchor;
	
	/** The execute module otc namespace. */
	public String executeModuleOtcNamespace;
	
	/** The has execute module. */
	public boolean hasExecuteModule;
	
	/** The execute otc converter. */
	public String executeOtcConverter;
	
	/** The has execute converter. */
	public boolean hasExecuteConverter;
	
	/** The algorithm id. */
	public ALGORITHM_ID algorithmId;

	/** The loop counter. */
	public int loopsCounter;

	/** The anchor index. */
	public int anchorIndex;

	/**
	 * Instantiates a new target otc command context.
	 */
	public TargetOtcCommandContext() {
		mainClassDto = new ClassDto();
		if (factoryClassDto == null) {
			factoryClassDto = new ClassDto();
		}
	}

	/**
	 * Clone.
	 *
	 * @return the target otc command context
	 */
	@Override
	public TargetOtcCommandContext clone() {
		TargetOtcCommandContext targetOtcCommandContext = new TargetOtcCommandContext();
		targetOtcCommandContext.commandId = commandId;
		targetOtcCommandContext.scriptDto = scriptDto;
		targetOtcCommandContext.helper = helper;
		targetOtcCommandContext.mainClassDto = mainClassDto;
		targetOtcCommandContext.factoryClassDto = factoryClassDto;
		targetOtcCommandContext.otcChain = otcChain;
		targetOtcCommandContext.rawOtcTokens = rawOtcTokens;
		targetOtcCommandContext.otcTokens = otcTokens;
		targetOtcCommandContext.otcCommandDto = otcCommandDto;
		targetOtcCommandContext.indexedCollectionsDto = indexedCollectionsDto;
		targetOtcCommandContext.hasAnchorInChain = hasAnchorInChain;
		targetOtcCommandContext.hasPreAnchor = hasPreAnchor;
		targetOtcCommandContext.hasPostAnchor = hasPostAnchor;
		targetOtcCommandContext.hasExecuteModule = hasExecuteModule;
		targetOtcCommandContext.hasExecuteConverter = hasExecuteConverter;
		targetOtcCommandContext.executeModuleOtcNamespace = executeModuleOtcNamespace;
		targetOtcCommandContext.executeOtcConverter = executeOtcConverter;
		targetOtcCommandContext.algorithmId = algorithmId;
		targetOtcCommandContext.collectionsCount = collectionsCount;
		targetOtcCommandContext.currentCollectionTokenIndex = currentCollectionTokenIndex;
		return targetOtcCommandContext;
	}

	/**
	 * Append code.
	 *
	 * @param code the code
	 */
	public void appendCode(String code) {
		factoryClassDto.codeBuilder.append(code);
	}

	/**
	 * Append code.
	 *
	 * @param code the code
	 */
	public void appendCode(StringBuilder code) {
		factoryClassDto.codeBuilder.append(code);
	}
}
