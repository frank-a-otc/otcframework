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

import org.otcframework.common.dto.OtcCommandDto;
import org.otcframework.common.engine.compiler.OtcCommandContext;
import org.otcframework.core.engine.compiler.command.OtcCommand;
import org.otcframework.core.engine.compiler.exception.LexicalizerException;
import org.otcframework.dateconverters.DateConverterFacade;

// TODO: Auto-generated Javadoc
/**
 * The Class OtcLeavesSemanticsChecker.
 */
final class OtcLeavesSemanticsChecker {

	/**
	 * Check leaves semantics.
	 *
	 * @param targetOCC the target OCC
	 * @param sourceOCC the source OCC
	 * @return true, if successful
	 */
	static boolean checkLeavesSemantics(OtcCommandContext targetOCC, OtcCommandContext sourceOCC) {
		OtcCommandDto targetOCD = OtcCommand.retrieveLeafOCD(targetOCC);
		if (DateConverterFacade.isOfAnyDateType(targetOCD.fieldType)) {
			OtcCommandDto sourceOCD = OtcCommand.retrieveLeafOCD(sourceOCC);
			if (!DateConverterFacade.isOfAnyDateType(sourceOCD.fieldType) &&
					String.class != sourceOCD.fieldType) {
				throw new LexicalizerException("","Target leaf-node is one of date-type. "
						+ "But Source leaf-node is not a compatible type.");
			}
		}
		return true;
	}
}
