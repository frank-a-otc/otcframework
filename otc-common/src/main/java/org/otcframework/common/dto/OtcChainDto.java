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
package org.otcframework.common.dto;

import java.util.Arrays;

// TODO: Auto-generated Javadoc
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
		if (!Arrays.equals(rawOtcTokens, other.rawOtcTokens))
			return false;
		return true;
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
