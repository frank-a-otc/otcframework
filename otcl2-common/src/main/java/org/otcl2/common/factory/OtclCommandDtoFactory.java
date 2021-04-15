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
package org.otcl2.common.factory;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.otcl2.common.OtclConstants;
import org.otcl2.common.OtclConstants.TARGET_SOURCE;
import org.otcl2.common.dto.OtclCommandDto;
import org.otcl2.common.dto.OtclCommandDto.CollectionDescriptor;
import org.otcl2.common.exception.OtclException;
import org.otcl2.common.util.CommonUtils;
import org.otcl2.common.util.OtclUtils;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating OtclCommandDto objects.
 */
public class OtclCommandDtoFactory {

	/**
	 * Creates the.
	 *
	 * @param enumTargetOrSource the enum target or source
	 * @param otclToken the otcl token
	 * @param tokenPath the token path
	 * @param idx the idx
	 * @param fldName the fld name
	 * @param concreteType the concrete type
	 * @param isRootNode the is root node
	 * @param field the field
	 * @param fldType the fld type
	 * @param genericType the generic type
	 * @param isLeaf the is leaf
	 * @return the otcl command dto
	 */
	public static OtclCommandDto create(TARGET_SOURCE enumTargetOrSource, String otclToken,
			String tokenPath, int idx, String fldName, String concreteType, boolean isRootNode, Field field,
			Class<?> fldType, Class<?> genericType, boolean isLeaf) {
		OtclCommandDto.Builder builder = OtclCommandDto.newBuilder()
				.addTargetOrSource(enumTargetOrSource) 
				.addOtclToken(otclToken)
				.addTokenPath(tokenPath)
				.addOtclTokenIndex(idx)
				.addFieldName(fldName)
				.addIsRootNode(isRootNode)
				.addField(field)
				.addFieldType(fldType)
				.addConcreteType(genericType);
		OtclCommandDto otclCommandDto = builder.build();
		if (!isLeaf) {
			otclCommandDto.children = new HashMap<>();
		}
		return otclCommandDto;
	}
	
	
	/**
	 * Creates a new OtclCommandDto object.
	 *
	 * @param id the id
	 * @param otclCommandDto the otcl command dto
	 * @param otclChain the otcl chain
	 * @param otclTokens the otcl tokens
	 */
	public static void createMembers(String id, OtclCommandDto otclCommandDto, String otclChain,
			String[] otclTokens) {
		if (!otclCommandDto.hasCollectionNotation && !otclCommandDto.hasMapNotation) {
			return;
		}
		int idx = otclCommandDto.otclTokenIndex;
		boolean isLeaf = (idx == otclTokens.length - 1 ? true : false);
		if (otclCommandDto.hasCollectionNotation) {
			createCollectionMember(otclCommandDto, isLeaf);
		} else if (otclCommandDto.hasMapNotation) {
			createMapMember(id, otclCommandDto, otclChain, otclTokens, isLeaf);
		}
		if (isLeaf && otclCommandDto.isCollectionOrMap()) {
			if (otclCommandDto.children == null) {
				otclCommandDto.children = new LinkedHashMap<>();
			}
		}
	}
	
	/**
	 * Creates a new OtclCommandDto object.
	 *
	 * @param otclCommandDto the otcl command dto
	 * @param isLeaf the is leaf
	 * @return the otcl command dto
	 */
	public static OtclCommandDto createCollectionMember(OtclCommandDto otclCommandDto, boolean isLeaf) {
		OtclCommandDto memberOCD = otclCommandDto.children.get(otclCommandDto.fieldName);
		if (memberOCD != null) {
			return memberOCD;
		}
		Field field = otclCommandDto.field;
		Class<?> fieldType = field.getType();
		if (List.class.isAssignableFrom(fieldType)) {
			otclCommandDto.collectionDescriptor = CollectionDescriptor.LIST;
		} else if (Set.class.isAssignableFrom(fieldType)) {
			otclCommandDto.collectionDescriptor = CollectionDescriptor.SET;
		} else if (Queue.class.isAssignableFrom(fieldType)) {
			otclCommandDto.collectionDescriptor = CollectionDescriptor.QUEUE;
		} else if (fieldType.isArray()) {
			otclCommandDto.collectionDescriptor = CollectionDescriptor.ARRAY;
		}
		Class<?> memberFieldType = null;
		Type parameterizedType = field.getGenericType();
		if (otclCommandDto.isArray()) {
			String memberFieldTypeName = fieldType.getComponentType().getName();
			memberFieldType = OtclUtils.loadClass(memberFieldTypeName);
		} else {
			memberFieldType = (Class<?>) ((ParameterizedType) parameterizedType).getActualTypeArguments()[0];
		}
		String memberOtclToken = otclCommandDto.fieldName;
		memberOCD = OtclCommandDtoFactory.create(otclCommandDto.enumTargetSource, memberOtclToken, otclCommandDto.tokenPath,
				otclCommandDto.otclTokenIndex, otclCommandDto.fieldName, otclCommandDto.concreteTypeName, false, null,
				memberFieldType, null, isLeaf);
		memberOCD.tokenPath = CommonUtils.replaceLast(memberOCD.tokenPath, OtclConstants.ARR_REF, "");
		memberOCD.collectionDescriptor = CollectionDescriptor.COLLECTION_MEMBER;
		otclCommandDto.addChild(memberOCD);
		memberOCD.parent = otclCommandDto;
		return memberOCD;
	}

	/**
	 * Creates a new OtclCommandDto object.
	 *
	 * @param id the id
	 * @param otclCommandDto the otcl command dto
	 * @param otclChain the otcl chain
	 * @param otclTokens the otcl tokens
	 * @param isLeaf the is leaf
	 * @return the otcl command dto
	 */
	public static OtclCommandDto createMapMember(String id, OtclCommandDto otclCommandDto,
			String otclChain, String[] otclTokens, boolean isLeaf) {
		String otclToken = otclTokens[otclCommandDto.otclTokenIndex];
		String memberOtclToken = null;
		boolean isKey = false;
		if (otclToken.contains(OtclConstants.MAP_KEY_REF)) {
			memberOtclToken = OtclConstants.MAP_KEY_REF + otclCommandDto.fieldName;
			isKey = true;
		} else if (otclToken.contains(OtclConstants.MAP_VALUE_REF)) {
			memberOtclToken = OtclConstants.MAP_VALUE_REF + otclCommandDto.fieldName;
		}
		if (memberOtclToken == null) {
			throw new OtclException("", "Oops... OTCL-token didn't pass Semantics-checker in Script-block : " +
					id + " - <K> / <V> notation missing.");
		}
		OtclCommandDto memberOCD = otclCommandDto.children.get(memberOtclToken);
		if (memberOCD != null) {
			return memberOCD;
		}
		Field field = otclCommandDto.field;
		Type parameterizedType = field.getGenericType();
		otclCommandDto.collectionDescriptor = CollectionDescriptor.MAP;
		OtclCommandDto mainOCD = null;
		OtclCommandDto keyMemberOCD = createMapMember(otclCommandDto, parameterizedType, isLeaf, true);
		OtclCommandDto valueMemberOCD = createMapMember(otclCommandDto, parameterizedType, isLeaf, false);
		String chainPath = otclCommandDto.tokenPath;
		if (chainPath.contains(OtclConstants.MAP_KEY_REF)) {
			chainPath = chainPath.replace(OtclConstants.MAP_KEY_REF, "");
		} else if (chainPath.contains(OtclConstants.MAP_VALUE_REF)) {
			chainPath = chainPath.replace(OtclConstants.MAP_VALUE_REF, "");
		}
		otclCommandDto.tokenPath = chainPath;
		if (isKey) {
			mainOCD = keyMemberOCD;
		} else {
			mainOCD = valueMemberOCD;
		}
		return mainOCD;
	}

	/**
	 * Creates a new OtclCommandDto object.
	 *
	 * @param otclCommandDto the otcl command dto
	 * @param parameterizedType the parameterized type
	 * @param isLeaf the is leaf
	 * @param isKey the is key
	 * @return the otcl command dto
	 */
	public static OtclCommandDto createMapMember(OtclCommandDto otclCommandDto, Type parameterizedType, boolean isLeaf,
			boolean isKey) {
		Class<?> memberOtclGenericTypeClz = null;
		String memberConcreteType = null;
		String memberOtclToken = null;
		if (isKey) {
			memberOtclToken = OtclConstants.MAP_KEY_REF + otclCommandDto.fieldName;
			memberConcreteType = otclCommandDto.mapKeyConcreteType;
		} else {
			memberOtclToken = OtclConstants.MAP_VALUE_REF + otclCommandDto.fieldName;
			memberConcreteType = otclCommandDto.mapValueConcreteType;
		}
		if (!CommonUtils.isEmpty(memberConcreteType)) {
			memberOtclGenericTypeClz = OtclUtils.loadClass(memberConcreteType);
		}
		OtclCommandDto memberOCD = otclCommandDto.children.get(memberOtclToken);
		if (memberOCD == null) {
			Class<?> memberType = null;
			if (isKey) {
				memberType = (Class<?>) ((ParameterizedType) parameterizedType).getActualTypeArguments()[0];
			} else {
				memberType = (Class<?>) ((ParameterizedType) parameterizedType).getActualTypeArguments()[1];
			}
			memberOCD = OtclCommandDtoFactory.create(otclCommandDto.enumTargetSource, memberOtclToken,
					otclCommandDto.tokenPath, otclCommandDto.otclTokenIndex, otclCommandDto.fieldName, memberConcreteType,
					false, null, memberType, memberOtclGenericTypeClz, isLeaf);
			memberOCD.tokenPath = CommonUtils.replaceLast(memberOCD.tokenPath, OtclConstants.MAP_KEY_REF, "");
			memberOCD.tokenPath = CommonUtils.replaceLast(memberOCD.tokenPath, OtclConstants.MAP_VALUE_REF, "");
			if (isKey) {
				memberOCD.collectionDescriptor = CollectionDescriptor.MAP_KEY;
				memberOCD.tokenPath = memberOCD.tokenPath + OtclConstants.MAP_KEY_REF;
			} else {
				memberOCD.collectionDescriptor = CollectionDescriptor.MAP_VALUE;
				memberOCD.tokenPath = memberOCD.tokenPath + OtclConstants.MAP_VALUE_REF;
			}
			otclCommandDto.addChild(memberOCD);
			memberOCD.parent = otclCommandDto;
		} else {
			memberOCD.concreteTypeName = memberConcreteType;
			memberOCD.concreteType = memberOtclGenericTypeClz;
		}
		return memberOCD;
	}

}
