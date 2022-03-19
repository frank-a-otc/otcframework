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

import java.util.Map;

import org.otcframework.common.OtcConstants;
import org.otcframework.common.OtcConstants.TARGET_SOURCE;
import org.otcframework.common.dto.DeploymentDto;
import org.otcframework.common.engine.indexer.dto.IndexedCollectionsDto;
import org.otcframework.common.executor.CodeExecutor;
import org.otcframework.common.util.OtcUtils;
import org.otcframework.executor.exception.OtcExecutorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class OtcExecutorImpl.
 */
// TODO: Auto-generated Javadoc
public final class OtcExecutorImpl implements OtcExecutor {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(OtcExecutorImpl.class);

	/** The Constant otcRegistry. */
	private static final OtcRegistry otcRegistry = (OtcRegistryImpl) OtcRegistryImpl.instance;

	/** The Constant objectIndexer. */
	private static final ObjectIndexer objectIndexer = ObjectIndexerImpl.getInstance();

	/** The otc executor. */
	private static OtcExecutor otcExecutor = new OtcExecutorImpl();;

	/**
	 * Instantiates a new otc executor impl.
	 */
	private OtcExecutorImpl() {
	}

	/**
	 * Gets the single instance of OtcExecutorImpl.
	 *
	 * @return single instance of OtcExecutorImpl
	 */
	public static OtcExecutor getInstance() {
		return otcExecutor;
	}

	/**
	 * Execute otc.
	 *
	 * @param <T>          the generic type
	 * @param otcNamespace the otc namespace
	 * @param targetClz    the target clz
	 * @param data         the data
	 * @return the t
	 */
	@Override
	public <T> T execute(String otcNamespace, Class<T> targetClz, Map<String, Object> data) {
		T target = execute(otcNamespace, null, targetClz, data);
		return target;
	}

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
	@Override
	public <T, S> T execute(String otcNamespace, S source, Class<T> targetClz, Map<String, Object> data) {
		DeploymentDto deploymentDto = otcRegistry.retrieveDeploymentDto(otcNamespace, source, targetClz);
		if (deploymentDto == null) {
			String otcFile = OtcUtils.createDeploymentId(otcNamespace, source, targetClz)
					+ OtcConstants.OTC_SCRIPT_EXTN;
			String errMsg = "Oops... Cannot proceed. Missing or uncompiled OTC file! " + otcFile;
			LOGGER.error(errMsg);
			throw new OtcExecutorException(errMsg);
		}
		IndexedCollectionsDto indexedCollectionsDto = null;
		if (source != null && deploymentDto.isProfilingRequried) {
			indexedCollectionsDto = objectIndexer.indexObject(deploymentDto, TARGET_SOURCE.SOURCE, source);
		}
		CodeExecutor<S, T> codeExecutor = deploymentDto.codeExecutor;
		return (T) codeExecutor.execute(source, indexedCollectionsDto, data);
	}
}
