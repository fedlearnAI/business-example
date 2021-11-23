package com.jdt.fedlearn.frontend.entity.match;

import java.util.List;

public class MatchListReq {
    private String runningType;
    private List<String> taskList;
    private String username;

    public MatchListReq() {
    }

    public String getRunningType() {
        return runningType;
    }

    public List<String> getTaskList() {
        return taskList;
    }

    public String getUsername() {
        return username;
    }
}
