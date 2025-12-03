package com.thereadingroom.model;

public class UserBook {

    private int id;
    private int userId;
    private int bookId;
    private String listType; // reading/read/want
    private Integer rating;  // nullable

    public UserBook(int id, int userId, int bookId, String listType, Integer rating) {
        this.id = id;
        this.userId = userId;
        this.bookId = bookId;
        this.listType = listType;
        this.rating = rating;
    }

    public UserBook(int userId, int bookId, String listType) {
        this(-1, userId, bookId, listType, null);
    }

}
