package org.otcl2.core.engine.compiler;

import java.util.List;

import org.otcl2.common.OtclConstants;
import org.otcl2.common.dto.OtclCommandDto;
import org.otcl2.common.dto.OtclFileDto.Copy;
import org.otcl2.common.dto.OtclFileDto.Target.Override;
import org.otcl2.common.dto.ScriptDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class ConcreteTypeNotationProcessor.
 */
final class ConcreteTypeNotationProcessor {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(OtclLexicalizer.class);

	/**
	 * Process.
	 *
	 * @param script the script
	 * @param otclCommandDto the otcl command dto
	 * @param rawOtclToken the raw otcl token
	 * @param otclChain the otcl chain
	 * @param isMapNotation the is map notation
	 * @param idxArrNotation the idx arr notation
	 * @return true, if successful
	 */
	public static boolean process(ScriptDto script, OtclCommandDto otclCommandDto, String rawOtclToken,
			String otclChain, boolean isMapNotation, int idxArrNotation) {
		String scriptId = null;
		List<Override> overrides = null;
		Copy copy = (Copy) script.command;
		if (copy != null && copy.to != null && copy.to.overrides != null) {
			overrides = copy.to.overrides;
		}
		if (overrides == null) {
			return true;
		}
		otclCommandDto.concreteTypeName = null;
		for (Override override : overrides) {
			String concreteType = override.concreteType;
			if (concreteType == null) {
				continue;
			}
			String tokenPath = override.tokenPath;
			if (tokenPath.contains(OtclConstants.MAP_KEY_REF)) {
				if (otclCommandDto.mapKeyConcreteType == null) {
					otclCommandDto.mapKeyConcreteType = concreteType;
				} else {
					LOGGER.warn("Oops... Syntax error in Script-block : " + scriptId + ". Ignoring unexpected "
							+ "'override.concreteType' value '" + concreteType + "' found for Map-key <K>");
				}
			} else if (rawOtclToken.contains(OtclConstants.MAP_KEY_REF)) {
				if (otclCommandDto.mapValueConcreteType == null) {
					otclCommandDto.mapValueConcreteType = concreteType;
				} else {
					LOGGER.warn("Oops... Syntax error in Script-block : " + scriptId + ". Ignoring unexpected "
							+ "'override.concreteType' value '" + concreteType + "' found for Map-value <V>");
				}
			} else if (otclCommandDto.tokenPath.equals(tokenPath)) {
				if (otclCommandDto.concreteTypeName == null) {
					otclCommandDto.concreteTypeName = concreteType;
				} else {
					LOGGER.warn("Oops... Syntax error in Script-block : " + scriptId + ". Ignoring unexpected "
							+ "'override.concreteType' value '" + concreteType + "' found.");
				}
			}
		}
		return true;
	}
}
