/**
* Copyright (c) otclfoundation.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*
* This file is part of the OTCL framework.
* 
*  The OTCL framework is free software: you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  the Free Software Foundation, version 3 of the License.
*
*  The OTCL framework is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU General Public License for more details.
*
*  A copy of the GNU General Public License is made available as 'License.md' file, 
*  along with OTCL framework project.  If not, see <https://www.gnu.org/licenses/>.
*
*/
package org.otcl2.core.engine;

import java.util.Map;

import org.otcl2.common.OtclConstants;
import org.otcl2.common.OtclConstants.TARGET_SOURCE;
import org.otcl2.common.dto.DeploymentDto;
import org.otcl2.common.engine.profiler.dto.IndexedCollectionsDto;
import org.otcl2.common.executor.CodeExecutor;
import org.otcl2.common.util.OtclUtils;
import org.otcl2.core.engine.exception.OtclEngineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class OtclExecutorImpl.
 */
final class OtclExecutorImpl implements OtclExecutor {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(OtclExecutorImpl.class);

	/** The Constant deploymentContainer. */
	private static final DeploymentContainer deploymentContainer = (DeploymentContainerImpl) 
			DeploymentContainerImpl.getInstance();
	
	/** The Constant objectProfiler. */
	private static final ObjectProfiler objectProfiler = ObjectProfilerImpl.getInstance();

	/** The otcl executor. */
	private static OtclExecutor otclExecutor = new OtclExecutorImpl();;

	/**
	 * Instantiates a new otcl executor impl.
	 */
	private OtclExecutorImpl() {
	}

	/**
	 * Gets the single instance of OtclExecutorImpl.
	 *
	 * @return single instance of OtclExecutorImpl
	 */
	public static OtclExecutor getInstance() {
		return otclExecutor;
	}

	/**
	 * Execute otcl.
	 *
	 * @param <T> the generic type
	 * @param otclNamespace the otcl namespace
	 * @param targetClz the target clz
	 * @param data the data
	 * @return the t
	 */
	@Override
	public <T> T executeOtcl(String otclNamespace, Class<T> targetClz, Map<String, Object> data) {
		T target = executeOtcl(otclNamespace, null, targetClz, data);
		return target;
	}

	/**
	 * Execute otcl.
	 *
	 * @param <T> the generic type
	 * @param <S> the generic type
	 * @param otclNamespace the otcl namespace
	 * @param source the source
	 * @param targetClz the target clz
	 * @param data the data
	 * @return the t
	 */
	@Override
	public <T, S> T executeOtcl(String otclNamespace, S source, Class<T> targetClz, Map<String, Object> data) {
		DeploymentDto deploymentDto = deploymentContainer.retrieveDeploymentDto(otclNamespace, source, targetClz);
		if (deploymentDto == null) {
			String otclFile = OtclUtils.createDeploymentId(otclNamespace, source, targetClz) +
					OtclConstants.OTCL_SCRIPT_EXTN; 
			String errMsg = "Oops... Cannot proceed. Missing or uncompiled OTCL file! " + otclFile;
			LOGGER.error(errMsg);
			throw new OtclEngineException(errMsg);
		}
		IndexedCollectionsDto indexedCollectionsDto = null;
		if (source != null && deploymentDto.isProfilingRequried) {
			indexedCollectionsDto = objectProfiler.profileObject(deploymentDto, TARGET_SOURCE.SOURCE, source);
		}
		CodeExecutor<S, T> codeExecutor = deploymentDto.codeExecutor;
		return (T) codeExecutor.execute(source, indexedCollectionsDto, data);
	}
}
