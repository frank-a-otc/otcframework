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

import org.otcl2.common.engine.OtclEngine;
import org.otcl2.core.engine.compiler.OtclCompiler;
import org.otcl2.core.engine.compiler.OtclCompilerImpl;

// TODO: Auto-generated Javadoc
/**
 * The Enum OtclEngineImpl.
 */
public enum OtclEngineImpl implements OtclEngine {
	
	/** The instance. */
	instance;

	/** The Constant otclCompiler. */
	private static final OtclCompiler otclCompiler = OtclCompilerImpl.getInstance();
	
	/** The Constant deploymentContainer. */
	private static final DeploymentContainer deploymentContainer = DeploymentContainerImpl.getInstance();
	
	/** The Constant otclExecutor. */
	private static final OtclExecutor otclExecutor = OtclExecutorImpl.getInstance();

	/**
	 * Instantiates a new otcl engine impl.
	 */
	private OtclEngineImpl() {
	}

	static {
		instance.register();
	}
	
	/**
	 * Gets the single instance of OtclEngineImpl.
	 *
	 * @return single instance of OtclEngineImpl
	 */
	public static OtclEngine getInstance() {
		return instance;
	}

	/**
	 * Compile otcl.
	 */
	@Override
	public void compileOtcl() {
		otclCompiler.compileOtcl();
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
		deploymentContainer.deploy();
	}
	
	/**
	 * Execute otcl.
	 *
	 * @param <T> the generic type
	 * @param <S> the generic type
	 * @param targetClz the target clz
	 * @param data the data
	 * @return the t
	 */
	@Override
	public <T, S> T executeOtcl(String otclNamespace, Class<T> targetClz, Map<String, Object> data) {
		T target = otclExecutor.executeOtcl(otclNamespace, targetClz, data);
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
		return otclExecutor.executeOtcl(otclNamespace, source, targetClz, data);
	}
}
