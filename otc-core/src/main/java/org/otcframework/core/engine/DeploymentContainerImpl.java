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
package org.otcframework.core.engine;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import org.msgpack.MessagePack;
import org.otcframework.common.OtcConstants;
import org.otcframework.common.config.OtcConfig;
import org.otcframework.common.dto.DeploymentDto;
import org.otcframework.common.dto.DeploymentDto.CompiledInfo;
import org.otcframework.common.dto.OtcChainDto;
import org.otcframework.common.dto.OtcCommandDto;
import org.otcframework.common.exception.OtcException;
import org.otcframework.common.executor.CodeExecutor;
import org.otcframework.common.factory.OtcCommandDtoFactory;
import org.otcframework.common.util.CommonUtils;
import org.otcframework.common.util.OtcUtils;
import org.otcframework.core.engine.compiler.exception.DeploymentContainerException;
import org.otcframework.core.engine.exception.OtcEngineException;
import org.otcframework.core.engine.utils.OtcReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class DeploymentContainerImpl.
 */
// TODO: Auto-generated Javadoc
final class DeploymentContainerImpl implements DeploymentContainer {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(DeploymentContainerImpl.class);

	/** The map packaged otc dtos. */
	private Map<String, DeploymentDto> mapPackagedOtcDtos = new HashMap<>();

	/** The Constant otcDeploymentContainer. */
	private static final DeploymentContainer otcDeploymentContainer = new DeploymentContainerImpl();

	/** The Constant depFileFilter. */
	private static final FileFilter depFileFilter = CommonUtils.createFilenameFilter(OtcConstants.OTC_TMD_EXTN);

	/** The Constant msgPack. */
	private static final MessagePack msgPack = new MessagePack();

	/** The Constant objectMapper. */
	private static final ObjectMapper objectMapper = new ObjectMapper();

	/** The Constant clzLoader. */
	private static final URLClassLoader clzLoader = OtcConfig.getTargetClassLoader();

	/**
	 * Instantiates a new deployment container impl.
	 */
	private DeploymentContainerImpl() {
	}

	/**
	 * Gets the single instance of DeploymentContainerImpl.
	 *
	 * @return single instance of DeploymentContainerImpl
	 */
	public static DeploymentContainer getInstance() {
		return otcDeploymentContainer;
	}

	/**
	 * Deploy.
	 */
	@Override
	public void deploy() {
		String binDir = OtcConfig.getOtcBinLocation();
		File directory = new File(binDir);
		File[] files = directory.listFiles(depFileFilter);
		if (files == null) {
			return;
		}
		LOGGER.info("Begining OTC deployment from {}", binDir);
		long startTime = System.nanoTime();
		boolean hasDeployments = false;
		for (File file : files) {
			if (file.isDirectory()) {
				continue;
			}
			try {
				FileInputStream fis = new FileInputStream(file);
				byte[] contents = msgPack.read(fis, byte[].class);
				DeploymentDto deploymentDto = objectMapper.readValue(contents, DeploymentDto.class);
				if (deploymentDto.isError) {
					LOGGER.error(
							"Ignoring deployment of {}. "
									+ "Probable cause: full compilation did not succeed on previous attempt.",
							file.getAbsolutePath());
					continue;
				}
				for (CompiledInfo compiledInfo : deploymentDto.compiledInfos.values()) {
					// init source
					initOtcCommandDto(compiledInfo.id, compiledInfo.sourceOCDStem, deploymentDto.sourceClz,
							compiledInfo.sourceOtcChainDto);
					// init target
					initOtcCommandDto(compiledInfo.id, compiledInfo.targetOCDStem, deploymentDto.targetClz,
							compiledInfo.targetOtcChainDto);
				}
				deploy(deploymentDto);
				hasDeployments = true;
			} catch (IOException e) {
				throw new OtcEngineException("", e);
			}
		}
		long endTime = System.nanoTime();
		if (hasDeployments) {
			LOGGER.info("Completed OTC deployments in {} millis.", ((endTime - startTime) / 1000000.0));
		} else {
			LOGGER.info("Nothing to deploy - no deployment files found !!");
		}
		return;
	}

	/**
	 * Inits the otc command dto.
	 *
	 * @param id            the id
	 * @param otcCommandDto the otc command dto
	 * @param clz           the clz
	 * @param otcChainDto   the otc chain dto
	 */
	private void initOtcCommandDto(String id, OtcCommandDto otcCommandDto, Class<?> clz, OtcChainDto otcChainDto) {
		if (otcCommandDto == null) {
			return;
		}
		Field field = OtcReflectionUtil.findField(clz, otcCommandDto.fieldName);
		otcCommandDto.field = field;
		if (otcCommandDto.children == null) {
			return;
		}
		for (int idx = 1; idx < otcChainDto.otcTokens.length; idx++) {
			String otcToken = otcChainDto.otcTokens[idx];
			if (otcCommandDto.isCollection() || otcCommandDto.isMap()) {
				OtcCommandDtoFactory.createMembers(id, otcCommandDto, otcChainDto.otcChain, otcChainDto.rawOtcTokens);
				if (otcCommandDto.isCollection()) {
					otcCommandDto = otcCommandDto.children.get(otcCommandDto.fieldName);
				} else if (otcCommandDto.isMap()) {
					if (otcChainDto.rawOtcTokens[otcCommandDto.otcTokenIndex].contains(OtcConstants.MAP_KEY_REF)) {
						otcCommandDto = otcCommandDto.children.get(OtcConstants.MAP_KEY_REF + otcCommandDto.fieldName);
					} else {
						otcCommandDto = otcCommandDto.children
								.get(OtcConstants.MAP_VALUE_REF + otcCommandDto.fieldName);
					}
				}
			}
			OtcCommandDto childOCD = otcCommandDto.children.get(otcToken);
			field = OtcReflectionUtil.findField(otcCommandDto.fieldType, childOCD.fieldName);
			childOCD.field = field;
			otcCommandDto = childOCD;
		}
		return;
	}

	/**
	 * Deploy.
	 *
	 * @param deploymentDto the deployment dto
	 */
	@Override
	public void deploy(DeploymentDto deploymentDto) {
		if (deploymentDto == null) {
			LOGGER.warn("Nothing to deploy!");
			return;
		}
		String mainClass = deploymentDto.mainClass;
		// exception will be thrown for loadclass if class is not compiled.
		Class<?> mainClz = null;
		try {
			mainClz = OtcUtils.loadClass(mainClass);
		} catch (Exception ex) {
			try {
				mainClz = clzLoader.loadClass(mainClass);
			} catch (Exception e) {
				throw new OtcException("", e);
			}
		}
		CodeExecutor codeExecutor;
		try {
			codeExecutor = (CodeExecutor) mainClz.newInstance();
			deploymentDto.codeExecutor = codeExecutor;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new OtcException("", e);
		}
		mapPackagedOtcDtos.put(deploymentDto.deploymentId, deploymentDto);
		return;
	}

	/**
	 * Retrieve deployment dto.
	 *
	 * @param otcNamespace the otc namespace
	 * @param source       the source
	 * @param targetClz    the target clz
	 * @return the deployment dto
	 */
	@Override
	public DeploymentDto retrieveDeploymentDto(String otcNamespace, Object source, Class<?> targetClz) {
		String deploymentId = OtcUtils.createDeploymentId(otcNamespace, source, targetClz);
		return retrieveDeploymentDto(deploymentId);
	}

	/**
	 * Retrieve deployment dto.
	 *
	 * @param otcNamespace the otc namespace
	 * @param sourceClz    the source clz
	 * @param targetClz    the target clz
	 * @return the deployment dto
	 */
	@Override
	public DeploymentDto retrieveDeploymentDto(String otcNamespace, Class<?> sourceClz, Class<?> targetClz) {
		String deploymentId = OtcUtils.createDeploymentId(otcNamespace, sourceClz, targetClz);
		return retrieveDeploymentDto(deploymentId);
	}

	/**
	 * Retrieve deployment dto.
	 *
	 * @param deploymentId the deployment id
	 * @return the deployment dto
	 */
	private DeploymentDto retrieveDeploymentDto(String deploymentId) {
		DeploymentDto deploymentDto = mapPackagedOtcDtos.get(deploymentId);
		if (deploymentDto == null) {
			throw new DeploymentContainerException("",
					"OTC deployment with ID '" + deploymentId + "' not found or not compiled and deployed!");
		}
		return deploymentDto;
	}
}
