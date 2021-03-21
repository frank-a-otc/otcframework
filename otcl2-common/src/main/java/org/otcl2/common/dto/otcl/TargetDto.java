/**
* Copyright (c) otclfoundation.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*/
package org.otcl2.common.dto.otcl;

import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class OtclFileDto.
 */
public class TargetDto {

	/** The otcl chain. */
	public String otclChain;
	
	/** The overrides. */
	public List<Override> overrides;

	/**
	 * The Class Override.
	 */
	public static final class Override extends OverrideDto {
		
		/** The concrete type. */
		public String concreteType;

		/** The setter. */
		public String setter;

		/** The setter helper. */
		public String setterHelper;
	}
}


