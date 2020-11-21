package org.otcl2.common.dto;

import java.util.Map;

import org.otcl2.common.engine.executor.CodeExecutor;

// TODO: Auto-generated Javadoc
/**
 * The Class DeploymentDto.
 */
public final class DeploymentDto {

	/** The deployment id. */
	public String deploymentId;
	
	/** The otcl namespace. */
	public String otclNamespace;
	
	/** The otcl file name. */
	public String otclFileName;
	
	/** The main class. */
	public String mainClass;
	
	/** The source clz. */
	public Class<?> sourceClz;
	
	/** The target clz. */
	public Class<?> targetClz;
	
	/** The compiled infos. */
	public Map<String, CompiledInfo> compiledInfos;
	
	/** The is profiling requried. */
	public boolean isProfilingRequried;
	
	/** The otcl code executor. */
	public CodeExecutor otclCodeExecutor;
	
	/**
	 * The Class CompiledInfo.
	 */
	public static final class CompiledInfo {
		
		/** The id. */
		public String id;
		
		/** The factory class name. */
		public String factoryClassName;
		
		/** The source OCD stem. */
		public OtclCommandDto sourceOCDStem;
		
		/** The target OCD stem. */
		public OtclCommandDto targetOCDStem;
		
		/** The source otcl chain dto. */
		public OtclChainDto sourceOtclChainDto;
		
		/** The target otcl chain dto. */
		public OtclChainDto targetOtclChainDto;
	}
	
}
