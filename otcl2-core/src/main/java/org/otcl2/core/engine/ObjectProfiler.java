/**
* Copyright (c) otcl2.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*/
package org.otcl2.core.engine;

import org.otcl2.common.OtclConstants.TARGET_SOURCE;
import org.otcl2.common.dto.DeploymentDto;
import org.otcl2.common.engine.profiler.dto.IndexedCollectionsDto;

interface ObjectProfiler {

	IndexedCollectionsDto profileObject(DeploymentDto deploymentDto, TARGET_SOURCE enumTargetSource, Object object);

}
