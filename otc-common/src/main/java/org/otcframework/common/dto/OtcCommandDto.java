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
package org.otcframework.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.otcframework.common.OtcConstants.TARGET_SOURCE;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Class OtcCommandDto.
 */
public class OtcCommandDto {

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
			return (NORMAL.value.equals(this.value));
		}

		/**
		 * Checks if is collection.
		 *
		 * @return true, if is collection
		 */
		public boolean isCollection() {
			return (ARRAY.value.equals(this.value) || LIST.value.equals(this.value) || SET.value.equals(this.value)
					|| QUEUE.value.equals(this.value));
		}

		/**
		 * Checks if is array.
		 *
		 * @return true, if is array
		 */
		public boolean isArray() {
			return (ARRAY.value.equals(this.value));
		}

		/**
		 * Checks if is collection member.
		 *
		 * @return true, if is collection member
		 */
		public boolean isCollectionMember() {
			return (COLLECTION_MEMBER.value.equals(this.value));
		}

		/**
		 * Checks if is map.
		 *
		 * @return true, if is map
		 */
		public boolean isMap() {
			return (MAP.value.equals(this.value));
		}

		/**
		 * Checks if is map key.
		 *
		 * @return true, if is map key
		 */
		public boolean isMapKey() {
			return (MAP_KEY.value.equals(this.value));
		}

		/**
		 * Checks if is map value.
		 *
		 * @return true, if is map value
		 */
		public boolean isMapValue() {
			return (MAP_VALUE.value.equals(this.value));
		}
	}

	/**
	 * Instantiates a new otc command dto.
	 */
	private OtcCommandDto() {
	}

	/** The token path. */
	public String tokenPath;

	/** The enum target source. */
	public TARGET_SOURCE enumTargetSource;

	/** The otc token. */
	public String otcToken;

	/** The otc token index. */
	public int otcTokenIndex;

	/** The is root node. */
	public boolean isRootNode;

	/** The is first node. */
	public boolean isFirstNode;

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

	/** The enable getter helper. */
	public boolean enableGetterHelper;

	/** The enable setter helper. */
	public boolean enableSetterHelper;

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
	public OtcCommandDto parent;

	/** The children. */
	public Map<String, OtcCommandDto> children;

	/** The occurs in commands. */
	public List<String> occursInCommands;

	/**
	 * Instantiates a new otc command dto.
	 *
	 * @param builder the builder
	 */
	private OtcCommandDto(Builder builder) {
		occursInCommands = builder.occursInCommands;
		enumTargetSource = builder.enumTargetSource;
		otcToken = builder.otcToken;
		tokenPath = builder.tokenPath;
		otcTokenIndex = builder.otcTokenIndex;
		isFirstNode = builder.isFirstNode;
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
			public OtcCommandDto build() {
				return new OtcCommandDto(this);
			}
		};
	}

	/**
	 * Adds the child.
	 *
	 * @param otcCommandDto the otc command dto
	 */
	public void addChild(OtcCommandDto otcCommandDto) {
		if (children == null) {
			children = new HashMap<>();
		}
		children.put(otcCommandDto.otcToken, otcCommandDto);
	}

	/**
	 * Adds the command id.
	 *
	 * @param commandId the command id
	 */
	public void addCommandId(String commandId) {
		occursInCommands.add(commandId);
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

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return "OtcCommandDto [tokenPath=" + tokenPath + ", enumTargetSource=" + enumTargetSource + ", otcToken="
				+ otcToken + ", otcTokenIndex=" + otcTokenIndex + ", isRootNode=" + isRootNode + ", isFirstNode="
				+ isFirstNode + ", hasCollectionNotation=" + hasCollectionNotation + ", hasMapNotation="
				+ hasMapNotation + ", collectionDescriptor=" + collectionDescriptor + ", concreteTypeName="
				+ concreteTypeName + ", mapKeyConcreteType=" + mapKeyConcreteType + ", mapValueConcreteType="
				+ mapValueConcreteType + ", fieldName=" + fieldName + ", field=" + field + ", enableGetterHelper="
				+ enableGetterHelper + ", enableSetterHelper=" + enableSetterHelper + ", isGetterInitialized="
				+ isGetterInitialized + ", isSetterInitialized=" + isSetterInitialized + ", getter=" + getter
				+ ", setter=" + setter + ", fieldType=" + fieldType + ", concreteType=" + concreteType
				+ ", declaringClass=" + declaringClass + ", parent=" + parent + ", children=" + children
				+ ", occursInCommands=" + occursInCommands + "]";
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
		result = prime * result + ((concreteType == null) ? 0 : concreteType.hashCode());
		result = prime * result + ((concreteTypeName == null) ? 0 : concreteTypeName.hashCode());
		result = prime * result + ((declaringClass == null) ? 0 : declaringClass.hashCode());
		result = prime * result + (enableGetterHelper ? 1231 : 1237);
		result = prime * result + (enableSetterHelper ? 1231 : 1237);
		result = prime * result + ((enumTargetSource == null) ? 0 : enumTargetSource.hashCode());
		result = prime * result + ((field == null) ? 0 : field.hashCode());
		result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
		result = prime * result + ((fieldType == null) ? 0 : fieldType.hashCode());
		result = prime * result + ((getter == null) ? 0 : getter.hashCode());
		result = prime * result + (hasCollectionNotation ? 1231 : 1237);
		result = prime * result + (hasMapNotation ? 1231 : 1237);
		result = prime * result + (isFirstNode ? 1231 : 1237);
		result = prime * result + (isGetterInitialized ? 1231 : 1237);
		result = prime * result + (isRootNode ? 1231 : 1237);
		result = prime * result + (isSetterInitialized ? 1231 : 1237);
		result = prime * result + ((mapKeyConcreteType == null) ? 0 : mapKeyConcreteType.hashCode());
		result = prime * result + ((mapValueConcreteType == null) ? 0 : mapValueConcreteType.hashCode());
		result = prime * result + ((occursInCommands == null) ? 0 : occursInCommands.hashCode());
		result = prime * result + ((otcToken == null) ? 0 : otcToken.hashCode());
		result = prime * result + otcTokenIndex;
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
		OtcCommandDto other = (OtcCommandDto) obj;
		if (children == null) {
			if (other.children != null)
				return false;
		} else if (!children.equals(other.children))
			return false;
		if (collectionDescriptor != other.collectionDescriptor)
			return false;
		if (concreteType == null) {
			if (other.concreteType != null)
				return false;
		} else if (!concreteType.equals(other.concreteType))
			return false;
		if (concreteTypeName == null) {
			if (other.concreteTypeName != null)
				return false;
		} else if (!concreteTypeName.equals(other.concreteTypeName))
			return false;
		if (declaringClass == null) {
			if (other.declaringClass != null)
				return false;
		} else if (!declaringClass.equals(other.declaringClass))
			return false;
		if (enableGetterHelper != other.enableGetterHelper)
			return false;
		if (enableSetterHelper != other.enableSetterHelper)
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
		if (fieldType == null) {
			if (other.fieldType != null)
				return false;
		} else if (!fieldType.equals(other.fieldType))
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
		if (isFirstNode != other.isFirstNode)
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
		if (occursInCommands == null) {
			if (other.occursInCommands != null)
				return false;
		} else if (!occursInCommands.equals(other.occursInCommands))
			return false;
		if (otcToken == null) {
			if (other.otcToken != null)
				return false;
		} else if (!otcToken.equals(other.otcToken))
			return false;
		if (otcTokenIndex != other.otcTokenIndex)
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

		/** The occurs in commands. */
		public List<String> occursInCommands;

		/** The enum target source. */
		private TARGET_SOURCE enumTargetSource;

		/** The otc token. */
		private String otcToken;

		/** The token path. */
		private String tokenPath;

		/** The otc token index. */
		private int otcTokenIndex;

		/** The is first node. */
		private boolean isFirstNode;

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
		private OtcCommandDto parent;

		/** The children. */
		private Map<String, OtcCommandDto> children;

		/**
		 * Builds the.
		 *
		 * @return the otc command dto
		 */
		public abstract OtcCommandDto build();

		/**
		 * Adds the command id.
		 *
		 * @param commandId the command id
		 * @return the builder
		 */
		public Builder addCommandId(String commandId) {
			if (occursInCommands == null) {
				occursInCommands = new ArrayList<>();
			}
			occursInCommands.add(commandId);
			return this;
		}

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
		 * Adds the otc token.
		 *
		 * @param otcToken the otc token
		 * @return the builder
		 */
		public Builder addOtcToken(String otcToken) {
			this.otcToken = otcToken;
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
		 * Adds the otc token index.
		 *
		 * @param otcTokenIndex the otc token index
		 * @return the builder
		 */
		public Builder addOtcTokenIndex(int otcTokenIndex) {
			this.otcTokenIndex = otcTokenIndex;
			return this;
		}

		/**
		 * Adds the is first node.
		 *
		 * @param isFirstNode the is first node
		 * @return the builder
		 */
		public Builder addIsFirstNode(boolean isFirstNode) {
			this.isFirstNode = isFirstNode;
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
		public Builder addParent(OtcCommandDto parent) {
			this.parent = parent;
			return this;
		}

		/**
		 * Adds the child.
		 *
		 * @param fieldName     the field name
		 * @param otcCommandDto the otc command dto
		 * @return the builder
		 */
		public Builder addChild(String fieldName, OtcCommandDto otcCommandDto) {
			if (children == null) {
				children = new HashMap<>();
			}
			children.put(fieldName, otcCommandDto);
			return this;
		}
	}
}
