package org.otcl2.core.engine.compiler.templates;

import org.otcl2.common.util.CommonUtils;

public final class ExecuteFactoryMethodCallTemplate extends AbstractTemplate {

	private ExecuteFactoryMethodCallTemplate() {}

	public static String generateCode(String factoryClzName, Class<?> targetClz, Class<?> sourceClz) {
		String targetVar = CommonUtils.initLower(targetClz.getSimpleName());
		String sourceVar = null;
		if (sourceClz != null) {
			sourceVar = CommonUtils.initLower(sourceClz.getSimpleName());
		}
		String executeMethodCallCode = String.format(executeFactoryMethodCallTemplate, factoryClzName, sourceVar, targetVar);
		return executeMethodCallCode;
	}

}
