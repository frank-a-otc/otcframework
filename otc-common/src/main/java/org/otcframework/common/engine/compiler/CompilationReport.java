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
package org.otcframework.common.engine.compiler;

import org.otcframework.common.dto.OtcDto;

// TODO: Auto-generated Javadoc
/**
 * The Class CompilationReport.
 */
public final class CompilationReport {

	/** The otc namespace. */
	public String otcNamespace;
	
	/** The otc file name. */
	public String otcFileName;
	
	/** The message. */
	public String message;
	
	/** The did succeed. */
	public boolean didSucceed;
	
	/** The cause. */
	public Throwable cause;
	
	/** The otc dto. */
	public OtcDto otcDto;

	/**
	 * Instantiates a new compilation report.
	 *
	 * @param builder the builder
	 */
	private CompilationReport(Builder builder) {
		otcNamespace = builder.otcNamespace;
		otcFileName = builder.otcFileName;
		message = builder.message;
		didSucceed = builder.didSucceed;
		cause = builder.cause;
		otcDto = builder.otcDto;
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
		return "CompilationReport [otcPackage=" + otcNamespace + ", otcFileName=" + otcFileName + ", message="
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
		result = prime * result + ((otcDto == null) ? 0 : otcDto.hashCode());
		result = prime * result + ((otcFileName == null) ? 0 : otcFileName.hashCode());
		result = prime * result + ((otcNamespace == null) ? 0 : otcNamespace.hashCode());
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
		if (otcDto == null) {
			if (other.otcDto != null)
				return false;
		} else if (!otcDto.equals(other.otcDto))
			return false;
		if (otcFileName == null) {
			if (other.otcFileName != null)
				return false;
		} else if (!otcFileName.equals(other.otcFileName))
			return false;
		if (otcNamespace == null) {
			if (other.otcNamespace != null)
				return false;
		} else if (!otcNamespace.equals(other.otcNamespace))
			return false;
		return true;
	}

	/**
	 * The Class Builder.
	 */
	public static class Builder {
		
		/** The otc namespace. */
		private String otcNamespace;
		
		/** The otc file name. */
		private String otcFileName;
		
		/** The message. */
		private String message;
		
		/** The did succeed. */
		private boolean didSucceed;
		
		/** The cause. */
		private Throwable cause;
		
		/** The otc dto. */
		private OtcDto otcDto;

		/**
		 * Adds the otc namespace.
		 *
		 * @param otcNamespace the otc namespace
		 * @return the builder
		 */
		public Builder addOtcNamespace(String otcNamespace) {
			this.otcNamespace = otcNamespace;
			return this;
		}

		/**
		 * Adds the otc file name.
		 *
		 * @param otcFileName the otc file name
		 * @return the builder
		 */
		public Builder addOtcFileName(String otcFileName) {
			this.otcFileName = otcFileName;
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
		 * Adds the otc dto.
		 *
		 * @param otcDto the otc dto
		 * @return the builder
		 */
		public Builder addOtcDto(OtcDto otcDto) {
			this.otcDto = otcDto;
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
