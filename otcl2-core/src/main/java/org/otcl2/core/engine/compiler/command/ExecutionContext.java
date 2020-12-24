package org.otcl2.core.engine.compiler.command;

// TODO: Auto-generated Javadoc
/**
 * The Class ExecutionContext.
 */
public class ExecutionContext {

	/**
	 * The Enum CHAIN_COLLECTION_SIZE.
	 */
	public enum CHAINS_COLLECTION_COMPARISON_TYPE {
		/** The large source. */
		LARGE_SOURCE, 
		
		/** The large target. */
		LARGE_TARGET, 
		
		/** The equal. */
		EQUAL_SIZE
	};

	/** The target OCC. */
	public TargetOtclCommandContext targetOCC;
	
	/** The source OCC. */
	public SourceOtclCommandContext sourceOCC;
	
	/** The otcl command. */
	public OtclCommand otclCommand;
	
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

	public CHAINS_COLLECTION_COMPARISON_TYPE currentCollectionSizeType(TargetOtclCommandContext targetOCC) {
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
