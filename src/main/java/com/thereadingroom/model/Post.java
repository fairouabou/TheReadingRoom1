package com.thereadingroom.model;

public class Post implements Postable {

    private int id;
    private int discussionId;
    private int userId;
    private String content;
    private String createdAt;
    private String username;

    public Post(int id, int discussionId, int userId, String content, String createdAt) {
        this.id = id;
        this.discussionId = discussionId;
        this.userId = userId;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Post(int discussionId, int userId, String content, String createdAt) {
        this(-1, discussionId, userId, content, createdAt);
    }

    // ===== Interface implementation =====
    @Override
    public int getAuthorId() {
        return userId;
    }

    @Override
    public String getContent() {
        return content;
    }

    // ===== Getters and setters =====
    public int getId() { return id; }
    public int getDiscussionId() { return discussionId; }
    public int getUserId() { return userId; }
    public String getCreatedAt() { return createdAt; }

    public void setId(int id) { this.id = id; }
    public void setDiscussionId(int discussionId) { this.discussionId = discussionId; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setContent(String content) { this.content = content; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    @Override
    public String toString() {
        if (username == null || username.isBlank()) return content;
        return username + ": " + content;
    }
}
