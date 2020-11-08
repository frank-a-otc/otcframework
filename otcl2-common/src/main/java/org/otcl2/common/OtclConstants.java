/**
* Copyright (c) otcl2.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*/
package org.otcl2.common;

public interface OtclConstants {

	static enum LogLevel {
		ERROR("error"),
		WARN("warn"),
		INFO("info"),
		DEBUG("debug"),
		TRACE("trace");
		
		private final String logLevel;

		LogLevel(String level) {
			logLevel = level;
		}
		
		public String toString() {
			return logLevel;
		}
	};

	enum ALGORITHM_ID {COPYVALUES, MODULE, CONVERTER, FLAT, COLLECTIONS};

	enum TARGET_SOURCE {TARGET, SOURCE};

	String EXECUTE_OTCL_CONVERTER = "otclConverter";
	String EXECUTE_OTCL_MODULE = "otclModule";
	

	String REGEX_OTCL_ON_DOT = new String("[.](?![^<]*>)(?![^\\[]*\\])(?![^\\(]*\\))");
//	String REGEX_OTCL_ON_COMMA_NOT_WITHIN_BRACKETS = new String("[,](?![^\\[]*\\])(?![^\\(]*\\))");
//	String REGEX_OTCL_ON_COMMA_NOT_WITHIN_QUOTES = new String(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
	String REGEX_CHECK_OTCLCHAIN = "(?s)from:\\sotclChain:";

	String ROOT = "<ROOT>";
//	String ZERO = "<ZERO>";
	String ARR_REF = "[*]";
	String APPEND_ARR_REF = "[append";

	String ASTERISK = "*";
	String ANCHOR = "^";
	String PRE_ANCHOR = "[^*";
	String POST_ANCHOR = "*^]";
	String OPEN_BRACKET = "[";
	String CLOSE_BRACKET = "]";

	String MAP_BEGIN_REF = "[<";
	String MAP_END_REF = ">]";
	String MAP_PRE_ANCHOR = "[^<";
	String MAP_POST_ANCHOR = ">^]";
	String MAP_REF = "[<K,V>]";
	String MAP_KEY_REF = "<K>";
	String MAP_VALUE_REF = "<V>";

	String OTCL_FILE_EXTN = ".otcl";
	String OTCL_GENERATEDCODE_EXTN = ".java";
	String OTCL_DEP_EXTN = ".dep";

	String OTCL_SOURCE = "/otcl-source";
	String OTCL_TEST_SOURCE = "/otcl-test-source";
}
