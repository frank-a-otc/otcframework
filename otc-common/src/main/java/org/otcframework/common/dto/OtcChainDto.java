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
package org.otcframework.common.dto;

import java.util.Arrays;

/**
 * The Class OtcChainDto.
 */
public final class OtcChainDto {

	/** The otc chain. */
	public String otcChain;

	/** The collection count. */
	public int collectionCount;

	/** The dictionary count. */
	public int dictionaryCount;

	/** The raw otc tokens. */
	public String[] rawOtcTokens;

	/** The otc tokens. */
	public String[] otcTokens;

	/**
	 * Instantiates a new otc chain dto.
	 */
	public OtcChainDto() {
	}

	/**
	 * Instantiates a new otc chain dto.
	 *
	 * @param builder the builder
	 */
	private OtcChainDto(Builder builder) {
		otcChain = builder.otcChain;
		collectionCount = builder.collectionCount;
		dictionaryCount = builder.dictionaryCount;
		rawOtcTokens = builder.rawOtcTokens;
		otcTokens = builder.otcTokens;
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
		return "OtcChainDto [otcChain=" + otcChain + ", collectionCount=" + collectionCount + ", dictionaryCount="
				+ dictionaryCount + ", rawOtcTokens=" + Arrays.toString(rawOtcTokens) + "]";
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
		result = prime * result + collectionCount;
		result = prime * result + dictionaryCount;
		result = prime * result + ((otcChain == null) ? 0 : otcChain.hashCode());
		result = prime * result + Arrays.hashCode(otcTokens);
		result = prime * result + Arrays.hashCode(rawOtcTokens);
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
		OtcChainDto other = (OtcChainDto) obj;
		if (collectionCount != other.collectionCount)
			return false;
		if (dictionaryCount != other.dictionaryCount)
			return false;
		if (otcChain == null) {
			if (other.otcChain != null)
				return false;
		} else if (!otcChain.equals(other.otcChain))
			return false;
		if (!Arrays.equals(otcTokens, other.otcTokens))
			return false;
		return !Arrays.equals(rawOtcTokens, other.rawOtcTokens);
	}

	/**
	 * The Class Builder.
	 */
	public static class Builder {

		/** The otc chain. */
		private String otcChain;

		/** The collection count. */
		private int collectionCount;

		/** The dictionary count. */
		private int dictionaryCount;

		/** The raw otc tokens. */
		public String[] rawOtcTokens;

		/** The otc tokens. */
		private String[] otcTokens;

		/**
		 * Adds the otc chain.
		 *
		 * @param otcChain the otc chain
		 * @return the builder
		 */
		public Builder addOtcChain(String otcChain) {
			this.otcChain = otcChain;
			return this;
		}

		/**
		 * Increment collection count.
		 *
		 * @return the builder
		 */
		public Builder incrementCollectionCount() {
			this.collectionCount++;
			return this;
		}

		/**
		 * Increment dictionary count.
		 *
		 * @return the builder
		 */
		public Builder incrementDictionaryCount() {
			this.dictionaryCount++;
			return this;
		}

		/**
		 * Adds the otc tokens.
		 *
		 * @param otcTokens the otc tokens
		 * @return the builder
		 */
		public Builder addOtcTokens(String[] otcTokens) {
			this.otcTokens = otcTokens;
			this.rawOtcTokens = Arrays.copyOf(otcTokens, otcTokens.length);
			return this;
		}

		/**
		 * Gets the otc chain.
		 *
		 * @return the otc chain
		 */
		public String getOtcChain() {
			return otcChain;
		}

		/**
		 * Gets the otc tokens.
		 *
		 * @return the otc tokens
		 */
		public String[] getOtcTokens() {
			return otcTokens;
		}

		/**
		 * Gets the raw otc tokens.
		 *
		 * @return the raw otc tokens
		 */
		public String[] getRawOtcTokens() {
			return rawOtcTokens;
		}

		/**
		 * Builds the.
		 *
		 * @return the otc chain dto
		 */
		public OtcChainDto build() {
			return new OtcChainDto(this);
		}
	}
}
