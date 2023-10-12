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
package org.otcframework.common.util;

import org.otcframework.common.dto.OtcCommandDto;
import org.otcframework.common.exception.OtcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The Class OtcReflectionUtil.
 */
public class OtcReflectionUtil {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(OtcReflectionUtil.class);

	/**
	 * The Enum GETTER_SETTER.
	 */
	public static enum GETTER_SETTER {

		/** The getter. */
		GETTER,

		/** The setter. */
		SETTER
	};

	/** The Constant ZERO_LENGTH_FIELD_ARRAY. */
	private static final Field[] ZERO_LENGTH_FIELD_ARRAY = new Field[0];

	/** The Constant ZERO_LENGTH_CLASS_ARRAY. */
	private static final Class[] ZERO_LENGTH_CLASS_ARRAY = new Class[0];

	/** The Constant fieldsCache. */
	private static final Map<Class<?>, Field[]> fieldsCache = new ConcurrentHashMap<>(100);

	/**
	 * Read field value.
	 *
	 * @param field the field
	 * @param value the value
	 * @return the object
	 */
	public static Object readFieldValue(Field field, Object value) {
		if (value == null) {
			throw new OtcException("", "Property value cannot be null!.");
		}
		try {
			field.setAccessible(true);
			value = field.get(value);
		} catch (IllegalAccessException ex) {
			LOGGER.warn(ex.getMessage());
		} finally {
			field.setAccessible(false);
		}
		return value;
	}

	/**
	 * Find getter name.
	 *
	 * @param otcCommandDto the otc command dto
	 * @return the string
	 */
	public static String findGetterName(OtcCommandDto otcCommandDto) {
		Field field = otcCommandDto.field;
		String fieldName = field.getName();
		String getter = otcCommandDto.getter;
		if (getter == null) {
			if (Boolean.class.isAssignableFrom(otcCommandDto.fieldType)) {
				getter = "is" + CommonUtils.initCap(fieldName);
			} else {
				getter = "get" + CommonUtils.initCap(fieldName);
			}
		}
		Method method = findMethod(GETTER_SETTER.GETTER, getter, otcCommandDto);
		String methodName = method.getName();
		return methodName;
	}

	/**
	 * Find setter name.
	 *
	 * @param otcCommandDto the otc command dto
	 * @return the string
	 */
	public static String findSetterName(OtcCommandDto otcCommandDto) {
		Field field = otcCommandDto.field;
		String fieldName = field.getName();
		String setter = otcCommandDto.setter;
		if (setter == null) {
			setter = "set" + CommonUtils.initCap(fieldName);
		}
		Method method = findMethod(GETTER_SETTER.SETTER, setter, otcCommandDto);
		String methodName = method.getName();
		return methodName;
	}

	/**
	 * Find method.
	 *
	 * @param enumGetterSetter the enum getter setter
	 * @param methodName       the method name
	 * @param otcCommandDto    the otc command dto
	 * @return the method
	 */
	private static Method findMethod(GETTER_SETTER enumGetterSetter, String methodName, OtcCommandDto otcCommandDto) {
		Method method = null;
		Field field = otcCommandDto.field;
		Class<?> declaringClz = field.getDeclaringClass();
		Class<?>[] paramTypes = null;
		try {
			if (GETTER_SETTER.GETTER == enumGetterSetter) {
				paramTypes = ZERO_LENGTH_CLASS_ARRAY;
			} else {
				paramTypes = new Class[] { field.getType() };
			}
			method = declaringClz.getMethod(methodName, paramTypes);
			return method;
		} catch (NoSuchMethodException | SecurityException e) {
			String msg = createMethodNotFoundMessage(declaringClz, methodName, paramTypes, otcCommandDto);
			LOGGER.warn(msg, e.getMessage());
		}
		Class<?> parentConcreteType = null;
		if (!otcCommandDto.isFirstNode) {
			parentConcreteType = otcCommandDto.parent.concreteType;
		}
		if (GETTER_SETTER.GETTER == enumGetterSetter) {
			if (parentConcreteType != null) {
				paramTypes = ZERO_LENGTH_CLASS_ARRAY;
				try {
					method = parentConcreteType.getMethod(methodName, paramTypes);
					return method;
				} catch (NoSuchMethodException | SecurityException e) {
					String msg = createMethodNotFoundMessage(parentConcreteType, methodName, paramTypes, otcCommandDto);
					throw new OtcException("", msg, e);
				}
			}
		} else {
			Class<?> concreteType = otcCommandDto.concreteType;
			if (concreteType != null) {
				paramTypes = new Class[] { concreteType };
				try {
					method = declaringClz.getMethod(methodName, paramTypes);
					return method;
				} catch (NoSuchMethodException | SecurityException e) {
					String msg = createMethodNotFoundMessage(declaringClz, methodName, paramTypes, otcCommandDto);
					LOGGER.warn(msg, e.getMessage());
				}
				try {
					if (parentConcreteType != null) {
						method = parentConcreteType.getMethod(methodName, paramTypes);
						return method;
					}
				} catch (NoSuchMethodException | SecurityException e) {
					String msg = createMethodNotFoundMessage(parentConcreteType, methodName, paramTypes, otcCommandDto);
					throw new OtcException("", msg, e);
				}
			}
		}
		return method;
	}

	/**
	 * Find helper method name.
	 *
	 * @param factoryHelper    the factory helper
	 * @param enumGetterSetter the enum getter setter
	 * @param otcCommandDto    the otc command dto
	 * @param fieldType        the field type
	 * @return the string
	 */
	public static String findHelperMethodName(Class<?> factoryHelper, GETTER_SETTER enumGetterSetter,
			OtcCommandDto otcCommandDto, Class<?> fieldType) {
		Method method = findFactoryHelperMethod(factoryHelper, enumGetterSetter, otcCommandDto, fieldType);
		String methodName = null;
		if (method != null) {
			methodName = method.getName();
		}
		return methodName;
	}

	/**
	 * Find factory helper method.
	 *
	 * @param factoryHelper    the factory helper
	 * @param enumGetterSetter the enum getter setter
	 * @param otcCommandDto    the otc command dto
	 * @param fieldType        the field type
	 * @return the method
	 */
	public static Method findFactoryHelperMethod(Class<?> factoryHelper, GETTER_SETTER enumGetterSetter,
			OtcCommandDto otcCommandDto, Class<?> fieldType) {
		if (factoryHelper == null) {
			throw new OtcException("", "Helper class cannot be null to invoke this method!");
		}
		String methodName = null;
		if (GETTER_SETTER.SETTER == enumGetterSetter) {
			methodName = otcCommandDto.setter;
		} else {
			methodName = otcCommandDto.getter;
		}
		Method method = null;
		Field field = otcCommandDto.field;
		Class<?> declaringClz = field.getDeclaringClass();
		if (GETTER_SETTER.SETTER == enumGetterSetter) {
			method = findMethod(factoryHelper, methodName, otcCommandDto, declaringClz, fieldType);
			otcCommandDto.enableSetterHelper = true;
		} else {
			method = findMethod(factoryHelper, methodName, otcCommandDto, declaringClz);
			otcCommandDto.enableGetterHelper = true;
		}
		return method;
	}

	/**
	 * Find method.
	 *
	 * @param clz           the clz
	 * @param methodName    the method name
	 * @param otcCommandDto the otc command dto
	 * @param paramTypes    the param types
	 * @return the method
	 */
	private static Method findMethod(Class<?> clz, String methodName, OtcCommandDto otcCommandDto,
			Class<?>... paramTypes) {
		Method method = null;
		try {
			method = clz.getMethod(methodName, paramTypes);
		} catch (NoSuchMethodException | SecurityException e) {
			String msg = createMethodNotFoundMessage(clz, methodName, paramTypes, otcCommandDto);
			throw new OtcException("", msg, e);
		}
		return method;
	}

	/**
	 * Creates the method not found message.
	 *
	 * @param clz           the clz
	 * @param methodName    the method name
	 * @param paramTypes    the param types
	 * @param otcCommandDto the otc command dto
	 * @return the string
	 */
	private static String createMethodNotFoundMessage(Class<?> clz, String methodName, Class<?>[] paramTypes,
			OtcCommandDto otcCommandDto) {
		StringBuilder paramsBuilder = null;
		if (paramTypes != null && paramTypes.length > 0) {
			for (Class<?> paramType : paramTypes) {
				if (paramsBuilder == null) {
					paramsBuilder = new StringBuilder("(").append(paramType.getName());
				} else {
					paramsBuilder.append(",").append(paramType.getName());
				}
			}
			paramsBuilder.append(")");
		} else {
			paramsBuilder = new StringBuilder("()");
		}
		String msg = "Method '" + clz.getName() + "." + methodName + paramsBuilder.toString()
				+ " not found for tokenpath : " + otcCommandDto.tokenPath + "' - probable conflicts in command(s) "
				+ otcCommandDto.occursInCommands;
		return msg;
	}

	/**
	 * Find field.
	 *
	 * @param clazz the clazz
	 * @param name  the name
	 * @return the field
	 */
	public static Field findField(Class<?> clazz, String name) {
		return findField(clazz, name, null);
	}

	/**
	 * Find field.
	 *
	 * @param clazz the clazz
	 * @param name  the name
	 * @param type  the type
	 * @return the field
	 */
	public static Field findField(Class<?> clazz, String name, Class<?> type) {
		Class<?> searchType = clazz;
		while (Object.class != searchType && searchType != null) {
			Field[] fields = getDeclaredFields(searchType);
			for (Field field : fields) {
				if ((name == null || name.equals(field.getName())) && (type == null || type.equals(field.getType()))) {
					return field;
				}
			}
			searchType = searchType.getSuperclass();
		}
		return null;
	}

	/**
	 * Gets the declared fields.
	 *
	 * @param clazz the clazz
	 * @return the declared fields
	 */
	private static Field[] getDeclaredFields(Class<?> clazz) {
		Field[] result = fieldsCache.get(clazz);
		if (result == null) {
			try {
				result = clazz.getDeclaredFields();
				fieldsCache.put(clazz, (result.length == 0 ? ZERO_LENGTH_FIELD_ARRAY : result));
			} catch (Throwable ex) {
				throw new IllegalStateException("Failed to introspect Class [" + clazz.getName()
						+ "] from ClassLoader [" + clazz.getClassLoader() + "]", ex);
			}
		}
		return result;
	}
}
