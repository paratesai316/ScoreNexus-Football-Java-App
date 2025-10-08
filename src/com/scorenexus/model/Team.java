package com.scorenexus.model;

import java.util.ArrayList;
import java.util.List;

public class Team {
    private String name;
    private List<Player> players;
    private int score = 0;

    public Team(String name) {
        this.name = name;
        this.players = new ArrayList<>();
    }

    public void addPlayer(Player player) {
        this.players.add(player);
    }

    public String getName() { return name; }
    public List<Player> getPlayers() { return players; }
    public int getScore() { return score; }
    public void incrementScore() { this.score++; }
}