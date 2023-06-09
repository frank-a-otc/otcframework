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
package org.otcframework.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;

/**
 * The Class CommonUtils.
 */
// TODO: Auto-generated Javadoc
public class CommonUtils {

	/** The logger. */
	private static Logger LOGGER = LoggerFactory.getLogger(CommonUtils.class);

	/**
	 * Inits the cap.
	 *
	 * @param str the str
	 * @return the string
	 */
	public static String initCap(String str) {
		char[] chars = str.toCharArray();
		chars[0] = Character.toUpperCase(chars[0]);
		return new String(chars);
	}

	/**
	 * Inits the lower.
	 *
	 * @param str the str
	 * @return the string
	 */
	public static String initLower(String str) {
		char[] chars = str.toCharArray();
		chars[0] = Character.toLowerCase(chars[0]);
		return new String(chars);
	}

	/**
	 * Replace last.
	 *
	 * @param orginalStr the orginal str
	 * @param searchStr  the search str
	 * @param replaceStr the replace str
	 * @return the string
	 */
	public static String replaceLast(String orginalStr, String searchStr, String replaceStr) {
		if (orginalStr == null || searchStr == null || replaceStr == null) {
			return orginalStr;
		}
		int idx = orginalStr.lastIndexOf(searchStr);
		if (idx < 0) {
			return orginalStr;
		}
		return orginalStr.substring(0, idx) + replaceStr + orginalStr.substring(idx + searchStr.length());
	}

	/**
	 * Checks if is empty.
	 *
	 * @param str the str
	 * @return true, if is empty
	 */
	public static boolean isEmpty(String str) {
		return str == null || str.trim().length() == 0;
	}

	/**
	 * Creates the filename filter.
	 *
	 * @param ext the ext
	 * @return the file filter
	 */
	public static FileFilter createFilenameFilter(final String ext) {
		FileFilter fileFilter = new FileFilter() {
			public boolean accept(File file) {
				if (file.getName().endsWith(ext) || file.isDirectory()) {
					return true;
				}
				return false;
			}
		};
		return fileFilter;
	}

}
