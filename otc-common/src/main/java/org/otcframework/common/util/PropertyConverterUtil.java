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

import org.otcframework.common.exception.PropertyConverterException;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * The Class PropertyConverterUtil.
 */
public class PropertyConverterUtil {

	/** The Constant CONSTR_ARGS. */
	private static final Class<?>[] CONSTR_ARGS = { String.class };

	/** The Constant wrapperTypes. */
	private static final Map<Class<?>, Class<?>> wrapperTypes = new IdentityHashMap<>(9);

	/** The Constant allTypes. */
	private static final Map<Class<?>, Class<?>> allTypes;
	static {
		wrapperTypes.put(Boolean.class, boolean.class);
		wrapperTypes.put(Byte.class, byte.class);
		wrapperTypes.put(Character.class, char.class);
		wrapperTypes.put(Double.class, double.class);
		wrapperTypes.put(Float.class, float.class);
		wrapperTypes.put(Integer.class, int.class);
		wrapperTypes.put(Long.class, long.class);
		wrapperTypes.put(Short.class, short.class);
		wrapperTypes.put(Void.class, void.class);
		allTypes = new IdentityHashMap<>(wrapperTypes);
		allTypes.put(BigInteger.class, BigInteger.class);
		allTypes.put(BigDecimal.class, BigDecimal.class);
	}

	private PropertyConverterUtil() {}
	/**
	 * To integer.
	 *
	 * @param value the value
	 * @return the integer
	 */
	public static Integer toInteger(Object value) {
		Number num = toNumber(value, Integer.class);
		if (num instanceof Integer) {
			return (Integer) num;
		} else if (num != null) {
			return num.intValue();
		}
		return null;
	}

	/**
	 * To number.
	 *
	 * @param value the value
	 * @param clz   the clz
	 * @return the number
	 */
	static Number toNumber(Object value, Class<?> clz) {
		if (value == null) {
			return null;
		}
		if (value instanceof Number) {
			return (Number) value;
		}
		String strValue = value.toString();
		Number num;
		try {
			Constructor<?> constr = clz.getConstructor(CONSTR_ARGS);
			num = (Number) constr.newInstance((Object[]) new String[] { strValue });
		} catch (Exception ex) {
			throw new PropertyConverterException(
					"Number Conversion error! Error converting '" + strValue + "' to " + clz.getName(), ex);

		}
		return num;
	}

}
