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
package org.otcframework.common.util;

import org.otcframework.common.OtcConstants;
import org.otcframework.common.config.OtcConfig;
import org.otcframework.common.dto.OtcCommandDto;
import org.otcframework.common.exception.OtcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The Class OtcUtils.
 */
// TODO: Auto-generated Javadoc
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
		String fileName = createRegistryId(null, sourceClz, targetClz) + OtcConstants.OTC_SCRIPT_EXTN;
		return fileName;
	}
	
	/**
	 * Creates the registry id.
	 *
	 * @param otcNamespace the otc namespace
	 * @param source       the source
	 * @param targetClz    the target clz
	 * @return the string
	 */
	public static String createRegistryId(String otcNamespace, Object source, Class<?> targetClz) {
		if (source == null) {
			return createRegistryId(otcNamespace, null, targetClz.getName());
		} else {
			return createRegistryId(otcNamespace, source.getClass().getName(), targetClz.getName());
		}
	}

	/**
	 * Creates the registry id.
	 *
	 * @param otcNamespace the otc namespace
	 * @param targetClz the target clz
	 * @return the string
	 */
	public static String createRegistryId(String otcNamespace, Class<?> targetClz) {
		return createRegistryId(otcNamespace, null, targetClz.getName());
	}

	/**
	 * Creates the registry id.
	 *
	 * @param otcNamespace the otc namespace
	 * @param sourceClz    the source clz
	 * @param targetClz    the target clz
	 * @return the string
	 */
	public static String createRegistryId(String otcNamespace, Class<?> sourceClz, Class<?> targetClz) {
		if (sourceClz == null) {
			return createRegistryId(otcNamespace, null, targetClz.getName());
		} else {
			return createRegistryId(otcNamespace, sourceClz.getName(), targetClz.getName());			
		}
	}

	/**
	 * Creates the registry id.
	 *
	 * @param otcNamespace the otc namespace
	 * @param sourceClz    the source clz
	 * @param targetClz    the target clz
	 * @return the string
	 */
	public static String createRegistryId(String otcNamespace, String sourceClz, String targetClz) {
		String registryId = null;
		if (sourceClz == null) {
			registryId = targetClz;
		} else {
			registryId = sourceClz + "_" + targetClz;
		}
		if (!CommonUtils.isEmpty(otcNamespace)) {
			registryId = otcNamespace + "." + registryId;
		}
		return registryId;
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
	 * @param mapOCDs     the map OC ds
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
			otcCommandDto = retrieveNextOCD(otcCommandDto.children, otcTokens[otcCommandDto.otcTokenIndex + 1]);
		}
		return otcCommandDto;
	}

	/**
	 * Retrieve next OCD.
	 *
	 * @param mapOCDs the map OC ds
	 * @param ocdKey  the ocd key
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
	 * @param directory  the directory
	 * @param fileFilter the file filter
	 * @param urls       the urls
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
						url = new URL("jar:file:" + file.getAbsolutePath() + "!/");
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
