/**
* Copyright (c) otcframework.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*
* This file is part of the OTC framework.
* 
*  The OTC framework is free software: you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  the Free Software Foundation, version 3 of the License.
*
*  The OTC framework is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU General Public License for more details.
*
*  A copy of the GNU General Public License is made available as 'License.md' file, 
*  along with OTC framework project.  If not, see <https://www.gnu.org/licenses/>.
*
*/
package org.otcframework.common.factory;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.otcframework.common.OtcConstants;
import org.otcframework.common.OtcConstants.TARGET_SOURCE;
import org.otcframework.common.dto.OtcCommandDto;
import org.otcframework.common.dto.OtcCommandDto.CollectionDescriptor;
import org.otcframework.common.exception.OtcException;
import org.otcframework.common.util.CommonUtils;
import org.otcframework.common.util.OtcUtils;

/**
 * A factory for creating OtcCommandDto objects.
 */
// TODO: Auto-generated Javadoc
public class OtcCommandDtoFactory {

	/**
	 * Creates the.
	 *
	 * @param commandId          the command id
	 * @param enumTargetOrSource the enum target or source
	 * @param otcToken           the otc token
	 * @param tokenPath          the token path
	 * @param idx                the idx
	 * @param fldName            the fld name
	 * @param concreteType       the concrete type
	 * @param isFirstNode        the is first node
	 * @param field              the field
	 * @param fldType            the fld type
	 * @param genericType        the generic type
	 * @param isLeaf             the is leaf
	 * @return the otc command dto
	 */
	public static OtcCommandDto create(String commandId, TARGET_SOURCE enumTargetOrSource, String otcToken,
			String tokenPath, int idx, String fldName, String concreteType, boolean isFirstNode, Field field,
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
	 * @param otcChain      the otc chain
	 * @param otcTokens     the otc tokens
	 */
	public static void createMembers(String commandId, OtcCommandDto otcCommandDto, String otcChain,
			String[] otcTokens) {
		if (!otcCommandDto.hasCollectionNotation && !otcCommandDto.hasMapNotation) {
			return;
		}
		int idx = otcCommandDto.otcTokenIndex;
		boolean isLeaf = (idx == otcTokens.length - 1 ? true : false);
		if (otcCommandDto.hasCollectionNotation) {
			createCollectionMember(commandId, otcCommandDto, isLeaf);
		} else if (otcCommandDto.hasMapNotation) {
			createMapMember(commandId, otcCommandDto, otcChain, otcTokens, isLeaf);
		}
		if (isLeaf && otcCommandDto.isCollectionOrMap()) {
			if (otcCommandDto.children == null) {
				otcCommandDto.children = new LinkedHashMap<>();
			}
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
				otcCommandDto.concreteTypeName, false, null, memberFieldType, null, isLeaf);
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
	 * @param otcChain      the otc chain
	 * @param otcTokens     the otc tokens
	 * @param isLeaf        the is leaf
	 * @return the otc command dto
	 */
	public static OtcCommandDto createMapMember(String commandId, OtcCommandDto otcCommandDto, String otcChain,
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
		if (!CommonUtils.isEmpty(memberConcreteType)) {
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
					otcCommandDto.tokenPath, otcCommandDto.otcTokenIndex, otcCommandDto.fieldName, memberConcreteType,
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
