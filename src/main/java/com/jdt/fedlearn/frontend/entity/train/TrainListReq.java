package com.jdt.fedlearn.frontend.entity.train;

import java.util.List;

public class TrainListReq {
    private String username;
    private List<String> taskList;
    private String type;

    public TrainListReq() {
    }

    public TrainListReq(String username, List<String> taskList, String type) {
        this.username = username;
        this.taskList = taskList;
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public List<String> getTaskList() {
        return taskList;
    }

    public String getType() {
        return type;
    }
}
