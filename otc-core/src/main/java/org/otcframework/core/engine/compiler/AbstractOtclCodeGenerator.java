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
			if (execute.otcModule != null) {
				targetOCC.executeModuleOtcNamespace = execute.otcModule.otcNamespace;
			}
			targetOCC.hasExecuteConverter = scriptDto.hasExecuteConverter;
			targetOCC.executeOtcConverter = execute.otcConverter;
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
