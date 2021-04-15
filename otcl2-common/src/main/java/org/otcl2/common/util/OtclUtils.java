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
package org.otcl2.common.util;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.otcl2.common.OtclConstants;
import org.otcl2.common.config.OtclConfig;
import org.otcl2.common.exception.OtclException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class OtclUtils.
 */
public class OtclUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(OtclUtils.class);

	private static URLClassLoader clzLoader;
	private static final String otclLibLocation = OtclConfig.getOtclLibLocation();

	/**
	 * Creates the otcl file name.
	 *
	 * @param sourceClz the source clz
	 * @param targetClz the target clz
	 * @return the string
	 */
	public static String createOtclFileName(String sourceClz, String targetClz) {
		if (CommonUtils.isEmpty(targetClz)) {
			throw new OtclException("", "Target-class cannot be null.");
		}
		String fileName = createDeploymentId(null, sourceClz, targetClz) + OtclConstants.OTCL_SCRIPT_EXTN;
		return fileName;
	}

	/**
	 * Creates the deployment id.
	 *
	 * @param otclNamespace the otcl namespace
	 * @param source the source
	 * @param targetClz the target clz
	 * @return the string
	 */
	public static String createDeploymentId(String otclNamespace, Object source, Class<?> targetClz) {
		if (source == null) {
			return createDeploymentId(otclNamespace, null, targetClz.getName());
		} else {
			return createDeploymentId(otclNamespace, source.getClass().getName(), targetClz.getName());
		}
	}
	
	/**
	 * Creates the deployment id.
	 *
	 * @param otclNamespace the otcl namespace
	 * @param sourceClz the source clz
	 * @param targetClz the target clz
	 * @return the string
	 */
	public static String createDeploymentId(String otclNamespace, Class<?> sourceClz, Class<?> targetClz) {
		return createDeploymentId(otclNamespace, sourceClz.getName(), targetClz.getName());
	}
	
	/**
	 * Creates the deployment id.
	 *
	 * @param otclNamespace the otcl namespace
	 * @param sourceClz the source clz
	 * @param targetClz the target clz
	 * @return the string
	 */
	public static String createDeploymentId(String otclNamespace, String sourceClz, String targetClz) {
		String deploymentId = null;
		if (sourceClz == null) {
			deploymentId = targetClz;
		} else {
			deploymentId = sourceClz + "_" + targetClz;
		}
		if (!CommonUtils.isEmpty(otclNamespace)) {
			deploymentId = otclNamespace + "." + deploymentId;
		}
		return deploymentId;
	}
	
	/**
	 * Sanitize otcl.
	 *
	 * @param otclChain the otcl chain
	 * @return the string
	 */
	public static String sanitizeOtcl(String otclChain) {
		if (otclChain.contains(OtclConstants.ANCHOR)) {
			otclChain = otclChain.replace(OtclConstants.ANCHOR, "");
		}
		int idxEnd = 0;
		while (true) {
			int idx = otclChain.indexOf(OtclConstants.MAP_BEGIN_REF, idxEnd);
			if (idx < 0) {
				idx = otclChain.indexOf(OtclConstants.MAP_PRE_ANCHOR, idxEnd);
			}
			if (idx > 0) {
				idxEnd = otclChain.indexOf(OtclConstants.CLOSE_BRACKET, idx) + 1;
				otclChain = otclChain.replace(otclChain.substring(idx, idxEnd), OtclConstants.MAP_REF);
			} else {
				idx = otclChain.indexOf(OtclConstants.OPEN_BRACKET, idxEnd);
				if (idx > 0) {
					idxEnd = otclChain.indexOf(OtclConstants.CLOSE_BRACKET, idx) + 1;
					otclChain = otclChain.replace(otclChain.substring(idx, idxEnd), OtclConstants.ARR_REF);
				}
			}
			idx = otclChain.indexOf(OtclConstants.OPEN_BRACKET, idxEnd);
			if (idx < 0) {
				break;
			}
		}
		return otclChain;
	}

	/**
	 * Retrieve index character.
	 *
	 * @param otclToken the otcl token
	 * @return the string
	 */
	public static String retrieveIndexCharacter(String otclToken) {
		int idxCollectionNotation = otclToken.indexOf(OtclConstants.OPEN_BRACKET) + 1;
		int idxEndCollectionNotation = otclToken.indexOf(OtclConstants.CLOSE_BRACKET);
		String idxCharacter = otclToken.substring(idxCollectionNotation, idxEndCollectionNotation);
		return idxCharacter;
	}
	
	public static URLClassLoader loadURLClassLoader(String path) {
		File otclBinDirectory = new File(path);
		List<URL> urls = createURLs(otclBinDirectory, CommonUtils.createFilenameFilter(".jar"), null);
		if (urls == null || urls.isEmpty()) {
			return null;
		}
		URLClassLoader clzLoader = new URLClassLoader(urls.toArray(new URL[urls.size()]), 
				ClassLoader.getSystemClassLoader()); 
		return clzLoader;
	}

	public static List<URL> createURLs(File directory, FileFilter fileFilter, List<URL> urls) {
		for (File file : directory.listFiles(fileFilter)) {
			if (file.isDirectory()) {
				if (urls == null) {
					urls = createURLs(file, fileFilter, urls); 
				} else {
					urls.addAll(createURLs(file, fileFilter, urls));
				}
			} else {
				try {
					URL url = null;
					if (file.getName().endsWith(".jar")) {
						url = new URL("jar:file:" + file.getAbsolutePath() +"!/");
					} else {
						url = new URL("file:" + file.getAbsolutePath());
					}
					if (urls == null) {
						urls = new ArrayList<>();
					}
					urls.add(url);
				} catch (MalformedURLException e) {
					LOGGER.warn(e.getMessage());
				}
			}
		}
		return urls;
	}

	/**
	 * Load class.
	 *
	 * @param clzName the clz name
	 * @return the class
	 */
	public static Class<?> loadClass(String clzName) {
		if (clzLoader == null) {
			clzLoader = loadURLClassLoader(otclLibLocation);
		}
		if (clzLoader == null || clzName == null) {
			throw new OtclException("", "Invalid value : null!");
		}
		Class<?> cls = null;
		try {
			cls = clzLoader.loadClass(clzName);
		} catch (Exception e) {
			throw new OtclException("", e);
		}
		return cls;
	}

	public static URLClassLoader fetchCurrentURLClassLoader() {
		return clzLoader;
	}
}
