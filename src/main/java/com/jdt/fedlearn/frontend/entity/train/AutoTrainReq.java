package com.jdt.fedlearn.frontend.entity.train;

public class AutoTrainReq {
    private String algorithm;
    private String username;
    private int taskId;
    private String matchId;

    public AutoTrainReq(String algorithm, String username, int taskId, String matchId) {
        this.algorithm = algorithm;
        this.username = username;
        this.taskId = taskId;
        this.matchId = matchId;
    }

    public AutoTrainReq() {
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public String getUsername() {
        return username;
    }

    public int getTaskId() {
        return taskId;
    }

    public String getMatchId() {
        return matchId;
    }
}
