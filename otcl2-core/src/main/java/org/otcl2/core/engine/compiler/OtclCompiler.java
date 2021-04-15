/**
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

import org.otcl2.common.engine.compiler.CompilationReport;


// TODO: Auto-generated Javadoc
/**
 * The Interface OtclCompiler.
 */
public interface OtclCompiler {

	/**
	 * Compile otcl.
	 *
	 * @return the list
	 */
	List<CompilationReport> compileOtcl();

	/**
	 * Compile source code.
	 */
	void compileSourceCode();
}
