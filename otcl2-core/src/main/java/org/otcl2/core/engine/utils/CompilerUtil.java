package org.otcl2.core.engine.utils;

import org.otcl2.common.OtclConstants;
import org.otcl2.common.util.CommonUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class CompilerUtil.
 */
public final class CompilerUtil {

	/**
	 * Builds the java class name.
	 *
	 * @param otclNamespace the otcl namespace
	 * @param otclFileName the otcl file name
	 * @param otclChain the otcl chain
	 * @return the string
	 */
	public static String buildJavaClassName(String otclNamespace, String otclFileName, String otclChain) {
		if (otclFileName.endsWith(".otcl")) {
			otclFileName = otclFileName.replace(otclFileName.substring(otclFileName.lastIndexOf(".otcl")), "");
		}
		String factoryClzName = sanitizeJavaIdentifier(otclFileName);
		if (!CommonUtils.isEmpty(otclNamespace)) {
			factoryClzName = otclNamespace + "." + factoryClzName;
		}
		if (!CommonUtils.isEmpty(otclChain)) {
			String newOtclChain = null;
			if (otclChain.contains(OtclConstants.MAP_KEY_REF) || otclChain.contains(OtclConstants.MAP_VALUE_REF)) {
				if (otclChain.contains(OtclConstants.MAP_KEY_REF)) {
					newOtclChain = otclChain.replace(OtclConstants.MAP_KEY_REF, "Key");
				} else {
					newOtclChain = otclChain.replace(OtclConstants.MAP_VALUE_REF, "Value");
				}
			} else {
				newOtclChain = otclChain;
			}
			newOtclChain = CompilerUtil.sanitizeJavaIdentifier(newOtclChain);
			factoryClzName = factoryClzName + "__" + newOtclChain;
		}
		return factoryClzName;
	}

	/**
	 * Builds the java class name.
	 *
	 * @param otclNamespace the otcl namespace
	 * @param otclFileName the otcl file name
	 * @return the string
	 */
	public static String buildJavaClassName(String otclNamespace, String otclFileName) {
		return buildJavaClassName(otclNamespace, otclFileName, null);
	}

	/**
	 * Sanitize java identifier.
	 *
	 * @param identifier the identifier
	 * @return the string
	 */
	public static String sanitizeJavaIdentifier(String identifier) {
		if (identifier.contains(OtclConstants.ANCHOR)) {
			identifier = identifier.replace(OtclConstants.ANCHOR, "");
		}
		if (identifier.contains(OtclConstants.OPEN_BRACKET)) {
			int idx = identifier.indexOf(OtclConstants.OPEN_BRACKET);
			while (idx > 0) {
				int idxEnd = identifier.indexOf(OtclConstants.CLOSE_BRACKET);
				identifier = identifier.replace(identifier.substring(idx, idxEnd + 1), "");
				idx = identifier.indexOf(OtclConstants.OPEN_BRACKET);
			}
			if (identifier.contains(OtclConstants.MAP_KEY_REF) || identifier.contains(OtclConstants.MAP_VALUE_REF)) {
				identifier = identifier.replace(OtclConstants.MAP_KEY_REF, "").replace(OtclConstants.MAP_VALUE_REF, "");
			}
		}
		char[] chars = identifier.toCharArray();
		chars[0] = Character.toUpperCase(chars[0]);
		int idx = identifier.indexOf(".");
		while (idx > 0) {
			chars[idx + 1] = Character.toUpperCase(chars[idx + 1]);
			idx = identifier.indexOf(".", idx + 1);
		}
		idx = identifier.indexOf("_");
		if (idx > 0) {
			chars[idx + 1] = Character.toUpperCase(chars[idx + 1]);
		}
		identifier = new String(chars).replace(".", "");
		return identifier;
	}
}
