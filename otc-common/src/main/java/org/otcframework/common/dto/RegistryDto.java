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
package org.otcframework.common.dto;

import org.otcframework.common.executor.CodeExecutor;

import java.util.Map;

/**
 * The Class RegistryDto.
 */
// TODO: Auto-generated Javadoc
public final class RegistryDto {

	/** The registry id. */
	public String registryId;

	/** The otc namespace. */
	public String otcNamespace;

	/** The otc file name. */
	public String otcFileName;

	/** The registry file name. */
	public String registryFileName;

	/** The is error. */
	public boolean hasError;

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

	/** The code executor. */
	public CodeExecutor codeExecutor;

	/**
	 * The Class CompiledInfo.
	 */
	public static final class CompiledInfo {

		/** The id. */
		public String id;

		/** The factory class name. */
		public String factoryClassName;

		/** The source OCD stem. */
		public OtcCommandDto sourceOCDStem;

		/** The target OCD stem. */
		public OtcCommandDto targetOCDStem;

		/** The source otc chain dto. */
		public OtcChainDto sourceOtcChainDto;

		/** The target otc chain dto. */
		public OtcChainDto targetOtcChainDto;
	}
}
