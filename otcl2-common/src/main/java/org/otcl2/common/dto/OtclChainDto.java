/**
* Copyright (c) otclfoundation.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*
* This file is part of the OTCL framework.
* 
*  The OTCL framework is free software: you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  the Free Software Foundation, version 3 of the License.
*
*  The OTCL framework is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU General Public License for more details.
*
*  A copy of the GNU General Public License is made available as 'License.md' file, 
*  along with OTCL framework project.  If not, see <https://www.gnu.org/licenses/>.
*
*/
package org.otcl2.common.dto;

import java.util.Arrays;

// TODO: Auto-generated Javadoc
/**
 * The Class OtclChainDto.
 */
public final class OtclChainDto {

	/** The otcl chain. */
	public String otclChain;
	
	/** The collection count. */
	public int collectionCount;
	
	/** The dictionary count. */
	public int dictionaryCount;
	
	/** The raw otcl tokens. */
	public String[] rawOtclTokens;
	
	/** The otcl tokens. */
	public String[] otclTokens;

	/**
	 * Instantiates a new otcl chain dto.
	 */
	public OtclChainDto() {	
	}
	
	/**
	 * Instantiates a new otcl chain dto.
	 *
	 * @param builder the builder
	 */
	private OtclChainDto(Builder builder) {
		otclChain = builder.otclChain;
		collectionCount = builder.collectionCount;
		dictionaryCount = builder.dictionaryCount;
		rawOtclTokens = builder.rawOtclTokens;
		otclTokens = builder.otclTokens;
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
		return "OtclChainDto [otclChain=" + otclChain + ", collectionCount=" + collectionCount + ", dictionaryCount="
				+ dictionaryCount + ", rawOtclTokens=" + Arrays.toString(rawOtclTokens) + "]";
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
		result = prime * result + ((otclChain == null) ? 0 : otclChain.hashCode());
		result = prime * result + Arrays.hashCode(otclTokens);
		result = prime * result + Arrays.hashCode(rawOtclTokens);
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

	/**
	 * The Class Builder.
	 */
	public static class Builder {
		
		/** The otcl chain. */
		private String otclChain;
		
		/** The collection count. */
		private int collectionCount;
		
		/** The dictionary count. */
		private int dictionaryCount;
		
		/** The raw otcl tokens. */
		public String[] rawOtclTokens;
		
		/** The otcl tokens. */
		private String[] otclTokens;

		/**
		 * Adds the otcl chain.
		 *
		 * @param otclChain the otcl chain
		 * @return the builder
		 */
		public Builder addOtclChain(String otclChain) {
			this.otclChain = otclChain;
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
		 * Adds the otcl tokens.
		 *
		 * @param otclTokens the otcl tokens
		 * @return the builder
		 */
		public Builder addOtclTokens(String[] otclTokens) {
			this.otclTokens = otclTokens;
			this.rawOtclTokens = Arrays.copyOf(otclTokens, otclTokens.length);
			return this;
		}

		/**
		 * Gets the otcl chain.
		 *
		 * @return the otcl chain
		 */
		public String getOtclChain() {
			return otclChain;
		}

		/**
		 * Gets the otcl tokens.
		 *
		 * @return the otcl tokens
		 */
		public String[] getOtclTokens() {
			return otclTokens;
		}

		/**
		 * Gets the raw otcl tokens.
		 *
		 * @return the raw otcl tokens
		 */
		public String[] getRawOtclTokens() {
			return rawOtclTokens;
		}

		/**
		 * Builds the.
		 *
		 * @return the otcl chain dto
		 */
		public OtclChainDto build() {
			return new OtclChainDto(this);
		}
	}
}
