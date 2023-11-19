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
package org.otcframework.compiler.utils;

import org.otcframework.common.OtcConstants;
import org.otcframework.common.util.CommonUtils;

/**
 * The Class CompilerUtil.
 */
public final class CompilerUtil {

	private CompilerUtil() {}
	/**
	 * Builds the java class name.
	 *
	 * @param otcNamespace the otc namespace
	 * @param otcFileName  the otc file name
	 * @param otcChain     the otc chain
	 * @return the string
	 */
	public static String buildJavaClassName(String otcNamespace, String otcFileName, String otcChain) {
		String factoryClzName = sanitizeJavaIdentifier(otcFileName);
		if (!CommonUtils.isTrimmedAndEmpty(otcNamespace)) {
			factoryClzName = otcNamespace + "." + factoryClzName;
		}
		if (!CommonUtils.isTrimmedAndEmpty(otcChain)) {
			String newOtcChain = null;
			if (otcChain.contains(OtcConstants.MAP_KEY_REF) || otcChain.contains(OtcConstants.MAP_VALUE_REF)) {
				if (otcChain.contains(OtcConstants.MAP_KEY_REF)) {
					newOtcChain = otcChain.replace(OtcConstants.MAP_KEY_REF, "Key");
				} else {
					newOtcChain = otcChain.replace(OtcConstants.MAP_VALUE_REF, "Value");
				}
			} else {
				newOtcChain = otcChain;
			}
			newOtcChain = CompilerUtil.sanitizeJavaIdentifier(newOtcChain);
			factoryClzName = factoryClzName + "__" + newOtcChain;
		}
		return factoryClzName;
	}

	/**
	 * Builds the java class name.
	 *
	 * @param otcNamespace the otc namespace
	 * @param otcFileName  the otc file name
	 * @return the string
	 */
	public static String buildJavaClassName(String otcNamespace, String otcFileName) {
		return buildJavaClassName(otcNamespace, otcFileName, null);
	}

	/**
	 * Sanitize java identifier.
	 *
	 * @param identifier the identifier
	 * @return the string
	 */
	public static String sanitizeJavaIdentifier(String identifier) {
		if (identifier.contains(OtcConstants.ANCHOR)) {
			identifier = identifier.replace(OtcConstants.ANCHOR, "");
		}
		if (identifier.contains(OtcConstants.OPEN_BRACKET)) {
			int idx = identifier.indexOf(OtcConstants.OPEN_BRACKET);
			while (idx > 0) {
				int idxEnd = identifier.indexOf(OtcConstants.CLOSE_BRACKET);
				identifier = identifier.replace(identifier.substring(idx, idxEnd + 1), "");
				idx = identifier.indexOf(OtcConstants.OPEN_BRACKET);
			}
			if (identifier.contains(OtcConstants.MAP_KEY_REF) || identifier.contains(OtcConstants.MAP_VALUE_REF)) {
				identifier = identifier.replace(OtcConstants.MAP_KEY_REF, "").replace(OtcConstants.MAP_VALUE_REF, "");
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
