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

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.IdentityHashMap;
import java.util.Map;

import org.otcframework.common.exception.PropertyConverterException;

/**
 * The Class PropertyConverterUtil.
 */
// TODO: Auto-generated Javadoc
public class PropertyConverterUtil {

	/** The Constant HEX_PREFIX. */
	private static final String HEX_PREFIX = "0x";

	/** The Constant HEX_RADIX. */
	private static final int HEX_RADIX = 16;

	/** The Constant BIN_PREFIX. */
	private static final String BIN_PREFIX = "0b";

	/** The Constant BIN_RADIX. */
	private static final int BIN_RADIX = 2;

	/** The Constant CONSTR_ARGS. */
	private static final Class<?>[] CONSTR_ARGS = { String.class };
//	private static final char LIST_ESC_CHAR = '\\';
	/** The Constant wrapperTypes. */
//	private static final String INTERNET_ADDRESS_CLASSNAME = "javax.mail.internet.InternetAddress";
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
		try {
			if (strValue.startsWith(HEX_PREFIX)) {
				return new BigInteger(strValue.substring(HEX_PREFIX.length()), HEX_RADIX);
			} else if (strValue.startsWith(BIN_PREFIX)) {
				return new BigInteger(strValue.substring(BIN_PREFIX.length()), BIN_RADIX);
			} else {
				Constructor<?> constr = clz.getConstructor(CONSTR_ARGS);
				return (Number) constr.newInstance(new Object[] { strValue });
			}
		} catch (NumberFormatException nex) {
			String errMsg = null;
			if (strValue.startsWith(HEX_PREFIX)) {
				errMsg = "! Invalid hex number.";
			} else if (strValue.startsWith(BIN_PREFIX)) {
				errMsg = "! Invalid binary number.";
			}
			throw new PropertyConverterException(
					"Number Conversion error! Error converting '" + strValue + "' to " + clz.getName() + errMsg, nex);
		} catch (Exception ex) {
			throw new PropertyConverterException(
					"Number Conversion error! Error converting '" + strValue + "' to " + clz.getName(), ex);
		}
	}

	/**
	 * To boolean object.
	 *
	 * @param str the str
	 * @return the boolean
	 */
	public static Boolean toBooleanObject(String str) {
		// this approach is reported to be faster
		if (str == null) {
			return null;
		}
		if (str.equalsIgnoreCase("true")) {
			return Boolean.TRUE;
		}
		switch (str.length()) {
		case 1: {
			char ch0 = str.charAt(0);
			if ((ch0 == 'y' || ch0 == 'Y') || (ch0 == 't' || ch0 == 'T')) {
				return Boolean.TRUE;
			}
			if ((ch0 == 'n' || ch0 == 'N') || (ch0 == 'f' || ch0 == 'F')) {
				return Boolean.FALSE;
			}
			break;
		}
		case 2: {
			char ch0 = str.charAt(0);
			char ch1 = str.charAt(1);
			if ((ch0 == 'o' || ch0 == 'O') && (ch1 == 'n' || ch1 == 'N')) {
				return Boolean.TRUE;
			}
			if ((ch0 == 'n' || ch0 == 'N') && (ch1 == 'o' || ch1 == 'O')) {
				return Boolean.FALSE;
			}
			break;
		}
		case 3: {
			char ch0 = str.charAt(0);
			char ch1 = str.charAt(1);
			char ch2 = str.charAt(2);
			if ((ch0 == 'y' || ch0 == 'Y') && (ch1 == 'e' || ch1 == 'E') && (ch2 == 's' || ch2 == 'S')) {
				return Boolean.TRUE;
			}
			if ((ch0 == 'o' || ch0 == 'O') && (ch1 == 'f' || ch1 == 'F') && (ch2 == 'f' || ch2 == 'F')) {
				return Boolean.FALSE;
			}
			break;
		}
		case 4: {
			char ch0 = str.charAt(0);
			char ch1 = str.charAt(1);
			char ch2 = str.charAt(2);
			char ch3 = str.charAt(3);
			if ((ch0 == 't' || ch0 == 'T') && (ch1 == 'r' || ch1 == 'R') && (ch2 == 'u' || ch2 == 'U')
					&& (ch3 == 'e' || ch3 == 'E')) {
				return Boolean.TRUE;
			}
			break;
		}
		case 5: {
			char ch0 = str.charAt(0);
			char ch1 = str.charAt(1);
			char ch2 = str.charAt(2);
			char ch3 = str.charAt(3);
			char ch4 = str.charAt(4);
			if ((ch0 == 'f' || ch0 == 'F') && (ch1 == 'a' || ch1 == 'A') && (ch2 == 'l' || ch2 == 'L')
					&& (ch3 == 's' || ch3 == 'S') && (ch4 == 'e' || ch4 == 'E')) {
				return Boolean.FALSE;
			}
			break;
		}
		}
		return null;
	}
}
