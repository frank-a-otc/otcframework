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

import org.otcframework.common.dto.otc.OtcFileDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Class OtcDto.
 */
public final class OtcDto {

	/** The otc file dto. */
	public OtcFileDto otcFileDto;

	/** The otc namespace. */
	public String otcNamespace;

	/** The otc file name. */
	public String otcFileName;

	/** The source clz. */
	public Class<?> sourceClz;

	/** The target clz. */
	public Class<?> targetClz;

	/** The main class dto. */
	public ClassDto mainClassDto;

	/** The script dtos. */
	public List<ScriptDto> scriptDtos;

	/** The source OCD stems. */
	public Map<String, OtcCommandDto> sourceOCDStems;

	/** The target OCD stems. */
	public Map<String, OtcCommandDto> targetOCDStems;

	/**
	 * Instantiates a new otc dto.
	 *
	 * @param builder the builder
	 */
	private OtcDto(Builder builder) {
		otcNamespace = builder.otcNamespace;
		otcFileName = builder.otcFileName;
		sourceClz = builder.sourceClz;
		targetClz = builder.targetClz;
		targetOCDStems = builder.targetOCDStems;
		sourceOCDStems = builder.sourceOCDStems;
		scriptDtos = builder.scriptDtos;
	}

	/**
	 * New builder.
	 *
	 * @return the builder
	 */
	public static Builder newBuilder() {
		return new Builder();
	}

	/**
	 * The Class Builder.
	 */
	public static class Builder {

		/** The otc namespace. */
		private String otcNamespace;

		/** The otc file name. */
		private String otcFileName;

		/** The registryId id. */
		public String registryId;

		/** The source clz. */
		private Class<?> sourceClz;

		/** The target clz. */
		private Class<?> targetClz;

		/** The script dtos. */
		public List<ScriptDto> scriptDtos;

		/** The source OCD stems. */
		private Map<String, OtcCommandDto> sourceOCDStems;

		/** The target OCD stems. */
		private Map<String, OtcCommandDto> targetOCDStems;

		/**
		 * Adds the otc namespace.
		 *
		 * @param otcNamespace the otc namespace
		 * @return the builder
		 */
		public Builder addOtcNamespace(String otcNamespace) {
			this.otcNamespace = otcNamespace;
			return this;
		}

		/**
		 * Adds the otc file name.
		 *
		 * @param otcFileName the otc file name
		 * @return the builder
		 */
		public Builder addOtcFileName(String otcFileName) {
			this.otcFileName = otcFileName;
			return this;
		}

		/**
		 * Adds the registry id.
		 *
		 * @param registryId the registry id
		 * @return the builder
		 */
		public Builder addRegistryId(String registryId) {
			this.registryId = registryId;
			return this;
		}

		/**
		 * Adds the source clz.
		 *
		 * @param sourceClz the source clz
		 * @return the builder
		 */
		public Builder addSourceClz(Class<?> sourceClz) {
			this.sourceClz = sourceClz;
			return this;
		}

		/**
		 * Adds the target clz.
		 *
		 * @param targetClz the target clz
		 * @return the builder
		 */
		public Builder addTargetClz(Class<?> targetClz) {
			this.targetClz = targetClz;
			return this;
		}

		/**
		 * Adds the script dto.
		 *
		 * @param scriptDto the script dto
		 * @return the builder
		 */
		public Builder addScriptDto(ScriptDto scriptDto) {
			if (scriptDtos == null) {
				scriptDtos = new ArrayList<>();
			}
			scriptDtos.add(scriptDto);
			return this;
		}

		/**
		 * Adds the source otc command dto stem.
		 *
		 * @param sourceOCD the source OCD
		 * @return the builder
		 */
		public Builder addSourceOtcCommandDtoStem(OtcCommandDto sourceOCD) {
			if (sourceOCDStems == null) {
				sourceOCDStems = new HashMap<>();
			}
			if (!sourceOCDStems.containsKey(sourceOCD.otcToken)) {
				sourceOCDStems.put(sourceOCD.otcToken, sourceOCD);
			}
			return this;
		}

		/**
		 * Adds the target otc command dto stem.
		 *
		 * @param targetOCD the target OCD
		 * @return the builder
		 */
		public Builder addTargetOtcCommandDtoStem(OtcCommandDto targetOCD) {
			if (targetOCDStems == null) {
				targetOCDStems = new HashMap<>();
			}
			if (!targetOCDStems.containsKey(targetOCD.otcToken)) {
				targetOCDStems.put(targetOCD.otcToken, targetOCD);
			}
			return this;
		}

		/**
		 * Builds the.
		 *
		 * @return the otc dto
		 */
		public OtcDto build() {
			return new OtcDto(this);
		}
	}
}
