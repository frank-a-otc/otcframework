/**
* Copyright (c) otclfoundation.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*/
package org.otcl2.common.dto;

import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class OtclFileDto.
 */
public final class OtclFileDto {
	
	/** The file name. */
	public String fileName;
	
	/** The metadata. */
	public Metadata metadata;
	
	/** The otcl scripts. */
	public List<OtclScript> otclScripts;

	/**
	 * The Class Metadata.
	 */
	public static final class Metadata {
		
		/** The main class name. */
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
	 * The Class OtclScript.
	 */
	public static final class OtclScript {
		
		/** The copy. */
		public Copy copy;
		
		/** The execute. */
		public Execute execute;
	}

	/**
	 * The Class Command.
	 */
	public static class Command {
		
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
	public static final class Copy extends Command {
		
		/** The from. */
		public Source from;
		
		/** The to. */
		public Target to;
		
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
		public static final class Source {
			
			/** The values. */
			public List<String> values;
			
			/** The otcl chain. */
			public String otclChain;
			
			/** The overrides. */
			public List<Override> overrides;
			
			/**
			 * To string.
			 *
			 * @return the string
			 */
			public String toString() {
				return "Source [values=" + values + ", otclChain=" + otclChain + ", overrides=" + overrides + "]";
			}
		}
	}
	
	/**
	 * The Class Override.
	 */
	public static final class Override {
		
		/** The token path. */
		public String tokenPath;
		
		/** The getter. */
		public String getter;
		
		/** The getter helper. */
		public String getterHelper;
	}

	/**
	 * The Class Execute.
	 */
	public static final class Execute extends Command {
		
		/** The otcl converter. */
		public String otclConverter;
		
		/** The otcl module. */
		public OtclModule otclModule;
		
		/** The execution order. */
		public List<String> executionOrder;
		
		/** The source. */
		public Source source;
		
		/** The target. */
		public Target target;

		/**
		 * The Class Source.
		 */
		public static final class Source {
			
			/** The otcl chain. */
			public String otclChain;
			
			/** The overrides. */
			public List<Override> overrides;
		}
		
		/**
		 * The Class OtclModule.
		 */
		public static final class OtclModule {
			
			/** The otcl namespace. */
			public String otclNamespace;
		}
	}
	
	/**
	 * The Class Target.
	 */
	public static final class Target {
		
		/** The otcl chain. */
		public String otclChain;
		
		/** The overrides. */
		public List<Override> overrides;

		/**
		 * The Class Override.
		 */
		public static final class Override {
			
			/** The token path. */
			public String tokenPath;
			
			/** The concrete type. */
			public String concreteType;
			
			/** The getter. */
			public String getter;
			
			/** The setter. */
			public String setter;
			
			/** The getter helper. */
			public String getterHelper;
			
			/** The setter helper. */
			public String setterHelper;
		}
	}
}
