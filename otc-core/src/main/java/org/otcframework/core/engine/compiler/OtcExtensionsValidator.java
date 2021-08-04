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
package org.otcframework.core.engine.compiler;

import org.otcframework.common.OtcConstants;
import org.otcframework.common.dto.OtcChainDto;
import org.otcframework.common.dto.ScriptDto;
import org.otcframework.common.dto.otc.OtcFileDto.Copy;
import org.otcframework.common.dto.otc.OtcFileDto.Execute;
import org.otcframework.core.engine.compiler.exception.OtcExtensionsException;

/**
 * The Class OtcExtensionsValidator.
 */
// TODO: Auto-generated Javadoc
final class OtcExtensionsValidator {

	/**
	 * Validate extensions.
	 *
	 * @param script                   the script
	 * @param targetClz                the target clz
	 * @param builderTargetOtcChainDto the builder target otc chain dto
	 * @param sourceClz                the source clz
	 * @param builderSourceOtcChainDto the builder source otc chain dto
	 * @return true, if successful
	 */
	static boolean validateExtensions(ScriptDto script, Class<?> targetClz,
			OtcChainDto.Builder builderTargetOtcChainDto, Class<?> sourceClz,
			OtcChainDto.Builder builderSourceOtcChainDto) {
		String targetOtcChain = null;
		if (script.command instanceof Copy) {
			targetOtcChain = ((Copy) script.command).to.objectPath;
		} else {
			targetOtcChain = ((Execute) script.command).target.objectPath;
		}
		if (script.command instanceof Execute && targetOtcChain.contains(OtcConstants.ANCHOR)) {
			throw new OtcExtensionsException("", "Otc Lexicalizer-phase failure in OTC-command : " + script.command.id
					+ ". Invalid applciation of ElasticTree nature on 'execute' commands - remove the anchors.");
		}
		if (script.command instanceof Copy) {
			Copy copy = (Copy) script.command;
			if (copy.from != null && copy.from.values != null && copy.from.objectPath != null) {
				throw new OtcExtensionsException("",
						"Otc Lexicalizer-phase failure in OTC-command : " + script.command.id
								+ ". Both 'source: from: otcChain' and 'source: from: values' cannot co-exist.");
			}
		} else if (script.command instanceof Execute) {
			Execute execute = (Execute) script.command;
			if (execute != null && execute.otcModule != null) {
				String sourceOtcChain = execute.source.objectPath;
				if (targetOtcChain.contains(OtcConstants.OPEN_BRACKET)
						&& sourceOtcChain.contains(OtcConstants.OPEN_BRACKET)) {
					throw new OtcExtensionsException("",
							"Otc Lexicalizer-phase failure in OTC-command : " + script.command.id
									+ ". Extension cannot have Collection/Map notations on both target and source at "
									+ "the same time 'executeOtcConverter' extension.");
				}
				if (execute.executionOrder != null) {
					for (String exeOrd : execute.executionOrder) {
						if (OtcConstants.EXECUTE_OTC_CONVERTER.equals(exeOrd) && execute.otcConverter == null) {
							throw new OtcExtensionsException("",
									"Otc Lexicalizer-phase failure in OTC-command : " + script.command.id
											+ ". 'executeOtcConverter' defined in 'extensions: executionOrder' "
											+ "but 'extensions: executeOtcConverter' is undefined.");
						}
						if (OtcConstants.EXECUTE_OTC_MODULE.equals(exeOrd) && execute.otcModule == null) {
							throw new OtcExtensionsException("",
									"Otc Lexicalizer-phase failure in OTC-command : " + script.command.id
											+ ". 'executeOtcModule' defined in 'extensions: executionOrder' "
											+ "but 'extensions: executeOtcModule' is undefined.");
						}
					}
					script.hasExecutionOrder = true;
				}
				if (execute.otcModule != null) {
					script.hasExecuteModule = true;
				}
				if (execute.otcConverter != null) {
					script.hasExecuteConverter = true;
				}
			}
		}
		builderTargetOtcChainDto.addOtcChain(targetOtcChain);
		return true;
	}
}
