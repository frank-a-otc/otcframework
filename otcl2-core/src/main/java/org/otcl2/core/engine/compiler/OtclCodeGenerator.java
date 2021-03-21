package org.otcl2.core.engine.compiler;

import java.util.List;

import javax.tools.JavaFileObject;

import org.otcl2.common.dto.OtclDto;


// TODO: Auto-generated Javadoc
/**
 * The Interface OtclCodeGenerator.
 */
public interface OtclCodeGenerator {

	/**
	 * Generate sourcecode.
	 *
	 * @param otclDto the otcl dto
	 * @return the list
	 */
	List<JavaFileObject> generateSourcecode(OtclDto otclDto);

}
