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
package org.otcframework.core.engine.utils;

import org.otcframework.common.OtcConstants;
import org.otcframework.common.util.CommonUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class CompilerUtil.
 */
public final class CompilerUtil {

	/**
	 * Builds the java class name.
	 *
	 * @param otcNamespace the otc namespace
	 * @param otcFileName the otc file name
	 * @param otcChain the otc chain
	 * @return the string
	 */
	public static String buildJavaClassName(String otcNamespace, String otcFileName, String otcChain) {
		if (otcFileName.endsWith(".otc")) {
			otcFileName = otcFileName.replace(otcFileName.substring(otcFileName.lastIndexOf(".otc")), "");
		}
		String factoryClzName = sanitizeJavaIdentifier(otcFileName);
		if (!CommonUtils.isEmpty(otcNamespace)) {
			factoryClzName = otcNamespace + "." + factoryClzName;
		}
		if (!CommonUtils.isEmpty(otcChain)) {
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
	 * @param otcFileName the otc file name
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
