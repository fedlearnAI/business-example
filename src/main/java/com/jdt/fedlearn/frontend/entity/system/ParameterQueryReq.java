package com.jdt.fedlearn.frontend.entity.system;

/**
 *
 */
public class ParameterQueryReq {
    private String username;

    public ParameterQueryReq(String username) {
        this.username = username;
    }

    public ParameterQueryReq() {
    }

    public String getUsername() {
        return username;
    }
}
