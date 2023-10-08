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

import java.util.Set;
import java.util.stream.Collectors;

/**
 * The Class PackagesFilterUtil.
 */
public class PackagesFilterUtil {

	/** The pkgs to filter. */
	private static Set<String> pkgsToFilter;

	private PackagesFilterUtil() {}
	/**
	 * Sets the filtered packages.
	 *
	 * @param pkgsToFilter the new filtered packages
	 */
	public static void setFilteredPackages(Set<String> pkgsToFilter) {
		PackagesFilterUtil.pkgsToFilter = pkgsToFilter.stream().map(pkg -> {
			if (pkg.endsWith("*")) {
				pkg = pkg.replace("*", "");
			}
			return pkg;
		}).collect(Collectors.toSet());
	}

	/**
	 * Checks if is filtered package.
	 *
	 * @param type the type
	 * @return true, if is filtered package
	 */
	public static boolean isFilteredPackage(Class<?> type) {
		if (type.isArray()) {
			type = type.getComponentType();
		}
		return isFilteredPackage(((Class<?>) type).getName());
	}

	/**
	 * Checks if is filtered package.
	 *
	 * @param clsName the cls name
	 * @return true, if is filtered package
	 */
	public static boolean isFilteredPackage(String clsName) {
		for (String pkgName : pkgsToFilter) {
			if (!clsName.endsWith("."))
				pkgName = pkgName.substring(0, pkgName.length() - 1);
			if (clsName.startsWith(pkgName)) {
				return true;
			}
		}
		return false;
	}
}
