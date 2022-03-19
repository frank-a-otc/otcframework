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
package org.otcframework.executor;

import org.otcframework.common.OtcConstants.TARGET_SOURCE;
import org.otcframework.common.dto.DeploymentDto;
import org.otcframework.common.engine.indexer.dto.IndexedCollectionsDto;

/**
 * The Interface ObjectIndexer.
 */
// TODO: Auto-generated Javadoc
interface ObjectIndexer {

	/**
	 * Index object.
	 *
	 * @param deploymentDto    the deployment dto
	 * @param enumTargetSource the enum target source
	 * @param object           the object
	 * @return the indexed collections dto
	 */
	IndexedCollectionsDto indexObject(DeploymentDto deploymentDto, TARGET_SOURCE enumTargetSource, Object object);
}
