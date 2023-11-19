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
package org.otcframework.common.factory;

import org.otcframework.common.OtcConstants;
import org.otcframework.common.OtcConstants.TARGET_SOURCE;
import org.otcframework.common.dto.OtcCommandDto;
import org.otcframework.common.dto.OtcCommandDto.CollectionDescriptor;
import org.otcframework.common.exception.OtcException;
import org.otcframework.common.util.CommonUtils;
import org.otcframework.common.util.OtcUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * A factory for creating OtcCommandDto objects.
 */
public class OtcCommandDtoFactory {

	private OtcCommandDtoFactory() {}
	/**
	 * Creates the.
	 *
	 * @param commandId          the command id
	 * @param enumTargetOrSource the enum target or source
	 * @param otcToken           the otc token
	 * @param tokenPath          the token path
	 * @param idx                the idx
	 * @param fldName            the fld name
	 * @param isFirstNode        the is first node
	 * @param field              the field
	 * @param fldType            the fld type
	 * @param genericType        the generic type
	 * @param isLeaf             the is leaf
	 * @return the otc command dto
	 */
	public static OtcCommandDto create(String commandId, TARGET_SOURCE enumTargetOrSource, String otcToken,
			String tokenPath, int idx, String fldName, boolean isFirstNode, Field field,
			Class<?> fldType, Class<?> genericType, boolean isLeaf) {
		OtcCommandDto.Builder builder = OtcCommandDto.newBuilder().addCommandId(commandId)
				.addTargetOrSource(enumTargetOrSource).addOtcToken(otcToken).addTokenPath(tokenPath)
				.addOtcTokenIndex(idx).addFieldName(fldName).addIsFirstNode(isFirstNode).addField(field)
				.addFieldType(fldType).addConcreteType(genericType);
		OtcCommandDto otcCommandDto = builder.build();
		if (!isLeaf) {
			otcCommandDto.children = new HashMap<>();
		}
		return otcCommandDto;
	}

	/**
	 * Creates a new OtcCommandDto object.
	 *
	 * @param commandId     the command id
	 * @param otcCommandDto the otc command dto
	 * @param otcTokens     the otc tokens
	 */
	public static void createMembers(String commandId, OtcCommandDto otcCommandDto,
			String[] otcTokens) {
		if (!otcCommandDto.hasCollectionNotation && !otcCommandDto.hasMapNotation) {
			return;
		}
		int idx = otcCommandDto.otcTokenIndex;
		boolean isLeaf = idx == otcTokens.length - 1;
		if (otcCommandDto.hasCollectionNotation) {
			createCollectionMember(commandId, otcCommandDto, isLeaf);
		} else {
			createMapMember(commandId, otcCommandDto, otcTokens, isLeaf);
		}
		if (isLeaf && otcCommandDto.isCollectionOrMap() && otcCommandDto.children == null) {
			otcCommandDto.children = new LinkedHashMap<>();
		}
	}

	/**
	 * Creates a new OtcCommandDto object.
	 *
	 * @param commandId     the command id
	 * @param otcCommandDto the otc command dto
	 * @param isLeaf        the is leaf
	 * @return the otc command dto
	 */
	public static OtcCommandDto createCollectionMember(String commandId, OtcCommandDto otcCommandDto, boolean isLeaf) {
		OtcCommandDto memberOCD = otcCommandDto.children.get(otcCommandDto.fieldName);
		if (memberOCD != null) {
			return memberOCD;
		}
		Field field = otcCommandDto.field;
		Class<?> fieldType = field.getType();
		if (List.class.isAssignableFrom(fieldType)) {
			otcCommandDto.collectionDescriptor = CollectionDescriptor.LIST;
		} else if (Set.class.isAssignableFrom(fieldType)) {
			otcCommandDto.collectionDescriptor = CollectionDescriptor.SET;
		} else if (Queue.class.isAssignableFrom(fieldType)) {
			otcCommandDto.collectionDescriptor = CollectionDescriptor.QUEUE;
		} else if (fieldType.isArray()) {
			otcCommandDto.collectionDescriptor = CollectionDescriptor.ARRAY;
		}
		Class<?> memberFieldType = null;
		Type parameterizedType = field.getGenericType();
		if (otcCommandDto.isArray()) {
			String memberFieldTypeName = fieldType.getComponentType().getName();
			memberFieldType = OtcUtils.loadClass(memberFieldTypeName);
		} else {
			memberFieldType = (Class<?>) ((ParameterizedType) parameterizedType).getActualTypeArguments()[0];
		}
		String memberOtcToken = otcCommandDto.fieldName;
		memberOCD = OtcCommandDtoFactory.create(commandId, otcCommandDto.enumTargetSource, memberOtcToken,
				otcCommandDto.tokenPath, otcCommandDto.otcTokenIndex, otcCommandDto.fieldName,
				false, null, memberFieldType, null, isLeaf);
		memberOCD.tokenPath = CommonUtils.replaceLast(memberOCD.tokenPath, OtcConstants.ARR_REF, "");
		memberOCD.collectionDescriptor = CollectionDescriptor.COLLECTION_MEMBER;
		otcCommandDto.addChild(memberOCD);
		memberOCD.parent = otcCommandDto;
		return memberOCD;
	}

	/**
	 * Creates a new OtcCommandDto object.
	 *
	 * @param commandId     the command id
	 * @param otcCommandDto the otc command dto
	 * @param otcTokens     the otc tokens
	 * @param isLeaf        the is leaf
	 * @return the otc command dto
	 */
	public static OtcCommandDto createMapMember(String commandId, OtcCommandDto otcCommandDto,
			String[] otcTokens, boolean isLeaf) {
		String otcToken = otcTokens[otcCommandDto.otcTokenIndex];
		String memberOtcToken = null;
		boolean isKey = false;
		if (otcToken.contains(OtcConstants.MAP_KEY_REF)) {
			memberOtcToken = OtcConstants.MAP_KEY_REF + otcCommandDto.fieldName;
			isKey = true;
		} else if (otcToken.contains(OtcConstants.MAP_VALUE_REF)) {
			memberOtcToken = OtcConstants.MAP_VALUE_REF + otcCommandDto.fieldName;
		}
		if (memberOtcToken == null) {
			throw new OtcException("", "Oops... OTC-token didn't pass Semantics-checker in OTC-Command-Id : "
					+ commandId + " - <K> / <V> notation missing.");
		}
		OtcCommandDto memberOCD = otcCommandDto.children.get(memberOtcToken);
		if (memberOCD != null) {
			return memberOCD;
		}
		Field field = otcCommandDto.field;
		Type parameterizedType = field.getGenericType();
		otcCommandDto.collectionDescriptor = CollectionDescriptor.MAP;
		OtcCommandDto mainOCD = null;
		OtcCommandDto keyMemberOCD = createMapMember(commandId, otcCommandDto, parameterizedType, isLeaf, true);
		OtcCommandDto valueMemberOCD = createMapMember(commandId, otcCommandDto, parameterizedType, isLeaf, false);
		String chainPath = otcCommandDto.tokenPath;
		if (chainPath.contains(OtcConstants.MAP_KEY_REF)) {
			chainPath = chainPath.replace(OtcConstants.MAP_KEY_REF, "");
		} else if (chainPath.contains(OtcConstants.MAP_VALUE_REF)) {
			chainPath = chainPath.replace(OtcConstants.MAP_VALUE_REF, "");
		}
		otcCommandDto.tokenPath = chainPath;
		if (isKey) {
			mainOCD = keyMemberOCD;
		} else {
			mainOCD = valueMemberOCD;
		}
		return mainOCD;
	}

	/**
	 * Creates a new OtcCommandDto object.
	 *
	 * @param commandId         the command id
	 * @param otcCommandDto     the otc command dto
	 * @param parameterizedType the parameterized type
	 * @param isLeaf            the is leaf
	 * @param isKey             the is key
	 * @return the otc command dto
	 */
	public static OtcCommandDto createMapMember(String commandId, OtcCommandDto otcCommandDto, Type parameterizedType,
			boolean isLeaf, boolean isKey) {
		Class<?> memberOtcGenericTypeClz = null;
		String memberConcreteType = null;
		String memberOtcToken = null;
		if (isKey) {
			memberOtcToken = OtcConstants.MAP_KEY_REF + otcCommandDto.fieldName;
			memberConcreteType = otcCommandDto.mapKeyConcreteType;
		} else {
			memberOtcToken = OtcConstants.MAP_VALUE_REF + otcCommandDto.fieldName;
			memberConcreteType = otcCommandDto.mapValueConcreteType;
		}
		if (!CommonUtils.isTrimmedAndEmpty(memberConcreteType)) {
			memberOtcGenericTypeClz = OtcUtils.loadClass(memberConcreteType);
		}
		OtcCommandDto memberOCD = otcCommandDto.children.get(memberOtcToken);
		if (memberOCD == null) {
			Class<?> memberType = null;
			if (isKey) {
				memberType = (Class<?>) ((ParameterizedType) parameterizedType).getActualTypeArguments()[0];
			} else {
				memberType = (Class<?>) ((ParameterizedType) parameterizedType).getActualTypeArguments()[1];
			}
			memberOCD = OtcCommandDtoFactory.create(commandId, otcCommandDto.enumTargetSource, memberOtcToken,
					otcCommandDto.tokenPath, otcCommandDto.otcTokenIndex, otcCommandDto.fieldName,
					false, null, memberType, memberOtcGenericTypeClz, isLeaf);
			memberOCD.tokenPath = CommonUtils.replaceLast(memberOCD.tokenPath, OtcConstants.MAP_KEY_REF, "");
			memberOCD.tokenPath = CommonUtils.replaceLast(memberOCD.tokenPath, OtcConstants.MAP_VALUE_REF, "");
			if (isKey) {
				memberOCD.collectionDescriptor = CollectionDescriptor.MAP_KEY;
				memberOCD.tokenPath = memberOCD.tokenPath + OtcConstants.MAP_KEY_REF;
			} else {
				memberOCD.collectionDescriptor = CollectionDescriptor.MAP_VALUE;
				memberOCD.tokenPath = memberOCD.tokenPath + OtcConstants.MAP_VALUE_REF;
			}
			otcCommandDto.addChild(memberOCD);
			memberOCD.parent = otcCommandDto;
		} else {
			memberOCD.concreteTypeName = memberConcreteType;
			memberOCD.concreteType = memberOtcGenericTypeClz;
		}
		return memberOCD;
	}
}
