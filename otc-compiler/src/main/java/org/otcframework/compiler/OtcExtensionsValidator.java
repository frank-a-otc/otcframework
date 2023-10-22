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
package org.otcframework.compiler;

import org.otcframework.common.OtcConstants;
import org.otcframework.common.dto.OtcChainDto;
import org.otcframework.common.dto.ScriptDto;
import org.otcframework.common.dto.otc.OtcFileDto.Copy;
import org.otcframework.common.dto.otc.OtcFileDto.Execute;
import org.otcframework.compiler.exception.OtcExtensionsException;

/**
 * The Class OtcExtensionsValidator.
 */
final class OtcExtensionsValidator {

	private OtcExtensionsValidator() {}

	private static final String LEXICALIZATION_FAILURE = "Otc Lexicalization-phase failure in OTC-command : ";
	/**
	 * Validate extensions.
	 *
	 * @param script                   the script
	 * @param builderTargetOtcChainDto the builder target otc chain dto
	 * @return true, if successful
	 */
	static boolean validateExtensions(ScriptDto script, OtcChainDto.Builder builderTargetOtcChainDto) {
		String targetOtcChain = null;
		if (script.command instanceof Copy) {
			targetOtcChain = ((Copy) script.command).to.objectPath;
		} else {
			targetOtcChain = ((Execute) script.command).target.objectPath;
		}
		if (script.command instanceof Copy) {
			Copy copy = (Copy) script.command;
			if (copy.from != null && copy.from.values != null && copy.from.objectPath != null) {
				throw new OtcExtensionsException("",
						LEXICALIZATION_FAILURE + script.command.id
								+ ". Both 'source: from: otcChain' and 'source: from: values' cannot co-exist.");
			}
		} else {
			if (targetOtcChain.contains(OtcConstants.ANCHOR)) {
				throw new OtcExtensionsException("", LEXICALIZATION_FAILURE + script.command.id
						+ ". Invalid applciation of ElasticTree nature on 'execute' commands - remove the anchors.");
			}
			Execute execute = (Execute) script.command;
			if (execute.module != null) {
				String sourceOtcChain = execute.source.objectPath;
				if (targetOtcChain.contains(OtcConstants.OPEN_BRACKET)
						&& sourceOtcChain.contains(OtcConstants.OPEN_BRACKET)) {
					throw new OtcExtensionsException("",
							LEXICALIZATION_FAILURE + script.command.id
									+ ". Execute commmand cannot have Collection/Map notations on both target and source at "
									+ "the same time in 'executeOtcConverter' extension.");
				}
				if (execute.executionOrder != null) {
					execute.executionOrder.forEach(exeOrd -> {
						if (OtcConstants.EXECUTE_OTC_CONVERTER.equals(exeOrd) && execute.converter == null) {
							throw new OtcExtensionsException("",
									LEXICALIZATION_FAILURE + script.command.id
											+ ". 'executeOtcConverter' defined in 'extensions: executionOrder' "
											+ "but 'extensions: executeOtcConverter' is undefined.");
						}
						if (OtcConstants.EXECUTE_OTC_MODULE.equals(exeOrd) && execute.module == null) {
							throw new OtcExtensionsException("",
									LEXICALIZATION_FAILURE + script.command.id
											+ ". 'executeOtcModule' defined in 'extensions: executionOrder' "
											+ "but 'extensions: executeOtcModule' is undefined.");
						}
					});
					script.hasExecutionOrder = true;
				}
				if (execute.module != null) {
					script.hasExecuteModule = true;
				}
				if (execute.converter != null) {
					script.hasExecuteConverter = true;
				}
			}
		}
		builderTargetOtcChainDto.addOtcChain(targetOtcChain);
		return true;
	}
}
