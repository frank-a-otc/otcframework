package org.otcl2.core.engine.compiler.command;

import java.util.Scanner;

import org.otcl2.common.util.CommonUtils;

final class JavaCodeFormatter {

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
