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
package org.otcframework.core.engine;

import java.util.Map;

/**
 * The Interface OtcExecutor.
 */
// TODO: Auto-generated Javadoc
public interface OtcExecutor {

	/**
	 * Execute otc.
	 *
	 * @param <T>          the generic type
	 * @param <S>          the generic type
	 * @param otcNamespace the otc namespace
	 * @param source       the source
	 * @param targetClz    the target clz
	 * @param data         the data
	 * @return the t
	 */
	<T, S> T executeOtc(String otcNamespace, S source, Class<T> targetClz, Map<String, Object> data);

	/**
	 * Execute otc.
	 *
	 * @param <T>          the generic type
	 * @param otcNamespace the otc namespace
	 * @param targetClz    the target clz
	 * @param data         the data
	 * @return the t
	 */
	<T> T executeOtc(String otcNamespace, Class<T> targetClz, Map<String, Object> data);
}
