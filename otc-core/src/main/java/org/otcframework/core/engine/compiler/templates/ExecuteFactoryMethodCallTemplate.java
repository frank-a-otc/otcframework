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
package org.otcframework.core.engine.compiler.templates;

import org.otcframework.common.util.CommonUtils;

/**
 * The Class ExecuteFactoryMethodCallTemplate.
 */
// TODO: Auto-generated Javadoc
public final class ExecuteFactoryMethodCallTemplate extends AbstractTemplate {

	/**
	 * Instantiates a new execute factory method call template.
	 */
	private ExecuteFactoryMethodCallTemplate() {
	}

	/**
	 * Generate code.
	 *
	 * @param factoryClzName the factory clz name
	 * @param targetClz      the target clz
	 * @param sourceClz      the source clz
	 * @return the string
	 */
	public static String generateCode(String factoryClzName, Class<?> targetClz, Class<?> sourceClz) {
		String targetVar = CommonUtils.initLower(targetClz.getSimpleName());
		String sourceVar = null;
		String sourceICD = null;
		if (sourceClz != null) {
			sourceVar = CommonUtils.initLower(sourceClz.getSimpleName());
			sourceICD = "sourceICD";
		}
		String executeMethodCallCode = String.format(executeFactoryMethodCallTemplate, factoryClzName, sourceVar,
				sourceICD, targetVar);
		return executeMethodCallCode;
	}
}
