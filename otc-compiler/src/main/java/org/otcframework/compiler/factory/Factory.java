package org.otcframework.compiler.factory;

import org.otcframework.compiler.command.TargetOtcCommandContext;

public class Factory {

    private Factory() {}

    /**
     *
     * @param targetOtcCommandContext
     * @return TargetOtcCommandContext
     */
    public static TargetOtcCommandContext create(TargetOtcCommandContext targetOtcCommandContext) {
        TargetOtcCommandContext newTargetOtcCommandContext = new TargetOtcCommandContext();
        newTargetOtcCommandContext.commandId = targetOtcCommandContext.commandId;
        newTargetOtcCommandContext.scriptDto = targetOtcCommandContext.scriptDto;
        newTargetOtcCommandContext.helper = targetOtcCommandContext.helper;
        newTargetOtcCommandContext.mainClassDto = targetOtcCommandContext.mainClassDto;
        newTargetOtcCommandContext.factoryClassDto = targetOtcCommandContext.factoryClassDto;
        newTargetOtcCommandContext.otcChain = targetOtcCommandContext.otcChain;
        newTargetOtcCommandContext.rawOtcTokens = targetOtcCommandContext.rawOtcTokens;
        newTargetOtcCommandContext.otcTokens = targetOtcCommandContext.otcTokens;
        newTargetOtcCommandContext.otcCommandDto = targetOtcCommandContext.otcCommandDto;
        newTargetOtcCommandContext.indexedCollectionsDto = targetOtcCommandContext.indexedCollectionsDto;
        newTargetOtcCommandContext.hasAnchorInChain = targetOtcCommandContext.hasAnchorInChain;
        newTargetOtcCommandContext.hasPreAnchor = targetOtcCommandContext.hasPreAnchor;
        newTargetOtcCommandContext.hasPostAnchor = targetOtcCommandContext.hasPostAnchor;
        newTargetOtcCommandContext.hasExecuteModule = targetOtcCommandContext.hasExecuteModule;
        newTargetOtcCommandContext.hasExecuteConverter = targetOtcCommandContext.hasExecuteConverter;
        newTargetOtcCommandContext.executeModuleOtcNamespace = targetOtcCommandContext.executeModuleOtcNamespace;
        newTargetOtcCommandContext.executeOtcConverter = targetOtcCommandContext.executeOtcConverter;
        newTargetOtcCommandContext.algorithmId = targetOtcCommandContext.algorithmId;
        newTargetOtcCommandContext.collectionsCount = targetOtcCommandContext.collectionsCount;
        newTargetOtcCommandContext.currentCollectionTokenIndex = targetOtcCommandContext.currentCollectionTokenIndex;
        return newTargetOtcCommandContext;
    }

}
