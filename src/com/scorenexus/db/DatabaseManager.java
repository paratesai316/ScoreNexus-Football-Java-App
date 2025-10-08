package com.scorenexus.db;

import com.scorenexus.model.Game;
import com.scorenexus.model.Player;
import com.scorenexus.model.Team;
import java.sql.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:scorenexus_gamedata.db";

    public Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public void createNewDatabase() {
        String sqlGames = "CREATE TABLE IF NOT EXISTS games ("
                        + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + " team_a_name TEXT NOT NULL,"
                        + " team_b_name TEXT NOT NULL,"
                        + " team_a_score INTEGER DEFAULT 0,"
                        + " team_b_score INTEGER DEFAULT 0,"
                        + " duration_minutes INTEGER,"
                        + " game_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                        + ");";

        String sqlTimeline = "CREATE TABLE IF NOT EXISTS timeline ("
                           + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                           + " game_id INTEGER NOT NULL,"
                           + " timestamp TEXT NOT NULL,"
                           + " action TEXT NOT NULL,"
                           + " team_name TEXT,"
                           + " FOREIGN KEY (game_id) REFERENCES games (id)"
                           + ");";

        String sqlPlayerStats = "CREATE TABLE IF NOT EXISTS player_stats ("
                              + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                              + " game_id INTEGER NOT NULL,"
                              + " team_name TEXT NOT NULL,"
                              + " player_number INTEGER NOT NULL,"
                              + " player_name TEXT NOT NULL,"
                              + " minutes_played INTEGER DEFAULT 0,"
                              + " goals INTEGER DEFAULT 0,"
                              + " assists INTEGER DEFAULT 0,"
                              + " FOREIGN KEY (game_id) REFERENCES games (id)"
                              + ");";

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlGames);
            stmt.execute(sqlTimeline);
            stmt.execute(sqlPlayerStats);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public long insertNewGame(String teamA, String teamB, int duration) {
        String sql = "INSERT INTO games(team_a_name, team_b_name, duration_minutes) VALUES(?, ?, ?)";
        long gameId = -1;
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, teamA);
            pstmt.setString(2, teamB);
            pstmt.setInt(3, duration);
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                gameId = rs.getLong(1);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return gameId;
    }
    
    public void logEvent(long gameId, String timestamp, String action, String teamName) {
        String sql = "INSERT INTO timeline(game_id, timestamp, action, team_name) VALUES(?,?,?,?)";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, gameId);
            pstmt.setString(2, timestamp);
            pstmt.setString(3, action);
            pstmt.setString(4, teamName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void finalizeGame(Game game) {
        // Update final score
        String sqlGame = "UPDATE games SET team_a_score = ?, team_b_score = ? WHERE id = ?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sqlGame)) {
            pstmt.setInt(1, game.getTeamA().getScore());
            pstmt.setInt(2, game.getTeamB().getScore());
            pstmt.setLong(3, game.getGameId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        // Save player stats
        savePlayerStatsForTeam(game, game.getTeamA());
        savePlayerStatsForTeam(game, game.getTeamB());
    }
    
    private void savePlayerStatsForTeam(Game game, Team team) {
        String sql = "INSERT INTO player_stats(game_id, team_name, player_number, player_name, goals, assists) VALUES(?,?,?,?,?,?)";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (Player player : team.getPlayers()) {
                 pstmt.setLong(1, game.getGameId());
                 pstmt.setString(2, team.getName());
                 pstmt.setInt(3, player.getNumber());
                 pstmt.setString(4, player.getName());
                 pstmt.setInt(5, player.getGoals());
                 pstmt.setInt(6, player.getAssists());
                 pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}