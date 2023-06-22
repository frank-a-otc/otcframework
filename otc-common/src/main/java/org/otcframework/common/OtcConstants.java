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
package org.otcframework.common;

/**
 * The Interface OtcConstants.
 */
// TODO: Auto-generated Javadoc
public interface OtcConstants {

	/**
	 * The Enum LogLevel.
	 */
	static enum LogLevel {

		/** The error. */
		ERROR("error"),
		/** The warn. */
		WARN("warn"),
		/** The info. */
		INFO("info"),
		/** The debug. */
		DEBUG("debug"),
		/** The trace. */
		TRACE("trace");

		/** The log level. */
		private final String logLevel;

		/**
		 * Instantiates a new log level.
		 *
		 * @param level the level
		 */
		LogLevel(String level) {
			logLevel = level;
		}

		/**
		 * To string.
		 *
		 * @return the string
		 */
		public String toString() {
			return logLevel;
		}
	};

	/**
	 * The Enum ALGORITHM_ID.
	 */
	enum ALGORITHM_ID {

		/** The copyvalues. */
		COPYVALUES,
		/** The module. */
		MODULE,
		/** The converter. */
		CONVERTER,
		/** The flat. */
		FLAT,
		/** The collections. */
		COLLECTIONS
	};

	/**
	 * The Enum TARGET_SOURCE.
	 */
	enum TARGET_SOURCE {

		/** The target. */
		TARGET,
		/** The source. */
		SOURCE
	};

	/** The execute otc converter. */
	String EXECUTE_OTC_CONVERTER = "converter";

	/** The execute otc module. */
	String EXECUTE_OTC_MODULE = "module";

	/** The regex otc on dot. */
	String REGEX_OTC_ON_DOT = new String("[.](?![^<]*>)(?![^\\[]*\\])(?![^\\(]*\\))");

	/** The regex check otcchain. */
	String REGEX_CHECK_OTCCHAIN = "(?s)from:\\sobjectPath:";

	/** The root. */
	String ROOT = "$";

	/** The arr ref. */
	String ARR_REF = "[*]";

	/** The asterisk. */
	String ASTERISK = "*";

	/** The anchor. */
	String ANCHOR = "^";

	/** The pre anchor. */
	String PRE_ANCHOR = "[^*";

	/** The post anchor. */
	String POST_ANCHOR = "*^]";

	/** The open bracket. */
	String OPEN_BRACKET = "[";

	/** The close bracket. */
	String CLOSE_BRACKET = "]";

	/** The map ref. */
	String MAP_REF = "[*,*]";

	/** The map begin ref. */
	String MAP_BEGIN_REF = "[*,*";

	/** The map end ref. */
	String MAP_END_REF = "*,*]";

	/** The map pre anchor. */
	String MAP_PRE_ANCHOR = "[^*,*";

	/** The map post anchor. */
	String MAP_POST_ANCHOR = "*,*^]";

	/** The map key ref. */
	String MAP_KEY_REF = "<K>";

	/** The map value ref. */
	String MAP_VALUE_REF = "<V>";

	/** The otc script extn. */
	String OTC_SCRIPT_EXTN = ".otcs";

	/** The otc generatedcode extn. */
	String OTC_GENERATEDCODE_EXTN = ".java";

	String CLASS_EXTN = ".class";

	/** The otc tmd extn. */
	String OTC_TMD_EXTN = ".tmd";
}
