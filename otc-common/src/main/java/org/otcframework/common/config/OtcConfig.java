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
package org.otcframework.common.config;

import org.otcframework.common.config.exception.OtcConfigException;
import org.otcframework.common.exception.OtcException;
import org.otcframework.common.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

/**
 * The Enum OtcConfig.
 */
public enum OtcConfig {

	/** The instance. */
	INSTANCE;

	private static final Logger LOGGER = LoggerFactory.getLogger(OtcConfig.class);

	private static final String OTC_HOME_ENV_VAR = "OTC_HOME";

	private static final String OTC_UNITTEST_FOLDER = "otc-unittest" + File.separator;
	private static final String OTC_LIB_FOLDER = "lib" + File.separator;
	public static final String OTC_SRC_FOLDER = "src" + File.separator;
	public static final String OTC_TMD_FOLDER = "tmd" + File.separator;
	public static final String OTC_TARGET_FOLDER = "target" + File.separator;
	public static final String OTC_CONFIG_FILE = "config" + File.separator + "otc.yaml";
	private static boolean isDefaultLocations = true;
	private static String otcHome;

	private static final YamlConfig YAML_CONFIG;
	private static final URLClassLoader CLZ_LOADER;
	private static String sourceCodeLocation;
	private static String tmdLocation;
	private static String targetLocation;
	private static final Integer DEFAULT_CYCLIC_REFERENCE_DEPTH = 2;

	/**
	 * Instantiates a new otc config.
	 */
	OtcConfig() {
	}

	static {
		Map<String, String> sysEnv = System.getenv();
		if (!sysEnv.containsKey(OTC_HOME_ENV_VAR)) {
			throw new OtcConfigException("",
					"Oops... Cannot proceed - '" + OTC_HOME_ENV_VAR + "' not set! Please set '" +
							OTC_HOME_ENV_VAR + "' environment variable.");
		}
		otcHome = sysEnv.get(OTC_HOME_ENV_VAR);
		if (CommonUtils.isTrimmedAndEmpty(otcHome)) {
			throw new OtcException("", "Oops... Environment variable '" + OTC_HOME_ENV_VAR + "' not set! ");
		}
		if (!otcHome.endsWith(File.separator)) {
			otcHome += File.separator;
		}
		try {
			YAML_CONFIG = YamlSerializationHelper.deserialize(otcHome + OTC_CONFIG_FILE, YamlConfig.class);
		} catch (Exception ex) {
			throw new OtcConfigException(ex);
		}
		// -- load sourceCodeLocation and tmdLocation properties
		if (YAML_CONFIG.compiler != null && YAML_CONFIG.compiler.locations != null) {
			sourceCodeLocation = YAML_CONFIG.compiler.locations.sourceCodeLocation;
			tmdLocation = YAML_CONFIG.compiler.locations.tmdLocation;
			targetLocation = YAML_CONFIG.compiler.locations.targetLocation;
			boolean isSourceCodeLocationDefined = !CommonUtils.isTrimmedAndEmpty(sourceCodeLocation);
			boolean isTmdLocationDefined = !CommonUtils.isTrimmedAndEmpty(tmdLocation);
			boolean isTargetLocationDefined = !CommonUtils.isTrimmedAndEmpty(targetLocation);
			if (!(isSourceCodeLocationDefined == isTmdLocationDefined &&
					isSourceCodeLocationDefined == isTargetLocationDefined)) {
				throw new OtcConfigException("", String.format("Either ALL or NONE of this set of 3 properties " +
						"('compiler.locations.sourceCodeLocation:', 'compiler.locations.tmdLocation:', " +
						"'compiler.locations.targetLocation:') should be defined in the '%s%s' file.", otcHome, OTC_CONFIG_FILE));
			}
			if (isSourceCodeLocationDefined) {
				isDefaultLocations = false;
			}
			if (getCleanupBeforeCompile()) {
				LOGGER.warn("You have set 'compiler.cleanupBeforeCompile' property to true. Updated " +
						"source-code if any will be lost during clean-up.");
			}
		}
		sourceCodeLocation = initFolder(sourceCodeLocation, OTC_SRC_FOLDER);
		targetLocation = initFolder(targetLocation, OTC_TARGET_FOLDER);
		if (!CommonUtils.isTrimmedAndEmpty(tmdLocation)) {
			if (!tmdLocation.endsWith(File.separator)) {
				tmdLocation += File.separator;
			}
			tmdLocation += OTC_TMD_FOLDER;
		} else {
			tmdLocation = otcHome + OTC_TMD_FOLDER;
		}
		OtcUtils.deleteRecursive(tmdLocation);
		OtcUtils.creteDirectory(tmdLocation);

		try {
			URL url = new File(targetLocation).toURI().toURL();
			URL[] urls = new URL[] { url };
			CLZ_LOADER = URLClassLoader.newInstance(urls);
		} catch (MalformedURLException e) {
			throw new OtcConfigException(e);
		}
		Set<String> filteredPackages = YAML_CONFIG.filterPackages;
		PackagesFilterUtil.setFilteredPackages(filteredPackages);
	}

	private static String initFolder(String configuredPath, String defaultPath) {
		String path = configuredPath;
		if (!CommonUtils.isTrimmedAndEmpty(configuredPath)) {
			if (!configuredPath.endsWith(File.separator)) {
				path += File.separator;
			}
		} else {
			path = otcHome + defaultPath;
		}
		OtcUtils.deleteRecursive(path);
		OtcUtils.creteDirectory(path);
		return path;
	}

	/**
	 * Gets the otc home location.
	 *
	 * @return the otc home location
	 */
	public static String getOtcHomeLocation() {
		return otcHome;
	}

	public static boolean isDefaultLocations() {
		return isDefaultLocations;
	}
	/**
	 * Gets the otc lib location.
	 *
	 * @return the otc lib location
	 */
	public static String getOtcLibLocation() {
		if (YAML_CONFIG.compiler.locations != null && YAML_CONFIG.compiler.locations.libLocation != null) {
			return YAML_CONFIG.compiler.locations.libLocation;
		}
		return otcHome + OTC_LIB_FOLDER;
	}

	public static boolean getCleanupBeforeCompile() {
		if (YAML_CONFIG.compiler.cleanupBeforeCompile != null) {
			return YAML_CONFIG.compiler.cleanupBeforeCompile;
		}
		return false;
	}

	/**
	 * Gets the otc source location.
	 *
	 * @return the otc source location
	 */
	public static String getUnitTestLocation() {
		return otcHome + OTC_UNITTEST_FOLDER;
	}

	/**
	 * Gets the source code location.
	 *
	 * @return the source code location
	 */
	public static String getSourceCodeLocation() {
		if (!CommonUtils.isTrimmedAndEmpty(sourceCodeLocation)) {
			return sourceCodeLocation;
		}
		return otcHome + OTC_SRC_FOLDER;
	}

	public static Integer getCyclicReferenceDepth() {
		if (YAML_CONFIG.compiler.cyclicReferenceDepth != null && YAML_CONFIG.compiler.cyclicReferenceDepth > 0) {
			return YAML_CONFIG.compiler.cyclicReferenceDepth;
		}
		return DEFAULT_CYCLIC_REFERENCE_DEPTH;
	}

	/**
	 * Gets the otc tmd location.
	 *
	 * @return the otc tmd location
	 */
	public static String getOtcTmdLocation() {
		if (!CommonUtils.isTrimmedAndEmpty(tmdLocation)) {
			return tmdLocation;
		}
		return otcHome + OTC_TMD_FOLDER;
	}

	/**
	 * Gets the compiled code location.
	 *
	 * @return the compiled code location
	 */
	public static String getTargetLocation() {
		if (!CommonUtils.isTrimmedAndEmpty(targetLocation)) {
			return targetLocation;
		}
		return otcHome + OTC_TARGET_FOLDER;
	}

	/**
	 * Gets the test case expected result location.
	 *
	 * @return the test case expected result location
	 */
	public static String getTestCaseExpectedResultLocation() {
		String expectedLocation = otcHome + File.separator + "result_expected" + File.separator;
		OtcUtils.creteDirectory(expectedLocation);
		return expectedLocation;
	}

	/**
	 * Gets the compiler sourcecode failonerror.
	 *
	 * @return the compiler sourcecode failonerror
	 */
	public static boolean getCompilerSourcecodeFailonerror() {
		if (YAML_CONFIG.compiler.failFast != null) {
			return YAML_CONFIG.compiler.failFast;
		}
		return false;
	}

	/**
	 * Gets the target class loader.
	 *
	 * @return the target class loader
	 */
	public static URLClassLoader getTargetClassLoader() {
		return CLZ_LOADER;
	}

	/**
	 * Gets the concrete types.
	 *
	 * @return the concrete types
	 */
	public static Map<Class<?>, String> getConcreteTypes() {
		Map<String, String> yamlConcreteTypes = YAML_CONFIG.concreteTypes;
		if (yamlConcreteTypes != null) {
			IdentityHashMap<Class<?>, String> concreteTypes = new IdentityHashMap<>(yamlConcreteTypes.size());
			yamlConcreteTypes.forEach((key, value) -> {
				Class<?> clz = OtcUtils.loadClass(key);
				concreteTypes.put(clz, value);
			});
			return concreteTypes;
		}
		return null;
	}

	public static final class YamlConfig {
		public CompilerProps compiler;
		public Map<String, String> concreteTypes;
		public Set<String> filterPackages;
		
		public static final class CompilerProps {
			public Boolean failFast;
			public Boolean cleanupBeforeCompile;
			public Integer cyclicReferenceDepth;
			public Locations locations;

			public static final class Locations {
				public String libLocation;
				public String sourceCodeLocation;
				public String tmdLocation;
				public String targetLocation;
			}
		}
	}
}
