package com.jdt.fedlearn.frontend.entity.inference;

public class RemoteProgressReq {
    private String username;
    private String inferenceId;

    public RemoteProgressReq(String username, String inferenceId) {
        this.username = username;
        this.inferenceId = inferenceId;
    }

    public RemoteProgressReq() {
    }

    public String getUsername() {
        return username;
    }

    public String getInferenceId() {
        return inferenceId;
    }
}
