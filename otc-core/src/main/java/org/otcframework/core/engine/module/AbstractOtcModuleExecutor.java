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
package org.otcframework.core.engine.module;

import java.util.Map;

import org.otcframework.common.engine.OtcEngine;
import org.otcframework.core.engine.OtcEngineImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// TODO: Auto-generated Javadoc

/**
 * The Class AbstractOtcModuleExecutor.
 */
public abstract class AbstractOtcModuleExecutor {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractOtcModuleExecutor.class);

	/** The otc engine. */
	private static OtcEngine otcEngine = OtcEngineImpl.getInstance();

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
			newTarget = (T) otcEngine.executeOtc(otcNamespace, source, target.getClass(), config);
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
		}
		return newTarget;
	}
}
