package org.otcl2.core.engine.compiler.templates;

// TODO: Auto-generated Javadoc
/**
 * The Class MethodEndTemplate.
 */
public final class MethodEndTemplate extends AbstractTemplate {

	/**
	 * Instantiates a new method end template.
	 */
	private MethodEndTemplate() {}

	/**
	 * Generate code.
	 *
	 * @param varName the var name
	 * @return the string
	 */
	public static String generateCode(String varName) {
		String endExecuteMethod = String.format(methodEndTemplate, varName);
		return endExecuteMethod;
	}
}
