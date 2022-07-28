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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.Set;

import org.otcframework.common.config.exception.OtcConfigException;
import org.otcframework.common.exception.OtcException;
import org.otcframework.common.util.CommonUtils;
import org.otcframework.common.util.PackagesFilterUtil;
import org.otcframework.common.util.YamlSerializationHelper;

/**
 * The Enum OtcConfig.
 */
// TODO: Auto-generated Javadoc
public enum OtcConfig {

	/** The instance. */
	instance;

	/** The Constant OTC_HOME_ENV_VAR. */
	private static final String OTC_HOME_ENV_VAR = "OTC_HOME";

	/** The Constant OTC_UNITTEST_FOLDER. */
	private static final String OTC_UNITTEST_FOLDER = "/otc-unittest";

	/** The Constant COMPILER_CODEGEN_SOURCE_BASEDIR. */
	private static final String COMPILER_CODEGEN_SOURCE_BASEDIR = "compiler.codegen.source.basedir";

	/** The Constant EXECUTOR_PACKAGES_FILTER. */
	private static final String EXECUTOR_PACKAGES_FILTER = "executor.packages.filter";

	/** The Constant COMPILER_SOURCECODE_FAILONERROR. */
	private static final String COMPILER_SOURCECODE_FAILONERROR = "compiler.sourcecode.failonerror";

	/** The compiler sourcecode failonerror. */
	private static boolean compilerSourcecodeFailonerror = false;

	/** The Constant otcHome. */
	private static final String otcHome;

	/** The Constant otcConfigProps. */
//	private static final Properties otcConfigProps = new Properties();

	/** The Constant yamlConfig. */
	private static final YamlConfig yamlConfig;

	/** The Constant clzLoader. */
	private static final URLClassLoader clzLoader;
	
	/** The Constant sourceCodeLocation. */
	private static String sourceCodeLocation;


	/**
	 * Instantiates a new otc config.
	 */
	private OtcConfig() {
	}

	static {
		Map<String, String> sysEnv = System.getenv();
		if (!sysEnv.containsKey(OTC_HOME_ENV_VAR)) {
			throw new OtcConfigException("",
					"Oops... Cannot proceed - 'otc_home' not set! Please set 'otc_home' environment variable.");
		}
		otcHome = sysEnv.get(OTC_HOME_ENV_VAR);
		if (CommonUtils.isEmpty(otcHome)) {
			throw new OtcException("", "Oops... Environment variable 'otc.home' not set! ");
		}
//		try (InputStream inStream = new FileInputStream(otcHome + "/config/otc.properties")) {
//			otcConfigProps.load(inStream);
//			if (!otcConfigProps.containsKey(EXECUTOR_PACKAGES_FILTER)) {
//				throw new OtcConfigException("", "Oops... Cannot proceed - 'otc.pkgsToInclude' not set in '" + otcHome
//						+ "/config/otc.properties' file");
//			}
//		} catch (IOException ex) {
//			throw new OtcConfigException(ex);
//		}
		try {
			yamlConfig = YamlSerializationHelper.deserialize(otcHome + "/config/otc.yaml", YamlConfig.class);
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
//		String filteredPackages = otcConfigProps.getProperty(EXECUTOR_PACKAGES_FILTER);
//		String compilerSourcecodeFailonerror = otcConfigProps.getProperty(COMPILER_SOURCECODE_FAILONERROR);
		if (yamlConfig.compiler != null) {
			OtcConfig.compilerSourcecodeFailonerror = yamlConfig.compiler.failOnError;
		}
//		try {
//			OtcConfig.compilerSourcecodeFailonerror = PropertyConverterUtil
//					.toBooleanObject(compilerSourcecodeFailonerror);
//		} catch (Exception ex) {
//		}
//		if (!filteredPackages.contains(",") && filteredPackages.contains(" ")) {
//			filteredPackages = filteredPackages.replace("  ", " ").replace(" ", ",");
//		}
//		List<String> lstFilteredPackages = Arrays.asList(filteredPackages.split(","));
		Set<String> filteredPackages = yamlConfig.filterPackages;
		PackagesFilterUtil.setFilteredPackages(filteredPackages);

		if (yamlConfig.compiler != null) {
			sourceCodeLocation = yamlConfig.compiler.sourceCodeLocation;
			if (!sourceCodeLocation.endsWith(File.separator)) {
				sourceCodeLocation += File.separator;
			}
		}
		if (CommonUtils.isEmpty(sourceCodeLocation)) {
			sourceCodeLocation = otcHome + File.separator + "src" + File.separator;
		}
	}

	/**
	 * Gets the otc home location.
	 *
	 * @return the otc home location
	 */
	public static String getOtcHomeLocation() {
		return otcHome;
	}

	/**
	 * Gets the otc lib location.
	 *
	 * @return the otc lib location
	 */
	public static String getOtcLibLocation() {
		isOtcHomeSet();
		return otcHome + File.separator + "lib" + File.separator;
	}

	/**
	 * Gets the otc source location.
	 *
	 * @return the otc source location
	 */
	public static String getOtcSourceLocation() {
		isOtcHomeSet();
		return otcHome + OTC_UNITTEST_FOLDER;
	}

	/**
	 * Gets the source code location.
	 *
	 * @return the source code location
	 */
	public static String getSourceCodeLocation() {
		return sourceCodeLocation;
	}


	/**
	 * Gets the otc tmd location.
	 *
	 * @return the otc tmd location
	 */
	public static String getOtcTmdLocation() {
		isOtcHomeSet();
		return otcHome + File.separator + "tmd" + File.separator;
	}

	/**
	 * Gets the compiled code location.
	 *
	 * @return the compiled code location
	 */
	public static String getCompiledCodeLocation() {
		isOtcHomeSet();
		return otcHome + File.separator + "target" + File.separator;
	}

	/**
	 * Gets the test case expected result location.
	 *
	 * @return the test case expected result location
	 */
	public static String getTestCaseExpectedResultLocation() {
		isOtcHomeSet();
		String expectedLocation = otcHome + File.separator + "result_expected" + File.separator;
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
	 * Check otc home set.
	 */
	private static boolean isOtcHomeSet() {
		if (CommonUtils.isEmpty(otcHome)) {
			throw new OtcException("", "Oops... Environment variable 'otc.home' not set! ");
		}
		return true;
	}
	
	public static final class YamlConfig {
		public Compiler compiler;
		public Map<String, String> concreteTypes;
		public Set<String> filterPackages;
		
		public static final class Compiler {
			public Boolean failOnError;
			public String sourceCodeLocation;
		}

	}
}
