package org.otcl2.core.engine.compiler.templates;

public final class PreloopVarsTemplate extends AbstractTemplate {

	private PreloopVarsTemplate() {}

	public static String generateCode() {
		String offsetIdxCode = String.format(preloopVarsTemplate);
		return offsetIdxCode;
	}
}
