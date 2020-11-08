package org.otcl2.common.dto;

import java.util.Map;

import org.otcl2.common.engine.executor.CodeExecutor;

public final class DeploymentDto {

	public String deploymentId;
	public String otclNamespace;
	public String otclFileName;
	public String mainClass;
	public Class<?> sourceClz;
	public Class<?> targetClz;
	public Map<String, CompiledInfo> compiledInfos;
	public boolean isProfilingRequried;
	public CodeExecutor otclCodeExecutor;
	
	public static final class CompiledInfo {
		public String id;
		public String factoryClassName;
		public OtclCommandDto sourceOCDStem;
		public OtclCommandDto targetOCDStem;
		public OtclChainDto sourceOtclChainDto;
		public OtclChainDto targetOtclChainDto;
	}
	
}
