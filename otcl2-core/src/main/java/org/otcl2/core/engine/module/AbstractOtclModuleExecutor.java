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
		LOGGER.debug(AbstractOtclModuleExecutor.class.getSimpleName() + " called!");
		T newTarget = null;
		try {
			newTarget = (T) otclEngine.executeOtcl(otclNamespace, source, target.getClass(), config);
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
		}
		return newTarget;
	}

}
