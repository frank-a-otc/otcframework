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
package org.otcframework.core.engine.compiler.command;

/**
 * The Class ExecutionContext.
 */
// TODO: Auto-generated Javadoc
public class ExecutionContext {

	/**
	 * The Enum CHAINS_COLLECTION_COMPARISON_TYPE.
	 */
	public enum CHAINS_COLLECTION_COMPARISON_TYPE {

		/** The large source. */
		LARGE_SOURCE,

		/** The large target. */
		LARGE_TARGET,

		/** The equal size. */
		EQUAL_SIZE
	};

	/** The target OCC. */
	public TargetOtcCommandContext targetOCC;

	/** The source OCC. */
	public SourceOtcCommandContext sourceOCC;

	/** The otc command. */
	public OtcCommand otcCommand;

	/** The target clz. */
	public Class<?> targetClz;

	/** The source clz. */
	public Class<?> sourceClz;

	/** The collections comparison type. */
	public CHAINS_COLLECTION_COMPARISON_TYPE collectionsComparisonType;

	/** The should increment offset idx. */
	public boolean shouldIncrementOffsetIdx;

	/** The is offset idx already added. */
	public boolean isOffsetIdxAlreadyAdded = false;

	/**
	 * Inits the collection size type.
	 */
	public void initCollectionSizeType() {
		int sourceDescendantsCollectionsCount = sourceOCC.descendantsCollectionsCountInclusive();
		int targetDescendantsCollectionsCount = targetOCC.descendantsCollectionsCountInclusive();
		if (targetDescendantsCollectionsCount > sourceDescendantsCollectionsCount) {
			collectionsComparisonType = CHAINS_COLLECTION_COMPARISON_TYPE.LARGE_TARGET;
		} else if (targetDescendantsCollectionsCount == sourceDescendantsCollectionsCount) {
			collectionsComparisonType = CHAINS_COLLECTION_COMPARISON_TYPE.EQUAL_SIZE;
		} else {
			collectionsComparisonType = CHAINS_COLLECTION_COMPARISON_TYPE.LARGE_SOURCE;
		}
	}

	/**
	 * Current collection size type.
	 *
	 * @param targetOCC the target OCC
	 * @return the chains collection comparison type
	 */
	public CHAINS_COLLECTION_COMPARISON_TYPE currentCollectionSizeType(TargetOtcCommandContext targetOCC) {
		int sourceCollectionsCount = 0;
		if (targetOCC.isCurrentTokenAnchored() || !targetOCC.hasAnchorInChain) {
			sourceCollectionsCount = sourceOCC.collectionsCount;
		} else {
			sourceCollectionsCount = sourceOCC.collectionsCount - 1;
		}
		int targetDescendantsCollectionsCount = targetOCC.descendantsCollectionsCountInclusive();
		CHAINS_COLLECTION_COMPARISON_TYPE currentCollectionSizeType;
		if (targetDescendantsCollectionsCount > sourceCollectionsCount) {
			currentCollectionSizeType = CHAINS_COLLECTION_COMPARISON_TYPE.LARGE_TARGET;
		} else if (targetDescendantsCollectionsCount == sourceCollectionsCount) {
			currentCollectionSizeType = CHAINS_COLLECTION_COMPARISON_TYPE.EQUAL_SIZE;
		} else {
			currentCollectionSizeType = CHAINS_COLLECTION_COMPARISON_TYPE.LARGE_SOURCE;
		}
		return currentCollectionSizeType;
	}

	/**
	 * Current collection inclusive size type.
	 *
	 * @return the chains collection comparison type
	 */
	public CHAINS_COLLECTION_COMPARISON_TYPE currentCollectionInclusiveSizeType() {
		int sourceCollectionsCount = sourceOCC.collectionsCount;
		int targetDescendantsCollectionsCount = targetOCC.descendantsCollectionsCountInclusive();
		CHAINS_COLLECTION_COMPARISON_TYPE currentCollectionSizeType;
		if (targetDescendantsCollectionsCount > sourceCollectionsCount) {
			currentCollectionSizeType = CHAINS_COLLECTION_COMPARISON_TYPE.LARGE_TARGET;
		} else if (targetDescendantsCollectionsCount == sourceCollectionsCount) {
			currentCollectionSizeType = CHAINS_COLLECTION_COMPARISON_TYPE.EQUAL_SIZE;
		} else {
			currentCollectionSizeType = CHAINS_COLLECTION_COMPARISON_TYPE.LARGE_SOURCE;
		}
		return currentCollectionSizeType;
	}

	/**
	 * Checks if is large source.
	 *
	 * @return true, if is large source
	 */
	public boolean isLargeSource() {
		return CHAINS_COLLECTION_COMPARISON_TYPE.LARGE_SOURCE == collectionsComparisonType;
	}

	/**
	 * Checks if is large target.
	 *
	 * @return true, if is large target
	 */
	public boolean isLargeTarget() {
		return CHAINS_COLLECTION_COMPARISON_TYPE.LARGE_TARGET == collectionsComparisonType;
	}
}
