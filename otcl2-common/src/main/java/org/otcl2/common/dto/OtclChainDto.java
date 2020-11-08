/**
* Copyright (c) otcl2.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*/
package org.otcl2.common.dto;

import java.util.Arrays;

public final class OtclChainDto {

	public String otclChain;
	public int collectionCount;
	public int dictionaryCount;
	public String[] rawOtclTokens;
	public String[] otclTokens;

	public OtclChainDto() {	
	}
	
	private OtclChainDto(Builder builder) {
		otclChain = builder.otclChain;
		collectionCount = builder.collectionCount;
		dictionaryCount = builder.dictionaryCount;
		rawOtclTokens = builder.rawOtclTokens;
		otclTokens = builder.otclTokens;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	@Override
	public String toString() {
		return "OtclChainDto [otclChain=" + otclChain + ", collectionCount=" + collectionCount + ", dictionaryCount="
				+ dictionaryCount + ", rawOtclTokens=" + Arrays.toString(rawOtclTokens) + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + collectionCount;
		result = prime * result + dictionaryCount;
		result = prime * result + ((otclChain == null) ? 0 : otclChain.hashCode());
		result = prime * result + Arrays.hashCode(otclTokens);
		result = prime * result + Arrays.hashCode(rawOtclTokens);
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
		OtclChainDto other = (OtclChainDto) obj;
		if (collectionCount != other.collectionCount)
			return false;
		if (dictionaryCount != other.dictionaryCount)
			return false;
		if (otclChain == null) {
			if (other.otclChain != null)
				return false;
		} else if (!otclChain.equals(other.otclChain))
			return false;
		if (!Arrays.equals(otclTokens, other.otclTokens))
			return false;
		if (!Arrays.equals(rawOtclTokens, other.rawOtclTokens))
			return false;
		return true;
	}

	public static class Builder {
		private String otclChain;
		private int collectionCount;
		private int dictionaryCount;
		public String[] rawOtclTokens;
		private String[] otclTokens;

		public Builder addOtclChain(String otclChain) {
			this.otclChain = otclChain;
			return this;
		}

		public Builder incrementCollectionCount() {
			this.collectionCount++;
			return this;
		}

		public Builder incrementDictionaryCount() {
			this.dictionaryCount++;
			return this;
		}

		public Builder addOtclTokens(String[] otclTokens) {
			this.otclTokens = otclTokens;
			this.rawOtclTokens = Arrays.copyOf(otclTokens, otclTokens.length);
			return this;
		}

		public String getOtclChain() {
			return otclChain;
		}

		public String[] getOtclTokens() {
			return otclTokens;
		}

		public String[] getRawOtclTokens() {
			return rawOtclTokens;
		}

		public OtclChainDto build() {
			return new OtclChainDto(this);
		}
	}
}
