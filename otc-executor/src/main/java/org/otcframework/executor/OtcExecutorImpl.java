/**
* Copyright (c) otcframework.org
*
* @author  Franklin J Abel
* @version 1.0
* @since   2020-06-08 
*
* This file is part of the OTC framework.
* 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      https://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*/
package org.otcframework.executor;

import java.util.Map;

import org.otcframework.common.OtcConstants;
import org.otcframework.common.OtcConstants.TARGET_SOURCE;
import org.otcframework.common.dto.RegistryDto;
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
		otcRegistry.register();
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
		RegistryDto registryDto = otcRegistry.retrieveRegistryDto(otcNamespace, source, targetClz);
		if (registryDto == null) {
			String otcFile = OtcUtils.createRegistryId(otcNamespace, source, targetClz)
					+ OtcConstants.OTC_SCRIPT_EXTN;
			String errMsg = "Oops... Cannot proceed. Missing or uncompiled OTC file! " + otcFile;
			LOGGER.error(errMsg);
			throw new OtcExecutorException(errMsg);
		}
		IndexedCollectionsDto indexedCollectionsDto = null;
		if (source != null && registryDto.isProfilingRequried) {
			indexedCollectionsDto = objectIndexer.indexObject(registryDto, TARGET_SOURCE.SOURCE, source);
		}
		CodeExecutor<S, T> codeExecutor = registryDto.codeExecutor;
		return (T) codeExecutor.execute(source, indexedCollectionsDto, data);
	}
}
