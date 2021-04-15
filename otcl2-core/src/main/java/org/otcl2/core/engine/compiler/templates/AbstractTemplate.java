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

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractTemplate.
 */
public abstract class AbstractTemplate {

	/** The Constant fromTypes. */
	private static final Set<Class<?>> fromTypes = new HashSet<>(8);
	
	/** The Constant convertFromStringExpressions. */
	private static final Map<Class<?>, String> convertFromStringExpressions = new IdentityHashMap<>(10);
	
	/** The Constant toTypeConvertExpressions. */
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

	/** The Constant SOURCE_ICD. */
	public static final String SOURCE_ICD = "sourceICD";
	
	/** The Constant PARENT_SOURCE_ICD. */
	public static final String PARENT_SOURCE_ICD = "parentSourceICD";
	
	/** The Constant MEMBER_SOURCE_ICD. */
	public static final String MEMBER_SOURCE_ICD = "memberSourceICD";
	
	/** The Constant TARGET_ICD. */
	public static final String TARGET_ICD = "targetICD";
	
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

	/** The Constant mainClassBeginCodeTemplate. */
	protected static final String mainClassBeginCodeTemplate = 
			"// This file was generated by the OTCL Engine. \r\n"
			+ "// See <a href=\"https://otclfoundation.org\">https://otclfoundation.org</a> \r\n"
			+ "// Any modifications to this file will be lost upon recompilation of the respective OTCL file. \r\n"
			+ "//\r\n"
			+ "\npackage %s;\n"
			+ "\nimport org.otcl2.common.engine.profiler.dto.IndexedCollectionsDto;"
			+ "\nimport org.otcl2.common.executor.CodeExecutor;"
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

	/** The Constant loggerInitTemplate. */
	protected static final String loggerInitTemplate = "\n\nprivate static final Logger LOGGER = "
			+ "LoggerFactory.getLogger(%s.class); ";

	/** The Constant factoryClassBeginCodeTemplate. */
	protected static final String factoryClassBeginCodeTemplate = 
			"// This file was generated by the OTCL Engine. \r\n"
			+ "// See <a href=\"https://otclfoundation.org\">https://otclfoundation.org</a> \r\n"
			+ "// Any modifications to this file will be lost upon recompilation of the respective OTCL file. \r\n"
			+ "//\r\n"
			+ "\npackage %s;\n"
			+ "\nimport org.otcl2.common.engine.profiler.dto.IndexedCollectionsDto;"
			+ "\n" + OtclCommand.CODE_TO_IMPORT
			+ "\n\npublic class %s {"
			+ loggerInitTemplate
			+ "\n\npublic static void execute(%s %s, IndexedCollectionsDto sourceICD, "
			+ "\n%s %s, IndexedCollectionsDto targetICD, "
			+ "\nMap<String, Object> data) {";

	/** The Constant executeFactoryMethodCallTemplate. */
	protected static final String executeFactoryMethodCallTemplate = "\n%s.execute(%s, %s, %s, targetICD, data);";

	/** The Constant factoryModuleClassBeginCodeTemplate. */
	protected static final String factoryModuleClassBeginCodeTemplate = 
			"// This file was generated by the OTCL Engine. \r\n"
			+ "// See <a href=\"https://otclfoundation.org\">https://otclfoundation.org</a> \r\n"
			+ "// Any modifications to this file will be lost upon recompilation of the respective OTCL file. \r\n"
			+ "//\r\n"
			+ "\npackage %s;\n"
			+ "\nimport org.otcl2.common.engine.profiler.dto.IndexedCollectionsDto;"
			+ "\nimport org.otcl2.core.engine.module.AbstractOtclModuleExecutor;"
			+ "\n" + OtclCommand.CODE_TO_IMPORT
			+ "\n\npublic class %s extends AbstractOtclModuleExecutor {"
			+ loggerInitTemplate
			+ "\n\npublic static void execute(%s %s, IndexedCollectionsDto sourceICD, "
			+ "\n%s %s, IndexedCollectionsDto targetICD,"
			+ "\nMap<String, Object> data) {";

	/** The Constant executeModuleTemplate. */
	protected static final String executeModuleTemplate = "\nString otclNamespace = \"%s\";"
			+ "\nexecuteModule(otclNamespace, %s, %s, data);";

	/** The Constant executeConverterTemplate. */
	protected static final String executeConverterTemplate = "\n%s.convert(%s, %s, data);";
	
	/** The Constant parentSourceIcdTemplate. */
	protected static final String parentSourceIcdTemplate = "\nIndexedCollectionsDto parentSourceICD = null;";
	
	/** The Constant memberSourceIcdTemplate. */
	protected static final String memberSourceIcdTemplate = "\nIndexedCollectionsDto memberSourceICD = null;";
	
	/** The Constant parentTargetIcdTemplate. */
	protected static final String parentTargetIcdTemplate = "\nIndexedCollectionsDto parentTargetICD = null;";
	
	/** The Constant memberTargetIcdTemplate. */
	protected static final String memberTargetIcdTemplate = "\nIndexedCollectionsDto memberTargetICD = null;";

	/** The Constant keyTargetIcdTemplate. */
	protected static final String keyTargetIcdTemplate = "\nIndexedCollectionsDto keyTargetICD = null;";
	
	/** The Constant valueTargetIcdTemplate. */
	protected static final String valueTargetIcdTemplate = "\nIndexedCollectionsDto valueTargetICD = null;";
	
	/** The Constant keySourceIcdTemplate. */
	protected static final String keySourceIcdTemplate = "\nIndexedCollectionsDto keySourceICD = null;";
	
	/** The Constant valueSourceIcdTemplate. */
	protected static final String valueSourceIcdTemplate = "\nIndexedCollectionsDto valueSourceICD = null;";
	
	/** The Constant anchoredIcdTemplate. */
	protected static final String anchoredIcdTemplate = "\nIndexedCollectionsDto anchoredICD = null;";

	/** The Constant assignKeyToMemberIcdTemplate. */
	protected static final String assignKeyToMemberIcdTemplate = "\nmemberTargetICD = keyTargetICD;";
	
	/** The Constant assignValueToMemberIcdTemplate. */
	protected static final String assignValueToMemberIcdTemplate = "\nmemberTargetICD = valueTargetICD;";
	
	/** The Constant assignMemberIcdToParentIcdTemplate. */
	protected static final String assignMemberIcdToParentIcdTemplate = "\nparentTargetICD = memberTargetICD;";
	
	/** The Constant assignParentIcdToAnchoredIcdTemplate. */
	protected static final String assignParentIcdToAnchoredIcdTemplate = "\nanchoredICD = parentTargetICD;";
	
	/** The Constant assignAnchoredIcdToParentIcdTemplate. */
	protected static final String assignAnchoredIcdToParentIcdTemplate = "\nparentTargetICD = anchoredICD;";

	/** The Constant idxAndLenTemplate. */
	protected static final String idxAndLenTemplate = "\nint idx = 0;"
			+ "\nint len = 0;\n";
	
	/** The Constant preloopVarsTemplate. */
	protected static final String preloopVarsTemplate = "\nint offsetIdx = 0;";
	
	/** The Constant incrementOffsetIdx. */
	public static final String incrementOffsetIdx = "\noffsetIdx++;";
	
	/** The Constant initOffsetIdx. */
	public static final String initOffsetIdx = "\noffsetIdx = 0;";

	/** The Constant createInitVarTemplate. */
	protected static final String createInitVarTemplate = "\n%s %s = %s;";
	
	/** The Constant createInstanceTemplate. */
	protected static final String createInstanceTemplate = "\n%s %s = new %s();";
	
	/** The Constant getterTemplate. */
	protected static final String getterTemplate = "\n%s %s = %s.%s();";
	
	/** The Constant helperGetterTemplate. */
	protected static final String helperGetterTemplate = "\n%s %s = %s.%s(%s);";
	
	/** The Constant getSetTemplate. */
	protected static final String getSetTemplate = "\n%s.%s(%s.%s());";
	
	/** The Constant getHelperTemplate. */
	protected static final String getHelperTemplate = "\n%s.%s(%s.%s(%s));";
	
	/** The Constant setHelperTemplate. */
	protected static final String setHelperTemplate = "\n%s.%s(%s, %s.%s());";
	
	/** The Constant setHelperGetHelperTemplate. */
	protected static final String setHelperGetHelperTemplate = "\n%s.%s(%s, %s.%s(%s));";

	/** The Constant getSetTargetEnumTemplate. */
	protected static final String getSetTargetEnumTemplate = "\n%s.%s(%s.valueOf(%s.%s()));";
	
	/** The Constant getSetSourceEnumTemplate. */
	protected static final String getSetSourceEnumTemplate = "\n%s.%s(%s.%s().toString());";
	
	/** The Constant getSetBothEnumTemplate. */
	protected static final String getSetBothEnumTemplate = "\n%s.%s(%s.valueOf(%s.%s().toString()));";

	/** The Constant setterTemplate. */
	protected static final String setterTemplate = "\n%s.%s(%s);";
	
	/** The Constant setterTargetEnumTemplate. */
	protected static final String setterTargetEnumTemplate = "\n%s.%s(%s.valueOf(%s));";
	
	/** The Constant setterSourceEnumTemplate. */
	protected static final String setterSourceEnumTemplate = "\n%s.%s(%s.toString());";
	
	/** The Constant setterBothEnumTemplate. */
	protected static final String setterBothEnumTemplate = "\n%s.%s(%s.valueOf(%s.toString()));";

	/** The Constant helperSetterTemplate. */
	protected static final String helperSetterTemplate = "\n%s.%s(%s, %s);";

	/** The Constant dateConverterTemplate. */
	protected static final String dateConverterTemplate = "\n%s.%s(DateConverterFacade.convert(%s, %s.class));";
	
	/** The Constant dateToStringConverterTemplate. */
	protected static final String dateToStringConverterTemplate = "\n%s.%s(%s.toString());";

	/** The Constant ifNullCreateAndSetTemplate. */
	protected static final String ifNullCreateAndSetTemplate = "\nif (%s == null) {" 
			+ "\n%s = new %s();"
			+ "\n%s.%s(%s);" 
			+ "\n}";

	/** The Constant ifNullCreateAndHelperSetTemplate. */
	protected static final String ifNullCreateAndHelperSetTemplate = "\nif (%s == null) {" 
			+ "\n%s = new %s();"
			+ "\n%s.%s(%s, %s);" 
			+ "\n}";


	/** The Constant getterIfNullReturnTemplate. */
	protected static final String getterIfNullReturnTemplate = getterTemplate
			+ "\nif (%s == null) {" 
			+ "\nLOGGER.%s(\"%s\");"
			+ "\nreturn;" 
			+ "\n}";

	/** The Constant getterIfNullContinueTemplate. */
	protected static final String getterIfNullContinueTemplate = getterTemplate
			+ "\nif (%s == null) {" 
			+ "\nLOGGER.%s(\"%s\");"
			+ "\ncontinue;" 
			+ "\n}";

	/** The Constant helperGetIfNullReturnTemplate. */
	protected static final String helperGetIfNullReturnTemplate = helperGetterTemplate
			+ "\nif (%s == null) {" 
			+ "\nLOGGER.%s(\"%s\");"
			+ "\nreturn;" 
			+ "\n}";

	/** The Constant helperGetIfNullContinueTemplate. */
	protected static final String helperGetIfNullContinueTemplate = helperGetterTemplate
			+ "\nif (%s == null) {" 
			+ "\nLOGGER.%s(\"%s\");"
			+ "\ncontinue;" 
			+ "\n}";

	/** The Constant methodEndTemplate. */
	protected static final String methodEndTemplate = "\nreturn %s;"
			+ "\n}";

	/** The Constant ifNullTargetRootIcdCreateTemplate. */
	protected static final String ifNullTargetRootIcdCreateTemplate = "\nparentTargetICD = targetICD.children.get(\"%s\");"
			+ "\nif (parentTargetICD == null) {"
			+ "\nparentTargetICD = IndexedCollectionsDtoFactory.create(targetICD, %s, \"%s\", true);"
			+ "\n}";

	/** The Constant ifNullTargetRootIcdReturnTemplate. */
	protected static final String ifNullTargetRootIcdReturnTemplate = "\nparentTargetICD = targetICD.children.get(\"%s\");"
			+ "\nif (parentTargetICD == null || parentTargetICD.children == null || parentTargetICD.children.size() == 0) {"
			+ "\nLOGGER.%s(\"%s\");"
			+ "\nreturn;"
			+ "\n}";

	/** The Constant ifNullSourceIcdReturnTemplate. */
	protected static final String ifNullSourceIcdReturnTemplate = "\nif (sourceICD == null) {"
			+ "\nLOGGER.warn(\"Cannot continue! No collections present in source-object.\");"
			+ "\nreturn;" 
			+ "\n}";
	
	/** The Constant ifNullSourceRootIcdReturnTemplate. */
	protected static final String ifNullSourceRootIcdReturnTemplate = ifNullSourceIcdReturnTemplate
			+ "\nparentSourceICD = sourceICD.children.get(\"%s\");"
			+ "\nif (parentSourceICD == null || parentSourceICD.children == null || parentSourceICD.children.size() == 0) {"
			+ "\nLOGGER.%s(\"%s\");"
			+ "\nreturn;"
			+ "\n}";

	/** The Constant ifNullIcdReturnTemplate. */
	protected static final String ifNullIcdReturnTemplate = 
			"\n%s = %s.children.get(%s);"
			+ "\nif (%s == null || %s.children == null || %s.children.size() == 0) {"
			+ "\nLOGGER.%s(\"%s\");"
			+ "\nreturn;"
			+ "\n}";

	/** The Constant ifNullLastIcdReturnTemplate. */
	protected static final String ifNullLastIcdReturnTemplate = 
			"\n%s = %s.children.get(%s);"
			+ "\nif (%s == null) {"
			+ "\nLOGGER.%s(\"%s\");"
			+ "\nreturn;"
			+ "\n}";

	/** The Constant ifNullTargetIcdCreateTemplate. */
	protected static final String ifNullTargetIcdCreateTemplate =  "\n%s = %s.children.get(%s);"
			+ "\nif (%s == null) {"
			+ "\n%s = IndexedCollectionsDtoFactory.create(%s, %s, %s, true);"
			+ "\n}";
	
	/** The Constant ifNullTargetIcdCreateOrInitTemplate. */
	protected static final String ifNullTargetIcdCreateOrInitTemplate = "\n%s = %s.children.get(%s);"
			+ "\nif (%s == null) {"
			+ "\n%s = IndexedCollectionsDtoFactory.create(%s, null, %s, true);"
			+ "\n} else {"
			+ "\n%s = %s;"
			+ "\n}";

	/** The Constant retrieveMemberFromIcdTemplate. */
	protected static final String retrieveMemberFromIcdTemplate = "\n%s %s = (%s) %s.profiledObject;";

	/** The Constant addToArrayTemplate. */
	protected static final String addToArrayTemplate = "\nlen = %s.length;"
			+ "\nif (len < %s + 1) {"
			+ "\n%s = Arrays.copyOf(%s, len + 1);"
			+ "\n%s.%s(%s);"
			+ "\n}"
			+ createInstanceTemplate
			+ "\n%s[%s] = %s;";
	
	/** The Constant helperAddToArrayTemplate. */
	protected static final String helperAddToArrayTemplate = "\nlen = %s.length;"
			+ "\nif (len < %s + 1) {"
			+ "\n%s = Arrays.copyOf(%s, len + 1);"
			+ "\n%s.%s(%s, %s);"
			+ "\n}"
			+ createInstanceTemplate
			+ "\n%s[%s] = %s;";
	
	/** The Constant resizeArrayAndAddAtEndTemplate. */
	protected static final String resizeArrayAndAddAtEndTemplate = "\nlen = %s.length;"
			+ "\n%s = Arrays.copyOf(%s, len + 1);"
			+ CODE_TO_REPLACE
			+ "\n%s[len] = %s;";
		
	/** The Constant addToCollectionTemplate. */
	protected static final String addToCollectionTemplate = "\n%s.add(%s);";

	/** The Constant addCollectionMemberTemplate. */
	protected static final String addCollectionMemberTemplate = createInitVarTemplate
			+ "\nmemberTargetICD = parentTargetICD.children.get(%s);"
			+ "\nif (memberTargetICD != null) {"
			+ "\n%s = (%s) memberTargetICD.profiledObject;"
			+ "\n}"
			+ "\nif (%s == null) {"
			+ CODE_TO_REPLACE
			+ "\nmemberTargetICD = IndexedCollectionsDtoFactory.create(parentTargetICD, %s, %s, true);"
			+ "\n}";

	/** The Constant addCollectionMemberAtEndTemplate. */
	protected static final String addCollectionMemberAtEndTemplate = "\nidx = 0;" 
			+ "\nif (parentTargetICD.children.size() > 0) {" 
			+ "\nidx = parentTargetICD.children.size();" 
			+ "\n}" 
			+ CODE_TO_REPLACE
			+ "\nmemberTargetICD = IndexedCollectionsDtoFactory.create(parentTargetICD, %s, \"\" + idx, true);";

	/** The Constant getIcdChildrenSizeTemplate. */
	protected static final String getIcdChildrenSizeTemplate = "\nint size%s = %s.children.size();";

	/** The Constant retrieveParentIcd. */
	protected static final String retrieveParentIcd = "\nIndexedCollectionsDto parentICD%s = %s.children.get(%s);";

	/** The Constant preLoopTemplate. */
	protected static final String preLoopTemplate = retrieveParentIcd
			+ "\nif (parentICD%s == null || parentICD%s.children == null || parentICD%s.children.size() == 0) {"
			+ "\nLOGGER.%s(\"%s\");"
			+ "\ncontinue;"
			+ "\n}"
			+ "\nint size%s = parentICD%s.children.size();";

	/** The Constant postLoopTemplate. */
	protected static final String postLoopTemplate = 
			"\nif (memberICD%s == null || memberICD%s.children == null || memberICD%s.children.size() == 0) {"
			+ "\nLOGGER.%s(\"%s\");"
			+ "\ncontinue;"
			+ "\n}";
	
	/** The Constant lastPostSourceLoopTemplate. */
	protected static final String lastPostSourceLoopTemplate = 
			"\nif (memberICD%s == null) {"
			+ "\nLOGGER.%s(\"%s\");"
			+ "\ncontinue;"
			+ "\n}"
			+ retrieveMemberFromIcdTemplate;

	/** The Constant retrieveMemberIcd. */
	protected static final String retrieveMemberIcd = "\nIndexedCollectionsDto memberICD%s = %s.children.get(%s);";
	
	/** The Constant toplevelTargetPreLoopTemplate. */
	protected static final String toplevelTargetPreLoopTemplate = "\nint size%s = 0;"
			+ "\nif (%s.children.size() == 0) {"
			+ "\nsize%s = 1;"
			+ "\n} else {"
			+ "\nsize%s = %s.children.size();"
			+ "\n}";

	/** The Constant preTargetLoopTemplate. */
	protected static final String preTargetLoopTemplate = "\nint size%s = 0;"
			+ "\nIndexedCollectionsDto parentICD%s = %s.children.get(%s);"
			+ "\nif (parentICD%s == null) {"
			+ "\nparentICD%s = IndexedCollectionsDtoFactory.create(%s, %s, %s, true);"
			+ "\nsize%s = 1;"
			+ "\n} else {"
			+ "\nsize%s = parentICD%s.children.size();"
			+ "\n}";

	/** The Constant forLoopTemplate. */
	protected static final String forLoopTemplate = "\nfor (int %s = 0; %s < size%s; %s++) {";
	
	/** The Constant postTargetLoopTemplate. */
	protected static final String postTargetLoopTemplate = 
			"\nIndexedCollectionsDto memberICD%s = %s.children.get(%s);"
			+ "\n%s %s = null;"
			+ "\nif (memberICD%s != null) {"
			+ "\n%s = (%s) memberICD%s.profiledObject;"
			+ "\n} else {"
			+ CODE_TO_REPLACE
			+ "\nmemberICD%s = IndexedCollectionsDtoFactory.create(%s, %s, %s, true);"
			+ "\n}";

	/** The Constant postTargetLoopMapKeyTemplate. */
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

	/** The Constant postTargetLoopMapValueTemplate. */
	protected static final String postTargetLoopMapValueTemplate = 
			"\nIndexedCollectionsDto memberICD%s = %s.children.get(%s);"
			+ "\nif (memberICD%s == null) {" 
			+ "\nLOGGER.%s(\"%s\");"
			+ "\ncontinue;"
			+ "\n}"
			+ "\n%s %s = (%s) memberICD%s.profiledObject;";

	/** The Constant ifNotContainsMapKeyTemplate. */
	protected static final String ifNotContainsMapKeyTemplate = "\nif (!%s.containsKey(%s)) {" 
			+ CODE_TO_ADD_MAPENTRY
			+ "\n}";

	/** The Constant ifNullMapKeyICDTemplate. */
	protected static final String ifNullMapKeyICDTemplate = "\nif (parentTargetICD.children != null) {" 
			+ "\nkeyTargetICD = parentTargetICD.children.get(%s);"
			+ "\n}"
			+ "\nif (keyTargetICD == null) {" 
			+ CODE_TO_ADD_MAPENTRY
			+ "\n} else {"
			+ CODE_TO_ADD_ELSE_MAPENTRY
			+ "\n}";

	/** The Constant addMapEntryTemplate. */
	protected static final String addMapEntryTemplate = CODE_TO_CREATE_MAPKEY + " "
			+ CODE_TO_CREATE_MAPVALUE + " "
			+ "\n%s.put(%s, %s);"
			+ "\nkeyTargetICD = IndexedCollectionsDtoFactory.create(parentTargetICD, %s, %s + \"<K>\", true);" 
			+ "\nvalueTargetICD = IndexedCollectionsDtoFactory.create(parentTargetICD, %s, %s + \"<V>\", true);";

	/** The Constant retrieveLastMapKeyTemplate. */
	protected static final String retrieveLastMapKeyTemplate = "\nidx = parentTargetICD.children.size() / 2 - 1;" 
			+ "\nkeyTargetICD = parentTargetICD.children.get(idx + \"<K>\");"
			+ "\nvalueTargetICD = parentTargetICD.children.get(idx + \"<V>\");";

	/** The Constant ifNullMapKeyIcdReturnTemplate. */
	protected static final String ifNullMapKeyIcdReturnTemplate = "\nkeyTargetICD = parentTargetICD.children.get(%s);"
			+ "\nif (keyTargetICD == null) {" 
			+ "\nLOGGER.%s(\"%s\");"
			+ "\nreturn;"
			+ "\n}"
			+ "\nvalueTargetICD = parentTargetICD.children.get(%s);";

	/** The Constant ifNullMapKeyIcdReturnTemplate. */
	protected static final String ifNullMapKeyIcdContinueTemplate = "\nkeyTargetICD = parentTargetICD.children.get(%s);"
			+ "\nif (keyTargetICD == null) {" 
			+ "\nLOGGER.%s(\"%s\");"
			+ "\ncontinue;"
			+ "\n}"
			+ "\nvalueTargetICD = parentTargetICD.children.get(%s);";

	/** The Constant addMapEntryUpdateIcdTemplate. */
	protected static final String addMapEntryUpdateIcdTemplate = "\n%s.put(%s, %s);"
			+ "\nvalueTargetICD.profiledObject = %s;";

	/** The Constant retrieveMapKeyFromIcdTemplate. */
	protected static final String retrieveMapKeyFromIcdTemplate = "\n%s %s = (%s) keyTargetICD.profiledObject;";
	
	/** The Constant retrieveMapValueFromIcdTemplate. */
	protected static final String retrieveMapValueFromIcdTemplate = "\n%s %s = (%s) valueTargetICD.profiledObject;";

	/**
	 * Fetch field type name.
	 *
	 * @param targetOCC the target OCC
	 * @param sourceOCC the source OCC
	 * @param otclCommandDto the otcl command dto
	 * @param createNewVarName the create new var name
	 * @param varNamesMap the var names map
	 * @return the string
	 */
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
	

	/**
	 * Fetch concrete type name.
	 *
	 * @param targetOCC the target OCC
	 * @param otclCommandDto the otcl command dto
	 * @return the string
	 */
	protected static String fetchConcreteTypeName(TargetOtclCommandContext targetOCC, OtclCommandDto otclCommandDto) {
//		String clzName = null;
//		if (otclCommandDto.concreteTypeName != null) {
//			clzName = otclCommandDto.concreteTypeName;
//			if (clzName != null) {
//				return clzName;
//			}
//		}
		String clzName = otclCommandDto.concreteTypeName;
		if (clzName != null) {
			return clzName;
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
		if (clzName == null) {
			return null;
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

	/**
	 * Fetch sanitized type name.
	 *
	 * @param targetOCC the target OCC
	 * @param otclCommandDto the otcl command dto
	 * @return the string
	 */
	protected static String fetchSanitizedTypeName(TargetOtclCommandContext targetOCC, OtclCommandDto otclCommandDto) {
		String fieldType = otclCommandDto.fieldType.getName();
		if (fieldType.contains("$")) {
			fieldType = fieldType.replace("$", ".");
		}
		fieldType = targetOCC.factoryClassDto.addImport(fieldType);
		return fieldType;
	}
	
	/**
	 * Creates the var name.
	 *
	 * @param otclCommandDto the otcl command dto
	 * @param createNewVarName the create new var name
	 * @param varNamesSet the var names set
	 * @param varNamesMap the var names map
	 * @return the string
	 */
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
	
	/**
	 * Creates the var name.
	 *
	 * @param otclCommandDto the otcl command dto
	 * @param varNamesSet the var names set
	 * @return the string
	 */
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
	
	/**
	 * Creates the var name.
	 *
	 * @param type the type
	 * @param varNamesSet the var names set
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
	 * @param varName the var name
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
	 * @param otclToken the otcl token
	 * @return the string
	 */
	protected static String createIcdKey(String otclToken) {
		if (!otclToken.startsWith("\"")) {
			otclToken = "\"" + otclToken + "\"";
		}
		return otclToken;
	}
	
	/**
	 * Creates the icd key.
	 *
	 * @param memberOCD the member OCD
	 * @param idxPrefix the idx prefix
	 * @param idx the idx
	 * @return the string
	 */
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
	
	/**
	 * Fetch value or var.
	 *
	 * @param targetOCC the target OCC
	 * @param sourceOCD the source OCD
	 * @param value the value
	 * @param createNewVarName the create new var name
	 * @param varNamesSet the var names set
	 * @param varNamesMap the var names map
	 * @return the string
	 */
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
	
	/**
	 * Creates the convert expression.
	 *
	 * @param otclCommandDto the otcl command dto
	 * @param value the value
	 * @return the string
	 */
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

	/**
	 * Creates the convert expression.
	 *
	 * @param targetOCC the target OCC
	 * @param sourceOCC the source OCC
	 * @param createNewVarName the create new var name
	 * @param varNamesSet the var names set
	 * @param varNamesMap the var names map
	 * @return the string
	 */
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
