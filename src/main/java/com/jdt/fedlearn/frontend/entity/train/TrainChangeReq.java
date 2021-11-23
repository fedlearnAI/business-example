package com.jdt.fedlearn.frontend.entity.train;

public class TrainChangeReq {
    private String username;
    private String trainId;
    private String type;

    public TrainChangeReq(String username, String trainId, String type) {
        this.username = username;
        this.trainId = trainId;
        this.type = type;
    }

    public TrainChangeReq() {
    }

    public String getUsername() {
        return username;
    }

    public String getTrainId() {
        return trainId;
    }

    public String getType() {
        return type;
    }
}
