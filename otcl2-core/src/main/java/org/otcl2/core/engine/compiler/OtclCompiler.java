/**
* Copyright (c) otcl2.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*/
package org.otcl2.core.engine.compiler;

import java.util.List;

import org.otcl2.common.engine.compiler.CompilationReport;

public interface OtclCompiler {

	List<CompilationReport> compileOtcl();

	void compileSourceCode();
}
