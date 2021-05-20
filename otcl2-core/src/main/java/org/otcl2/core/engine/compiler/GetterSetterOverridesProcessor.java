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
package org.otcl2.core.engine.compiler;

import java.util.List;

import org.otcl2.common.OtclConstants;
import org.otcl2.common.OtclConstants.TARGET_SOURCE;
import org.otcl2.common.dto.OtclCommandDto;
import org.otcl2.common.dto.ScriptDto;
import org.otcl2.common.dto.otcl.OtclFileDto.Copy;
import org.otcl2.common.dto.otcl.OtclFileDto.Execute;
import org.otcl2.common.dto.otcl.OverrideDto;
import org.otcl2.common.dto.otcl.TargetDto;
import org.otcl2.core.engine.compiler.exception.SemanticsException;
import org.otcl2.core.engine.compiler.exception.SyntaxException;

// TODO: Auto-generated Javadoc
/**
 * The Class GetterSetterProcessor.
 */
final class GetterSetterOverridesProcessor {

	/**
	 * Process.
	 *
	 * @param script the script
	 * @param otclCommandDto the otcl command dto
	 */
	public static void process(ScriptDto script, OtclCommandDto otclCommandDto) {
		if (OtclConstants.ROOT.equals(otclCommandDto.fieldName)) {
			return;
		}
		String sourceOtclChain = null;
		String targetOtclChain = null;
		String commandId = script.command.id;
		List<OverrideDto> fromOverrides = null;
		List<TargetDto.Override> toOverrides = null;
		if (script.command instanceof Copy) {
			Copy copy = (Copy) script.command;
			sourceOtclChain = copy.from.otclChain;
			targetOtclChain = copy.to.otclChain;
			fromOverrides = copy.from.overrides;
			toOverrides = copy.to.overrides;
		} else {
			Execute execute = (Execute) script.command;
			sourceOtclChain = execute.source.otclChain;
			targetOtclChain = execute.target.otclChain;
			fromOverrides = execute.source.overrides;
			toOverrides = execute.target.overrides;
		}
		if (toOverrides == null && fromOverrides == null) {
			return;
		}
		if (otclCommandDto.getter == null && fromOverrides != null) {
			initFromGetter(otclCommandDto, sourceOtclChain, commandId, fromOverrides);
		}
		if (TARGET_SOURCE.TARGET == otclCommandDto.enumTargetSource && otclCommandDto.setter == null
				&& toOverrides != null) {
			initToSetterGetter(otclCommandDto, targetOtclChain, commandId, toOverrides);
		}
		return;
	}
	
	private static void initToSetterGetter(OtclCommandDto otclCommandDto, String targetOtclChain, 
			String commandId, List<TargetDto.Override> overrides) {
		if (overrides == null || TARGET_SOURCE.TARGET != otclCommandDto.enumTargetSource ||
				(otclCommandDto.getter != null && otclCommandDto.setter != null)) {
			return;
		}
		if (TARGET_SOURCE.TARGET != otclCommandDto.enumTargetSource) {
			return;
		}
		for (TargetDto.Override override : overrides) {
			String tokenPath = override.tokenPath;
			if (tokenPath == null) {
				throw new SyntaxException("", "Oops... Syntax error in Command-block : " + commandId + 
						".  'overrides.tokenPath: ' is missing.");
			}
			if (!targetOtclChain.startsWith(tokenPath)) {
				throw new SemanticsException("", "Irrelevant tokenPath '" + tokenPath + 
						"' in targets overrides section in command : " + commandId);
			}
			if (!otclCommandDto.tokenPath.equals(tokenPath)) {
				continue;
			}
			if (otclCommandDto.getter == null) {
				if (override.getterHelper != null) {
					otclCommandDto.getter = override.getterHelper;
					otclCommandDto.enableGetterHelper = true;
					otclCommandDto.isGetterInitialized = false;
				} else if (override.getter != null) {
					otclCommandDto.getter = override.getter;
					otclCommandDto.isGetterInitialized = false;
				}
			}
			if (otclCommandDto.setter != null) {
				continue;
			}
			if (override.setterHelper != null) {
				otclCommandDto.setter = override.setterHelper;
				otclCommandDto.enableSetterHelper = true;
				otclCommandDto.isSetterInitialized = false;
			} else if (override.setter != null) {
				otclCommandDto.setter = override.setter;
				otclCommandDto.isSetterInitialized = false;
			}
		}
	}
	
	private static void initFromGetter(OtclCommandDto otclCommandDto, String otclChain, 
			String commandId, List<OverrideDto> overrides) {
		if (overrides == null || otclCommandDto.getter != null) {
			return;
		}
		for (OverrideDto override : overrides) {
			String tokenPath = override.tokenPath;
			if (tokenPath == null) {
				throw new SyntaxException("", "Oops... Syntax error in Command-block : " + commandId + 
						".  'overrides.tokenPath: ' is missing.");
			}
			if (!otclChain.startsWith(tokenPath)) {
				throw new SemanticsException("", "Irrelevant tokenPath '" + tokenPath + 
						"' in source's overrides section in command : " + commandId);
			}
			if (!otclCommandDto.tokenPath.equals(tokenPath)) {
				continue;
			}
			if (override.getterHelper != null) {
				otclCommandDto.enableGetterHelper = true;
				otclCommandDto.isGetterInitialized = false;
				otclCommandDto.getter = override.getterHelper;
			} else if (override.getter != null) {
				otclCommandDto.getter = override.getter;
				otclCommandDto.isGetterInitialized = false;
			}
		}
	}
}
