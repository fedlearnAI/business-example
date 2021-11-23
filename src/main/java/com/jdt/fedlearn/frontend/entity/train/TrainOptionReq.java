package com.jdt.fedlearn.frontend.entity.train;

public class TrainOptionReq {
    private String algorithmType;
    private String username;
    private int taskId;

    public TrainOptionReq(String algorithmType, String username, int taskId) {
        this.algorithmType = algorithmType;
        this.username = username;
        this.taskId = taskId;
    }

    public TrainOptionReq() {
    }

    public String getAlgorithmType() {
        return algorithmType;
    }

    public String getUsername() {
        return username;
    }

    public int getTaskId() {
        return taskId;
    }
}
