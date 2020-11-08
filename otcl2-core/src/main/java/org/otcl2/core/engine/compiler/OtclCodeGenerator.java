package org.otcl2.core.engine.compiler;

import java.util.List;

import javax.tools.JavaFileObject;

import org.otcl2.common.dto.OtclDto;

public interface OtclCodeGenerator {

	List<JavaFileObject> generateSourcecode(OtclDto otclDto);

}
