/**
* Copyright (c) otclfoundation.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*
* This file is part of the OTCL framework.
* 
*  The OTCL framework is free software: you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  the Free Software Foundation, version 3 of the License.
*
*  The OTCL framework is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU General Public License for more details.
*
*  A copy of the GNU General Public License is made available as 'License.md' file, 
*  along with OTCL framework project.  If not, see <https://www.gnu.org/licenses/>.
*
*/
package org.otcl2.common.dto;

import org.otcl2.common.dto.otcl.OtclFileDto.CommandCommonParams;
import org.otcl2.common.dto.otcl.OtclFileDto.Execute;
import org.otcl2.common.dto.otcl.OtclFileDto.Execute.OtclModule;
import org.otcl2.common.dto.otcl.OtclFileDto.OtclCommands;

// TODO: Auto-generated Javadoc
/**
 * The Class ScriptDto.
 */
public class ScriptDto {

	/** The command. */
	public CommandCommonParams command;
	
	/** The target otcl chain dto. */
	public OtclChainDto targetOtclChainDto;
	
	/** The source otcl chain dto. */
	public OtclChainDto sourceOtclChainDto;
	
	/** The has set values. */
	public boolean hasSetValues;
	
	/** The has execute module. */
	public boolean hasExecuteModule;
	
	/** The has execute converter. */
	public boolean hasExecuteConverter;
	
	/** The has execution order. */
	public boolean hasExecutionOrder;

	/**
	 * Instantiates a new script dto.
	 */
	private ScriptDto() { }

	/**
	 * Instantiates a new script dto.
	 *
	 * @param script the script
	 */
	public ScriptDto(OtclCommands script) {
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
	
	/**
	 * Clone.
	 *
	 * @return the script dto
	 */
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

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return "ScriptDto [command=" + command + ", targetOtclChainDto=" + targetOtclChainDto
				+ ", sourceOtclChainDto=" + sourceOtclChainDto + ", hasSetValueExtension=" + hasSetValues
				+ ", hasExecuteModule=" + hasExecuteModule + ", hasExecuteConverter=" + hasExecuteConverter
				+ ", hasExecutionOrder=" + hasExecutionOrder + "]";
	}

	/**
	 * Hash code.
	 *
	 * @return the int
	 */
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

	/**
	 * Equals.
	 *
	 * @param obj the obj
	 * @return true, if successful
	 */
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
