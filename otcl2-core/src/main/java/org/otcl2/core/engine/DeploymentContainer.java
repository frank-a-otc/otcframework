/**
* Copyright (c) otclfoundation.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*/
package org.otcl2.core.engine;

import org.otcl2.common.dto.DeploymentDto;

// TODO: Auto-generated Javadoc
/**
 * The Interface DeploymentContainer.
 */
interface DeploymentContainer {

	/**
	 * Deploy.
	 */
	void deploy();

	/**
	 * Deploy.
	 *
	 * @param deploymentDtos the deployment dtos
	 */
	void deploy(DeploymentDto deploymentDtos);

	/**
	 * Retrieve deployment dto.
	 *
	 * @param otclNamespace the otcl namespace
	 * @param sourceClz the source clz
	 * @param targetClz the target clz
	 * @return the deployment dto
	 */
	DeploymentDto retrieveDeploymentDto(String otclNamespace, Class<?> sourceClz, Class<?> targetClz);

	/**
	 * Retrieve deployment dto.
	 *
	 * @param otclNamespace the otcl namespace
	 * @param source the source
	 * @param targetClz the target clz
	 * @return the deployment dto
	 */
	DeploymentDto retrieveDeploymentDto(String otclNamespace, Object source, Class<?> targetClz);

}
