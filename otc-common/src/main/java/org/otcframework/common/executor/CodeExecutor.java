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
package org.otcframework.common.executor;

import java.util.Map;

import org.otcframework.common.engine.indexer.dto.IndexedCollectionsDto;

// TODO: Auto-generated Javadoc
/**
 * The Interface CodeExecutor.
 *
 * @param <S> the generic type
 * @param <T> the generic type
 */
public interface CodeExecutor<S, T> {

	/**
	 * Execute.
	 *
	 * @param srcObject the src object
	 * @param sourcePCD the source PCD
	 * @param config the config
	 * @return the t
	 */
	public T execute(S srcObject, IndexedCollectionsDto sourcePCD, Map<String, Object> config);
}
