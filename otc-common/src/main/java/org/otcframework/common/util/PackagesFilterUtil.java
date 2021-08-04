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
package org.otcframework.common.util;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class PackagesFilterUtil.
 */
// TODO: Auto-generated Javadoc
public class PackagesFilterUtil {

	/** The pkgs to filter. */
	private static List<String> pkgsToFilter;

	/**
	 * Sets the filtered packages.
	 *
	 * @param pkgsToFilter the new filtered packages
	 */
	public static void setFilteredPackages(List<String> pkgsToFilter) {
		if (PackagesFilterUtil.pkgsToFilter == null) {
			PackagesFilterUtil.pkgsToFilter = new ArrayList<>();
		}
		for (String pkg : pkgsToFilter) {
			if (pkg.endsWith("*")) {
				pkg = pkg.replace("*", "");
			}
			PackagesFilterUtil.pkgsToFilter.add(pkg);
		}
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
