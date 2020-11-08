package org.otcl2.common.dto;

import org.otcl2.common.dto.OtclFileDto.Command;
import org.otcl2.common.dto.OtclFileDto.Execute;
import org.otcl2.common.dto.OtclFileDto.Execute.OtclModule;
import org.otcl2.common.dto.OtclFileDto.OtclScript;

public class ScriptDto {

	public Command command;
	public OtclChainDto targetOtclChainDto;
	public OtclChainDto sourceOtclChainDto;
	public boolean hasSetValues;
	public boolean hasExecuteModule;
	public boolean hasExecuteConverter;
	public boolean hasExecutionOrder;

	private ScriptDto() { }

	public ScriptDto(OtclScript script) {
		if (script.copy != null) {
			command = script.copy;
		} else {
			command = script.execute;
		}
		if (command instanceof Execute) {
			Execute execute = (Execute) command;
			if (execute.executionOrder != null) {
				hasExecutionOrder = true;
			}
			if (execute.otclConverter != null) {
				hasExecuteConverter = true;
			}
			if (execute.otclModule != null) {
				hasExecuteModule = true;
			}
		}
	}
	
	public ScriptDto clone() {
		ScriptDto scriptDto = new ScriptDto();
		if (command instanceof Execute) {
			Execute execute = (Execute) command;
			Execute executeClone = new Execute();
			scriptDto.command = executeClone;
			if (execute.otclModule != null) {
				executeClone.otclModule = new OtclModule();
				executeClone.otclModule.otclNamespace = execute.otclModule.otclNamespace;
			}
			executeClone.otclConverter = execute.otclConverter;
			executeClone.executionOrder = execute.executionOrder;
		}
		scriptDto.hasSetValues = hasSetValues;
		scriptDto.hasExecuteModule = hasExecuteModule;
		scriptDto.hasExecuteConverter = hasExecuteConverter;
		scriptDto.targetOtclChainDto = targetOtclChainDto;
		scriptDto.sourceOtclChainDto = sourceOtclChainDto;
		return scriptDto;
	}

	@Override
	public String toString() {
		return "ScriptDto [command=" + command + ", targetOtclChainDto=" + targetOtclChainDto
				+ ", sourceOtclChainDto=" + sourceOtclChainDto + ", hasSetValueExtension=" + hasSetValues
				+ ", hasExecuteModule=" + hasExecuteModule + ", hasExecuteConverter=" + hasExecuteConverter
				+ ", hasExecutionOrder=" + hasExecutionOrder + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((command == null) ? 0 : command.hashCode());
		result = prime * result + (hasExecuteConverter ? 1231 : 1237);
		result = prime * result + (hasExecuteModule ? 1231 : 1237);
		result = prime * result + (hasExecutionOrder ? 1231 : 1237);
		result = prime * result + (hasSetValues ? 1231 : 1237);
		result = prime * result + ((sourceOtclChainDto == null) ? 0 : sourceOtclChainDto.hashCode());
		result = prime * result + ((targetOtclChainDto == null) ? 0 : targetOtclChainDto.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ScriptDto other = (ScriptDto) obj;
		if (command == null) {
			if (other.command != null)
				return false;
		} else if (!command.equals(other.command))
			return false;
		if (hasExecuteConverter != other.hasExecuteConverter)
			return false;
		if (hasExecuteModule != other.hasExecuteModule)
			return false;
		if (hasExecutionOrder != other.hasExecutionOrder)
			return false;
		if (hasSetValues != other.hasSetValues)
			return false;
		if (sourceOtclChainDto == null) {
			if (other.sourceOtclChainDto != null)
				return false;
		} else if (!sourceOtclChainDto.equals(other.sourceOtclChainDto))
			return false;
		if (targetOtclChainDto == null) {
			if (other.targetOtclChainDto != null)
				return false;
		} else if (!targetOtclChainDto.equals(other.targetOtclChainDto))
			return false;
		return true;
	}

}
