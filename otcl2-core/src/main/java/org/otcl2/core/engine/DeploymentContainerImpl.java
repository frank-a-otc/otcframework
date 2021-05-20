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
package org.otcl2.core.engine;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import org.msgpack.MessagePack;
import org.otcl2.common.OtclConstants;
import org.otcl2.common.config.OtclConfig;
import org.otcl2.common.dto.DeploymentDto;
import org.otcl2.common.dto.DeploymentDto.CompiledInfo;
import org.otcl2.common.dto.OtclChainDto;
import org.otcl2.common.dto.OtclCommandDto;
import org.otcl2.common.exception.OtclException;
import org.otcl2.common.executor.CodeExecutor;
import org.otcl2.common.factory.OtclCommandDtoFactory;
import org.otcl2.common.util.CommonUtils;
import org.otcl2.common.util.OtclUtils;
import org.otcl2.core.engine.compiler.exception.DeploymentContainerException;
import org.otcl2.core.engine.exception.OtclEngineException;
import org.otcl2.core.engine.utils.OtclReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

// TODO: Auto-generated Javadoc
/**
 * The Class DeploymentContainerImpl.
 */
final class DeploymentContainerImpl implements DeploymentContainer {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(DeploymentContainerImpl.class);

	/** The map packaged otcl dtos. */
	private Map<String, DeploymentDto> mapPackagedOtclDtos = new HashMap<>();
	
	/** The Constant otclDeploymentContainer. */
	private static final DeploymentContainer otclDeploymentContainer = new DeploymentContainerImpl();
	
	/** The Constant depFileFilter. */
	private static final FileFilter depFileFilter = CommonUtils.createFilenameFilter(OtclConstants.OTCL_TMD_EXTN);
	
	/** The Constant msgPack. */
	private static final MessagePack msgPack = new MessagePack();
	
	/** The Constant objectMapper. */
	private static final ObjectMapper objectMapper = new ObjectMapper();
	
	/** The Constant clzLoader. */
	private static final URLClassLoader clzLoader = OtclConfig.getTargetClassLoader();

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
		return otclDeploymentContainer;
	}

	/**
	 * Deploy.
	 */
	@Override
	public void deploy() {
		String binDir = OtclConfig.getOtclBinLocation();
		File directory = new File(binDir);
		File[] files = directory.listFiles(depFileFilter);
		if (files == null) {
			return;
		}
		LOGGER.info("Begining OTCL deployment from {}", binDir);
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
					LOGGER.error("Ignoring deployment of {}. " +
							"Probable cause: full compilation did not succeed on previous attempt.", file.getAbsolutePath());
					continue;
				}
				for (CompiledInfo compiledInfo : deploymentDto.compiledInfos.values()) {
					// init source
					initOtclCommandDto(compiledInfo.id, compiledInfo.sourceOCDStem, deploymentDto.sourceClz,
							compiledInfo.sourceOtclChainDto); 
					// init target
					initOtclCommandDto(compiledInfo.id, compiledInfo.targetOCDStem, deploymentDto.targetClz,
							compiledInfo.targetOtclChainDto); 
				}
				deploy(deploymentDto);
				hasDeployments = true;
			} catch (IOException e) {
				throw new OtclEngineException("", e);
			}
		}
		long endTime = System.nanoTime();
		if (hasDeployments) {
			LOGGER.info("Completed OTCL deployments in {} millis.", ((endTime - startTime) / 1000000.0));
		} else {
			LOGGER.info("Nothing to deploy - no deployment files found !!");
		}
		return;
	}
	
	/**
	 * Inits the otcl command dto.
	 *
	 * @param id the id
	 * @param otclCommandDto the otcl command dto
	 * @param clz the clz
	 * @param otclChainDto the otcl chain dto
	 */
	private void initOtclCommandDto(String id, OtclCommandDto otclCommandDto, Class<?> clz, OtclChainDto otclChainDto) {
		if (otclCommandDto == null) {
			return;
		}
		Field field = OtclReflectionUtil.findField(clz, otclCommandDto.fieldName);
		otclCommandDto.field = field;
		if (otclCommandDto.children == null) {
			return;
		}
		for (int idx = 1; idx < otclChainDto.otclTokens.length; idx++) {
			String otclToken = otclChainDto.otclTokens[idx];
			if (otclCommandDto.isCollection() || otclCommandDto.isMap()) {
				OtclCommandDtoFactory.createMembers(id, otclCommandDto, otclChainDto.otclChain, otclChainDto.rawOtclTokens);
				if (otclCommandDto.isCollection()) {
					otclCommandDto = otclCommandDto.children.get(otclCommandDto.fieldName);
				} else if (otclCommandDto.isMap()) {
					if (otclChainDto.rawOtclTokens[otclCommandDto.otclTokenIndex].contains(OtclConstants.MAP_KEY_REF)) {
						otclCommandDto = otclCommandDto.children.get(OtclConstants.MAP_KEY_REF + otclCommandDto.fieldName);
					} else {
						otclCommandDto = otclCommandDto.children.get(OtclConstants.MAP_VALUE_REF + otclCommandDto.fieldName);
					}
				}
			}
			OtclCommandDto childOCD = otclCommandDto.children.get(otclToken);
			field = OtclReflectionUtil.findField(otclCommandDto.fieldType, childOCD.fieldName);
			childOCD.field = field;
			otclCommandDto = childOCD;
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
			mainClz = OtclUtils.loadClass(mainClass);
		} catch (Exception ex) {
			try {
				mainClz = clzLoader.loadClass(mainClass);
			} catch (Exception e) {
				throw new OtclException("", e);
			}
		}
		CodeExecutor codeExecutor;
		try {
			codeExecutor = (CodeExecutor) mainClz.newInstance();
			deploymentDto.codeExecutor = codeExecutor;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new OtclException("", e);
		}
		mapPackagedOtclDtos.put(deploymentDto.deploymentId, deploymentDto);
		return;
	}

	/**
	 * Retrieve deployment dto.
	 *
	 * @param otclNamespace the otcl namespace
	 * @param source the source
	 * @param targetClz the target clz
	 * @return the deployment dto
	 */
	@Override
	public DeploymentDto retrieveDeploymentDto(String otclNamespace, Object source, Class<?> targetClz) {
		String deploymentId = OtclUtils.createDeploymentId(otclNamespace, source, targetClz);
		return retrieveDeploymentDto(deploymentId);
	}

	/**
	 * Retrieve deployment dto.
	 *
	 * @param otclNamespace the otcl namespace
	 * @param sourceClz the source clz
	 * @param targetClz the target clz
	 * @return the deployment dto
	 */
	@Override
	public DeploymentDto retrieveDeploymentDto(String otclNamespace, Class<?> sourceClz, Class<?> targetClz) {
		String deploymentId = OtclUtils.createDeploymentId(otclNamespace, sourceClz, targetClz);
		return retrieveDeploymentDto(deploymentId);
	}

	/**
	 * Retrieve deployment dto.
	 *
	 * @param deploymentId the deployment id
	 * @return the deployment dto
	 */
	private DeploymentDto retrieveDeploymentDto(String deploymentId) {
		DeploymentDto deploymentDto = mapPackagedOtclDtos.get(deploymentId);
		if (deploymentDto == null) {
			throw new DeploymentContainerException("",
					"OTCL deployment with ID '" + deploymentId + "' not found or not compiled and deployed!");
		}
		return deploymentDto;
	}

}
