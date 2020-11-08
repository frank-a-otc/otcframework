package org.otcl2.core.engine.compiler.templates;

public final class MethodEndTemplate extends AbstractTemplate {

	private MethodEndTemplate() {}

	public static String generateCode(String varName) {
		String endExecuteMethod = String.format(methodEndTemplate, varName);
		return endExecuteMethod;
	}
}
