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
package org.otcframework.common.dto;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * The Class ClassDto.
 */
public class ClassDto {

	/** The imports. */
	private Map<String, String> imports = new HashMap<>();

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
