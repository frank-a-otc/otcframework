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

import org.otcframework.common.dto.DeploymentDto;

/**
 * The Interface DeploymentContainer.
 */
// TODO: Auto-generated Javadoc
public interface OtcRegistry {

	/**
	 * Deploy.
	 */
	void register();

	/**
	 * Deploy.
	 *
	 * @param deploymentDtos the deployment dtos
	 */
	void deploy(DeploymentDto deploymentDtos);

	/**
	 * Retrieve deployment dto.
	 *
	 * @param otcNamespace the otc namespace
	 * @param sourceClz    the source clz
	 * @param targetClz    the target clz
	 * @return the deployment dto
	 */
	DeploymentDto retrieveDeploymentDto(String otcNamespace, Class<?> sourceClz, Class<?> targetClz);

	/**
	 * Retrieve deployment dto.
	 *
	 * @param otcNamespace the otc namespace
	 * @param source       the source
	 * @param targetClz    the target clz
	 * @return the deployment dto
	 */
	DeploymentDto retrieveDeploymentDto(String otcNamespace, Object source, Class<?> targetClz);
}
