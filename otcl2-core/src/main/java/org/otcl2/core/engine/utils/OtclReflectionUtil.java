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
package org.otcl2.core.engine.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.otcl2.common.dto.OtclCommandDto;
import org.otcl2.common.exception.OtclException;
import org.otcl2.common.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class OtclReflectionUtil.
 */
public class OtclReflectionUtil {

	/** The logger. */
	private static Logger LOGGER = LoggerFactory.getLogger(OtclReflectionUtil.class);

	/**
	 * The Enum GETTER_SETTER.
	 */
	public static enum GETTER_SETTER {
		GETTER, 
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
			throw new OtclException("", "Property value cannot be null!.");
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
	 * @param otclCommandDto the otcl command dto
	 * @return the string
	 */
	public static String findGetterName(OtclCommandDto otclCommandDto) {
		Field field = otclCommandDto.field;
		String fieldName = field.getName();
		String getter = otclCommandDto.getter;
		if (getter == null) {
			getter = "get" + CommonUtils.initCap(fieldName);
		}
		Method method = findMethod(GETTER_SETTER.GETTER, getter, otclCommandDto);
		String methodName = method.getName();
		return methodName;
	}

	/**
	 * Find setter name.
	 *
	 * @param otclCommandDto the otcl command dto
	 * @return the string
	 */
	public static String findSetterName(OtclCommandDto otclCommandDto) {
		Field field = otclCommandDto.field;
		String fieldName = field.getName();
		String setter = otclCommandDto.setter;
		if (setter == null) {
			setter = "set" + CommonUtils.initCap(fieldName);
		}
		Method method = findMethod(GETTER_SETTER.SETTER, setter, otclCommandDto);
		String methodName = method.getName();
		return methodName;
	}

	/**
	 * Find method.
	 *
	 * @param enumGetterSetter the enum getter setter
	 * @param methodName the method name
	 * @param otclCommandDto the otcl command dto
	 * @return the method
	 */
	private static Method findMethod(GETTER_SETTER enumGetterSetter, String methodName, OtclCommandDto otclCommandDto) {
		Method method = null;
		Field field = otclCommandDto.field;
		Class<?> declaringClz = field.getDeclaringClass();
		Exception ex = null;
		try {
			if (GETTER_SETTER.GETTER == enumGetterSetter) {
				method = declaringClz.getMethod(methodName, ZERO_LENGTH_CLASS_ARRAY);
			} else {
				method = declaringClz.getMethod(methodName, new Class[] { field.getType() });
			}
			return method;
		} catch (NoSuchMethodException | SecurityException e) {
			ex = e;
		}
		Class<?> parentConcreteType = null;
		if (!otclCommandDto.isRootNode) {
			parentConcreteType = otclCommandDto.parent.concreteType;
		}
		if (GETTER_SETTER.GETTER == enumGetterSetter) {
			if (parentConcreteType != null) {
				try {
					method = parentConcreteType.getMethod(methodName, ZERO_LENGTH_CLASS_ARRAY);
					return method;
				} catch (NoSuchMethodException | SecurityException e) {
					ex = e;
				}
			}
		} else {
			Class<?> concreteType = otclCommandDto.concreteType;
			if (concreteType != null) {
				try {
					method = declaringClz.getMethod(methodName, new Class[] { concreteType });
					return method;
				} catch (NoSuchMethodException | SecurityException e) {
					ex = e;
				}
				try {
					if (parentConcreteType != null) {
						method = parentConcreteType.getMethod(methodName, new Class[] { concreteType });
						return method;
					}
				} catch (NoSuchMethodException | SecurityException e) {
					ex = e;
				}
			}
		}
		if (method == null && ex != null) {
			throw new OtclException("", ex);
		}
		return method;
	}

	/**
	 * Find helper method name.
	 *
	 * @param factoryHelper the factory helper
	 * @param enumGetterSetter the enum getter setter
	 * @param methodName the method name
	 * @param otclCommandDto the otcl command dto
	 * @return the string
	 */
	public static String findHelperMethodName(Class<?> factoryHelper, GETTER_SETTER enumGetterSetter,
			String methodName, OtclCommandDto otclCommandDto) {
		Method method = findFactoryHelperMethod(factoryHelper, enumGetterSetter, methodName, otclCommandDto);
		if (method != null) {
			methodName = method.getName();
		}
		return methodName;
	}
	
	/**
	 * Find factory helper method.
	 *
	 * @param factoryHelper the factory helper
	 * @param enumGetterSetter the enum getter setter
	 * @param methodName the method name
	 * @param otclCommandDto the otcl command dto
	 * @return the method
	 */
	public static Method findFactoryHelperMethod(Class<?> factoryHelper, GETTER_SETTER enumGetterSetter, String methodName,
			OtclCommandDto otclCommandDto) {
		if (factoryHelper == null) {
			throw new OtclException("", "Helper class cannot be null to invoke this method!");
		}
		Method method = null;
		Field field = otclCommandDto.field;
		Class<?> declaringClz = field.getDeclaringClass();
		Exception ex = null;
		try {
			if (GETTER_SETTER.SETTER == enumGetterSetter) {
				Class<?> fieldType = otclCommandDto.fieldType;
				method = findMethod(factoryHelper, methodName, otclCommandDto, declaringClz, fieldType);
				otclCommandDto.enableFactoryHelperSetter = true;
			} else {
				method = findMethod(factoryHelper, methodName, otclCommandDto, declaringClz);
				otclCommandDto.enableFactoryHelperGetter = true;
			}
		} catch (NoSuchMethodException | SecurityException e) {
			throw new OtclException("", ex);
		}
		return method;
	}

	/**
	 * Find method.
	 *
	 * @param clz the clz
	 * @param methodName the method name
	 * @param otclCommandDto the otcl command dto
	 * @param paramTypes the param types
	 * @return the method
	 * @throws NoSuchMethodException the no such method exception
	 * @throws SecurityException the security exception
	 */
	private static Method findMethod(Class<?> clz, String methodName, OtclCommandDto otclCommandDto, 
			Class<?>... paramTypes) throws NoSuchMethodException, SecurityException {
		Method method = clz.getMethod(methodName, paramTypes);
		return method;
	}

	/**
	 * Find field.
	 *
	 * @param clazz the clazz
	 * @param name the name
	 * @return the field
	 */
	public static Field findField(Class<?> clazz, String name) {
		return findField(clazz, name, null);
	}

	/**
	 * Find field.
	 *
	 * @param clazz the clazz
	 * @param name the name
	 * @param type the type
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
