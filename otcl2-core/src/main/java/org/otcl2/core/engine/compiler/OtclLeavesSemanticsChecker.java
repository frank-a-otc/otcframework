/**
* Copyright (c) otclfoundation.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*/
package org.otcl2.core.engine.compiler;

import org.otcl2.common.dto.OtclCommandDto;
import org.otcl2.common.engine.compiler.OtclCommandContext;
import org.otcl2.core.engine.compiler.command.OtclCommand;
import org.otcl2.core.engine.compiler.exception.LexicalizerException;
import org.otclfoundation.dateconverters.DateConverterFacade;

// TODO: Auto-generated Javadoc
/**
 * The Class OtclLeavesSemanticsChecker.
 */
final class OtclLeavesSemanticsChecker {

	/**
	 * Check leaves semantics.
	 *
	 * @param targetOCC the target OCC
	 * @param sourceOCC the source OCC
	 * @return true, if successful
	 */
	static boolean checkLeavesSemantics(OtclCommandContext targetOCC, OtclCommandContext sourceOCC) {
		OtclCommandDto targetOCD = OtclCommand.retrieveLeafOCD(targetOCC);
		if (DateConverterFacade.isOfAnyDateType(targetOCD.fieldType)) {
			OtclCommandDto sourceOCD = OtclCommand.retrieveLeafOCD(sourceOCC);
			if (!DateConverterFacade.isOfAnyDateType(sourceOCD.fieldType) &&
					!(String.class != sourceOCD.fieldType)) {
				throw new LexicalizerException("","Target leaf-node is one of date-type. "
						+ "But Source leaf-node is not a compatible type.");
			}
		}
		return true;
	}
}
