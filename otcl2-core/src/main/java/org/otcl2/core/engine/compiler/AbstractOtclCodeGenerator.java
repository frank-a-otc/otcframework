package org.otcl2.core.engine.compiler;

import java.util.ArrayList;
import java.util.List;

import javax.tools.JavaFileObject;

import org.otcl2.common.OtclConstants;
import org.otcl2.common.dto.OtclChainDto;
import org.otcl2.common.dto.OtclFileDto.Execute;
import org.otcl2.common.dto.ScriptDto;
import org.otcl2.common.util.CommonUtils;
import org.otcl2.core.engine.compiler.command.JavaCodeStringObject;
import org.otcl2.core.engine.compiler.command.SourceOtclCommandContext;
import org.otcl2.core.engine.compiler.command.TargetOtclCommandContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractOtclCodeGenerator.
 */
abstract class AbstractOtclCodeGenerator  {
		
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractOtclCodeGenerator.class);

	/**
	 * Gets the index.
	 *
	 * @param targetOCC the target OCC
	 * @param currentIdx the current idx
	 * @param scriptGroupIdx the script group idx
	 * @param offsetIdx the offset idx
	 * @return the index
	 */
	protected static Integer getIndex(TargetOtclCommandContext targetOCC, Integer currentIdx, int scriptGroupIdx,
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
	
	/**
	 * Reset OCC.
	 *
	 * @param targetOCC the target OCC
	 * @param scriptDto the script dto
	 */
	protected static void resetOCC(TargetOtclCommandContext targetOCC, ScriptDto scriptDto) {
		targetOCC.scriptDto = scriptDto;
		targetOCC.scriptId = scriptDto.command.id;
		OtclChainDto targetOtclChainDto = scriptDto.targetOtclChainDto;
		String targetOtclChain = targetOtclChainDto.otclChain;
		targetOCC.otclChain = targetOtclChain;
		targetOCC.otclTokens = targetOtclChainDto.otclTokens;
		targetOCC.rawOtclTokens = targetOtclChainDto.rawOtclTokens;
		targetOCC.hasAnchorInChain = targetOtclChain.contains(OtclConstants.ANCHOR);
		boolean hasPreAnchor = targetOtclChain.contains(OtclConstants.PRE_ANCHOR) || 
				targetOtclChain.contains(OtclConstants.MAP_PRE_ANCHOR);
		targetOCC.hasPreAnchor = hasPreAnchor;
		boolean hasPostAnchor = targetOtclChain.contains(OtclConstants.POST_ANCHOR) || 
				targetOtclChain.contains(OtclConstants.MAP_POST_ANCHOR);
		targetOCC.hasPostAnchor = hasPostAnchor;
		if (scriptDto.command instanceof Execute) {
			Execute execute = (Execute) scriptDto.command;
			targetOCC.hasExecuteModule = scriptDto.hasExecuteModule;
			if (execute.otclModule != null) {
				targetOCC.executeModuleOtclNamespace = execute.otclModule.otclNamespace;
			}
			targetOCC.hasExecuteConverter = scriptDto.hasExecuteConverter;
			targetOCC.executeOtclConverter = execute.otclConverter;
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
			LOGGER.warn("Stripping Namespace/Package name in 'factoryClassName' property!. ");
			int idx = factoryClzName.lastIndexOf(".");
			String pkg = factoryClzName.substring(0, idx);
			String clzName = factoryClzName.substring(idx + 1);
			targetOCC.factoryClassDto.packageName = pkg;
			targetOCC.factoryClassDto.className = clzName;
		}
		targetOCC.currentCollectionTokenIndex = 0;
	}
	
	/**
	 * Reset OCC.
	 *
	 * @param sourceOCC the source OCC
	 * @param scriptDto the script dto
	 */
	protected static void resetOCC(SourceOtclCommandContext sourceOCC, ScriptDto scriptDto) {
		OtclChainDto sourceOtclChainDto = scriptDto.sourceOtclChainDto;
		if (sourceOtclChainDto != null) {
			sourceOCC.otclChain = sourceOtclChainDto.otclChain;
			sourceOCC.otclTokens = sourceOtclChainDto.otclTokens;
			sourceOCC.rawOtclTokens = sourceOtclChainDto.rawOtclTokens;
		}
		sourceOCC.currentCollectionTokenIndex = 0;
	}
	
	/**
	 * Adds the java string object.
	 *
	 * @param javaFileObjects the java file objects
	 * @param javaStringObject the java string object
	 * @return the list
	 */
	protected static List<JavaFileObject> addJavaStringObject(List<JavaFileObject> javaFileObjects, 
			JavaCodeStringObject javaStringObject) {
		if (javaFileObjects == null) {
			javaFileObjects = new ArrayList<>();
		}
		javaFileObjects.add(javaStringObject);
		return javaFileObjects;
	}

}
