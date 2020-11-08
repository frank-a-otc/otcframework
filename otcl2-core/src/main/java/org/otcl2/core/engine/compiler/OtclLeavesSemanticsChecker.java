/**
* Copyright (c) otcl2.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*/
package org.otcl2.core.engine.compiler;

import org.otcl.dateconverters.MutualDateTypesConverterFacade;
import org.otcl2.common.dto.OtclCommandDto;
import org.otcl2.common.engine.compiler.OtclCommandContext;
import org.otcl2.core.engine.compiler.command.OtclCommand;
import org.otcl2.core.engine.compiler.exception.LexicalizerException;

final class OtclLeavesSemanticsChecker {

	static boolean checkLeavesSemantics(OtclCommandContext targetOCC, OtclCommandContext sourceOCC) {
		OtclCommandDto targetOCD = OtclCommand.retrieveLeafOCD(targetOCC);
		if (MutualDateTypesConverterFacade.isOfAnyDateType(targetOCD.fieldType)) {
			OtclCommandDto sourceOCD = OtclCommand.retrieveLeafOCD(sourceOCC);
			if (!MutualDateTypesConverterFacade.isOfAnyDateType(sourceOCD.fieldType) &&
					!(String.class != sourceOCD.fieldType)) {
				throw new LexicalizerException("","Target leaf-node is one of date-type. "
						+ "But Source leaf-node is not a compatible type.");
			}
		}
		return true;
	}
}
