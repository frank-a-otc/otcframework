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
