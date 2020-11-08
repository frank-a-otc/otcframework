package org.otcl2.core.engine.compiler.command;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.tools.SimpleJavaFileObject;

import org.otcl2.core.engine.compiler.exception.OtclCompilerException;

public final class JavaCodeStringObject extends SimpleJavaFileObject {
	private final String source;

	public JavaCodeStringObject(String name, String source) {
		super(URI.create("string:///" + name + Kind.SOURCE.extension), Kind.SOURCE);
		this.source = source;
	}

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

	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
		return source;
	}
}
