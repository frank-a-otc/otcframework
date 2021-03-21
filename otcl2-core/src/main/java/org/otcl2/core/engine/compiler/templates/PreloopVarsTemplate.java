package org.otcl2.core.engine.compiler.templates;

// TODO: Auto-generated Javadoc
/**
 * The Class PreloopVarsTemplate.
 */
public final class PreloopVarsTemplate extends AbstractTemplate {

	/**
	 * Instantiates a new preloop vars template.
	 */
	private PreloopVarsTemplate() {}

	/**
	 * Generate code.
	 *
	 * @return the string
	 */
	public static String generateCode() {
		String offsetIdxCode = String.format(preloopVarsTemplate);
		return offsetIdxCode;
	}
}
