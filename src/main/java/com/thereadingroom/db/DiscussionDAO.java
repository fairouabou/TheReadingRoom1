package com.thereadingroom.db;

import com.thereadingroom.model.Discussion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DiscussionDAO {

    public static List<Discussion> findAll() {
        List<Discussion> list = new ArrayList<>();
        String sql = "SELECT * FROM discussions ORDER BY id DESC";

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new Discussion(
                        rs.getInt("id"),
                        rs.getInt("book_id"),
                        rs.getString("title")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error loading discussions: " + e.getMessage());
        }

        return list;
    }

    public static void insert(Discussion d) {
        String sql = "INSERT INTO discussions (book_id, title) VALUES (?, ?)";

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, d.getBookId());
            stmt.setString(2, d.getTitle());
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error inserting discussion: " + e.getMessage());
        }
    }
}
