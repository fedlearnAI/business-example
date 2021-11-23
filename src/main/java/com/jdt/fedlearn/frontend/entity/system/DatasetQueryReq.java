package com.jdt.fedlearn.frontend.entity.system;

public class DatasetQueryReq {
    private String username;
    private int taskId;
    private String taskPwd;
    private String clientUrl;

    public DatasetQueryReq() {
    }

    public DatasetQueryReq(String username, int taskId, String taskPwd, String clientUrl) {
        this.username = username;
        this.taskId = taskId;
        this.taskPwd = taskPwd;
        this.clientUrl = clientUrl;
    }

    public String getUsername() {
        return username;
    }

    public int getTaskId() {
        return taskId;
    }

    public String getTaskPwd() {
        return taskPwd;
    }

    public String getClientUrl() {
        return clientUrl;
    }
}
