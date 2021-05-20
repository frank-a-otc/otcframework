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
package org.otcl2.core.engine.module;

import java.util.Map;

import org.otcl2.common.engine.OtclEngine;
import org.otcl2.core.engine.OtclEngineImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractOtclModuleExecutor {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractOtclModuleExecutor.class);

	private static OtclEngine otclEngine = OtclEngineImpl.getInstance();
	
	protected static <S, T> T executeModule(String otclNamespace, S source, T target, Map<String, Object> config) {
		LOGGER.debug(AbstractOtclModuleExecutor.class.getName() + " called!");
		T newTarget = null;
		try {
			newTarget = (T) otclEngine.executeOtcl(otclNamespace, source, target.getClass(), config);
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
		}
		return newTarget;
	}

}
