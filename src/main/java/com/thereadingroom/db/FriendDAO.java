package com.thereadingroom.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FriendDAO {
    public static List<Integer> findFriendIdsForUser(int userId) {
        String sql = "SELECT friend_id FROM friends WHERE user_id = ?";
        List<Integer> result = new ArrayList<>();

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(rs.getInt("friend_id"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error loading friends: " + e.getMessage());
        }

        return result;
    }

    public static void addFriendPair(int userId, int friendId) {
        String sql = "INSERT OR IGNORE INTO friends (user_id, friend_id) VALUES (?, ?)";

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             PreparedStatement stmt2 = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, friendId);
            stmt.executeUpdate();

            stmt2.setInt(1, friendId);
            stmt2.setInt(2, userId);
            stmt2.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error adding friend pair: " + e.getMessage());
        }
    }

    public static void removeFriendPair(int userId, int friendId) {
        String sql = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             PreparedStatement stmt2 = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, friendId);
            stmt.executeUpdate();

            stmt2.setInt(1, friendId);
            stmt2.setInt(2, userId);
            stmt2.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error removing friend pair: " + e.getMessage());
        }
    }

}