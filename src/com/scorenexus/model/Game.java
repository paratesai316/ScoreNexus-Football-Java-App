package com.scorenexus.model;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private long gameId;
    private Team teamA;
    private Team teamB;
    private int gameDurationMinutes;
    private List<GameEvent> timeline;

    public Game(Team teamA, Team teamB, int gameDurationMinutes) {
        this.teamA = teamA;
        this.teamB = teamB;
        this.gameDurationMinutes = gameDurationMinutes;
        this.timeline = new ArrayList<>();
    }
    
    public void addGameEvent(GameEvent event) {
        this.timeline.add(event);
    }

    public long getGameId() { return gameId; }
    public void setGameId(long gameId) { this.gameId = gameId; }
    public Team getTeamA() { return teamA; }
    public Team getTeamB() { return teamB; }
    public int getGameDurationMinutes() { return gameDurationMinutes; }
    public List<GameEvent> getTimeline() { return timeline; }
}