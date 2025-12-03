package com.thereadingroom.model;

public class User {

    protected int id;
    protected String username;
    protected String password;
    protected String email;
    protected String role;

    public User(int id, String username, String password, String email, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    public User(String username, String password, String email, String role) {
        this(-1, username, password, email, role);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean canManageUsers() { return false; }
    public boolean canManageBooks() { return false; }
    public boolean canDeleteDiscussion() { return false; }

    @Override
    public String toString() {
        return username + " (" + role + ")";
    }
}
