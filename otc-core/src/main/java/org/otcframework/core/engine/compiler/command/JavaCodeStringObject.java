/**
* Copyright (c) otcframework.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*
* This file is part of the OTC framework.
* 
*  The OTC framework is free software: you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  the Free Software Foundation, version 3 of the License.
*
*  The OTC framework is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU General Public License for more details.
*
*  A copy of the GNU General Public License is made available as 'License.md' file, 
*  along with OTC framework project.  If not, see <https://www.gnu.org/licenses/>.
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
