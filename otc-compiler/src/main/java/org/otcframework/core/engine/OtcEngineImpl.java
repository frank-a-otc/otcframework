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
package org.otcframework.core.engine;

import java.util.Map;

import org.otcframework.common.config.OtcConfig;
import org.otcframework.common.engine.OtcEngine;
import org.otcframework.core.engine.compiler.OtclCompiler;
import org.otcframework.core.engine.compiler.OtclCompilerImpl;
import org.otcframework.executor.OtcExecutor;
import org.otcframework.executor.OtcExecutorImpl;
import org.otcframework.executor.OtcRegistry;
import org.otcframework.executor.OtcRegistryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Enum OtcEngineImpl.
 */
// TODO: Auto-generated Javadoc
public final class OtcEngineImpl implements OtcEngine {

	private static final OtcEngine otclEngine = new OtcEngineImpl();

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(OtcEngineImpl.class);

	/** The Constant otclCompiler. */
	private static final OtclCompiler otclCompiler = OtclCompilerImpl.getInstance();

	/** The Constant otcRegistry. */
	private static final OtcRegistry otcRegistry = OtcRegistryImpl.instance;
	
	/** The Constant otcExecutor. */
	private static final OtcExecutor otcExecutor = OtcExecutorImpl.getInstance();

	/**
	 * Instantiates a new otc engine impl.
	 */
	private OtcEngineImpl() {
	}

	static {
		otclEngine.register();
	}

	/**
	 * Gets the single instance of OtcEngineImpl.
	 *
	 * @return single instance of OtcEngineImpl
	 */
	public static OtcEngine getInstance() {
		return otclEngine;
	}

	/**
	 * Compile otc.
	 */
	@Override
	public void compileOtcl() {
		otclCompiler.compile();
		return;
	}

	/**
	 * Compile source code.
	 */
	@Override
	public void compileSourceCode() {
		otclCompiler.compileSourceCode();
		return;
	}

	/**
	 * Register.
	 */
	@Override
	public void register() {
		LOGGER.info(
				"For detailed logging, set 'otc.log.level' property in '{}/config/log.properties' file to 'DEBUG' mode.",
				OtcConfig.getOtcHomeLocation());
		otcRegistry.register();
	}

	/**
	 * Execute otc.
	 *
	 * @param <T>          the generic type
	 * @param <S>          the generic type
	 * @param otcNamespace the otc namespace
	 * @param targetClz    the target clz
	 * @param data         the data
	 * @return the t
	 */
	@Override
	public <T, S> T executeOtc(String otcNamespace, Class<T> targetClz, Map<String, Object> data) {
		T target = otcExecutor.execute(otcNamespace, targetClz, data);
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
	public <T, S> T executeOtc(String otcNamespace, S source, Class<T> targetClz, Map<String, Object> data) {
		return otcExecutor.execute(otcNamespace, source, targetClz, data);
	}
}
