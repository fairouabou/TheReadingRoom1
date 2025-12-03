package com.thereadingroom.model;

public class StandardUser extends User {

    public StandardUser(int id, String username, String password, String email) {
        super(id, username, password, email, "standard");
    }

    public StandardUser(String username, String password, String email) {
        super(username, password, email, "standard");
    }
}
