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
package org.otcframework.executor.module;

import org.otcframework.executor.OtcExecutor;
import org.otcframework.executor.OtcExecutorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * The Class AbstractOtcModuleExecutor.
 */
public abstract class AbstractOtcModuleExecutor {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractOtcModuleExecutor.class);

	/** The otc engine. */
	private static OtcExecutor otcExecutor = OtcExecutorImpl.getInstance();

	/**
	 * Execute module.
	 *
	 * @param <S>          the generic type
	 * @param <T>          the generic type
	 * @param otcNamespace the otc namespace
	 * @param source       the source
	 * @param target       the target
	 * @param config       the config
	 * @return the t
	 */
	protected static <S, T> T executeModule(String otcNamespace, S source, T target, Map<String, Object> config) {
		LOGGER.debug(AbstractOtcModuleExecutor.class.getName() + " called!");
		T newTarget = null;
		try {
			newTarget = (T) otcExecutor.execute(otcNamespace, source, target.getClass(), config);
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
		}
		return newTarget;
	}
}
