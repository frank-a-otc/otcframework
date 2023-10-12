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
import org.otcframework.common.util.CommonUtils;
import org.otcframework.common.util.OtcUtils;
import org.otcframework.common.util.PackagesFilterUtil;
import org.otcframework.common.util.YamlSerializationHelper;

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

	/** The Constant OTC_HOME_ENV_VAR. */
	private static final String OTC_HOME_ENV_VAR = "OTC_HOME";

	/** The Constant OTC_UNITTEST_FOLDER. */
	private static final String OTC_UNITTEST_FOLDER = "/otc-unittest/";
	private static final String OTC_LIB_FOLDER = "/lib/";
	private static final String OTC_SRC_FOLDER = "/src/";
	private static final String OTC_TMD_FOLDER = "/tmd/";
	private static final String OTC_TARGET_FOLDER = "/target/";
	private static final String OTC_CONFIG_FILE = "/config/otc.yaml";

	/** The compiler sourcecode failonerror. */
	private static boolean compilerSourcecodeFailonerror = false;

	/** The Constant otcHome. */
	private static String OTC_HOME;

	/** The Constant yamlConfig. */
	private static final YamlConfig yamlConfig;

	/** The Constant clzLoader. */
	private static final URLClassLoader clzLoader;
	
	/** The Constant sourceCodeLocation. */
	private static String sourceCodeLocation;

	private static final Integer DEFAULT_CYCLIC_DEPENDENCY_DEPTH = 2;

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
		OTC_HOME = sysEnv.get(OTC_HOME_ENV_VAR);
		if (CommonUtils.isEmpty(OTC_HOME)) {
			throw new OtcException("", "Oops... Environment variable '" + OTC_HOME_ENV_VAR + "' not set! ");
		}
		if (!OTC_HOME.endsWith(File.separator)) {
			OTC_HOME += File.separator;
		}
		try {
			yamlConfig = YamlSerializationHelper.deserialize(OTC_HOME + OTC_CONFIG_FILE, YamlConfig.class);
		} catch (Exception ex) {
			throw new OtcConfigException(ex);
		}
		try {
			String targetDir = getCompiledCodeLocation();
			File targetFolder = new File(targetDir);
			if (!targetFolder.exists()) {
				targetFolder.mkdir();
			}
			URL url = new File(targetDir).toURI().toURL();
			URL[] urls = new URL[] { url };
			clzLoader = URLClassLoader.newInstance(urls);
		} catch (MalformedURLException e) {
			throw new OtcConfigException(e);
		}
		if (yamlConfig.compiler != null) {
			OtcConfig.compilerSourcecodeFailonerror = yamlConfig.compiler.failOnError;
		}
		Set<String> filteredPackages = yamlConfig.filterPackages;
		PackagesFilterUtil.setFilteredPackages(filteredPackages);

		if (yamlConfig.compiler != null) {
			sourceCodeLocation = yamlConfig.compiler.sourceCodeLocation;
		}
		if (CommonUtils.isEmpty(sourceCodeLocation)) {
			sourceCodeLocation = OTC_HOME + OTC_SRC_FOLDER;
		} else if (!sourceCodeLocation.endsWith(File.separator)) {
			sourceCodeLocation += File.separator;
		}
	}

	/**
	 * Gets the otc home location.
	 *
	 * @return the otc home location
	 */
	public static String getOtcHomeLocation() {
		return OTC_HOME;
	}

	/**
	 * Gets the otc lib location.
	 *
	 * @return the otc lib location
	 */
	public static String getOtcLibLocation() {
		return OTC_HOME + OTC_LIB_FOLDER;
	}

	/**
	 * Gets the otc source location.
	 *
	 * @return the otc source location
	 */
	public static String getOtcSourceLocation() {
		return OTC_HOME + OTC_UNITTEST_FOLDER;
	}

	/**
	 * Gets the source code location.
	 *
	 * @return the source code location
	 */
	public static String getSourceCodeLocation() {
		return sourceCodeLocation;
	}

	public static Integer getCyclicDependencyDepth() {
		if (yamlConfig.compiler.cyclicDependencyDepth != null && yamlConfig.compiler.cyclicDependencyDepth > 0) {
			return yamlConfig.compiler.cyclicDependencyDepth;
		}
		return DEFAULT_CYCLIC_DEPENDENCY_DEPTH;
	}

	/**
	 * Gets the otc tmd location.
	 *
	 * @return the otc tmd location
	 */
	public static String getOtcTmdLocation() {
		return OTC_HOME + OTC_TMD_FOLDER;
	}

	/**
	 * Gets the compiled code location.
	 *
	 * @return the compiled code location
	 */
	public static String getCompiledCodeLocation() {
		return OTC_HOME + OTC_TARGET_FOLDER;
	}

	/**
	 * Gets the test case expected result location.
	 *
	 * @return the test case expected result location
	 */
	public static String getTestCaseExpectedResultLocation() {
		String expectedLocation = OTC_HOME + File.separator + "result_expected" + File.separator;
		File file = new File(expectedLocation);
		if (!file.exists()) {
			file.mkdir();
		}
		return expectedLocation;
	}

	/**
	 * Gets the compiler sourcecode failonerror.
	 *
	 * @return the compiler sourcecode failonerror
	 */
	public static boolean getCompilerSourcecodeFailonerror() {
		return compilerSourcecodeFailonerror;
	}

	/**
	 * Gets the target class loader.
	 *
	 * @return the target class loader
	 */
	public static URLClassLoader getTargetClassLoader() {
		return clzLoader;
	}

	/**
	 * Gets the concrete types.
	 *
	 * @return the concrete types
	 */
	public static Map<Class<?>, String> getConcreteTypes() {
		Map<String, String> yamlConcreteTypes = yamlConfig.concreteTypes;
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
			public Boolean failOnError;
			public String sourceCodeLocation;
			public Integer cyclicDependencyDepth;
		}

	}
}
