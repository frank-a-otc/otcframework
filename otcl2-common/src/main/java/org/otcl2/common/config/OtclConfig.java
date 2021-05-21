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
package org.otcl2.common.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.otcl2.common.config.exception.OtclConfigException;
import org.otcl2.common.exception.OtclException;
import org.otcl2.common.util.CommonUtils;
import org.otcl2.common.util.PackagesFilterUtil;
import org.otcl2.common.util.PropertyConverterUtil;

// TODO: Auto-generated Javadoc
/**
 * The Enum OtclConfig.
 */
public enum OtclConfig {
	
	/** The instance. */
	instance;

	/** The Constant OTCL_HOME_ENV_VAR. */
	private static final String OTCL_HOME_ENV_VAR = "OTCL_HOME";

	/** The otcl test source. */
	private static final String OTCL_UNITTEST_FOLDER = "/otcl-unittest";
	
	/** The Constant COMPILER_CODEGEN_SOURCE_BASEDIR. */
	private static final String COMPILER_CODEGEN_SOURCE_BASEDIR = "compiler.codegen.source.basedir";

	/** The Constant EXECUTOR_PACKAGES_FILTER. */
	private static final String EXECUTOR_PACKAGES_FILTER = "executor.packages.filter";

	/** The Constant COMPILER_SOURCECODE_FAILONERROR. */
	private static final String COMPILER_SOURCECODE_FAILONERROR = "compiler.sourcecode.failonerror";
	
	/** The Constant compilerTestprofileEnableDefault. */
	private static boolean compilerSourcecodeFailonerror = false;

	/** The Constant otclHome. */
	private static final String otclHome;
	
	/** The Constant otclConfigProps. */
	private static final Properties otclConfigProps = new Properties();

	/** The Constant clzLoader. */
	private static final URLClassLoader clzLoader;

	/**
	 * Instantiates a new otcl config.
	 */
	private OtclConfig() {
	}

	static {
		Map<String, String> sysEnv = System.getenv();
		if (!sysEnv.containsKey(OTCL_HOME_ENV_VAR)) {
			throw new OtclConfigException("",
					"Oops... Cannot proceed - 'otcl_home' not set! Please set otcl_home environment variable.");
		}
		otclHome = sysEnv.get(OTCL_HOME_ENV_VAR);
		if (CommonUtils.isEmpty(otclHome)) {
			throw new OtclException("", "Oops... Environment variable 'otcl.home' not set! ");
		}
		try (InputStream inStream = new FileInputStream(otclHome + "/config/otcl.properties")) {
			otclConfigProps.load(inStream);
			if (!otclConfigProps.containsKey(EXECUTOR_PACKAGES_FILTER)) {
				throw new OtclConfigException("", "Oops... Cannot proceed - 'otcl.pkgsToInclude' not set in '"
						+ otclHome + "/config/otcl.properties' file");
			}
		} catch (IOException ex) {
			throw new OtclConfigException(ex);
		}
		String filteredPackages = otclConfigProps.getProperty(EXECUTOR_PACKAGES_FILTER);
		String compilerSourcecodeFailonerror = otclConfigProps.getProperty(COMPILER_SOURCECODE_FAILONERROR);
		try {
			OtclConfig.compilerSourcecodeFailonerror = PropertyConverterUtil.toBooleanObject(compilerSourcecodeFailonerror);
		} catch (Exception ex) {
			
		}
		if (!filteredPackages.contains(",") && filteredPackages.contains(" ")) {
			filteredPackages = filteredPackages.replace("  ", " ").replace(" ", ",");
		}
		List<String> lstFilteredPackages = Arrays.asList(filteredPackages.split(","));
		PackagesFilterUtil.setFilteredPackages(lstFilteredPackages);
		URL url;
		try {
			String targetDir = getCompiledCodeLocation();
			File binFolder = new File(targetDir);
			if (!binFolder.exists()) {
				binFolder.mkdir();
			}
			url = new File(targetDir).toURI().toURL();
			URL[] urls = new URL[] { url };
			clzLoader = URLClassLoader.newInstance(urls);
		} catch (MalformedURLException e) {
			throw new OtclConfigException(e);
		}
	}

	/**
	 * Gets the otcl home location.
	 *
	 * @return the otcl home location
	 */
	public static String getOtclHomeLocation() {
		return otclHome;
	}

	/**
	 * Gets the otcl lib location.
	 *
	 * @return the otcl lib location
	 */
	public static String getOtclLibLocation() {
		checkOtclHomeSet();
		return otclHome + File.separator + "lib" + File.separator;
	}

	/**
	 * Gets the otcl source location.
	 *
	 * @return the otcl source location
	 */
	public static String getOtclSourceLocation() {
		checkOtclHomeSet();
		return otclHome + OTCL_UNITTEST_FOLDER;
	}

	/**
	 * Gets the generated code source location.
	 *
	 * @return the generated code source location
	 */
	public static String getSourceCodeLocation() {
		String sourceCodeLocation = null;
		if (otclConfigProps.containsKey(COMPILER_CODEGEN_SOURCE_BASEDIR)) {
			sourceCodeLocation = otclConfigProps.getProperty(COMPILER_CODEGEN_SOURCE_BASEDIR);
			if (!sourceCodeLocation.endsWith(File.separator)) {
				sourceCodeLocation += File.separator;
			}
		}
		if (CommonUtils.isEmpty(sourceCodeLocation)) {
			sourceCodeLocation = otclHome + File.separator + "src" + File.separator;
		}
		return sourceCodeLocation;
	}

	/**
	 * Gets the otcl bin location.
	 *
	 * @return the otcl bin location
	 */
	public static String getOtclBinLocation() {
		checkOtclHomeSet();
		return otclHome + File.separator + "bin" + File.separator;
	}

	/**
	 * Gets the otcl target location.
	 *
	 * @return the otcl target location
	 */
	public static String getCompiledCodeLocation() {
		checkOtclHomeSet();
		return otclHome + File.separator + "target" + File.separator;
	}

	/**
	 * Gets the expected result location.
	 *
	 * @return the expected result location
	 */
	public static String getTestCaseExpectedResultLocation() {
		checkOtclHomeSet();
		String expectedLocation = otclHome + File.separator + "tc_expected" + File.separator;
		File file = new File(expectedLocation);
		if (!file.exists()) {
			file.mkdir();
		}
		return expectedLocation;
	}
	
	public static String getTestCaseActualResultLocation() {
		String expectedLocation = otclHome + File.separator + "tc_actual" + File.separator;
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
	
	private static void checkOtclHomeSet() {
		if (CommonUtils.isEmpty(otclHome)) {
			throw new OtclException("", "Oops... Environment variable 'otcl.home' not set! ");
		}
	}

}
