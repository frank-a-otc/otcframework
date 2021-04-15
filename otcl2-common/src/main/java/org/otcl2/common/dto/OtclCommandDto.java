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

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.otcl2.common.OtclConstants.TARGET_SOURCE;

import com.fasterxml.jackson.annotation.JsonIgnore;

// TODO: Auto-generated Javadoc
/**
 * The Class OtclCommandDto.
 */
public class OtclCommandDto {

	/**
	 * The Enum CollectionDescriptor.
	 */
	public enum CollectionDescriptor {
		
		/** The normal. */
		NORMAL("None"), 
		
		/** The array. */
		ARRAY("Array"), 
		
		/** The list. */
		LIST("List"), 
		
		/** The set. */
		SET("Set"), 
		
		/** The queue. */
		QUEUE("Queue"),
		
		/** The collection member. */
		COLLECTION_MEMBER("Collection.Member"), 
		
		/** The map. */
		MAP("Map"), 
		
		/** The map key. */
		MAP_KEY("Map.Key"), 
		
		/** The map value. */
		MAP_VALUE("Map.Value");

		/** The value. */
		private final String value;

		/**
		 * Instantiates a new collection descriptor.
		 *
		 * @param value the value
		 */
		CollectionDescriptor(String value) {
			this.value = value;
		}

		/**
		 * Checks if is normal.
		 *
		 * @return true, if is normal
		 */
		public boolean isNormal() {
			return (NORMAL.value == this.value);
		}

		/**
		 * Checks if is collection.
		 *
		 * @return true, if is collection
		 */
		public boolean isCollection() {
			return (ARRAY.value == this.value || LIST.value == this.value || SET.value == this.value
					|| QUEUE.value == this.value);
		}

		/**
		 * Checks if is array.
		 *
		 * @return true, if is array
		 */
		public boolean isArray() {
			return (ARRAY.value == this.value);
		}

		/**
		 * Checks if is collection member.
		 *
		 * @return true, if is collection member
		 */
		public boolean isCollectionMember() {
			return (COLLECTION_MEMBER.value == this.value);
		}

		/**
		 * Checks if is map.
		 *
		 * @return true, if is map
		 */
		public boolean isMap() {
			return (MAP.value == this.value);
		}

		/**
		 * Checks if is map key.
		 *
		 * @return true, if is map key
		 */
		public boolean isMapKey() {
			return (MAP_KEY.value == this.value);
		}

		/**
		 * Checks if is map value.
		 *
		 * @return true, if is map value
		 */
		public boolean isMapValue() {
			return (MAP_VALUE.value == this.value);
		}
	}

	/**
	 * Instantiates a new otcl command dto.
	 */
	private OtclCommandDto() {
	}

	/** The token path. */
	public String tokenPath;
	
	/** The enum target source. */
	public TARGET_SOURCE enumTargetSource;
	
	/** The otcl token. */
	public String otclToken;
	
	/** The otcl token index. */
	public int otclTokenIndex;
	
	/** The is root node. */
	public boolean isRootNode;
	
	/** The has collection notation. */
	public boolean hasCollectionNotation;
	
	/** The has map notation. */
	public boolean hasMapNotation;
	
	/** The collection descriptor. */
	public CollectionDescriptor collectionDescriptor = CollectionDescriptor.NORMAL;
	
	/** The concrete type name. */
	public String concreteTypeName;
	
	/** The map key concrete type. */
	public String mapKeyConcreteType;
	
	/** The map value concrete type. */
	public String mapValueConcreteType;
	
	/** The field name. */
	public String fieldName;
	
	/** The field. */
	public Field field;
	
	/** The enable factory helper getter. */
	public boolean enableFactoryHelperGetter;
	
	/** The enable factory helper setter. */
	public boolean enableFactoryHelperSetter;
	
	/** The is getter initialized. */
	public boolean isGetterInitialized;
	
	/** The is setter initialized. */
	public boolean isSetterInitialized;
	
	/** The getter. */
	public String getter;
	
	/** The setter. */
	public String setter;
	
	/** The field type. */
	public Class<?> fieldType;
	
	/** The concrete type. */
	public Class<?> concreteType;
	
	/** The declaring class. */
	public Class<?> declaringClass;
	
	/** The parent. */
	public OtclCommandDto parent;
	
	/** The children. */
	public Map<String, OtclCommandDto> children;

	/**
	 * Instantiates a new otcl command dto.
	 *
	 * @param builder the builder
	 */
	private OtclCommandDto(Builder builder) {
		enumTargetSource = builder.enumTargetSource;
		otclToken = builder.otclToken;
		tokenPath = builder.tokenPath;
		otclTokenIndex = builder.otclTokenIndex;
		isRootNode = builder.isRootNode;
		collectionDescriptor = builder.collectionDescriptor;
		concreteTypeName = builder.concreteTypeName;
		fieldName = builder.fieldName;
		field = builder.field;
		fieldType = builder.fieldType;
		concreteType = builder.concreteType;
		parent = builder.parent;
		children = builder.children;
	}

	/**
	 * New builder.
	 *
	 * @return the builder
	 */
	public static Builder newBuilder() {
		return new Builder() {
			@Override
			public OtclCommandDto build() {
				return new OtclCommandDto(this);
			}
		};
	}

	/**
	 * Adds the child.
	 *
	 * @param otclCommandDto the otcl command dto
	 */
	public void addChild(OtclCommandDto otclCommandDto) {
		if (children == null) {
			children = new HashMap<>();
		}
		children.put(otclCommandDto.otclToken, otclCommandDto);
	}

    /**
     * Checks if is enum.
     *
     * @return true, if is enum
     */
    @JsonIgnore
    public boolean isEnum() {
		return fieldType.isEnum();
	}

    /**
     * Checks if is normal.
     *
     * @return true, if is normal
     */
    @JsonIgnore
	public boolean isNormal() {
		return collectionDescriptor.isNormal();
	}

    /**
     * Checks if is collection or map.
     *
     * @return true, if is collection or map
     */
    @JsonIgnore
	public boolean isCollectionOrMap() {
		return isCollection() || isMap();
	}

    /**
     * Checks if is collection.
     *
     * @return true, if is collection
     */
    @JsonIgnore
	public boolean isCollection() {
		return collectionDescriptor.isCollection();
	}

    /**
     * Checks if is array.
     *
     * @return true, if is array
     */
    @JsonIgnore
	public boolean isArray() {
		return collectionDescriptor.isArray();
	}

    /**
     * Checks if is collection or map member.
     *
     * @return true, if is collection or map member
     */
    @JsonIgnore
	public boolean isCollectionOrMapMember() {
		return isCollectionMember() || isMapMember();
	}

    /**
     * Checks if is collection member.
     *
     * @return true, if is collection member
     */
    @JsonIgnore
	public boolean isCollectionMember() {
		return collectionDescriptor.isCollectionMember();
	}

    /**
     * Checks if is map.
     *
     * @return true, if is map
     */
    @JsonIgnore
	public boolean isMap() {
		return collectionDescriptor.isMap();
	}

    /**
     * Checks if is map key.
     *
     * @return true, if is map key
     */
    @JsonIgnore
	public boolean isMapKey() {
		return collectionDescriptor.isMapKey();
	}

    /**
     * Checks if is map value.
     *
     * @return true, if is map value
     */
    @JsonIgnore
	public boolean isMapValue() {
		return collectionDescriptor.isMapValue();
	}

    /**
     * Checks if is map member.
     *
     * @return true, if is map member
     */
    @JsonIgnore
	public boolean isMapMember() {
		return collectionDescriptor.isMapKey() || collectionDescriptor.isMapValue();
	}

//	public boolean isKeyPath(String[] otclTokens) {
//		return otclTokens[otclTokenIndex].contains(OtclConstants.MAP_KEY_REF);
//	}
//
//	public boolean isLeafParent(String[] otclTokens) {
//		if (otclTokenIndex == otclTokens.length - 2) {
//			if (collectionDescriptor.isCollection() || collectionDescriptor.isMap()) {
//				return false;
//			}
//			String otclToken = otclTokens[otclTokenIndex + 1];
//			OtclCommandDto otclCommandDto = children.get(otclToken);
//			if (otclCommandDto.collectionDescriptor.isNormal()) {
//				return true;
//			}
//			return false;
//		} else if (otclTokenIndex == otclTokens.length - 1) {
//			if (collectionDescriptor.isCollection() || collectionDescriptor.isMap()) {
//				return true;
//			}
//		}
//		return false;
//	}
//
//	public boolean isLeaf(String[] otclTokens) {
//		if (otclTokenIndex >= otclTokens.length - 1) {
//			if (collectionDescriptor.isNormal() || collectionDescriptor.isMapKey() || collectionDescriptor.isMapValue()
//					|| collectionDescriptor.isCollectionMember()) {
//				return true;
//			}
//		}
//		return false;
//	}
//
//	public boolean hasAncestralCollectionOrMap(String[] otclTokens) {
//		if (otclTokens.length == 1 || isRootNode) {
//			return false;
//		}
//		int startIdx = otclTokenIndex - 1;
//		for (int idx = startIdx; idx >= 0; idx--) {
//			String otclToken = otclTokens[idx];
//			if (otclToken.contains(OtclConstants.OPEN_BRACKET)) {
//				return true;
//			}
//		}
//		return false;
//	}
//
//	public boolean hasDescendantCollectionOrMap(String[] otclTokens) {
//		if (otclTokens.length == 1) {
//			return false;
//		}
//		int startIdx = otclTokenIndex + 1;
//		for (int idx = startIdx; idx < otclTokens.length; idx++) {
//			String otclToken = otclTokens[idx];
//			if (otclToken.contains(OtclConstants.OPEN_BRACKET)) {
//				return true;
//			}
//		}
//		return false;
//	}
//
//	public boolean hasChildren(String[] otclTokens) {
//		if (isCollectionOrMap()) {
//			return true;
//		}
//		return children != null && otclTokens.length > otclTokenIndex + 1;
//	}
//	
//	public boolean hasMapValueDescendant(String[] rawOtclTokens) {
//		if (rawOtclTokens.length == 1) {
//			return false;
//		}
//		int startIdx = otclTokenIndex + 1;
//		for (int idx = startIdx; idx < rawOtclTokens.length; idx++) {
//			String otclToken = rawOtclTokens[idx];
//			if (otclToken.contains(OtclConstants.MAP_VALUE_REF)) {
//				return true;
//			}
//		}
//		return false;
//	}
//
//	public boolean hasMapValueMember(String[] rawOtclTokens) {
//		if (rawOtclTokens[otclTokenIndex].contains(OtclConstants.MAP_VALUE_REF)) {
//			return true;
//		}
//		return false;
//	}
//
//	public boolean hasAnchoredDescendant(String[] otclTokens) {
//		if (otclTokens.length == 1) {
//			return false;
//		}
//		int startIdx = otclTokenIndex + 1;
//		for (int idx = startIdx; idx < otclTokens.length; idx++) {
//			String otclToken = otclTokens[idx];
//			if (otclToken.contains(OtclConstants.ANCHOR)) {
//				return true;
//			}
//		}
//		return false;
//	}
//
////	public boolean hasAnchor(String otclChain) {
////		if (otclChain.contains(OtclConstants.ANCHOR)) {
////			return true;
////		}
////		return false;
////	}
//
//	public boolean isAnchored(String[] otclTokens) {
//		String otclToken = otclTokens[otclTokenIndex];
//		if (otclToken.contains(OtclConstants.ANCHOR)) {
//			return true;
//		}
//		return false;
//	}
//
//	public boolean isPreAnchored(String[] otclTokens) {
//		String otclToken = otclTokens[otclTokenIndex];
//		if (otclToken.contains(OtclConstants.PRE_ANCHOR) || otclToken.contains(OtclConstants.MAP_PRE_ANCHOR)) {
//			return true;
//		}
//		return false;
//	}
//
//	public boolean isPostAnchored(String[] otclTokens) {
//		String otclToken = otclTokens[otclTokenIndex];
//		if (otclToken.contains(OtclConstants.POST_ANCHOR) || otclToken.contains(OtclConstants.MAP_POST_ANCHOR)) {
//			return true;
//		}
//		return false;
//	}
//
//	public boolean hasEndedCollectionInChain(String otclChain) {
//		if (otclChain.endsWith(OtclConstants.CLOSE_BRACKET) || otclChain.endsWith(OtclConstants.MAP_KEY_REF)
//				|| otclChain.endsWith(OtclConstants.MAP_VALUE_REF)) {
//			return true;
//		}
//		return false;
//	}
//
//	public int descendantsCollectionsCount(String[] otclTokens) {
//		if (otclTokens.length == 1) {
//			return 0;
//		}
//		int descendantsCollectionsCount = 0;
//		int startIdx = otclTokenIndex + 1;
//		for (int idx = startIdx; idx < otclTokens.length; idx++) {
//			String otclToken = otclTokens[idx];
//			if (otclToken.contains(OtclConstants.OPEN_BRACKET) && !(otclToken.contains(OtclConstants.MAP_BEGIN_REF)
//					|| otclToken.contains(OtclConstants.MAP_PRE_ANCHOR))) {
//				descendantsCollectionsCount++;
//			}
//		}
//		return descendantsCollectionsCount;
//	}
//
//	public int descendantsMapsCount(String[] otclTokens) {
//		if (otclTokens.length == 1) {
//			return 0;
//		}
//		int descendantsMapssCount = 0;
//		int startIdx = otclTokenIndex + 1;
//		for (int idx = startIdx; idx < otclTokens.length; idx++) {
//			String otclToken = otclTokens[idx];
//			if (otclToken.contains(OtclConstants.MAP_BEGIN_REF) || otclToken.contains(OtclConstants.MAP_PRE_ANCHOR)) {
//				descendantsMapssCount++;
//			}
//		}
//		return descendantsMapssCount;
//	}

	/**
 * To string.
 *
 * @return the string
 */
@Override
	public String toString() {
		return "OtclCommandDto [tokenPath=" + tokenPath + ", enumTargetSource=" + enumTargetSource + ", otclToken="
				+ otclToken + ", otclTokenIndex=" + otclTokenIndex + ", isRootNode=" + isRootNode
				+ ", hasCollectionNotation=" + hasCollectionNotation + ", hasMapNotation=" + hasMapNotation
				+ ", collectionDescriptor=" + collectionDescriptor + ", concreteTypeName=" + concreteTypeName
				+ ", mapKeyConcreteType=" + mapKeyConcreteType + ", mapValueConcreteType=" + mapValueConcreteType
				+ ", fieldName=" + fieldName + ", field=" + field + ", enableFactoryHelperGetter="
				+ enableFactoryHelperGetter + ", enableFactoryHelperSetter=" + enableFactoryHelperSetter
				+ ", isGetterInitialized=" + isGetterInitialized + ", isSetterInitialized=" + isSetterInitialized
				+ ", getter=" + getter + ", setter=" + setter + ", fieldType=" + fieldType + ", concreteType="
				+ concreteType + "]";
	}

	/**
	 * Hash code.
	 *
	 * @return the int
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((children == null) ? 0 : children.hashCode());
		result = prime * result + ((collectionDescriptor == null) ? 0 : collectionDescriptor.hashCode());
		result = prime * result + ((concreteTypeName == null) ? 0 : concreteTypeName.hashCode());
		result = prime * result + (enableFactoryHelperGetter ? 1231 : 1237);
		result = prime * result + (enableFactoryHelperSetter ? 1231 : 1237);
		result = prime * result + ((enumTargetSource == null) ? 0 : enumTargetSource.hashCode());
		result = prime * result + ((field == null) ? 0 : field.hashCode());
		result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
		result = prime * result + ((getter == null) ? 0 : getter.hashCode());
		result = prime * result + (hasCollectionNotation ? 1231 : 1237);
		result = prime * result + (hasMapNotation ? 1231 : 1237);
		result = prime * result + (isGetterInitialized ? 1231 : 1237);
		result = prime * result + (isRootNode ? 1231 : 1237);
		result = prime * result + (isSetterInitialized ? 1231 : 1237);
		result = prime * result + ((mapKeyConcreteType == null) ? 0 : mapKeyConcreteType.hashCode());
		result = prime * result + ((mapValueConcreteType == null) ? 0 : mapValueConcreteType.hashCode());
		result = prime * result + ((otclToken == null) ? 0 : otclToken.hashCode());
		result = prime * result + otclTokenIndex;
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		result = prime * result + ((setter == null) ? 0 : setter.hashCode());
		result = prime * result + ((tokenPath == null) ? 0 : tokenPath.hashCode());
		return result;
	}

	/**
	 * Equals.
	 *
	 * @param obj the obj
	 * @return true, if successful
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OtclCommandDto other = (OtclCommandDto) obj;
		if (children == null) {
			if (other.children != null)
				return false;
		} else if (!children.equals(other.children))
			return false;
		if (collectionDescriptor != other.collectionDescriptor)
			return false;
		if (concreteTypeName == null) {
			if (other.concreteTypeName != null)
				return false;
		} else if (!concreteTypeName.equals(other.concreteTypeName))
			return false;
		if (enableFactoryHelperGetter != other.enableFactoryHelperGetter)
			return false;
		if (enableFactoryHelperSetter != other.enableFactoryHelperSetter)
			return false;
		if (enumTargetSource != other.enumTargetSource)
			return false;
		if (field == null) {
			if (other.field != null)
				return false;
		} else if (!field.equals(other.field))
			return false;
		if (fieldName == null) {
			if (other.fieldName != null)
				return false;
		} else if (!fieldName.equals(other.fieldName))
			return false;
		if (getter == null) {
			if (other.getter != null)
				return false;
		} else if (!getter.equals(other.getter))
			return false;
		if (hasCollectionNotation != other.hasCollectionNotation)
			return false;
		if (hasMapNotation != other.hasMapNotation)
			return false;
		if (isGetterInitialized != other.isGetterInitialized)
			return false;
		if (isRootNode != other.isRootNode)
			return false;
		if (isSetterInitialized != other.isSetterInitialized)
			return false;
		if (mapKeyConcreteType == null) {
			if (other.mapKeyConcreteType != null)
				return false;
		} else if (!mapKeyConcreteType.equals(other.mapKeyConcreteType))
			return false;
		if (mapValueConcreteType == null) {
			if (other.mapValueConcreteType != null)
				return false;
		} else if (!mapValueConcreteType.equals(other.mapValueConcreteType))
			return false;
		if (otclToken == null) {
			if (other.otclToken != null)
				return false;
		} else if (!otclToken.equals(other.otclToken))
			return false;
		if (otclTokenIndex != other.otclTokenIndex)
			return false;
		if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals(other.parent))
			return false;
		if (setter == null) {
			if (other.setter != null)
				return false;
		} else if (!setter.equals(other.setter))
			return false;
		if (tokenPath == null) {
			if (other.tokenPath != null)
				return false;
		} else if (!tokenPath.equals(other.tokenPath))
			return false;
		return true;
	}

	/**
	 * The Class Builder.
	 */
	public abstract static class Builder {
		
		/** The enum target source. */
		private TARGET_SOURCE enumTargetSource;
		
		/** The otcl token. */
		private String otclToken;
		
		/** The token path. */
		private String tokenPath;
		
		/** The otcl token index. */
		private int otclTokenIndex;
		
		/** The is root node. */
		private boolean isRootNode;
		
		/** The collection descriptor. */
		private CollectionDescriptor collectionDescriptor = CollectionDescriptor.NORMAL;
		
		/** The concrete type name. */
		private String concreteTypeName;
		
		/** The field name. */
		private String fieldName;
		
		/** The field. */
		private Field field;
		
		/** The field type. */
		private Class<?> fieldType;
		
		/** The concrete type. */
		private Class<?> concreteType;
		
		/** The parent. */
		private OtclCommandDto parent;
		
		/** The children. */
		private Map<String, OtclCommandDto> children;

		/**
		 * Builds the.
		 *
		 * @return the otcl command dto
		 */
		public abstract OtclCommandDto build();

		/**
		 * Adds the target or source.
		 *
		 * @param enumTargetOrSource the enum target or source
		 * @return the builder
		 */
		public Builder addTargetOrSource(TARGET_SOURCE enumTargetOrSource) {
			this.enumTargetSource = enumTargetOrSource;
			return this;
		}

		/**
		 * Adds the otcl token.
		 *
		 * @param otclToken the otcl token
		 * @return the builder
		 */
		public Builder addOtclToken(String otclToken) {
			this.otclToken = otclToken;
			return this;
		}

		/**
		 * Adds the token path.
		 *
		 * @param chainPath the chain path
		 * @return the builder
		 */
		public Builder addTokenPath(String chainPath) {
			this.tokenPath = chainPath;
			return this;
		}

		/**
		 * Adds the otcl token index.
		 *
		 * @param otclTokenIndex the otcl token index
		 * @return the builder
		 */
		public Builder addOtclTokenIndex(int otclTokenIndex) {
			this.otclTokenIndex = otclTokenIndex;
			return this;
		}

		/**
		 * Adds the is root node.
		 *
		 * @param isRootNode the is root node
		 * @return the builder
		 */
		public Builder addIsRootNode(boolean isRootNode) {
			this.isRootNode = isRootNode;
			return this;
		}

		/**
		 * Adds the collection definer.
		 *
		 * @param collectionDescriptor the collection descriptor
		 * @return the builder
		 */
		public Builder addCollectionDefiner(CollectionDescriptor collectionDescriptor) {
			this.collectionDescriptor = collectionDescriptor;
			return this;
		}

		/**
		 * Adds the concrete type name.
		 *
		 * @param concreteTypeName the concrete type name
		 * @return the builder
		 */
		public Builder addConcreteTypeName(String concreteTypeName) {
			if (concreteTypeName == null) {
				return this;
			}
			this.concreteTypeName = concreteTypeName;
			return this;
		}

		/**
		 * Adds the field name.
		 *
		 * @param fieldName the field name
		 * @return the builder
		 */
		public Builder addFieldName(String fieldName) {
			this.fieldName = fieldName;
			return this;
		}

		/**
		 * Adds the field.
		 *
		 * @param field the field
		 * @return the builder
		 */
		public Builder addField(Field field) {
			this.field = field;
			return this;
		}

		/**
		 * Adds the field type.
		 *
		 * @param fieldType the field type
		 * @return the builder
		 */
		public Builder addFieldType(Class<?> fieldType) {
			this.fieldType = fieldType;
			return this;
		}

		/**
		 * Adds the concrete type.
		 *
		 * @param concreteType the concrete type
		 * @return the builder
		 */
		public Builder addConcreteType(Class<?> concreteType) {
			this.concreteType = concreteType;
			return this;
		}

		/**
		 * Adds the parent.
		 *
		 * @param parent the parent
		 * @return the builder
		 */
		public Builder addParent(OtclCommandDto parent) {
			this.parent = parent;
			return this;
		}

		/**
		 * Adds the child.
		 *
		 * @param fieldName the field name
		 * @param otclCommandDto the otcl command dto
		 * @return the builder
		 */
		public Builder addChild(String fieldName, OtclCommandDto otclCommandDto) {
			if (children == null) {
				children = new HashMap<>();
			}
			children.put(fieldName, otclCommandDto);
			return this;
		}
	}
}
