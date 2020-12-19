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

	public CHAINS_COLLECTION_COMPARISON_TYPE currentCollectionSizeType() {
		int sourceCollectionsCount = sourceOCC.collectionsCount - 1;
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
	
	/**
	 * Checks if is equal size.
	 *
	 * @return true, if is equal size
	 */
	public boolean isEqualSize() {
		return CHAINS_COLLECTION_COMPARISON_TYPE.EQUAL_SIZE == collectionsComparisonType;
	}
	
	/**
	 * Checks if is current large source.
	 *
	 * @return true, if is current large source
	 */
	public boolean isCurrentLargeSource() {
		return CHAINS_COLLECTION_COMPARISON_TYPE.LARGE_SOURCE == currentCollectionSizeType();
	}
	
	/**
	 * Checks if is current large target.
	 *
	 * @return true, if is current large target
	 */
	public boolean isCurrentLargeTarget() {
		return CHAINS_COLLECTION_COMPARISON_TYPE.LARGE_TARGET == currentCollectionSizeType();
	}
	
	/**
	 * Checks if is current equal size.
	 *
	 * @return true, if is current equal size
	 */
	public boolean isCurrentEqualSize() {
		return CHAINS_COLLECTION_COMPARISON_TYPE.EQUAL_SIZE == currentCollectionSizeType();
	}
	
}
