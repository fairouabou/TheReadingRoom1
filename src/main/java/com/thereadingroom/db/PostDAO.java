package com.thereadingroom.db;

import com.thereadingroom.model.Post;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostDAO {

    public static void insert(Post p) {
        String sql = "INSERT INTO posts (discussion_id, user_id, content, created_at) VALUES (?, ?, ?, ?)";

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, p.getDiscussionId());
            stmt.setInt(2, p.getUserId());
            stmt.setString(3, p.getContent());
            stmt.setString(4, p.getCreatedAt());

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error inserting post: " + e.getMessage());
        }
    }

    public static List<Post> findByDiscussion(int discussionId) {
        List<Post> posts = new ArrayList<>();

        String sql = """
        SELECT p.id, p.discussion_id, p.user_id, p.content, p.created_at,
               u.username
        FROM posts p
        JOIN users u ON p.user_id = u.id
        WHERE p.discussion_id = ?
        ORDER BY p.id ASC
        """;

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, discussionId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Post post = new Post(
                            rs.getInt("id"),
                            rs.getInt("discussion_id"),
                            rs.getInt("user_id"),
                            rs.getString("content"),
                            rs.getString("created_at")
                    );

                    // attach username into the Post object
                    post.setUsername(rs.getString("username"));

                    posts.add(post);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error loading posts: " + e.getMessage());
        }

        return posts;
    }

}
