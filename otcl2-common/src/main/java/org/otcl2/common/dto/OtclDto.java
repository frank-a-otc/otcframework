/**
* Copyright (c) otclfoundation.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*/
package org.otcl2.common.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * The Class OtclDto.
 */
public final class OtclDto {

	/** The otcl file dto. */
	public OtclFileDto otclFileDto;
	
	/** The otcl namespace. */
	public String otclNamespace;
	
	/** The otcl file name. */
	public String otclFileName;
	
	/** The source clz. */
	public Class<?> sourceClz;
	
	/** The target clz. */
	public Class<?> targetClz;
	
	/** The main class dto. */
	public ClassDto mainClassDto;

/** The script dtos. */
//	public Map<String, ScriptGroupDto> groupedScriptDtos;
	public List<ScriptDto> scriptDtos;
	
	/** The source OCD stems. */
	public Map<String, OtclCommandDto> sourceOCDStems;
	
	/** The target OCD stems. */
	public Map<String, OtclCommandDto> targetOCDStems;

	/**
	 * Instantiates a new otcl dto.
	 *
	 * @param builder the builder
	 */
	private OtclDto(Builder builder) {
		otclNamespace = builder.otclNamespace;
		otclFileName = builder.otclFileName;
		sourceClz = builder.sourceClz;
		targetClz = builder.targetClz;
//		groupedScriptDtos = builder.groupedScriptDtos;
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
		
		/** The otcl namespace. */
		private String otclNamespace;
		
		/** The otcl file name. */
		private String otclFileName;
		
		/** The deployment id. */
		public String deploymentId;
		
		/** The source clz. */
		private Class<?> sourceClz;
		
		/** The target clz. */
		private Class<?> targetClz;
//		private Map<String, ScriptGroupDto> groupedScriptDtos;

		/** The script dtos. */
public List<ScriptDto> scriptDtos;

		/** The source OCD stems. */
		private Map<String, OtclCommandDto> sourceOCDStems;
		
		/** The target OCD stems. */
		private Map<String, OtclCommandDto> targetOCDStems;

		/**
		 * Adds the otcl namespace.
		 *
		 * @param otclNamespace the otcl namespace
		 * @return the builder
		 */
		public Builder addOtclNamespace(String otclNamespace) {
			this.otclNamespace = otclNamespace;
			return this;
		}

		/**
		 * Adds the otcl file name.
		 *
		 * @param otclFileName the otcl file name
		 * @return the builder
		 */
		public Builder addOtclFileName(String otclFileName) {
			this.otclFileName = otclFileName;
			return this;
		}

		/**
		 * Adds the deployment id.
		 *
		 * @param deploymentId the deployment id
		 * @return the builder
		 */
		public Builder addDeploymentId(String deploymentId) {
			this.deploymentId = deploymentId;
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

//		public Builder addGroupedScriptDtos(Map<String, ScriptGroupDto> groupedScriptDtos) {
//			this.groupedScriptDtos = groupedScriptDtos;
//			return this;
//		}

		/**
 * Adds the source otcl command dto stem.
 *
 * @param sourceOCD the source OCD
 * @return the builder
 */
public Builder addSourceOtclCommandDtoStem(OtclCommandDto sourceOCD) {
			if (sourceOCDStems == null) {
				sourceOCDStems = new HashMap<>();
			}
			if (!sourceOCDStems.containsKey(sourceOCD.otclToken)) {
				sourceOCDStems.put(sourceOCD.otclToken, sourceOCD);
			}
			return this;
		}

		/**
		 * Adds the target otcl command dto stem.
		 *
		 * @param targetOCD the target OCD
		 * @return the builder
		 */
		public Builder addTargetOtclCommandDtoStem(OtclCommandDto targetOCD) {
			if (targetOCDStems == null) {
				targetOCDStems = new HashMap<>();
			}
			if (!targetOCDStems.containsKey(targetOCD.otclToken)) {
				targetOCDStems.put(targetOCD.otclToken, targetOCD);
			}
			return this;
		}

		/**
		 * Builds the.
		 *
		 * @return the otcl dto
		 */
		public OtclDto build() {
			return new OtclDto(this);
		}
	}

}
