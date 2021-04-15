/**
* Copyright (c) otclfoundation.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*
* This file is part of the OTCL framework.
* 
*  The OTCL framework is free software: you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  the Free Software Foundation, version 3 of the License.
*
*  The OTCL framework is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU General Public License for more details.
*
*  A copy of the GNU General Public License is made available as 'License.md' file, 
*  along with OTCL framework project.  If not, see <https://www.gnu.org/licenses/>.
*
*/
package org.otcl2.common.dto;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * The Class ClassDto.
 */
public class ClassDto {

	/** The imports. */
	private Map<String,String> imports = new HashMap<>();
	
	/** The package name. */
	public String packageName;
	
	/** The class name. */
	public String className;
	
	/** The code builder. */
	public StringBuilder codeBuilder = new StringBuilder();
	
	/** The fully qualified class name. */
	public String fullyQualifiedClassName;
	
	/**
	 * Clear imports.
	 */
	public void clearImports() {
		imports.clear();
	}
	
	/**
	 * Retrieve import fq names.
	 *
	 * @return the collection
	 */
	public Collection<String> retrieveImportFqNames() {
		return imports.values();
	}

	/**
	 * Adds the import.
	 *
	 * @param fqTypeName the fq type name
	 * @return the string
	 */
	public String addImport(String fqTypeName) {
		String typeName = fqTypeName;
		String pkg = null;
		if (fqTypeName.contains(".")) {
			pkg = fqTypeName.substring(0, fqTypeName.lastIndexOf("."));
			typeName = fqTypeName.substring(fqTypeName.lastIndexOf(".") + 1);
			if (pkg.equals("java.lang")) {
				return typeName;
			}
		}
		String typeNameKey = typeName;
		String typeNameValue = fqTypeName;
		if (typeNameKey.contains("[]")) {
			typeNameKey = typeNameKey.replace("[]", "");
			typeNameValue = typeNameValue.replace("[]", "");
		}
		if (imports.containsKey(typeNameKey)) {
			if (imports.get(typeNameKey).equals(typeNameValue)) {
				return typeName;
			} else {
				return fqTypeName;
			}
		} else {
			imports.put(typeNameKey, typeNameValue);
			return typeName;
		}
	}

}
