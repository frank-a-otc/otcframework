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
package org.otcframework.common.dto;

import java.util.Map;

import org.otcframework.common.executor.CodeExecutor;

// TODO: Auto-generated Javadoc
/**
 * The Class DeploymentDto.
 */
public final class DeploymentDto {

	/** The deployment id. */
	public String deploymentId;
	
	/** The otc namespace. */
	public String otcNamespace;
	
	/** The otc file name. */
	public String otcFileName;
	
	/** The deployment file name. */
	public String deploymentFileName;
	
	/** The is error. */
	public boolean isError;

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
	
	/** The otc code executor. */
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
