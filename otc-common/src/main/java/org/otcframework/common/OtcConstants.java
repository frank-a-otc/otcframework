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
	String EXECUTE_OTC_CONVERTER = "otcConverter";

	/** The execute otc module. */
	String EXECUTE_OTC_MODULE = "otcModule";

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
	String OTC_SCRIPT_EXTN = ".otcl";

	/** The otc generatedcode extn. */
	String OTC_GENERATEDCODE_EXTN = ".java";

	/** The otc tmd extn. */
	String OTC_TMD_EXTN = ".tmd";
}
