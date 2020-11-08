package org.otcl2.common.dto;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ClassDto {

	private Map<String,String> imports = new HashMap<>();
	public String packageName;
	public String className;
	public StringBuilder codeBuilder = new StringBuilder();
	public String fullyQualifiedClassName;
	
	public void clearImports() {
		imports.clear();
	}
	public Collection<String> retrieveImportFqNames() {
		return imports.values();
	}

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
