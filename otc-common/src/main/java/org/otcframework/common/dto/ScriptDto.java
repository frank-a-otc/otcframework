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

import org.otcframework.common.dto.otc.OtcFileDto.CommonCommandParams;
import org.otcframework.common.dto.otc.OtcFileDto.Execute;
import org.otcframework.common.dto.otc.OtcFileDto.Execute.OtclModule;
import org.otcframework.common.dto.otc.OtcFileDto.OtclCommand;

/**
 * The Class ScriptDto.
 */
// TODO: Auto-generated Javadoc
public class ScriptDto {

	/** The command. */
	public CommonCommandParams command;

	/** The target otc chain dto. */
	public OtcChainDto targetOtcChainDto;

	/** The source otc chain dto. */
	public OtcChainDto sourceOtcChainDto;

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
	private ScriptDto() {
	}

	/**
	 * Instantiates a new script dto.
	 *
	 * @param script the script
	 */
	public ScriptDto(OtclCommand script) {
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
		scriptDto.targetOtcChainDto = targetOtcChainDto;
		scriptDto.sourceOtcChainDto = sourceOtcChainDto;
		return scriptDto;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return "ScriptDto [command=" + command + ", targetOtcChainDto=" + targetOtcChainDto + ", sourceOtcChainDto="
				+ sourceOtcChainDto + ", hasSetValueExtension=" + hasSetValues + ", hasExecuteModule="
				+ hasExecuteModule + ", hasExecuteConverter=" + hasExecuteConverter + ", hasExecutionOrder="
				+ hasExecutionOrder + "]";
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
		result = prime * result + ((sourceOtcChainDto == null) ? 0 : sourceOtcChainDto.hashCode());
		result = prime * result + ((targetOtcChainDto == null) ? 0 : targetOtcChainDto.hashCode());
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
		if (sourceOtcChainDto == null) {
			if (other.sourceOtcChainDto != null)
				return false;
		} else if (!sourceOtcChainDto.equals(other.sourceOtcChainDto))
			return false;
		if (targetOtcChainDto == null) {
			if (other.targetOtcChainDto != null)
				return false;
		} else if (!targetOtcChainDto.equals(other.targetOtcChainDto))
			return false;
		return true;
	}
}
