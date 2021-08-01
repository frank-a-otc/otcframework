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
package org.otcframework.common.util;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.otcframework.common.OtcConstants;
import org.otcframework.common.config.OtcConfig;
import org.otcframework.common.dto.OtcCommandDto;
import org.otcframework.common.exception.OtcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class OtcUtils.
 */
public class OtcUtils {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(OtcUtils.class);

	/** The clz loader. */
	private static URLClassLoader clzLoader;
	
	/** The Constant otcLibLocation. */
	private static final String otcLibLocation = OtcConfig.getOtcLibLocation();

	/**
	 * Creates the otc file name.
	 *
	 * @param sourceClz the source clz
	 * @param targetClz the target clz
	 * @return the string
	 */
	public static String createOtcFileName(String sourceClz, String targetClz) {
		if (CommonUtils.isEmpty(targetClz)) {
			throw new OtcException("", "Target-class cannot be null.");
		}
		String fileName = createDeploymentId(null, sourceClz, targetClz) + OtcConstants.OTC_SCRIPT_EXTN;
		return fileName;
	}

	/**
	 * Creates the deployment id.
	 *
	 * @param otcNamespace the otc namespace
	 * @param source the source
	 * @param targetClz the target clz
	 * @return the string
	 */
	public static String createDeploymentId(String otcNamespace, Object source, Class<?> targetClz) {
		if (source == null) {
			return createDeploymentId(otcNamespace, null, targetClz.getName());
		} else {
			return createDeploymentId(otcNamespace, source.getClass().getName(), targetClz.getName());
		}
	}
	
	/**
	 * Creates the deployment id.
	 *
	 * @param otcNamespace the otc namespace
	 * @param sourceClz the source clz
	 * @param targetClz the target clz
	 * @return the string
	 */
	public static String createDeploymentId(String otcNamespace, Class<?> sourceClz, Class<?> targetClz) {
		return createDeploymentId(otcNamespace, sourceClz.getName(), targetClz.getName());
	}
	
	/**
	 * Creates the deployment id.
	 *
	 * @param otcNamespace the otc namespace
	 * @param sourceClz the source clz
	 * @param targetClz the target clz
	 * @return the string
	 */
	public static String createDeploymentId(String otcNamespace, String sourceClz, String targetClz) {
		String deploymentId = null;
		if (sourceClz == null) {
			deploymentId = targetClz;
		} else {
			deploymentId = sourceClz + "_" + targetClz;
		}
		if (!CommonUtils.isEmpty(otcNamespace)) {
			deploymentId = otcNamespace + "." + deploymentId;
		}
		return deploymentId;
	}
	
	/**
	 * Sanitize otc.
	 *
	 * @param otcChain the otc chain
	 * @return the string
	 */
	public static String sanitizeOtc(String otcChain) {
		// this method is written this way to enable a future features implementations 
		// to support index and map-keys.
		if (otcChain.contains(OtcConstants.ANCHOR)) {
			otcChain = otcChain.replace(OtcConstants.ANCHOR, "");
		}
		int idxEnd = 0;
		while (true) {
			int idx = otcChain.indexOf(OtcConstants.MAP_BEGIN_REF, idxEnd);
			if (idx < 0) {
				idx = otcChain.indexOf(OtcConstants.MAP_PRE_ANCHOR, idxEnd);
			}
			if (idx > 0) {
				idxEnd = otcChain.indexOf(OtcConstants.CLOSE_BRACKET, idx) + 1;
				otcChain = otcChain.replace(otcChain.substring(idx, idxEnd), OtcConstants.MAP_REF);
			} else {
				idx = otcChain.indexOf(OtcConstants.OPEN_BRACKET, idxEnd);
				if (idx > 0) {
					idxEnd = otcChain.indexOf(OtcConstants.CLOSE_BRACKET, idx) + 1;
					otcChain = otcChain.replace(otcChain.substring(idx, idxEnd), OtcConstants.ARR_REF);
				}
			}
			idx = otcChain.indexOf(OtcConstants.OPEN_BRACKET, idxEnd);
			if (idx < 0) {
				break;
			}
		}
		return otcChain;
	}

	/**
	 * Retrieve leaf OCD.
	 *
	 * @param mapOCDs the map OC ds
	 * @param rawOtcChain the raw otc chain
	 * @return the otc command dto
	 */
	public static OtcCommandDto retrieveLeafOCD(Map<String, OtcCommandDto> mapOCDs, String rawOtcChain) {
		String[] rawOtcTokens = rawOtcChain.split("\\.");
		String otcChain = OtcUtils.sanitizeOtc(rawOtcChain);
		String[] otcTokens = otcChain.split("\\.");

		OtcCommandDto otcCommandDto = retrieveNextOCD(mapOCDs, otcTokens[0]);
		for (int idx = 1; idx < otcTokens.length; idx++) {
			if (otcCommandDto.isCollectionOrMap()) {
				String memberName = otcCommandDto.fieldName;
				if (otcCommandDto.isMap()) {
					if (rawOtcTokens[otcCommandDto.otcTokenIndex].contains(OtcConstants.MAP_KEY_REF)) {
						memberName = OtcConstants.MAP_KEY_REF + memberName;
					} else {
						memberName = OtcConstants.MAP_VALUE_REF + memberName;
					}
				}
				otcCommandDto = otcCommandDto.children.get(memberName);
			}
//			otcCommandDto = otcCommandDto.children.get(otcTokens[otcCommandDto.otcTokenIndex + 1]);
			otcCommandDto = retrieveNextOCD(otcCommandDto.children, otcTokens[otcCommandDto.otcTokenIndex + 1]);
		}
		return otcCommandDto;
	}

	/**
	 * Retrieve next OCD.
	 *
	 * @param mapOCDs the map OC ds
	 * @param ocdKey the ocd key
	 * @return the otc command dto
	 */
	private static OtcCommandDto retrieveNextOCD(Map<String, OtcCommandDto> mapOCDs, String ocdKey) {
		if (ocdKey.contains(OtcConstants.MAP_KEY_REF)) {
			ocdKey = ocdKey.replace(OtcConstants.MAP_KEY_REF, "");
		} else if (ocdKey.contains(OtcConstants.MAP_VALUE_REF)) {
			ocdKey = ocdKey.replace(OtcConstants.MAP_VALUE_REF, "");
		}
		OtcCommandDto otcCommandDto = mapOCDs.get(ocdKey);
		return otcCommandDto;
	}
	
	/**
	 * Creates the method not found message.
	 *
	 * @param clz the clz
	 * @param methodName the method name
	 * @param paramTypes the param types
	 * @param otcCommandDto the otc command dto
	 * @return the string
	 */
	public static String createMethodNotFoundMessage(Class<?> clz, String methodName, Class<?>[] paramTypes, OtcCommandDto otcCommandDto) {
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
				" not found for tokenpath : " + otcCommandDto.tokenPath + "' - probable conflicts in command(s) " + 
				otcCommandDto.occursInCommands;
		return msg;
	}

	/**
	 * Retrieve index character.
	 *
	 * @param otcToken the otc token
	 * @return the string
	 */
	public static String retrieveIndexCharacter(String otcToken) {
		int idxCollectionNotation = otcToken.indexOf(OtcConstants.OPEN_BRACKET) + 1;
		int idxEndCollectionNotation = otcToken.indexOf(OtcConstants.CLOSE_BRACKET);
		String idxCharacter = otcToken.substring(idxCollectionNotation, idxEndCollectionNotation);
		return idxCharacter;
	}
	
	/**
	 * Checks if is tokenpath leafparent.
	 *
	 * @param otcChain the otc chain
	 * @param tokenPath the token path
	 * @return true, if is tokenpath leafparent
	 */
	public static boolean isTokenpathLeafparent(String otcChain, String tokenPath) {
		String remainderChain = otcChain.replace(tokenPath, "");
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
		File otcBinDirectory = new File(path);
		List<URL> urls = createURLs(otcBinDirectory, CommonUtils.createFilenameFilter(".jar"), null);
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
			clzLoader = loadURLClassLoader(otcLibLocation);
		}
		if (clzLoader == null || clzName == null) {
			throw new OtcException("", "Invalid value : null!");
		}
		Class<?> cls = null;
		try {
			cls = clzLoader.loadClass(clzName);
		} catch (Exception e) {
			throw new OtcException("", e);
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
