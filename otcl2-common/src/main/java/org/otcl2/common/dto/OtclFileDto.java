/**
* Copyright (c) otcl2.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*/
package org.otcl2.common.dto;

import java.util.List;

public final class OtclFileDto {
	public String fileName;
	public Metadata metadata;
	public List<OtclScript> otclScripts;

	public static final class Metadata {
		public String mainClassName;
		public String helper;
		public ObjectTypes objectTypes;

		public static final class ObjectTypes {
			public String source;
			public String target;
		}
	}

	public static final class OtclScript {
		public Copy copy;
		public Execute execute;
	}

	public static class Command {
		public String id;
		public String factoryClassName;
		public boolean disable;
		public boolean debug;
	}
	
	public static final class Copy extends Command {
		public Source from;
		public Target to;
		
		public String toString() {
			return "Copy [from=" + from + ", to=" + to + ", id=" + id + ", factoryClassName=" + factoryClassName
					+ ", disable=" + disable + ", debug=" + debug + "]";
		}

		public static final class Source {
			public List<String> values;
			public String otclChain;
			public List<Override> overrides;
			
			public String toString() {
				return "Source [values=" + values + ", otclChain=" + otclChain + ", overrides=" + overrides + "]";
			}
		}
	}
	
	public static final class Override {
		public String tokenPath;
		public String getter;
		public String getterHelper;
	}

	public static final class Execute extends Command {
		public String otclConverter;
		public OtclModule otclModule;
		public List<String> executionOrder;
		public Source source;
		public Target target;

		public static final class Source {
			public String otclChain;
			public List<Override> overrides;
		}
		
		public static final class OtclModule {
			public String otclNamespace;
		}
	}
	
	public static final class Target {
		public String otclChain;
		public List<Override> overrides;

		public static final class Override {
			public String tokenPath;
			public String concreteType;
			public String getter;
			public String setter;
			public String getterHelper;
			public String setterHelper;
		}
	}
}
