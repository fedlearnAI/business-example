package com.jdt.fedlearn.frontend.entity.inference;

public class RemoteInferenceReq {
    private String username;
    private String modelId;
    private String filePath;
    private boolean secureMode;

    public RemoteInferenceReq(String username, String modelId, String filePath, boolean secureMode) {
        this.username = username;
        this.modelId = modelId;
        this.filePath = filePath;
        this.secureMode = secureMode;
    }

    public RemoteInferenceReq() {
    }

    public String getUsername() {
        return username;
    }

    public String getModelId() {
        return modelId;
    }

    public String getFilePath() {
        return filePath;
    }

    public boolean isSecureMode() {
        return secureMode;
    }
}
