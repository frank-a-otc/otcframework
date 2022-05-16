/**
* Copyright (c) otcframework.org
*
* @author  Franklin J Abel
* @version 1.0
* @since   2020-06-08 
*
* This file is part of the OTC framework.
* 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      https://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*/
package org.otcframework.core.engine.compiler.command;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.tools.SimpleJavaFileObject;

import org.otcframework.core.engine.compiler.exception.OtcCompilerException;

/**
 * The Class JavaCodeStringObject.
 */
// TODO: Auto-generated Javadoc
public final class JavaCodeStringObject extends SimpleJavaFileObject {

	/** The source. */
	private final String source;

	/**
	 * Instantiates a new java code string object.
	 *
	 * @param name   the name
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
			throw new OtcCompilerException(e);
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
