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
package org.otcframework.compiler.command;

import java.util.Scanner;

import org.otcframework.common.util.CommonUtils;

/**
 * The Class JavaCodeFormatter.
 */
// TODO: Auto-generated Javadoc
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
				} else if (isPostClassDeclaration && !decrementPhase) {
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
	 * @param line          the line
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
