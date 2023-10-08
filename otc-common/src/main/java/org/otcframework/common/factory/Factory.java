package org.otcframework.common.factory;

import org.otcframework.common.compiler.OtcCommandContext;
import org.otcframework.common.dto.ScriptDto;
import org.otcframework.common.dto.otc.OtcFileDto;

public class Factory {

    private Factory() {}

    /**
     * Clone.
     *
     * @return the script dto
     */
    public static ScriptDto create(ScriptDto scriptDto) {
        ScriptDto newScriptDto = new ScriptDto();
        if (scriptDto.command instanceof OtcFileDto.Execute) {
            OtcFileDto.Execute execute = (OtcFileDto.Execute) scriptDto.command;
            OtcFileDto.Execute executeClone = new OtcFileDto.Execute();
            newScriptDto.command = executeClone;
            if (execute.module != null) {
                executeClone.module = new OtcFileDto.Execute.OtclModule();
                executeClone.module.namespace = execute.module.namespace;
            }
            executeClone.converter = execute.converter;
            executeClone.executionOrder = execute.executionOrder;
        }
        newScriptDto.hasSetValues = scriptDto.hasSetValues;
        newScriptDto.hasExecuteModule = scriptDto.hasExecuteModule;
        newScriptDto.hasExecuteConverter = scriptDto.hasExecuteConverter;
        newScriptDto.targetOtcChainDto = scriptDto.targetOtcChainDto;
        newScriptDto.sourceOtcChainDto = scriptDto.sourceOtcChainDto;
        return newScriptDto;
    }

    /**
     * Clone.
     *
     * @return the otc command context
     */
    public static OtcCommandContext create(OtcCommandContext otcCommandContext) {
        OtcCommandContext newOtcCommandContext = new OtcCommandContext();
        newOtcCommandContext.otcChain = otcCommandContext.otcChain;
        newOtcCommandContext.rawOtcTokens = otcCommandContext.rawOtcTokens;
        newOtcCommandContext.otcTokens = otcCommandContext.otcTokens;
        newOtcCommandContext.otcCommandDto = otcCommandContext.otcCommandDto;
        newOtcCommandContext.indexedCollectionsDto = otcCommandContext.indexedCollectionsDto;
        return newOtcCommandContext;
    }

}
