/**
* Copyright (c) otcl2.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*/
package org.otcl2.common.dto;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.otcl2.common.OtclConstants.TARGET_SOURCE;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class OtclCommandDto {

	public enum CollectionDescriptor {
		NORMAL("None"), 
		ARRAY("Array"), 
		LIST("List"), 
		SET("Set"), 
		QUEUE("Queue"),
		COLLECTION_MEMBER("Collection.Member"), 
		MAP("Map"), 
		MAP_KEY("Map.Key"), 
		MAP_VALUE("Map.Value");

		private final String value;

		CollectionDescriptor(String value) {
			this.value = value;
		}

		public boolean isNormal() {
			return NORMAL.value == this.value;
		}

		public boolean isCollection() {
			return ARRAY.value == this.value || LIST.value == this.value || SET.value == this.value
					|| QUEUE.value == this.value;
		}

		public boolean isArray() {
			return ARRAY.value == this.value;
		}

		public boolean isCollectionMember() {
			return COLLECTION_MEMBER.value == this.value;
		}

		public boolean isMap() {
			return MAP.value == this.value;
		}

		public boolean isMapKey() {
			return MAP_KEY.value == this.value;
		}

		public boolean isMapValue() {
			return MAP_VALUE.value == this.value;
		}
	}

	private OtclCommandDto() {
	}

	public String tokenPath;
	public TARGET_SOURCE enumTargetSource;
	public String otclToken;
	public int otclTokenIndex;
	public boolean isRootNode;
	public boolean hasCollectionNotation;
	public boolean hasMapNotation;
	public CollectionDescriptor collectionDescriptor = CollectionDescriptor.NORMAL;
	public String concreteTypeName;
	public String mapKeyConcreteType;
	public String mapValueConcreteType;
	public String fieldName;
	public Field field;
	public boolean enableFactoryHelperGetter;
	public boolean enableFactoryHelperSetter;
	public boolean isGetterInitialized;
	public boolean isSetterInitialized;
	public String getter;
	public String setter;
	public Class<?> fieldType;
	public Class<?> concreteType;
	public Class<?> declaringClass;
	public OtclCommandDto parent;
	public Map<String, OtclCommandDto> children;

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

	public static Builder newBuilder() {
		return new Builder() {
			@Override
			public OtclCommandDto build() {
				return new OtclCommandDto(this);
			}
		};
	}

	public void addChild(OtclCommandDto otclCommandDto) {
		if (children == null) {
			children = new HashMap<>();
		}
		children.put(otclCommandDto.otclToken, otclCommandDto);
	}

    @JsonIgnore
    public boolean isEnum() {
		return fieldType.isEnum();
	}

    @JsonIgnore
	public boolean isNormal() {
		return collectionDescriptor.isNormal();
	}

    @JsonIgnore
	public boolean isCollectionOrMap() {
		return isCollection() || isMap();
	}

    @JsonIgnore
	public boolean isCollection() {
		return collectionDescriptor.isCollection();
	}

    @JsonIgnore
	public boolean isArray() {
		return collectionDescriptor.isArray();
	}

    @JsonIgnore
	public boolean isCollectionOrMapMember() {
		return isCollectionMember() || isMapMember();
	}

    @JsonIgnore
	public boolean isCollectionMember() {
		return collectionDescriptor.isCollectionMember();
	}

    @JsonIgnore
	public boolean isMap() {
		return collectionDescriptor.isMap();
	}

    @JsonIgnore
	public boolean isMapKey() {
		return collectionDescriptor.isMapKey();
	}

    @JsonIgnore
	public boolean isMapValue() {
		return collectionDescriptor.isMapValue();
	}

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

	public abstract static class Builder {
		private TARGET_SOURCE enumTargetSource;
		private String otclToken;
		private String tokenPath;
		private int otclTokenIndex;
		private boolean isRootNode;
		private CollectionDescriptor collectionDescriptor = CollectionDescriptor.NORMAL;
		private String concreteTypeName;
		private String fieldName;
		private Field field;
		private Class<?> fieldType;
		private Class<?> concreteType;
		private OtclCommandDto parent;
		private Map<String, OtclCommandDto> children;

		public abstract OtclCommandDto build();

		public Builder addTargetOrSource(TARGET_SOURCE enumTargetOrSource) {
			this.enumTargetSource = enumTargetOrSource;
			return this;
		}

		public Builder addOtclToken(String otclToken) {
			this.otclToken = otclToken;
			return this;
		}

		public Builder addTokenPath(String chainPath) {
			this.tokenPath = chainPath;
			return this;
		}

		public Builder addOtclTokenIndex(int otclTokenIndex) {
			this.otclTokenIndex = otclTokenIndex;
			return this;
		}

		public Builder addIsRootNode(boolean isRootNode) {
			this.isRootNode = isRootNode;
			return this;
		}

		public Builder addCollectionDefiner(CollectionDescriptor collectionDescriptor) {
			this.collectionDescriptor = collectionDescriptor;
			return this;
		}

		public Builder addConcreteTypeName(String concreteTypeName) {
			if (concreteTypeName == null) {
				return this;
			}
			this.concreteTypeName = concreteTypeName;
			return this;
		}

		public Builder addFieldName(String fieldName) {
			this.fieldName = fieldName;
			return this;
		}

		public Builder addField(Field field) {
			this.field = field;
			return this;
		}

		public Builder addFieldType(Class<?> fieldType) {
			this.fieldType = fieldType;
			return this;
		}

		public Builder addConcreteType(Class<?> concreteType) {
			this.concreteType = concreteType;
			return this;
		}

		public Builder addParent(OtclCommandDto parent) {
			this.parent = parent;
			return this;
		}

		public Builder addChild(String fieldName, OtclCommandDto otclCommandDto) {
			if (children == null) {
				children = new HashMap<>();
			}
			children.put(fieldName, otclCommandDto);
			return this;
		}
	}
}
