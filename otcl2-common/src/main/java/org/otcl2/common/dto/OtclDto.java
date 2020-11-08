/**
* Copyright (c) otcl2.org
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

public final class OtclDto {

	public OtclFileDto otclFileDto;
	public String otclNamespace;
	public String otclFileName;
	public Class<?> sourceClz;
	public Class<?> targetClz;
	public ClassDto mainClassDto;
//	public Map<String, ScriptGroupDto> groupedScriptDtos;
	public List<ScriptDto> scriptDtos;
	public Map<String, OtclCommandDto> sourceOCDStems;
	public Map<String, OtclCommandDto> targetOCDStems;

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

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder {
		private String otclNamespace;
		private String otclFileName;
		public String deploymentId;
		private Class<?> sourceClz;
		private Class<?> targetClz;
//		private Map<String, ScriptGroupDto> groupedScriptDtos;

		public List<ScriptDto> scriptDtos;

		private Map<String, OtclCommandDto> sourceOCDStems;
		private Map<String, OtclCommandDto> targetOCDStems;

		public Builder addOtclNamespace(String otclNamespace) {
			this.otclNamespace = otclNamespace;
			return this;
		}

		public Builder addOtclFileName(String otclFileName) {
			this.otclFileName = otclFileName;
			return this;
		}

		public Builder addDeploymentId(String deploymentId) {
			this.deploymentId = deploymentId;
			return this;
		}

		public Builder addSourceClz(Class<?> sourceClz) {
			this.sourceClz = sourceClz;
			return this;
		}

		public Builder addTargetClz(Class<?> targetClz) {
			this.targetClz = targetClz;
			return this;
		}

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

		public Builder addSourceOtclCommandDtoStem(OtclCommandDto sourceOCD) {
			if (sourceOCDStems == null) {
				sourceOCDStems = new HashMap<>();
			}
			if (!sourceOCDStems.containsKey(sourceOCD.otclToken)) {
				sourceOCDStems.put(sourceOCD.otclToken, sourceOCD);
			}
			return this;
		}

		public Builder addTargetOtclCommandDtoStem(OtclCommandDto targetOCD) {
			if (targetOCDStems == null) {
				targetOCDStems = new HashMap<>();
			}
			if (!targetOCDStems.containsKey(targetOCD.otclToken)) {
				targetOCDStems.put(targetOCD.otclToken, targetOCD);
			}
			return this;
		}

		public OtclDto build() {
			return new OtclDto(this);
		}
	}

}
