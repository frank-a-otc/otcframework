package org.otcl2.core.engine.compiler;

import java.util.List;

import org.otcl2.common.OtclConstants;
import org.otcl2.common.OtclConstants.TARGET_SOURCE;
import org.otcl2.common.dto.OtclCommandDto;
import org.otcl2.common.dto.ScriptDto;
import org.otcl2.common.dto.otcl.OtclFileDto.Copy;
import org.otcl2.common.dto.otcl.OtclFileDto.Execute;
import org.otcl2.common.dto.otcl.OverrideDto;
import org.otcl2.common.dto.otcl.TargetDto;
import org.otcl2.common.util.CommonUtils;
import org.otcl2.core.engine.compiler.exception.SyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class GetterSetterProcessor.
 */
final class GetterSetterProcessor {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(GetterSetterProcessor.class);

	/**
	 * Process.
	 *
	 * @param script the script
	 * @param otclCommandDto the otcl command dto
	 */
	public static void process(ScriptDto script, OtclCommandDto otclCommandDto) {
		if (OtclConstants.ROOT.equals(otclCommandDto.fieldName)) {
			return;
		}
		if (otclCommandDto.getter == null) {
			String getter = null;
			if (Boolean.class.isAssignableFrom(otclCommandDto.fieldType)) { 
				getter = "is" + CommonUtils.initCap(otclCommandDto.fieldName);
			} else {
				getter = "get" + CommonUtils.initCap(otclCommandDto.fieldName);
			}
			otclCommandDto.getter = getter;
		}
		if (TARGET_SOURCE.TARGET == otclCommandDto.enumTargetSource) {
			if (otclCommandDto.setter == null) {
				String setter = "set" + CommonUtils.initCap(otclCommandDto.fieldName);
				otclCommandDto.setter = setter;
			}
			List<TargetDto.Override> overrides = null;
			if (script.command instanceof Copy) {
				Copy copy = (Copy) script.command;
				overrides = copy.to.overrides;
			} else {
				Execute execute = (Execute) script.command;
				overrides = execute.target.overrides;
			}
			if (overrides == null) {
				return;
			}
			for (TargetDto.Override override : overrides) {
				String tokenPath = override.tokenPath;
				if (tokenPath == null) {
					throw new SyntaxException("", "Oops... Syntax error in Script-block : " + script.command.id + 
							". OTCL-token didn't pass Syntax-Checker - 'overrides: tokenPath' is missing.");
				}
				if (!otclCommandDto.tokenPath.equals(tokenPath)) {
					continue;
				}
				if (override.getterHelper != null) {
					otclCommandDto.enableFactoryHelperGetter = true;
					otclCommandDto.isGetterInitialized = false;
					otclCommandDto.getter = override.getterHelper;
				} else if (override.getter != null) {
					otclCommandDto.getter = override.getter;
					otclCommandDto.isGetterInitialized = false;
				}
				if (override.setterHelper != null) {
					otclCommandDto.enableFactoryHelperSetter = true;
					otclCommandDto.isSetterInitialized = false;
					otclCommandDto.setter = override.setterHelper;
				} else if (override.setter != null) {
					otclCommandDto.setter = override.setter;
					otclCommandDto.isSetterInitialized = false;
				}
			}
		} else {
			List<OverrideDto> overrides = null;
			if (script.command instanceof Copy) {
				Copy copy = (Copy) script.command;
				overrides = copy.from.overrides;
			} else {
				Execute execute = (Execute) script.command;
				overrides = execute.source.overrides;
			}
			if (overrides == null) {
				return;
			}
			for (OverrideDto override : overrides) {
				String tokenPath = override.tokenPath;
				if (tokenPath == null) {
					throw new SyntaxException("", "Oops... Syntax error in Script-block : " + script.command.id + 
							". OTCL-token didn't pass Syntax-Checker - 'overrides: tokenPath' is missing.");
				}
				if (!otclCommandDto.tokenPath.equals(tokenPath)) {
					continue;
				}
				if (override.getterHelper != null) {
					otclCommandDto.enableFactoryHelperGetter = true;
					otclCommandDto.isGetterInitialized = false;
					otclCommandDto.getter = override.getterHelper;
				} else if (override.getter != null) {
					otclCommandDto.getter = override.getter;
					otclCommandDto.isGetterInitialized = false;
				}
			}
		}
		LOGGER.debug("Getter/Setter processing : Okay for " + script.command.id);
		return;
	}
}
