package com.jdt.fedlearn.frontend.entity.privacyInfer;

public class ModelQueryReq {
    private String username;

    public ModelQueryReq(String username) {
        this.username = username;
    }

    public ModelQueryReq() {
    }

    public String getUsername() {
        return username;
    }
}
