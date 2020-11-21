package org.otcl2.core.engine.compiler.command;

// TODO: Auto-generated Javadoc
/**
 * The Class ExecutionContext.
 */
public class ExecutionContext {

	/** The target OCC. */
	public TargetOtclCommandContext targetOCC;
	
	/** The source OCC. */
	public SourceOtclCommandContext sourceOCC;
//	public Entry<String, ScriptGroupDto> entry;
/** The otcl command. */
//	public ScriptDto scriptDto;
	public OtclCommand otclCommand;
	
	/** The target clz. */
	public Class<?> targetClz;
	
	/** The source clz. */
	public Class<?> sourceClz;
//	public List<JavaFileObject> javaFileObjects;
}
