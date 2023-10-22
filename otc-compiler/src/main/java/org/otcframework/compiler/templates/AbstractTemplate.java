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
package org.otcframework.compiler.templates;

import org.otcframework.common.OtcConstants;
import org.otcframework.common.OtcConstants.TARGET_SOURCE;
import org.otcframework.common.config.OtcConfig;
import org.otcframework.common.dto.OtcCommandDto;
import org.otcframework.common.util.CommonUtils;
import org.otcframework.compiler.command.OtcCommand;
import org.otcframework.compiler.command.SourceOtcCommandContext;
import org.otcframework.compiler.command.TargetOtcCommandContext;
import org.otcframework.compiler.exception.CodeGeneratorException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.*;

/**
 * The Class AbstractTemplate.
 */
public abstract class AbstractTemplate {

	/** The Constant convertFromStringExpressions. */
	private static final Map<Class<?>, String> convertFromStringExpressions = new IdentityHashMap<>(10);

	protected static Map<Class<?>, String> concreteTypes = OtcConfig.getConcreteTypes();

	static {
		if (concreteTypes == null) {
			concreteTypes = new IdentityHashMap<>(4);
		}
		if (!concreteTypes.containsKey(List.class)) {
			concreteTypes.put(List.class, ArrayList.class.getName());
		}
		if (!concreteTypes.containsKey(Set.class)) {
			concreteTypes.put(Set.class, HashSet.class.getName());
		}
		if (!concreteTypes.containsKey(Map.class)) {
			concreteTypes.put(Map.class, HashMap.class.getName());
		}
		if (!concreteTypes.containsKey(Queue.class)) {
			concreteTypes.put(Queue.class, LinkedList.class.getName());
		}		

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

	AbstractTemplate() {}

	/** The Constant CODE_TO_REPLACE. */
	public static final String CODE_TO_REPLACE = "CODE_TO_REPLACE";

	/** The Constant CODE_TO_ADD_MAPENTRY. */
	public static final String CODE_TO_ADD_MAPENTRY = "CODE_TO_ADD_MAPENTRY";

	/** The Constant CODE_TO_ADD_ELSE_MAPENTRY. */
	public static final String CODE_TO_ADD_ELSE_MAPENTRY = "CODE_TO_ADD_ELSE_MAPENTRY";

	/** The Constant CODE_TO_CREATE_MAPKEY. */
	public static final String CODE_TO_CREATE_MAPKEY = "CODE_TO_CREATE_MAPKEY";

	/** The Constant CODE_TO_CREATE_MAPVALUE. */
	public static final String CODE_TO_CREATE_MAPVALUE = "CODE_TO_CREATE_MAPVALUE";

	/** The Constant PARENT_SOURCE_ICD. */
	public static final String PARENT_SOURCE_ICD = "parentSourceICD";

	/** The Constant MEMBER_SOURCE_ICD. */
	public static final String MEMBER_SOURCE_ICD = "memberSourceICD";

	/** The Constant PARENT_TARGET_ICD. */
	public static final String PARENT_TARGET_ICD = "parentTargetICD";

	/** The Constant MEMBER_TARGET_ICD. */
	public static final String MEMBER_TARGET_ICD = "memberTargetICD";

	/** The Constant PARENT_ICD. */
	public static final String PARENT_ICD = "parentICD";

	/** The Constant MEMBER_ICD. */
	public static final String MEMBER_ICD = "memberICD";

	/** The Constant SOURCE_IDX. */
	public static final String SOURCE_IDX = "sourceIdx";

	/** The Constant TARGET_IDX. */
	public static final String TARGET_IDX = "targetIdx";

	/** The Constant OFFSET_IDX. */
	public static final String OFFSET_IDX = "offsetIdx";

	private static final String CR_LF = "\r\n";

	private static final String APP_DATA_VAR = "\nMap<String, Object> data) {";
	private static final String ELSE = "\n} else {";
	private static final String PACKAGE = "\npackage %s;\n";
	private static final String LEN = "\nlen = %s.length;";
	private static final String ARRAYS_COPY = "\n%s = Arrays.copyOf(%s, len + 1);";
	private static final String IMPORT_ICD = "\nimport org.otcframework.common.engine.indexer.dto.IndexedCollectionsDto;";
	private static final String IF_MEMBER_ICD = "\nif (memberICD%s == null) {";
	private static final String LOGGER = "\nLOGGER.%s(\"%s\");";
	private static final String RETURN = "\nreturn;";
	private static final String CONTINUE = "\ncontinue;";
	private static final String CHILDREN_GET = "\n%s = %s.children.get(%s);";
	private static final String PUT = "\n%s.put(%s, %s);";
	private static final String IF_KEY_TARGET_ICD = "\nif (keyTargetICD == null) {";
	private static final String IF_S_NULL = "\nif (%s == null) {";
	private static final String KEY_TARGET_ICD = "\nkeyTargetICD = parentTargetICD.children.get(%s);";

	protected static final String NEW = "new %s()";
	protected static final String SIZE = ".size();";
	protected static final String SIZE_DIVIDED_BY_2 = ".size() / 2;";
	protected static final String MEMBER_ICD_VAR = "memberICD";
	public static final String INVALID_CALL_TO_TEMPLATE = "Invalid call to method in OTC-command : ";
	public static final String TOKEN_SHOULD_NOT_BE = "! Token should not be of a member for this operation.";

	/** The Constant mainClassBeginCodeTemplate. */
	protected static final String MAIN_CLASS_BEGIN_CODE_TEMPLATE = "// This file was generated by the OTC Framework's Compiler."
			+ CR_LF
			+ "// For details on the framework, visit <a href=\"https://otcframework.org\">https://otcframework.org</a>"
			+ CR_LF
			+ "// Any modifications to this file will be lost upon recompilation of the respective OTCS file."
			+ CR_LF + CR_LF
			+ PACKAGE + IMPORT_ICD
			+ "\nimport org.otcframework.common.executor.CodeExecutor;" + "\nimport java.util.Map;"
			+ "\nimport java.util.HashMap;" + OtcCommand.CODE_TO_IMPORT + "\npublic class %s"
			+ "\nimplements CodeExecutor<%s, %s> {" + "\n\n@Override"
			+ "\npublic %s execute(%s %s, IndexedCollectionsDto sourceICD, " + APP_DATA_VAR
			+ "\n\n%s %s = new %s();" + "\nIndexedCollectionsDto targetICD = new IndexedCollectionsDto();"
			+ "\ntargetICD.children = new HashMap<>();";

	/** The Constant loggerInitTemplate. */
	protected static final String LOGGER_INIT_TEMPLATE = "\n\nprivate static final Logger LOGGER = "
			+ "LoggerFactory.getLogger(%s.class); ";

	/** The Constant factoryClassBeginCodeTemplate. */
	protected static final String FACTORY_CLASS_BEGIN_CODE_TEMPLATE = "// This file was generated by the OTC Compiler. \r\n"
			+ "// See <a href=\"https://otcframework.org\">https://otcframework.org</a> \r\n"
			+ "// Any modifications to this file will be lost upon recompilation of the respective OTC file. \r\n"
			+ "//\r\n" + PACKAGE+ IMPORT_ICD + "\n"
			+ OtcCommand.CODE_TO_IMPORT + "\n\npublic class %s {" + LOGGER_INIT_TEMPLATE
			+ "\n\npublic static void execute(%s %s, IndexedCollectionsDto sourceICD, "
			+ "\n%s %s, IndexedCollectionsDto targetICD, " + APP_DATA_VAR;

	/** The Constant executeFactoryMethodCallTemplate. */
	protected static final String EXECUTE_FACTORY_METHOD_CALL_TEMPLATE = "\n%s.execute(%s, %s, %s, targetICD, data);";

	/** The Constant factoryModuleClassBeginCodeTemplate. */
	protected static final String FACTORY_MODULE_CLASS_BEGIN_CODE_TEMPLATE =
			"// This file was generated by the OTC Framework's Compiler. \r\n"
			+ "// For details of the framework, pls visit <a href=\"https://otcframework.org\">https://otcframework.org</a> \r\n"
			+ "// Any modifications to this file will be lost upon recompilation of the respective OTC file. \r\n"
			+ "//\r\n" + PACKAGE + IMPORT_ICD
			+ "\nimport org.otcframework.executor.module.AbstractOtcModuleExecutor;" + "\n" + OtcCommand.CODE_TO_IMPORT
			+ "\n\npublic class %s extends AbstractOtcModuleExecutor {" + LOGGER_INIT_TEMPLATE
			+ "\n\npublic static void execute(%s %s, IndexedCollectionsDto sourceICD, "
			+ "\n%s %s, IndexedCollectionsDto targetICD," + APP_DATA_VAR;

	/** The Constant executeModuleTemplate. */
	protected static final String EXECUTE_MODULE_TEMPLATE = "\nString otcNamespace = \"%s\";"
			+ "\nexecuteModule(otcNamespace, %s, %s, data);";

	/** The Constant executeConverterTemplate. */
	protected static final String EXECUTE_CONVERTER_TEMPLATE = "\n%s.convert(%s, %s, data);";

	/** The Constant parentSourceIcdTemplate. */
	protected static final String PARENT_SOURCE_ICD_TEMPLATE = "\nIndexedCollectionsDto parentSourceICD = null;";

	/** The Constant memberSourceIcdTemplate. */
	protected static final String MEMBER_SOURCE_ICD_TEMPLATE = "\nIndexedCollectionsDto memberSourceICD = null;";

	/** The Constant parentTargetIcdTemplate. */
	protected static final String PARENT_TARGET_ICD_TEMPLATE = "\nIndexedCollectionsDto parentTargetICD = null;";

	/** The Constant memberTargetIcdTemplate. */
	protected static final String MEMBER_TARGET_ICD_TEMPLATE = "\nIndexedCollectionsDto memberTargetICD = null;";

	/** The Constant keyTargetIcdTemplate. */
	protected static final String KEY_TARGET_ICD_TEMPLATE = "\nIndexedCollectionsDto keyTargetICD = null;";

	/** The Constant valueTargetIcdTemplate. */
	protected static final String VALUE_TARGET_ICD_TEMPLATE = "\nIndexedCollectionsDto valueTargetICD = null;";

	/** The Constant keySourceIcdTemplate. */
	protected static final String KEY_SOURCE_ICD_TEMPLATE = "\nIndexedCollectionsDto keySourceICD = null;";

	/** The Constant valueSourceIcdTemplate. */
	protected static final String VALUE_SOURCE_ICD_TEMPLATE = "\nIndexedCollectionsDto valueSourceICD = null;";

	/** The Constant anchoredIcdTemplate. */
	protected static final String ANCHORED_ICD_TEMPLATE = "\nIndexedCollectionsDto anchoredICD = null;";

	/** The Constant assignKeyToMemberIcdTemplate. */
	protected static final String ASSIGN_KEY_TO_MEMBER_ICD_TEMPLATE = "\nmemberTargetICD = keyTargetICD;";

	/** The Constant assignValueToMemberIcdTemplate. */
	protected static final String ASSIGN_VALUE_TO_MEMBER_ICD_TEMPLATE = "\nmemberTargetICD = valueTargetICD;";

	/** The Constant assignParentIcdToAnchoredIcdTemplate. */
	protected static final String ASSIGN_PARENT_ICD_TO_ANCHORED_ICD_TEMPLATE = "\nanchoredICD = parentTargetICD;";

	/** The Constant assignAnchoredIcdToParentIcdTemplate. */
	protected static final String ASSIGN_ANCHORED_ICD_TO_PARENT_ICD_TEMPLATE = "\nparentTargetICD = anchoredICD;";

	protected static final String IDX_AND_LEN_TEMPLATE = "\nint idx = 0;" + "\nint len = 0;\n";
	protected static final String PRELOOP_VARS_TEMPLATE = "\nint offsetIdx = 0;";
	public static final String INCREMENT_OFFSET_IDX = "\noffsetIdx++;";
	public static final String INIT_OFFSET_IDX = "\noffsetIdx = 0;";
	protected static final String CREATE_INIT_VAR_TEMPLATE = "\n%s %s = %s;";
	protected static final String CREATE_INSTANCE_TEMPLATE = "\n%s %s = new %s();";
	protected static final String GETTER_TEMPLATE = "\n%s %s = %s.%s();";
	protected static final String HELPER_GETTER_TEMPLATE = "\n%s %s = %s.%s(%s);";
	protected static final String GET_HELPER_TEMPLATE = "\n%s.%s(%s.%s(%s));";
	protected static final String SET_HELPER_TEMPLATE = "\n%s.%s(%s, %s.%s());";
	protected static final String SET_HELPER_GET_HELPER_TEMPLATE = "\n%s.%s(%s, %s.%s(%s));";
	protected static final String SETTER_TEMPLATE = "\n%s.%s(%s);";
	protected static final String SETTER_TARGET_ENUM_TEMPLATE = "\n%s.%s(%s.valueOf(%s));";
	protected static final String SETTER_SOURCE_ENUM_TEMPLATE = "\n%s.%s(%s.toString());";
	protected static final String SETTER_BOTH_ENUM_TEMPLATE = "\n%s.%s(%s.valueOf(%s.toString()));";
	protected static final String IF_NULL_ENUM_CREATE_AND_SET_TEMPLATE = IF_S_NULL + "\n%s = %s.valueOf(%s);"
			+ SETTER_TEMPLATE + "\n}";
	protected static final String HELPER_SETTER_TEMPLATE = "\n%s.%s(%s, %s);";
	protected static final String DATE_CONVERTER_TEMPLATE = "\n%s.%s(DateConverterFacade.convert(%s, %s.class));";
	protected static final String DATE_TO_STRING_CONVERTER_TEMPLATE = "\n%s.%s(%s.toString());";
	protected static final String IF_NULL_CREATE_AND_SET_TEMPLATE = IF_S_NULL + "\n%s = new %s();"
			+ SETTER_TEMPLATE + "\n}";
	protected static final String IF_NULL_CREATE_AND_HELPER_SET_TEMPLATE = IF_S_NULL + "\n%s = new %s();"
			+ HELPER_SETTER_TEMPLATE + "\n}";
	protected static final String GETTER_IF_NULL_RETURN_TEMPLATE = GETTER_TEMPLATE + IF_S_NULL
			+ LOGGER + RETURN + "\n}";
	protected static final String GETTER_IF_NULL_CONTINUE_TEMPLATE = GETTER_TEMPLATE + IF_S_NULL
			+ LOGGER + CONTINUE + "\n}";
	protected static final String HELPER_GET_IF_NULL_RETURN_TEMPLATE = HELPER_GETTER_TEMPLATE + IF_S_NULL
			+ LOGGER + RETURN + "\n}";
	protected static final String HELPER_GET_IF_NULL_CONTINUE_TEMPLATE = HELPER_GETTER_TEMPLATE + IF_S_NULL
			+ LOGGER + CONTINUE + "\n}";
	protected static final String METHOD_END_TEMPLATE = "\nreturn %s;" + "\n}";
	protected static final String IF_NULL_TARGET_ROOT_ICD_CREATE_TEMPLATE = "\nparentTargetICD = targetICD.children.get(\"%s\");"
			+ "\nif (parentTargetICD == null) {"
			+ "\nparentTargetICD = IndexedCollectionsDtoFactory.create(targetICD, %s, \"%s\", true);" + "\n}";
	protected static final String IF_NULL_TARGET_ROOT_ICD_RETURN_TEMPLATE = "\nparentTargetICD = targetICD.children.get(\"%s\");"
			+ "\nif (parentTargetICD == null || parentTargetICD.children == null || parentTargetICD.children.size() == 0) {"
			+ LOGGER + RETURN + "\n}";
	protected static final String IF_NULL_SOURCE_ICD_RETURN_TEMPLATE = "\nif (sourceICD == null) {"
			+ "\nLOGGER.warn(\"Cannot continue! No collections present in source-object.\");" + RETURN + "\n}";
	protected static final String IF_NULL_SOURCE_ROOT_ICD_RETURN_TEMPLATE = IF_NULL_SOURCE_ICD_RETURN_TEMPLATE
			+ "\nparentSourceICD = sourceICD.children.get(\"%s\");"
			+ "\nif (parentSourceICD == null || parentSourceICD.children == null || parentSourceICD.children.size() == 0) {"
			+ LOGGER + RETURN + "\n}";
	protected static final String IF_NULL_ICD_RETURN_TEMPLATE = CHILDREN_GET
			+ "\nif (%s == null || %s.children == null || %s.children.size() == 0) {" + LOGGER
			+ RETURN + "\n}";
	protected static final String IF_NULL_LAST_ICD_RETURN_TEMPLATE = CHILDREN_GET + IF_S_NULL
			+ LOGGER + RETURN + "\n}";
	protected static final String IF_NULL_TARGET_ICD_CREATE_TEMPLATE = CHILDREN_GET + IF_S_NULL
			+ "\n%s = IndexedCollectionsDtoFactory.create(%s, %s, %s, true);" + "\n}";

//	protected static final String ifNullTargetIcdCreateOrInitTemplate = "\n%s = %s.children.get(%s);"
//			+ "\nif (%s == null) {" + "\n%s = IndexedCollectionsDtoFactory.create(%s, null, %s, true);" + ELSE
//			+ "\n%s = %s;" + "\n}";
//	protected static final String getIcdChildrenSizeTemplate = "\nint size%s = %s.children.size();";
//	protected static final String getSetTemplate = "\n%s.%s(%s.%s());";
//	protected static final String getSetTargetEnumTemplate = "\n%s.%s(%s.valueOf(%s.%s()));";
//	protected static final String getSetSourceEnumTemplate = "\n%s.%s(%s.%s().toString());";
//	protected static final String getSetBothEnumTemplate = "\n%s.%s(%s.valueOf(%s.%s().toString()));";
//	protected static final String setterTargetEnumWithAssignTemplate = "\n%s = %s.%s(%s.valueOf(%s));";
//	protected static final String assignMemberIcdToParentIcdTemplate = "\nparentTargetICD = memberTargetICD;";
//	public static final String SOURCE_ICD = "sourceICD";
//	public static final String TARGET_ICD = "targetICD";
//	protected static final String toplevelTargetPreLoopTemplate = "\nint size%s = 0;"
//			+ "\nif (%s.children.size() == 0) {" + "\nsize%s = 1;" + ELSE + "\nsize%s = %s.children.size();"
//			+ "\n}";

	/** The Constant retrieveMemberFromIcdTemplate. */
	protected static final String RETRIEVE_MEMBER_FROM_ICD_TEMPLATE = "\n%s %s = (%s) %s.indexedObject;";

	/** The Constant addToArrayTemplate. */
	protected static final String ADD_TO_ARRAY_TEMPLATE = LEN + "\nif (len < %s + 1) {"
			+ ARRAYS_COPY + SETTER_TEMPLATE + "\n}" + CREATE_INSTANCE_TEMPLATE + "\n%s[%s] = %s;";

	/** The Constant helperAddToArrayTemplate. */
	protected static final String HELPER_ADD_TO_ARRAY_TEMPLATE = LEN + "\nif (len < %s + 1) {"
			+ ARRAYS_COPY + HELPER_SETTER_TEMPLATE + CREATE_INSTANCE_TEMPLATE
			+ "\n%s[%s] = %s;";

	/** The Constant resizeArrayAndAddAtEndTemplate. */
	protected static final String RESIZE_ARRAY_AND_ADD_AT_END_TEMPLATE = LEN
			+ ARRAYS_COPY + CODE_TO_REPLACE + "\n%s[len] = %s;";

	/** The Constant addToCollectionTemplate. */
	protected static final String ADD_TO_COLLECTION_TEMPLATE = "\n%s.add(%s);";

	/** The Constant addCollectionMemberTemplate. */
	protected static final String ADD_COLLECTION_MEMBER_TEMPLATE = CREATE_INIT_VAR_TEMPLATE
			+ "\nmemberTargetICD = parentTargetICD.children.get(%s);" + "\nif (memberTargetICD != null) {"
			+ "\n%s = (%s) memberTargetICD.indexedObject;" + "\n}" + IF_S_NULL + CODE_TO_REPLACE
			+ "\nmemberTargetICD = IndexedCollectionsDtoFactory.create(parentTargetICD, %s, %s, true);" + "\n}";

	/** The Constant addCollectionMemberAtEndTemplate. */
	protected static final String ADD_COLLECTION_MEMBER_AT_END_TEMPLATE = "\nidx = 0;"
			+ "\nif (parentTargetICD.children.size() > 0) {" + "\nidx = parentTargetICD.children.size();" + "\n}"
			+ CODE_TO_REPLACE
			+ "\nmemberTargetICD = IndexedCollectionsDtoFactory.create(parentTargetICD, %s, \"\" + idx, true);";

	/** The Constant retrieveParentIcd. */
	protected static final String RETRIEVE_PARENT_ICD = "\nIndexedCollectionsDto parentICD%s = %s.children.get(%s);";

	/** The Constant preLoopTemplate. */
	protected static final String PRE_LOOP_TEMPLATE = RETRIEVE_PARENT_ICD
			+ "\nif (parentICD%s == null || parentICD%s.children == null || parentICD%s.children.size() == 0) {"
			+ LOGGER + CONTINUE + "\n}" + "\nint size%s = parentICD%s.children.size();";

	/** The Constant postLoopTemplate. */
	protected static final String POST_LOOP_TEMPLATE = "\nif (memberICD%s == null || memberICD%s.children == null || memberICD%s.children.size() == 0) {"
			+ LOGGER + CONTINUE + "\n}";

	/** The Constant lastPostSourceLoopTemplate. */
	protected static final String LAST_POST_SOURCE_LOOP_TEMPLATE = IF_MEMBER_ICD + LOGGER
			+ CONTINUE + "\n}" + RETRIEVE_MEMBER_FROM_ICD_TEMPLATE;

	/** The Constant retrieveMemberIcd. */
	protected static final String RETRIEVE_MEMBER_ICD = "\nIndexedCollectionsDto memberICD%s = %s.children.get(%s);";

	/** The Constant preTargetLoopTemplate. */
	protected static final String PRE_TARGET_LOOP_TEMPLATE = "\nint size%s = 0;"
			+ RETRIEVE_PARENT_ICD + "\nif (parentICD%s == null) {"
			+ "\nparentICD%s = IndexedCollectionsDtoFactory.create(%s, %s, %s, true);" + "\nsize%s = 1;" + ELSE
			+ "\nsize%s = parentICD%s.children.size();" + "\n}";

	/** The Constant forLoopTemplate. */
	protected static final String FOR_LOOP_TEMPLATE = "\nfor (int %s = 0; %s < size%s; %s++) {";

	/** The Constant postTargetLoopTemplate. */
	protected static final String POST_TARGET_LOOP_TEMPLATE = RETRIEVE_MEMBER_ICD
			+ "\n%s %s = null;" + "\nif (memberICD%s != null) {" + "\n%s = (%s) memberICD%s.indexedObject;"
			+ ELSE + CODE_TO_REPLACE + "\nmemberICD%s = IndexedCollectionsDtoFactory.create(%s, %s, %s, true);"
			+ "\n}";

	/** The Constant postTargetLoopMapKeyTemplate. */
	protected static final String POST_TARGET_LOOP_MAP_KEY_TEMPLATE = RETRIEVE_MEMBER_ICD
			+ "\n%s %s = null;" + IF_MEMBER_ICD + CODE_TO_CREATE_MAPKEY + " " + CODE_TO_CREATE_MAPVALUE
			+ " " + PUT + "\nmemberICD%s = IndexedCollectionsDtoFactory.create(%s, %s, %s, true);"
			+ "\nvalueTargetICD = IndexedCollectionsDtoFactory.create(%s, %s, %s, true);" + ELSE
			+ "\n%s = (%s) memberICD%s.indexedObject;" + "\n}";

	/** The Constant postTargetLoopMapValueTemplate. */
	protected static final String POST_TARGET_LOOP_MAP_VALUE_TEMPLATE = RETRIEVE_MEMBER_ICD
			+ IF_MEMBER_ICD + LOGGER + CONTINUE + "\n}"
			+ "\n%s %s = (%s) memberICD%s.indexedObject;";

	/** The Constant ifNotContainsMapKeyTemplate. */
	protected static final String IF_NOT_CONTAINS_MAP_KEY_TEMPLATE = "\nif (!%s.containsKey(%s)) {" + CODE_TO_ADD_MAPENTRY
			+ "\n}";

	/** The Constant ifNullMapKeyICDTemplate. */
	protected static final String IF_NULL_MAP_KEY_ICD_TEMPLATE = "\nif (parentTargetICD.children != null) {"
			+ KEY_TARGET_ICD + "\n}" + IF_KEY_TARGET_ICD
			+ CODE_TO_ADD_MAPENTRY + ELSE + CODE_TO_ADD_ELSE_MAPENTRY + "\n}";

	/** The Constant addMapEntryTemplate. */
	protected static final String ADD_MAP_ENTRY_TEMPLATE = CODE_TO_CREATE_MAPKEY + " " + CODE_TO_CREATE_MAPVALUE + " "
			+ PUT
			+ "\nkeyTargetICD = IndexedCollectionsDtoFactory.create(parentTargetICD, %s, %s + \"<K>\", true);"
			+ "\nvalueTargetICD = IndexedCollectionsDtoFactory.create(parentTargetICD, %s, %s + \"<V>\", true);";

	/** The Constant retrieveLastMapKeyTemplate. */
	protected static final String RETRIEVE_LAST_MAP_KEY_TEMPLATE = "\nidx = parentTargetICD.children.size() / 2 - 1;"
			+ "\nkeyTargetICD = parentTargetICD.children.get(idx + \"<K>\");"
			+ "\nvalueTargetICD = parentTargetICD.children.get(idx + \"<V>\");";

	/** The Constant ifNullMapKeyIcdReturnTemplate. */
	protected static final String IF_NULL_MAP_KEY_ICD_RETURN_TEMPLATE = KEY_TARGET_ICD
			+ IF_KEY_TARGET_ICD + LOGGER + RETURN + "\n}"
			+ "\nvalueTargetICD = parentTargetICD.children.get(%s);";

	/** The Constant ifNullMapKeyIcdContinueTemplate. */
	protected static final String IF_NULL_MAP_KEY_ICD_CONTINUE_TEMPLATE = KEY_TARGET_ICD
			+ IF_KEY_TARGET_ICD + LOGGER + CONTINUE + "\n}"
			+ "\nvalueTargetICD = parentTargetICD.children.get(%s);";

	/** The Constant addMapEntryUpdateIcdTemplate. */
	protected static final String ADD_MAP_ENTRY_UPDATE_ICD_TEMPLATE = PUT
			+ "\nvalueTargetICD.indexedObject = %s;";

	/** The Constant retrieveMapKeyFromIcdTemplate. */
	protected static final String RETRIEVE_MAP_KEY_FROM_ICD_TEMPLATE = "\n%s %s = (%s) keyTargetICD.indexedObject;";

	/** The Constant retrieveMapValueFromIcdTemplate. */
	protected static final String RETRIEVE_MAP_VALUE_FROM_ICD_TEMPLATE = "\n%s %s = (%s) valueTargetICD.indexedObject;";

	/**
	 * Fetch field type name.
	 *
	 * @param targetOCC        the target OCC
	 * @param sourceOCC        the source OCC
	 * @param otcCommandDto    the otc command dto
	 * @param createNewVarName the create new var name
	 * @param varNamesMap      the var names map
	 * @return the string
	 */
	protected static String fetchFieldTypeName(TargetOtcCommandContext targetOCC, SourceOtcCommandContext sourceOCC,
			OtcCommandDto otcCommandDto, boolean createNewVarName, Map<String, String> varNamesMap) {
		boolean isVarAlreadyCreated = (!createNewVarName &&
				varNamesMap.containsKey(otcCommandDto.enumTargetSource + otcCommandDto.tokenPath));
		String fqTypeName = "";
		if (!isVarAlreadyCreated) {
			if (otcCommandDto.fieldType.isArray()) {
				fqTypeName = otcCommandDto.fieldType.getTypeName();
			} else {
				fqTypeName = otcCommandDto.fieldType.getName();
			}
			if (fqTypeName.contains("$")) {
				fqTypeName = fqTypeName.replace("$", ".");
			}
			fqTypeName = targetOCC.factoryClassDto.addImport(fqTypeName);
			if ((otcCommandDto.isCollectionOrMap() && !otcCommandDto.isArray())) {
				String generics = null;
				OtcCommandDto memberOCD = null;
				if (TARGET_SOURCE.SOURCE == otcCommandDto.enumTargetSource) {
					memberOCD = OtcCommand.retrieveMemberOCD(sourceOCC);
				} else {
					memberOCD = OtcCommand.retrieveMemberOCD(targetOCC);
				}
				generics = memberOCD.fieldType.getName();
				if (generics.contains("$")) {
					generics = generics.replace("$", ".");
				}
				generics = targetOCC.factoryClassDto.addImport(generics);
				if (otcCommandDto.isMap()) {
					String genericKey = null;
					String genericVal = null;
					if (memberOCD.isMapKey()) {
						genericKey = generics;
						OtcCommandDto valueOCD = otcCommandDto.children
								.get(OtcConstants.MAP_VALUE_REF + otcCommandDto.fieldName);
						genericVal = valueOCD.fieldType.getName();
						if (genericVal.contains("$")) {
							genericVal = fqTypeName.replace("$", ".");
						}
						genericVal = targetOCC.factoryClassDto.addImport(genericVal);
					} else {
						genericVal = generics;
						OtcCommandDto keyOCD = otcCommandDto.children
								.get(OtcConstants.MAP_KEY_REF + otcCommandDto.fieldName);
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

	/**
	 * Fetch concrete type name.
	 *
	 * @param targetOCC     the target OCC
	 * @param otcCommandDto the otc command dto
	 * @return the string
	 */
	protected static String fetchConcreteTypeName(TargetOtcCommandContext targetOCC, OtcCommandDto otcCommandDto) {
		String clzName = otcCommandDto.concreteTypeName;
		if (clzName != null) {
			return clzName;
		}
		Class<?> fieldType = otcCommandDto.fieldType;
		if (fieldType.isArray()) {
			clzName = fieldType.getTypeName();
		} else if (!fieldType.isInterface()) {
			clzName = fieldType.getName();
		} else if (concreteTypes.containsKey(fieldType)) {
			clzName = concreteTypes.get(fieldType);
		}
		if (clzName == null) {
			return null;
		}
		if (clzName.contains("$")) {
			clzName = clzName.replace("$", ".");
		}
		clzName = targetOCC.factoryClassDto.addImport(clzName);
		if (concreteTypes.containsKey(fieldType)) {
			clzName += "<>";
		}
		return clzName;
	}

	/**
	 * Fetch sanitized type name.
	 *
	 * @param targetOCC     the target OCC
	 * @param otcCommandDto the otc command dto
	 * @return the string
	 */
	protected static String fetchSanitizedTypeName(TargetOtcCommandContext targetOCC, OtcCommandDto otcCommandDto) {
		String fieldType = otcCommandDto.fieldType.getName();
		if (fieldType.contains("$")) {
			fieldType = fieldType.replace("$", ".");
		}
		fieldType = targetOCC.factoryClassDto.addImport(fieldType);
		return fieldType;
	}

	/**
	 * Creates the var name.
	 *
	 * @param otcCommandDto    the otc command dto
	 * @param createNewVarName the create new var name
	 * @param varNamesSet      the var names set
	 * @param varNamesMap      the var names map
	 * @return the string
	 */
	protected static String createVarName(OtcCommandDto otcCommandDto, boolean createNewVarName,
			Set<String> varNamesSet, Map<String, String> varNamesMap) {
		String varName = null;
		if (!createNewVarName) {
			varName = varNamesMap.get(otcCommandDto.enumTargetSource + otcCommandDto.tokenPath);
		}
		if (varName == null) {
			if (otcCommandDto.isFirstNode) {
				if (otcCommandDto.fieldName.equals(OtcConstants.ROOT)) {
					varName = CommonUtils.initLower(otcCommandDto.declaringClass.getSimpleName());
				} else {
					varName = createVarName(otcCommandDto, varNamesSet);
					varNamesMap.put(otcCommandDto.enumTargetSource + otcCommandDto.tokenPath, varName);
				}
			} else {
				varName = createVarName(otcCommandDto, varNamesSet);
				varNamesMap.put(otcCommandDto.enumTargetSource + otcCommandDto.tokenPath, varName);
			}
		}
		return varName;
	}

	/**
	 * Creates the var name.
	 *
	 * @param otcCommandDto the otc command dto
	 * @param varNamesSet   the var names set
	 * @return the string
	 */
	protected static String createVarName(OtcCommandDto otcCommandDto, Set<String> varNamesSet) {
		String varName = otcCommandDto.fieldName;
		if (otcCommandDto.isMapKey()) {
			varName += "Key";
		} else if (otcCommandDto.isMapValue()) {
			varName += "Value";
		}
		varName = sanitizeVarName(varName, varNamesSet);
		return varName;
	}

	/**
	 * Creates the var name.
	 *
	 * @param type         the type
	 * @param varNamesSet  the var names set
	 * @param createNewVar the create new var
	 * @return the string
	 */
	protected static String createVarName(String type, Set<String> varNamesSet, boolean createNewVar) {
		String varName = CommonUtils.initLower(type);
		if (!createNewVar) {
			return varName;
		}
		varName = sanitizeVarName(varName, varNamesSet);
		return varName;
	}

	/**
	 * Sanitize var name.
	 *
	 * @param varName     the var name
	 * @param varNamesSet the var names set
	 * @return the string
	 */
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

	/**
	 * Creates the icd key.
	 *
	 * @param otcToken the otc token
	 * @return the string
	 */
	protected static String createIcdKey(String otcToken) {
		if (!otcToken.startsWith("\"")) {
			otcToken = "\"" + otcToken + "\"";
		}
		return otcToken;
	}

	/**
	 * Creates the icd key.
	 *
	 * @param memberOCD the member OCD
	 * @param idxPrefix the idx prefix
	 * @param idx       the idx
	 * @return the string
	 */
	protected static String createIcdKey(OtcCommandDto memberOCD, String idxPrefix, Integer idx) {
		if ((idx == null && idxPrefix == null) || (idx != null && idxPrefix != null)) {
			throw new CodeGeneratorException("",
					"Invalid call to the method! " + "Either one of idx or idxPrefix only is required.");
		}
		if (!memberOCD.isCollectionOrMapMember()) {
			throw new CodeGeneratorException("", "Invalid call to the method! " + "Not a Collection / Map member.");
		}
		String mapKeyValueRef = null;
		if (memberOCD.isMapMember()) {
			if (memberOCD.isMapKey()) {
				mapKeyValueRef = OtcConstants.MAP_KEY_REF;
			} else {
				mapKeyValueRef = OtcConstants.MAP_VALUE_REF;
			}
		}
		String icdKey;
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

	/**
	 * Fetch value or var.
	 *
	 * @param targetOCC        the target OCC
	 * @param sourceOCD        the source OCD
	 * @param value            the value
	 * @param createNewVarName the create new var name
	 * @param varNamesSet      the var names set
	 * @param varNamesMap      the var names map
	 * @return the string
	 */
	protected static String fetchValueOrVar(TargetOtcCommandContext targetOCC, OtcCommandDto sourceOCD, String value,
			boolean createNewVarName, Set<String> varNamesSet, Map<String, String> varNamesMap) {
		OtcCommandDto memberOCD = targetOCC.otcCommandDto;
		String valOrVar = null;
		if (targetOCC.isLeaf()) {
			if (value == null) {
				if (sourceOCD == null) {
					throw new CodeGeneratorException("", INVALID_CALL_TO_TEMPLATE
							+ targetOCC.commandId + "! Both value and SourceOCD cannot be null for a leaf-token.");
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

	/**
	 * Creates the convert expression.
	 *
	 * @param otcCommandDto the otc command dto
	 * @param value         the value
	 * @return the string
	 */
	protected static String createConvertExpression(OtcCommandDto otcCommandDto, String value) {
		Class<?> clz = otcCommandDto.fieldType;
		String expr = null;
		if (otcCommandDto.isEnum()) {
			expr = otcCommandDto.fieldType.getSimpleName() + ".valueOf(\"" + value + "\")";
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

	protected static String addInlineComments(String inlineComments, String generatedCode) {
		if (CommonUtils.isTrimmedAndEmpty(generatedCode)) {
			return null;
		}
		return inlineComments + generatedCode;
	}
}
