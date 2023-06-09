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

import etree.dateconverters.DateConverterFacade;
import org.otcframework.common.compiler.OtcCommandContext;
import org.otcframework.common.dto.OtcCommandDto;
import org.otcframework.compiler.command.OtcCommand;
import org.otcframework.compiler.exception.LexicalizerException;

/**
 * The Class OtcLeavesSemanticsChecker.
 */
// TODO: Auto-generated Javadoc
final class OtcLeavesSemanticsProcessor {

	/**
	 * Check leaves semantics.
	 *
	 * @param targetOCC the target OCC
	 * @param sourceOCC the source OCC
	 * @return true, if successful
	 */
	static boolean process(OtcCommandContext targetOCC, OtcCommandContext sourceOCC) {
		OtcCommandDto targetOCD = OtcCommand.retrieveLeafOCD(targetOCC);
		if (DateConverterFacade.isOfAnyDateType(targetOCD.fieldType)) {
			OtcCommandDto sourceOCD = OtcCommand.retrieveLeafOCD(sourceOCC);
			if (!DateConverterFacade.isOfAnyDateType(sourceOCD.fieldType)
					&& String.class != sourceOCD.fieldType) {
				throw new LexicalizerException("", "Target leaf-node is one of the date-types. " + 
						"But Source leaf-node is not a compatible type.");
			}
		}
		return true;
	}
}
