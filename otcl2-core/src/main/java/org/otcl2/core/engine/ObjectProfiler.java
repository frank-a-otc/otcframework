/**
* Copyright (c) otclfoundation.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*/
package org.otcl2.core.engine;

import org.otcl2.common.OtclConstants.TARGET_SOURCE;
import org.otcl2.common.dto.DeploymentDto;
import org.otcl2.common.engine.profiler.dto.IndexedCollectionsDto;

// TODO: Auto-generated Javadoc
/**
 * The Interface ObjectProfiler.
 */
interface ObjectProfiler {

	/**
	 * Profile object.
	 *
	 * @param deploymentDto the deployment dto
	 * @param enumTargetSource the enum target source
	 * @param object the object
	 * @return the indexed collections dto
	 */
	IndexedCollectionsDto profileObject(DeploymentDto deploymentDto, TARGET_SOURCE enumTargetSource, Object object);

}
