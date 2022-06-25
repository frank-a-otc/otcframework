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
package org.otcframework.common.dto.otc;

import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class OtcFileDto.
 */
public final class OtcFileDto {

	/** The file name. */
	public String fileName;

	/** The metadata. */
	public Metadata metadata;

	/** The otc commands. */
	public List<OtclCommand> otclCommands;

	/**
	 * The Class Metadata.
	 */
	public static final class Metadata {

		/** The entry class name. */
		public String entryClassName;

		/** The helper. */
		public String helper;

		/** The object types. */
		public ObjectTypes objectTypes;

		/**
		 * The Class ObjectTypes.
		 */
		public static final class ObjectTypes {

			/** The source. */
			public String source;

			/** The target. */
			public String target;
		}
	}

	/**
	 * The Class OtcCommands.
	 */
	public static final class OtclCommand {

		/** The copy. */
		public Copy copy;

		/** The execute. */
		public Execute execute;
	}

	/**
	 * The Class CommonCommandParams.
	 */
	public static class CommonCommandParams {

		/** The id. */
		public String id;

		/** The factory class name. */
		public String factoryClassName;

		/** The disable. */
		public boolean disable;

		/** The debug. */
		public boolean debug;
	}

	/**
	 * The Class Copy.
	 */
	public static final class Copy extends CommonCommandParams {

		/** The from. */
		public Source from;

		/** The to. */
		public TargetDto to;

		/**
		 * To string.
		 *
		 * @return the string
		 */
		public String toString() {
			return "Copy [from=" + from + ", to=" + to + ", id=" + id + ", factoryClassName=" + factoryClassName
					+ ", disable=" + disable + ", debug=" + debug + "]";
		}

		/**
		 * The Class Source.
		 */
		public static final class Source extends SourceDto {

			/** The values. */
			public List<String> values;

			/**
			 * To string.
			 *
			 * @return the string
			 */
			public String toString() {
				return "Source [values=" + values + ", otcChain=" + objectPath + ", overrides=" + overrides + "]";
			}
		}
	}

	/**
	 * The Class Execute.
	 */
	public static final class Execute extends CommonCommandParams {

		/** The otc converter. */
		public String converter;

		/** The otc module. */
		public OtclModule module;

		/** The execution order. */
		public List<String> executionOrder;

		/** The source. */
		public SourceDto source;

		/** The target. */
		public TargetDto target;

		/**
		 * The Class OtclModule.
		 */
		public static final class OtclModule {

			/** The otc namespace. */
			public String namespace;
		}
	}
}
