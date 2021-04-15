/**
* Copyright (c) otclfoundation.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*
* This file is part of the OTCL framework.
* 
*  The OTCL framework is free software: you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  the Free Software Foundation, version 3 of the License.
*
*  The OTCL framework is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU General Public License for more details.
*
*  A copy of the GNU General Public License is made available as 'License.md' file, 
*  along with OTCL framework project.  If not, see <https://www.gnu.org/licenses/>.
*
*/
package org.otcl2.core.engine.compiler.command;

import org.otcl2.common.OtclConstants.ALGORITHM_ID;
import org.otcl2.common.dto.ClassDto;
import org.otcl2.common.engine.compiler.OtclCommandContext;

// TODO: Auto-generated Javadoc
/**
 * The Class TargetOtclCommandContext.
 */
public final class TargetOtclCommandContext extends OtclCommandContext {

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
	
	/** The execute module otcl namespace. */
	public String executeModuleOtclNamespace;
	
	/** The has execute module. */
	public boolean hasExecuteModule;
	
	/** The execute otcl converter. */
	public String executeOtclConverter;
	
	/** The has execute converter. */
	public boolean hasExecuteConverter;
	
	/** The algorithm id. */
	public ALGORITHM_ID algorithmId;
	
	/** The idx var. */
//	public String idxVar;
	
	/** The loop counter. */
	public int loopsCounter;

	/** The anchor index. */
	public int anchorIndex;

	/**
	 * Instantiates a new target otcl command context.
	 */
	public TargetOtclCommandContext() {
		mainClassDto = new ClassDto();
		if (factoryClassDto == null) {
			factoryClassDto = new ClassDto();
		}
	}

	/**
	 * Clone.
	 *
	 * @return the target otcl command context
	 */
	@Override
	public TargetOtclCommandContext clone() {
		TargetOtclCommandContext targetOtclCommandContext = new TargetOtclCommandContext();
		targetOtclCommandContext.scriptId = scriptId;
		targetOtclCommandContext.scriptDto = scriptDto;
		targetOtclCommandContext.helper = helper;
		targetOtclCommandContext.mainClassDto = mainClassDto;
		targetOtclCommandContext.factoryClassDto = factoryClassDto;
		targetOtclCommandContext.otclChain = otclChain;
		targetOtclCommandContext.rawOtclTokens = rawOtclTokens;
		targetOtclCommandContext.otclTokens = otclTokens;
		targetOtclCommandContext.otclCommandDto = otclCommandDto;
		targetOtclCommandContext.profiledCollectionsDto = profiledCollectionsDto;
		targetOtclCommandContext.hasAnchorInChain = hasAnchorInChain;
		targetOtclCommandContext.hasPreAnchor = hasPreAnchor;
		targetOtclCommandContext.hasPostAnchor = hasPostAnchor;
		targetOtclCommandContext.hasExecuteModule = hasExecuteModule;
		targetOtclCommandContext.hasExecuteConverter = hasExecuteConverter;
		targetOtclCommandContext.executeModuleOtclNamespace = executeModuleOtclNamespace;
		targetOtclCommandContext.executeOtclConverter = executeOtclConverter;
		targetOtclCommandContext.algorithmId = algorithmId;
		targetOtclCommandContext.collectionsCount = collectionsCount;
		targetOtclCommandContext.currentCollectionTokenIndex = currentCollectionTokenIndex;
		return targetOtclCommandContext;
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
