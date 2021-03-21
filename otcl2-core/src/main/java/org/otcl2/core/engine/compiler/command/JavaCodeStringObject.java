package org.otcl2.core.engine.compiler.command;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.tools.SimpleJavaFileObject;

import org.otcl2.core.engine.compiler.exception.OtclCompilerException;

// TODO: Auto-generated Javadoc
/**
 * The Class JavaCodeStringObject.
 */
public final class JavaCodeStringObject extends SimpleJavaFileObject {
	
	/** The source. */
	private final String source;

	/**
	 * Instantiates a new java code string object.
	 *
	 * @param name the name
	 * @param source the source
	 */
	public JavaCodeStringObject(String name, String source) {
		super(URI.create("string:///" + name + Kind.SOURCE.extension), Kind.SOURCE);
		this.source = source;
	}

	/**
	 * Instantiates a new java code string object.
	 *
	 * @param file the file
	 */
	public JavaCodeStringObject(File file) {
		super(URI.create("file:///" + file.getAbsolutePath().replace("\\", "/")), Kind.SOURCE);
		String source;
		try {
			source = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
		} catch (IOException e) {
			throw new OtclCompilerException(e);
		}
		this.source = source;
	}

	/**
	 * Gets the char content.
	 *
	 * @param ignoreEncodingErrors the ignore encoding errors
	 * @return the char content
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
		return source;
	}
}
