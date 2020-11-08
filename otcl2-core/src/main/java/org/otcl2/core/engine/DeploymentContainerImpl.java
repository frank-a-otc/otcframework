/**
* Copyright (c) otcl2.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
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
import org.otcl2.common.engine.executor.CodeExecutor;
import org.otcl2.common.exception.OtclException;
import org.otcl2.common.factory.OtclCommandDtoFactory;
import org.otcl2.common.util.CommonUtils;
import org.otcl2.common.util.OtclUtils;
import org.otcl2.core.engine.compiler.exception.DeploymentContainerException;
import org.otcl2.core.engine.exception.OtclEngineException;
import org.otcl2.core.engine.utils.OtclReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

final class DeploymentContainerImpl implements DeploymentContainer {

	private static final Logger LOGGER = LoggerFactory.getLogger(DeploymentContainerImpl.class);

	private Map<String, DeploymentDto> mapPackagedOtclDtos = new HashMap<>();
	private static final DeploymentContainer otclDeploymentContainer = new DeploymentContainerImpl();
	private static final FileFilter depFileFilter = CommonUtils.createFilenameFilter(OtclConstants.OTCL_DEP_EXTN);
	private static final MessagePack msgPack = new MessagePack();
	private static final ObjectMapper objectMapper = new ObjectMapper();
	private static final URLClassLoader clzLoader = OtclConfig.getTargetClassLoader();

	private DeploymentContainerImpl() {
	}

	public static DeploymentContainer getInstance() {
		return otclDeploymentContainer;
	}

	@Override
	public void deploy() {
		String binDir = OtclConfig.getOtclBinLocation();
		LOGGER.info("Begining OTCL deployment from " + binDir);
		File directory = new File(binDir);
		for (File file : directory.listFiles(depFileFilter)) {
			if (file.isDirectory()) {
				continue;
			}
			try {
				FileInputStream fis = new FileInputStream(file);
				byte[] contents = msgPack.read(fis, byte[].class);
				DeploymentDto deploymentDto = objectMapper.readValue(contents, DeploymentDto.class);
				for (CompiledInfo compiledInfo : deploymentDto.compiledInfos.values()) {
					initOtclCommandDto(compiledInfo.id, compiledInfo.sourceOCDStem, deploymentDto.sourceClz,
							compiledInfo.sourceOtclChainDto); 
					initOtclCommandDto(compiledInfo.id, compiledInfo.sourceOCDStem, deploymentDto.sourceClz,
							compiledInfo.sourceOtclChainDto); 
				}
				deploy(deploymentDto);
			} catch (IOException e) {
				throw new OtclEngineException("", e);
			}
		}
		LOGGER.info("Completed OTCL deployments.");
		return;
	}
	
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
			if (otclToken.contains(OtclConstants.MAP_REF)) {
				otclToken = otclToken.substring(0, otclToken.indexOf(OtclConstants.CLOSE_BRACKET)); 
			}
			if (otclCommandDto.isCollection() || otclCommandDto.isMap()) {
				OtclCommandDtoFactory.createMembers(id, otclCommandDto, otclChainDto.otclChain, otclChainDto.otclTokens);
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
			mainClz = CommonUtils.loadClass(mainClass);
		} catch (Exception ex) {
			try {
				mainClz = clzLoader.loadClass(mainClass);
			} catch (Exception e) {
				throw new OtclException("", e);
			}
		}
		CodeExecutor otclCodeExecutor;
		try {
			otclCodeExecutor = (CodeExecutor) mainClz.newInstance();
			deploymentDto.otclCodeExecutor = otclCodeExecutor;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new OtclException("", e);
		}
		mapPackagedOtclDtos.put(deploymentDto.deploymentId, deploymentDto);
		return;
	}

	@Override
	public DeploymentDto retrieveDeploymentDto(String otclNamespace, Object source, Class<?> targetClz) {
		String deploymentId = OtclUtils.createDeploymentId(otclNamespace, source, targetClz);
		return retrieveDeploymentDto(deploymentId);
	}

	@Override
	public DeploymentDto retrieveDeploymentDto(String otclNamespace, Class<?> sourceClz, Class<?> targetClz) {
		String deploymentId = OtclUtils.createDeploymentId(otclNamespace, sourceClz, targetClz);
		return retrieveDeploymentDto(deploymentId);
	}

	private DeploymentDto retrieveDeploymentDto(String deploymentId) {
		DeploymentDto deploymentDto = mapPackagedOtclDtos.get(deploymentId);
		if (deploymentDto == null) {
			throw new DeploymentContainerException("",
					"OTCL deployment with ID '" + deploymentId + "' not found or not compiled and deployed!");
		}
		return deploymentDto;
	}

}
