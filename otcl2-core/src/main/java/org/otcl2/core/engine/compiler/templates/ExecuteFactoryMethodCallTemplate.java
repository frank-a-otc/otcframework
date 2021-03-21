package org.otcl2.core.engine.compiler.templates;

import org.otcl2.common.util.CommonUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class ExecuteFactoryMethodCallTemplate.
 */
public final class ExecuteFactoryMethodCallTemplate extends AbstractTemplate {

	/**
	 * Instantiates a new execute factory method call template.
	 */
	private ExecuteFactoryMethodCallTemplate() {}

	/**
	 * Generate code.
	 *
	 * @param factoryClzName the factory clz name
	 * @param targetClz the target clz
	 * @param sourceClz the source clz
	 * @return the string
	 */
	public static String generateCode(String factoryClzName, Class<?> targetClz, Class<?> sourceClz) {
		String targetVar = CommonUtils.initLower(targetClz.getSimpleName());
		String sourceVar = null;
		String sourceICD = null;
		if (sourceClz != null) {
			sourceVar = CommonUtils.initLower(sourceClz.getSimpleName());
			sourceICD = "sourceICD";
		}
		String executeMethodCallCode = String.format(executeFactoryMethodCallTemplate, factoryClzName, sourceVar, sourceICD, 
				targetVar);
		return executeMethodCallCode;
	}

}
