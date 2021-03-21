/**
* Copyright (c) otclfoundation.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*/
package org.otcl2.common.util;

import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class PackagesFilterUtil.
 */
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
