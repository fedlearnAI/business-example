package com.jdt.fedlearn.frontend.entity.train;

import java.util.List;

public class TrainStartReq {
    private String algorithm;
    private String username;
    private int taskId;
    private String matchId;
    private List<ParameterField> parameters;

    public TrainStartReq(String algorithm, String username, int taskId, String matchId, List<ParameterField> parameters) {
        this.algorithm = algorithm;
        this.username = username;
        this.taskId = taskId;
        this.matchId = matchId;
        this.parameters = parameters;
    }

    public TrainStartReq() {
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

    public List<ParameterField> getParameters() {
        return parameters;
    }
}
