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
package org.otcframework.compiler.command;

/**
 * The Class ExecutionContext.
 */
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
