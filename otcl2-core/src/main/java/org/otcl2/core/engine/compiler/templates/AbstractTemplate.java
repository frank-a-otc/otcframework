package org.otcl2.core.engine.compiler.templates;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.otcl2.common.OtclConstants;
import org.otcl2.common.OtclConstants.TARGET_SOURCE;
import org.otcl2.common.dto.OtclCommandDto;
import org.otcl2.common.util.CommonUtils;
import org.otcl2.common.util.PackagesFilterUtil;
import org.otcl2.core.engine.compiler.command.OtclCommand;
import org.otcl2.core.engine.compiler.command.SourceOtclCommandContext;
import org.otcl2.core.engine.compiler.command.TargetOtclCommandContext;
import org.otcl2.core.engine.compiler.exception.CodeGeneratorException;

public abstract class AbstractTemplate {

	private static final Set<Class<?>> fromTypes = new HashSet<>(8);
	private static final Map<Class<?>, String> convertFromStringExpressions = new IdentityHashMap<>(10);
	private static final Map<Class<?>, String> toTypeConvertExpressions = new IdentityHashMap<>(8);

	static {

//		wrapperTypes.add(Character.class);
//		wrapperTypes.add(Boolean.class);
		fromTypes.add(Byte.class);
		fromTypes.add(Double.class);
		fromTypes.add(Float.class);
		fromTypes.add(Integer.class);
		fromTypes.add(Long.class);
		fromTypes.add(Short.class);
		fromTypes.add(BigInteger.class);
		fromTypes.add(BigDecimal.class);

		toTypeConvertExpressions.put(String.class, "%s.toString()");
		toTypeConvertExpressions.put(Byte.class, "%s.byteValue()");
		toTypeConvertExpressions.put(Double.class, "%s.doubleValue()");
		toTypeConvertExpressions.put(Float.class, "%s.floatValue()");
		toTypeConvertExpressions.put(Integer.class, "%s.intValue()");
		toTypeConvertExpressions.put(Long.class, "%s.longValue()");
		toTypeConvertExpressions.put(Short.class, "%s.shortValue()");
		
		convertFromStringExpressions.put(Boolean.class, "Boolean.valueOf(%s)");
		convertFromStringExpressions.put(Byte.class, "Byte.valueOf(%s)");
		convertFromStringExpressions.put(Double.class, "Double.valueOf(%s)");
		convertFromStringExpressions.put(Float.class, "Float.valueOf(%s)");
		convertFromStringExpressions.put(Integer.class, "Integer.valueOf(%s)");
		convertFromStringExpressions.put(Long.class, "Long.valueOf(%s)");
		convertFromStringExpressions.put(Short.class, "Short.valueOf(%s)");
		convertFromStringExpressions.put(BigInteger.class, "new BigInteger(%s)");
		convertFromStringExpressions.put(BigDecimal.class, "new BigDecimal(%s)");
		convertFromStringExpressions.put(URL.class, "new URL(%s)");
	}

	public static final String CODE_TO_REPLACE = "CODE_TO_REPLACE";
	public static final String CODE_TO_ADD_MAPENTRY = "CODE_TO_ADD_MAPENTRY";
	public static final String CODE_TO_ADD_ELSE_MAPENTRY = "CODE_TO_ADD_ELSE_MAPENTRY";
	public static final String CODE_TO_CREATE_MAPKEY = "CODE_TO_CREATE_MAPKEY";
	public static final String CODE_TO_CREATE_MAPVALUE = "CODE_TO_CREATE_MAPVALUE";

	public static final String SOURCE_ICD = "sourceICD";
	public static final String PARENT_SOURCE_ICD = "parentSourceICD";
	public static final String MEMBER_SOURCE_ICD = "memberSourceICD";
	public static final String TARGET_ICD = "targetICD";
	public static final String PARENT_TARGET_ICD = "parentTargetICD";
	public static final String MEMBER_TARGET_ICD = "memberTargetICD";

	public static final String PARENT_ICD = "parentICD";
	public static final String MEMBER_ICD = "memberICD";
	public static final String SOURCE_IDX = "sourceIdx";
	public static final String TARGET_IDX = "targetIdx";
	public static final String OFFSET_IDX = "offsetIdx";

	protected static final String mainClassBeginCodeTemplate = "package %s;\n"
			+ "\nimport org.otcl2.common.engine.profiler.dto.IndexedCollectionsDto;"
			+ "\nimport org.otcl2.common.engine.executor.CodeExecutor;"
			+ "\nimport java.util.Map;"
			+ "\nimport java.util.HashMap;"
			+ OtclCommand.CODE_TO_IMPORT
			+ "\npublic class %s"
			+ "\nimplements CodeExecutor<%s, %s> {"
			+ "\n\n@Override" 
			+ "\npublic %s execute(%s %s, IndexedCollectionsDto sourceICD, "
			+ "\nMap<String, Object> data) {"
			+ "\n\n%s %s = new %s();"
			+ "\nIndexedCollectionsDto targetICD = new IndexedCollectionsDto();"
			+ "\ntargetICD.children = new HashMap<>();";

	protected static final String loggerInitTemplate = "\n\nprivate static final Logger LOGGER = "
			+ "LoggerFactory.getLogger(%s.class); ";

	protected static final String factoryClassBeginCodeTemplate = "package %s;\n"
			+ "\nimport org.otcl2.common.engine.profiler.dto.IndexedCollectionsDto;"
			+ "\n" + OtclCommand.CODE_TO_IMPORT
			+ "\n\npublic class %s {"
			+ loggerInitTemplate
			+ "\n\npublic static void execute(%s %s, IndexedCollectionsDto sourceICD, "
			+ "\n%s %s, IndexedCollectionsDto targetICD, "
			+ "\nMap<String, Object> data) {";

	protected static final String executeFactoryMethodCallTemplate = "\n%s.execute(%s, sourceICD, %s, targetICD, data);";

	protected static final String factoryModuleClassBeginCodeTemplate = "package %s;\n"
			+ "\nimport org.otcl2.common.engine.profiler.dto.IndexedCollectionsDto;"
			+ "\nimport org.otcl2.core.engine.module.AbstractOtclModuleExecutor;"
			+ "\n" + OtclCommand.CODE_TO_IMPORT
			+ "\n\npublic class %s extends AbstractOtclModuleExecutor {"
			+ loggerInitTemplate
			+ "\n\npublic static void execute(%s %s, IndexedCollectionsDto sourceICD, "
			+ "\n%s %s, IndexedCollectionsDto targetICD,"
			+ "\nMap<String, Object> data) {";

	protected static final String executeModuleTemplate = "\nString otclNamespace = \"%s\";"
			+ "\nexecuteModule(otclNamespace, %s, %s, data);";

	protected static final String executeConverterTemplate = "\n%s.convert(%s, %s, data);";
	
	protected static final String parentSourceIcdTemplate = "\nIndexedCollectionsDto parentSourceICD = null;";
	protected static final String memberSourceIcdTemplate = "\nIndexedCollectionsDto memberSourceICD = null;";
	protected static final String parentTargetIcdTemplate = "\nIndexedCollectionsDto parentTargetICD = null;";
	protected static final String memberTargetIcdTemplate = "\nIndexedCollectionsDto memberTargetICD = null;";

	protected static final String keyTargetIcdTemplate = "\nIndexedCollectionsDto keyTargetICD = null;";
	protected static final String valueTargetIcdTemplate = "\nIndexedCollectionsDto valueTargetICD = null;";
	protected static final String keySourceIcdTemplate = "\nIndexedCollectionsDto keySourceICD = null;";
	protected static final String valueSourceIcdTemplate = "\nIndexedCollectionsDto valueSourceICD = null;";
	protected static final String anchoredIcdTemplate = "\nIndexedCollectionsDto anchoredICD = null;";

	protected static final String assignKeyToMemberIcdTemplate = "\nmemberTargetICD = keyTargetICD;";
	protected static final String assignValueToMemberIcdTemplate = "\nmemberTargetICD = valueTargetICD;";
	
	protected static final String assignMemberIcdToParentIcdTemplate = "\nparentTargetICD = memberTargetICD;";
	protected static final String assignParentIcdToAnchoredIcdTemplate = "\nanchoredICD = parentTargetICD;";
	protected static final String assignAnchoredIcdToParentIcdTemplate = "\nparentTargetICD = anchoredICD;";

	protected static final String idxAndLenTemplate = "\nint idx = 0;"
			+ "\nint len = 0;\n";
	
	protected static final String preloopVarsTemplate = "\nint offsetIdx = 0;";
	public static final String incrementOffsetIdx = "\noffsetIdx++;";
	public static final String initOffsetIdx = "\noffsetIdx = 0;";

	protected static final String createInitVarTemplate = "\n%s %s = %s;";
	protected static final String createInstanceTemplate = "\n%s %s = new %s();";
	
	protected static final String getterTemplate = "\n%s %s = %s.%s();";
	protected static final String helperGetterTemplate = "\n%s %s = %s.%s(%s);";
	protected static final String getSetTemplate = "\n%s.%s(%s.%s());";
	protected static final String getHelperTemplate = "\n%s.%s(%s.%s(%s));";
	protected static final String setHelperTemplate = "\n%s.%s(%s, %s.%s());";
	protected static final String setHelperGetHelperTemplate = "\n%s.%s(%s, %s.%s(%s));";

	protected static final String getSetTargetEnumTemplate = "\n%s.%s(%s.valueOf(%s.%s()));";
	protected static final String getSetSourceEnumTemplate = "\n%s.%s(%s.%s().toString());";
	protected static final String getSetBothEnumTemplate = "\n%s.%s(%s.valueOf(%s.%s().toString()));";

	protected static final String setterTemplate = "\n%s.%s(%s);";
	protected static final String setterTargetEnumTemplate = "\n%s.%s(%s.valueOf(%s));";
	protected static final String setterSourceEnumTemplate = "\n%s.%s(%s.toString());";
	protected static final String setterBothEnumTemplate = "\n%s.%s(%s.valueOf(%s.toString()));";

	protected static final String helperSetterTemplate = "\n%s.%s(%s, %s);";

	protected static final String dateConverterTemplate = "\n%s.%s(MutualDateTypesConverterFacade.convert(%s, %s.class));";
	protected static final String dateToStringConverterTemplate = "\n%s.%s(%s.toString());";
//	protected static final String formattedDateConverterTemplate = "\n%s.%s(MutualDateTypesConverterFacade.convert(%s, %s));";

	protected static final String ifNullCreateAndSetTemplate = "\nif (%s == null) {" 
			+ "\n%s = new %s();"
			+ "\n%s.%s(%s);" 
			+ "\n}";

	protected static final String ifNullCreateAndHelperSetTemplate = "\nif (%s == null) {" 
			+ "\n%s = new %s();"
			+ "\n%s.%s(%s, %s);" 
			+ "\n}";


	protected static final String getterIfNullReturnTemplate = getterTemplate
			+ "\nif (%s == null) {" 
			+ "\nLOGGER.%s(\"%s\");"
			+ "\nreturn;" 
			+ "\n}";

	protected static final String getterIfNullContinueTemplate = getterTemplate
			+ "\nif (%s == null) {" 
			+ "\nLOGGER.%s(\"%s\");"
			+ "\ncontinue;" 
			+ "\n}";

	protected static final String helperGetIfNullReturnTemplate = helperGetterTemplate
			+ "\nif (%s == null) {" 
			+ "\nLOGGER.%s(\"%s\");"
			+ "\nreturn;" 
			+ "\n}";

	protected static final String helperGetIfNullContinueTemplate = helperGetterTemplate
			+ "\nif (%s == null) {" 
			+ "\nLOGGER.%s(\"%s\");"
			+ "\ncontinue;" 
			+ "\n}";

	protected static final String methodEndTemplate = "\nreturn %s;"
			+ "\n}";

	protected static final String ifNullTargetRootIcdCreateTemplate = "\nparentTargetICD = targetICD.children.get(\"%s\");"
			+ "\nif (parentTargetICD == null) {"
			+ "\nparentTargetICD = IndexedCollectionsDtoFactory.create(targetICD, %s, \"%s\", true);"
			+ "\n}";

	protected static final String ifNullTargetRootIcdReturnTemplate = "\nparentTargetICD = targetICD.children.get(\"%s\");"
			+ "\nif (parentTargetICD == null || parentTargetICD.children == null || parentTargetICD.children.size() == 0) {"
			+ "\nLOGGER.%s(\"%s\");"
			+ "\nreturn;"
			+ "\n}";

	protected static final String ifNullSourceIcdReturnTemplate = "\nif (sourceICD == null) {"
			+ "\nLOGGER.warn(\"Cannot continue! No collections present in source-object.\");"
			+ "\nreturn;" 
			+ "\n}";
	
	protected static final String ifNullSourceRootIcdReturnTemplate = ifNullSourceIcdReturnTemplate
			+ "\nparentSourceICD = sourceICD.children.get(\"%s\");"
			+ "\nif (parentSourceICD == null || parentSourceICD.children == null || parentSourceICD.children.size() == 0) {"
			+ "\nLOGGER.%s(\"%s\");"
			+ "\nreturn;"
			+ "\n}";

	protected static final String ifNullIcdReturnTemplate = 
			"\n%s = %s.children.get(%s);"
			+ "\nif (%s == null || %s.children == null || %s.children.size() == 0) {"
			+ "\nLOGGER.%s(\"%s\");"
			+ "\nreturn;"
			+ "\n}";

	protected static final String ifNullLastIcdReturnTemplate = 
			"\n%s = %s.children.get(%s);"
			+ "\nif (%s == null) {"
			+ "\nLOGGER.%s(\"%s\");"
			+ "\nreturn;"
			+ "\n}";

	protected static final String ifNullTargetIcdCreateTemplate =  "\n%s = %s.children.get(%s);"
			+ "\nif (%s == null) {"
			+ "\n%s = IndexedCollectionsDtoFactory.create(%s, %s, %s, true);"
			+ "\n}";
	
	protected static final String ifNullTargetIcdCreateOrInitTemplate = "\n%s = %s.children.get(%s);"
			+ "\nif (%s == null) {"
			+ "\n%s = IndexedCollectionsDtoFactory.create(%s, null, %s, true);"
			+ "\n} else {"
			+ "\n%s = %s;"
			+ "\n}";

	protected static final String retrieveMemberFromIcdTemplate = "\n%s %s = (%s) %s.profiledObject;";

	protected static final String addToArrayTemplate = "\nlen = %s.length;"
			+ "\nif (len < %s + 1) {"
			+ "\n%s = Arrays.copyOf(%s, len + 1);"
			+ "\n%s.%s(%s);"
			+ "\n}"
			+ createInstanceTemplate
			+ "\n%s[%s] = %s;";
	
	protected static final String helperAddToArrayTemplate = "\nlen = %s.length;"
			+ "\nif (len < %s + 1) {"
			+ "\n%s = Arrays.copyOf(%s, len + 1);"
			+ "\n%s.%s(%s, %s);"
			+ "\n}"
			+ createInstanceTemplate
			+ "\n%s[%s] = %s;";
	
	protected static final String resizeArrayAndAddAtEndTemplate = "\nlen = %s.length;"
			+ "\n%s = Arrays.copyOf(%s, len + 1);"
			+ CODE_TO_REPLACE
			+ "\n%s[len] = %s;";
		
	protected static final String addToCollectionTemplate = "\n%s.add(%s);";

	protected static final String addCollectionMemberTemplate = createInitVarTemplate
			+ "\nmemberTargetICD = parentTargetICD.children.get(%s);"
			+ "\nif (memberTargetICD != null) {"
			+ "\n%s = (%s) memberTargetICD.profiledObject;"
			+ "\n}"
			+ "\nif (%s == null) {"
			+ CODE_TO_REPLACE
			+ "\nmemberTargetICD = IndexedCollectionsDtoFactory.create(parentTargetICD, %s, %s, true);"
			+ "\n}";

	protected static final String addCollectionMemberAtEndTemplate = "\nidx = 0;" 
			+ "\nif (parentTargetICD.children.size() > 0) {" 
			+ "\nidx = parentTargetICD.children.size();" 
			+ "\n}" 
			+ CODE_TO_REPLACE
			+ "\nmemberTargetICD = IndexedCollectionsDtoFactory.create(parentTargetICD, %s, \"\" + idx, true);";

	protected static final String getIcdChildrenSizeTemplate = "\nint size%s = %s.children.size();";

	protected static final String retrieveParentIcd = "\nIndexedCollectionsDto parentICD%s = %s.children.get(%s);";

	protected static final String preLoopTemplate = retrieveParentIcd
			+ "\nif (parentICD%s == null || parentICD%s.children == null || parentICD%s.children.size() == 0) {"
			+ "\nLOGGER.%s(\"%s\");"
			+ "\ncontinue;"
			+ "\n}"
			+ "\nint size%s = parentICD%s.children.size();";

	protected static final String postLoopTemplate = 
			"\nif (memberICD%s == null || memberICD%s.children == null || memberICD%s.children.size() == 0) {"
			+ "\nLOGGER.%s(\"%s\");"
			+ "\ncontinue;"
			+ "\n}";
	
	protected static final String lastPostSourceLoopTemplate = 
			"\nif (memberICD%s == null) {"
			+ "\nLOGGER.%s(\"%s\");"
			+ "\ncontinue;"
			+ "\n}"
			+ retrieveMemberFromIcdTemplate;

	protected static final String retrieveMemberIcd = "\nIndexedCollectionsDto memberICD%s = %s.children.get(%s);";
	
	protected static final String toplevelTargetPreLoopTemplate = "\nint size%s = 0;"
			+ "\nif (%s.children.size() == 0) {"
			+ "\nsize%s = 1;"
			+ "\n} else {"
			+ "\nsize%s = %s.children.size();"
			+ "\n}";

	protected static final String preTargetLoopTemplate = "\nint size%s = 0;"
			+ "\nIndexedCollectionsDto parentICD%s = %s.children.get(%s);"
			+ "\nif (parentICD%s == null) {"
			+ "\nparentICD%s = IndexedCollectionsDtoFactory.create(%s, %s, %s, true);"
			+ "\nsize%s = 1;"
			+ "\n} else {"
			+ "\nsize%s = parentICD%s.children.size();"
			+ "\n}";

	protected static final String forLoopTemplate = "\nfor (int %s = 0; %s < size%s; %s++) {";
	
	protected static final String postTargetLoopTemplate = 
			"\nIndexedCollectionsDto memberICD%s = %s.children.get(%s);"
			+ "\n%s %s = null;"
			+ "\nif (memberICD%s != null) {"
			+ "\n%s = (%s) memberICD%s.profiledObject;"
			+ "\n} else {"
			+ CODE_TO_REPLACE
			+ "\nmemberICD%s = IndexedCollectionsDtoFactory.create(%s, %s, %s, true);"
			+ "\n}";

	protected static final String postTargetLoopMapKeyTemplate = 
			"\nIndexedCollectionsDto memberICD%s = %s.children.get(%s);"
			+ "\n%s %s = null;"
			+ "\nif (memberICD%s == null) {" 
			+ CODE_TO_CREATE_MAPKEY + " "
			+ CODE_TO_CREATE_MAPVALUE + " "
			+ "\n%s.put(%s, %s);"
			+ "\nmemberICD%s = IndexedCollectionsDtoFactory.create(%s, %s, %s, true);" 
			+ "\nvalueTargetICD = IndexedCollectionsDtoFactory.create(%s, %s, %s, true);"
			+ "\n} else {"
			+ "\n%s = (%s) memberICD%s.profiledObject;"
			+ "\n}";

	protected static final String postTargetLoopMapValueTemplate = 
			"\nIndexedCollectionsDto memberICD%s = %s.children.get(%s);"
			+ "\nif (memberICD%s == null) {" 
			+ "\nLOGGER.%s(\"%s\");"
			+ "\ncontinue;"
			+ "\n}"
			+ "\n%s %s = (%s) memberICD%s.profiledObject;";

	protected static final String ifNotContainsMapKeyTemplate = "\nif (!%s.containsKey(%s)) {" 
			+ CODE_TO_ADD_MAPENTRY
			+ "\n}";

	protected static final String ifNullMapKeyICDTemplate = "\nif (parentTargetICD.children != null) {" 
			+ "\nkeyTargetICD = parentTargetICD.children.get(%s);"
			+ "\n}"
			+ "\nif (keyTargetICD == null) {" 
			+ CODE_TO_ADD_MAPENTRY
			+ "\n} else {"
			+ CODE_TO_ADD_ELSE_MAPENTRY
			+ "\n}";

	protected static final String addMapEntryTemplate = CODE_TO_CREATE_MAPKEY + " "
			+ CODE_TO_CREATE_MAPVALUE + " "
			+ "\n%s.put(%s, %s);"
			+ "\nkeyTargetICD = IndexedCollectionsDtoFactory.create(parentTargetICD, %s, %s + \"<K>\", true);" 
			+ "\nvalueTargetICD = IndexedCollectionsDtoFactory.create(parentTargetICD, %s, %s + \"<V>\", true);";

	protected static final String retrieveLastMapKeyTemplate = "\nidx = parentTargetICD.children.size() / 2 - 1;" 
			+ "\nkeyTargetICD = parentTargetICD.children.get(idx + \"<K>\");"
			+ "\nvalueTargetICD = parentTargetICD.children.get(idx + \"<V>\");";

	protected static final String ifNullMapKeyIcdReturnTemplate = "\nkeyTargetICD = parentTargetICD.children.get(%s);"
			+ "\nif (keyTargetICD == null) {" 
			+ "\nLOGGER.%s(\"%s\");"
			+ "\nreturn;"
			+ "\n}"
			+ "\nvalueTargetICD = parentTargetICD.children.get(%s);";

	protected static final String addMapEntryUpdateIcdTemplate = "\n%s.put(%s, %s);"
			+ "\nvalueTargetICD.profiledObject = %s;";

	protected static final String retrieveMapKeyFromIcdTemplate = "\n%s %s = (%s) keyTargetICD.profiledObject;";
	protected static final String retrieveMapValueFromIcdTemplate = "\n%s %s = (%s) valueTargetICD.profiledObject;";

	protected static String fetchFieldTypeName(TargetOtclCommandContext targetOCC, SourceOtclCommandContext sourceOCC,
			OtclCommandDto otclCommandDto, boolean createNewVarName, Map<String, String> varNamesMap) {
		boolean isVarAlreadyCreated = false;
		if (!createNewVarName) {
			if (varNamesMap.containsKey(otclCommandDto.enumTargetSource + otclCommandDto.tokenPath)) {
				isVarAlreadyCreated = true;
			}
		}
		String fqTypeName = "";
		if (!isVarAlreadyCreated) {
			if (otclCommandDto.fieldType.isArray()) {
				fqTypeName = otclCommandDto.fieldType.getTypeName();
			} else {
				fqTypeName = otclCommandDto.fieldType.getName();
			}
			if (fqTypeName.contains("$")) {
				fqTypeName = fqTypeName.replace("$", ".");
			}
			fqTypeName = targetOCC.factoryClassDto.addImport(fqTypeName);
			if ((otclCommandDto.isCollectionOrMap() && !otclCommandDto.isArray())) {
				String generics = null;
				OtclCommandDto memberOCD = null;
				if (TARGET_SOURCE.SOURCE == otclCommandDto.enumTargetSource) {
					memberOCD = OtclCommand.retrieveMemberOCD(sourceOCC);
				} else {
					memberOCD = OtclCommand.retrieveMemberOCD(targetOCC);
				}
				generics = memberOCD.fieldType.getName();
				if (generics.contains("$")) {
					generics = generics.replace("$", ".");
				}
				generics = targetOCC.factoryClassDto.addImport( generics);
				if (otclCommandDto.isMap()) {
					String genericKey = null;
					String genericVal = null;
					if (memberOCD.isMapKey()) {
						genericKey = generics;
						OtclCommandDto valueOCD = otclCommandDto.children.get(OtclConstants.MAP_VALUE_REF + 
								otclCommandDto.fieldName);
						genericVal = valueOCD.fieldType.getName();
						if (genericVal.contains("$")) {
							genericVal = fqTypeName.replace("$", ".");
						}
						genericVal = targetOCC.factoryClassDto.addImport(genericVal);
					} else {
						genericVal = generics;
						OtclCommandDto keyOCD = otclCommandDto.children.get(OtclConstants.MAP_KEY_REF + 
								otclCommandDto.fieldName);
						genericKey = keyOCD.fieldType.getName();
						if (genericKey.contains("$")) {
							genericKey = fqTypeName.replace("$", ".");
						}
						genericKey = targetOCC.factoryClassDto.addImport(genericKey);
					}
					generics = genericKey + ", " + genericVal;
				}
				fqTypeName += "<" + generics + ">";
			}
		}
		return fqTypeName;
	}
	

	protected static String fetchConcreteTypeName(TargetOtclCommandContext targetOCC, OtclCommandDto otclCommandDto) {
		String clzName = null;
		if (otclCommandDto.concreteTypeName != null) {
			clzName = otclCommandDto.concreteTypeName;
			if (clzName != null) {
				return clzName;
			}
		}
		Class<?> fieldType = otclCommandDto.fieldType;
		if (fieldType.isArray()) {
			clzName = fieldType.getTypeName();
		} else if (!fieldType.isInterface()) {
			clzName = fieldType.getName();
		} else if (fieldType.equals(List.class)) {
			clzName = ArrayList.class.getName();
		} else if (fieldType.equals(Set.class)) {
			clzName = HashSet.class.getName();
		} else if (fieldType.equals(Queue.class)) {
			clzName = LinkedList.class.getName();
		} else if (fieldType.equals(Map.class)) {
			clzName = HashMap.class.getName();
		}
		if (clzName.contains("$")) {
			clzName = clzName.replace("$", ".");
		}
		clzName = targetOCC.factoryClassDto.addImport(clzName);
		if (fieldType.equals(List.class) || fieldType.equals(Set.class) || fieldType.equals(Queue.class) ||
				fieldType.equals(Map.class)) {
			clzName += "<>";
		}
		return clzName;
	}

	protected static String fetchSanitizedTypeName(TargetOtclCommandContext targetOCC, OtclCommandDto otclCommandDto) {
		String fieldType = otclCommandDto.fieldType.getName();
		if (fieldType.contains("$")) {
			fieldType = fieldType.replace("$", ".");
		}
		fieldType = targetOCC.factoryClassDto.addImport(fieldType);
		return fieldType;
	}
	
	protected static String createVarName(OtclCommandDto otclCommandDto, boolean createNewVarName, Set<String> varNamesSet,
			Map<String, String> varNamesMap) {
		String varName = null;
		if (!createNewVarName) {
			varName = varNamesMap.get(otclCommandDto.enumTargetSource + otclCommandDto.tokenPath);
		}
		if (varName == null) {
			if (otclCommandDto.isRootNode) {
				if (otclCommandDto.fieldName.equals(OtclConstants.ROOT)) {
					varName = CommonUtils.initLower(otclCommandDto.declaringClass.getSimpleName());
				} else {
					varName = createVarName(otclCommandDto, varNamesSet);
					varNamesMap.put(otclCommandDto.enumTargetSource + otclCommandDto.tokenPath, varName);
				}
			} else {
				varName = createVarName(otclCommandDto, varNamesSet);
				varNamesMap.put(otclCommandDto.enumTargetSource + otclCommandDto.tokenPath, varName);
			}
		}
		return varName;
	}
	
	protected static String createVarName(OtclCommandDto otclCommandDto, Set<String> varNamesSet) {
		String varName = otclCommandDto.fieldName;
		if (otclCommandDto.isMapKey()) {
			varName += "Key"; 
		} else if (otclCommandDto.isMapValue()) {
			varName += "Value"; 
		}
		varName = sanitizeVarName(varName, varNamesSet);
		return varName;
	}
	
	protected static String createVarName(String type, Set<String> varNamesSet, boolean createNewVar) {
		String varName = CommonUtils.initLower(type);
		if (!createNewVar) {
			return varName;
		}
		varName = sanitizeVarName(varName, varNamesSet);
		return varName;
	}
	
	protected static String sanitizeVarName(String varName, Set<String> varNamesSet) {
		if (varNamesSet == null) {
			return varName;
		}
		if (!varNamesSet.contains(varName)) {
			varNamesSet.add(varName);
			return varName;
		}
		int idx = 0;
		while (true) {
			if (!varNamesSet.contains(varName + idx)) {
				varName = varName + idx;
				break;
			}
			idx++;
		}
		varNamesSet.add(varName);
		return varName;
	}
	
	protected static String createIcdKey(String otclToken) {
		if (!otclToken.startsWith("\"")) {
			otclToken = "\"" + otclToken + "\"";
		}
		return otclToken;
	}
	
	protected static String createIcdKey(OtclCommandDto memberOCD, String idxPrefix, Integer idx) {
		if ((idx == null && idxPrefix == null) || (idx != null && idxPrefix != null)) {
			throw new CodeGeneratorException("", "Invalid call to the method! "
					+ "Either one of idx or idxPrefix only is required.");
		}
		if (!memberOCD.isCollectionOrMapMember()) {
			throw new CodeGeneratorException("", "Invalid call to the method! "
					+ "Not a Colleciton / Map member.");
		}
		String mapKeyValueRef = null;
		if (memberOCD.isMapMember()) {
			if (memberOCD.isMapKey()) {
				mapKeyValueRef = OtclConstants.MAP_KEY_REF;
			} else {
				mapKeyValueRef = OtclConstants.MAP_VALUE_REF;
			}
		}
		String icdKey = null;
		if (idxPrefix != null) {
			if (memberOCD.isMapMember()) {
				icdKey = idxPrefix + " + " + "\"" + mapKeyValueRef + "\"";
			} else {
				icdKey = "\"\"" + " + " + idxPrefix;
			}
		} else {
			if (memberOCD.isMapMember()) {
				icdKey = "\"" + idx + mapKeyValueRef + "\"";
			} else {
				icdKey = "\"" + idx + "\"";
			}
		}
		return icdKey;
	}
	
	protected static String fetchValueOrVar(TargetOtclCommandContext targetOCC, OtclCommandDto sourceOCD, String value,
			boolean createNewVarName, Set<String> varNamesSet, Map<String, String> varNamesMap) {
		OtclCommandDto memberOCD = targetOCC.otclCommandDto;
		String valOrVar = null;
		if (targetOCC.isLeaf()) { 
			if (value == null) {
				if (sourceOCD == null) {
					throw new CodeGeneratorException("", "Invalid call to method in Script-block : " + targetOCC.scriptId + 
							"! Both value and SourceOCD cannot be null for a leaf-token.");
				}
				valOrVar = createVarName(sourceOCD, createNewVarName, varNamesSet, varNamesMap);
			} else {
				valOrVar = createConvertExpression(memberOCD, value);
			}
		} else {
			valOrVar = createVarName(memberOCD, createNewVarName, varNamesSet, varNamesMap);
		}
		return valOrVar;
	}
	
	public static String createConvertExpression(OtclCommandDto otclCommandDto, String value) {
		Class<?> clz = otclCommandDto.fieldType;
		String expr = null;
		if (otclCommandDto.isEnum()) {
			expr = otclCommandDto.fieldType.getSimpleName() + ".valueOf(\"" + value + "\")";
		} else {
			if (!value.startsWith("\"")) {
				expr = "\"" + value + "\"";
			} else {
				expr = value;
			}
			if (convertFromStringExpressions.containsKey(clz)) {
				expr = String.format(convertFromStringExpressions.get(clz), expr);
			}
		}
		return expr;
	}

	public static String createConvertExpression(TargetOtclCommandContext targetOCC, SourceOtclCommandContext sourceOCC,
			boolean createNewVarName, Set<String> varNamesSet, Map<String, String> varNamesMap) {
		OtclCommandDto targetOCD = targetOCC.otclCommandDto;
		OtclCommandDto sourceOCD = sourceOCC.otclCommandDto;
		Class<?> targetType = targetOCD.fieldType;
		Class<?> sourceType = sourceOCD.fieldType;
		if (PackagesFilterUtil.isFilteredPackage(targetType) || PackagesFilterUtil.isFilteredPackage(sourceType)) {
			throw new CodeGeneratorException("", "Invalid call to method! The type should not be of a filtered package.");
		}
		String expr = null;
		String sourceVarName = createVarName(sourceOCD, createNewVarName, varNamesSet, varNamesMap);
		if (targetType.isAssignableFrom(sourceType)) {
			return sourceVarName;
		} else if (String.class == targetType) {
			expr = String.format(toTypeConvertExpressions.get(String.class), sourceVarName);
		} else if (fromTypes.contains(sourceType)) {
			if (toTypeConvertExpressions.containsKey(targetType)) {
				expr = String.format(toTypeConvertExpressions.get(targetType), sourceVarName);
			} else if (String.class == sourceType) {
				expr = String.format(convertFromStringExpressions.get(targetType), sourceVarName);
			}
		}
		return expr;
	}

} 
