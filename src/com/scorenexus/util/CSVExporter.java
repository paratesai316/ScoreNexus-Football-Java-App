package com.scorenexus.util;

import com.scorenexus.model.Game;
import com.scorenexus.model.GameEvent;
import com.scorenexus.model.Player;
import com.scorenexus.model.Team;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class CSVExporter {

    public static void exportGameSummary(Game game) throws IOException {
        String fileName = "game_" + game.getGameId() + "_summary.csv";
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println("team_name,goals");
            writer.printf("%s,%d\n", game.getTeamA().getName(), game.getTeamA().getScore());
            writer.printf("%s,%d\n", game.getTeamB().getName(), game.getTeamB().getScore());
        }
    }
    
    public static void exportGameTimeline(Game game) throws IOException {
        String fileName = "game_" + game.getGameId() + "_timeline.csv";
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println("timestamp,action,team_name");
            for (GameEvent event : game.getTimeline()) {
                writer.printf("\"%s\",\"%s\",\"%s\"\n", event.getTimestamp(), event.getAction(), event.getTeamName());
            }
        }
    }

    public static void exportPlayerStats(Game game) throws IOException {
        String fileName = "game_" + game.getGameId() + "_player_stats.csv";
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println("team_name,player_number,player_name,minutes_played,goals_scored,assists_provided");
            writePlayerStatsForTeam(writer, game.getTeamA(), game.getGameDurationMinutes());
            writePlayerStatsForTeam(writer, game.getTeamB(), game.getGameDurationMinutes());
        }
    }

    private static void writePlayerStatsForTeam(PrintWriter writer, Team team, int duration) {
        for (Player p : team.getPlayers()) {
            // NOTE: A more complex system would track exact minutes played during substitutions.
            // For simplicity, we assume starters played the full game if not subbed.
            int minutesPlayed = p.isSubstitute() ? 0 : duration; 
            writer.printf("\"%s\",%d,\"%s\",%d,%d,%d\n",
                    team.getName(),
                    p.getNumber(),
                    p.getName(),
                    minutesPlayed,
                    p.getGoals(),
                    p.getAssists());
        }
    }
}