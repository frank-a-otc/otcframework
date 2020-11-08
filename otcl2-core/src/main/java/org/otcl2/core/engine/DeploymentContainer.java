/**
* Copyright (c) otcl2.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*/
package org.otcl2.core.engine;

import org.otcl2.common.dto.DeploymentDto;

interface DeploymentContainer {

	void deploy();

	void deploy(DeploymentDto deploymentDtos);

	DeploymentDto retrieveDeploymentDto(String otclNamespace, Class<?> sourceClz, Class<?> targetClz);

	DeploymentDto retrieveDeploymentDto(String otclNamespace, Object source, Class<?> targetClz);

}
