package com.jdt.fedlearn.frontend.entity.match;

public class MatchDeleteReq {
    private String matchId;
    private String username;

    public MatchDeleteReq(String matchId, String username) {
        this.matchId = matchId;
        this.username = username;
    }

    public MatchDeleteReq() {
    }

    public String getMatchId() {
        return matchId;
    }

    public String getUsername() {
        return username;
    }
}
