/**
* Copyright (c) otclfoundation.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*/
package org.otcl2.common.engine.compiler;

import org.otcl2.common.dto.OtclDto;

// TODO: Auto-generated Javadoc
/**
 * The Class CompilationReport.
 */
public final class CompilationReport {

	/** The otcl namespace. */
	public String otclNamespace;
	
	/** The otcl file name. */
	public String otclFileName;
	
	/** The message. */
	public String message;
	
	/** The did succeed. */
	public boolean didSucceed;
	
	/** The cause. */
	public Throwable cause;
	
	/** The otcl dto. */
	public OtclDto otclDto;

	/**
	 * Instantiates a new compilation report.
	 *
	 * @param builder the builder
	 */
	private CompilationReport(Builder builder) {
		otclNamespace = builder.otclNamespace;
		otclFileName = builder.otclFileName;
		message = builder.message;
		didSucceed = builder.didSucceed;
		cause = builder.cause;
		otclDto = builder.otclDto;
	}

	/**
	 * New builder.
	 *
	 * @return the builder
	 */
	public static Builder newBuilder() {
		return new Builder();
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return "CompilationReport [otclPackage=" + otclNamespace + ", otclFileName=" + otclFileName + ", message="
				+ message + ", didSucceed=" + didSucceed + ", cause=" + cause + "]";
	}

	/**
	 * Hash code.
	 *
	 * @return the int
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (didSucceed ? 1231 : 1237);
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + ((otclDto == null) ? 0 : otclDto.hashCode());
		result = prime * result + ((otclFileName == null) ? 0 : otclFileName.hashCode());
		result = prime * result + ((otclNamespace == null) ? 0 : otclNamespace.hashCode());
		return result;
	}

	/**
	 * Equals.
	 *
	 * @param obj the obj
	 * @return true, if successful
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CompilationReport other = (CompilationReport) obj;
		if (didSucceed != other.didSucceed)
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		if (otclDto == null) {
			if (other.otclDto != null)
				return false;
		} else if (!otclDto.equals(other.otclDto))
			return false;
		if (otclFileName == null) {
			if (other.otclFileName != null)
				return false;
		} else if (!otclFileName.equals(other.otclFileName))
			return false;
		if (otclNamespace == null) {
			if (other.otclNamespace != null)
				return false;
		} else if (!otclNamespace.equals(other.otclNamespace))
			return false;
		return true;
	}

	/**
	 * The Class Builder.
	 */
	public static class Builder {
		
		/** The otcl namespace. */
		private String otclNamespace;
		
		/** The otcl file name. */
		private String otclFileName;
		
		/** The message. */
		private String message;
		
		/** The did succeed. */
		private boolean didSucceed;
		
		/** The cause. */
		private Throwable cause;
		
		/** The otcl dto. */
		private OtclDto otclDto;

		/**
		 * Adds the otcl namespace.
		 *
		 * @param otclNamespace the otcl namespace
		 * @return the builder
		 */
		public Builder addOtclNamespace(String otclNamespace) {
			this.otclNamespace = otclNamespace;
			return this;
		}

		/**
		 * Adds the otcl file name.
		 *
		 * @param otclFileName the otcl file name
		 * @return the builder
		 */
		public Builder addOtclFileName(String otclFileName) {
			this.otclFileName = otclFileName;
			return this;
		}

		/**
		 * Adds the message.
		 *
		 * @param message the message
		 * @return the builder
		 */
		public Builder addMessage(String message) {
			this.message = message;
			return this;
		}

		/**
		 * Adds the did succeed.
		 *
		 * @param didSucceed the did succeed
		 * @return the builder
		 */
		public Builder addDidSucceed(boolean didSucceed) {
			this.didSucceed = didSucceed;
			return this;
		}

		/**
		 * Adds the cause.
		 *
		 * @param cause the cause
		 * @return the builder
		 */
		public Builder addCause(Throwable cause) {
			this.cause = cause;
			return this;
		}

		/**
		 * Adds the otcl dto.
		 *
		 * @param otclDto the otcl dto
		 * @return the builder
		 */
		public Builder addOtclDto(OtclDto otclDto) {
			this.otclDto = otclDto;
			return this;
		}

		/**
		 * Builds the.
		 *
		 * @return the compilation report
		 */
		public CompilationReport build() {
			return new CompilationReport(this);
		}
	}

}
