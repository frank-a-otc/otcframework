package org.otcl2.core.engine;

import java.util.Map;

import org.otcl2.common.OtclConstants;
import org.otcl2.common.OtclConstants.TARGET_SOURCE;
import org.otcl2.common.dto.DeploymentDto;
import org.otcl2.common.engine.executor.CodeExecutor;
import org.otcl2.common.engine.profiler.dto.IndexedCollectionsDto;
import org.otcl2.common.util.OtclUtils;
import org.otcl2.core.engine.exception.OtclEngineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class OtclExecutorImpl implements OtclExecutor {

	private static final Logger LOGGER = LoggerFactory.getLogger(OtclExecutorImpl.class);

	private static final DeploymentContainer deploymentContainer = (DeploymentContainerImpl) 
			DeploymentContainerImpl.getInstance();
	private static final ObjectProfiler objectProfiler = ObjectProfilerImpl.getInstance();

	private static OtclExecutor otclExecutor = new OtclExecutorImpl();;

	private OtclExecutorImpl() {
	}

	public static OtclExecutor getInstance() {
		return otclExecutor;
	}

	@Override
	public <T, S> T executeOtcl(Class<T> targetClz, Map<String, Object> data) {
		T target = executeOtcl(null, null, targetClz, data);
		return target;
	}

	@Override
	public <T> T executeOtcl(String otclNamespace, Class<T> targetClz, Map<String, Object> data) {
		T target = executeOtcl(otclNamespace, null, targetClz, data);
		return target;
	}

	@Override
	public <T, S> T executeOtcl(S source, Class<T> targetClz, Map<String, Object> data) {
		T target = executeOtcl(null, source, targetClz, data);
		return target;
	}

	@Override
	public <T, S> T executeOtcl(String otclNamespace, S source, Class<T> targetClz, Map<String, Object> data) {
		long startTime = System.nanoTime();
		DeploymentDto deploymentDto = deploymentContainer.retrieveDeploymentDto(otclNamespace, source, targetClz);
		if (deploymentDto == null) {
			String deploymentId = OtclUtils.createDeploymentId(otclNamespace, source, targetClz) +
					OtclConstants.OTCL_FILE_EXTN; 
			String errMsg = "Oops... Cannot proceed. Missing or uncompiled OTCL file! " + deploymentId;
			LOGGER.error(errMsg);
			throw new OtclEngineException(errMsg);
		}
		IndexedCollectionsDto indexedCollectionsDto = null;
		if (source != null && deploymentDto.isProfilingRequried) {
			indexedCollectionsDto = objectProfiler.profileObject(deploymentDto, TARGET_SOURCE.SOURCE, source);
		}
		CodeExecutor<S, T> otclCodeExecutor = deploymentDto.otclCodeExecutor;
		return (T) otclCodeExecutor.execute(source, indexedCollectionsDto, data);
	}
}
