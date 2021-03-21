/**
* Copyright (c) otclfoundation.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*/
package org.otcl2.common.util;

import org.otcl2.common.OtclConstants;
import org.otcl2.common.exception.OtclException;

// TODO: Auto-generated Javadoc
/**
 * The Class OtclUtils.
 */
public class OtclUtils {

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

}
