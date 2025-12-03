package com.thereadingroom.model;

public class Admin extends User {

    public Admin(int id, String username, String password, String email) {
        super(id, username, password, email, "admin");
    }

    public Admin(String username, String password, String email) {
        super(username, password, email, "admin");
    }

    @Override
    public boolean canManageUsers() { return true; }
    @Override
    public boolean canManageBooks() { return true; }
    @Override
    public boolean canDeleteDiscussion() { return true; }
}
