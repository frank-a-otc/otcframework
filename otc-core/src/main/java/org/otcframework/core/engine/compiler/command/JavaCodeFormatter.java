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

import java.util.Scanner;

import org.otcframework.common.util.CommonUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class JavaCodeFormatter.
 */
final class JavaCodeFormatter {

	/**
	 * Format.
	 *
	 * @param javaCode the java code
	 * @return the string
	 */
	static String format(String javaCode) {
		Scanner scanner = new Scanner(javaCode);
		StringBuilder javaCodeBuilder = new StringBuilder();
		int bracesCounter = 0;
		boolean isPostClassDeclaration = false;
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine().trim();
			boolean decrementPhase = false;
			if (line.contains("}")) {
				bracesCounter--;
				decrementPhase = true;
			}
			if (bracesCounter > 0) {
				line = doIndent(line, bracesCounter);
				String unWhitespacedLine = line.replace("\n", "").replace("\t", "");
				if (bracesCounter == 1 && !decrementPhase && !CommonUtils.isEmpty(line) && !line.endsWith(";")
						&& !unWhitespacedLine.startsWith("public ") && !unWhitespacedLine.startsWith("@Override")) {
					line = line.replace("\n\t", "\n\t\t\t");
				}
			}
			if (bracesCounter == 0) {
				if (line.startsWith("public ")) {
					line = "\n" + line;
					isPostClassDeclaration = true;
				} else if (isPostClassDeclaration && !decrementPhase){
					line = "\t" + line;
				}
				line = "\n" + line;
			}
			javaCodeBuilder.append(line);
			if (line.contains("{")) {
				bracesCounter++;
			}
		}
		scanner.close();
		return javaCodeBuilder.toString();
	}

	/**
	 * Do indent.
	 *
	 * @param line the line
	 * @param bracesCounter the braces counter
	 * @return the string
	 */
	static String doIndent(String line, int bracesCounter) {
		if (bracesCounter == 0) {
			return line;
		}
		StringBuilder tabs = new StringBuilder();
		line = line.replace("\t", "");
		for (int idx = 0; idx < bracesCounter; idx++) {
			tabs.append("\t");
		}
		line = "\n" + tabs + line;
		return line;
	}
}
