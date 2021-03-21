/**
* Copyright (c) otclfoundation.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*/
package org.otcl2.core.engine.compiler;

import org.otcl2.common.OtclConstants;
import org.otcl2.common.dto.OtclChainDto;
import org.otcl2.common.dto.ScriptDto;
import org.otcl2.common.dto.otcl.OtclFileDto.Copy;
import org.otcl2.common.dto.otcl.OtclFileDto.Execute;
import org.otcl2.core.engine.compiler.exception.OtclExtensionsException;

// TODO: Auto-generated Javadoc
/**
 * The Class OtclExtensionsValidator.
 */
final class OtclExtensionsValidator {

	/**
	 * Validate extensions.
	 *
	 * @param script the script
	 * @param targetClz the target clz
	 * @param builderTargetOtclChainDto the builder target otcl chain dto
	 * @param sourceClz the source clz
	 * @param builderSourceOtclChainDto the builder source otcl chain dto
	 * @return true, if successful
	 */
	static boolean validateExtensions(ScriptDto script, Class<?> targetClz, OtclChainDto.Builder builderTargetOtclChainDto,
			Class<?> sourceClz, OtclChainDto.Builder builderSourceOtclChainDto) {
		String targetOtclChain = null;
		if (script.command instanceof Copy) {
			targetOtclChain = ((Copy) script.command).to.otclChain;
		} else {
			targetOtclChain = ((Execute) script.command).target.otclChain;
		}
		if (script.command instanceof Execute && targetOtclChain.contains(OtclConstants.ANCHOR)) {
			throw new OtclExtensionsException("", "Otcl Lexicalizer-phase failure in Script-block : " + script.command.id +
					". Invalid applciation of ElasticTree nature on 'execute' commands - remove the anchors.");
		}
		if (script.command instanceof Copy) {
			Copy copy = (Copy) script.command;
			if (copy.from != null && copy.from.values != null && copy.from.otclChain != null) {
				throw new OtclExtensionsException("", "Otcl Lexicalizer-phase failure in Script-block : " +
						script.command.id + ". Both 'source: from: otclChain' and 'source: from: values' cannot co-exist.");
			}
		} else if (script.command instanceof Execute) {
			Execute execute = (Execute) script.command;
			if (execute != null && execute.otclModule != null) {
				String sourceOtclChain = execute.source.otclChain;
				if (targetOtclChain.contains(OtclConstants.OPEN_BRACKET) && 
						sourceOtclChain.contains(OtclConstants.OPEN_BRACKET)) {
					throw new OtclExtensionsException("", "Otcl Lexicalizer-phase failure in Script-block : " + 
						script.command.id + ". Extension cannot have Collection/Map notations on both target and source at "
								+ "the same time 'executeOtclConverter' extension.");
				}
				if (execute.executionOrder != null) {
					for (String exeOrd : execute.executionOrder) {
						if (OtclConstants.EXECUTE_OTCL_CONVERTER.equals(exeOrd) && 
								execute.otclConverter == null) {
							throw new OtclExtensionsException("", "Otcl Lexicalizer-phase failure in Script-block : " +
								script.command.id + ". 'executeOtclConverter' defined in 'extensions: executionOrder' "
								+ "but 'extensions: executeOtclConverter' is undefined.");
						}
						if (OtclConstants.EXECUTE_OTCL_MODULE.equals(exeOrd) && 
								execute.otclModule == null) {
							throw new OtclExtensionsException("", "Otcl Lexicalizer-phase failure in Script-block : " +
								script.command.id + ". 'executeOtclModule' defined in 'extensions: executionOrder' "
								+ "but 'extensions: executeOtclModule' is undefined.");
						}
					}
					script.hasExecutionOrder = true;
				}
				if (execute.otclModule != null) {
					script.hasExecuteModule = true;
				}
				if (execute.otclConverter != null) {
					script.hasExecuteConverter = true;
				}
			}
		}
		builderTargetOtclChainDto.addOtclChain(targetOtclChain);
		return true;
	}
}
