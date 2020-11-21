/**
* Copyright (c) otclfoundation.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
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
