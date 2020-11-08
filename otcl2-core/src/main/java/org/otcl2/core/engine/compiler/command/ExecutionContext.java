package org.otcl2.core.engine.compiler.command;

public class ExecutionContext {

	public TargetOtclCommandContext targetOCC;
	public SourceOtclCommandContext sourceOCC;
//	public Entry<String, ScriptGroupDto> entry;
//	public ScriptDto scriptDto;
	public OtclCommand otclCommand;
	public Class<?> targetClz;
	public Class<?> sourceClz;
//	public List<JavaFileObject> javaFileObjects;
}
