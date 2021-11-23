package com.jdt.fedlearn.frontend.entity.match;

public class MatchStart {
    private String taskId;
    private String matchType;
    private String username;

    public MatchStart() {
    }

    public MatchStart(String taskId, String matchType, String username) {
        this.taskId = taskId;
        this.matchType = matchType;
        this.username = username;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getMatchType() {
        return matchType;
    }

    public String getUsername() {
        return username;
    }
}
