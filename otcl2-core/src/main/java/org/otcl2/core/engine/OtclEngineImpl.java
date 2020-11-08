/**
* Copyright (c) otcl2.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-05-06 
*/
package org.otcl2.core.engine;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.otcl2.common.engine.OtclEngine;
import org.otcl2.core.engine.compiler.OtclCompiler;
import org.otcl2.core.engine.compiler.OtclCompilerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum OtclEngineImpl implements OtclEngine {
	instance;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(OtclEngineImpl.class);

	private static final OtclCompiler otclCompiler = OtclCompilerImpl.getInstance();
	private static final DeploymentContainer deploymentContainer = DeploymentContainerImpl.getInstance();
	private static final OtclExecutor otclExecutor = OtclExecutorImpl.getInstance();

	private OtclEngineImpl() {
	}

	public static class Init {
		@PostConstruct
		public static void init() {
			instance.deploy();
		}
	}

	public static OtclEngine getInstance() {
		return instance;
	}

	@Override
	public void compileOtcl() {
		otclCompiler.compileOtcl();
		return;
	}
	
	@Override
	public void compileSourceCode() {
		otclCompiler.compileSourceCode();
		return;
	}
	
	@Override
	public void deploy() {
		deploymentContainer.deploy();
	}
	
	@Override
	public <T, S> T executeOtcl(Class<T> targetClz, Map<String, Object> data) {
		T target = otclExecutor.executeOtcl(targetClz, data);
		return target;
	}
	
	@Override
	public <T, S> T executeOtcl(S source, Class<T> targetClz, Map<String, Object> data) {
		T target = otclExecutor.executeOtcl(source, targetClz, data);
		return target;
	}

	@Override
	public <T, S> T executeOtcl(String otclNamespace, S source, Class<T> targetClz, Map<String, Object> data) {
		return otclExecutor.executeOtcl(otclNamespace, source, targetClz, data);
	}
}
