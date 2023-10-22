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
package org.otcframework.executor;

import org.otcframework.common.dto.RegistryDto;

/**
 * The Interface OtcRegistry.
 */
public interface OtcRegistry {

	/**
	 * register.
	 */
	void register();

	/**
	 * register.
	 *
	 * @param registryDto the registry dto
	 */
	void register(RegistryDto registryDto);

	/**
	 * Retrieve registry dto.
	 *
	 * @param otcNamespace the otc namespace
	 * @param sourceClz    the source clz
	 * @param targetClz    the target clz
	 * @return the registry dto
	 */
	RegistryDto retrieveRegistryDto(String otcNamespace, Class<?> sourceClz, Class<?> targetClz);

	/**
	 * Retrieve registry dto.
	 *
	 * @param otcNamespace the otc namespace
	 * @param source       the source
	 * @param targetClz    the target clz
	 * @return the registry dto
	 */
	RegistryDto retrieveRegistryDto(String otcNamespace, Object source, Class<?> targetClz);
}
