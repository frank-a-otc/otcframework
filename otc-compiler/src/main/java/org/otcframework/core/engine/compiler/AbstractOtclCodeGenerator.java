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
package org.otcframework.core.engine.compiler;

import java.util.ArrayList;
import java.util.List;

import javax.tools.JavaFileObject;

import org.otcframework.common.OtcConstants;
import org.otcframework.common.dto.OtcChainDto;
import org.otcframework.common.dto.ScriptDto;
import org.otcframework.common.dto.otc.OtcFileDto.Execute;
import org.otcframework.common.util.CommonUtils;
import org.otcframework.core.engine.compiler.command.JavaCodeStringObject;
import org.otcframework.core.engine.compiler.command.SourceOtcCommandContext;
import org.otcframework.core.engine.compiler.command.TargetOtcCommandContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
abstract class AbstractOtcCodeGenerator {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractOtcCodeGenerator.class);

	protected static Integer getIndex(TargetOtcCommandContext targetOCC, Integer currentIdx, int scriptGroupIdx,
			Integer offsetIdx) {
		Integer idx = null;
		boolean isPreAnchored = targetOCC.isPreAnchored();
		boolean isPostAnchored = targetOCC.isPostAnchored();
		if (isPreAnchored) {
			idx = offsetIdx;
		} else if (isPostAnchored) {
			idx = scriptGroupIdx;
		} else if (targetOCC.hasDescendantCollectionOrMap()) {
			idx = 0;
		} else {
			if (targetOCC.hasPreAnchor) {
				idx = 0;
			} else if (targetOCC.hasPostAnchor) {
				idx = currentIdx;
			} else {
				idx = offsetIdx;
			}
		}
		return idx;
	}

	protected static void resetOCC(TargetOtcCommandContext targetOCC, ScriptDto scriptDto) {
		targetOCC.scriptDto = scriptDto;
		String commandId = scriptDto.command.id;
		targetOCC.commandId = commandId;
		OtcChainDto targetOtcChainDto = scriptDto.targetOtcChainDto;
		String targetOtcChain = targetOtcChainDto.otcChain;
		targetOCC.otcChain = targetOtcChain;
		targetOCC.otcTokens = targetOtcChainDto.otcTokens;
		targetOCC.rawOtcTokens = targetOtcChainDto.rawOtcTokens;
		targetOCC.hasAnchorInChain = targetOtcChain.contains(OtcConstants.ANCHOR);
		boolean hasPreAnchor = targetOtcChain.contains(OtcConstants.PRE_ANCHOR)
				|| targetOtcChain.contains(OtcConstants.MAP_PRE_ANCHOR);
		targetOCC.hasPreAnchor = hasPreAnchor;
		boolean hasPostAnchor = targetOtcChain.contains(OtcConstants.POST_ANCHOR)
				|| targetOtcChain.contains(OtcConstants.MAP_POST_ANCHOR);
		targetOCC.hasPostAnchor = hasPostAnchor;
		if (scriptDto.command instanceof Execute) {
			Execute execute = (Execute) scriptDto.command;
			targetOCC.hasExecuteModule = scriptDto.hasExecuteModule;
			if (execute.module != null) {
				targetOCC.executeModuleOtcNamespace = execute.module.namespace;
			}
			targetOCC.hasExecuteConverter = scriptDto.hasExecuteConverter;
			targetOCC.executeOtcConverter = execute.converter;
		} else {
			targetOCC.hasExecuteConverter = false;
			targetOCC.hasExecuteModule = false;
		}
		String pkgName = targetOCC.factoryClassDto.packageName;
		String factoryClzName = scriptDto.command.factoryClassName;
		targetOCC.factoryClassDto.fullyQualifiedClassName = factoryClzName;
		if (!CommonUtils.isEmpty(pkgName)) {
			if (!factoryClzName.startsWith(pkgName)) {
				targetOCC.factoryClassDto.fullyQualifiedClassName = pkgName + "." + factoryClzName;
			}
		}
		targetOCC.factoryClassDto.className = factoryClzName;
		if (factoryClzName.contains(".")) {
			LOGGER.warn(
					"Stripping illegal presence of Namespace/Package name in 'factoryClassName' property in command-id : {}",
					commandId);
			int idx = factoryClzName.lastIndexOf(".");
			String pkg = factoryClzName.substring(0, idx);
			String clzName = factoryClzName.substring(idx + 1);
			targetOCC.factoryClassDto.packageName = pkg;
			targetOCC.factoryClassDto.className = clzName;
		}
		targetOCC.currentCollectionTokenIndex = 0;
	}

	protected static void resetOCC(SourceOtcCommandContext sourceOCC, ScriptDto scriptDto) {
		OtcChainDto sourceOtcChainDto = scriptDto.sourceOtcChainDto;
		if (sourceOtcChainDto != null) {
			sourceOCC.otcChain = sourceOtcChainDto.otcChain;
			sourceOCC.otcTokens = sourceOtcChainDto.otcTokens;
			sourceOCC.rawOtcTokens = sourceOtcChainDto.rawOtcTokens;
		}
		sourceOCC.currentCollectionTokenIndex = 0;
	}

	protected static List<JavaFileObject> addJavaStringObject(List<JavaFileObject> javaFileObjects,
			JavaCodeStringObject javaStringObject) {
		if (javaFileObjects == null) {
			javaFileObjects = new ArrayList<>();
		}
		javaFileObjects.add(javaStringObject);
		return javaFileObjects;
	}
}
