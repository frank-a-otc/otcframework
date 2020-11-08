/**
* Copyright (c) otcl2.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*/
package org.otcl2.common.util;

import java.util.ArrayList;
import java.util.List;

public class PackagesFilterUtil {

	private static List<String> pkgsToFilter;

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

	public static boolean isFilteredPackage(Class<?> type) {
		if (type.isArray()) {
			type = type.getComponentType();
		}
		return isFilteredPackage(((Class<?>) type).getName());
	}

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
