/**
* Copyright (c) otcl2.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*/
package org.otcl2.core.engine.compiler.command;

import org.otcl2.common.OtclConstants.ALGORITHM_ID;
import org.otcl2.common.dto.ClassDto;
import org.otcl2.common.engine.compiler.OtclCommandContext;

public final class TargetOtclCommandContext extends OtclCommandContext {

	public String helper;
	public ClassDto mainClassDto;
	public ClassDto factoryClassDto;
	public boolean hasAnchor;
	public boolean hasPreAnchor;
	public boolean hasPostAnchor;
	public String executeModuleOtclNamespace;
	public boolean hasExecuteModule;
	public String executeOtclConverter;
	public boolean hasExecuteConverter;
	public ALGORITHM_ID algorithmId;
	public String idxVar;
	public int loopCounter;

	public TargetOtclCommandContext() {
		mainClassDto = new ClassDto();
		if (factoryClassDto == null) {
			factoryClassDto = new ClassDto();
		}
	}

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
		targetOtclCommandContext.hasAnchor = hasAnchor;
		targetOtclCommandContext.hasPreAnchor = hasPreAnchor;
		targetOtclCommandContext.hasPostAnchor = hasPostAnchor;
		targetOtclCommandContext.hasExecuteModule = hasExecuteModule;
		targetOtclCommandContext.hasExecuteConverter = hasExecuteConverter;
		targetOtclCommandContext.executeModuleOtclNamespace = executeModuleOtclNamespace;
		targetOtclCommandContext.executeOtclConverter = executeOtclConverter;
		targetOtclCommandContext.algorithmId = algorithmId;
		return targetOtclCommandContext;
	}

	public void appendCode(String code) {
		factoryClassDto.codeBuilder.append(code);
	}

	public void appendCode(StringBuilder code) {
		factoryClassDto.codeBuilder.append(code);
	}
}
