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
package org.otcframework.common.dto.otc;

import java.util.List;

/**
 * The Class TargetDto.
 */
// TODO: Auto-generated Javadoc
public class TargetDto {

	/** The otc chain. */
	public String objectPath;

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
