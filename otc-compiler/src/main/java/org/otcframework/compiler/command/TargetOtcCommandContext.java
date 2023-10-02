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

import org.otcframework.common.OtcConstants.ALGORITHM_ID;
import org.otcframework.common.compiler.OtcCommandContext;
import org.otcframework.common.dto.ClassDto;

/**
 * The Class TargetOtcCommandContext.
 */
public final class TargetOtcCommandContext extends OtcCommandContext {

	/** The helper. */
	public String helper;

	/** The main class dto. */
	public ClassDto mainClassDto;

	/** The factory class dto. */
	public ClassDto factoryClassDto;

	/** The has anchor in chain. */
	public boolean hasAnchorInChain;

	/** The has pre anchor. */
	public boolean hasPreAnchor;

	/** The has post anchor. */
	public boolean hasPostAnchor;

	/** The execute module otc namespace. */
	public String executeModuleOtcNamespace;

	/** The has execute module. */
	public boolean hasExecuteModule;

	/** The execute otc converter. */
	public String executeOtcConverter;

	/** The has execute converter. */
	public boolean hasExecuteConverter;

	/** The algorithm id. */
	public ALGORITHM_ID algorithmId;

	/** The loops counter. */
	public int loopsCounter;

	/** The anchor index. */
	public int anchorIndex;

	/**
	 * Instantiates a new target otc command context.
	 */
	public TargetOtcCommandContext() {
		mainClassDto = new ClassDto();
		if (factoryClassDto == null) {
			factoryClassDto = new ClassDto();
		}
	}

	/**
	 * Append code.
	 *
	 * @param code the code
	 */
	public void appendCode(String code) {
		factoryClassDto.codeBuilder.append(code);
	}

	/**
	 * Append code.
	 *
	 * @param code the code
	 */
	public void appendCode(StringBuilder code) {
		factoryClassDto.codeBuilder.append(code);
	}
}
