/**
* Copyright (c) otcl2.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*/
package org.otcl2.common.engine.compiler;

import org.otcl2.common.dto.OtclDto;

public final class CompilationReport {

	public String otclNamespace;
	public String otclFileName;
	public String message;
	public boolean didSucceed;
	public Throwable cause;
	public OtclDto otclDto;

	private CompilationReport(Builder builder) {
		otclNamespace = builder.otclNamespace;
		otclFileName = builder.otclFileName;
		message = builder.message;
		didSucceed = builder.didSucceed;
		cause = builder.cause;
		otclDto = builder.otclDto;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	@Override
	public String toString() {
		return "CompilationReport [otclPackage=" + otclNamespace + ", otclFileName=" + otclFileName + ", message="
				+ message + ", didSucceed=" + didSucceed + ", cause=" + cause + "]";
	}

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

	public static class Builder {
		private String otclNamespace;
		private String otclFileName;
		private String message;
		private boolean didSucceed;
		private Throwable cause;
		private OtclDto otclDto;

		public Builder addOtclNamespace(String otclNamespace) {
			this.otclNamespace = otclNamespace;
			return this;
		}

		public Builder addOtclFileName(String otclFileName) {
			this.otclFileName = otclFileName;
			return this;
		}

		public Builder addMessage(String message) {
			this.message = message;
			return this;
		}

		public Builder addDidSucceed(boolean didSucceed) {
			this.didSucceed = didSucceed;
			return this;
		}

		public Builder addCause(Throwable cause) {
			this.cause = cause;
			return this;
		}

		public Builder addOtclDto(OtclDto otclDto) {
			this.otclDto = otclDto;
			return this;
		}

		public CompilationReport build() {
			return new CompilationReport(this);
		}
	}

}
