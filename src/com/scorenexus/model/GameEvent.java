package com.scorenexus.model;

public class GameEvent {
    private String timestamp;
    private String action;
    private String teamName;

    public GameEvent(String timestamp, String action, String teamName) {
        this.timestamp = timestamp;
        this.action = action;
        this.teamName = teamName;
    }

    public String getTimestamp() { return timestamp; }
    public String getAction() { return action; }
    public String getTeamName() { return teamName; }
}