/**
* Copyright (c) otcl2.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
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

import org.otcl2.common.OtclConstants;
import org.otcl2.common.config.exception.OtclConfigException;
import org.otcl2.common.exception.OtclException;
import org.otcl2.common.util.CommonUtils;
import org.otcl2.common.util.PackagesFilterUtil;
import org.otcl2.common.util.PropertyConverterUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum OtclConfig {
	instance;

	private static final Logger LOGGER = LoggerFactory.getLogger(OtclConfig.class);

	private static final String OTCL_HOME_ENV_VAR = "OTCL_HOME";
	private static final String COMPILER_CODEGEN_SOURCE_BASEDIR = "compiler.codegen.source.basedir";
	private static final String COMPILER_TESTPROFILE_ENABLED = "compiler.testprofile.enable";

	private static final String EXECUTOR_PACKAGES_FILTER = "executor.packages.filter";

	private static final boolean engineLogingDetailedDefault = true;
	private static final boolean compilerTestprofileEnableDefault = false;

	private static final String otclHome;
	private static final Properties otclConfigProps = new Properties();
	private static boolean isTestProfile = false;
	private static final URLClassLoader clzLoader;

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
			LOGGER.error(ex.getMessage());
			throw new OtclConfigException(ex);
		}
		String filteredPackages = otclConfigProps.getProperty(EXECUTOR_PACKAGES_FILTER);
		if (!filteredPackages.contains(",") && filteredPackages.contains(" ")) {
			filteredPackages = filteredPackages.replace("  ", " ").replace(" ", ",");
		}
		List<String> lstFilteredPackages = Arrays.asList(filteredPackages.split(","));
		PackagesFilterUtil.setFilteredPackages(lstFilteredPackages);
		URL url;
		try {
			String targetDir = getOtclTargetLocation();
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
		isTestProfile = getConfigCompilerTestProfileEnabled();
	}

	public static void enableTestProfile() {
		isTestProfile = true;
	}

	public static void disableTestProfile() {
		isTestProfile = false;
	}

	public static String getOtclHomeLocation() {
		return otclHome;
	}

	public static String getOtclLibLocation() {
		if (CommonUtils.isEmpty(otclHome)) {
			throw new OtclException("", "Oops... Environment variable 'otcl.home' not set! ");
		}
		return otclHome + File.separator + "lib" + File.separator;
	}

	public static String getOtclSourceLocation() {
		if (CommonUtils.isEmpty(otclHome)) {
			throw new OtclException("", "Oops... Environment variable 'otcl.home' not set! ");
		}
		if (isTestProfile) {
			return otclHome + OtclConstants.OTCL_TEST_SOURCE;
		} else {
			return otclHome + OtclConstants.OTCL_SOURCE;
		}
	}

	public static String getGeneratedCodeSourceLocation() {
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

	public static String getOtclBinLocation() {
		if (CommonUtils.isEmpty(otclHome)) {
			throw new OtclException("", "Oops... Environment variable 'otcl.home' not set! ");
		}
		return otclHome + File.separator + "bin" + File.separator;
	}

	public static String getOtclTargetLocation() {
		if (CommonUtils.isEmpty(otclHome)) {
			throw new OtclException("", "Oops... Environment variable 'otcl.home' not set! ");
		}
		return otclHome + File.separator + "target" + File.separator;
	}

	public static URLClassLoader getTargetClassLoader() {
		return clzLoader;
	}

	public static Boolean getConfigCompilerTestProfileEnabled() {
		if (otclConfigProps.containsKey(COMPILER_TESTPROFILE_ENABLED)) {
			return PropertyConverterUtil.toBooleanObject(otclConfigProps.getProperty(COMPILER_TESTPROFILE_ENABLED));
		}
		return compilerTestprofileEnableDefault;
	}
}
