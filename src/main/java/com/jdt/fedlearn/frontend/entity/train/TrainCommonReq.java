package com.jdt.fedlearn.frontend.entity.train;

/**
 * 训练通用请求，包含用户名和训练id
 */
public class TrainCommonReq {
    private String trainId;
    private String username;

    public TrainCommonReq() {
    }

    public TrainCommonReq(String trainId, String username) {
        this.trainId = trainId;
        this.username = username;
    }

    public String getTrainId() {
        return trainId;
    }

    public String getUsername() {
        return username;
    }
}
