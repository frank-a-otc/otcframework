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
import java.util.Map;

import org.otcl2.common.OtclConstants;
import org.otcl2.common.config.OtclConfig;
import org.otcl2.common.dto.OtclCommandDto;
import org.otcl2.common.exception.OtclException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class OtclUtils.
 */
public class OtclUtils {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(OtclUtils.class);

	/** The clz loader. */
	private static URLClassLoader clzLoader;
	
	/** The Constant otclLibLocation. */
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
		// this method is written this way to enable a future features implementations 
		// to support index and map-keys.
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
	 * Retrieve leaf OCD.
	 *
	 * @param mapOCDs the map OC ds
	 * @param rawOtclChain the raw otcl chain
	 * @return the otcl command dto
	 */
	public static OtclCommandDto retrieveLeafOCD(Map<String, OtclCommandDto> mapOCDs, String rawOtclChain) {
		String[] rawOtclTokens = rawOtclChain.split("\\.");
		String otclChain = OtclUtils.sanitizeOtcl(rawOtclChain);
		String[] otclTokens = otclChain.split("\\.");

		OtclCommandDto otclCommandDto = retrieveNextOCD(mapOCDs, otclTokens[0]);
		for (int idx = 1; idx < otclTokens.length; idx++) {
			if (otclCommandDto.isCollectionOrMap()) {
				String memberName = otclCommandDto.fieldName;
				if (otclCommandDto.isMap()) {
					if (rawOtclTokens[otclCommandDto.otclTokenIndex].contains(OtclConstants.MAP_KEY_REF)) {
						memberName = OtclConstants.MAP_KEY_REF + memberName;
					} else {
						memberName = OtclConstants.MAP_VALUE_REF + memberName;
					}
				}
				otclCommandDto = otclCommandDto.children.get(memberName);
			}
//			otclCommandDto = otclCommandDto.children.get(otclTokens[otclCommandDto.otclTokenIndex + 1]);
			otclCommandDto = retrieveNextOCD(otclCommandDto.children, otclTokens[otclCommandDto.otclTokenIndex + 1]);
		}
		return otclCommandDto;
	}

	/**
	 * Retrieve next OCD.
	 *
	 * @param mapOCDs the map OC ds
	 * @param ocdKey the ocd key
	 * @return the otcl command dto
	 */
	private static OtclCommandDto retrieveNextOCD(Map<String, OtclCommandDto> mapOCDs, String ocdKey) {
		if (ocdKey.contains(OtclConstants.MAP_KEY_REF)) {
			ocdKey = ocdKey.replace(OtclConstants.MAP_KEY_REF, "");
		} else if (ocdKey.contains(OtclConstants.MAP_VALUE_REF)) {
			ocdKey = ocdKey.replace(OtclConstants.MAP_VALUE_REF, "");
		}
		OtclCommandDto otclCommandDto = mapOCDs.get(ocdKey);
		return otclCommandDto;
	}
	
	/**
	 * Creates the method not found message.
	 *
	 * @param clz the clz
	 * @param methodName the method name
	 * @param paramTypes the param types
	 * @param otclCommandDto the otcl command dto
	 * @return the string
	 */
	public static String createMethodNotFoundMessage(Class<?> clz, String methodName, Class<?>[] paramTypes, OtclCommandDto otclCommandDto) {
		StringBuilder paramsBuilder = null;
		if (paramTypes != null && paramTypes.length > 0) {
			for (Class<?> paramType : paramTypes) {
				if (paramsBuilder == null) {
					paramsBuilder = new StringBuilder("(").append(paramType.getName());
				} else {
					paramsBuilder.append(",").append(paramType.getName());
				}
			}
			paramsBuilder.append(")");
		} else {
			paramsBuilder = new StringBuilder("()");
		}
		String msg = "Method '" + clz.getName() + "." + methodName + paramsBuilder.toString() +
				" not found for tokenpath : " + otclCommandDto.tokenPath + "' - probable conflicts in command(s) " + 
				otclCommandDto.occursInCommands;
		return msg;
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
	
	/**
	 * Checks if is tokenpath leafparent.
	 *
	 * @param otclChain the otcl chain
	 * @param tokenPath the token path
	 * @return true, if is tokenpath leafparent
	 */
	public static boolean isTokenpathLeafparent(String otclChain, String tokenPath) {
		String remainderChain = otclChain.replace(tokenPath, "");
		if (!CommonUtils.isEmpty(remainderChain)) {
			if (remainderChain.startsWith(".")) {
				remainderChain = remainderChain.substring(1);
			}
			return remainderChain.split("\\.").length == 1;
		}
		return false;
	}
	
	/**
	 * Load URL class loader.
	 *
	 * @param path the path
	 * @return the URL class loader
	 */
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

	/**
	 * Creates the UR ls.
	 *
	 * @param directory the directory
	 * @param fileFilter the file filter
	 * @param urls the urls
	 * @return the list
	 */
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

	/**
	 * Fetch current URL class loader.
	 *
	 * @return the URL class loader
	 */
	public static URLClassLoader fetchCurrentURLClassLoader() {
		return clzLoader;
	}
}
